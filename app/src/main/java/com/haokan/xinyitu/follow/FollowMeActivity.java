package com.haokan.xinyitu.follow;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;
import com.haokan.xinyitu.base.UserInfoBean;
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.JsonUtil;
import com.haokan.xinyitu.util.UrlsUtil;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class FollowMeActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout mRlHeader;
    private ImageButton mIbBack;
    private TextView mTvTitle;
    private View mDivider1;
    private View mLoadingLayout;
    private View mNetErrorLayout;
    private ListView mLvFollows;
    protected int mCurrentPage; //分页加载，当前第几页
    protected static final int COUNT_ONE_PAGE = 20; //每页多少个数据
    protected boolean mHasMoreData;
    protected boolean mIsLoading = false;
    private ArrayList<UserInfoBean> mData = new ArrayList<>();
    private MyFollowsAdapter mAdapter;


    private void assignViews() {
        mRlHeader = (RelativeLayout) findViewById(R.id.rl_header);
        mIbBack = (ImageButton) findViewById(R.id.ib_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mDivider1 = findViewById(R.id.divider_1);
        mLvFollows = (ListView) findViewById(R.id.lv_follows);
        mLoadingLayout = findViewById(R.id.loading_layout);
        mNetErrorLayout = findViewById(R.id.net_error_layout);

        mIbBack.setOnClickListener(this);
//        mAdapter = new MyFollowsAdapter(this, mData);
//        mLvFollows.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.followme_activity_layout);
        assignViews();
        if (HttpClientManager.checkNetWorkStatus(this)) {
            loadData();
        } else {
            mNetErrorLayout.setVisibility(View.VISIBLE);
            mLoadingLayout.setVisibility(View.GONE);
        }
    }

    private List<ResponseBeanFollwsUsersId.UserIdEntity> mUserIdEntities;
    private void loadData() {
        String url = UrlsUtil.getLikeMeUrl(App.sessionId);
        Log.d("wangzixu", "FollowMe loadData url = " + url);
        HttpClientManager.getInstance(this).getData(url, new BaseJsonHttpResponseHandler<ResponseBeanFollwsUsersId>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanFollwsUsersId response) {
                if (response.getErr_code() == 0) {
                    mUserIdEntities = response.getData();
                    loadUsersInfo();
                } else {
                    mLoadingLayout.setVisibility(View.GONE);
                    //todo 没有关注的人，应该有一个ui提示
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanFollwsUsersId errorResponse) {
                mLoadingLayout.setVisibility(View.GONE);
                //解析错误
            }

            @Override
            protected ResponseBeanFollwsUsersId parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return JsonUtil.fromJson(rawJsonData, ResponseBeanFollwsUsersId.class);
            }
        });
    }

    private void loadUsersInfo() {
        if (mIsDestory) {
            return;
        }
        if (mIsLoading) {
            return;
        }

        int begin = mCurrentPage * COUNT_ONE_PAGE;
        int end = Math.min((mCurrentPage + 1) * COUNT_ONE_PAGE, mUserIdEntities.size());
        if (end >= mUserIdEntities.size()) {
            mHasMoreData = false;
        }

        if (begin < end) {
            mIsLoading = true;
            RequestBeanUsersInfo requestBean = new RequestBeanUsersInfo();
            ArrayList<RequestBeanUsersInfo.UserInfoRequestEntity> beans = new ArrayList<>();
            for (int i = begin; i < end; i++) {
                ResponseBeanFollwsUsersId.UserIdEntity idEntity = mUserIdEntities.get(i);
                RequestBeanUsersInfo.UserInfoRequestEntity bean = new RequestBeanUsersInfo.UserInfoRequestEntity();
                bean.setId(String.valueOf(idEntity.getUserid()));
                beans.add(bean);
            }
            requestBean.setUser(beans);
            final String data = JsonUtil.toJson(requestBean);
            String url = UrlsUtil.getUserInfoUrl(App.sessionId, data);
            Log.d("wangzixu", "loadUsersInfo url = " + url);
            HttpClientManager.getInstance(this).getData(url, new BaseJsonHttpResponseHandler<ResponseBeanOtherUserInfo>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanOtherUserInfo response) {
                    if (response.getErr_code() == 0) {
                        ArrayList<UserInfoBean> data1 = response.getData();
                        mData.addAll(data1);
                        mCurrentPage++;
                        if (mAdapter == null) {
                            mAdapter = new MyFollowsAdapter(FollowMeActivity.this, mData);
                            mLvFollows.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                    mIsLoading = false;
                    mLoadingLayout.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanOtherUserInfo errorResponse) {
                    mIsLoading = false;
                    mLoadingLayout.setVisibility(View.GONE);
                }

                @Override
                protected ResponseBeanOtherUserInfo parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    return JsonUtil.fromJson(rawJsonData, ResponseBeanOtherUserInfo.class);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
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
                            boolean loadMore = mLvFollows.getLastVisiblePosition() + 5 >= mData.size();
                            if (loadMore) {
                                loadUsersInfo();
                            }
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                }
            });
        }
    }
}
