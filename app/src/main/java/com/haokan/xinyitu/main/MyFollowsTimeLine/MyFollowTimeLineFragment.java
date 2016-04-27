package com.haokan.xinyitu.main.MyFollowsTimeLine;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseResponseBean;
import com.haokan.xinyitu.main.Base_PTR_LoadMore_Fragment;
import com.haokan.xinyitu.main.discovery.AlbumInfoBean;
import com.haokan.xinyitu.main.discovery.RequestBeanAlbumInfo;
import com.haokan.xinyitu.main.discovery.ResponseBeanAlbumInfo;
import com.haokan.xinyitu.main.mypersonalcenter.ResponseBeanAlbumListPersonnal;
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.ImgAndTagWallManager;
import com.haokan.xinyitu.util.JsonUtil;
import com.haokan.xinyitu.util.UrlsUtil;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MyFollowTimeLineFragment extends Base_PTR_LoadMore_Fragment implements PullToRefreshBase.OnRefreshListener<ListView>{

    private List<ResponseBeanAlbumListPersonnal.DataEntity> mAlbumIdList;
    private ArrayList<AlbumInfoBean> mData = new ArrayList<>();
    private MyFollowTimelineFragmentAdapter mAdapter;
    private Handler mHandler = new Handler();
    //private View mNoContentLayout;
    //private TextView mTvNoItem;
    private boolean mIsLogin;


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myfollowstimeline_fragment_layout, container, false);
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.ptrlv_1);
        mPullToRefreshListView.setOnRefreshListener(this);
        mListView = mPullToRefreshListView.getRefreshableView();
        mLoadingLayout = view.findViewById(R.id.loading_layout);
        mNetErrorLayout = view.findViewById(R.id.net_error_layout);
        mBtnNetError = mNetErrorLayout.findViewById(R.id.iv_net_error);
