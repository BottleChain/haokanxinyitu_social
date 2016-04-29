package com.haokan.xinyitu.notice;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.JsonUtil;
import com.haokan.xinyitu.util.UrlsUtil;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by wangzixu on 2016/4/28.
 */
public class NoticeMainActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout mRlHeader;
    private ImageButton mIbBack;
    private TextView mTvTitle;
    private ListView mLvListview;
    private LinearLayout mLlNoItem;
    private View mLoadingLayout;
    private View mNetErrorLayout;
    protected int mCurrentPage; //分页加载，当前第几页
    protected static final int COUNT_ONE_PAGE = 20; //每页多少个数据
    protected boolean mHasMoreData;
    protected boolean mIsLoading = false;
    private List<ResponseBeanNoticeIdList.NoTiceIdBean> mNoticeIdList;
    private List<ResponseBeanNoticeContentList.NoticeContentBean> mData = new ArrayList<>();
    private NoticeMainAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_main_activity_layout);
        assignViews();
        loadData();
    }

    private void assignViews() {
        mRlHeader = (RelativeLayout) findViewById(R.id.rl_header);
        mIbBack = (ImageButton) findViewById(R.id.ib_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mLvListview = (ListView) findViewById(R.id.lv_listview);
        mLlNoItem = (LinearLayout) findViewById(R.id.ll_no_item);
        mLoadingLayout = findViewById(R.id.loading_layout);
        mNetErrorLayout = findViewById(R.id.net_error_layout);

        mLvListview.setOnScrollListener(new PauseLoadImgOnScrollListener());
        mNetErrorLayout.findViewById(R.id.iv_net_error).setOnClickListener(this);
        mIbBack.setOnClickListener(this);
    }

    private void loadData() {
        if (HttpClientManager.checkNetWorkStatus(this)) {
            String url = UrlsUtil.getMyReceivedNoticeIdListUrl(App.sessionId);
            Log.d("wangzixu", "NoticeMainActivity loadData url = " + url);
            HttpClientManager.getInstance(this).getData(url, new BaseJsonHttpResponseHandler<ResponseBeanNoticeIdList>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanNoticeIdList response) {
                    if (response.getErr_code() == 0) {
                        mNoticeIdList = response.getData();
                        mHasMoreData = true;
                        loadNoticeContent();
                    } else {
                        mLoadingLayout.setVisibility(View.GONE);
                        mLlNoItem.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanNoticeIdList errorResponse) {
                    mLoadingLayout.setVisibility(View.GONE);
                    mLlNoItem.setVisibility(View.VISIBLE);
                }

                @Override
                protected ResponseBeanNoticeIdList parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    return JsonUtil.fromJson(rawJsonData, ResponseBeanNoticeIdList.class);
                }
            });

        } else {
            mNetErrorLayout.setVisibility(View.VISIBLE);
            mLoadingLayout.setVisibility(View.GONE);
        }
    }

    public void loadNoticeContent() {
        if (mIsDestory) {
            return;
        }
        if (mIsLoading) {
            return;
        }

        int begin = mCurrentPage * COUNT_ONE_PAGE;
        int end = Math.min((mCurrentPage + 1) * COUNT_ONE_PAGE, mNoticeIdList.size());
        if (end >= mNoticeIdList.size()) {
            mHasMoreData = false;
        }

        if (begin < end) {
            mIsLoading = true;

            RequestBeanNoticeContent bean = new RequestBeanNoticeContent();
            ArrayList<RequestBeanNoticeContent.NoticeIdsBean> arrayList = new ArrayList<>();
            for (int i = begin; i < end; i++) {
                ResponseBeanNoticeIdList.NoTiceIdBean noTiceIdBean = mNoticeIdList.get(i);
                RequestBeanNoticeContent.NoticeIdsBean noticeIdsBean = new RequestBeanNoticeContent.NoticeIdsBean();
                noticeIdsBean.setId(noTiceIdBean.getMessage_id());
                arrayList.add(noticeIdsBean);
            }
            bean.setSize(240);
            bean.setNotice_ids(arrayList);

            String url = UrlsUtil.getMyReceivedNoticeListUrl(App.sessionId, JsonUtil.toJson(bean));
            Log.d("wangzixu", "NoticeMainActivity loadNoticeContent url = " + url);
            HttpClientManager.getInstance(this).getData(url, new BaseJsonHttpResponseHandler<ResponseBeanNoticeContentList>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanNoticeContentList response) {
                    if (response.getErr_code() == 0) {
                        List<ResponseBeanNoticeContentList.NoticeContentBean> data = response.getData();
                        mData.addAll(data);
                        mCurrentPage ++;
                        if (mAdapter == null) {
                            mAdapter = new NoticeMainAdapter(NoticeMainActivity.this, mData);
                            mLvListview.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                    mLoadingLayout.setVisibility(View.GONE);
                    mIsLoading = false;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanNoticeContentList errorResponse) {
                    mLoadingLayout.setVisibility(View.GONE);
                    mIsLoading = false;
                }

                @Override
                protected ResponseBeanNoticeContentList parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    Log.d("wangzixu", "NoticeMainActivity loadNoticeContent rawJsonData " + rawJsonData);
                    return JsonUtil.fromJson(rawJsonData, ResponseBeanNoticeContentList.class);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                onBackPressed();
                break;
            case R.id.iv_net_error:
                mNetErrorLayout.setVisibility(View.GONE);
                mLoadingLayout.setVisibility(View.VISIBLE);
                loadData();
                break;
            default:
                break;
        }
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
            super(ImageLoader.getInstance(), false, true, new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                        //滑动到尾页的一半时继续加载下一页，使用户感觉有无限多的数据，实现方式有两种：
                        //1，监听滚动停止事件，停止后添加
                        //2，在adapter中getItem提供回调，position大于一定值开始加载
                        //目前使用第一种方式
                        if (mHasMoreData) {
                            boolean loadMore = mLvListview.getLastVisiblePosition() + 5 >= mData.size();
                            if (loadMore) {
                                loadNoticeContent();
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
