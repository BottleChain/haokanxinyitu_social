package com.haokan.xinyitu.main.otherpersonalcenter;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
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
import com.haokan.xinyitu.follow.RequestBeanUsersInfo;
import com.haokan.xinyitu.follow.ResponseBeanOtherUserInfo;
import com.haokan.xinyitu.main.Base_PTR_LoadMore_Fragment;
import com.haokan.xinyitu.main.discovery.RequestBeanAlbumInfo;
import com.haokan.xinyitu.main.discovery.ResponseBeanAlbumInfo;
import com.haokan.xinyitu.main.mypersonalcenter.MyPersonnalcenterFragmentAdapter;
import com.haokan.xinyitu.main.mypersonalcenter.ResponseBeanAlbumListPersonnal;
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.ImageLoaderManager;
import com.haokan.xinyitu.util.ImgAndTagWallManager;
import com.haokan.xinyitu.util.JsonUtil;
import com.haokan.xinyitu.util.UrlsUtil;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class OtherPersonalCenterFragment extends Base_PTR_LoadMore_Fragment implements PullToRefreshBase.OnRefreshListener<ListView> {

    private List<ResponseBeanAlbumListPersonnal.DataEntity> mAlbumIdList;
    private ArrayList<ResponseBeanAlbumInfo.DataEntity> mData = new ArrayList<>();
    private View mRlTopbar;
    private View mIbBack;
    private View mIbFollow;
    private TextView mTvTitle;
    private TextView mTvHeaderTitle;
    private MyPersonnalcenterFragmentAdapter mAdapter;

    //处理上划显示标题用到的一些数据
    private int mTopBarBottom; //actionbar的底部位置
    private int mHeaderTitleTop;
    private int mHeaderTitleHeight;
    private View mHeader;
    private View mRlMyGallery;
    private View mRlMyFollows;
    private View mRlFollowMe;
    private TextView mTvHeaderDesc;
    private TextView mTvAblumCount;
    private TextView mTvMyFollowsCount;
    private TextView mTvFollowMeCount;
    private ImageView mIvAvatar;
    private boolean mIsMyCenterInfo;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.otherpersonalcenter_fragment_layout, container, false);
        mRlTopbar = view.findViewById(R.id.rl_header);
        mIbBack = view.findViewById(R.id.ib_back);
        mTvTitle = (TextView) view.findViewById(R.id.tv_title);
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.ptrlv_1);
        mPullToRefreshListView.setOnRefreshListener(this);
        mListView = mPullToRefreshListView.getRefreshableView();
        mLoadingLayout = view.findViewById(R.id.loading_layout);
        mNetErrorLayout = view.findViewById(R.id.net_error_layout);
        mBtnNetError = mNetErrorLayout.findViewById(R.id.iv_net_error);

        mHeader = inflater.inflate(R.layout.otherpersonnalcenter_header, container, false);
        mTvHeaderTitle = (TextView) mHeader.findViewById(R.id.tv_name);
        mTvHeaderDesc = (TextView) mHeader.findViewById(R.id.tv_desc);
        mIvAvatar = (ImageView) mHeader.findViewById(R.id.iv_avatar);
        mIbFollow = mHeader.findViewById(R.id.ib_follow);

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

        mIbBack.setOnClickListener((View.OnClickListener) getActivity());
        mRlMyFollows.setOnClickListener((View.OnClickListener) getActivity());
        mRlFollowMe.setOnClickListener((View.OnClickListener) getActivity());
        if (mIsMyCenterInfo) {
            mIbFollow.setVisibility(View.GONE);
        } else {
            mIbFollow.setVisibility(View.VISIBLE);
            mIbFollow.setOnClickListener((View.OnClickListener) getActivity());
        }

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

        return view;
    }

    public void setIsMyCenterInfo(boolean isMyCenterInfo) {
        mIsMyCenterInfo = isMyCenterInfo;
        if (mIbFollow == null) {
            return;
        }
        if (mIsMyCenterInfo) {
            mIbFollow.setVisibility(View.GONE);
        } else {
            mIbFollow.setVisibility(View.VISIBLE);
        }
    }

    public String mUserId;

    public void setUserId(String userId) {
        mUserId = userId;
        setIsMyCenterInfo(App.user_Id.equals(userId));
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

    @Override
    protected String getLoadDataUrl() {
        RequestBeanUsersInfo requestBean = new RequestBeanUsersInfo();
        ArrayList<RequestBeanUsersInfo.UserInfoRequestEntity> beans = new ArrayList<>();
        RequestBeanUsersInfo.UserInfoRequestEntity bean = new RequestBeanUsersInfo.UserInfoRequestEntity();
        bean.setId(String.valueOf(mUserId));
        beans.add(bean);
        requestBean.setUser(beans);
        final String data = JsonUtil.toJson(requestBean);
        String url = UrlsUtil.getUserInfoUrl(App.sessionId, data);
        Log.d("OtherPersonal", "loadData url = " + url);
        return url;
    }

    @Override
    protected void loadDataSuccess(Context context, int statusCode, Header[] headers, String rawJsonResponse, BaseResponseBean baseResponseBean) {
        ResponseBeanOtherUserInfo response = (ResponseBeanOtherUserInfo) baseResponseBean;
        mPullToRefreshListView.setVisibility(View.VISIBLE);

        String avatarUrl;
        if (App.sDensity >= 3) {
            avatarUrl = response.getData().get(0).getAvatar_url().getS150();
        } else {
            avatarUrl = response.getData().get(0).getAvatar_url().getS100();
        }
        String nickname = response.getData().get(0).getNickname();
        mTvTitle.setText(nickname);
        mTvHeaderTitle.setText(nickname);

        //开始加载组图
        loadAblumIdList(context);
        //加载头像
        if (avatarUrl != null) {
            ImageLoaderManager.getInstance().asyncLoadCircleImage(mIvAvatar, avatarUrl
                    , mIvAvatar.getWidth(), mIvAvatar.getHeight());
        }
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
                    mCurrentPage = 0;
                    mAlbumIdList = response.getData();
                    mHasMoreData = true;
                    mTvAblumCount.setText(String.valueOf(mAlbumIdList.size()));
                    loadAlbumInfoData(context, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanAlbumListPersonnal errorResponse) {
            }

            @Override
            protected ResponseBeanAlbumListPersonnal parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return JsonUtil.fromJson(rawJsonData, ResponseBeanAlbumListPersonnal.class);
            }
        });
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
        return JsonUtil.fromJson(rawJsonData, ResponseBeanOtherUserInfo.class);
    }


    public void loadAlbumInfoData(final Context context, final boolean isClearData) {
        if (mIsLoading) {
            return;
        }

        int begin = mCurrentPage * COUNT_ONE_PAGE;
        int end = Math.min((mCurrentPage + 1) * COUNT_ONE_PAGE, mAlbumIdList.size());
        if (end >= mAlbumIdList.size()) {
            mHasMoreData = false;
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
                        List<ResponseBeanAlbumInfo.DataEntity> data1 = response.getData();
                        for (int i = 0; i < data1.size(); i++) {
                            ImgAndTagWallManager.getInstance(context).initImgsWall(data1.get(i).getImages());
                            ImgAndTagWallManager.getInstance(context).initTagsWallForItem0(data1.get(i).getTags());
                        }
                        if (isClearData) {
                            mData.clear();
                        }
                        mData.addAll(response.getData());
                        mCurrentPage ++;
                        if (mAdapter == null) {
                            mAdapter = new MyPersonnalcenterFragmentAdapter(context, mData, (View.OnClickListener)getActivity());
                            mListView.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
//                        mAdapter.notifyDataSetChanged();
                    }
                    mIsLoading = false;
                    mLoadingLayout.setVisibility(View.GONE);
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
    public void addFirstAblum(ResponseBeanAlbumInfo.DataEntity album) {
        if (mAdapter != null) {
            mData.add(0, album);
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mLastLoadDataTime = SystemClock.uptimeMillis();
        }
    }
}