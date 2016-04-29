package com.haokan.xinyitu.comment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;
import com.haokan.xinyitu.base.BaseResponseBean;
import com.haokan.xinyitu.main.otherpersonalcenter.OtherPersonalCenterActivity;
import com.haokan.xinyitu.util.CommonUtil;
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.JsonUtil;
import com.haokan.xinyitu.util.ToastManager;
import com.haokan.xinyitu.util.UrlsUtil;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by wangzixu on 2016/4/18.
 */
public class CommentMainActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    public static final String KEY_INITENT_ALBUMID = "ablumid";
    private View mIbClose;
    private View mLoadingLayout;
    private View mNetErrorLayout;
    private View mNoCommentLayout;
    private ListView mListView;
    private EditText mEditText;
    private String mAlbumId;
    protected int mCurrentPage; //分页加载，当前第几页
    protected static final int COUNT_ONE_PAGE = 30; //每页多少个数据
    protected boolean mHasMoreData; //是否还有更多的数据
    protected boolean mIsLoading = false; //是否正在加载
    private List<ResponseBeanCommentIdList.CommitIdBean> mCommitIdBeanList;
    private List<ResponseBeanGetCommentInfo.CommentInfoBean> mData = new ArrayList<>();
    private CommentMainAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commentmain_activity_layout);
        initViews();
        loadCommitIdData();
    }

    private void initViews() {
        mIbClose = findViewById(R.id.ib_close);
        mLoadingLayout = findViewById(R.id.loading_layout);
        mNetErrorLayout = findViewById(R.id.net_error_layout);
        mNoCommentLayout = findViewById(R.id.ll_no_comment);
        mListView = (ListView)findViewById(R.id.lv_comment);
        mEditText = (EditText)findViewById(R.id.et_edit);

        findViewById(R.id.tv_click_add).setOnClickListener(this);
        mIbClose.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(new PauseLoadImgOnScrollListener());
//        findViewById(R.id.rl_edit_container).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                Log.d("wangzixu", "onLayoutChange: bottom, old= " + bottom + ", " + oldBottom);
//                //利用这个玩意监听软键盘的出现和隐藏
//                if (oldBottom != 0 && bottom > oldBottom + 250) { //过滤掉华为手机隐藏显示导航栏的情况
//                    //隐藏了软键盘
//                    resetEditText();
//                }
//            }
//        });

        mNetErrorLayout.findViewById(R.id.iv_net_error).setOnClickListener(this);
    }

    private void loadCommitIdData() {
        mNoCommentLayout.setVisibility(View.GONE);
        if (HttpClientManager.checkNetWorkStatus(this)) {
            mAlbumId = getIntent().getStringExtra(KEY_INITENT_ALBUMID);
            String url = UrlsUtil.getCommentIdListUrl(App.sessionId, mAlbumId);
            Log.d("wangzixu", "getCommentIdListUrl url = " + url);
            HttpClientManager.getInstance(this).getData(url, new BaseJsonHttpResponseHandler<ResponseBeanCommentIdList>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanCommentIdList response) {
                    if (response.getErr_code() == 0) {
                        mCommitIdBeanList = response.getData();
                        mHasMoreData = true;
                        loadCommitInfoData(true);
                    } else {
                        if (mCommitIdBeanList == null || mCommitIdBeanList.size() == 0) {
                            mNoCommentLayout.setVisibility(View.VISIBLE);
                            mLoadingLayout.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanCommentIdList errorResponse) {
                    if (mCommitIdBeanList == null || mCommitIdBeanList.size() == 0) {
                        mNoCommentLayout.setVisibility(View.VISIBLE);
                        mLoadingLayout.setVisibility(View.GONE);
                    }
                }

                @Override
                protected ResponseBeanCommentIdList parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    return JsonUtil.fromJson(rawJsonData, ResponseBeanCommentIdList.class);
                }
            });
        } else {
            mNetErrorLayout.setVisibility(View.VISIBLE);
            mLoadingLayout.setVisibility(View.GONE);
        }
    }

    private void loadCommitInfoData(final boolean isClearData) {
        if (mIsLoading) {
            return;
        }
        if (isClearData) {
            mCurrentPage = 0;
        }
        int begin = mCurrentPage * COUNT_ONE_PAGE;
        final int end = Math.min((mCurrentPage + 1) * COUNT_ONE_PAGE, mCommitIdBeanList.size());
        if (end >= mCommitIdBeanList.size()) {
            mHasMoreData = false;
//            if (!mHasFootView) {
//                addFootView();
//            }
        }
        if (begin < end) {
            mIsLoading = true;
            RequestBeanGetCommentInfo requestBean = new RequestBeanGetCommentInfo();
            ArrayList<String> commitIdList = new ArrayList<>();
            for (int i = begin; i < end; i++) {
                ResponseBeanCommentIdList.CommitIdBean commitIdBean = mCommitIdBeanList.get(i);
                commitIdList.add(commitIdBean.getComment_id());
            }
            requestBean.setAlbum_id(mAlbumId);
            requestBean.setComment_id(commitIdList);
            final String data = JsonUtil.toJson(requestBean);
            String url = UrlsUtil.getCommentListByIdUrl(App.sessionId, data);
            Log.d("wangzixu", "loadCommitInfoData url = " + url);
            HttpClientManager.getInstance(this).getData(url, new BaseJsonHttpResponseHandler<ResponseBeanGetCommentInfo>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanGetCommentInfo response) {
                    Log.d("wangzixu", "loadCommitInfoData response.getErr_code() = " + response.getErr_code());
                    mIsLoading = false;
                    if (response.getErr_code() == 0) {
                        List<ResponseBeanGetCommentInfo.CommentInfoBean> data1 = response.getData();

                        if (isClearData) {
                            mData.clear();
                        }

                        mData.addAll(data1);

                        mCurrentPage++;
                        mLoadingLayout.setVisibility(View.GONE);
                        if (mData.size() == 0) { //相当于没有返回数据
                            mNoCommentLayout.setVisibility(View.VISIBLE);
                            mListView.setVisibility(View.INVISIBLE);
                            return;
                        }
                        mListView.setVisibility(View.VISIBLE);
                        if (mAdapter == null) {
                            mAdapter = new CommentMainAdapter(CommentMainActivity.this, mData);
                            mListView.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                        Log.d("wangzixu", "loadCommitInfoData success");
                    } else {
                        mLoadingLayout.setVisibility(View.GONE);
                        if (mData.size() == 0) { //相当于没有返回数据
                            mNoCommentLayout.setVisibility(View.VISIBLE);
                            mListView.setVisibility(View.INVISIBLE);
                            return;
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanGetCommentInfo errorResponse) {
                    Log.d("wangzixu", "loadCommitInfoData onFailure");
                    mIsLoading = false;
                    mLoadingLayout.setVisibility(View.GONE);
                    if (mData.size() == 0) { //相当于没有返回数据
                        mNoCommentLayout.setVisibility(View.VISIBLE);
                        mListView.setVisibility(View.INVISIBLE);
                        return;
                    }
                }

                @Override
                protected ResponseBeanGetCommentInfo parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    Log.d("wangzixu", "loadCommitInfoData parseResponse");
                    return JsonUtil.fromJson(rawJsonData, ResponseBeanGetCommentInfo.class);
                }
            });
        }
    }

    private void resetEditText() {
        mEditText.setHint("请输入评论内容");
        mEditText.setText("");
        m2UserId = null;
        m2CommentId = null;
    }


    @Override
    public void onClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.ib_close:
                InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm1.hideSoftInputFromWindow(mEditText.getWindowToken(), 0)) {
                    //nothing
                } else {
                    onBackPressed();
                }
                break;
            case R.id.tv_click_add:
                addComment();
                break;
            case R.id.iv_net_error:
                loadCommitIdData();
                break;
            case R.id.iv_avatar:
                String userId = (String) v.getTag();
                if (TextUtils.isEmpty(userId)) {
                    return;
                }
                Intent intent2 = new Intent(this, OtherPersonalCenterActivity.class);
                intent2.putExtra(OtherPersonalCenterActivity.KEY_INTENT_USERID, userId);
                startActivity(intent2);
                overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            default:
                break;
        }
    }

    private void addComment() {
        String comment = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(comment)) {
            return;
        }
        if (TextUtils.isEmpty(App.sessionId)) {
            ToastManager.showShort(CommentMainActivity.this, "您还未登录");
            return;
        }
        if (comment.contains("@")) {
            ToastManager.showShort(CommentMainActivity.this, "输入内容中不能含有 @ 符号");
            return;
        }
        if (HttpClientManager.checkNetWorkStatus(this)) {
            String url = UrlsUtil.getAddCommentUrl(App.sessionId);
            RequestParams params = new RequestParams();
            params.put("album_id", mAlbumId);
            params.put("comment", comment);
            if (!TextUtils.isEmpty(m2UserId)) {
                params.put("to_user_id", m2UserId);
                params.put("pcomment_id", m2CommentId);
            }
            Log.d("wangzixu", "addComment album_id = " + mAlbumId);
            Log.d("wangzixu", "addComment comment = " + comment);
            Log.d("wangzixu", "addComment to_user_id = " + m2UserId);
            Log.d("wangzixu", "addComment pcomment_id = " + m2CommentId);
            HttpClientManager.getInstance(this).postData(url, params, new BaseJsonHttpResponseHandler<BaseResponseBean>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, BaseResponseBean response) {
                    if (response.getErr_code() == 0) {
                        ToastManager.showShort(CommentMainActivity.this, "发布成功");
                        resetEditText();
                        InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                        loadCommitIdData(); //重新读取数据
                    } else {
                        ToastManager.showShort(CommentMainActivity.this, response.getErr_msg());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, BaseResponseBean errorResponse) {

                }

                @Override
                protected BaseResponseBean parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    return JsonUtil.fromJson(rawJsonData, BaseResponseBean.class);
                }
            });
        } else {
            ToastManager.showShort(this, "请检查网络连接");
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("wangzixu", "onBackPressed");
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
    }

    private String m2UserId;
    private String m2CommentId;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < mData.size()) {
            ResponseBeanGetCommentInfo.CommentInfoBean infoBean = mData.get(position);
            mEditText.setHint("回复 " + infoBean.getUser_nickname());
            mEditText.requestFocus();
            InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(mEditText, 0);
            m2UserId = infoBean.getUser_id();
            m2CommentId = infoBean.getComment_id();
        }
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
                            boolean loadMore = mListView.getLastVisiblePosition() + 8 >= mData.size();
                            if (loadMore) {
                                loadCommitInfoData(false);
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
