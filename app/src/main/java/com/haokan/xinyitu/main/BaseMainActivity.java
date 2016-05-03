package com.haokan.xinyitu.main;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.haokan.xinyitu.albumfailed.FailedAlbumActivity;
import com.haokan.xinyitu.base.BaseActivity;
import com.haokan.xinyitu.base.BaseResponseBean;
import com.haokan.xinyitu.bigimgbrowse.BigImgBrowseActivity;
import com.haokan.xinyitu.comment.CommentMainActivity;
import com.haokan.xinyitu.follow.FollowMeActivity;
import com.haokan.xinyitu.follow.MyFollowsActivity;
import com.haokan.xinyitu.follow.ResponseBeanFollwsUsers;
import com.haokan.xinyitu.login_register.Login_Register_Activity;
import com.haokan.xinyitu.main.discovery.AlbumInfoBean;
import com.haokan.xinyitu.main.otherpersonalcenter.OtherPersonalCenterActivity;
import com.haokan.xinyitu.setting.ChangeDataMainActivity;
import com.haokan.xinyitu.setting.SettingMainActivity;
import com.haokan.xinyitu.tagtimeline.TagTimelineActivity;
import com.haokan.xinyitu.tipoff.TipOffActivity;
import com.haokan.xinyitu.upload.UpLoadGalleryActivity;
import com.haokan.xinyitu.util.CommonUtil;
import com.haokan.xinyitu.util.ConstantValues;
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.ImageUtil;
import com.haokan.xinyitu.util.JsonUtil;
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

    protected PopupWindow mMorePopupWindow;
    private View mMorePopBg;
    private View mMorePopShareContent;
    private UMShareListener mUMShareListener;
    private Handler mHandler = new Handler();
    private View mMorePopBtn; //点击弹出分享框的按钮，需要改变其select的状态，所以每次点击弹窗是记住点击的按钮，取消时把此按钮select（false）
    private View mPupDeleteView;
    private View mMorePopJubaoContent;
    private String mShareUrl;
    public static final int REQUEST_CODE_LOGIN = 100;
    public static final int REQUEST_CODE_SETTING = 101;
    final public static int REQUEST_CODE_PERMISSION_STORAGE = 102;
    public static final int REQUEST_CODE_COMMENT = 103;
    private TextView mTvCommentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        savedInstanceState = null;
        super.onCreate(savedInstanceState);
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
     * 切换到活动页（第4页）
     */
    protected void setEventFragment() {
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
        int id = v.getId();
        switch (id) {
            case R.id.tv_failed_album: //个人中心中的失败图库
                Intent ifailed = new Intent(BaseMainActivity.this, FailedAlbumActivity.class);
                startActivity(ifailed);
                overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            case R.id.tv_change_personnaldata: //个人中心中的修改个人资料按钮
                Intent i1 = new Intent(BaseMainActivity.this, ChangeDataMainActivity.class);
                startActivity(i1);
                overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            case R.id.iv_for_bigimg: //点击图片进入大图浏览页
                if (CommonUtil.isQuickClick()) {
                    return;
                }
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
                if (CommonUtil.isQuickClick()) {
                    return;
                }

                if (TextUtils.isEmpty(App.sessionId)) {
                    Log.d("wangzixu", "App.sessionId = " + App.sessionId);
                    Intent i5 = new Intent(BaseMainActivity.this, Login_Register_Activity.class);
                    startActivity(i5);
                    overridePendingTransition(R.anim.activity_in_bottom2top, R.anim.activity_out_boootom2top);
                    return;
                }

                if (Build.VERSION.SDK_INT >= 23) {
                    int checkCallPhonePermission = ContextCompat.checkSelfPermission(BaseMainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(BaseMainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE_PERMISSION_STORAGE);
                        return;
                    }else{
                        Intent i3 = new Intent(BaseMainActivity.this, UpLoadGalleryActivity.class);
                        startActivity(i3);
                        overridePendingTransition(R.anim.activity_in_bottom2top, R.anim.activity_out_boootom2top);
                    }
                } else {
                    Intent i3 = new Intent(BaseMainActivity.this, UpLoadGalleryActivity.class);
                    startActivity(i3);
                    overridePendingTransition(R.anim.activity_in_bottom2top, R.anim.activity_out_boootom2top);
                }
                break;
            case R.id.iv_bottom_bar_4:
                setEventFragment();
                break;
            case R.id.iv_bottom_bar_5:
                Log.d("wangzixu", "App.sessionId = " + App.sessionId);
                setMyPersonalCenerFragment();
                break;
            case R.id.ib_person_setting: //个人中心，设置页面
                if (CommonUtil.isQuickClick()) {
                    return;
                }
                Intent iSetting = new Intent(BaseMainActivity.this, SettingMainActivity.class);
                startActivityForResult(iSetting, REQUEST_CODE_SETTING);
                overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            case R.id.tv_not_login: //没有登录，点击弹出登录界面
                if (CommonUtil.isQuickClick()) {
                    return;
                }
                Intent ilogin = new Intent(BaseMainActivity.this, Login_Register_Activity.class);
                startActivityForResult(ilogin, REQUEST_CODE_LOGIN);
                overridePendingTransition(R.anim.activity_in_bottom2top, R.anim.activity_out_boootom2top);
                break;
            case R.id.tv_like_1://条目0的喜欢
                if (CommonUtil.isQuickClick()) {
                    return;
                }
                changeLikeStatus(v);
                break;
            case R.id.rl_personalcenter_myfollow: //个人中心我关注的人
                if (CommonUtil.isQuickClick()) {
                    return;
                }
                Intent intent = new Intent(this, MyFollowsActivity.class);
                Log.d("wangzixu", "rl_personalcenter_myfollow tag = " + v.getTag());
                if (v.getTag() != null && v.getTag() instanceof String) {
                    intent.putExtra(ConstantValues.KEY__USERID, (String)v.getTag());
                }
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            case R.id.rl_personalcenter_followme:
                if (CommonUtil.isQuickClick()) {
                    return;
                }
                Intent intent1 = new Intent(this, FollowMeActivity.class);
                if (v.getTag() != null && v.getTag() instanceof String) {
                    intent1.putExtra(ConstantValues.KEY__USERID, (String)v.getTag());
                }
                startActivity(intent1);
                overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            case R.id.tv_comment_1://条目0的评论
                if (CommonUtil.isQuickClick()) {
                    return;
                }
                mTvCommentBtn = (TextView) v;
                AlbumInfoBean beanDiscovery = (AlbumInfoBean) v.getTag();
                Intent iComment = new Intent(this, CommentMainActivity.class);
                iComment.putExtra(CommentMainActivity.KEY_INITENT_ALBUMID, beanDiscovery.getAlbum_id());
                startActivityForResult(iComment, REQUEST_CODE_COMMENT);
                overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            case R.id.ib_1://首页的关注按钮，关注某人，或者不再关注某人
            case R.id.ib_follow:
                if (CommonUtil.isQuickClick()) {
                    return;
                }
                changeFollowState(v);
                break;
            case R.id.ib_back:
                if (CommonUtil.isQuickClick()) {
                    return;
                }
                onBackPressed();
                overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
                break;
            case R.id.rl_item0_1://首页条目1的头像位置，可以点击的区域
                if (CommonUtil.isQuickClick()) {
                    return;
                }
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
                if (CommonUtil.isQuickClick()) {
                    return;
                }
                if (mMorePopupWindow == null) {
                    initMorePopupWindow();
                }

                if (mMorePopupWindow.isShowing()) {
                    return;
                }

                mMorePopShareContent.setVisibility(View.VISIBLE);
                mMorePopJubaoContent.setVisibility(View.GONE);

                Object tag = v.getTag();
                if (tag instanceof String) { //不是点击的自己的，不能显示删除按钮
                    mPupDeleteView.setVisibility(View.GONE);
                    mShareUrl = (String) tag;
                } else {
                    mPupDeleteView.setVisibility(View.VISIBLE);
                    mPupDeleteView.setTag(tag);
                    mShareUrl = ((AlbumInfoBean)tag).getShare_url();
                }
                mMorePopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
                mMorePopBg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.popupwindow_bg_in));
                mMorePopShareContent.startAnimation(AnimationUtils.loadAnimation(this, R.anim.popupwindow_bottom_in));
                v.setSelected(true);
                mMorePopBtn = v;
                break;
            case R.id.tv_morepop_delete: //删除当前点击的timeline
                if (CommonUtil.isQuickClick()) {
                    return;
                }
                deleteAblum(v);
                break;
            case R.id.tv_tag: //标签
                if (CommonUtil.isQuickClick()) {
                    return;
                }
                Intent itag = new Intent(BaseMainActivity.this, TagTimelineActivity.class);
                DemoTagBean bean = (DemoTagBean) v.getTag();
                itag.putExtra(TagTimelineActivity.KEY_INTENT_TAGID, bean.getId());
                itag.putExtra(TagTimelineActivity.KEY_INTENT_TAGNAME, bean.getName());
                App.sTagString = App.sTagString + bean.getName() + "-";
                startActivity(itag);
                overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            case R.id.tv_morepop_jubao: //举报按钮
                if (CommonUtil.isQuickClick()) {
                    return;
                }
                tipOff();
                break;
            case R.id.tv_jubao_1: //举报按钮
            case R.id.tv_jubao_2: //举报按钮
            case R.id.tv_jubao_3: //举报按钮
            case R.id.tv_jubao_4: //举报按钮
            case R.id.tv_jubao_5: //举报按钮
                if (CommonUtil.isQuickClick()) {
                    return;
                }
                disMissMorePop(new Runnable() {
                    @Override
                    public void run() {
                        Intent ijubao = new Intent(BaseMainActivity.this, TipOffActivity.class);
                        startActivity(ijubao);
//                        overridePendingTransition(R.anim.activity_in_bottom2top, R.anim.activity_out_boootom2top);
                        overridePendingTransition(R.anim.activity_in_bottom2top, R.anim.activity_retain);
                    }
                });
                break;
            case R.id.tv_pop_cancel:
            case R.id.pop_shadow:
                if (CommonUtil.isQuickClick()) {
                    return;
                }
                disMissMorePop();
                break;
            case R.id.tv_morepop_weixin://微信分享
                if (CommonUtil.isQuickClick()) {
                    return;
                }
                UMImage image = new UMImage(BaseMainActivity.this,
                        BitmapFactory.decodeResource(getResources(), R.drawable.icon_home_hot));

                new ShareAction(this)
                        .setPlatform(SHARE_MEDIA.WEIXIN)
                        .setCallback(mUMShareListener)
                        .withText("Levect")
                        .withTargetUrl("http://www.baidu.com")
                        .withMedia(image)
                        .share();
                break;
            case R.id.tv_morepop_qq://qq分享
                if (CommonUtil.isQuickClick()) {
                    return;
                }
                UMImage image1 = new UMImage(BaseMainActivity.this,
                        BitmapFactory.decodeResource(getResources(), R.drawable.icon_home_hot));

                new ShareAction(this)
                        .setPlatform(SHARE_MEDIA.QQ)
                        .setCallback(mUMShareListener)
                        .withText("Levect")
                        .withTargetUrl(mShareUrl)
                        .withMedia(image1)
                        .share();
                break;
            case R.id.tv_morepop_qqzone://qqzone分享
                if (CommonUtil.isQuickClick()) {
                    return;
                }
                UMImage image2 = new UMImage(BaseMainActivity.this,
                        BitmapFactory.decodeResource(getResources(), R.drawable.icon_home_hot));

                new ShareAction(this)
                        .setPlatform(SHARE_MEDIA.QZONE)
                        .setCallback(mUMShareListener)
                        .withText("Levect")
                        .withTargetUrl(mShareUrl)
                        .withMedia(image2)
                        .share();
                break;
            case R.id.tv_morepop_pengyouquan://朋友圈分享
                if (CommonUtil.isQuickClick()) {
                    return;
                }
                UMImage image3 = new UMImage(BaseMainActivity.this,
                        BitmapFactory.decodeResource(getResources(), R.drawable.icon_home_hot));

                new ShareAction(this)
                        .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                        .setCallback(mUMShareListener)
                        .withText("Levect")
                        .withTargetUrl(mShareUrl)
                        .withMedia(image3)
                        .share();
                break;
            case R.id.tv_morepop_weibo://weibo分享
                if (CommonUtil.isQuickClick()) {
                    return;
                }
                UMImage image4 = new UMImage(BaseMainActivity.this,
                        BitmapFactory.decodeResource(getResources(), R.drawable.icon_home_hot));

                new ShareAction(this)
                        .setPlatform(SHARE_MEDIA.SINA)
                        .setCallback(mUMShareListener)
                        .withText("Levect")
                        .withTargetUrl(mShareUrl)
                        .withMedia(image4)
                        .share();
                break;
            default:
                break;
        }
        if  (v instanceof ImageView && !(v instanceof ImageButton)) {
            ImageUtil.changeLight((ImageView) v, true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //用户点击了同意
                    Intent i3 = new Intent(BaseMainActivity.this, UpLoadGalleryActivity.class);
                    startActivity(i3);
                    overridePendingTransition(R.anim.activity_in_bottom2top, R.anim.activity_out_boootom2top);
                } else {
                    // 不同意
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void tipOff() {
        Animation outAnim = AnimationUtils.loadAnimation(this, R.anim.popupwindow_bottom_out);
        outAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                final Animation inAnim = AnimationUtils.loadAnimation(BaseMainActivity.this, R.anim.popupwindow_bottom_in);
                mMorePopShareContent.setVisibility(View.GONE);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mMorePopJubaoContent.setVisibility(View.VISIBLE);
                        mMorePopJubaoContent.startAnimation(inAnim);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mMorePopShareContent.startAnimation(outAnim);
    }

    protected void deleteAblum(View view) {
        final AlbumInfoBean bean = (AlbumInfoBean) view.getTag();
        if (TextUtils.isEmpty(bean.getAlbum_id())) {
            return;
        }

        View v = LayoutInflater.from(this).inflate(R.layout.delete_ablum_dialog_layout, null);
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
    protected void deleteAblum(AlbumInfoBean bean) {

    }

    /**
     * 改变关注按钮的状态，使用于timeline中的可以被复用的关注按钮
     * @param view
     */
    protected void changeFollowState(final View view) {
        final AlbumInfoBean bean = (AlbumInfoBean) view.getTag();
        if (TextUtils.isEmpty(bean.getUser_id())) {
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
                            bean.setIsFollowed(false);

                            ResponseBeanFollwsUsers.FollowUserIdBean idEntity = null;
                            for (int i = 0; i < App.sMyFollowsUser.size(); i++) {
                                if(App.sMyFollowsUser.get(i).getUserid().equals(bean.getUser_id())) {
                                    idEntity = App.sMyFollowsUser.get(i);
                                }
                            }
                            App.sMyFollowsUser.remove(idEntity);
                            notifyFragmentsFollowChanged(bean.getUser_id(), false);

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
            ResponseBeanFollwsUsers.FollowUserIdBean idEntity = new ResponseBeanFollwsUsers.FollowUserIdBean();
            idEntity.setUserid(bean.getUser_id());
            App.sMyFollowsUser.add(idEntity);
            notifyFragmentsFollowChanged(bean.getUser_id(), true);
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

    protected void notifyFragmentsFollowChanged(String userId, boolean newStatus) {
    }

    private void changeLikeStatus(View v) {
        final AlbumInfoBean likeBean = (AlbumInfoBean) v.getTag();
        final TextView textView = (TextView)v;
        final int duration = 130;
        final int newLikeStatus;
        if (v.isSelected()) {
            newLikeStatus = 0;
            likeBean.setIs_liked(0);
            likeBean.setLike_num(likeBean.getLike_num() - 1);
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
                    textView.setText(String.valueOf(likeBean.getLike_num()));
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
            newLikeStatus = 1;
            likeBean.setIs_liked(1);
            likeBean.setLike_num(likeBean.getLike_num() + 1);
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
                    textView.setText(String.valueOf(likeBean.getLike_num()));
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
        mHandler.removeCallbacksAndMessages(likeBean);
        Runnable runnable = new Runnable() {
            @Override
            public void run() { //如果要改变的状态和目前服务器状态相同，则就不用去访问服务器了
                Log.d("wangzixu", "getLikeAlbumUrl likeBean.getIs_liked_clickcount = " + likeBean.getIs_liked_clickcount());
                if (likeBean.getIs_liked_clickcount() % 2 == 0) {
                    likeBean.setIs_liked_clickcount(0);
                    return;
                }
                likeBean.setIs_liked_clickcount(0);
//                synchronized (App.class) { //因为需要用到likeBean.getIs_liked_local()状态，而在其他地方有并行的线程改变这个状态，加锁
//                }
                String url = UrlsUtil.getLikeAlbumUrl(App.sessionId, likeBean.getAlbum_id());
                Log.d("wangzixu", "getLikeAlbumUrl url = " + url);
                HttpClientManager.getInstance(BaseMainActivity.this).getData(url, new BaseJsonHttpResponseHandler<BaseResponseBean>() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, BaseResponseBean response) {

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
        };
        Log.d("wangzixu", "getLikeAlbumUrl");
        likeBean.setIs_liked_clickcount(likeBean.getIs_liked_clickcount()+1);
        mHandler.postAtTime(runnable, likeBean, SystemClock.uptimeMillis() + 3000);
        notifyLikeStatusChanged(likeBean.getAlbum_id(), newLikeStatus);
    }

    /**
     * 点赞的状态改变了
     */
    protected void notifyLikeStatusChanged(String ablumId, int newStatus) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);//友盟分享需要重写的
        switch (requestCode) {
            case REQUEST_CODE_COMMENT:
                if (resultCode == RESULT_OK) {
                    if (mTvCommentBtn != null ) {
                        String commentCount = data.getStringExtra("count");
                        mTvCommentBtn.setText(commentCount);
                        Object object = mTvCommentBtn.getTag();
                        if (object != null) {
                            AlbumInfoBean beanDiscovery = (AlbumInfoBean)object;
                            beanDiscovery.setComment_num(Integer.valueOf(commentCount));
                        }
                    }
                }
                break;
            default:
        }
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
        mMorePopBg.setOnClickListener(this);

        mMorePopShareContent = v.findViewById(R.id.ll_morepop_sharecontent);
        mMorePopShareContent.findViewById(R.id.tv_pop_cancel).setOnClickListener(this);
        mMorePopShareContent.findViewById(R.id.tv_morepop_qq).setOnClickListener(this);
        mMorePopShareContent.findViewById(R.id.tv_morepop_qqzone).setOnClickListener(this);
        mMorePopShareContent.findViewById(R.id.tv_morepop_weibo).setOnClickListener(this);
        mMorePopShareContent.findViewById(R.id.tv_morepop_weixin).setOnClickListener(this);
        mMorePopShareContent.findViewById(R.id.tv_morepop_pengyouquan).setOnClickListener(this);
        mMorePopShareContent.findViewById(R.id.tv_morepop_jubao).setOnClickListener(this);
        mPupDeleteView = mMorePopShareContent.findViewById(R.id.tv_morepop_delete);
        mPupDeleteView.setOnClickListener(this);
        mMorePopShareContent.setOnClickListener(this);

        mMorePopJubaoContent = v.findViewById(R.id.ll_morepop_jubaocontent);
        mMorePopJubaoContent.setVisibility(View.GONE);
        mMorePopJubaoContent.findViewById(R.id.tv_pop_cancel).setOnClickListener(this);
        mMorePopJubaoContent.findViewById(R.id.tv_jubao_1).setOnClickListener(this);
        mMorePopJubaoContent.findViewById(R.id.tv_jubao_2).setOnClickListener(this);
        mMorePopJubaoContent.findViewById(R.id.tv_jubao_3).setOnClickListener(this);
        mMorePopJubaoContent.findViewById(R.id.tv_jubao_4).setOnClickListener(this);
        mMorePopJubaoContent.findViewById(R.id.tv_jubao_5).setOnClickListener(this);
    }

    protected void disMissMorePop() {
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
                    if (mMorePopShareContent.getVisibility() == View.VISIBLE) {
                        mMorePopShareContent.startAnimation(animation);
                    } else {
                        mMorePopJubaoContent.startAnimation(animation);
                    }
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
