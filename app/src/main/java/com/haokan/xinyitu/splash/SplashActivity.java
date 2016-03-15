package com.haokan.xinyitu.splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.ImageView;

import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;
import com.haokan.xinyitu.login_register.Login_Register_Activity;
import com.haokan.xinyitu.util.CommonUtil;
import com.haokan.xinyitu.util.HttpClientManager;

public class SplashActivity extends BaseActivity {
    public static final String PREFERENCE_GUIDE_PAGE = "guide_page";
    private Handler mHandler = new Handler();
    private ImageView mImageView;
    private final int mAdRemainTime = 2000; //广告加载出来后，停留的时长
    private final int mSplashRemainTimeMIn = 1000; //一图logo停留时长
    private final int mSplashRemainTimeMax = 2000; //一图logo停留时长
    private Runnable mLaunchHomeRunnable;
    private long mStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_activity_layout);
        mStart = SystemClock.uptimeMillis();
        mImageView = (ImageView) findViewById(R.id.image);
        mLaunchHomeRunnable = new Runnable() {
            @Override
            public void run() {
                launcherHome();
            }
        };

        String versionName = CommonUtil.getLocalVersionName(this);
        SharedPreferences sp = getSharedPreferences(PREFERENCE_GUIDE_PAGE, Context.MODE_PRIVATE);
        boolean isFirstLauncher = sp.getBoolean(versionName, true);

        if (false) {
            //进入引导页
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Intent i = new Intent(SplashActivity.this, GuideActivity.class);
//                    startActivity(i);
//                    finish();
//                    overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
//                }
//            }, mSplashRemainTimeMIn);
        } else {
            initApp();
            mHandler.postDelayed(mLaunchHomeRunnable, mSplashRemainTimeMax);
        }
    }

    /**
     * 初始化一些全局的变量，如要不要显示广告等
     */
    private void initApp() {
        if (HttpClientManager.checkNetWorkStatus(this)) {
            //requestAd();
        }
    }

    private void launcherHome() {
        Intent i = new Intent(SplashActivity.this, Login_Register_Activity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
    }

    @Override
    public void onBackPressed() {
    }
}
