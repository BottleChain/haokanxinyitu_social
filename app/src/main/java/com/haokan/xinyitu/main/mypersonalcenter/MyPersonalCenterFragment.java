package com.haokan.xinyitu.main.mypersonalcenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseResponseBean;
import com.haokan.xinyitu.albumfailed.FailedAlbumInfoBean;
import com.haokan.xinyitu.database.MyDatabaseHelper;
import com.haokan.xinyitu.main.Base_PTR_LoadMore_Fragment;
import com.haokan.xinyitu.main.MainActivity;
import com.haokan.xinyitu.main.discovery.AlbumInfoBean;
import com.haokan.xinyitu.main.discovery.RequestBeanAlbumInfo;
import com.haokan.xinyitu.main.discovery.ResponseBeanAlbumInfo;
import com.haokan.xinyitu.notice.NoticeMainActivity;
import com.haokan.xinyitu.util.ConstantValues;
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.ImageLoaderManager;
import com.haokan.xinyitu.util.ImgAndTagWallManager;
import com.haokan.xinyitu.util.JsonUtil;
import com.haokan.xinyitu.util.UrlsUtil;
import com.j256.ormlite.dao.Dao;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MyPersonalCenterFragment extends Base_PTR_LoadMore_Fragment implements PullToRefreshBase.OnRefreshListener<ListView> {

    private List<ResponseBeanAlbumListPersonnal.DataEntity> mAlbumIdList;
    private ArrayList<AlbumInfoBean> mData = new ArrayList<>();
    private View mIbPersonSetting;
    private View mRlTopbar;
    private View mIbPersonNotice;
    private View mIvNoticePoint;
    private TextView mTvTitle;
    private TextView mTvHeaderTitle;
    private PersonnalcenterFragmentAdapter mAdapter;
    private Handler mHandler = new Handler();

    //处理上划显示标题用到的一些数据
    private int mTopBarBottom; //actionbar的底部位置
    private int mHeaderTitleTop;
    private int mHeaderTitleHeight;
    private View mHeader;
    private View mHeaderFailed;
    private View mRlMyGallery;
    private View mRlMyFollows;
    private View mRlFollowMe;
    private TextView mTvHeaderDesc;
    private TextView mTvAblumCount;
    private TextView mTvMyFollowsCount;
    private TextView mTvFollowMeCount;
    private View mNotLoginLayout;
    private ImageView mIvAvatar;
    private boolean mIsLogin;
    private View mTvChangePersonData;
    private String mOldAvatarUrl;
    private String mOldNickName;
    private String mOldDesc;
    private ResponseBeanMyUserInfo mUserInfoResponse;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mypersonalcenter_fragment_layout, container, false);
        mRlTopbar = view.findViewById(R.id.rl_header);
        mIbPersonSetting = view.findViewById(R.id.ib_person_setting);
        mIbPersonNotice = view.findViewById(R.id.ib_person_notice);
        mIvNoticePoint = view.findViewById(R.id.iv_notice_point);
        mTvTitle = (TextView) view.findViewById(R.id.tv_title);
        mNotLoginLayout = view.findViewById(R.id.no_login_layout);
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.ptrlv_1);
        mPullToRefreshListView.setOnRefreshListener(this);
        mListView = mPullToRefreshListView.getRefreshableView();
        mLoadingLayout = view.findViewById(R.id.loading_layout);
        mNetErrorLayout = view.findViewById(R.id.net_error_layout);
        mBtnNetError = mNetErrorLayout.findViewById(R.id.iv_net_error);

        mHeader = inflater.inflate(R.layout.mypersonnalcenter_header, container, false);
        mHeaderFailed = inflater.inflate(R.layout.mypersonnalcenter_header_albumfailed, container, false);
        AbsListView.LayoutParams lp1 = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT
                , AbsListView.LayoutParams.WRAP_CONTENT);
        mHeaderFailed.setLayoutParams(lp1);

        mTvHeaderTitle = (TextView) mHeader.findViewById(R.id.tv_name);
        mTvHeaderDesc = (TextView) mHeader.findViewById(R.id.tv_desc);
        mIvAvatar = (ImageView) mHeader.findViewById(R.id.iv_avatar);
        mTvChangePersonData = mHeader.findViewById(R.id.tv_change_personnaldata);

        mRlMyGallery = mHeader.findViewById(R.id.rl_mygallery);
        mTvAblumCount = (TextView) mHeader.findViewById(R.id.tv_mygallery_count);
        mRlMyFollows = mHeader.findViewById(R.id.rl_personalcenter_myfollow);
        mTvMyFollowsCount = (TextView) mHeader.findViewById(R.id.tv_myfollow_count);
        mRlFollowMe = mHeader.findViewById(R.id.rl_personalcenter_followme);
        mTvFollowMeCount = (TextView) mHeader.findViewById(R.id.tv_followme_count);

        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT
                , AbsListView.LayoutParams.WRAP_CONTENT);
        mHeader.setLayoutParams(lp);
        mListView.addHeaderView(mHeader);

        mRlMyGallery.setClickable(false);
        mRlMyGallery.setSelected(true);
        mIbPersonSetting.setOnClickListener((View.OnClickListener) getActivity());
        mRlMyFollows.setOnClickListener((View.OnClickListener) getActivity());
        mRlFollowMe.setOnClickListener((View.OnClickListener) getActivity());
        mTvChangePersonData.setOnClickListener((View.OnClickListener) getActivity());
        mHeaderFailed.findViewById(R.id.tv_failed_album).setOnClickListener((View.OnClickListener) getActivity());
        mIbPersonNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    MainActivity activity = (MainActivity) getActivity();
                    activity.resetNoticePoint();
                    Intent intent = new Intent(getActivity(), NoticeMainActivity.class);
                    startActivity(intent);
                    mIvNoticePoint.setVisibility(View.GONE);
                }
            }
        });
        mIvNoticePoint.setVisibility(((MainActivity)getActivity()).getNoticePointVisiblity() ? View.VISIBLE : View.GONE);
        mNotLoginLayout.findViewById(R.id.tv_not_login).setOnClickListener((View.OnClickListener) getActivity());
        mNotLoginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //nothing
            }
        });
        mRlTopbar.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mTopBarBottom = bottom;
                mRlTopbar.removeOnLayoutChangeListener(this);
            }
        });

        mTvHeaderTitle.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mHeaderTitleTop = top;
                mHeaderTitleHeight = bottom - top;
                mTvHeaderTitle.removeOnLayoutChangeListener(this);
            }
        });

        if (TextUtils.isEmpty(App.sessionId)) {
            mIsLogin = false;
            mNotLoginLayout.setVisibility(View.VISIBLE);
            mLoadingLayout.setVisibility(View.GONE);
        } else {
            mIsLogin = true;
            mLoadingLayout.setVisibility(View.VISIBLE);
            mNotLoginLayout.setVisibility(View.GONE);
        }
        return view;
    }

    public void reLoad() {
        //mPullToRefreshListView.setRefreshing(true);
        loadData(getActivity());
    }

