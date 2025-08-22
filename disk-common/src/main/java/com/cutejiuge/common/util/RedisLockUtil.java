package com.cutejiuge.common.util;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * redis分布式锁工具类
 *
 * @author cutejiuge
 * @since 2025/8/22 下午11:28
 */
@Slf4j
@Component
public class RedisLockUtil {
    @Resource
    private RedissonClient redissonClient;

    // 锁前缀
    private static final String LOCK_KEY_PREFIX = "east-disk:lock:";
    // 获取锁默认等待时间 单位s
    private static final long DEFAULT_WAIT_TIME = 10L;
    // 默认锁持有时间 单位s
    private static final long DEFAULT_LEASE_TIME = 30L;

    /**
     * 执行加锁的操作，无返回值
     * @param lockKey 锁的key
     * @param waitTime 等待时间 单位s
     * @param leaseTime 持有时间 单位s
     * @param task 待执行的操作
     */
    public void executeWithLock(String lockKey, long waitTime, long leaseTime, Runnable task) {
        String fullLockKey = LOCK_KEY_PREFIX + lockKey;
        RLock lock = redissonClient.getLock(fullLockKey);
        try {
            boolean acquired = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            if (acquired) {
                log.debug("获取锁成功: {}", fullLockKey);
                task.run();
            } else {
                log.warn("获取锁失败: {}", fullLockKey);
                throw new RuntimeException("获取锁失败，请稍候再试");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取锁被中断: {}", fullLockKey, e);
            throw new RuntimeException("获取锁被中断", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("释放锁: {}", fullLockKey);
            }
        }
    }

    /**
     * 执行加锁的操作，无返回值
     * @param lockKey 锁的key
     * @param task 待执行的操作
     */
    public void executeWithLock(String lockKey, Runnable task) {
        executeWithLock(lockKey, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, task);
    }

    /**
     * 执行枷锁的操作，带返回值
     * @param lockKey 锁的key
     * @param waitTime 加锁等待时间 单位s
     * @param leaseTime 锁的持有时间 单位s
     * @param supplier 待执行的操作
     * @return 执行操作的返回值
     * @param <T> 返回值类型
     */
    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, Supplier<T> supplier) {
        String fullLockKey = LOCK_KEY_PREFIX + lockKey;
        RLock lock = redissonClient.getLock(fullLockKey);
        try {
            boolean acquired = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            if (acquired) {
                log.debug("成功获取锁: {}", fullLockKey);
                return supplier.get();
            } else {
                log.warn("获取锁失败: {}", fullLockKey);
                throw new RuntimeException("获取锁失败，请稍后重试");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取锁被中断: {}", fullLockKey, e);
            throw new RuntimeException("获取锁被中断", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("释放锁: {}", fullLockKey);
            }
        }
    }

    /**
     * 执行带锁的操作（有返回值）
     *
     * @param lockKey 锁键
     * @param supplier 要执行的任务
     * @param <T> 返回值类型
     * @return 任务执行结果
     */
    public <T> T executeWithLock(String lockKey, Supplier<T> supplier) {
        return executeWithLock(lockKey, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, supplier);
    }

    /**
     * 尝试获取锁（不等待）
     *
     * @param lockKey 锁键
     * @param leaseTime 持有时间（秒）
     * @return 锁对象，如果获取失败返回null
     */
    public RLock tryLock(String lockKey, long leaseTime) {
        String fullLockKey = LOCK_KEY_PREFIX + lockKey;
        RLock lock = redissonClient.getLock(fullLockKey);

        try {
            boolean acquired = lock.tryLock(0, leaseTime, TimeUnit.SECONDS);
            if (acquired) {
                log.debug("成功获取锁: {}", fullLockKey);
                return lock;
            } else {
                log.debug("获取锁失败: {}", fullLockKey);
                return null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取锁被中断: {}", fullLockKey, e);
            return null;
        }
    }

    /**
     * 释放锁
     *
     * @param lock 锁对象
     */
    public void unlock(RLock lock) {
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
            log.debug("释放锁成功");
        }
    }

    /**
     * 文件上传锁 - 防止重复上传
     *
     * @param fileSha256 文件Sha256
     * @param task 要执行的任务
     */
    public void executeWithUploadLock(String fileSha256, Runnable task) {
        String lockKey = "upload:" + fileSha256;
        executeWithLock(lockKey, 10, 300, task);
    }

    /**
     * 文件分片合并锁
     *
     * @param uploadId 上传任务ID
     * @param task 要执行的任务
     */
    public void executeWithChunkMergeLock(String uploadId, Runnable task) {
        String lockKey = "chunk_merge:" + uploadId;
        executeWithLock(lockKey, 10, 60, task);
    }

    /**
     * 文件删除锁
     *
     * @param fileId 文件ID
     * @param task 要执行的任务
     */
    public void executeWithFileDeleteLock(Long fileId, Runnable task) {
        String lockKey = "file_delete:" + fileId;
        executeWithLock(lockKey, 5, 30, task);
    }

    /**
     * 文件移动锁
     *
     * @param fileId 文件ID
     * @param task 要执行的任务
     */
    public void executeWithFileMoveLock(Long fileId, Runnable task) {
        String lockKey = "file_move:" + fileId;
        executeWithLock(lockKey, 5, 30, task);
    }

    /**
     * 验证码发送锁
     *
     * @param email 邮箱地址
     * @param task 要执行的任务
     */
    public void executeWithVerificationCodeLock(String email, Runnable task) {
        String lockKey = "verification_code:" + email;
        executeWithLock(lockKey, 5, 60, task);
    }

    /**
     * 分享链接访问锁
     *
     * @param shareCode 分享码
     * @param task 要执行的任务
     */
    public void executeWithShareAccessLock(String shareCode, Runnable task) {
        String lockKey = "share_access:" + shareCode;
        executeWithLock(lockKey, 3, 10, task);
    }

    /**
     * 用户登录锁（防止暴力破解）
     *
     * @param username 用户名
     * @param task 要执行的任务
     */
    public void executeWithLoginLock(String username, Runnable task) {
        String lockKey = "login:" + username;
        executeWithLock(lockKey, 3, 10, task);
    }
}
