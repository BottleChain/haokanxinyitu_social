package com.haokan.xinyitu.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;
import com.haokan.xinyitu.base.BaseResponseBean;
import com.haokan.xinyitu.bigimgbrowse.BigImgBrowseActivity;
import com.haokan.xinyitu.follow.FollowMeActivity;
import com.haokan.xinyitu.follow.MyFollowsActivity;
import com.haokan.xinyitu.login_register.Login_Register_Activity;
import com.haokan.xinyitu.main.discovery.ResponseBeanAlbumInfo;
import com.haokan.xinyitu.main.otherpersonalcenter.OtherPersonalCenterActivity;
import com.haokan.xinyitu.upload.UpLoadGalleryActivity;
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

public abstract class BaseMainActivity extends BaseActivity implements View.OnClickListener {

    private PopupWindow mMorePopupWindow;
    private View mMorePopBg;
    private View mMorePopContent;
    private UMShareListener mUMShareListener;
    private Handler mHandler = new Handler();
    private View mMorePopBtn; //点击弹出分享框的按钮，需要改变其select的状态，所以每次点击弹窗是记住点击的按钮，取消时把此按钮select（false）
    private View mPupDeleteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        savedInstanceState = null;
        super.onCreate(savedInstanceState);
        SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
        // 设置状态栏状态
        systemBarTintManager.setStatusBarTintEnabled(true);
        // 设置状态栏颜色
        systemBarTintManager.setStatusBarTintColor(getResources().getColor(R.color.main_color_actionbar_item01));
        initShare();

    }

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
                Toast.makeText(BaseMainActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(SHARE_MEDIA platform, Throwable t) {
                Toast.makeText(BaseMainActivity.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                //Toast.makeText(MainActivity.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
            }
        };
    }

    /**
     * 切换到发现页（第一页）
     */
    protected void setDiscoveryFragment() {
    }

    /**
     * 切换到个人中心页
     */
    protected void setMyPersonalCenerFragment() {
    }

    /**
     * 切换到我关注的人页
     */
    protected void setMyFollowsFragment() {
    }

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
                    ToastManager.showShort(BaseMainActivity.this, "该图片没有绑定数据");
                } else {
                    ArrayList<DemoImgBean> imgs = (ArrayList<DemoImgBean>) object;
                    int pos = (int) v.getTag(R.string.TAG_KEY_POSITION);
                    Intent ibigimg = new Intent(BaseMainActivity.this, BigImgBrowseActivity.class);
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
                    Intent i5 = new Intent(BaseMainActivity.this, Login_Register_Activity.class);
                    startActivity(i5);
                    overridePendingTransition(R.anim.activity_in_bottom2top, R.anim.activity_out_boootom2top);
                } else {
                    Intent i3 = new Intent(BaseMainActivity.this, UpLoadGalleryActivity.class);
                    startActivity(i3);
                    overridePendingTransition(R.anim.activity_in_bottom2top, R.anim.activity_out_boootom2top);
                }
                break;
            case R.id.iv_bottom_bar_5:
                Log.d("wangzixu", "App.sessionId = " + App.sessionId);
                if (TextUtils.isEmpty(App.sessionId)) {
                    Intent i5 = new Intent(BaseMainActivity.this, Login_Register_Activity.class);
                    startActivity(i5);
                    overridePendingTransition(R.anim.activity_in_bottom2top, R.anim.activity_out_boootom2top);
                } else {
                    setMyPersonalCenerFragment();
                }
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
            case R.id.ib_follow:
                changeFollowState(v);
                break;
            case R.id.ib_back:
                onBackPressed();
                overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
                break;
            case R.id.rl_item0_1://首页条目1的头像位置，可以点击的区域
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
                Object tag = v.getTag();
                if (tag == null) { //不是点击的自己的，不能显示删除按钮
                    mPupDeleteView.setVisibility(View.GONE);
                } else {
                    mPupDeleteView.setVisibility(View.VISIBLE);
                    mPupDeleteView.setTag(tag);
                }
                mMorePopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
                mMorePopBg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.popupwindow_bg_in));
                mMorePopContent.startAnimation(AnimationUtils.loadAnimation(this, R.anim.popupwindow_bottom_in));
                v.setSelected(true);
                mMorePopBtn = v;
                break;
            case R.id.tv_morepop_delete: //删除当前点击的timeline
                deleteAblum(v);
                break;
            case R.id.tv_morepop_jubao: //举报按钮

                break;
            case R.id.tv_pop_cancel:
            case R.id.pop_shadow:
                disMissMorePop();
                break;
            case R.id.tv_morepop_weixin://微信分享
                UMImage image = new UMImage(BaseMainActivity.this,
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
                UMImage image1 = new UMImage(BaseMainActivity.this,
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
                UMImage image2 = new UMImage(BaseMainActivity.this,
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
                UMImage image3 = new UMImage(BaseMainActivity.this,
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
                UMImage image4 = new UMImage(BaseMainActivity.this,
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

    private void tipOff() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

            }
        };
        disMissMorePop();
    }

    protected void deleteAblum(View view) {
        final ResponseBeanAlbumInfo.DataEntity bean = (ResponseBeanAlbumInfo.DataEntity) view.getTag();
        if (TextUtils.isEmpty(bean.getAlbum_id())) {
            return;
        }

        View v = LayoutInflater.from(this).inflate(R.layout.delete_ablum_dialog_layout, null);
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
                        if (bean.getAlbum_id().equals("failed")) { //上传失败时给赋的值
                            //nothing
                        } else {
                            String url = UrlsUtil.getDelAlbumUrl(App.sessionId, bean.getAlbum_id());
                            Log.d("wangzixu", "deleteAblum cancel url = " + url);
                            HttpClientManager.getInstance(BaseMainActivity.this).getData(url, new BaseJsonHttpResponseHandler<BaseResponseBean>() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, BaseResponseBean response) {
                                    Log.d("wangzixu", "deleteAblum onSuccess = " + response.getErr_msg());
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
                        disMissMorePop();
                        deleteAblum(bean);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * 已经想服务器发送了删除图册的请求，需要本地直接处理删除后的结果。
     */
    protected void deleteAblum(ResponseBeanAlbumInfo.DataEntity bean) {

    }

    /**
     * 改变关注按钮的状态，使用于timeline中的可以被复用的关注按钮
     * @param view
     */
    protected void changeFollowState(final View view) {
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
                            HttpClientManager.getInstance(BaseMainActivity.this).getData(url, new BaseJsonHttpResponseHandler<BaseResponseBean>() {
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
    public void onBackPressed() {
        if (mMorePopupWindow != null && mMorePopupWindow.isShowing()) {
            disMissMorePop();
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
//        v.findViewById(R.id.tv_morepop_download).setOnClickListener(this);
        mPupDeleteView = v.findViewById(R.id.tv_morepop_delete);
        mPupDeleteView.setOnClickListener(this);
        mMorePopContent.setOnClickListener(this);
    }

    private void disMissMorePop() {
        disMissMorePop(null);
    }

    private void disMissMorePop(final Runnable endRunnable) {
        mMorePopBtn.setSelected(false);
        if (mMorePopupWindow != null && mMorePopupWindow.isShowing()) {
            final Animation outAnim = AnimationUtils.loadAnimation(this, R.anim.popupwindow_bg_out);
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
                            if (endRunnable != null) {
                                endRunnable.run();
                            }
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

            });

            final Animation animation = AnimationUtils.loadAnimation(this, R.anim.popupwindow_bottom_out);
            animation.setFillAfter(true);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mMorePopBg.startAnimation(outAnim);
                    mMorePopContent.startAnimation(animation);
                }
            });
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Config.dialog = null; //释放一些绑定了此activity的资源，防止内存泄露
    }
}
