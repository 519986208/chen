package com.ahhf.cly.test.function;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ahhf.chen.ChenApp;
import com.ahhf.chen.redis.DistributeRedisService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ChenApp.class)
public class TestRedis {

    @Resource
    private DistributeRedisService distributeRedisService;

    @Test
    public void distibuteTest() {
        String key = "abcde";
        int size = 20;
        CountDownLatch latch = new CountDownLatch(size);
        final Semaphore sp = new Semaphore(2);
        try {
            for (int i = 0; i < size; i++) {
                sp.acquire();
                new Thread(new Runnable() {
                    public void run() {
                        System.out.println("线程" + distributeRedisService.tryGetDistributedLock(key, 10 * 1000));//10秒
                        latch.countDown();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sp.release();
                    }
                }).start();
            }
            latch.await();
            distributeRedisService.releaseDistributedLock(key);
            System.out.println("释放完毕后：" + distributeRedisService.tryGetDistributedLock(key, 10));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
