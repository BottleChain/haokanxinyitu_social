package com.haokan.xinyitu.main.otherpersonalcenter;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseResponseBean;
import com.haokan.xinyitu.main.BaseMainActivity;
import com.haokan.xinyitu.main.discovery.ResponseBeanAlbumInfo;
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.JsonUtil;
import com.haokan.xinyitu.util.UrlsUtil;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class OtherPersonalCenterActivity extends BaseMainActivity{
    public static final String KEY_INTENT_USERID = "userId";
    private String mUserId;
    private OtherPersonalCenterFragment mOtherPersonalCenterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mUserId = savedInstanceState.getString(KEY_INTENT_USERID, "");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otherpersonalcenter_activity_layout);
        if (TextUtils.isEmpty(mUserId)) {
            mUserId = getIntent().getStringExtra(KEY_INTENT_USERID);
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        mOtherPersonalCenterFragment = new OtherPersonalCenterFragment();
        mOtherPersonalCenterFragment.setUserId(mUserId);
        transaction.replace(R.id.fl_content, mOtherPersonalCenterFragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_INTENT_USERID, mUserId);
    }

    @Override
    protected void deleteAblum(ResponseBeanAlbumInfo.DataEntity bean) {
        if (mOtherPersonalCenterFragment != null) {
            mOtherPersonalCenterFragment.deleteAblum(bean);
        }
    }

    @Override
    protected void changeFollowState(final View view) {
        if (TextUtils.isEmpty(mUserId)) {
            Log.d("wangzixu", "changeFollowState userId is empty!");
            return;
        }
        if (view.isSelected()) {
            View v = LayoutInflater.from(this).inflate(R.layout.cancel_follow_dialog_layout, null);
            final AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
                    .setTitle("提示")
                    .setView(v)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            view.setSelected(false);
                            String url = UrlsUtil.getdelFollowUrl(App.sessionId, mUserId);
                            Log.d("wangzixu", "changeFollowState cancel url = " + url);
                            HttpClientManager.getInstance(OtherPersonalCenterActivity.this).getData(url, new BaseJsonHttpResponseHandler<BaseResponseBean>() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, BaseResponseBean response) {
                                    Log.d("wangzixu", "changeFollowState cancel onSuccess = " + response.getErr_msg());
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, BaseResponseBean errorResponse) {

                                }

                                @Override
                                protected BaseResponseBean parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                                    return JsonUtil.fromJson(rawJsonData, BaseResponseBean.class);
                                }
                            });
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            view.setSelected(true);
            String url = UrlsUtil.getaddFollowUrl(App.sessionId, mUserId);
            Log.d("wangzixu", "changeFollowState add url = " + url);
            HttpClientManager.getInstance(this).getData(url, new BaseJsonHttpResponseHandler<BaseResponseBean>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, BaseResponseBean response) {
                    Log.d("wangzixu", "changeFollowState add onSuccess = " + response.getErr_msg());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, BaseResponseBean errorResponse) {

                }

                @Override
                protected BaseResponseBean parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    return JsonUtil.fromJson(rawJsonData, BaseResponseBean.class);
                }
            });
        }
    }
}