//    @Override
//    public void loadData(Context context) {
//        if (TextUtils.isEmpty(App.sessionId)) {
//            mIsLogin = false;
//            mNotLoginLayout.setVisibility(View.VISIBLE);
//            mLoadingLayout.setVisibility(View.GONE);
//        } else {
//            mIsLogin = true;
//            mNotLoginLayout.setVisibility(View.GONE);
//            super.loadData(context);
//        }
//    }

    @Override
    protected boolean hasLogin() {
        return !TextUtils.isEmpty(App.sessionId);
    }

    public void setNoticeVisible(int visible) {
        if (mIvNoticePoint != null) {
            mIvNoticePoint.setVisibility(visible);
        }
    }

    private boolean hasFailedHeader = false;
    public void addAblumFailedHeader() {
        if (!hasFailedHeader) {
            hasFailedHeader = true;
            mListView.addHeaderView(mHeaderFailed);
        }
    }

    public void removeAblumFailedHeader() {
        hasFailedHeader = false;
        mListView.removeHeaderView(mHeaderFailed);
    }

    /**
     * 改变登录状态
     */
    public void updataLoginStatus(boolean isLogin) {
        Log.d("wangzixu", "updataLoginStatus isLogin = " + isLogin);
        if (isLogin && !mIsLogin) {
            mIsLogin = true;
            mNotLoginLayout.setVisibility(View.GONE);
            loadData(getActivity());
        } else if (!isLogin && mIsLogin) {
            mIsLogin = false;
            mNotLoginLayout.setVisibility(View.VISIBLE);
            mPullToRefreshListView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setExtraScrollListener() {
        mExtraScrollListener = new ExtraScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int titleTop = mTopBarBottom + mHeader.getTop() + mHeaderTitleTop;
                int delta = mTopBarBottom - titleTop;
                float f = Math.min(delta / (float) (mHeaderTitleHeight), 1.0f);
                if (f <= 0 && mTvTitle.getVisibility() != View.GONE) {
                    mTvTitle.setVisibility(View.GONE);
                } else {
                    if (mTvTitle.getVisibility() != View.VISIBLE) {
                        mTvTitle.setVisibility(View.VISIBLE);
                    }
                    mTvTitle.setAlpha(f);
                }
            }
        };
    }

