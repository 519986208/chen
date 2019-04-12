package com.ahhf.chen.test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.ahhf.chen.datasource.sequence.Snowflake;

public class TestSnowFlake {

    public static void main(String[] args) {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            long id = Snowflake.getNextId();
            try {
                TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(id);
        }
    }

}
