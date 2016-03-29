package com.haokan.xinyitu.main.discovery;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseFragment;
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.JsonUtil;
import com.haokan.xinyitu.util.UrlsUtil;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class DiscoveryFragment extends BaseFragment {

    private ListView mPtrLv;
    private View mLoadingLayout;
    private View mNetErrorLayout;
    private int mCurrentPage; //分页加载，当前第几页
    private static final int COUNT_ONE_PAGE = 10; //每页多少个数据
    private List<ResponseBeanAlbumList.DataBean.AlbumListBean> mAlbumIdList;
    private ArrayList<ResponseBeanAlbumInfo.DataBean> mAlbumInfoBeanLIst = new ArrayList<>();

    public DiscoveryFragment() {
        Log.d("DiscoveryFragment", "DiscoveryFragment --");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("DiscoveryFragment", "onCreateView --");
        View view = inflater.inflate(R.layout.discovery_fragment_layout, container, false);
        mPtrLv = ((PullToRefreshListView) view.findViewById(R.id.ptrlv_1)).getRefreshableView();
        mLoadingLayout = view.findViewById(R.id.loading_layout);
        mNetErrorLayout = view.findViewById(R.id.net_error_layout);

        mPtrLv.setOnScrollListener(new PauseLoadImgOnScrollListener());
        DiscoveryFragmentAdapter adapter = new DiscoveryFragmentAdapter(getActivity(), mAlbumInfoBeanLIst, (View.OnClickListener)getActivity());
        mPtrLv.setAdapter(adapter);
        return view;
    }

    public void loadAlbumListData(final Context context) {
        String url = UrlsUtil.getLastestAblumUrl(App.sessionId);
        Log.d("DiscoveryFragment", "loadAlbumListData url = " + url);

        HttpClientManager.getInstance(context).getData(url, new BaseJsonHttpResponseHandler<ResponseBeanAlbumList>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanAlbumList response) {
                if (response.getErr_code() == 0) {
                    mAlbumIdList = null;
                    mAlbumInfoBeanLIst.clear();
                    mCurrentPage = 0;
                    mAlbumIdList = response.getData().getAlbumList();
                    loadAlbumInfoData(context);
                } else {
                    mLoadingLayout.setVisibility(View.GONE);
                    //                    mNetErrorLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanAlbumList errorResponse) {

            }

            @Override
            protected ResponseBeanAlbumList parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                Log.d("DiscoveryFragment", "loadAlbumInfoData rawJsonData = " + rawJsonData);
                return JsonUtil.fromJson(rawJsonData, ResponseBeanAlbumList.class);
            }
        });
    }

    private boolean mIsLoading = false;
    private boolean mHasMoreData = true;
    public void loadAlbumInfoData(Context context) {
        if (mIsLoading) {
            return;
        }
        int begin = mCurrentPage * COUNT_ONE_PAGE;
        int end = Math.min((mCurrentPage + 1) * COUNT_ONE_PAGE, mAlbumIdList.size());
        if (begin < end) {
            mIsLoading = true;
            RequestBeanAlbumInfo requestBean = new RequestBeanAlbumInfo();
            ArrayList<RequestBeanAlbumInfo.AlbumBean> albumBeans = new ArrayList<>();
            for (int i = begin; i < end; i++) {
                ResponseBeanAlbumList.DataBean.AlbumListBean albumListBean = mAlbumIdList.get(i);
                RequestBeanAlbumInfo.AlbumBean albumBean = new RequestBeanAlbumInfo.AlbumBean();
                albumBean.setId(albumListBean.getAlbum_id());
                albumBeans.add(albumBean);
            }
            requestBean.setAlbum(albumBeans);
            String data = JsonUtil.toJson(requestBean);
            String url = UrlsUtil.getAblumInfoUrl(App.sessionId, data);
            Log.d("DiscoveryFragment", "loadAlbumInfoData url = " + url);
            HttpClientManager.getInstance(context).getData(url, new BaseJsonHttpResponseHandler<ResponseBeanAlbumInfo>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanAlbumInfo response) {
                    if (response.getErr_code() == 0) {
                        mCurrentPage ++;
                        mAlbumInfoBeanLIst.addAll(response.getData());
                    }
                    mIsLoading = false;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanAlbumInfo errorResponse) {
                    mIsLoading = false;
                }

                @Override
                protected ResponseBeanAlbumInfo parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    Log.d("DiscoveryFragment", "loadAlbumInfoData rawJsonData = " + rawJsonData);
                    return JsonUtil.fromJson(rawJsonData, ResponseBeanAlbumInfo.class);
                }
            });
        } else {
            mHasMoreData = false;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DiscoveryFragment", "onCreate --");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("DiscoveryFragment", "onAttach --");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("DiscoveryFragment", "onAttach 1 --");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("DiscoveryFragment", "onActivityCreated --");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("DiscoveryFragment", "onStart --");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("DiscoveryFragment", "onResume --");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("DiscoveryFragment", "onPause --");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("DiscoveryFragment", "onStop --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("DiscoveryFragment", "onDestroy --");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("DiscoveryFragment", "onDestroyView --");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("DiscoveryFragment", "onDetach --");
    }

    private class PauseLoadImgOnScrollListener extends PauseOnScrollListener {
        public PauseLoadImgOnScrollListener() {
            super(ImageLoader.getInstance(), true, true, new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                        if (mHasMoreData) {
                            boolean loadMore = mPtrLv.getLastVisiblePosition() + 5 >= mAlbumInfoBeanLIst.size();
                            if (loadMore) {
                                //滑动到尾页的一半时继续加载下一页，使用户感觉有无限多的数据，实现方式有两种：
                                //1，监听滚动停止事件，停止后添加
                                //2，在adapter中getItem提供回调，position大于一定值开始加载
                                //目前使用第一种方式
                                loadAlbumInfoData(getActivity());
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