//    @Override
//    protected String getLoadDataUrl() {
//        String url = UrlsUtil.getMyAlbumsUrl(App.sessionId);
////        String url = UrlsUtil.getLastestAblumUrl(App.sessionId);
//        Log.d("MyPersonalCenter", "loadData url = " + url);
//        return url;
//    }

    @Override
    protected String getLoadDataUrl() {
        String url = UrlsUtil.getMyinfoUrl(App.sessionId);
        Log.d("wangzixu", "MyPersonalCenterFragment getLoadDataUrl url = " + url);
        return url;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if (TextUtils.isEmpty(App.sessionId)) {
//            return;
//        }
//        if (mIsFirstLoad) { //除了第一次进来是通过load加载的头像等，其他再回此界面需要重新加载一下名称和描述头像等，因为有可能改变
//            mIsFirstLoad = false;
//        } else {
//            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//            String avatarUrl = defaultSharedPreferences.getString(ConstantValues.KEY_SP_AVATAR_URL, mOldAvatarUrl);
//            if (avatarUrl != null && !avatarUrl.equals(mOldAvatarUrl)) {
//                ImageLoaderManager.getInstance().asyncLoadCircleImage(mIvAvatar, avatarUrl
//                        , mIvAvatar.getWidth(), mIvAvatar.getHeight());
//            }
//
//            String nickName = defaultSharedPreferences.getString(ConstantValues.KEY_SP_NICKNAME, mOldNickName);
//            if (!nickName.equals(mOldNickName)) {
//                mTvTitle.setText(nickName);
//                mTvHeaderTitle.setText(nickName);
//            }
//
//            String desc = defaultSharedPreferences.getString(ConstantValues.KEY_SP_DESC, mOldDesc);
//            if (desc == null || !desc.equals(mOldDesc)) {
//                if (TextUtils.isEmpty(desc)) {
//                    mTvHeaderDesc.setVisibility(View.GONE);
//                } else {
//                    mTvHeaderDesc.setVisibility(View.VISIBLE);
//                    mTvHeaderDesc.setText(desc);
//                }
//            }
//        }
//    }

    @Override
    public void onStart() {
        super.onStart();
        if (TextUtils.isEmpty(App.sessionId)) {
            return;
        }
        try {
            Dao dao = MyDatabaseHelper.getInstance(getActivity()).getDaoQuickly(FailedAlbumInfoBean.class);
            List list = dao.queryForAll();
            if (list != null && list.size() > 0) { //说明至少有一个失败的
                addAblumFailedHeader();
            } else {
                removeAblumFailedHeader();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (mIsFirstLoad) { //除了第一次进来是通过load加载的头像等，其他再回此界面需要重新加载一下名称和描述头像等，因为有可能改变
            mIsFirstLoad = false;
        } else {
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String avatarUrl = defaultSharedPreferences.getString(ConstantValues.KEY_SP_AVATAR_URL, mOldAvatarUrl);
            if (avatarUrl != null && !avatarUrl.equals(mOldAvatarUrl)) {
                ImageLoaderManager.getInstance().asyncLoadCircleImage(mIvAvatar, avatarUrl
                        , mIvAvatar.getWidth(), mIvAvatar.getHeight());
            }

            String nickName = defaultSharedPreferences.getString(ConstantValues.KEY_SP_NICKNAME, mOldNickName);
            if (!nickName.equals(mOldNickName)) {
                mTvTitle.setText(nickName);
                mTvHeaderTitle.setText(nickName);
            }

            String desc = defaultSharedPreferences.getString(ConstantValues.KEY_SP_DESC, mOldDesc);
            if (desc == null || !desc.equals(mOldDesc)) {
                if (TextUtils.isEmpty(desc)) {
                    mTvHeaderDesc.setVisibility(View.GONE);
                } else {
                    mTvHeaderDesc.setVisibility(View.VISIBLE);
                    mTvHeaderDesc.setText(desc);
                }
            }
        }
    }

//    @Override
//    protected void loadDataSuccess(Context context, int statusCode, Header[] headers, String rawJsonResponse, BaseResponseBean response) {
//        ResponseBeanAlbumListPersonnal responseBeanAlbumList = (ResponseBeanAlbumListPersonnal) response;
//        mPullToRefreshListView.setVisibility(View.VISIBLE);
//        mAlbumIdList = null;
//        //mData.clear();
//        mCurrentPage = 0;
//        mAlbumIdList = responseBeanAlbumList.getData();
//        mHasMoreData = true;
//        mTvAblumCount.setText(String.valueOf(mAlbumIdList.size()));
//        loadAlbumInfoData(context, true);
//
//        loadMyInfo(context);
//    }

    @Override
    protected void loadDataSuccess(Context context, int statusCode, Header[] headers, String rawJsonResponse, BaseResponseBean baseResponseBean) {
        mUserInfoResponse = (ResponseBeanMyUserInfo) baseResponseBean;
        mPullToRefreshListView.setVisibility(View.VISIBLE);
        //mLoadingLayout.setVisibility(View.GONE);
        String avatarUrl;
        if (App.sDensity >= 3) {
            avatarUrl = mUserInfoResponse.getData().getAvatar_url().getS150();
        } else {
            avatarUrl = mUserInfoResponse.getData().getAvatar_url().getS100();
        }
        String nickname = mUserInfoResponse.getData().getNickname();
        mTvTitle.setText(nickname);
        mTvHeaderTitle.setText(nickname);
        mTvAblumCount.setText(mUserInfoResponse.getData().getAlbumnum());
        mTvMyFollowsCount.setText(App.sMyFollowsUser.size() + "");
        mTvFollowMeCount.setText(mUserInfoResponse.getData().getLikemenum());
        if (TextUtils.isEmpty(mUserInfoResponse.getData().getDescription())) {
            mTvHeaderDesc.setVisibility(View.GONE);
        } else {
            mTvHeaderDesc.setVisibility(View.VISIBLE);
            mTvHeaderDesc.setText(mUserInfoResponse.getData().getDescription());
        }
        //开始加载组图
        loadAblumIdList(context);
        //加载头像
        if (avatarUrl != null) {
            ImageLoaderManager.getInstance().asyncLoadCircleImage(mIvAvatar, avatarUrl
                    , mIvAvatar.getWidth(), mIvAvatar.getHeight());
        }

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = defaultSharedPreferences.edit();
        edit.putString(ConstantValues.KEY_SP_AVATAR_URL, avatarUrl);
        edit.putString(ConstantValues.KEY_SP_NICKNAME, nickname);
        edit.putString(ConstantValues.KEY_SP_DESC, mUserInfoResponse.getData().getDescription());
        edit.putString(ConstantValues.KEY_SP_PHONENUM, mUserInfoResponse.getData().getMobile());
        mOldAvatarUrl = avatarUrl;
        mOldNickName = nickname;
        mOldDesc = mUserInfoResponse.getData().getDescription();
        edit.apply();
    }

    private void hasNoAblum(Context context) {
        if (context == null) {
            return;
        }
        mAlbumIdList = null;
        mCurrentPage = 0;
        mHasMoreData = false;
        if (mAdapter == null || mAdapter.getData() != null) {
            mAdapter = new PersonnalcenterFragmentAdapter(context, null, (View.OnClickListener)getActivity(), true);
            mListView.setAdapter(mAdapter);
        }
        mListView.setBackgroundColor(getActivity().getResources().getColor(R.color.main_color_actionbar_item01));
        mLoadingLayout.setVisibility(View.GONE);
    }

    private void loadAblumIdList(final Context context) {
        //获取个人信息存下来
        String url = UrlsUtil.getMyAlbumsUrl(App.sessionId);
        Log.d("wangzixu", "loadAblumIdList url = " + url);

        HttpClientManager.getInstance(context).getData(url, new BaseJsonHttpResponseHandler<ResponseBeanAlbumListPersonnal>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanAlbumListPersonnal response) {
                if (response.getErr_code() == 0) {
                    mAlbumIdList = null;
                    mAdapter = null;
                    mCurrentPage = 0;
                    mAlbumIdList = response.getData();
                    mHasMoreData = true;
                    if (mHasFootView) {
                        removeFootView();
                    }
                    //mTvAblumCount.setText(String.valueOf(mAlbumIdList.size()));
                    Log.d("wangzixu", "loadAblumIdList url success ");
                    if (mAlbumIdList == null || mAlbumIdList.size() == 0) {
                        hasNoAblum(context);
                    } else {
                        loadAlbumInfoData(context, true);
                    }
                } else {//没有发布过组图 返回失败
                    hasNoAblum(context);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanAlbumListPersonnal errorResponse) {
                hasNoAblum(context);
            }

            @Override
            protected ResponseBeanAlbumListPersonnal parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                Log.d("wangzixu", "loadAblumIdList rawJsonData = " + rawJsonData);
                return JsonUtil.fromJson(rawJsonData, ResponseBeanAlbumListPersonnal.class);
            }
        });
    }

    public void notifyFragmentsFollowChanged() {
        if (mTvMyFollowsCount != null) {
            mTvMyFollowsCount.setText("" + App.sMyFollowsUser.size());
        }
    }

    public void notifyLikeStatusChanged(final String ablumId, final int newStatus) {
        Log.d("wangzixu", "notifyLikeStatusChanged isVisible = ---" + isVisible());
        if (!isVisible() || !isResumed()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (mIsDestory) {
                        return;
                    }
                    boolean hasChange = false;
                    for (int i = 0; i < mData.size(); i++) {
                        AlbumInfoBean entity = mData.get(i);
                        if (ablumId.equals(entity.getAlbum_id())) {
                            hasChange = true;
                            entity.setIs_liked(newStatus);
                            if (newStatus == 1) {
                                entity.setLike_num(entity.getLike_num() + 1);
                            } else {
                                entity.setLike_num(entity.getLike_num() - 1);
                            }
                        }
                    }
                    mLastLoadDataTime = SystemClock.uptimeMillis();
                    if (hasChange) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }).start();
        } else {
            mLastLoadDataTime = SystemClock.uptimeMillis();
        }
    }

    @Override
    protected void loadDataFailed() {
        mLoadingLayout.setVisibility(View.GONE);
        if (mData.size() == 0) {
            mNetErrorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void loadMoreData() {
        boolean loadMore = mListView.getLastVisiblePosition() + 5 >= mData.size();
        if (loadMore) {
            loadAlbumInfoData(getActivity(), false);
        }
    }

    @Override
    protected BaseResponseBean getResponse(String rawJsonData, boolean isFailure) {
        return JsonUtil.fromJson(rawJsonData, ResponseBeanMyUserInfo.class);
    }

    public void deleteAblum(final AlbumInfoBean bean) {
        final String ablumId = bean.getAlbum_id();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int tempI = -1;
                for (int i = 0; i < mAlbumIdList.size(); i++) {
                    if (ablumId.equals(mAlbumIdList.get(i).getAlbum_id())) {
                        tempI = i;
                    }
                }
                if (tempI != -1) {
                    mAlbumIdList.remove(tempI);
                }

                int index = mData.indexOf(bean);
                if (index == -1) { //bean 不在data中，按id删除
                    for (int i = 0; i < mData.size(); i++) {
                        if (ablumId.equals(mData.get(i).getAlbum_id())) {
                            index = i;
                            break;
                        }
                    }
                }
                if (index == -1) {
                    return;
                }
                mData.remove(index);
                mRlFollowMe.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        if (mData.size() == 0 && mAlbumIdList.size() == 0) {
                            hasNoAblum(getActivity());
                        }
                    }
                });
                mLastLoadDataTime = SystemClock.uptimeMillis();
            }
        }).start();
    }

    public void loadAlbumInfoData(final Context context, final boolean isClearData) {
        if (mIsLoading) {
            mLoadingLayout.setVisibility(View.GONE);
            return;
        }

        int begin = mCurrentPage * COUNT_ONE_PAGE;
        int end = Math.min((mCurrentPage + 1) * COUNT_ONE_PAGE, mAlbumIdList.size());
        if (end >= mAlbumIdList.size()) {
            mHasMoreData = false;
            if (mAlbumIdList.size() <= 1) {
                mListView.setBackgroundColor(context.getResources().getColor(R.color.main_color_actionbar_item01));
            } else if (!mHasFootView) {
                addFootView();
            }
        }

        if (begin < end) {
            mIsLoading = true;
            RequestBeanAlbumInfo requestBean = new RequestBeanAlbumInfo();
            ArrayList<RequestBeanAlbumInfo.AlbumBean> albumBeans = new ArrayList<>();
            for (int i = begin; i < end; i++) {
                ResponseBeanAlbumListPersonnal.DataEntity albumListBean = mAlbumIdList.get(i);
                RequestBeanAlbumInfo.AlbumBean albumBean = new RequestBeanAlbumInfo.AlbumBean();
                albumBean.setId(albumListBean.getAlbum_id());
                albumBeans.add(albumBean);
            }

            requestBean.setAlbum(albumBeans);
            requestBean.setSize(App.sPreviewImgSize);
            final String data = JsonUtil.toJson(requestBean);
            String url = UrlsUtil.getAblumInfoUrl(App.sessionId, data);
            Log.d("MyPersonalCenter", "loadAlbumInfoData url = " + url);
            HttpClientManager.getInstance(context).getData(url, new BaseJsonHttpResponseHandler<ResponseBeanAlbumInfo>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanAlbumInfo response) {
                    if (response.getErr_code() == 0) {
                        List<AlbumInfoBean> data1 = response.getData();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        }).start();
                        for (int i = 0; i < data1.size(); i++) {
                            ImgAndTagWallManager.getInstance(context).initImgsWall(data1.get(i).getImages());
                            ImgAndTagWallManager.getInstance(context).initTagsWallForItem0(data1.get(i).getTags());
                        }
                        if (isClearData) {
                            mData.clear();
                        }
                        mData.addAll(response.getData());
                        mCurrentPage ++;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mAdapter == null || mAdapter.getData() == null) {
                                    mAdapter = new PersonnalcenterFragmentAdapter(context, mData, (View.OnClickListener)getActivity(), true);
                                    mListView.setAdapter(mAdapter);
                                } else {
                                    mAdapter.notifyDataSetChanged();
                                }
                                mIsLoading = false;
                                mLoadingLayout.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        mIsLoading = false;
                        mLoadingLayout.setVisibility(View.GONE);
                        if (mData.size() == 0) {
                            if (mAdapter == null || mAdapter.getData() != null) {
                                mHasMoreData = false;
                                mAdapter = new PersonnalcenterFragmentAdapter(context, null, (View.OnClickListener)getActivity(), true);
                                mListView.setAdapter(mAdapter);
                                mListView.setBackgroundColor(getActivity().getResources().getColor(R.color.main_color_actionbar_item01));
                            }
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanAlbumInfo errorResponse) {
                    mIsLoading = false;
                    mLoadingLayout.setVisibility(View.GONE);
                }

                @Override
                protected ResponseBeanAlbumInfo parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    Log.d("MyPersonalCenter", "loadAlbumInfoData rawJsonData = " + rawJsonData);
                    return JsonUtil.fromJson(rawJsonData, ResponseBeanAlbumInfo.class);
                }
            });
        }
    }

    /**
     * 在发完组图后，发现页和个人页需要立马添加一条刚刚发布的信息（自己模拟的数据）
     */
    @Override
    public void addFirstAblum(AlbumInfoBean album) {
        if (mAdapter != null && mAdapter.getData() != null) {
            mData.add(0, album);
            int firstVisiblePosition = mListView.getFirstVisiblePosition();
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
        } else if (getActivity() != null) {
            mData.add(0, album);
            mAdapter = new PersonnalcenterFragmentAdapter(getActivity(), mData, (View.OnClickListener)getActivity(), true);
            mListView.setAdapter(mAdapter);
        }
        mLastLoadDataTime = SystemClock.uptimeMillis();
        int albumnum = Integer.valueOf(mUserInfoResponse.getData().getAlbumnum()) + 1;
        mUserInfoResponse.getData().setAlbumnum(albumnum+"");
        mTvAblumCount.setText(albumnum+"");
    }
}
