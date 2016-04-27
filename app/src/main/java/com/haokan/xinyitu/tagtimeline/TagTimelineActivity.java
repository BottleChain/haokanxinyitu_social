package com.haokan.xinyitu.tagtimeline;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.main.BaseMainActivity;
import com.haokan.xinyitu.main.discovery.AlbumInfoBean;
import com.haokan.xinyitu.util.ConstantValues;

public class TagTimelineActivity extends BaseMainActivity{
    public static final String KEY_INTENT_TAGID = "tagid";
    public static final String KEY_INTENT_TAGNAME = "tagname";
    private String mTagId;
    private String mTagName;
    private TagTimelineFragment mTagTimeLineFragment;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mTagId = savedInstanceState.getString(KEY_INTENT_TAGID, "");
            mTagName = savedInstanceState.getString(KEY_INTENT_TAGNAME, "");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otherpersonalcenter_activity_layout);
        if (TextUtils.isEmpty(mTagId)) {
            mTagId = getIntent().getStringExtra(KEY_INTENT_TAGID);
            mTagName = getIntent().getStringExtra(KEY_INTENT_TAGNAME);
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        mTagTimeLineFragment = new TagTimelineFragment();
        mTagTimeLineFragment.setTagId(mTagId);
        mTagTimeLineFragment.setTagName(mTagName);
        transaction.replace(R.id.fl_content, mTagTimeLineFragment);
        transaction.commitAllowingStateLoss();

        registerMainBroadcast();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_INTENT_TAGID, mTagId);
    }

    @Override
    protected void deleteAblum(AlbumInfoBean bean) {
        if (mTagTimeLineFragment != null) {
            mTagTimeLineFragment.deleteAblum(bean);
        }
    }

    private void registerMainBroadcast() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("wangzixu", "tagtimeline registerMainBroadcast onReceive ---");
                String action = intent.getAction();
                if (ConstantValues.ACTION_MYFOLLOWS_CHANGE.equals(action)) {
                    String userId = intent.getStringExtra(ConstantValues.KEY__USERID);
                    boolean newStatus = intent.getBooleanExtra(ConstantValues.KEY_NEWSTATUS, false);
                    if (mTagTimeLineFragment != null) {
                        mTagTimeLineFragment.notifyFragmentsFollowChanged(userId, newStatus);
                    }
                } else if (ConstantValues.ACTION_LIKESTATUS_CHANGE.equals(action)) {
                    String ablumid = intent.getStringExtra(ConstantValues.KEY_ALBUMID);
                    int newStatus = intent.getIntExtra(ConstantValues.KEY_NEWSTATUS, 0);
                    if (mTagTimeLineFragment != null) {
                        mTagTimeLineFragment.notifyLikeStatusChanged(ablumid, newStatus);
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(ConstantValues.ACTION_MYFOLLOWS_CHANGE);
        filter.addAction(ConstantValues.ACTION_LIKESTATUS_CHANGE);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void notifyFragmentsFollowChanged(String userId, boolean newStatus) {
        Intent intent = new Intent(ConstantValues.ACTION_MYFOLLOWS_CHANGE);
        intent.putExtra(ConstantValues.KEY__USERID, userId);
        intent.putExtra(ConstantValues.KEY_NEWSTATUS, newStatus);
        sendBroadcast(intent); //通知关注的人改变了，主页要刷新其下面的关注按钮
    }

    @Override
    protected void notifyLikeStatusChanged(String ablumId, int newStatus) {
        Intent intent = new Intent(ConstantValues.ACTION_LIKESTATUS_CHANGE);
        intent.putExtra(ConstantValues.KEY_ALBUMID, ablumId);
        intent.putExtra(ConstantValues.KEY_NEWSTATUS, newStatus);
        sendBroadcast(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        App.sTagString = App.sTagString.replaceAll(mTagName, "-");
        super.onDestroy();
    }
}
