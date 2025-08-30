package com.cutejiuge.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码枚举
 *
 * @author cutejiuge
 * @since 2025/8/21 下午11:58
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    // ========== 系统级错误码 (10000-19999) ==========
    SUCCESS(200, "操作成功"),
    INTERNAL_SERVER_ERROR(10000, "系统内部错误"),
    PARAM_VALIDATION_ERROR(10001, "参数校验错误"),
    METHOD_NOT_SUPPORTED(10002, "请求方法不支持"),
    PATH_NOT_FOUND(10003, "请求路径不存在"),
    RATE_LIMIT_EXCEEDED(10004, "请求过于频繁"),
    SERVICE_UNAVAILABLE(10005, "服务暂不可用"),
    JSON_PARSE_ERROR(10006, "json解析异常"),
    DATABASE_ERROR(10007, "数据库操作异常"),
    RPC_ERROR(10008, "远程过程调用失败"),

    // ========== 用户服务错误码 (20000-29999)==================
    USER_NOT_FOUND(20001, "用户不存在"),
    PASSWORD_ERROR(20002, "密码错误"),
    USER_EXISTS(20003, "用户已注册"),
    VERIFICATION_CODE_ERROR(20004, "验证码错误"),
    VERIFICATION_CODE_EXPIRE(20005, "验证码已过期"),
    CAPTCHA_CODE_ERROR(20006, "图形验证码错误"),
    TOKEN_INVALID(20007, "用户token无效"),
    USER_DISABLED(20008, "用户已被禁用"),
    TOKEN_EXPIRED(20009, "Token已过期"),
    REFRESH_TOKEN_INVALID(20010, "刷新Token无效"),
    NOT_AUTHENTICATED(20011, "未登录"),
    ACCESS_DENIED(20012, "权限不足"),
    ACCOUNT_LOCKED(20013, "账号已锁定"),
    EMAIL_FORMAT_ERROR(20014, "邮箱格式错误"),
    PASSWORD_FORMAT_ERROR(20015, "密码格式错误"),
    VERIFICATION_CODE_USED(20016, "验证码已使用"),

    // ========== 文件服务错误码 (30000-39999) ==========
    FILE_NOT_FOUND(30001, "文件不存在"),
    FILE_NAME_DUPLICATE(30002, "文件名重复"),
    STORAGE_SPACE_INSUFFICIENT(30003, "存储空间不足"),
    FILE_SIZE_EXCEEDED(30004, "文件大小超限"),
    FILE_TYPE_NOT_SUPPORTED(30005, "文件类型不支持"),
    UPLOAD_TASK_NOT_FOUND(30006, "上传任务不存在"),
    CHUNK_VALIDATION_FAILED(30007, "分片校验失败"),
    FOLDER_NOT_EMPTY(30008, "文件夹不为空"),
    FILE_UPLOAD_FAILED(30009, "文件上传失败"),
    FILE_DOWNLOAD_FAILED(30010, "文件下载失败"),
    FILE_DELETE_FAILED(30011, "文件删除失败"),
    FILE_MOVE_FAILED(30012, "文件移动失败"),
    FILE_RENAME_FAILED(30013, "文件重命名失败"),
    FILE_PREVIEW_FAILED(30014, "文件预览失败"),
    CHUNK_MERGE_FAILED(30015, "分片合并失败"),

    // ========== 分享服务错误码 (40000-49999) ==========
    SHARE_NOT_FOUND(40001, "分享不存在"),
    SHARE_EXPIRED(40002, "分享已过期"),
    SHARE_PASSWORD_ERROR(40003, "提取密码错误"),
    DOWNLOAD_LIMIT_EXCEEDED(40004, "下载次数已达上限"),
    SHARE_CANCELLED(40005, "分享已被取消"),
    SHARE_CREATE_FAILED(40006, "分享创建失败"),
    FILE_SAVE_FAILED(40007, "文件转存失败"),
    CANNOT_SHARE_TO_SELF(40008, "不能分享自己的文件给自己"),

    // ========== 通知服务错误码 (50000-59999) ==========
    EMAIL_SEND_FAILED(50001, "邮件发送失败"),
    EMAIL_TEMPLATE_NOT_FOUND(50002, "邮件模板不存在"),
    SEND_FREQUENCY_TOO_HIGH(50003, "发送频率过高"),
    EMAIL_ADDRESS_INVALID(50004, "邮箱地址无效"),
    VERIFICATION_CODE_GENERATE_FAILED(50005, "验证码生成失败"),
    EMAIL_TEMPLATE_DISABLED(50006, "邮件模板已禁用"),
    ;

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态信息
     */
    private final String message;
}
