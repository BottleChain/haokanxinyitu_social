package com.haokan.xinyitu.base;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.umeng.analytics.MobclickAgent;

/**
 * activity基类，实现了状态栏半透明黑的效果
 */
public class BaseActivity extends Activity {
    protected boolean mIsDestory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }


    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //友盟统计基础
    }

    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //友盟统计基础
    }

    @Override
    protected void onDestroy() {
        System.gc();
        mIsDestory = true;
        super.onDestroy();
    }
}
