package com.haokan.xinyitu;

import android.app.Application;

import com.haokan.xinyitu.util.ImageLoaderManager;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //MobclickAgent.setDebugMode(true);
        ImageLoaderManager.initImageLoader(getApplicationContext()); //初始化imageLoader
    }
}
