package com.ahhf.chen.redis;

import java.util.Collections;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
@Slf4j
public class DistributeRedisServiceImpl implements DistributeRedisService {

    private static final Long RELEASE_SUCCESS = 1L;
    private static final String LOCK_SUCCESS = "OK";
    // nxxx： 只能取NX或者XX，如果取NX，则只有当key不存在是才进行set，如果取XX，则只有当key已经存在时才进行set
    private static final String SET_IF_NOT_EXIST = "NX";
    // expx： 只能取EX或者PX，代表数据过期时间的单位，EX代表秒，PX代表毫秒。
    private static final String SET_WITH_EXPIRE_TIME = "PX";

    @Resource
    private JedisPool pool;

    @Override
    public boolean tryGetDistributedLock(String lockKey, String value, int expireTime) {
        try (Jedis jedis = pool.getResource()) {
            String result = jedis.set(lockKey, value, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
            return LOCK_SUCCESS.equals(result);
        } catch (Exception e) {
            log.error("获取redis分布式锁异常！", e);
        }
        return false;
    }

    @Override
    public boolean releaseDistributedLock(String lockKey, String value) {
        try (Jedis jedis = pool.getResource()) {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(value));
            return RELEASE_SUCCESS.equals(result);
        } catch (Exception e) {
            log.error("释放分布式锁异常！", e);
        }
        return false;
    }

}
