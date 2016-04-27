package com.haokan.xinyitu.upload;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.albumfailed.FailedAlbumInfoBean;
import com.haokan.xinyitu.base.AvatarUrlBean;
import com.haokan.xinyitu.base.BaseActivity;
import com.haokan.xinyitu.bigimgbrowse.BigImgBrowseActivity;
import com.haokan.xinyitu.database.MyDatabaseHelper;
import com.haokan.xinyitu.main.DemoImgBean;
import com.haokan.xinyitu.main.DemoTagBean;
import com.haokan.xinyitu.main.discovery.AlbumInfoBean;
import com.haokan.xinyitu.util.CommonUtil;
import com.haokan.xinyitu.util.ConstantValues;
import com.haokan.xinyitu.util.DisplayUtil;
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.ImageLoaderManager;
import com.haokan.xinyitu.util.ImageUtil;
import com.haokan.xinyitu.util.ImgAndTagWallManager;
import com.haokan.xinyitu.util.JsonUtil;
import com.haokan.xinyitu.util.SecurityUtil;
import com.haokan.xinyitu.util.ToastManager;
import com.haokan.xinyitu.util.UrlsUtil;
import com.j256.ormlite.dao.Dao;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class UpLoadMainActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
    private RelativeLayout mRlHeader;
    private TextView mTvCancel;
    private TextView mTvConfirm;
    private EditText mEtUploadmainEdit;
    private TextView mTvTextcount;
    private RelativeLayout mRlUploadmianAddtag;
    private RelativeLayout mRlUploadmianAddtagContainer;
    private TextView mTvUploadmainGoonadd;
    private TextView mTvAddTag;
    private RelativeLayout mRlUploadmain;
    private RelativeLayout mRlUploadmainLocation;
    private ArrayList<DemoImgBean> mImgData;
    private Handler mHandler = new Handler();
    private int mLastLoadImgCount;
    private ProgressDialog mProgressDialog;

    private int START_CODE_TAG = 100;
    private int START_CODE_LOCATION = 101;

    private int mTagTextPadding;
    private int mTagTextSizePx; //px
    private int mTagTextSize; //sp
    private int mTagDrawablePading;
    private int mTagDrawableWidth;
    private int mTagRlWidth;
    public static final String ACTION_UPDATA_LAST_ABLUM = "com.haokan.xinyitu.tryCreateAblum.success";
    public static final String ACTION_CREATE_ALBUM_FAILED = "com.haokan.xinyitu.album.failed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadmain_activity_layout);
        assignViews();

        loadData();
    }

    private void assignViews() {
        mRlHeader = (RelativeLayout) findViewById(R.id.rl_header);
        mTvCancel = (TextView) findViewById(R.id.tv_cancel);
        mTvConfirm = (TextView) findViewById(R.id.tv_confirm);
        mEtUploadmainEdit = (EditText) findViewById(R.id.et_edit);
        mTvTextcount = (TextView) findViewById(R.id.tv_textcount);
        mTvAddTag = (TextView) findViewById(R.id.tv_add_tag);
        mRlUploadmianAddtag = (RelativeLayout) findViewById(R.id.rl_uploadmian_addtag);
        mRlUploadmianAddtagContainer = (RelativeLayout) findViewById(R.id.rl_uploadmian_addtag_container);
        mTvUploadmainGoonadd = (TextView) findViewById(R.id.tv_uploadmain_goonadd);
        mRlUploadmain = (RelativeLayout) findViewById(R.id.rl_uploadmain);
        mRlUploadmainLocation = (RelativeLayout) findViewById(R.id.rl_uploadmian_location);

        mTvCancel.setOnClickListener(this);
        mTvConfirm.setOnClickListener(this);
        mEtUploadmainEdit.addTextChangedListener(this);
        mRlUploadmianAddtag.setOnClickListener(this);
        mTvUploadmainGoonadd.setOnClickListener(this);
        mRlUploadmainLocation.setOnClickListener(this);

        //添加标签用的属性值
        mTagTextSize = 14;
        mTagTextSizePx = DisplayUtil.sp2px(this, mTagTextSize);
        mTagTextPadding = DisplayUtil.dip2px(this, 7);
        mTagRlWidth = getResources().getDisplayMetrics().widthPixels - DisplayUtil.dip2px(this, 30);
        mTagDrawablePading = DisplayUtil.dip2px(this, 4);
        mTagDrawableWidth = DisplayUtil.dip2px(this, 13);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    private void loadData() {
        App app = (App) getApplication();
        mImgData = app.getCheckedImgs();
        if (mLastLoadImgCount != 0 && mLastLoadImgCount == mImgData.size()) {
            return;
        }
        mLastLoadImgCount = mImgData.size();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ImgAndTagWallManager.getInstance(UpLoadMainActivity.this).initImgsWall(mImgData);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //显示图片
                        //Log.d("wangzixu", "initDiscoveryItem0 ----pos, imgCount = " + position + ", " + imgs.size());
                        mRlUploadmain.removeAllViewsInLayout();
                        for (int i = 0; i < mImgData.size(); i++) {
                            DemoImgBean bean = mImgData.get(i);
                            ImageView img = new ImageView(UpLoadMainActivity.this);
                            img.setScaleType(ImageView.ScaleType.CENTER);
                            //            img.setBackgroundColor(sBgColors[i % 4]);
                            img.setImageResource(R.drawable.icon_nopic);
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(bean.getItemWidth(), bean.getItemHeigh());
                            lp.leftMargin = bean.getMarginLeft();
                            lp.topMargin = bean.getMarginTop();
                            Log.d("wangzixu", "upLoadmain loadData i,l,t,w,h = " + ", " + i + ", " + bean.getMarginLeft() + ", " + bean.getMarginTop()
                                    + ", " + bean.getItemWidth() + ", " + bean.getItemHeigh());
                            //Log.d("wangzixu", "getView pos acturlItemW,H = " + position + ", " + bean.getItemWidth() + ", " + bean.getItemHeigh());
                            img.setLayoutParams(lp);
                            //mRlUploadmain.addView(img);
                            mRlUploadmain.addView(img, lp);
                            img.setTag(R.string.TAG_KEY_IS_FADEIN, true);
                            img.setTag(R.string.TAG_KEY_POSITION, i);
                            //img.setTag(R.string.TAG_KEY_BEAN_FOR_BIGIMG, imgs);
                            img.setId(R.id.iv_for_bigimg);
                            img.setOnClickListener(UpLoadMainActivity.this);
                            ImageLoaderManager.getInstance().asyncLoadImage(img, bean.getUrl()
                                    , bean.getItemWidth(), bean.getItemHeigh());
                        }
                    }
                });
            }
        }).start();
    }

    private boolean mHasFillTagIds;
    private ArrayList<String> mTagIds;
    private String mAlbumDes; //组图的配字

    /**
     * 发布图片，包括3步，1-检测图片是否支持秒传，2-支持秒传则走秒传接口，否则走传图片接口，3-发布组图
     * ps：步骤1和2是每张图进行一次，把服务器返回的每张图片的码集合起来一起再发布组图
     */
    private void releaseImgs() {
        if (TextUtils.isEmpty(App.sessionId)) {
            ToastManager.showShort(UpLoadMainActivity.this, "sessionId null, return!");
            mProgressDialog.dismiss();
            return;
        }

//        if (mTags == null || mTags.size() == 0) {
//            ToastManager.showShort(UpLoadMainActivity.this, "标签不能为空");
//            mProgressDialog.dismiss();
//            return;
//        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
                UpLoadMainActivity.super.onBackPressed();
            }
        }, 800);

        mAlbumDes = mEtUploadmainEdit.getText().toString();

        //makeLastestReleaseAlbum(); 造假数据展示在首页，去掉

        //1,检测图片是否支持秒传
        String scecondUploadCheckUrl = UrlsUtil.getSecondUploadCheckUrl(App.sessionId);
        Log.d("wangzixu", "releaseImgs scecondUploadCheckUrl = " + scecondUploadCheckUrl);
        for (int i = 0; i < mImgData.size(); i++) {
            final DemoImgBean imgBean = mImgData.get(i);
            final String md5 = imgBean.getMd5();
            HttpClientManager.getInstance(UpLoadMainActivity.this).scondUploadCheck(scecondUploadCheckUrl, md5
                    , new BaseJsonHttpResponseHandler<ResponseBeanSecondUploadCheck>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanSecondUploadCheck response) {
                    if (response != null && response.getErr_code() == 0) { //成功支持秒传
                        Log.d("wangzixu", "releaseImgs scecondUploadCheck 支持");
                        secondUpload(imgBean, App.sessionId, mTags, response.getData().getUnique_id(), response.getData().getFile_name());
                    } else { //不支持秒传
                        Log.d("wangzixu", "releaseImgs scecondUploadCheck 不支持");
                        uploadImgFile(imgBean, App.sessionId, mTags);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanSecondUploadCheck errorResponse) {
                    //访问服务器失败
                    Log.d("wangzixu", "releaseImgs scecondUploadCheck onFailure i");
                }

                @Override
                protected ResponseBeanSecondUploadCheck parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    Log.d("wangzixu", "releaseImgs scecondUploadCheck rawJsonData = " + rawJsonData);
                    return JsonUtil.fromJson(rawJsonData, ResponseBeanSecondUploadCheck.class);
                }
            });
        }
    }

    private void secondUpload(final DemoImgBean imgBean, String sessonId, ArrayList<DemoTagBean> tags, String unique_id, String fileName) {
        String url = UrlsUtil.getSecondUploadUrl(sessonId);
        Log.d("wangzixu", "releaseImgs secondUpload url = " + url);

        HttpClientManager.getInstance(UpLoadMainActivity.this).secondUpLoadFile(url, unique_id, null, fileName, tags
                , new BaseJsonHttpResponseHandler<ResponseBeanImgUpload>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanImgUpload response) {
                if (response.getErr_code() == 0) {
                    Log.d("wangzixu", "releaseImgs secondUpload onSuccess");
                    imgBean.setId(response.getData().getImage().getImage_id());
                    if (!mHasFillTagIds) {
                        fillTagIds(response.getData().getTags());
                    }
                } else { //上传失败 赋值一个true
                    Log.d("wangzixu", "releaseImgs secondUpload onSuccess 返回了1，失败原因 = " + response.getErr_msg());
                    imgBean.setId("true");
                }
                tryCreateAblum();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanImgUpload errorResponse) {
                //上传失败
                Log.d("wangzixu", "releaseImgs secondUpload onFailure");
                imgBean.setId("true");
                tryCreateAblum();
            }

            @Override
            protected ResponseBeanImgUpload parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                Log.d("wangzixu", "releaseImgs secondUpload rawJsonData = " + rawJsonData);
                return JsonUtil.fromJson(rawJsonData, ResponseBeanImgUpload.class);
            }
        });
    }

    /**
     * 上传单张图片
     */
    private void uploadImgFile(final DemoImgBean imgBean, String sessonId, ArrayList<DemoTagBean> tags) {
        String upfileUrl = UrlsUtil.getImageUploadUrl(sessonId);
        Log.d("wangzixu", "releaseImgs uploadImgFile upfileUrl = " + upfileUrl);
        String filePath = ImageDownloader.Scheme.FILE.crop(imgBean.getUrl());
        File file = new File(filePath);
        Log.d("wangzixu", "releaseImgs uploadImgFile filePath = " + filePath);
        HttpClientManager.getInstance(UpLoadMainActivity.this).upLoadImgFile(upfileUrl, file, null, tags
                , new BaseJsonHttpResponseHandler<ResponseBeanImgUpload>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanImgUpload response) {
                if (response.getErr_code() == 0) {
                    Log.d("wangzixu", "releaseImgs uploadImgFile onSuccess");
                    imgBean.setId(response.getData().getImage().getImage_id());
                    if (!mHasFillTagIds) {
                        fillTagIds(response.getData().getTags());
                    }
                    tryCreateAblum();
                } else { //上传失败 赋值一个-100
                    Log.d("wangzixu", "releaseImgs uploadImgFile onSuccess 返回了1，失败原因 = " + response.getErr_msg());
                    imgBean.setId("true");
                    tryCreateAblum();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanImgUpload errorResponse) {
                //上传失败
                Log.d("wangzixu", "releaseImgs uploadImgFile onFailure");
                imgBean.setId("true");
                tryCreateAblum();
            }

            @Override
            protected ResponseBeanImgUpload parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                Log.d("wangzixu", "releaseImgs uploadImgFile rawJsonData = " + rawJsonData);
                return JsonUtil.fromJson(rawJsonData, ResponseBeanImgUpload.class);
            }
        });
    }

    private void fillTagIds(List<DemoTagBean> tags) {
        mHasFillTagIds = true;
        mTagIds = new ArrayList<>();
        for (int i = 0; i < tags.size(); i++) {
            mTagIds.add(tags.get(i).getId());
        }
    }

    private void tryCreateAblum() {
        boolean isSuccess = true;
        for (DemoImgBean bean : mImgData) {
            if (TextUtils.isEmpty(bean.getId())) { //说明还有图没有上传成功
                return;
            }
            if (Boolean.valueOf(bean.getId())) { //说明此张图上传失败了
                isSuccess = false;
            }
        }
        Log.d("wangzixu", "releaseImgs tryCreateAblum 所有组图已经上传完成，isSuccess = " + mAlbumDes + ", " + isSuccess);
        //每个图片的imageid都不为null了，说明全部传完了-----
        if (isSuccess) { //全部上传成功了，发组图
            String url = UrlsUtil.getCreateAblumUrl(App.sessionId);
            Log.d("wangzixu", "releaseImgs tryCreateAblum url = " + url);
            HttpClientManager.getInstance(UpLoadMainActivity.this).createAblum(url, "组图标题", mAlbumDes
                    , mImgData, mTagIds, new BaseJsonHttpResponseHandler<ResponseBeanCreateAblum>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanCreateAblum response) {
                    if (response.getErr_code() == 0) {
                        Log.d("wangzixu", "releaseImgs tryCreateAblum success , errorCode，id = "
                                + response.getErr_code() + ", " + response.getData().getAlbum_id());
                        ToastManager.showShort(UpLoadMainActivity.this, "发布组图成功");
                        makeLastestReleaseAlbum(response);
                    } else {
                        createAlbumFailed();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanCreateAblum errorResponse) {
                    Log.d("wangzixu", "releaseImgs tryCreateAblum onFailure");
                    createAlbumFailed();
                }

                @Override
                protected ResponseBeanCreateAblum parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    Log.d("wangzixu", "releaseImgs tryCreateAblum rawJsonData = " + rawJsonData);
                    return JsonUtil.fromJson(rawJsonData, ResponseBeanCreateAblum.class);
                }
            });
        } else { //没有上传成功，需要把失败信息存到某一个地方，在个人中心页中能展示出来这些失败的信息
            createAlbumFailed();
        }
    }

    private void createAlbumFailed() {
        FailedAlbumInfoBean bean = new FailedAlbumInfoBean();
        bean.setDesc(mAlbumDes);
        bean.setUserId(App.user_Id);
        String tags="";
        if (mTags.size() > 0) {
            tags = JsonUtil.toJson(mTags);
        }
        bean.setTags(tags);

        String imgUrls = JsonUtil.toJson(mImgData);
        bean.setImg_urls(imgUrls);
//            Log.d("wangzixu", "releaseImgs tryCreateAblum tags, imgUrls = " + tags  + ", " +imgUrls);
        try {
            Dao dao = MyDatabaseHelper.getInstance(this).getDaoQuickly(FailedAlbumInfoBean.class);
            dao.create(bean);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ToastManager.showShort(UpLoadMainActivity.this, "您有组图发布失败");
        Intent intent = new Intent(ACTION_CREATE_ALBUM_FAILED);
        sendBroadcast(intent);
    }

    /**
     * 发送组图后，立马回主页，此时要显示刚发送的东西在第一条，所以需要手工制作这一条数据
     */
    private void makeLastestReleaseAlbum(ResponseBeanCreateAblum response) {
        //创建一个组图信息，发给首页，然后通知首页更新一下界面
        AlbumInfoBean myCreatedAblumEntity = new AlbumInfoBean();
        myCreatedAblumEntity.setImages(mImgData);
        ImgAndTagWallManager.getInstance(UpLoadMainActivity.this).initTagsWallForItem0(mTags);
        myCreatedAblumEntity.setTags(mTags);
        myCreatedAblumEntity.setUser_id(App.user_Id);
        myCreatedAblumEntity.setAlbum_desc(mAlbumDes);
        myCreatedAblumEntity.setCreatetime(response.getData().getCreatetime());
        myCreatedAblumEntity.setAlbum_id(response.getData().getAlbum_id());
        //此处在preference中获取昵称和头像地址赋值给entity
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String url = defaultSharedPreferences.getString(ConstantValues.KEY_SP_AVATAR_URL, "");
        String nickname = defaultSharedPreferences.getString(ConstantValues.KEY_SP_NICKNAME, "");
        if (!TextUtils.isEmpty(url)) {
            AvatarUrlBean avatarUrlEntity = new AvatarUrlBean();
            avatarUrlEntity.setS100(url);
            avatarUrlEntity.setS150(url);
            avatarUrlEntity.setS50(url);
            myCreatedAblumEntity.setAvatar_url(avatarUrlEntity);
        }
        myCreatedAblumEntity.setNickname(nickname);

        App app = (App) getApplication();
        app.setLastestUploadAlbum(myCreatedAblumEntity);
        Intent intent = new Intent(ACTION_UPDATA_LAST_ABLUM);
        sendBroadcast(intent);
    }

    private void getImgsMd5() throws IOException {
        for (int i = 0; i < mImgData.size(); i++) {
            DemoImgBean imgBean = mImgData.get(i);
            String filePath = ImageDownloader.Scheme.FILE.crop(imgBean.getUrl());
            File file = new File(filePath);
            String md5 = SecurityUtil.md5File(file);
            imgBean.setMd5(md5);
        }
    }

    @Override
    public void onBackPressed() {
        View v = LayoutInflater.from(this).inflate(R.layout.exitupload_dialog_layout, null);
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
                        UpLoadMainActivity.super.onBackPressed();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
        int id = v.getId();
        switch (id) {
            case R.id.tv_confirm://发布
                if (!HttpClientManager.checkNetWorkStatus(UpLoadMainActivity.this)) {
                    ToastManager.showShort(this, "网络连接不可用!");
                    return;
                }
                if (mImgData == null || mImgData.size() == 0) {
                    ToastManager.showShort(this, "您还没有选择图片");
                    return;
                }
                mProgressDialog = ProgressDialog.show(this, null, "图片载入中...");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEtUploadmainEdit.getWindowToken(), 0);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getImgsMd5();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    releaseImgs();
                                }
                            });
                        } catch (IOException e) {
                            Log.d("wangzixu", "releaseImgs 传图时报了Io异常");
                            mProgressDialog.dismiss();
                            //ToastManager.showShort(UpLoadMainActivity.this, "传图时报了Io异常");
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.tv_cancel: //取消
                InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm1.hideSoftInputFromWindow(mEtUploadmainEdit.getWindowToken(), 0)) {
                    //nothing
                } else {
                    onBackPressed();
                }
                break;
            case R.id.rl_uploadmian_addtag:
                Intent iTag = new Intent(UpLoadMainActivity.this, UploadTagsActivity.class);
                startActivityForResult(iTag, START_CODE_TAG);
                overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            case R.id.rl_uploadmian_location:
                Intent iLocation = new Intent(UpLoadMainActivity.this, UploadLocationActivity.class);
                startActivity(iLocation);
                overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            case R.id.tv_uploadmain_goonadd: //继续添加
                Intent intent = new Intent(UpLoadMainActivity.this, UpLoadGalleryActivity.class);
                intent.putExtra("addmore", true);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                //onBackPressed();
                break;
            case R.id.iv_for_bigimg: //点击进入大图
                ImageView imageView = (ImageView) v;
                ImageUtil.changeLight(imageView, true);
                Intent iBigImg = new Intent(this, BigImgBrowseActivity.class);
                int pos = (int) v.getTag(R.string.TAG_KEY_POSITION);
                iBigImg.putExtra(BigImgBrowseActivity.EXTRA_USED, 3);
                iBigImg.putExtra(BigImgBrowseActivity.EXTRA_INIT_POSITION, pos);
                //intent.putParcelableArrayListExtra(BigImgBrowseActivity.EXTRA_IMG_DATA, mData);
                //通过App来传递数据