//        mNoContentLayout = view.findViewById(R.id.ll_no_followed);
//        mTvNoItem = (TextView) mNoContentLayout.findViewById(R.id.tv_no_item);
        return view;
    }


    /**
     * 改变登录状态
     */
    public void updataLoginStatus(boolean isLogin) {
        Log.d("wangzixu", "updataLoginStatus isLogin = " + isLogin);
        if (isLogin && !mIsLogin) {
            mIsLogin = true;
            //mNoContentLayout.setVisibility(View.GONE);
            mLastLoadDataTime = 0;
            loadData(getActivity());
        } else if (!isLogin && mIsLogin) {
            mIsLogin = false;
            if (getActivity() == null) {
                return;
            }
            mAdapter = new MyFollowTimelineFragmentAdapter(getActivity(), null, 1, (View.OnClickListener) getActivity());
            mListView.setAdapter(mAdapter);
            if (mHasFootView) {
                removeFootView();
            }
//            mNoContentLayout.setVisibility(View.VISIBLE);
//            mTvNoItem.setText("您还没有登录");
        }
    }

    public void reLoad() {
        //mPullToRefreshListView.setRefreshing(true);
        loadData(getActivity());
    }

    @Override
    public void loadData(Context context) {
        if (TextUtils.isEmpty(App.sessionId)) {
//            mNoContentLayout.setVisibility(View.VISIBLE);
//            mTvNoItem.setText("您还没有登录");
            if (mAdapter == null || mAdapter.getType() != 1) {
                mAdapter = new MyFollowTimelineFragmentAdapter(getActivity(), null, 1, (View.OnClickListener) getActivity());
                mListView.setAdapter(mAdapter);
            }
            mLoadingLayout.setVisibility(View.GONE);
//            mPullToRefreshListView.setVisibility(View.GONE);
            if (mHasFootView) {
                removeFootView();
            }
            return;
        }
        super.loadData(context);
    }

    public void notifyFragmentsFollowChanged(final String userId, final boolean newStatus) {
        if (newStatus) { //增加了一个关注的人，需要从新去取数据
            //mNoContentLayout.setVisibility(View.GONE);
            mLastLoadDataTime = 0;
            loadData(getActivity());
        } else { //删除了一个关注的人，把这个人的数据干掉即可，不用再去取新数据了
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("wangzixu", "notifyFragmentsFollowChanged ---");
                    if (mIsDestory) {
                        return;
                    }
                    List<AlbumInfoBean> temp = new ArrayList<>();
                    for (int i = 0; i < mData.size(); i++) {
                        AlbumInfoBean entity = mData.get(i);
                        if (userId.equals(entity.getUser_id())) {
                            temp.add(entity);
                        }
                    }
                    if (temp.size() > 0) {
                        mData.removeAll(temp);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mData.size() == 0) { //没有东西了
                                    if (mAdapter == null || mAdapter.getType() != 2) {
                                        mAdapter = new MyFollowTimelineFragmentAdapter(getActivity(), null, 2, (View.OnClickListener) getActivity());
                                        mListView.setAdapter(mAdapter);
                                    }
                                    if (mHasFootView) {
                                        removeFootView();
                                    }
                                    //mPullToRefreshListView.setVisibility(View.GONE);
                                } else {
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            }).start();
        }
    }

    public void notifyLikeStatusChanged(final String ablumId, final int newStatus) {
//        Log.d("wangzixu", "notifyLikeStatusChanged isVisible = ---" + isVisible());
//        Log.d("wangzixu", "notifyLikeStatusChanged isResumed = ---" + isResumed());
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
    protected String getLoadDataUrl() {
        String url = UrlsUtil.getMyMomentListUrl(App.sessionId);
        Log.d("wangzixu", "myfollowtimeline getLoadDataUrl url = " + url);
        return url;
    }

    @Override
    protected void loadDataSuccess(Context context, int statusCode, Header[] headers, String rawJsonResponse, BaseResponseBean response) {
        ResponseBeanAlbumListPersonnal responseBeanAlbumList = (ResponseBeanAlbumListPersonnal) response;
        mPullToRefreshListView.setVisibility(View.VISIBLE);
//        mNoContentLayout.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.GONE);
        mAlbumIdList = null;
        //mData.clear();
        mCurrentPage = 0;
        mAlbumIdList = responseBeanAlbumList.getData();
        mHasMoreData = true;
        if (mHasFootView) {
            removeFootView();
        }
        if (mAlbumIdList == null || mAlbumIdList.size() == 0) {
            if (mAdapter == null || mAdapter.getType() != 2) {
                mAdapter = new MyFollowTimelineFragmentAdapter(getActivity(), null, 2, (View.OnClickListener) getActivity());
                mListView.setAdapter(mAdapter);
            }
            mHasMoreData = false;
            mLoadingLayout.setVisibility(View.GONE);
            if (mHasFootView) {
                removeFootView();
            }
        } else {
            loadAlbumInfoData(context, true);
        }
    }

    @Override
    protected void loadDataErrCodeError(BaseResponseBean response) {
//        mNoContentLayout.setVisibility(View.VISIBLE);
//        mTvNoItem.setText("暂时没有您关注的人发布的组图");
        if (mAdapter == null || mAdapter.getType() != 2) {
            mAdapter = new MyFollowTimelineFragmentAdapter(getActivity(), null, 2, (View.OnClickListener) getActivity());
            mListView.setAdapter(mAdapter);
        }
        mLoadingLayout.setVisibility(View.GONE);
        if (mHasFootView) {
            removeFootView();
        }
        //mPullToRefreshListView.setVisibility(View.GONE);
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
        return JsonUtil.fromJson(rawJsonData, ResponseBeanAlbumListPersonnal.class);
    }

    public void loadAlbumInfoData(final Context context, final boolean isClearData) {
        if (mIsLoading) {
            return;
        }
        int begin = mCurrentPage * COUNT_ONE_PAGE;
        final int end = Math.min((mCurrentPage + 1) * COUNT_ONE_PAGE, mAlbumIdList.size());
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
                ResponseBeanAlbumListPersonnal.DataEntity dataEntity = mAlbumIdList.get(i);
                RequestBeanAlbumInfo.AlbumBean albumBean = new RequestBeanAlbumInfo.AlbumBean();
                albumBean.setId(dataEntity.getAlbum_id());
                albumBeans.add(albumBean);
            }
            requestBean.setAlbum(albumBeans);
            requestBean.setSize(App.sPreviewImgSize);
            final String data = JsonUtil.toJson(requestBean);
            String url = UrlsUtil.getAblumInfoUrl(App.sessionId, data);
            Log.d("wangzixu", "myfollowtimeline loadAlbumInfoData url = " + url);
            HttpClientManager.getInstance(context).getData(url, new BaseJsonHttpResponseHandler<ResponseBeanAlbumInfo>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanAlbumInfo response) {
                    if (response.getErr_code() == 0) {
                        final List<AlbumInfoBean> data1 = response.getData();
                        new Thread(new Runnable() { //因为有不少处理数据的逻辑，所以应该放在子线程中去完成
                            @Override
                            public void run() {
                                //过滤数据，去掉我不再关注的数据
                                String myNotFollowsStr = "";
                                List<AlbumInfoBean> tempL = new ArrayList<>();
                                for (int j = 0; j < data1.size(); j++) {
                                    AlbumInfoBean entity = data1.get(j);
                                    String userId = entity.getUser_id();
                                    if (myNotFollowsStr.contains(userId)) { //如果连续多个userId相同，不用每个都去遍历MyFollowsId集合
                                        tempL.add(entity);
                                        continue;
                                    }
                                    boolean notFollow = true;
                                    for (int i = 0; i < App.sMyFollowsUser.size(); i++) {
                                        String myFollowId = App.sMyFollowsUser.get(i).getUserid();
                                        if (myFollowId.equals(userId)) {
                                            notFollow = false;
                                            break;
                                        }
                                    }
                                    if (notFollow) {
                                        tempL.add(entity);
                                        myNotFollowsStr = myNotFollowsStr + "-" + userId;
                                    }
                                }
                                data1.removeAll(tempL);
                                tempL.clear();

                                for (int i = 0; i < data1.size(); i++) {
                                    ImgAndTagWallManager.getInstance(context).initImgsWall(data1.get(i).getImages());
                                    ImgAndTagWallManager.getInstance(context).initTagsWallForItem0(data1.get(i).getTags());
                                }

                                if (isClearData) {
                                    mData.clear();
                                }

                                mData.addAll(data1);

                                mCurrentPage++;
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mIsLoading = false;
                                        mLoadingLayout.setVisibility(View.GONE);
                                        if (mData.size() == 0) { //相当于没有返回数据
                                            loadDataErrCodeError(null);
                                            return;
                                        }
                                        if (mAdapter == null || mAdapter.getType() != 0) {
                                            mAdapter = new MyFollowTimelineFragmentAdapter(context, mData, (View.OnClickListener) getActivity());
                                            mListView.setAdapter(mAdapter);
                                        } else {
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }).start();
                    } else {
                        //loadDataFailed();
                        mIsLoading = false;
                        mLoadingLayout.setVisibility(View.GONE);
                        if (mData == null || mData.size() == 0) {
                            if (mAdapter == null || mAdapter.getType() != 2) {
                                mAdapter = new MyFollowTimelineFragmentAdapter(getActivity(), null, 2, (View.OnClickListener) getActivity());
                                mListView.setAdapter(mAdapter);
                            }
                            mHasMoreData = false;
                            mLoadingLayout.setVisibility(View.GONE);
                            if (mHasFootView) {
                                removeFootView();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanAlbumInfo errorResponse) {
                    mIsLoading = false;
//                    if (mData == null || mData.size() == 0) {
//                        mNetErrorLayout.setVisibility(View.VISIBLE);
//                    }
                    mLoadingLayout.setVisibility(View.GONE);
                }

                @Override
                protected ResponseBeanAlbumInfo parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    Log.d("DiscoveryFragment", "loadAlbumInfoData rawJsonData = " + rawJsonData);
                    return JsonUtil.fromJson(rawJsonData, ResponseBeanAlbumInfo.class);
                }
            });
        }
    }
}
