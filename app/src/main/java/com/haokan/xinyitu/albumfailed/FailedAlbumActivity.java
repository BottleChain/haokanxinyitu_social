package com.haokan.xinyitu.albumfailed;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;
import com.haokan.xinyitu.database.MyDatabaseHelper;
import com.haokan.xinyitu.main.DemoImgBean;
import com.haokan.xinyitu.main.DemoTagBean;
import com.haokan.xinyitu.main.discovery.AlbumInfoBean;
import com.haokan.xinyitu.upload.ResponseBeanCreateAblum;
import com.haokan.xinyitu.upload.ResponseBeanImgUpload;
import com.haokan.xinyitu.upload.ResponseBeanSecondUploadCheck;
import com.haokan.xinyitu.util.CommonUtil;
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.ImgAndTagWallManager;
import com.haokan.xinyitu.util.JsonUtil;
import com.haokan.xinyitu.util.ToastManager;
import com.haokan.xinyitu.util.UrlsUtil;
import com.j256.ormlite.dao.Dao;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by wangzixu on 2016/4/25.
 */
public class FailedAlbumActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private RelativeLayout mRlHeader;
    private ImageButton mIbBack;
    private TextView mTvTitle;
    private ListView mLvListView;
    private View mLoadLayout;
    private View mTvNoItem;
    private List<AlbumInfoBean> mAlbumInfos = new ArrayList<>();
    Handler mHandler = new Handler();
    private PopupWindow mPopupWindow;
    private View mMorePopBg;
    private View mPopContent;
    private FailedAlbumAdapter mAdapter;
    private List<FailedAlbumInfoBean> mFailedAlbumInfoList;
    private List<DemoImgBean> mReUploadImages;
    private String mReUpLoadDesc;
    private Dialog mProgressDialog;
    public static final String ACTION_RE_CREATE_ALBUM_SUCCESS = "com.haokan.xinyitu.recreate.album.success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.failedalbum_activity_layout);
        assignViews();
        loadData();
    }

    @Override
    public void onBackPressed() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            disMissMorePop(null);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
        }
    }

    private void loadData() {
        mLoadLayout.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Dao dao = MyDatabaseHelper.getInstance(FailedAlbumActivity.this).getDaoQuickly(FailedAlbumInfoBean.class);
                    mFailedAlbumInfoList = dao.queryForAll();
                    for (int i = 0; i < mFailedAlbumInfoList.size(); i++) {
                        FailedAlbumInfoBean failedAlbumInfoBean = mFailedAlbumInfoList.get(i);
                        AlbumInfoBean beanAlbumInfo = new AlbumInfoBean();
                        beanAlbumInfo.setAlbum_desc(failedAlbumInfoBean.getDesc());
                        beanAlbumInfo.setUser_id(App.user_Id);
                        String tags = failedAlbumInfoBean.getTags();
                        if (!TextUtils.isEmpty(tags)) {
                            List<DemoTagBean> tagBeanList = JsonUtil.fromJson(tags, new TypeToken<List<DemoTagBean>>(){}.getType());
                            ImgAndTagWallManager.getInstance(FailedAlbumActivity.this).initTagsWallForItem0(tagBeanList);
                            beanAlbumInfo.setTags(tagBeanList);
                        }

                        String imgs = failedAlbumInfoBean.getImg_urls();
                        if (!TextUtils.isEmpty(imgs)) {
                            List<DemoImgBean> imgBeanList = JsonUtil.fromJson(imgs, new TypeToken<List<DemoImgBean>>(){}.getType());
                            beanAlbumInfo.setImages(imgBeanList);
                            mAlbumInfos.add(beanAlbumInfo);

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mLoadLayout.setVisibility(View.GONE);
                                    mAdapter = new FailedAlbumAdapter(FailedAlbumActivity.this, mAlbumInfos, null);
                                    mLvListView.setAdapter(mAdapter);
                                }
                            });
                        }

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void assignViews() {
        mRlHeader = (RelativeLayout) findViewById(R.id.rl_header);
        mIbBack = (ImageButton) findViewById(R.id.ib_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mLvListView = (ListView) findViewById(R.id.lv_listview);
        mLoadLayout = findViewById(R.id.loading_layout);
        mTvNoItem = findViewById(R.id.tv_no_item);

        mLvListView.setOnItemClickListener(this);
        mIbBack.setOnClickListener(this);
    }

    private void initMorePopupWindow() {
        View v = LayoutInflater.from(this).inflate(R.layout.failedalbum_popupwindow, null);
        mPopupWindow = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setFocusable(false);
        mPopupWindow.setAnimationStyle(0);

        mMorePopBg = v.findViewById(R.id.pop_shadow);
        mMorePopBg.setOnClickListener(this);

        mPopContent = v.findViewById(R.id.ll_pop_content);
        mPopContent.findViewById(R.id.tv_reupload).setOnClickListener(this);
        mPopContent.findViewById(R.id.tv_popup_delete).setOnClickListener(this);
        mPopContent.findViewById(R.id.tv_pop_cancel).setOnClickListener(this);
    }

    private void disMissMorePop(final Runnable endRun) {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
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
                            mPopupWindow.dismiss();
                            if (endRun != null) {
                                endRun.run();
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
                    mPopContent.startAnimation(animation);
                }
            });
        }
    }

    private int mCurrentPopPostion;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("wangzixu", "FailedAlbumActivity onItemClik pos = " + position);
        if (CommonUtil.isQuickClick()) {
            return;
        }
        if (mPopupWindow == null) {
            initMorePopupWindow();
        }

        if (mPopupWindow.isShowing()) {
            return;
        }
        mCurrentPopPostion = position;
        mPopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        mMorePopBg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.popupwindow_bg_in));
        mPopContent.startAnimation(AnimationUtils.loadAnimation(this, R.anim.popupwindow_bottom_in));
    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.tv_pop_cancel:
            case R.id.pop_shadow:
                disMissMorePop(null);
                break;
            case R.id.ib_back:
                onBackPressed();
                break;
            case R.id.tv_popup_delete: //删除
                mAlbumInfos.remove(mCurrentPopPostion);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Dao dao = MyDatabaseHelper.getInstance(FailedAlbumActivity.this).getDaoQuickly(FailedAlbumInfoBean.class);
                            dao.deleteById(mFailedAlbumInfoList.get(mCurrentPopPostion).get_id());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                if (mAlbumInfos.size() == 0) {
                    mLvListView.setVisibility(View.GONE);
                    mTvNoItem.setVisibility(View.VISIBLE);
                } else {
                    mAdapter.notifyDataSetChanged();
                }
                disMissMorePop(null);
                break;
            case R.id.tv_reupload: //重新上传
                if (mProgressDialog == null) {
                    mProgressDialog = new Dialog(FailedAlbumActivity.this, R.style.loading_progress);
                    mProgressDialog.setContentView(R.layout.loading_layout_progressdialog_titleloading);
                }
                mProgressDialog.show();
                disMissMorePop(new Runnable() {
                    @Override
                    public void run() {
                        releaseImgs(mCurrentPopPostion);
                    }
                });
                break;
            default:
                break;
        }
    }

    /**
     * 发布图片，包括3步，1-检测图片是否支持秒传，2-支持秒传则走秒传接口，否则走传图片接口，3-发布组图
     * ps：步骤1和2是每张图进行一次，把服务器返回的每张图片的码集合起来一起再发布组图
     */
    private void releaseImgs(int pos) {
        AlbumInfoBean albumInfoBean = mAlbumInfos.get(pos);
        mReUploadImages = albumInfoBean.getImages();
        mReUpLoadDesc = albumInfoBean.getAlbum_desc();
        final List<DemoTagBean> tags = albumInfoBean.getTags();
        for (int i = 0; i < mReUploadImages.size(); i++) {
            mReUploadImages.get(i).setId("");
        }

        //1,检测图片是否支持秒传
        String scecondUploadCheckUrl = UrlsUtil.getSecondUploadCheckUrl(App.sessionId);
        Log.d("wangzixu", "releaseImgs scecondUploadCheckUrl = " + scecondUploadCheckUrl);
        for (int i = 0; i < mReUploadImages.size(); i++) {
            final DemoImgBean imgBean = mReUploadImages.get(i);
            final String md5 = imgBean.getMd5();
            HttpClientManager.getInstance(FailedAlbumActivity.this).scondUploadCheck(scecondUploadCheckUrl, md5
                , new BaseJsonHttpResponseHandler<ResponseBeanSecondUploadCheck>() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanSecondUploadCheck response) {
                        if (response != null && response.getErr_code() == 0) { //成功支持秒传
                            Log.d("wangzixu", "releaseImgs scecondUploadCheck 支持");
                            secondUpload(imgBean, App.sessionId, tags, response.getData().getUnique_id(), response.getData().getFile_name());
                        } else { //不支持秒传
                            Log.d("wangzixu", "releaseImgs scecondUploadCheck 不支持");
                            uploadImgFile(imgBean, App.sessionId, tags);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanSecondUploadCheck errorResponse) {
                        //访问服务器失败
                        Log.d("wangzixu", "releaseImgs scecondUploadCheck onFailure");
                    }

                    @Override
                    protected ResponseBeanSecondUploadCheck parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                        Log.d("wangzixu", "releaseImgs scecondUploadCheck rawJsonData = " + rawJsonData);
                        return JsonUtil.fromJson(rawJsonData, ResponseBeanSecondUploadCheck.class);
                    }
                });
        }
    }

    private void secondUpload(final DemoImgBean imgBean, String sessonId, List<DemoTagBean> tags, String unique_id, String fileName) {
        String url = UrlsUtil.getSecondUploadUrl(sessonId);
        Log.d("wangzixu", "releaseImgs secondUpload url = " + url);

        HttpClientManager.getInstance(FailedAlbumActivity.this).secondUpLoadFile(url, unique_id, null, fileName, tags
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
    private void uploadImgFile(final DemoImgBean imgBean, String sessonId, List<DemoTagBean> tags) {
        String upfileUrl = UrlsUtil.getImageUploadUrl(sessonId);
        Log.d("wangzixu", "releaseImgs uploadImgFile upfileUrl = " + upfileUrl);
        String filePath = ImageDownloader.Scheme.FILE.crop(imgBean.getUrl());
        File file = new File(filePath);
        Log.d("wangzixu", "releaseImgs uploadImgFile filePath = " + filePath);
        HttpClientManager.getInstance(FailedAlbumActivity.this).upLoadImgFile(upfileUrl, file, null, tags
                , new BaseJsonHttpResponseHandler<ResponseBeanImgUpload>() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanImgUpload response) {
                        if (response.getErr_code() == 0) {
                            Log.d("wangzixu", "releaseImgs uploadImgFile onSuccess");
                            imgBean.setId(response.getData().getImage().getImage_id());
                            if (!mHasFillTagIds) {
                                fillTagIds(response.getData().getTags());
                            }
                        } else { //上传失败 赋值一个-100
                            Log.d("wangzixu", "releaseImgs uploadImgFile onSuccess 返回了1，失败原因 = " + response.getErr_msg());
                            imgBean.setId("true");
                        }
                        tryCreateAblum();
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

    private boolean mHasFillTagIds;
    private ArrayList<String> mTagIds;
    private void fillTagIds(List<DemoTagBean> tags) {
        mHasFillTagIds = true;
        mTagIds = new ArrayList<>();
        for (int i = 0; i < tags.size(); i++) {
            mTagIds.add(tags.get(i).getId());
        }
    }

    private void tryCreateAblum() {
        boolean isSuccess = true;
        for (DemoImgBean bean : mReUploadImages) {
            if (TextUtils.isEmpty(bean.getId())) { //说明还有图没有上传成功
                return;
            }
            if (Boolean.valueOf(bean.getId())) { //说明此张图上传失败了
                isSuccess = false;
            }
        }
        Log.d("wangzixu", "releaseImgs tryCreateAblum 所有组图已经上传完成，标题, 描述 = " + "组图标题" + ", " + mReUpLoadDesc);
        //每个图片的imageid都不为null了，说明全部传完了-----
        if (isSuccess) { //全部上传成功了，发组图
            String url = UrlsUtil.getCreateAblumUrl(App.sessionId);
            Log.d("wangzixu", "releaseImgs tryCreateAblum url = " + url);
            HttpClientManager.getInstance(FailedAlbumActivity.this).createAblum(url, "组图标题", mReUpLoadDesc
                , mReUploadImages, mTagIds, new BaseJsonHttpResponseHandler<ResponseBeanCreateAblum>() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanCreateAblum response) {
                        if (response.getErr_code() == 0) {
                            Log.d("wangzixu", "releaseImgs tryCreateAblum success , errorCode，id = "
                                    + response.getErr_code() + ", " + response.getData().getAlbum_id());
                            ToastManager.showShort(FailedAlbumActivity.this, "发布组图成功");
                            mAlbumInfos.remove(mCurrentPopPostion);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Dao dao = MyDatabaseHelper.getInstance(FailedAlbumActivity.this).getDaoQuickly(FailedAlbumInfoBean.class);
                                        dao.deleteById(mFailedAlbumInfoList.get(mCurrentPopPostion).get_id());
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                            if (mAlbumInfos.size() == 0) {
                                mLvListView.setVisibility(View.GONE);
                                mTvNoItem.setVisibility(View.VISIBLE);
                            } else {
                                mAdapter.notifyDataSetChanged();
                            }
                            //通知重新上传成功了
                            Intent intent = new Intent(ACTION_RE_CREATE_ALBUM_SUCCESS);
                            sendBroadcast(intent);
                        } else {
                            ToastManager.showShort(FailedAlbumActivity.this, "发布组图失败 = " + response.getErr_msg());
                        }
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanCreateAblum errorResponse) {
                        Log.d("wangzixu", "releaseImgs tryCreateAblum onFailure");
                        ToastManager.showShort(FailedAlbumActivity.this, "发布失败, onFail");
                        mProgressDialog.dismiss();
                    }

                    @Override
                    protected ResponseBeanCreateAblum parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                        Log.d("wangzixu", "releaseImgs tryCreateAblum rawJsonData = " + rawJsonData);
                        return JsonUtil.fromJson(rawJsonData, ResponseBeanCreateAblum.class);
                    }
                });
        } else {
            ToastManager.showShort(FailedAlbumActivity.this, "发布组图失败");
            mProgressDialog.dismiss();
        }
    }
}
