package com.haokan.xinyitu.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.client.protocol.ClientContext;
import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.protocol.HttpContext;

public class HttpClientManager {
    private static HttpClientManager singInstance;

    private AsyncHttpClient client;

    private HttpClientManager(Context context) {
        client = new AsyncHttpClient();
        PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        client.setCookieStore(myCookieStore);
    }

    public static HttpClientManager getInstance(Context context) {
        if (singInstance == null) {
            synchronized (HttpClientManager.class) {
                if (singInstance == null) {
                    singInstance = new HttpClientManager(context);
                }
            }
        }
        return singInstance;
    }

    public AsyncHttpClient getClient() {
        return client;
    }

    public void LogCookcie() {
        HttpContext httpContext = client.getHttpContext();
        CookieStore cookies = (CookieStore) httpContext.getAttribute(ClientContext.COOKIE_STORE);//获取AsyncHttpClient中的CookieStore
        if(cookies!=null){
            for(Cookie c:cookies.getCookies()){
                Log.d("LogCookcie ~~ " + c.getName(), c.getValue());
            }
        }else{
            Log.d("LogCookcie  ~~ ", "cookies is null");
        }
    }

    public void getData(String url, ResponseHandlerInterface responseHandlerInterface) {
        client.get(url, responseHandlerInterface);
    }

    public void loadFile(String url, FileAsyncHttpResponseHandler fileAsyncHttpResponseHandler) {
        client.get(url, fileAsyncHttpResponseHandler);
    }

    public void cancelAllRequest(boolean interrupt) {
        client.cancelAllRequests(interrupt);
    }

    /*
        The RequestParams class additionally supports multipart file uploads as follows:

        Add an InputStream to the RequestParams to upload:

        InputStream myInputStream = blah;
        RequestParams params = new RequestParams();
        params.put("secret_passwords", myInputStream, "passwords.txt");
        Add a File object to the RequestParams to upload:

        File myFile = new File("/path/to/file.png");
        RequestParams params = new RequestParams();
        try {
            params.put("profile_picture", myFile);
        } catch(FileNotFoundException e) {}
        Add a byte array to the RequestParams to upload:

        byte[] myByteArray = blah;
        RequestParams params = new RequestParams();
        params.put("soundtrack", new ByteArrayInputStream(myByteArray), "she-wolf.mp3");
     */

    public void upLoadFile(String url, File file, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        try {
            params.put("upfile", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // 上传文件
        client.post(url, params, handler);
    }


    /**检查网络连接状态
     * */
    public static boolean checkNetWorkStatus(Context context) {
        boolean result;
        if (context!=null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netinfo = cm.getActiveNetworkInfo();
            result = netinfo != null && netinfo.isConnected();
            return result;
        }else{
            return false;
        }
    }

    /**
     * 当前是不是wifi链接
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }
}
