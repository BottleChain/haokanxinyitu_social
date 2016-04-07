package com.haokan.xinyitu.main;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haokan.xinyitu.base.BaseFragment;
import com.haokan.xinyitu.base.BaseResponseBean;
import com.haokan.xinyitu.main.discovery.ResponseBeanAlbumInfo;
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.ToastManager;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import cz.msebera.android.httpclient.Header;

public abstract class Base_PTR_LoadMore_Fragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener<ListView> {
//    T extends BaseResponseBean <T extends BaseResponseBean>
    protected ListView mListView;
    protected View mLoadingLayout;
    protected View mNetErrorLayout;
    protected View mBtnNetError;
    protected PullToRefreshListView mPullToRefreshListView;
    protected int mCurrentPage; //分页加载，当前第几页
    protected static final int COUNT_ONE_PAGE = 15; //每页多少个数据
    protected boolean mHasMoreData;
    protected boolean mIsLoading = false;
    /**
     * 上一次最近的加载数据时间，下次加载数据（下拉刷新，或者来回切换tab页），如果事件间隔过短，则不去重新load，无意义
     */
    protected long mLastLoadDataTime = -30000;
    public static final int LOAD_DATA_DELTA = 30000; //两次加载数据的最小间隔，20s

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initView(inflater, container, savedInstanceState);

        mPullToRefreshListView.setOnRefreshListener(this);
        mListView.setOnScrollListener(new PauseLoadImgOnScrollListener());
        mPullToRefreshListView.setVisibility(View.INVISIBLE);
        if (HttpClientManager.checkNetWorkStatus(getActivity())) {
            loadData(getActivity());
        } else {
            mNetErrorLayout.setVisibility(View.VISIBLE);
            mLoadingLayout.setVisibility(View.GONE);
        }
        setExtraScrollListener();

        mBtnNetError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HttpClientManager.checkNetWorkStatus(getActivity())) {
                    mLoadingLayout.setVisibility(View.VISIBLE);
                    mPullToRefreshListView.setVisibility(View.INVISIBLE);
                    mNetErrorLayout.setVisibility(View.GONE);
                    mLastLoadDataTime = -LOAD_DATA_DELTA;
                    loadData(getActivity());
                } else {
                    mNetErrorLayout.setVisibility(View.VISIBLE);
                    mLoadingLayout.setVisibility(View.GONE);
                }
            }
        });
        return view;
    }

    /**
     * 初始化view，和一些成员变量，必须要有以下四个,是在onCreateView 第一句调用的，infalt view 并且赋值
       private ListView mListView;---
       private View mLoadingLayout;---
       private View mNetErrorLayout;---
       private PullToRefreshListView mPullToRefreshListView;
     */
    protected abstract View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * 获取loadData的url，子类必须实现
     */
    protected abstract String getLoadDataUrl();

    /**
     * 访问网络成功，并且返回的errorCode==0
     */
    protected abstract void loadDataSuccess(Context context, int statusCode, Header[] headers, String rawJsonResponse, BaseResponseBean response);

    /**
     * 访问网络失败，并且返回的errorCode!=0,或者onfailed
     */
    protected abstract void loadDataFailed();

    /**
     * 下拉时如果网络还有更多数据，则会调用此方法请求数据，子类需要实现是否要请求更多数据及怎么请求
     */
    protected abstract void loadMoreData();

    /**
     * 解析返回的json数据
     */
    protected abstract BaseResponseBean getResponse(String rawJsonData, boolean isFailure);

    /**
     * 在发完组图后，发现页和个人页需要立马添加一条刚刚发布的信息（自己模拟的数据）
     */
    public void addFirstAblum(ResponseBeanAlbumInfo.DataEntity album) {

    }

    public void loadData(final Context context) {
        if (context == null || mIsDestory) {
            return;
        }
        if (mIsLoading) {
            return;
        }

        //处理连续的两次加载数据
        long t = SystemClock.uptimeMillis();
        if (t - mLastLoadDataTime < LOAD_DATA_DELTA) { //间隔太小，不用加载
            if (mPullToRefreshListView.isRefreshing()) {
                mPullToRefreshListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshListView.onRefreshComplete();
                    }
                }, 600);
            }
            Log.d("wangzixu", "Base_ptr 更新间隔太短，直接返回");
            return;
        }
        mLastLoadDataTime = t;

        mIsLoading = true;
        String url = getLoadDataUrl();
        HttpClientManager.getInstance(context).getData(url, new BaseJsonHttpResponseHandler<BaseResponseBean>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, BaseResponseBean response) {
                mIsLoading = false;
                if (response.getErr_code() == 0) {
                    loadDataSuccess(context, statusCode, headers, rawJsonResponse, response);
                } else {
                    loadDataFailed();
                }
                if (mPullToRefreshListView.isRefreshing()) {
                    mPullToRefreshListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPullToRefreshListView.onRefreshComplete();
                        }
                    }, 600);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, BaseResponseBean errorResponse) {
                mIsLoading = false;
                loadDataFailed();
                if (mPullToRefreshListView.isRefreshing()) {
                    mPullToRefreshListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPullToRefreshListView.onRefreshComplete();
                        }
                    }, 600);
                }
            }

            @Override
            protected BaseResponseBean parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                Log.d("wangzixu", "Base_PTR rawJsonData  = " + rawJsonData);
                return getResponse(rawJsonData, isFailure);
            }
        });
    }


    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (HttpClientManager.checkNetWorkStatus(getActivity())) {
            loadData(getActivity());
        } else {
            ToastManager.showShort(getActivity(), "当前无可用网络");
            mPullToRefreshListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mIsDestory) {
                        return;
                    }
                    mPullToRefreshListView.onRefreshComplete();
                }
            }, 500);
        }
    }

    /**
     * listview 用的滚动监听，为了实现加载更多，listview的setOnscrollListener已经被占用，
     * 需要额外的滚动监听，请实现setExtraScrollListener方法；
     */
    public interface ExtraScrollListener {
        void onScrollStateChanged(AbsListView view, int scrollState);
        void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }
    protected ExtraScrollListener mExtraScrollListener;

    public void setExtraScrollListener() {
    }

    /**
     * 使listview滚动时不加载数据的监听器
     */
    private class PauseLoadImgOnScrollListener extends PauseOnScrollListener {
        public PauseLoadImgOnScrollListener() {
            super(ImageLoader.getInstance(), true, true, new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                        //滑动到尾页的一半时继续加载下一页，使用户感觉有无限多的数据，实现方式有两种：
                        //1，监听滚动停止事件，停止后添加
                        //2，在adapter中getItem提供回调，position大于一定值开始加载
                        //目前使用第一种方式
                        if (mHasMoreData) {
                            loadMoreData();
                        }
                    }
                    if (mExtraScrollListener != null) {
                        mExtraScrollListener.onScrollStateChanged(view, scrollState);
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (mExtraScrollListener != null) {
                        mExtraScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                    }
                }
            });
        }
    }
}
