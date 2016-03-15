package com.haokan.xinyitu.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * 用于解析json工具类
 */
public class JsonUtil {
    /**
     * 对象转换成json字符串,bean或者集合
     */
    public static String toJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    /**
     * json字符串转成对象，用于转化复杂的结构，如果map或者list集合
     */
    public static <T> T fromJson(String str, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(str, type);
    }

    /**
     * json字符串转成对象，转换简单的bean
     */
    public static <T> T fromJson(String str, Class<T> type) {
        Gson gson = new Gson();
        return gson.fromJson(str, type);
    }
}
