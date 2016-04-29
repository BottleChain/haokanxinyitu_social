package com.haokan.xinyitu.main;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.albumfailed.FailedAlbumActivity;
import com.haokan.xinyitu.base.BaseFragment;
import com.haokan.xinyitu.main.MyFollowsTimeLine.MyFollowTimeLineFragment;
import com.haokan.xinyitu.main.discovery.AlbumInfoBean;
import com.haokan.xinyitu.main.discovery.DiscoveryFragment;
import com.haokan.xinyitu.main.event.EventFragment;
import com.haokan.xinyitu.main.mypersonalcenter.MyPersonalCenterFragment;
import com.haokan.xinyitu.notice.ResponseBeanLastestNotice;
import com.haokan.xinyitu.upload.UpLoadMainActivity;
import com.haokan.xinyitu.util.ConstantValues;
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.JsonUtil;
import com.haokan.xinyitu.util.ToastManager;
import com.haokan.xinyitu.util.UrlsUtil;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends BaseMainActivity implements View.OnLongClickListener {

    private ImageButton mIvBottomBar1;
    private ImageButton mIvBottomBar2;
    private ImageButton mIvBottomBar3;
    private ImageButton mIvBottomBar4;
    private ImageButton mIvBottomBar5;
    private FragmentManager mFm;
    private DiscoveryFragment mDiscoveryFragment;
    private MyPersonalCenterFragment mMyPersonalCenterFragment;
    private EventFragment mEventFragment;
    private MyFollowTimeLineFragment mMyFollowFragment;
    private View mCurrentSelectTab;
    private BaseFragment mCurrentFragment;
    BroadcastReceiver mReceiver; // 刚发布完组图后，要在应该显示的页面显示出来（发现或者个人中心页），用来接收发完组图的广播
    private Handler mHandler = new Handler();
    private View mNoticePoint;

    private void assignViews() {
        mIvBottomBar1 = (ImageButton) findViewById(R.id.iv_bottom_bar_1);
        mIvBottomBar2 = (ImageButton) findViewById(R.id.iv_bottom_bar_2);
        mIvBottomBar3 = (ImageButton) findViewById(R.id.iv_bottom_bar_3);
        mIvBottomBar4 = (ImageButton) findViewById(R.id.iv_bottom_bar_4);
        mIvBottomBar5 = (ImageButton) findViewById(R.id.iv_bottom_bar_5);
        mNoticePoint = findViewById(R.id.iv_notice_point);

        mIvBottomBar1.setOnClickListener(this);
        mIvBottomBar2.setOnClickListener(this);
        mIvBottomBar3.setOnClickListener(this);
        mIvBottomBar4.setOnClickListener(this);
        mIvBottomBar5.setOnClickListener(this);

        mIvBottomBar1.setOnLongClickListener(this);
        mIvBottomBar2.setOnLongClickListener(this);
        mIvBottomBar4.setOnLongClickListener(this);
        mIvBottomBar5.setOnLongClickListener(this);

        mFm = getFragmentManager();

        pullNotice();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        assignViews();
        setDiscoveryFragment();
        registerMainBroadcast();
    }

    private Runnable mPullNoticeRun = new Runnable() {
        @Override
        public void run() {
            pullNotice();
        }
    };

    private void pullNotice() {
        if (!TextUtils.isEmpty(App.sessionId)) {
            String url = UrlsUtil.getHasLstestNoticeUrl(App.sessionId);
            Log.d("wangzixu", "pullNotice url = " + url);
            HttpClientManager.getInstance(MainActivity.this).getData(url, new BaseJsonHttpResponseHandler<ResponseBeanLastestNotice>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanLastestNotice response) {
                    if (response.getErr_code() == 0) {
                        if (response.getData().getCount() > 0) {
                            mNoticePoint.setVisibility(View.VISIBLE);
                            if (mMyPersonalCenterFragment != null) {
                                mMyPersonalCenterFragment.setNoticeVisible(View.VISIBLE);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanLastestNotice errorResponse) {

                }

                @Override
                protected ResponseBeanLastestNotice parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    return JsonUtil.fromJson(rawJsonData, ResponseBeanLastestNotice.class);
                }
            });
        } else {
            mNoticePoint.setVisibility(View.GONE);
            if (mMyPersonalCenterFragment != null) {
                mMyPersonalCenterFragment.setNoticeVisible(View.GONE);
            }
        }
        mHandler.removeCallbacks(mPullNoticeRun);
        mHandler.postDelayed(mPullNoticeRun, 30000);
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
                //mDiscoveryFragment.reLoad();
            }
            mCurrentFragment = mDiscoveryFragment;
            transaction.commitAllowingStateLoss();
        } else {

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
                mMyFollowFragment = new MyFollowTimeLineFragment();
                transaction.add(R.id.fl_content, mMyFollowFragment);
            } else {
                transaction.show(mMyFollowFragment);
                mMyFollowFragment.reLoad();
            }
            mCurrentFragment = mMyFollowFragment;
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * 切换到活动页
     */
    @Override
    protected void setEventFragment() {
        if (mCurrentSelectTab != mIvBottomBar4) {
            if (mCurrentSelectTab != null) {
                mCurrentSelectTab.setSelected(false);
            }
            mIvBottomBar4.setSelected(true);
            mCurrentSelectTab = mIvBottomBar4;
            FragmentTransaction transaction = mFm.beginTransaction();
            if (mCurrentFragment != null) {
                transaction.hide(mCurrentFragment);
            }
            if (mEventFragment == null) {
                mEventFragment = new EventFragment();
                transaction.add(R.id.fl_content, mEventFragment);
            } else {
                transaction.show(mEventFragment);
            }
            mCurrentFragment = mEventFragment;
            transaction.commitAllowingStateLoss();
        }
    }

    @Override
    protected void deleteAblum(AlbumInfoBean bean) {
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

    private void registerMainBroadcast() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("wangzixu", "registerMainBroadcast onReceive ---");
                String action = intent.getAction();
                if (UpLoadMainActivity.ACTION_UPDATA_LAST_ABLUM.equals(action)) {
                    App app = (App) getApplication();
                    AlbumInfoBean album = app.getLastestUploadAlbum();
                    app.setLastestUploadAlbum(null);
                    if (mDiscoveryFragment != null) {
                        mDiscoveryFragment.addFirstAblum(album);
                    }
                    if (mMyPersonalCenterFragment != null) {
                        mMyPersonalCenterFragment.addFirstAblum(album);
                    }
                    Log.d("wangzixu", "registerMainBroadcast onReceive 刚发布了组图---");
                } else if (ConstantValues.ACTION_MYFOLLOWS_CHANGE.equals(action)) {
                    String userId = intent.getStringExtra(ConstantValues.KEY__USERID);
                    boolean newStatus = intent.getBooleanExtra(ConstantValues.KEY_NEWSTATUS, false);
                    notifyFragmentsFollowChanged(userId, newStatus);
                } else if (ConstantValues.ACTION_LIKESTATUS_CHANGE.equals(action)) {
                    String ablumid = intent.getStringExtra(ConstantValues.KEY_ALBUMID);
                    int newStatus = intent.getIntExtra(ConstantValues.KEY_NEWSTATUS, 0);
                    notifyLikeStatusChanged(ablumid, newStatus);
                } else if(UpLoadMainActivity.ACTION_CREATE_ALBUM_FAILED.equals(action)) {
                    if (mMyPersonalCenterFragment != null) {
                        mMyPersonalCenterFragment.addAblumFailedHeader();
                    }
                } else if(FailedAlbumActivity.ACTION_RE_CREATE_ALBUM_SUCCESS.equals(action)) {
                    if (mDiscoveryFragment != null) {
                        mDiscoveryFragment.reLoad();
                    }
                    if (mMyPersonalCenterFragment != null) {
                        mMyPersonalCenterFragment.reLoad();
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(UpLoadMainActivity.ACTION_UPDATA_LAST_ABLUM); //发布组图的假数据
        filter.addAction(ConstantValues.ACTION_MYFOLLOWS_CHANGE); //我关注的人改变
        filter.addAction(ConstantValues.ACTION_LIKESTATUS_CHANGE); //点赞状态改变
        filter.addAction(UpLoadMainActivity.ACTION_CREATE_ALBUM_FAILED); //发布组图失败
        filter.addAction(FailedAlbumActivity.ACTION_RE_CREATE_ALBUM_SUCCESS); //发布组图失败
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void notifyFragmentsFollowChanged(String userId, boolean newStatus) {
        if (mDiscoveryFragment != null) {
            mDiscoveryFragment.notifyFragmentsFollowChanged(userId, newStatus);
        }
        if (mMyFollowFragment != null) {
            mMyFollowFragment.notifyFragmentsFollowChanged(userId, newStatus);
        }
        if (mMyPersonalCenterFragment != null) {
            mMyPersonalCenterFragment.notifyFragmentsFollowChanged();
        }
    }

    @Override
    protected void notifyLikeStatusChanged(String ablumId, int newStatus) {
        if (mDiscoveryFragment != null) {
            mDiscoveryFragment.notifyLikeStatusChanged(ablumId, newStatus);
        }
        if (mMyFollowFragment != null) {
            mMyFollowFragment.notifyLikeStatusChanged(ablumId, newStatus);
        }
        if (mMyPersonalCenterFragment != null) {
            mMyPersonalCenterFragment.notifyLikeStatusChanged(ablumId, newStatus);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("wangzixu", "onActivityResult requestCode, resultCode = " + requestCode + ", " + resultCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_LOGIN) { //登录界面回来de
                if (mMyPersonalCenterFragment != null && !TextUtils.isEmpty(App.sessionId)) {
                    mMyPersonalCenterFragment.updataLoginStatus(true);
                }
                if (mMyFollowFragment != null && !TextUtils.isEmpty(App.sessionId)) {
                    mMyFollowFragment.updataLoginStatus(true);
                }
            } else if (requestCode == REQUEST_CODE_SETTING) {//设置页退出登录回来的
                if (mMyPersonalCenterFragment != null && TextUtils.isEmpty(App.sessionId)) {
                    mMyPersonalCenterFragment.updataLoginStatus(false);
                }
                if (mMyFollowFragment != null && !TextUtils.isEmpty(App.sessionId)) {
                    mMyFollowFragment.updataLoginStatus(false);
                }
            }
        }
    }

    private long mLastBackTime = 0;
    @Override
    public void onBackPressed() {
        if (mMorePopupWindow != null && mMorePopupWindow.isShowing()) {
            disMissMorePop();
        } else {
            long currentTime = SystemClock.uptimeMillis();
            if (currentTime - mLastBackTime > 2000) {
                mLastBackTime = currentTime;
                ToastManager.showShort(this, "再按一次退出");
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.iv_bottom_bar_1:
                if (mCurrentFragment == mDiscoveryFragment) {
                    mDiscoveryFragment.longClik();
                    return true;
                }
                break;
            case R.id.iv_bottom_bar_2:
                if (mCurrentFragment == mMyFollowFragment) {
                    mMyFollowFragment.longClik();
                    return true;
                }
                break;
            case R.id.iv_bottom_bar_4:
                if (mCurrentFragment == mEventFragment) {
                    mEventFragment.longClik();
                    return true;
                }
                break;
            case R.id.iv_bottom_bar_5:
                if (mCurrentFragment == mMyPersonalCenterFragment) {
                    mMyPersonalCenterFragment.longClik();
                    return true;
                }
                break;
            default:
                break;
        }
        return false;
    }

    public void resetNoticePoint() {
        mNoticePoint.setVisibility(View.GONE);
    }

    public boolean getNoticePointVisiblity() {
        return mNoticePoint.getVisibility() == View.VISIBLE;
    }
}
