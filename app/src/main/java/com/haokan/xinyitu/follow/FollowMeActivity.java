package com.haokan.xinyitu.follow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;
import com.haokan.xinyitu.base.BaseResponseBean;
import com.haokan.xinyitu.main.otherpersonalcenter.OtherPersonalCenterActivity;
import com.haokan.xinyitu.util.ConstantValues;
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.ImageUtil;
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
    protected static final int COUNT_ONE_PAGE = 25; //每页多少个数据
    protected boolean mHasMoreData;
    protected boolean mIsLoading = false;
    private ArrayList<ResponseBeanOtherUserInfo.FollowUserInfoBean> mData = new ArrayList<>();
    private FollowMesAdapter mAdapter;
    private View mNoFollowedLayout;
    private String mUserId;


    private void assignViews() {
        mRlHeader = (RelativeLayout) findViewById(R.id.rl_header);
        mIbBack = (ImageButton) findViewById(R.id.ib_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mDivider1 = findViewById(R.id.divider_1);
        mLvFollows = (ListView) findViewById(R.id.lv_listview);
        mLoadingLayout = findViewById(R.id.loading_layout);
        mNetErrorLayout = findViewById(R.id.net_error_layout);
        mNoFollowedLayout = findViewById(R.id.ll_no_item);

        mLvFollows.setOnScrollListener(new PauseLoadImgOnScrollListener());
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

    private List<ResponseBeanFollwsUsers.FollowUserIdBean> mFollowMeUsers;
    private void loadData() {
        String url;
        if (TextUtils.isEmpty(getIntent().getStringExtra(ConstantValues.KEY__USERID))) {
            url = UrlsUtil.getLikeMeUrl(App.sessionId);
        } else {
            mUserId = getIntent().getStringExtra(ConstantValues.KEY__USERID);
            mTvTitle.setText("关注他的人");
            url = UrlsUtil.getUserFansUrl(App.sessionId, mUserId);
        }
        Log.d("wangzixu", "FollowMe loadData url = " + url);
        HttpClientManager.getInstance(this).getData(url, new BaseJsonHttpResponseHandler<ResponseBeanFollwsUsers>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanFollwsUsers response) {
                if (response.getErr_code() == 0) {
                    mFollowMeUsers = response.getData();
                    mHasMoreData = true;
                    loadUsersInfo();
                } else {
                    mNoFollowedLayout.setVisibility(View.VISIBLE);
                    mLvFollows.setVisibility(View.GONE);
                }
                mLoadingLayout.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanFollwsUsers errorResponse) {
                mLoadingLayout.setVisibility(View.GONE);
                //解析错误
            }

            @Override
            protected ResponseBeanFollwsUsers parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return JsonUtil.fromJson(rawJsonData, ResponseBeanFollwsUsers.class);
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
        int end = Math.min((mCurrentPage + 1) * COUNT_ONE_PAGE, mFollowMeUsers.size());
        if (end >= mFollowMeUsers.size()) {
            mHasMoreData = false;
        }

        if (begin < end) {
            mIsLoading = true;
            RequestBeanUsersInfo requestBean = new RequestBeanUsersInfo();
            ArrayList<RequestBeanUsersInfo.UserInfoRequestEntity> beans = new ArrayList<>();
            //String eachFollowStr = "";
            for (int i = begin; i < end; i++) {
                ResponseBeanFollwsUsers.FollowUserIdBean idEntity = mFollowMeUsers.get(i);
                RequestBeanUsersInfo.UserInfoRequestEntity bean = new RequestBeanUsersInfo.UserInfoRequestEntity();
                bean.setId(String.valueOf(idEntity.getUserid()));
                beans.add(bean);
//                if ("3".equals(idEntity.getRelation())) { //互粉
//                    eachFollowStr = eachFollowStr + idEntity.getUserid() + "-";
//                }
            }
//            final String eachFollowStrFinal = eachFollowStr;
//            eachFollowStr = null;
            requestBean.setUser(beans);
            final String data = JsonUtil.toJson(requestBean);
            String url = UrlsUtil.getUserInfoUrl(App.sessionId, data);
            Log.d("wangzixu", "loadUsersInfo url = " + url);
            HttpClientManager.getInstance(this).getData(url, new BaseJsonHttpResponseHandler<ResponseBeanOtherUserInfo>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanOtherUserInfo response) {
                    if (response.getErr_code() == 0) {
                        ArrayList<ResponseBeanOtherUserInfo.FollowUserInfoBean> data1 = response.getData();
//                        for (int i = 0; i < data1.size(); i++) { //设置正确的互粉关系
//                            ResponseBeanOtherUserInfo.FollowUserInfoBean bean = data1.get(i);
//                            if (eachFollowStrFinal.contains(bean.getUserid())) {
//                                bean.setEachFollow(true);
//                            }
//                        }
                        mData.addAll(data1);
                        mCurrentPage++;
                        if (mAdapter == null) {
                            mAdapter = new FollowMesAdapter(FollowMeActivity.this, mData);
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
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.iv_avatar:
                String userId = (String) v.getTag();
                if (TextUtils.isEmpty(userId)) {
                    return;
                }
                ImageUtil.changeLight((ImageView) v, true);
                Intent intent= new Intent(FollowMeActivity.this, OtherPersonalCenterActivity.class);
                intent.putExtra(OtherPersonalCenterActivity.KEY_INTENT_USERID, userId);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            case R.id.ib_follow: //点击关注
                final ResponseBeanOtherUserInfo.FollowUserInfoBean bean = (ResponseBeanOtherUserInfo.FollowUserInfoBean) v.getTag();
                final String relation = bean.getRelation();
                //关系:0.没关系,1.我的关注,2.我的粉丝,3.互粉
                switch (relation) {
                    case "1":
                    case "3":
                        View costomView = LayoutInflater.from(this).inflate(R.layout.cancel_follow_dialog_layout, null);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                                .setTitle("提示")
                                .setView(costomView)
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ResponseBeanFollwsUsers.FollowUserIdBean idEntity = null;
                                        for (int i = 0; i < App.sMyFollowsUser.size(); i++) {
                                            if(App.sMyFollowsUser.get(i).getUserid().equals(bean.getUserid())) {
                                                idEntity = App.sMyFollowsUser.get(i);
                                            }
                                        }
                                        App.sMyFollowsUser.remove(idEntity);
                                        notifyFragmentsFollowChanged(bean.getUserid(), false);
                                        if (relation.equals("1")) {
                                            bean.setRelation("0");
                                        } else {
                                            bean.setRelation("2");
                                        }
//                                        if (TextUtils.isEmpty(mUserId)) { //说明是关注我的人页面
//                                            mData.remove(bean);
//                                        } else { //说明是他关注的人界面
//                                        }
                                        mAdapter.notifyDataSetChanged();
                                        String url = UrlsUtil.getdelFollowUrl(App.sessionId, bean.getUserid());
                                        Log.d("wangzixu", "myfollows cancel url = " + url);
                                        HttpClientManager.getInstance(FollowMeActivity.this).getData(url, new BaseJsonHttpResponseHandler<BaseResponseBean>() {
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, BaseResponseBean response) {
                                                Log.d("wangzixu", "changeFollowState cancel onSuccess = " + response.getErr_msg());
                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, BaseResponseBean errorResponse) {

                                            }

                                            @Override
                                            protected BaseResponseBean parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                                                return JsonUtil.fromJson(rawJsonData, BaseResponseBean.class);
                                            }
                                        });
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        break;
                    case "0":
                    case "2":
                        if (relation.equals("0")) {
                            bean.setRelation("1");
                        } else {
                            bean.setRelation("3");
                        }
                        ResponseBeanFollwsUsers.FollowUserIdBean idEntity = new ResponseBeanFollwsUsers.FollowUserIdBean();
                        idEntity.setUserid(bean.getUserid());
                        idEntity.setRelation(bean.getRelation());
                        App.sMyFollowsUser.add(idEntity);
                        notifyFragmentsFollowChanged(bean.getUserid(), true);
                        mAdapter.notifyDataSetChanged();
                        String url = UrlsUtil.getaddFollowUrl(App.sessionId, bean.getUserid());
                        Log.d("wangzixu", "changeFollowState add url = " + url);
                        HttpClientManager.getInstance(this).getData(url, new BaseJsonHttpResponseHandler<BaseResponseBean>() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, BaseResponseBean response) {
                                Log.d("wangzixu", "changeFollowState add onSuccess = " + response.getErr_msg());
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, BaseResponseBean errorResponse) {

                            }

                            @Override
                            protected BaseResponseBean parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                                return JsonUtil.fromJson(rawJsonData, BaseResponseBean.class);
                            }
                        });
                        break;
                }
                break;
            case R.id.ib_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    protected void notifyFragmentsFollowChanged(String userId, boolean newStatus) {
        Intent intent = new Intent(ConstantValues.ACTION_MYFOLLOWS_CHANGE);
        intent.putExtra(ConstantValues.KEY__USERID, userId);
        intent.putExtra(ConstantValues.KEY_NEWSTATUS, newStatus);
        sendBroadcast(intent); //通知关注的人改变了，主页要刷新其它界面的关注按钮
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
