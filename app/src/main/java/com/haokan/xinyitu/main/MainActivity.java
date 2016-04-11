package com.haokan.xinyitu.main;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseFragment;
import com.haokan.xinyitu.main.MyFollowsTimeLine.MyFollowFragment;
import com.haokan.xinyitu.main.discovery.DiscoveryFragment;
import com.haokan.xinyitu.main.discovery.ResponseBeanAlbumInfo;
import com.haokan.xinyitu.main.mypersonalcenter.MyPersonalCenterFragment;
import com.haokan.xinyitu.upload.UpLoadMainActivity;

public class MainActivity extends BaseMainActivity {

    private ImageButton mIvBottomBar1;
    private ImageButton mIvBottomBar2;
    private ImageButton mIvBottomBar3;
    private ImageButton mIvBottomBar4;
    private ImageButton mIvBottomBar5;
    private FragmentManager mFm;
    private DiscoveryFragment mDiscoveryFragment;
    private MyPersonalCenterFragment mMyPersonalCenterFragment;
    private MyFollowFragment mMyFollowFragment;
    private View mCurrentSelectTab;
    private BaseFragment mCurrentFragment;
    BroadcastReceiver mReceiver; // 刚发布完组图后，要在应该显示的页面显示出来（发现或者个人中心页），用来接收发完组图的广播

    private void assignViews() {
        mIvBottomBar1 = (ImageButton) findViewById(R.id.iv_bottom_bar_1);
        mIvBottomBar2 = (ImageButton) findViewById(R.id.iv_bottom_bar_2);
        mIvBottomBar3 = (ImageButton) findViewById(R.id.iv_bottom_bar_3);
        mIvBottomBar4 = (ImageButton) findViewById(R.id.iv_bottom_bar_4);
        mIvBottomBar5 = (ImageButton) findViewById(R.id.iv_bottom_bar_5);

        mIvBottomBar1.setOnClickListener(this);
        mIvBottomBar2.setOnClickListener(this);
        mIvBottomBar3.setOnClickListener(this);
        mIvBottomBar4.setOnClickListener(this);
        mIvBottomBar5.setOnClickListener(this);

        mFm = getFragmentManager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_activity_layout);
        assignViews();
        setDiscoveryFragment();
        registerCompleteAblumBroadcast();
    }

    /**
     * 切换到发现页（第一页）
     */
    @Override
    protected void setDiscoveryFragment() {
        if (mCurrentSelectTab != mIvBottomBar1) {
            if (mCurrentSelectTab != null) {
                mCurrentSelectTab.setSelected(false);
            }
            mIvBottomBar1.setSelected(true);
            mCurrentSelectTab = mIvBottomBar1;
            FragmentTransaction transaction = mFm.beginTransaction();
            if (mCurrentFragment != null) {
                transaction.hide(mCurrentFragment);
            }
            if (mDiscoveryFragment == null) {
                mDiscoveryFragment = new DiscoveryFragment();
                transaction.replace(R.id.fl_content, mDiscoveryFragment);
                //mDiscoveryFragment.loadData(this);
            } else {
                transaction.show(mDiscoveryFragment);
                //mDiscoveryFragment.loadData(this);
            }
            mCurrentFragment = mDiscoveryFragment;
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * 切换到个人中心页
     */
    @Override
    protected void setMyPersonalCenerFragment() {
        if (mCurrentSelectTab != mIvBottomBar5) {
            if (mCurrentSelectTab != null) {
                mCurrentSelectTab.setSelected(false);
            }
            mIvBottomBar5.setSelected(true);
            mCurrentSelectTab = mIvBottomBar5;
            FragmentTransaction transaction = mFm.beginTransaction();
            if (mCurrentFragment != null) {
                transaction.hide(mCurrentFragment);
            }
            if (mMyPersonalCenterFragment == null) {
                mMyPersonalCenterFragment = new MyPersonalCenterFragment();
                transaction.add(R.id.fl_content, mMyPersonalCenterFragment);
            } else {
                transaction.show(mMyPersonalCenterFragment);
                //mMyPersonalCenterFragment.loadData(this);
            }
            mCurrentFragment = mMyPersonalCenterFragment;
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * 切换到我关注的人页
     */
    @Override
    protected void setMyFollowsFragment() {
        if (mCurrentSelectTab != mIvBottomBar2) {
            if (mCurrentSelectTab != null) {
                mCurrentSelectTab.setSelected(false);
            }
            mIvBottomBar2.setSelected(true);
            mCurrentSelectTab = mIvBottomBar2;
            FragmentTransaction transaction = mFm.beginTransaction();
            if (mCurrentFragment != null) {
                transaction.hide(mCurrentFragment);
            }
            if (mMyFollowFragment == null) {
                mMyFollowFragment = new MyFollowFragment();
                transaction.add(R.id.fl_content, mMyFollowFragment);
            } else {
                transaction.show(mMyFollowFragment);
                //mMyPersonalCenterFragment.loadData(this);
            }
            mCurrentFragment = mMyFollowFragment;
            transaction.commitAllowingStateLoss();
        }
    }

    @Override
    protected void deleteAblum(ResponseBeanAlbumInfo.DataEntity bean) {
        if (mDiscoveryFragment != null) {
            mDiscoveryFragment.deleteAblum(bean);
        }
        if (mMyPersonalCenterFragment != null) {
            mMyPersonalCenterFragment.deleteAblum(bean);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        App app = (App) getApplication();
        if (app != null) { //为了清空app中的集合对象，这些对象是在upload一系列界面中赋值的，用来传递和共享数据
            app.setBigImgData(null);
            app.setImgDirs(null);
            app.setCheckedImgs(null);
            app.setTagsTemp(null);
        }
    }

    private void registerCompleteAblumBroadcast() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("wangzixu", "BroadcastReceiver onReceive ---");
                App app = (App) getApplication();
                ResponseBeanAlbumInfo.DataEntity album = app.getLastestUploadAlbum();
                //app.setLastestUploadAlbum(null);
                if (mDiscoveryFragment != null) {
                    mDiscoveryFragment.addFirstAblum(album);
                }
                if (mMyPersonalCenterFragment != null) {
                    mMyPersonalCenterFragment.addFirstAblum(album);
                }
            }
        };
        IntentFilter filter = new IntentFilter(UpLoadMainActivity.ACTION_UPDATA_LAST_ABLUM);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
