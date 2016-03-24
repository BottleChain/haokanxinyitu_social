package com.haokan.xinyitu.upload;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;
import com.haokan.xinyitu.bigimgbrowse.BigImgBrowseActivity;
import com.haokan.xinyitu.main.DemoImgBean;
import com.haokan.xinyitu.util.CommonUtil;
import com.haokan.xinyitu.util.ConstantValues;
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.ImageLoaderManager;
import com.haokan.xinyitu.util.ImageUtil;
import com.haokan.xinyitu.util.ImgAndTagWallManager;
import com.haokan.xinyitu.util.JsonUtil;
import com.haokan.xinyitu.util.SecurityUtil;
import com.haokan.xinyitu.util.ToastManager;
import com.haokan.xinyitu.util.UrlsUtil;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class UpLoadMainActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
    private RelativeLayout mRlHeader;
    private TextView mTvCancel;
    private TextView mTvConfirm;
    private EditText mEtUploadmainEdit;
    private TextView mTvTextcount;
    private View mDevider1;
    private RelativeLayout mRlUploadmianAddtag;
    private View mDevider2;
    private TextView mTvUploadmainGoonadd;
    private RelativeLayout mRlUploadmain;
    private RelativeLayout mRlUploadmainLocation;
    private ArrayList<DemoImgBean> mImgData;
    private Handler mHandler = new Handler();

    private int mLastLoadImgCount;
    private SharedPreferences mDefaultSharedPreferences;


    private void assignViews() {
        mRlHeader = (RelativeLayout) findViewById(R.id.rl_header);
        mTvCancel = (TextView) findViewById(R.id.tv_cancel);
        mTvConfirm = (TextView) findViewById(R.id.tv_confirm);
        mEtUploadmainEdit = (EditText) findViewById(R.id.et_upload_edit);
        mTvTextcount = (TextView) findViewById(R.id.tv_textcount);
        mDevider1 = findViewById(R.id.devider_1);
        mRlUploadmianAddtag = (RelativeLayout) findViewById(R.id.rl_uploadmian_addtag);
        mDevider2 = findViewById(R.id.devider_2);
        mTvUploadmainGoonadd = (TextView) findViewById(R.id.tv_uploadmain_goonadd);
        mRlUploadmain = (RelativeLayout)findViewById(R.id.rl_uploadmain);
        mRlUploadmainLocation = (RelativeLayout)findViewById(R.id.rl_uploadmian_location);

        mTvCancel.setOnClickListener(this);
        mTvConfirm.setOnClickListener(this);
        mEtUploadmainEdit.addTextChangedListener(this);
        mRlUploadmianAddtag.setOnClickListener(this);
        mTvUploadmainGoonadd.setOnClickListener(this);
        mRlUploadmainLocation.setOnClickListener(this);
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
                        //Log.d("wangzixu", "initItem0 ----pos, imgCount = " + position + ", " + imgs.size());
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
                            ImageLoaderManager.getInstance().asyncLoadImage(img, bean.getPath()
                                    , bean.getItemWidth(), bean.getItemHeigh());
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadmain_activity_layout);
        assignViews();

        loadData();
    }


    private boolean mHasFillTagIds;
    private ArrayList<String> mTagIds;
    /**
     * 发布图片，包括3步，1-检测图片是否支持秒传，2-支持秒传则走秒传接口，否则走传图片接口，3-发布组图
     * ps：步骤1和2是每张图进行一次，把服务器返回的每张图片的码集合起来一起再发布组图
     */
    private void releaseImgs() {
        if (mDefaultSharedPreferences == null) {
            mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        }
        final String sessionId = mDefaultSharedPreferences.getString(ConstantValues.KEY_SP_SESSIONID, "");
        if (TextUtils.isEmpty(sessionId)) {
            //ToastManager.showShort(this, "sessionId null, return!");
            Log.d("wangzixu", "releaseImgs sessionId is null, return");
            return;
        }
        Log.d("wangzixu", "releaseImgs sessionId = " + sessionId);

        //获取标签
        final ArrayList<String> tags = new ArrayList<>();
        tags.add("测试标签1");
        tags.add("测试标签2");
        tags.add("测试标签3");
        tags.add("测试标签4");

        //1,检测图片是否支持秒传
        String scecondUploadCheckUrl = UrlsUtil.getSecondUploadCheckUrl(sessionId);
        Log.d("wangzixu", "releaseImgs scecondUploadCheckUrl = " + scecondUploadCheckUrl);
        for (int i = 0; i < mImgData.size(); i++) {
            final int finalI = i;
            final String md5 = mImgData.get(finalI).getMd5();
            HttpClientManager.getInstance(UpLoadMainActivity.this).scondUploadCheck(scecondUploadCheckUrl, md5
                    , new BaseJsonHttpResponseHandler<ResponseBeanSecondUploadCheck>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanSecondUploadCheck response) {
                    if (response != null && response.getErr_code() == 0) { //成功支持秒传
                        Log.d("wangzixu", "releaseImgs scecondUploadCheck 支持 = " + finalI);
                        secondUpload(finalI, sessionId, tags, response.getData().getUnique_id(), response.getData().getFile_name());
                    } else { //不支持秒传
                        Log.d("wangzixu", "releaseImgs scecondUploadCheck 不支持 = " + finalI);
                        uploadImgFile(finalI, sessionId, tags, mImgData.get(finalI));
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanSecondUploadCheck errorResponse) {
                    //访问服务器失败
                    Log.d("wangzixu", "releaseImgs scecondUploadCheck onFailure i = " + finalI );
                }

                @Override
                protected ResponseBeanSecondUploadCheck parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    Log.d("wangzixu", "releaseImgs scecondUploadCheck rawJsonData = " + rawJsonData );
                    return JsonUtil.fromJson(rawJsonData, ResponseBeanSecondUploadCheck.class);
                }
            });
        }
    }

    private void secondUpload(final int index, String sessonId, ArrayList<String> tags, String unique_id, String fileName) {
        String url = UrlsUtil.getSecondUploadUrl(sessonId);
        Log.d("wangzixu", "releaseImgs secondUpload url = " + url);

        HttpClientManager.getInstance(UpLoadMainActivity.this).secondUpLoadFile(url, unique_id, null, fileName, tags
                , new BaseJsonHttpResponseHandler<ResponseBeanImgUpload>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanImgUpload response) {
                if (response.getErr_code() == 0) {
                    Log.d("wangzixu", "releaseImgs secondUpload onSuccess");
                    mImgData.get(index).setImage_id(response.getData().getImage().getImage_id());
                    if (!mHasFillTagIds) {
                        fillTagIds(response.getData().getTags());
                    }
                } else { //上传失败 赋值一个-100
                    Log.d("wangzixu", "releaseImgs secondUpload onSuccess 返回了1，失败原因 = " + response.getErr_msg());
                    mImgData.get(index).setImage_id("true");
                }
                tryCreateAblum();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanImgUpload errorResponse) {
                //上传失败
                Log.d("wangzixu", "releaseImgs secondUpload onFailure");
                mImgData.get(index).setImage_id("true");
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
    private void uploadImgFile(final int index, String sessonId, ArrayList<String> tags, DemoImgBean bean) {
        String upfileUrl = UrlsUtil.getImageUploadUrl(sessonId);
        Log.d("wangzixu", "releaseImgs uploadImgFile upfileUrl = " + upfileUrl);
        String filePath = ImageDownloader.Scheme.FILE.crop(bean.getPath());
        File file = new File(filePath);

        HttpClientManager.getInstance(UpLoadMainActivity.this).upLoadImgFile(upfileUrl, file, null, tags
                , new BaseJsonHttpResponseHandler<ResponseBeanImgUpload>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanImgUpload response) {
                if (response.getErr_code() == 0) {
                    Log.d("wangzixu", "releaseImgs uploadImgFile onSuccess");
                    mImgData.get(index).setImage_id(response.getData().getImage().getImage_id());
                    if (!mHasFillTagIds) {
                        fillTagIds(response.getData().getTags());
                    }
                    tryCreateAblum();
                } else { //上传失败 赋值一个-100
                    Log.d("wangzixu", "releaseImgs uploadImgFile onSuccess 返回了1，失败原因 = " + response.getErr_msg());
                    mImgData.get(index).setImage_id("true");
                    tryCreateAblum();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanImgUpload errorResponse) {
                //上传失败
                Log.d("wangzixu", "releaseImgs uploadImgFile onFailure");
                mImgData.get(index).setImage_id("true");
                tryCreateAblum();
            }

            @Override
            protected ResponseBeanImgUpload parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                Log.d("wangzixu", "releaseImgs uploadImgFile rawJsonData = " + rawJsonData);
                return JsonUtil.fromJson(rawJsonData, ResponseBeanImgUpload.class);
            }
        });
    }

    private void fillTagIds(List<ResponseBeanImgUpload.DataBean.RBIUTagsBean> tags) {
        mHasFillTagIds = true;
        mTagIds = new ArrayList<>();
        for (int i = 0; i < tags.size(); i++) {
            mTagIds.add(tags.get(i).getId());
        }
    }

    private void tryCreateAblum() {
        boolean isSuccess = true;
        for (DemoImgBean bean : mImgData) {
            if (TextUtils.isEmpty(bean.getImage_id())) {
                return;
            }
            if (Boolean.valueOf(bean.getImage_id())) { //说明此张图上传失败了
                isSuccess = false;
            }
        }
        //每个图片的imageid都不为null了，说明全部传完了-----
        if (isSuccess) { //全部上传成功了，发组图

        } else { //没有上传成功，需要把失败信息存到某一个地方，在个人中心页中能展示出来这些失败的信息

        }


    }

    private void getImgsMd5() throws IOException {
        for (int i = 0; i < mImgData.size(); i++) {
            DemoImgBean imgBean = mImgData.get(i);
            String filePath = ImageDownloader.Scheme.FILE.crop(imgBean.getPath());
            File file = new File(filePath);
            String md5 = SecurityUtil.md5File(file);
            imgBean.setMd5(md5);
        }
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
                            //ToastManager.showShort(UpLoadMainActivity.this, "传图时报了Io异常");
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.tv_cancel: //取消
                onBackPressed();
                break;
            case R.id.rl_uploadmian_addtag:
                Intent iTag = new Intent(UpLoadMainActivity.this, UploadTagsActivity.class);
                startActivity(iTag);
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
