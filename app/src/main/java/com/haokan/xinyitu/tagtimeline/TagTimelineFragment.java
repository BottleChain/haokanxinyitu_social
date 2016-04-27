package com.haokan.xinyitu.tagtimeline;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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

public class TagTimelineFragment extends Base_PTR_LoadMore_Fragment implements PullToRefreshBase.OnRefreshListener<ListView> {

    private ArrayList<AlbumInfoBean> mData = new ArrayList<>();
    private View mIbBack;
    private TextView mTvTitle;
    private TagTimelineFragmentAdapter mAdapter;
    private boolean mIsMyCenterInfo;
    private View mRlTopbar;
    private List<ResponseBeanAlbumListPersonnal.DataEntity> mAlbumIdList;
    private Handler mHandler = new Handler();

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tagtimeline_fragment_layout, container, false);
        mRlTopbar = view.findViewById(R.id.rl_header);
        mIbBack = view.findViewById(R.id.ib_back);
        mTvTitle = (TextView) view.findViewById(R.id.tv_title);
        mTvTitle.setText(mTagName);
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.ptrlv_1);
        mPullToRefreshListView.setOnRefreshListener(this);
        mListView = mPullToRefreshListView.getRefreshableView();
        mLoadingLayout = view.findViewById(R.id.loading_layout);
        mNetErrorLayout = view.findViewById(R.id.net_error_layout);
        mBtnNetError = mNetErrorLayout.findViewById(R.id.iv_net_error);
        mIbBack.setOnClickListener((View.OnClickListener) getActivity());
        return view;
    }

    public String mTagId;
    public String mTagName;

    public void setTagId(String tagId) {
        mTagId = tagId;
    }

    public void setTagName(String tagName) {
        mTagName = tagName;
        if (mTvTitle != null) {
            mTvTitle.setText(mTagName);
        }
    }

    public void notifyFragmentsFollowChanged(final String userId, final boolean newStatus) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("wangzixu", "notifyFragmentsFollowChanged ---");
                if (mIsDestory) {
                    return;
                }
                for (int i = 0; i < mData.size(); i++) {
                    AlbumInfoBean entity = mData.get(i);
                    if (userId.equals(entity.getUser_id())) {
                        entity.setIsFollowed(newStatus);
                        final View viewWithTag = mListView.findViewWithTag(entity);
                        if (viewWithTag != null) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    viewWithTag.setSelected(newStatus);
                                }
                            });
                        }
                    }
                }
            }
        }).start();
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
    protected String getLoadDataUrl() {
        String url = UrlsUtil.getTagAlbumsUrl(App.sessionId, mTagId);
        Log.d("wangzixu", "tagtimeline, loadData url = " + url);
        return url;
    }

    @Override
    protected void loadDataSuccess(Context context, int statusCode, Header[] headers, String rawJsonResponse, BaseResponseBean baseResponseBean) {
        ResponseBeanAlbumListPersonnal responseBeanAlbumList = (ResponseBeanAlbumListPersonnal) baseResponseBean;
        mPullToRefreshListView.setVisibility(View.VISIBLE);
        mLoadingLayout.setVisibility(View.GONE);
        mAlbumIdList = null;
        //mData.clear();
        mCurrentPage = 0;
        mAlbumIdList = responseBeanAlbumList.getData();
        mHasMoreData = true;
        if (mHasFootView) {
            removeFootView();
        }
        loadAlbumInfoData(context, true);
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

    public void deleteAblum(AlbumInfoBean bean) {
        int i = mData.indexOf(bean);
        mData.remove(bean);
        mAlbumIdList.remove(i);
        mAdapter.notifyDataSetChanged();
        mLastLoadDataTime = SystemClock.uptimeMillis();
    }


    public void loadAlbumInfoData(final Context context, final boolean isClearData) {
        if (mIsLoading) {
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
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, final ResponseBeanAlbumInfo response) {
                    if (response.getErr_code() == 0) {
                        final List<AlbumInfoBean> data1 = response.getData();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < data1.size(); i++) {
                                    ImgAndTagWallManager.getInstance(context).initImgsWall(data1.get(i).getImages());
                                    ImgAndTagWallManager.getInstance(context).initTagsWallForItem0(data1.get(i).getTags());
                                }
                                //过滤数据，正确的给关注关系赋值
                                String myFollowsStr = "";
                                for (int j = 0; j < data1.size(); j++) {
                                    AlbumInfoBean entity = data1.get(j);
                                    entity.setIsFollowed(false);
                                    String userId = entity.getUser_id();
                                    if (myFollowsStr.contains(userId)) {
                                        entity.setIsFollowed(true);
                                        continue;
                                    }
                                    for (int i = 0; i < App.sMyFollowsUser.size(); i++) {
                                        String myFollowId = App.sMyFollowsUser.get(i).getUserid();
                                        if (userId.equals(myFollowId)) {
                                            entity.setIsFollowed(true);
                                            myFollowsStr = myFollowsStr + "-" + userId;
                                            break;
                                        }
                                    }
                                }
                                myFollowsStr = null;

                                if (isClearData) {
                                    mData.clear();
                                }
                                mData.addAll(response.getData());
                                mCurrentPage ++;

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mAdapter == null) {
                                            mAdapter = new TagTimelineFragmentAdapter(context, mData, (View.OnClickListener)getActivity(), false); //不显示删除，所以false
                                            mListView.setAdapter(mAdapter);
                                        } else {
                                            mAdapter.notifyDataSetChanged();
                                        }
                                        mIsLoading = false;
                                        mLoadingLayout.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }).start();
                    } else {
                        mIsLoading = false;
                        mLoadingLayout.setVisibility(View.GONE);
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
}
