package com.haokan.xinyitu.main.otherpersonalcenter;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseResponseBean;
import com.haokan.xinyitu.follow.ResponseBeanFollwsUsers;
import com.haokan.xinyitu.main.BaseMainActivity;
import com.haokan.xinyitu.main.discovery.AlbumInfoBean;
import com.haokan.xinyitu.util.ConstantValues;
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.JsonUtil;
import com.haokan.xinyitu.util.UrlsUtil;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class OtherPersonalCenterActivity extends BaseMainActivity{
    public static final String KEY_INTENT_USERID = "userId";
    private String mUserId;
    private OtherPersonalCenterFragment mOtherPersonalCenterFragment;
    private BroadcastReceiver mReceiver;

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

        registerMainBroadcast();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_INTENT_USERID, mUserId);
    }

    @Override
    protected void deleteAblum(AlbumInfoBean bean) {
        if (mOtherPersonalCenterFragment != null) {
            mOtherPersonalCenterFragment.deleteAblum(bean);
        }
    }

    private void registerMainBroadcast() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("wangzixu", "tagtimeline registerMainBroadcast onReceive ---");
                String action = intent.getAction();
                if (ConstantValues.ACTION_LIKESTATUS_CHANGE.equals(action)) {
                    String ablumid = intent.getStringExtra(ConstantValues.KEY_ALBUMID);
                    int newStatus = intent.getIntExtra(ConstantValues.KEY_NEWSTATUS, 0);
                    if (mOtherPersonalCenterFragment != null) {
                        mOtherPersonalCenterFragment.notifyLikeStatusChanged(ablumid, newStatus);
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(ConstantValues.ACTION_LIKESTATUS_CHANGE);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void notifyLikeStatusChanged(String ablumId, int newStatus) {
        Intent intent = new Intent(ConstantValues.ACTION_LIKESTATUS_CHANGE);
        intent.putExtra(ConstantValues.KEY_ALBUMID, ablumId);
        intent.putExtra(ConstantValues.KEY_NEWSTATUS, newStatus);
        sendBroadcast(intent);
    }

    @Override
    protected void changeFollowState(final View view) {
        if (TextUtils.isEmpty(mUserId)) {
            Log.d("wangzixu", "changeFollowState userId is empty!");
            return;
        }
        if (view.isSelected()) {
            View v = LayoutInflater.from(this).inflate(R.layout.cancel_follow_dialog_layout, null);
            final AlertDialog.Builder builder = new AlertDialog.Builder(this)
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
                            ResponseBeanFollwsUsers.FollowUserIdBean idEntity = null;
                            for (int i = 0; i < App.sMyFollowsUser.size(); i++) {
                                if(App.sMyFollowsUser.get(i).getUserid().equals(mUserId)) {
                                    idEntity = App.sMyFollowsUser.get(i);
                                }
                            }
                            App.sMyFollowsUser.remove(idEntity);
                            Intent intent = new Intent(ConstantValues.ACTION_MYFOLLOWS_CHANGE);
                            intent.putExtra(ConstantValues.KEY__USERID, mUserId);
                            intent.putExtra(ConstantValues.KEY_NEWSTATUS, false);
                            sendBroadcast(intent); //通知关注的人改变了，主页要刷新其下面的关注按钮

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
            ResponseBeanFollwsUsers.FollowUserIdBean idEntity = new ResponseBeanFollwsUsers.FollowUserIdBean();
            idEntity.setUserid(mUserId);
            App.sMyFollowsUser.add(idEntity);
            Intent intent = new Intent(ConstantValues.ACTION_MYFOLLOWS_CHANGE);
            intent.putExtra(ConstantValues.KEY__USERID, mUserId);
            intent.putExtra(ConstantValues.KEY_NEWSTATUS, true);
            sendBroadcast(intent); //通知关注的人改变了，主页要刷新其下面的关注按钮

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
