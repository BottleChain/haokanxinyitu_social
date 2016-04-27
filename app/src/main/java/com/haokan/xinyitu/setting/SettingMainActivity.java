package com.haokan.xinyitu.setting;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;
import com.haokan.xinyitu.base.BaseResponseBean;
import com.haokan.xinyitu.util.CacheManager;
import com.haokan.xinyitu.util.ConstantValues;
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.ImageLoaderManager;
import com.haokan.xinyitu.util.JsonUtil;
import com.haokan.xinyitu.util.ToastManager;
import com.haokan.xinyitu.util.UrlsUtil;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * 作者：wangzixu on 2016/4/12 17:50
 * QQ：378320002
 */
public class SettingMainActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout mRlHeader;
    private ImageButton mIbBack;
    private TextView mTvTitle;
    private RelativeLayout mRlSettingAvatar;
    private ImageView mIvAvatar;
    private TextView mTvSettingName;
    private TextView mTvEdit;
    private RelativeLayout mRlSettingAbout;
    private RelativeLayout mRlSettingSuggestion;
    private RelativeLayout mRlSettingClearcache;
    private TextView mTvClearCache;
    private RelativeLayout mRlSettingExit;
    private Dialog mProgress;
    private Handler mHandler = new Handler();
    private String mOldAvatarUrl;
    private String mOldNickname;

    private void assignViews() {
        mRlHeader = (RelativeLayout) findViewById(R.id.rl_header);
        mIbBack = (ImageButton) findViewById(R.id.ib_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mRlSettingAvatar = (RelativeLayout) findViewById(R.id.rl_setting_avatar);
        mIvAvatar = (ImageView) findViewById(R.id.iv_avatar);
        mTvSettingName = (TextView) findViewById(R.id.tv_setting_name);
        mTvEdit = (TextView) findViewById(R.id.tv_edit);
        mRlSettingAbout = (RelativeLayout) findViewById(R.id.rl_setting_about);
        mRlSettingSuggestion = (RelativeLayout) findViewById(R.id.rl_setting_suggestion);
        mRlSettingClearcache = (RelativeLayout) findViewById(R.id.rl_setting_clearcache);
        mTvClearCache = (TextView) findViewById(R.id.tv_clear_cache);
        mRlSettingExit = (RelativeLayout) findViewById(R.id.rl_setting_exit);

        mIbBack.setOnClickListener(this);
        mRlSettingAvatar.setOnClickListener(this);
        mRlSettingAbout.setOnClickListener(this);
        mRlSettingSuggestion.setOnClickListener(this);
        mRlSettingClearcache.setOnClickListener(this);
        mRlSettingExit.setOnClickListener(this);

        updataCacheSize();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingmain_activity_layout);
        assignViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String avatarUrl = defaultSharedPreferences.getString(ConstantValues.KEY_SP_AVATAR_URL, "");
        String nickName = defaultSharedPreferences.getString(ConstantValues.KEY_SP_NICKNAME, "");
        if (!nickName.equals(mOldNickname)) {
            mTvSettingName.setText(mOldNickname);
            mOldNickname = nickName;
        }
        if (!avatarUrl.equals(mOldAvatarUrl)) {
            mOldAvatarUrl = avatarUrl;
            if (!TextUtils.isEmpty(avatarUrl)) {
                ImageLoaderManager.getInstance().asyncLoadCircleImage(mIvAvatar, mOldAvatarUrl
                        , mIvAvatar.getWidth(), mIvAvatar.getHeight());
            }
        }
    }

    public void updataCacheSize() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (SettingMainActivity.class) {
                    Log.d("wangzixu", "updataCacheSize start...");
                    final String cacheSize = CacheManager.getCacheSize(SettingMainActivity.this);
                    Log.d("wangzixu", "updataCacheSize end... ");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("wangzixu", "updataCacheSize end... mIsDestory = " + mIsDestory);
                            if (!mIsDestory) {
                                mTvClearCache.setText(cacheSize);
                            }
                        }
                    });
                }
            }
        }, "getCacheSize").start();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                onBackPressed();
                break;
            case R.id.rl_setting_avatar:
                Intent i1 = new Intent(SettingMainActivity.this, ChangeDataMainActivity.class);
                startActivity(i1);
                overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            case R.id.rl_setting_about:
                Intent iabout = new Intent(SettingMainActivity.this, AboutActivity.class);
                startActivity(iabout);
                overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            case R.id.rl_setting_clearcache: //清除缓存
                if (mProgress == null) {
                    mProgress = new Dialog(SettingMainActivity.this, R.style.loading_progress);
                    mProgress.setContentView(R.layout.loading_layout_progressdialog_notitle);
                    mProgress.setCancelable(true);
                }
                mProgress.show();
                final long start = System.currentTimeMillis();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (SettingMainActivity.class) {
                            CacheManager.clearCache(SettingMainActivity.this);
                            Log.d("wangzixu", "clearCache end");
                            long end = System.currentTimeMillis();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mTvClearCache.setText("0.0Byte");
                                    mProgress.dismiss();
                                }
                            }, Math.max(0, 500 - (end - start)));
                        }
                    }
                }, "clearCache").start();
                break;
            case R.id.rl_setting_suggestion:
                Intent isugges = new Intent(SettingMainActivity.this, SuggestionActivity.class);
                startActivity(isugges);
                overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            case R.id.rl_setting_exit:
                String url = UrlsUtil.getLogoutUrl(SettingMainActivity.this);
                HttpClientManager.getInstance(SettingMainActivity.this).getData(url, new BaseJsonHttpResponseHandler<BaseResponseBean>() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, BaseResponseBean response) {
                        if (response.getErr_code() == 0) {
                            //nothing
                        } else {
                            if (!mIsDestory) {
                                ToastManager.showShort(SettingMainActivity.this, "登出失败: " + response.getErr_msg());
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, BaseResponseBean errorResponse) {
                        if (!mIsDestory) {
                            ToastManager.showShort(SettingMainActivity.this, "登出失败, onFailure");
                        }
                    }

                    @Override
                    protected BaseResponseBean parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                        return JsonUtil.fromJson(rawJsonData, BaseResponseBean.class);
                    }
                });
                App.sessionId = null;
                App.user_Id = null;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SettingMainActivity.this);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString(ConstantValues.KEY_SP_USERID, "");
                edit.putString(ConstantValues.KEY_SP_SESSIONID, "");
                setResult(RESULT_OK);
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
