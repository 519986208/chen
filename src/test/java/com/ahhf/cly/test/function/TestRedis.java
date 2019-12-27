package com.ahhf.cly.test.function;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ahhf.chen.ChenApp;
import com.ahhf.chen.redis.DistributeRedisService;
import com.alibaba.fastjson.JSON;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ChenApp.class)
public class TestRedis {

    @Resource
    private DistributeRedisService distributeRedisService;

    @Resource
    private JedisPool pool;

    @Test
    public void distibuteTest() {
        String key = "abcde";
        try {
            String value = UUID.randomUUID().toString();
            System.out.println("线程" + distributeRedisService.tryGetDistributedLock(key, value, 10 * 1000));// 10秒
            distributeRedisService.releaseDistributedLock(key, value);
            System.out.println("释放完毕后：" + distributeRedisService.tryGetDistributedLock(key, value, 10));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testL() {
        try {
            String key = "List-chengly";
            Jedis jedis = pool.getResource();
            jedis.lpush(key, UUID.randomUUID().toString());
            jedis.lpush(key, UUID.randomUUID().toString());
            jedis.lpush(key, UUID.randomUUID().toString());
            // jedis.rpush(key, strings);
            jedis.expire(key, 3);
            Long llen = jedis.llen(key);
            System.out.println("length: " + llen);

            String rpop = jedis.rpop(key);
            System.out.println(rpop);

            // -1 全部取出来
            List<String> mget = jedis.lrange(key, 0, -1);
            System.out.println(JSON.toJSONString(mget));

            TimeUnit.SECONDS.sleep(4);
            List<String> mget2 = jedis.lrange(key, 0, -1);
            System.out.println(JSON.toJSONString(mget2));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testH() {
        try {
            String key = "Hash-chengly";
            Jedis jedis = pool.getResource();

            // 先清空
            jedis.flushAll();

            jedis.hset(key, "dianzan", "1");// 点赞
            jedis.hset(key, "cai", "1");// 踩
            jedis.hset(key, "guanzhu", "1");// 关注
            jedis.expire(key, 30);

            jedis.hincrBy(key, "dianzan", 3);
            jedis.hincrBy(key, "cai", 2);
            jedis.hincrBy(key, "guanzhu", 4);

            Map<String, String> hgetAll = jedis.hgetAll(key);
            hgetAll.forEach((k, v) -> {
                System.out.println(k + "=" + v);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testS() {
        try {
            String key = "set-chengly";
            Jedis jedis = pool.getResource();
            jedis.sadd(key, "1", "2", "3");

            System.out.println(jedis.sismember(key, "2"));
            Set<String> smembers = jedis.smembers(key);
            smembers.forEach((k) -> {
                System.out.println(k);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testZ() {
        try {
            String key = "zset-chengly";
            Jedis jedis = pool.getResource();
            jedis.zadd(key, 1, "412");
            jedis.zadd(key, 3, "462");
            jedis.zadd(key, 14, "417");
            jedis.zadd(key, 16, "541");
            jedis.zadd(key, 7, "821");
            jedis.zadd(key, 8, "363");
            jedis.zadd(key, 6, "245");
            jedis.zadd(key, 4, "523");
            jedis.expire(key, 20);
            Set<String> zrange = jedis.zrangeByScore(key, 5, 20);
            zrange.forEach(k -> {
                System.out.println(k);
            });

            Long zrank = jedis.zrank(key, "412");
            System.out.println("排名" + zrank);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPub() {
        try {
            Random r = new Random();
            String channel = "channelPubSub";
            Jedis jedis = pool.getResource();
            while (true) {
                int nextInt = r.nextInt(1000);
                TimeUnit.MILLISECONDS.sleep(nextInt);
                jedis.publish(channel, "message aaaa " + nextInt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSub() {
        try {
            String channel = "channelPubSub";
            Jedis jedis = pool.getResource();
            JedisPubSub jedisPubSub = new JedisPubSub() {
                public void onMessage(String channel, String message) {
                    System.out.println("收到的消息：" + message);
                }
            };
            jedis.subscribe(jedisPubSub, channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLike() {
        try {
            Jedis jedis = pool.getResource();
            jedis.setex("abc1", 1, "abc1value");
            jedis.setex("abc2", 20, "abc2value");
            jedis.setex("abc3", 20, "abc3value");
            jedis.setex("abc4", 20, "abc4value");
            TimeUnit.SECONDS.sleep(2);
            Set<String> keys = jedis.keys("abc*");
            System.out.println(keys);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void ttl() {
        try {
            String key = "chen13455432";
            Jedis jedis = pool.getResource();
            Pipeline pipelined = jedis.pipelined();
            pipelined.incr(key);
            pipelined.expire(key, 5);
            pipelined.syncAndReturnAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCluster() {
        try {
            Set<HostAndPort> nodes = new HashSet<>();
            for (int i = 0; i < 6; i++) {
                HostAndPort node = new HostAndPort("localhost", 6380 + i);
                nodes.add(node);
            }
            JedisCluster jedisCluster = new JedisCluster(nodes);// (node,
                                                                // connectionTimeout,
                                                                // soTimeout,
                                                                // maxAttempts,
                                                                // password,
                                                                // poolConfig);
            jedisCluster.set("hello", "world");
            System.out.println(jedisCluster.get("hello"));
            jedisCluster.setex("abc1", 1, "abc1value");
            jedisCluster.setex("abc2", 20, "abc2value");
            jedisCluster.setex("abc3", 14, "abc3value");
            jedisCluster.setex("abc4", 20, "abc4value");
            jedisCluster.setex("abc5", 20, "abc4value");
            jedisCluster.setex("abc6", 41, "abc4value");
            jedisCluster.setex("abc7", 20, "abc4value");
            TimeUnit.SECONDS.sleep(2);

            Collection<JedisPool> values = jedisCluster.getClusterNodes().values();
            System.out.println(values.size());

            for (JedisPool jedisPool : values) {
                ScanParams params = new ScanParams();
                params.count(51);
                params.match("abc*");
                Jedis jedis = jedisPool.getResource();
                ScanResult<String> result = jedis.scan("0", params);
                List<String> r = result.getResult();
                System.out.println("集群： " + r);
            }
            jedisCluster.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用场景记录<br>
     * String: 不解释<br>
     * Hash: 点赞，关注 <br>
     * List: lpush rpop 可作为队列来使用<br>
     * Set: 共同好友<br>
     * SortedSet(ZSet):排名第几位 使用Zrank<br>
     * --记录物流车辆当天从12点到24点上报的位置记录 使用 ZrangeByScore <br>
     * pub/sub：发布订阅<br>
     */

}