//                App app = (App) getApplication();
//                app.setBigImgData(mImgDirs.get(mCrrentSelectFolder));
//                app.setCheckedImgs(mCheckedImgs);
                startActivity(iBigImg);
                overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_retain);
                break;
            default:
                break;
        }
    }

    private ArrayList<DemoTagBean> mTags = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("wangzixu", "onActivityResult requestCode, resultCode = " + requestCode + ", " + resultCode);
        Log.d("wangzixu", "onActivityResult data = " + data);
        if (resultCode == RESULT_OK) {
            if (requestCode == START_CODE_TAG) { //添加标签页回来，添加标签
                App app = (App) getApplication();
                mTags = app.getTagsTemp();
                if (mTags == null || mTags.size() == 0) {
                    if (mRlUploadmianAddtagContainer.indexOfChild(mTvAddTag) == -1) {
                        mRlUploadmianAddtagContainer.removeAllViewsInLayout();
                        mRlUploadmianAddtagContainer.addView(mTvAddTag);
                    }
                    return;
                }
                ImgAndTagWallManager.getInstance(this).initTagsWall(mTags, mTagTextPadding * 2, mTagTextPadding * 2
                        , mTagRlWidth, 0, 0, mTagTextSizePx, mTagTextSizePx, mTagDrawablePading, mTagDrawableWidth, 0, 0);
                mRlUploadmianAddtagContainer.removeAllViewsInLayout();
                for (int i = 0; i < mTags.size(); i++) {
                    DemoTagBean bean = mTags.get(i);
                    String tag = bean.getName();
                    TextView tv = new TextView(this);
                    tv.setIncludeFontPadding(false);
                    tv.setTypeface(Typeface.DEFAULT);
                    tv.setText(tag);
                    tv.setTextSize(mTagTextSize);
                    tv.setTextColor(getResources().getColor(R.color.hei_60));
                    tv.setSingleLine();
                    tv.setEllipsize(TextUtils.TruncateAt.END);
                    tv.setPadding(mTagTextPadding, mTagTextPadding, mTagTextPadding, mTagTextPadding);
                    tv.setCompoundDrawablePadding(mTagDrawablePading);
                    tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_upload_tag, 0, 0, 0);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                            , ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.leftMargin = bean.getMarginLeft();
                    lp.topMargin = bean.getMarginTop();
                    tv.setLayoutParams(lp);
                    mRlUploadmianAddtagContainer.addView(tv);
                }
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        mTvTextcount.setText(s.length() + "/150");
//        mTvTextcount.setText(getResources().getString(R.string.upload_et_count, s.length()));
    }
}
