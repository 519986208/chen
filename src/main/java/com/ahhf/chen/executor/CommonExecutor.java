package com.ahhf.chen.executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// 通用处理线程池
public class CommonExecutor {

    private static volatile ThreadPoolExecutor executor;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (executor != null) {
                executor.shutdown();
            }
        }));
    }

    public static void submitTask(Runnable runnable) {
        if (executor == null) {
            synchronized (CommonExecutor.class) {
                if (executor == null) {
                    executor = MdcThreadPoolExecutor.newWithCurrentMdc(5, 20, 5, TimeUnit.MINUTES, new ArrayBlockingQueue<>(20000));
                }
            }
        }
        executor.execute(runnable);
    }

}