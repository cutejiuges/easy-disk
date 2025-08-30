package com.cutejiuge.notification.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cutejiuge.common.annotation.BusinessLog;
import com.cutejiuge.common.exception.BusinessException;
import com.cutejiuge.common.response.ResultCode;
import com.cutejiuge.common.util.RedisLockUtil;
import com.cutejiuge.iface.dto.notification.EmailSendDTO;
import com.cutejiuge.notification.entity.EmailRecordEntity;
import com.cutejiuge.notification.pojo.enums.EmailPriorityEnum;
import com.cutejiuge.notification.pojo.enums.EmailSendStatusEnum;
import com.cutejiuge.notification.pojo.dto.OperateTemplateDTO;
import com.cutejiuge.notification.pojo.param.QueryEmailRecordParams;
import com.cutejiuge.notification.pojo.param.SaveEmailRecordParams;
import com.cutejiuge.notification.repository.EmailRecordRepository;
import com.cutejiuge.notification.service.EmailService;
import com.cutejiuge.notification.service.EmailTemplateService;
import com.cutejiuge.notification.validation.NotificationValidation;
import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 邮件服务实现类
 *
 * @author cutejiuge
 * @since 2025/8/26 上午8:37
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    @Resource
    private EmailRecordRepository emailRecordRepository;

    @Resource
    private EmailTemplateService emailTemplateService;

    @Resource
    private NotificationValidation notificationValidation;

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private RedisLockUtil redisLockUtil;

    // 配置变量
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${notification.email.from-name:网盘系统}")
    private String fromName;

    @Value("${notification.email.max-retry-times:3}")
    private Integer maxRetryTimes;

    @Value("${notification.email.retry-interval-seconds:300}")
    private Integer retryIntervalSeconds;

    @Value("${notification.email.daily-send-limit:1000}")
    private Integer dailySendLimit;

    @Value("${notification.mq.topics.email-send:email-send-topic}")
    private String emailSendTopic;

    @Value("${notification.mq.topics.email-retry:email-retry-topic}")
    private String emailRetryTopic;

    /**
     * 发送邮件
     */
    @Override
    @BusinessLog(operation = "发送邮件")
    @Transactional(rollbackFor = Exception.class)
    public void sendEmail(EmailSendDTO emailSendDTO) {
        // 校验邮件发送参数
        checkEmailSendParam(emailSendDTO);
        // 检查发送限制
        if (!checkCanSendLimit(emailSendDTO.getToEmail())) {
            throw new BusinessException(ResultCode.SEND_FREQUENCY_TOO_HIGH, "今日邮件发送已达上限");
        }
        // 创建邮件记录
        EmailRecordEntity record = createEmailRecord(emailSendDTO);
        // 判断是同步发送还是异步发送
        if (Boolean.TRUE.equals(emailSendDTO.getAsync())) {
            sendEmailAsync(emailSendDTO);
            return;
        }
        doSendEmail(record);
    }

    /**
     * 异步发送邮件
     */
    @Override
    @BusinessLog(operation = "异步发送邮件")
    public void sendEmailAsync(EmailSendDTO emailSendDTO) {
        try {
            // 发送到消息队列
            rocketMQTemplate.convertAndSend(emailSendTopic, JSONUtil.toJsonStr(emailSendDTO));
            log.info("邮件发送任务已经提交值异步队列: toEmail = {}", emailSendDTO.getToEmail());
        } catch (Exception e) {
            log.error("提交邮件发送任务到异步队列失败: toEmail = {}", emailSendDTO.getToEmail(), e);
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "邮件发送任务提交失败", e);
        }
    }

    /**
     * 重试发送邮件
     */
    @Override
    @BusinessLog(operation = "重试发送邮件")
    public void retrySendEmail(EmailRecordEntity emailRecord) {
        // 校验参数
        checkEmailRecordParam(emailRecord);
        // 检查重试次数
        if (emailRecord.getRetryCount() >=  emailRecord.getMaxRetryCount()) {
            log.warn("邮件重试次数已达上限: id = {}, retryCount = {}", emailRecord.getId(), emailRecord.getRetryCount());
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "邮件重试次数已达上限");
        }
        // 增加重试次数，并再次发送
        emailRecord.setRetryCount(emailRecord.getRetryCount() + 1);
        doSendEmail(emailRecord);
    }

    /**
     * 处理邮件发送队列
     */
    @Override
    @Scheduled(fixedDelay = 30000) // 每30s执行一次
    public void processEmailQueue() {
        try {
            // 查询待发送的邮件- pending
            List<EmailRecordEntity> pendingEmails = getUnFinishedEmailRecords(50, EmailSendStatusEnum.PENDING.getCode());
            if (pendingEmails.isEmpty()) {
                return;
            }
            log.info("开始处理邮件发送队列，当前待发送数量: {}", pendingEmails.size());
            for (EmailRecordEntity emailRecordEntity : pendingEmails) {
                try {
                    doSendEmail(emailRecordEntity);
                } catch (Exception e) {
                    log.error("处理邮件发送失败: id={}", emailRecordEntity.getId(), e);
                }
            }
            log.info("邮件发送队列处理完成");
        } catch (Exception e) {
            log.error("处理邮件发送队列异常", e);
        }
    }

    /**
     * 处理邮件重试队列
     */
    @Override
    @Scheduled(fixedDelay = 60000) // 每1min执行一次
    public void processRetryQueue() {
        try {
            // 查询需要重试的邮件
            List<EmailRecordEntity> retryEmails = getUnFinishedEmailRecords(20, EmailSendStatusEnum.FAILED.getCode());
            if (retryEmails.isEmpty()) {
                return;
            }
            log.info("开始处理邮件重试队列，待重试邮件数量: {}", retryEmails.size());
            for (EmailRecordEntity emailRecord : retryEmails) {
                try {
                    retrySendEmail(emailRecord);
                } catch (Exception e) {
                    log.error("处理邮件重试失败: id={}", emailRecord.getId(), e);
                }
            }
            log.info("邮件重试队列处理完成");
        } catch (Exception e) {
            log.error("处理邮件重试队列异常", e);
        }
    }

    /**
     * 清理过期的邮件记录
     */
    @Override
    @Scheduled(cron = "0 0 2 * * ?") // 每天2点执行
    public void cleanExpiredRecords() {
        try {
            // 删除30天之前的邮件记录
            LocalDateTime expireTime = LocalDateTime.now().minusDays(30);
            int deletedCount = deleteExpiredEmailRecords(expireTime);
            log.info("清理过期邮件记录完成: deletedCount={}", deletedCount);
        } catch (Exception e) {
            log.error("清理过期邮件记录失败", e);
        }
    }

    // ===================== 私有方法 ======================
    // 校验邮件发送参数
    private void checkEmailSendParam(EmailSendDTO dto) {
        this.notificationValidation.validateSendEmailParam(dto);
    }

    // 校验邮件记录参数
    private void checkEmailRecordParam(EmailRecordEntity emailRecord) {
        this.notificationValidation.validateEmailRecordParam(emailRecord);
    }

    // 检查发送限制
    private boolean checkCanSendLimit(String email) {
        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = start.plusDays(1);
        long todayCount = emailRecordRepository.countEmailRecords(QueryEmailRecordParams
                .builder().toEmail(email).startTime(start).endTime(end).build());
        return todayCount < dailySendLimit;
    }

    // 创建邮件记录
    private EmailRecordEntity createEmailRecord(EmailSendDTO emailSendDTO) {
        EmailRecordEntity record = new EmailRecordEntity();
        record.setToEmail(emailSendDTO.getToEmail());
        record.setSubject(emailSendDTO.getSubject());
        record.setContent(emailSendDTO.getContent());
        record.setEmailType(emailSendDTO.getEmailType());
        record.setTemplateCode(emailSendDTO.getTemplateCode());
        record.setSendStatus(EmailSendStatusEnum.PENDING.getCode());
        record.setRetryCount(0);
        record.setMaxRetryCount(emailSendDTO.getMaxRetryCount() != null ? emailSendDTO.getMaxRetryCount() : maxRetryTimes);
        record.setPriority(emailSendDTO.getPriority() != null ? emailSendDTO.getPriority() : EmailPriorityEnum.LOW.getCode());
        int result = this.emailRecordRepository.saveEmailRecord(record);
        if (result <= 0) {
            throw new BusinessException(ResultCode.DATABASE_ERROR, "保存邮件发送记录异常");
        }
        return record;
    }

    // 执行邮件发送
    private void doSendEmail(EmailRecordEntity emailRecord) {
        String lockKey = "email_send:" + emailRecord.getId();

        // 加锁并执行任务
        redisLockUtil.executeWithLock(lockKey, 30, 60, ()-> {
            try {
                // 1. 准备邮件内容
                String subject = emailRecord.getSubject();
                String content = emailRecord.getContent();
                // 2. 如果使用模板，渲染模板内容
                if (StrUtil.isNotBlank(emailRecord.getTemplateCode())) {
                    OperateTemplateDTO dto = OperateTemplateDTO.builder()
                            .templateCode(emailRecord.getTemplateCode())
                            .variables(parseTemplateVariables(emailRecord))
                            .build();
                    subject = emailTemplateService.renderSubject(dto);
                    content = emailTemplateService.renderTemplate(dto);
                }
                // 发送邮件
                sendMimeMessage(emailRecord.getToEmail(), subject, content);
                // 更新邮件记录的发送参数，发送成功
                updateEmailRecordSendInfo(emailRecord, EmailSendStatusEnum.SUCCESS.getCode());
                log.info("邮件发送成功: id = {}, toEmail = {}", emailRecord.getId(), emailRecord.getToEmail());
            } catch (Exception e) {
                // 更新邮件记录的发送参数，发送失败
                updateEmailRecordSendInfo(emailRecord, EmailSendStatusEnum.FAILED.getCode());
                log.error("邮件发送失败: id = {}, toEmail = {}", emailRecord.getId(), emailRecord.getToEmail(), e);
                if (emailRecord.getRetryCount() < emailRecord.getMaxRetryCount()) {
                    submitToRetryQueue(emailRecord);
                }
            }
        });
    }

    // 解析模板变量
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseTemplateVariables(EmailRecordEntity emailRecord) {
        Map<String, Object> variables = new HashMap<>();
        // 从邮件内容中解析变量（这里简化处理，实际可以从其他地方获取）
        if (StrUtil.isNotBlank(emailRecord.getContent())) {
            try {
                // 如果内容是JSON格式的变量，尝试解析
                if (emailRecord.getContent().startsWith("{") && emailRecord.getContent().endsWith("}")) {
                    variables = JSONUtil.toBean(emailRecord.getContent(), Map.class);
                }
            } catch (Exception e) {
                log.debug("解析邮件模板变量失败，使用默认变量", e);
            }
        }
        return variables;
    }

    // 发送MIME邮件
    private void sendMimeMessage(String toEmail, String subject, String content) throws Exception {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(fromEmail, fromName);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(content, true); // true表示支持HTML

        javaMailSender.send(mimeMessage);
    }

    // 更新邮件记录的发送状态
    private boolean updateEmailRecordSendInfo(EmailRecordEntity emailRecord, Integer sendStatus) {
        QueryEmailRecordParams queryParams = QueryEmailRecordParams
                .builder().id(emailRecord.getId()).build();
        SaveEmailRecordParams saveParams = SaveEmailRecordParams
                .builder().sendStatus(sendStatus)
                .sendTime(LocalDateTime.now()).retryCount(emailRecord.getRetryCount())
                .build();
        return this.emailRecordRepository.updateEmailRecord(queryParams, saveParams) > 0;
    }

    // 提交记录到重试队列中
    private void submitToRetryQueue(EmailRecordEntity emailRecord) {
        try {
            // 计算下次重试时间
            LocalDateTime nextRetryTime = LocalDateTime.now().plusSeconds(
                    (long) retryIntervalSeconds * (emailRecord.getRetryCount() + 1));
            // 提交到重试队列（这里可以使用延时消息）
            Map<String, Object> retryData = new HashMap<>();
            retryData.put("emailRecordId", emailRecord.getId());
            retryData.put("nextRetryTime", nextRetryTime);
            rocketMQTemplate.convertAndSend(emailRetryTopic, JSONUtil.toJsonStr(retryData));
            log.info("邮件重试任务已提交: id={}, nextRetryTime={}", emailRecord.getId(), nextRetryTime);
        } catch (Exception e) {
            log.error("提交邮件重试任务失败: id={}", emailRecord.getId(), e);
        }
    }

    // 查询指定状态的邮件记录
    private List<EmailRecordEntity> getUnFinishedEmailRecords(int limit, int status) {
        QueryEmailRecordParams queryParam = QueryEmailRecordParams
                .builder().sendStatus(status)
                .size(limit).page(0).build();
        if (status == EmailSendStatusEnum.FAILED.getCode()) {
            queryParam.setNeedRetry(true);
        }
        return this.emailRecordRepository.listEmailRecords(queryParam);
    }

    // 删除过期的邮件记录
    private int deleteExpiredEmailRecords(LocalDateTime expireTime) {
        QueryEmailRecordParams queryParam = QueryEmailRecordParams
                .builder().maxCreatedAt(expireTime).build();
        return this.emailRecordRepository.deleteEmailRecords(queryParam);
    }
}
