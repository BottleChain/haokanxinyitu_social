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
    private ArrayList<DemoImgBean> mImgData;
    private Handler mHandler = new Handler();

    private int mLastLoadImgCount;
    private SharedPreferences mDefaultSharedPreferences;


    private void assignViews() {
        mRlHeader = (RelativeLayout) findViewById(R.id.rl_header);
        mTvCancel = (TextView) findViewById(R.id.tv_cancel);
        mTvCancel.setOnClickListener(this);
        mTvConfirm = (TextView) findViewById(R.id.tv_confirm);
        mTvConfirm.setOnClickListener(this);
        mEtUploadmainEdit = (EditText) findViewById(R.id.et_uploadmain_edit);
        mEtUploadmainEdit.addTextChangedListener(this);
        mTvTextcount = (TextView) findViewById(R.id.tv_textcount);
        mDevider1 = findViewById(R.id.devider_1);
        mRlUploadmianAddtag = (RelativeLayout) findViewById(R.id.rl_uploadmian_addtag);
        mRlUploadmianAddtag.setOnClickListener(this);
        mDevider2 = findViewById(R.id.devider_2);
        mTvUploadmainGoonadd = (TextView) findViewById(R.id.tv_uploadmain_goonadd);
        mTvUploadmainGoonadd.setOnClickListener(this);
        mRlUploadmain = (RelativeLayout)findViewById(R.id.rl_uploadmain);
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


    /**
     * 发布图片，包括3步，1-检测图片是否支持秒传，2-支持秒传则走秒传接口，否则走传图片接口，3-发布组图
     * ps：步骤1和2是每张图进行一次，把服务器返回的每张图片的码集合起来一起再发布组图
     */
    private void releaseImgs() {
        if (mDefaultSharedPreferences == null) {
            mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        }
        String sessionId = mDefaultSharedPreferences.getString(ConstantValues.KEY_SP_SESSIONID, "");
        if (TextUtils.isEmpty(sessionId)) {
            //ToastManager.showShort(this, "sessionId null, return!");
            Log.d("wangzixu", "releaseImgs sessionId is null, return");
            return;
        } else {
            Log.d("wangzixu", "releaseImgs sessionId = " + sessionId);
        }

        // 1,检测图片是否支持秒传
        String scecondUploadCheckUrl = UrlsUtil.getSecondUploadCheckUrl(sessionId);
        Log.d("wangzixu", "releaseImgs scecondUploadCheckUrl = " + scecondUploadCheckUrl);
        for (int i = 0; i < mImgData.size(); i++) {
            final int finalI = i;
            HttpClientManager.getInstance(UpLoadMainActivity.this).scondUploadCheck(scecondUploadCheckUrl, mImgData.get(i).getMd5(), new BaseJsonHttpResponseHandler<ResponseBeanSecondUploadCheck>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanSecondUploadCheck response) {
                    if (response != null && response.getErr_code() == 0) { //成功支持秒传
                        Log.d("wangzixu", "releaseImgs scecondUploadCheck 支持 = " + finalI);
                    } else { //不支持秒传
                        Log.d("wangzixu", "releaseImgs scecondUploadCheck 不支持 = " + finalI);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanSecondUploadCheck errorResponse) {
                    //访问服务器失败
                    Log.d("wangzixu", "releaseImgs scecondUploadCheck onFailure i = " + finalI);
                }

                @Override
                protected ResponseBeanSecondUploadCheck parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    return JsonUtil.fromJson(rawJsonData, ResponseBeanSecondUploadCheck.class);
                }
            });
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
