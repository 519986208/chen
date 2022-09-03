package com.ahhf.chen.test;

import redis.clients.jedis.Jedis;

public class VpaRedisTest {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("15.206.233.8", 6379);
        jedis.auth("OTcxZWY2ZDczYjIwYTYzMzk2NzYzMzY4NmM4ZTEyNGE=");
        String s = jedis.get("rich062:flag");
        System.out.println(s);
    }

}
