package com.ahhf.chen.redis;

public interface DistributeRedisService {

    /**
     * 尝试获取分布式锁
     * 
     * @param lockKey 锁
     * @param expireTime 超时时间，单位毫秒
     * @return 是否获取成功
     */
    public boolean tryGetDistributedLock(String lockKey, int expireTime);

    /**
     * 释放分布式锁
     * 
     * @param lockKey 锁
     * @return 是否释放成功
     */
    public boolean releaseDistributedLock(String lockKey);

}
