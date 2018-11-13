package com.ahhf.chen.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.MapUtils;

import com.alibaba.fastjson.JSON;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttp {

    private static OkHttp       netRequest = new OkHttp();

    private static OkHttpClient okHttpClient;             // OKHttp网络请求

    private OkHttp() {
        // 初始化okhttp 创建一个OKHttpClient对象，一个app里最好实例化一个此对象
        okHttpClient = new OkHttpClient();
        okHttpClient.newBuilder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS);
    }

    //-------------对外提供的方法Start--------------------------------
    /**
     * 建立网络框架，获取网络数据，异步get请求（Form）
     */
    public static String doGetRequest(String url, Map<String, Object> params) {
        return netRequest.innerGetFormAsync(url, params);
    }

    /**
     * 建立网络框架，获取网络数据，异步post请求（Form）
     */
    public static String doPostFormRequest(String url, Map<String, Object> params) {
        return netRequest.innerPostFormAsync(url, params);
    }

    /**
     * 建立网络框架，获取网络数据，异步post请求（json）
     */
    public static String doPostJsonRequest(String url, Map<String, Object> params) {
        return netRequest.innerPostJsonAsync(url, params);
    }
    //-------------对外提供的方法End--------------------------------

    /**
     * 异步get请求（Form），内部实现方法
     */
    private String innerGetFormAsync(String url, Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        // 请求url（baseUrl+参数）
        String doUrl = urlJoint(url, params);
        // 新建一个请求
        Request request = new Request.Builder().url(doUrl).build();
        return excute(request);
    }

    /**
     * 异步post请求（Form）,内部实现方法
     */
    private String innerPostFormAsync(String url, Map<String, Object> params) {
        RequestBody requestBody;
        if (params == null) {
            params = new HashMap<>();
        }
        FormBody.Builder builder = new FormBody.Builder();
        /**
         * 在这对添加的参数进行遍历
         */
        for (Map.Entry<String, Object> map : params.entrySet()) {
            String key = map.getKey();
            String value;
            /**
             * 判断值是否是空的
             */
            if (map.getValue() == null) {
                value = "";
            } else {
                value = map.getValue().toString();
            }
            /**
             * 把key和value添加到formbody中
             */
            builder.add(key, value);
        }
        requestBody = builder.build();
        //结果返回
        Request request = new Request.Builder().url(url).post(requestBody).build();
        return excute(request);
    }

    /**
     * post请求传json
     */
    private String innerPostJsonAsync(String url, Map<String, Object> params) {
        String mapToJson = JSON.toJSONString(params);
        Request request = buildJsonPostRequest(url, mapToJson);
        return excute(request);
    }

    private String excute(Request request) {
        try {
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Json_POST请求参数
     */
    private Request buildJsonPostRequest(String url, String json) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        return new Request.Builder().url(url).post(requestBody).build();
    }

    /**
     * 拼接url和请求参数
     */
    private static String urlJoint(String url, Map<String, Object> params) {
        if (MapUtils.isEmpty(params)) {
            return url;
        }
        StringBuilder endUrl = new StringBuilder(url);
        boolean isFirst = true;
        Set<Map.Entry<String, Object>> entrySet = params.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            if (isFirst && !url.contains("?")) {
                isFirst = false;
                endUrl.append("?");
            } else {
                endUrl.append("&");
            }
            endUrl.append(entry.getKey());
            endUrl.append("=");
            endUrl.append(entry.getValue());
        }
        return endUrl.toString();
    }
}
