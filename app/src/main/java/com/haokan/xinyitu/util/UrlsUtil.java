package com.haokan.xinyitu.util;

import android.content.Context;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 用于生成访问网络的url地址的工具类
 */
public class UrlsUtil {
    public static class Urls {
        /** 厂商编号, KEY*/
        public static final String COMPANY_NO = "212";
        /** 秘钥（生成sign时用）*/
        public static final String SEC_KEY = "Eh3i336WXEQwaH3hXZ";

//        public static final String URL_HOST = "http://yitu.dev.levect.com"; //测试用域名
        public static final String URL_V = "1";

        public static final String URL_HOST_user = "http://user.demo.levect.com"; //用户部分域名，https://user.levect.com
        public static final String URL_HOST_yitu = "http://1px.demo.levect.com"; //用户部分域名，http://1px.levect.com
        //注册登录
        public static final String URL_user_c = "apiUserLogin";
        public static final String URL_sendsms_a = "SendSms"; //发送短信
        public static final String URL_register_a = "Reg"; //注册
        public static final String URL_loginsms_a = "LoginBySms"; //短信验证码登录

        //用户上传头像
        public static final String URL_user_upload_c = "apiUserAvatar";
        public static final String URL_sendsms_upload_a = "Upload";


        public static final String URL_img_upload_c = "apiimages";
        public static final String URL_img_seconduploadCheck_a = "SecondUploadCheck"; //秒传a
    }

    public static String getSendSmsUrl(Context context, String jsonData) {
        return getYiTuUrl(Urls.URL_HOST_user, Urls.URL_sendsms_a, Urls.URL_user_c, jsonData, Urls.URL_V, context);
    }

    public static String getRegisterUrl(Context context, String jsonData) {
        return getYiTuUrl(Urls.URL_HOST_user, Urls.URL_register_a, Urls.URL_user_c, jsonData, Urls.URL_V, context);
    }

    public static String getLoginSmsUrl(Context context, String jsonData) {
        return getYiTuUrl(Urls.URL_HOST_user, Urls.URL_loginsms_a, Urls.URL_user_c, jsonData, Urls.URL_V, context);
    }

    public static String getUploadAvatarUrl(String sessidonId) {
        return getYiTuUrlWithSessionId(Urls.URL_HOST_user, Urls.URL_sendsms_upload_a, Urls.URL_user_upload_c, null, Urls.URL_V, sessidonId);
    }

    public static String getSecondUploadCheckUrl(String sessidonId) {
        return getYiTuUrlWithSessionId(Urls.URL_HOST_yitu, Urls.URL_img_seconduploadCheck_a, Urls.URL_img_upload_c, null, Urls.URL_V, sessidonId);
    }


    /**
     * 获取sign
     */
    private static String getSign(String a, String c, String k, String t,
                                  String v, String jsonData) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("a", a);
        map.put("c", c);
        map.put("k", k);
        map.put("t", t);
        map.put("v", v);
        map.put("data", jsonData);

        List<String> list = new ArrayList<String>();
        list.addAll(map.keySet());
        Collections.sort(list);

        String sign = "";
        for (int i = 0; i < list.size(); i++) {
            // 拼接sign
            sign += list.get(i) + map.get(list.get(i));
        }

        String API_SEC = Urls.SEC_KEY;// 获取密钥
        sign = API_SEC + sign + API_SEC;// 拼接sign
        sign = SecurityUtil.md5String(sign).toLowerCase();// 进行md5加密，并转成小写
        return sign;
    }


    public static String getYiTuUrl(String host, String a, String c,
                                    String jsonData, String version, Context context) {
        if(jsonData == null) {
            jsonData = "{}";
        }
        String data = URLEncoder.encode(jsonData); // 获得URLEncoder后的data字符串

        String k = Urls.COMPANY_NO; // 厂商编号 212

        String t = String.valueOf(System.currentTimeMillis() / 1000); // 时间戳

        String sign = getSign(a, c, k, t, version, jsonData); // 获得sign字符串

//        String pid = CommonUtil.getChannelID(context);

        return host + "/?a=" + a + "&c=" + c + "&data=" + data
                + "&k=" + k + "&t=" + t + "&v=" + version + "&sign=" + sign
//                + "&pid=" + pid
                ;
    }

    public static String getYiTuUrlWithSessionId(String host, String a, String c,
                                    String jsonData, String version, String sessionid) {
        if(jsonData == null) {
            jsonData = "{}";
        }
        String data = URLEncoder.encode(jsonData); // 获得URLEncoder后的data字符串

        String k = Urls.COMPANY_NO; // 厂商编号 212

        String t = String.valueOf(System.currentTimeMillis() / 1000); // 时间戳

        String sign = getSign(a, c, k, t, version, jsonData); // 获得sign字符串

        return host + "/?a=" + a + "&c=" + c + "&data=" + data
                + "&k=" + k + "&t=" + t + "&v=" + version + "&sign=" + sign
                + "&HKSID=" + sessionid
                ;
    }
}
