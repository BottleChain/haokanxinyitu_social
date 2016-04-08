package com.haokan.xinyitu.main;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;
import com.haokan.xinyitu.base.BaseFragment;
import com.haokan.xinyitu.base.BaseResponseBean;
import com.haokan.xinyitu.bigimgbrowse.BigImgBrowseActivity;
import com.haokan.xinyitu.follow.FollowMeActivity;
import com.haokan.xinyitu.follow.MyFollowsActivity;
import com.haokan.xinyitu.login_register.Login_Register_Activity;
import com.haokan.xinyitu.main.MyFollowsTimeLine.MyFollowFragment;
import com.haokan.xinyitu.main.discovery.DiscoveryFragment;
import com.haokan.xinyitu.main.discovery.ResponseBeanAlbumInfo;
import com.haokan.xinyitu.main.mypersonalcenter.MyPersonalCenterFragment;
import com.haokan.xinyitu.main.otherpersonalcenter.OtherPersonalCenterActivity;
import com.haokan.xinyitu.upload.UpLoadGalleryActivity;
import com.haokan.xinyitu.upload.UpLoadMainActivity;
import com.haokan.xinyitu.util.CommonUtil;
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.ImageUtil;
import com.haokan.xinyitu.util.JsonUtil;
import com.haokan.xinyitu.util.SystemBarTintManager;
import com.haokan.xinyitu.util.ToastManager;
import com.haokan.xinyitu.util.UrlsUtil;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends BaseMainActivity {

    private ImageButton mIvBottomBar1;
    private ImageButton mIvBottomBar2;
    private ImageButton mIvBottomBar3;
    private ImageButton mIvBottomBar4;
    private ImageButton mIvBottomBar5;
    private FrameLayout mFlContent;
    private FragmentManager mFm;
    private PopupWindow mMorePopupWindow;
    private View mMorePopBg;
    private View mMorePopContent;
    private DiscoveryFragment mDiscoveryFragment;
    private MyPersonalCenterFragment mMyPersonalCenterFragment;
    private MyFollowFragment mMyFollowFragment;
    private Handler mHandler = new Handler();
    BroadcastReceiver mReceiver; // 刚发布完组图后，要在应该显示的页面显示出来（发现或者个人中心页），用来接收发完组图的广播

    private void assignViews() {
        mIvBottomBar1 = (ImageButton) findViewById(R.id.iv_bottom_bar_1);
        mIvBottomBar2 = (ImageButton) findViewById(R.id.iv_bottom_bar_2);
        mIvBottomBar3 = (ImageButton) findViewById(R.id.iv_bottom_bar_3);
        mIvBottomBar4 = (ImageButton) findViewById(R.id.iv_bottom_bar_4);
        mIvBottomBar5 = (ImageButton) findViewById(R.id.iv_bottom_bar_5);
        mFlContent = (FrameLayout) findViewById(R.id.fl_content);

        mIvBottomBar1.setOnClickListener(this);
        mIvBottomBar2.setOnClickListener(this);
        mIvBottomBar3.setOnClickListener(this);
        mIvBottomBar4.setOnClickListener(this);
        mIvBottomBar5.setOnClickListener(this);

        mFm = getFragmentManager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        savedInstanceState = null;
        super.onCreate(savedInstanceState);
        SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
        // 设置状态栏状态
        systemBarTintManager.setStatusBarTintEnabled(true);
        // 设置状态栏颜色
        systemBarTintManager.setStatusBarTintColor(getResources().getColor(R.color.main_color_actionbar_item01));
        setContentView(R.layout.homepage_activity_layout);
        assignViews();

        setDiscoveryFragment();

        initShare();
        registerCompleteAblumBroadcast();
    }

    private UMShareListener mUMShareListener;
    /**
     * 初始化分享用的一些东西，如dialog样式，回调监听，等
     */
    private void initShare() {
        ProgressDialog dialog =  new ProgressDialog(this);
        dialog.setMessage("分享中...");
        Config.dialog = dialog;
        Config.IsToastTip = true;
        mUMShareListener = new UMShareListener() {
            @Override
            public void onResult(SHARE_MEDIA platform) {
                Toast.makeText(MainActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(SHARE_MEDIA platform, Throwable t) {
                Toast.makeText(MainActivity.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                //Toast.makeText(MainActivity.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private View mCurrentSelectTab;
    private BaseFragment mCurrentFragment;

    /**
     * 切换到发现页（第一页）
     */
    private void setDiscoveryFragment() {
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
    private void setMyPersonalCenerFragment() {
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
    private void setMyFollowsFragment() {
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

    private View mMorePopBtn; //点击弹出分享框的按钮，需要改变其select的状态，所以每次点击弹窗是记住点击的按钮，取消时把此按钮select（false）
    @Override
    public void onClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
        if (v instanceof ImageView && !(v instanceof ImageButton)) {
            ImageUtil.changeLight((ImageView) v, true);
        }
        int id = v.getId();
        switch (id) {
            case R.id.iv_for_bigimg: //点击图片进入大图浏览页
                Object object = v.getTag(R.string.TAG_KEY_BEAN_FOR_BIGIMG);
                if (object == null) {
                    ToastManager.showShort(MainActivity.this, "该图片没有绑定数据");
                } else {
                    ArrayList<DemoImgBean> imgs = (ArrayList<DemoImgBean>) object;
                    int pos = (int) v.getTag(R.string.TAG_KEY_POSITION);
                    Intent ibigimg = new Intent(MainActivity.this, BigImgBrowseActivity.class);
                    ibigimg.putExtra(BigImgBrowseActivity.EXTRA_USED, 1);
                    ibigimg.putExtra(BigImgBrowseActivity.EXTRA_INIT_POSITION, pos);
                    ibigimg.putParcelableArrayListExtra(BigImgBrowseActivity.EXTRA_IMG_DATA, imgs);
                    startActivity(ibigimg);
                    overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_retain);
                }
                break;
            case R.id.iv_bottom_bar_1:
                setDiscoveryFragment();
                break;
            case R.id.iv_bottom_bar_2:
                setMyFollowsFragment();
                break;
            case R.id.iv_bottom_bar_3:
                if (TextUtils.isEmpty(App.sessionId)) {
                    Log.d("wangzixu", "App.sessionId = " + App.sessionId);
                    Intent i5 = new Intent(MainActivity.this, Login_Register_Activity.class);
                    startActivity(i5);
                    overridePendingTransition(R.anim.activity_in_bottom2top, R.anim.activity_out_boootom2top);
                } else {
                    Intent i3 = new Intent(MainActivity.this, UpLoadGalleryActivity.class);
                    startActivity(i3);
                    overridePendingTransition(R.anim.activity_in_bottom2top, R.anim.activity_out_boootom2top);
                }
                break;
            case R.id.iv_bottom_bar_5:
//                if (TextUtils.isEmpty(App.sessionId)) {
//                    Log.d("wangzixu", "App.sessionId = " + App.sessionId);
//                    Intent i5 = new Intent(MainActivity.this, Login_Register_Activity.class);
//                    startActivity(i5);
//                    overridePendingTransition(R.anim.activity_in_bottom2top, R.anim.activity_out_boootom2top);
//                } else {
//                }
                    setMyPersonalCenerFragment();
                break;
            case R.id.tv_like_1://条目0的喜欢
                final TextView textView = (TextView)v;
                final int duration = 130;
                if (v.isSelected()) {
                    final ScaleAnimation animation = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f
                            , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(duration);
                    //animation.setInterpolator(new AnticipateInterpolator());
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            textView.setSelected(false);
                            textView.setText(String.valueOf(Integer.valueOf(textView.getText().toString()) - 1));
                            ScaleAnimation animRe = new ScaleAnimation(1.2f, 1.0f, 1.2f, 1.0f
                                    , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                            animRe.setDuration(duration);
                            //animRe.setInterpolator(new OvershootInterpolator());
                            textView.startAnimation(animRe);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.startAnimation(animation);
                        }
                    });
                } else {
                    final ScaleAnimation animation = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f
                            , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(duration);
                    //animation.setInterpolator(new AnticipateInterpolator());
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            textView.setSelected(true);
                            textView.setText(String.valueOf(Integer.valueOf(textView.getText().toString()) + 1));
                            ScaleAnimation animRe = new ScaleAnimation(1.2f, 1.0f, 1.2f, 1.0f
                                    , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                            animRe.setDuration(duration);
                            //animRe.setInterpolator(new OvershootInterpolator());
                            textView.startAnimation(animRe);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.startAnimation(animation);
                        }
                    });
                }
                break;
            case R.id.rl_personalcenter_myfollow: //个人中心我关注的人
                Intent intent = new Intent(this, MyFollowsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            case R.id.rl_personalcenter_followme:
                Intent intent1 = new Intent(this, FollowMeActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            case R.id.ib_person_setting:
                break;
            case R.id.tv_comment_1://条目0的评论
                break;
            case R.id.ib_1://首页的关注按钮，关注某人，或者不再关注某人
                changeFollowState(v);
                break;
            case R.id.rl_item0_1://首页的关注按钮，关注某人，或者不再关注某人
                String userId = (String) v.getTag();
                if (TextUtils.isEmpty(userId)) {
                    return;
                }
                Intent intent2 = new Intent(this, OtherPersonalCenterActivity.class);
                intent2.putExtra(OtherPersonalCenterActivity.KEY_INTENT_USERID, userId);
                startActivity(intent2);
                overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            case R.id.ib_item0_more://条目0的更多按钮
                if (mMorePopupWindow == null) {
                    initMorePopupWindow();
                }

                if (mMorePopupWindow.isShowing()) {
                    return;
                }
                mMorePopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
                mMorePopBg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.popupwindow_bg_in));
                mMorePopContent.startAnimation(AnimationUtils.loadAnimation(this, R.anim.popupwindow_bottom_in));
                v.setSelected(true);
                mMorePopBtn = v;
                break;
            case R.id.tv_pop_cancel:
            case R.id.pop_shadow:
                disMissMorePop();
                mMorePopBtn.setSelected(false);
                break;
            case R.id.tv_morepop_weixin://微信分享
                UMImage image = new UMImage(MainActivity.this,
                        BitmapFactory.decodeResource(getResources(), R.drawable.icon_home_hot));

                new ShareAction(this)
                        .setPlatform(SHARE_MEDIA.WEIXIN)
                        .setCallback(mUMShareListener)
                        .withText("hello umeng video")
                        .withTargetUrl("http://www.baidu.com")
                        .withMedia(image)
                        .share();
                break;
            case R.id.tv_morepop_qq://qq分享
                UMImage image1 = new UMImage(MainActivity.this,
                        BitmapFactory.decodeResource(getResources(), R.drawable.icon_home_hot));

                new ShareAction(this)
                        .setPlatform(SHARE_MEDIA.QQ)
                        .setCallback(mUMShareListener)
                        .withText("hello umeng video")
                        .withTargetUrl("http://www.baidu.com")
                        .withMedia(image1)
                        .share();
                break;
            case R.id.tv_morepop_qqzone://qqzone分享
                UMImage image2 = new UMImage(MainActivity.this,
                        BitmapFactory.decodeResource(getResources(), R.drawable.icon_home_hot));

                new ShareAction(this)
                        .setPlatform(SHARE_MEDIA.QZONE)
                        .setCallback(mUMShareListener)
                        .withText("hello umeng video")
                        .withTargetUrl("http://www.baidu.com")
                        .withMedia(image2)
                        .share();
                break;
            case R.id.tv_morepop_pengyouquan://朋友圈分享
                UMImage image3 = new UMImage(MainActivity.this,
                        BitmapFactory.decodeResource(getResources(), R.drawable.icon_home_hot));

                new ShareAction(this)
                        .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                        .setCallback(mUMShareListener)
                        .withText("hello umeng video")
                        .withTargetUrl("http://www.baidu.com")
                        .withMedia(image3)
                        .share();
                break;
            case R.id.tv_morepop_weibo://weibo分享
                UMImage image4 = new UMImage(MainActivity.this,
                        BitmapFactory.decodeResource(getResources(), R.drawable.icon_home_hot));

                new ShareAction(this)
                        .setPlatform(SHARE_MEDIA.SINA)
                        .setCallback(mUMShareListener)
                        .withText("hello umeng video")
                        .withTargetUrl("http://www.baidu.com")
                        .withMedia(image4)
                        .share();
                break;
            default:
                break;
        }
    }

    private void changeFollowState(final View view) {
        final ResponseBeanAlbumInfo.DataEntity bean = (ResponseBeanAlbumInfo.DataEntity) view.getTag();
        if (TextUtils.isEmpty(bean.getUser_id())) {
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
                            bean.setIsFollowed(false);
                            String url = UrlsUtil.getdelFollowUrl(App.sessionId, bean.getUser_id());
                            Log.d("wangzixu", "changeFollowState cancel url = " + url);
                            HttpClientManager.getInstance(MainActivity.this).getData(url, new BaseJsonHttpResponseHandler<BaseResponseBean>() {
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
            bean.setIsFollowed(true);
            String url = UrlsUtil.getaddFollowUrl(App.sessionId, bean.getUser_id());
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);//友盟分享需要重写的
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

    @Override
    public void onBackPressed() {
        if (mMorePopupWindow != null && mMorePopupWindow.isShowing()) {
            disMissMorePop();
            mMorePopBtn.setSelected(false);
        } else {
            super.onBackPressed();
        }
    }

    private void initMorePopupWindow() {
        View v = LayoutInflater.from(this).inflate(R.layout.homepage_more_popupwindow, null);
        mMorePopupWindow = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mMorePopupWindow.setFocusable(false);
        mMorePopupWindow.setAnimationStyle(0);

        mMorePopBg = v.findViewById(R.id.pop_shadow);
        mMorePopContent = v.findViewById(R.id.ll_morepop_content);

        mMorePopBg.setOnClickListener(this);
        v.findViewById(R.id.tv_pop_cancel).setOnClickListener(this);
        v.findViewById(R.id.tv_morepop_qq).setOnClickListener(this);
        v.findViewById(R.id.tv_morepop_qqzone).setOnClickListener(this);
        v.findViewById(R.id.tv_morepop_weibo).setOnClickListener(this);
        v.findViewById(R.id.tv_morepop_weixin).setOnClickListener(this);
        v.findViewById(R.id.tv_morepop_pengyouquan).setOnClickListener(this);
        v.findViewById(R.id.tv_morepop_jubao).setOnClickListener(this);
        v.findViewById(R.id.tv_morepop_download).setOnClickListener(this);
        v.findViewById(R.id.tv_morepop_delete).setOnClickListener(this);
        mMorePopContent.setOnClickListener(this);
    }

    private void disMissMorePop() {
        if (mMorePopupWindow != null && mMorePopupWindow.isShowing()) {
            Animation outAnim = AnimationUtils.loadAnimation(this, R.anim.popupwindow_bg_out);
            outAnim.setFillAfter(true);
            outAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            mMorePopupWindow.dismiss();
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

            });
            mMorePopBg.startAnimation(outAnim);

            Animation animation = AnimationUtils.loadAnimation(this, R.anim.popupwindow_bottom_out);
            animation.setFillAfter(true);
            mMorePopContent.startAnimation(animation);
        }
    }

    private void registerCompleteAblumBroadcast() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("wangzixu", "BroadcastReceiver onReceive ---");
                App app = (App) getApplication();
                ResponseBeanAlbumInfo.DataEntity album = app.getLastestUploadAlbum();
                app.setLastestUploadAlbum(null);
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
        Config.dialog = null; //释放一些绑定了此activity的资源，防止内存泄露
    }
}
