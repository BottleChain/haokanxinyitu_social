package com.haokan.xinyitu.splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;
import com.haokan.xinyitu.follow.ResponseBeanFollwsUsers;
import com.haokan.xinyitu.main.MainActivity;
import com.haokan.xinyitu.main.mypersonalcenter.ResponseBeanMyUserInfo;
import com.haokan.xinyitu.util.CommonUtil;
import com.haokan.xinyitu.util.ConstantValues;
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.JsonUtil;
import com.haokan.xinyitu.util.UrlsUtil;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

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

        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        App.sessionId = defaultSharedPreferences.getString(ConstantValues.KEY_SP_SESSIONID, "");
        App.user_Id = defaultSharedPreferences.getString(ConstantValues.KEY_SP_USERID, "");

        float density = getResources().getDisplayMetrics().density;
        App.sDensity = density;
        if (density >= 3) {
            App.sBigImgSize = 1080;
            App.sPreviewImgSize = 720;
        } else {
            App.sBigImgSize = 720;
            App.sPreviewImgSize = 720;
        }

        if (!TextUtils.isEmpty(App.sessionId)) { //获取我关注的人列表
            String url = UrlsUtil.getIlikeUrl(App.sessionId);
            Log.d("wangzixu", "Splash getIlikeUrl url = " + url);
            HttpClientManager.getInstance(this).getData(url, new BaseJsonHttpResponseHandler<ResponseBeanFollwsUsers>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanFollwsUsers response) {
                    if (response.getErr_code() == 0) {
                        App.sMyFollowsUser = response.getData();
                    } else {
                        //没有关注的人
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanFollwsUsers errorResponse) {
                }

                @Override
                protected ResponseBeanFollwsUsers parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    return JsonUtil.fromJson(rawJsonData, ResponseBeanFollwsUsers.class);
                }
            });

            //获取个人信息存下来
            String myinfoUrl = UrlsUtil.getMyinfoUrl(App.sessionId);
            Log.d("wangzixu", "Splash getMyinfoUrl = " + myinfoUrl);

            HttpClientManager.getInstance(this).getData(myinfoUrl, new BaseJsonHttpResponseHandler<ResponseBeanMyUserInfo>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanMyUserInfo response) {
                    if (response.getErr_code() == 0) {
                        String avatarUrl;
                        if (App.sDensity >= 3) {
                            avatarUrl = response.getData().getAvatar_url().getS150();
                        } else {
                            avatarUrl = response.getData().getAvatar_url().getS100();
                        }
                        SharedPreferences.Editor edit = defaultSharedPreferences.edit();
                        edit.putString(ConstantValues.KEY_SP_AVATAR_URL, avatarUrl);
                        edit.putString(ConstantValues.KEY_SP_NICKNAME, response.getData().getNickname());
                        edit.putString(ConstantValues.KEY_SP_DESC, response.getData().getDescription());
                        edit.putString(ConstantValues.KEY_SP_PHONENUM, response.getData().getMobile());
                        edit.apply();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanMyUserInfo errorResponse) {
                }

                @Override
                protected ResponseBeanMyUserInfo parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    return JsonUtil.fromJson(rawJsonData, ResponseBeanMyUserInfo.class);
                }
            });
        }

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

    @Override
    protected boolean hasStatusBar() {
        return false;
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
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
    }

    @Override
    public void onBackPressed() {
    }
}
