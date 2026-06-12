package edu.cafuc.crossmall.service;

import edu.cafuc.crossmall.exception.BusinessException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
public class RedisLockService {

    private static final String PRODUCT_LOCK_PREFIX = "mall:lock:product:";

    /** 等锁最多 3 秒，持锁最多 10 秒（防死锁） */
    private static final long WAIT_SECONDS = 3;
    private static final long LEASE_SECONDS = 10;

    @Autowired
    private RedissonClient redissonClient;

    /** 按商品 id 加锁执行，有返回值 */
    public <T> T executeWithProductLock(Long productId, Supplier<T> action) {
        if (productId == null) {
            throw new BusinessException("商品不存在");
        }
        String lockKey = PRODUCT_LOCK_PREFIX + productId;
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(WAIT_SECONDS, LEASE_SECONDS, TimeUnit.SECONDS);
            if (!locked) {
                throw new BusinessException("系统繁忙，请重试");
            }
            return action.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("操作被中断，请重试");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /** 按商品 id 加锁执行，无返回值 */
    public void runWithProductLock(Long productId, Runnable action) {
        executeWithProductLock(productId, () -> {
            action.run();
            return null;
        });
    }
}