package com.haokan.xinyitu.main.discovery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseResponseBean;
import com.haokan.xinyitu.main.Base_PTR_LoadMore_Fragment;
import com.haokan.xinyitu.main.DemoImgBean;
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.ImgAndTagWallManager;
import com.haokan.xinyitu.util.JsonUtil;
import com.haokan.xinyitu.util.UrlsUtil;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class DiscoveryFragment extends Base_PTR_LoadMore_Fragment implements PullToRefreshBase.OnRefreshListener<ListView>{

    private List<ResponseBeanAlbumList.DataBean.AlbumListBean> mAlbumIdList;
    private ArrayList<AlbumInfoBean> mData = new ArrayList<>();
    private DiscoveryFragmentAdapter mAdapter;
    private BroadcastReceiver mMyFollowsChangeReceiver;
    private Handler mHandler = new Handler();

    //制造假数据用的
    private static int[] sW = {640, 640, 640, 640, 640, 640, 640};
    private static int[] sH = {640, 640, 640, 640, 640, 640, 640};
    private static int[] sIds = {R.drawable.test04, R.drawable.test04, R.drawable.test04
            , R.drawable.test04, R.drawable.test04, R.drawable.test04, R.drawable.test04};
    private static String[] sTags = {"风景如画", "猫", "好看摄影大赛第一季", "中国好声音", "萌", "北京雾霾天", "丝竹"};

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discovery_fragment_layout, container, false);
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.ptrlv_1);
        mPullToRefreshListView.setOnRefreshListener(this);
        mListView = mPullToRefreshListView.getRefreshableView();
        mLoadingLayout = view.findViewById(R.id.loading_layout);
        mNetErrorLayout = view.findViewById(R.id.net_error_layout);
        mBtnNetError = mNetErrorLayout.findViewById(R.id.iv_net_error);
        //registerChangeMyFollowBroadcast();
        return view;
    }

    @Override
    protected String getLoadDataUrl() {
        String url = UrlsUtil.getLastestAblumUrl(App.sessionId);
        Log.d("DiscoveryFragment", "loadData url = " + url);
        return url;
    }

    public void reLoad() {
        //mPullToRefreshListView.setRefreshing(true);
        loadData(getActivity());
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
                        if (viewWithTag != null && viewWithTag instanceof ImageButton) {
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
    protected void loadDataSuccess(Context context, int statusCode, Header[] headers, String rawJsonResponse, BaseResponseBean response) {
        ResponseBeanAlbumList responseBeanAlbumList = (ResponseBeanAlbumList) response;
        mPullToRefreshListView.setVisibility(View.VISIBLE);
        mAlbumIdList = null;
        //mData.clear();
        mCurrentPage = 0;
        Log.d("wangzixu", "000000000,responseBeanAlbumList,.getData() = " + responseBeanAlbumList + ", " + responseBeanAlbumList.getData());
        mAlbumIdList = responseBeanAlbumList.getData().getAlbumList();
        mHasMoreData = true;
        if (mHasFootView) {
            removeFootView();
        }
        loadAlbumInfoData(context, true);
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

                //处理3条特殊的timeline，使其位置不变
//                if (index > 12) {
//                    mData.remove(index);
//                } else if (index > 6) {
//                    if (mData.size() > 12) {
//                        AlbumInfoBean remove12 = mData.remove(12);
//                        mData.remove(index);
//                        if (mData.size() > 12) {
//                            mData.add(12, remove12);
//                        }
//                    } else {
//                        mData.remove(index);
//                    }
//                } else if (index > 2) {
//                    if (mData.size() > 12) {
//                        AlbumInfoBean remove12 = mData.remove(12);
//                        AlbumInfoBean remove6 = mData.remove(6);
//                        mData.remove(index);
//                        if (mData.size() > 12) {
//                            mData.add(12, remove12);
//                            mData.add(6,remove6);
//                        } else if (mData.size() > 6) {
//                            mData.add(6,remove6);
//                        }
//                    } else if (mData.size() > 6) {
//                        AlbumInfoBean remove6 = mData.remove(6);
//                        mData.remove(index);
//                        if (mData.size() > 6) {
//                            mData.add(6,remove6);
//                        }
//                        mData.remove(index);
//                    } else {
//                        mData.remove(index);
//                    }
//                } else {
//                    if (mData.size() > 12) {
//                        AlbumInfoBean remove12 = mData.remove(12);
//                        AlbumInfoBean remove6 = mData.remove(6);
//                        AlbumInfoBean remove2 = mData.remove(2);
//                        mData.remove(index);
//                        if (mData.size() > 12) {
//                            mData.add(12, remove12);
//                        }
//                        mData.add(6,remove6);
//                        mData.add(2, remove2);
//                    } else if (mData.size() > 6) {
//                        AlbumInfoBean remove6 = mData.remove(6);
//                        AlbumInfoBean remove2 = mData.remove(2);
//                        mData.remove(index);
//                        if (mData.size() > 6) {
//                            mData.add(6,remove6);
//                        }
//                        mData.add(2, remove2);
//                    } else if (mData.size() > 2) {
//                        AlbumInfoBean remove2 = mData.remove(2);
//                        mData.remove(index);
//                        if (mData.size() > 2) {
//                            mData.add(2, remove2);
//                        }
//                    } else {
//                        mData.remove(index);
//                    }
//                }
                mData.remove(index);
                mListView.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
                mLastLoadDataTime = SystemClock.uptimeMillis();
            }
        }).start();


    }

    @Override
    protected void loadDataFailed() {
        mLoadingLayout.setVisibility(View.GONE);
//        if (mData.size() == 0) {
//            mNetErrorLayout.setVisibility(View.VISIBLE);
//        }
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
        return JsonUtil.fromJson(rawJsonData, ResponseBeanAlbumList.class);
    }

    public void loadAlbumInfoData(final Context context, final boolean isClearData) {
        if (context == null || mIsDestory) {
            return;
        }
        if (mIsLoading) {
            mLoadingLayout.setVisibility(View.GONE);
            return;
        }
        int begin = mCurrentPage * COUNT_ONE_PAGE;
        final int end = Math.min((mCurrentPage + 1) * COUNT_ONE_PAGE, mAlbumIdList.size());
        if (end >= mAlbumIdList.size()) {
            mHasMoreData = false;
            if (mAlbumIdList.size() <= 2) {
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
                ResponseBeanAlbumList.DataBean.AlbumListBean albumListBean = mAlbumIdList.get(i);
                RequestBeanAlbumInfo.AlbumBean albumBean = new RequestBeanAlbumInfo.AlbumBean();
                albumBean.setId(albumListBean.getAlbum_id());
                albumBeans.add(albumBean);
            }
            requestBean.setAlbum(albumBeans);
            requestBean.setSize(App.sPreviewImgSize);
            final String data = JsonUtil.toJson(requestBean);
            String url = UrlsUtil.getAblumInfoUrl(App.sessionId, data);
            Log.d("wangzixu", "loadAlbumInfoData url = " + url);
            HttpClientManager.getInstance(context).getData(url, new BaseJsonHttpResponseHandler<ResponseBeanAlbumInfo>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanAlbumInfo response) {
                    if (response.getErr_code() == 0) {
                        final List<AlbumInfoBean> data1 = response.getData();
                        new Thread(new Runnable() { //因为有不少处理数据的逻辑，所以应该放在子线程中去完成
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
                                    if (myFollowsStr.contains(userId)) { //如果连续多个userId相同，不用每个都去遍历MyFollowsId集合
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

                                if (isClearData) {
                                    mData.clear();
                                }

                                mData.addAll(data1);
                                if (mCurrentPage == 0) { //造假数据
                                    //createItem_2_6_12(context);
                                }

                                mCurrentPage++;
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mAdapter == null) {
                                            mAdapter = new DiscoveryFragmentAdapter(context, mData, (View.OnClickListener) getActivity());
                                            mListView.setAdapter(mAdapter);
                                        } else {
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }).start();
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
                    return JsonUtil.fromJson(rawJsonData, ResponseBeanAlbumInfo.class);
                }
            });
        } else {
            mIsLoading = false;
            mLoadingLayout.setVisibility(View.GONE);
        }
    }

    private void createItem_2_6_12(Context context) {
        Random random = new Random();
        if (mData.size() > 2) {
            //第二块内容固定为今日最佳图片模块，造个假数据
            AlbumInfoBean bean1 = new AlbumInfoBean();
            bean1.setType(1);
            ArrayList<DemoImgBean> list = new ArrayList<>();
            int imgCount = random.nextInt(10) + 1;
            for (int j = 0; j < imgCount; j++) {
                DemoImgBean imgBean = new DemoImgBean();
                int index = random.nextInt(7);
                int id = sIds[index];
                int w = sW[index];
                int h = sH[index];
                imgBean.setHeight(String.valueOf(h));
                imgBean.setWidth(String.valueOf(w));
                imgBean.setUrl(ImageDownloader.Scheme.DRAWABLE.wrap(String.valueOf(id)));
                list.add(imgBean);
            }
            //处理图片数据
            ImgAndTagWallManager.getInstance(context).initImgsWall(list);
            bean1.setImages(list);
            mData.add(2, bean1);
        }

        if (mData.size() > 6) {
            //第6块内容固定为优秀摄影师推荐，造个假数据
            AlbumInfoBean bean2 = new AlbumInfoBean();
            bean2.setType(2);
            ArrayList<DemoImgBean> list2 = new ArrayList<>();
            int imgCount2 = random.nextInt(10) + 1;
            for (int j = 0; j < imgCount2; j++) {
                DemoImgBean imgBean = new DemoImgBean();
                int index = random.nextInt(7);
                int id = sIds[index];
                int w = sW[index];
                int h = sH[index];
                imgBean.setHeight(String.valueOf(h));
                imgBean.setWidth(String.valueOf(w));
                imgBean.setUrl(ImageDownloader.Scheme.DRAWABLE.wrap(String.valueOf(id)));
                list2.add(imgBean);
            }
            bean2.setImages(list2);
            mData.add(6, bean2);
            ImgAndTagWallManager.getInstance(context).initImgsWall(list2);
        }

        if (mData.size() > 12) {
            //第12块内容固定为用户推荐，造个假数据
            AlbumInfoBean bean3 = new AlbumInfoBean();
            bean3.setType(3);
            mData.add(12, bean3);
        }
    }

    /**
     * 在发完组图后，发现页和个人页需要立马添加一条刚刚发布的信息（自己模拟的数据）
     */
    @Override
    public void addFirstAblum(AlbumInfoBean album) {
        if (mAdapter != null) {
//            //固定位置的条目，不能变了位置，所以需要处理下
//            AlbumInfoBean remove2 = null;
//            AlbumInfoBean remove6 = null;
//            AlbumInfoBean remove12 = null;
//            if (mData.size() > 12) {
//                remove2 = mData.remove(12);
//            }
//            if (mData.size() > 6) {
//                remove2 = mData.remove(6);
//            }
//            if (mData.size() > 2) {
//                remove2 = mData.remove(2);
//            }
            mData.add(0, album);
//            if (remove2 != null) {
//                mData.add(2, remove2);
//            }
//            if (remove6 != null) {
//                mData.add(6, remove6);
//            }
//            if (remove12 != null) {
//                mData.add(12, remove12);
//            }
//            int firstVisiblePosition = mListView.getFirstVisiblePosition();
//            int nextPostion;
//            if (firstVisiblePosition == 3 ||
//                    firstVisiblePosition == 7 ||
//                    firstVisiblePosition == 13) {
//                nextPostion = firstVisiblePosition;
//            } else {
//                nextPostion = firstVisiblePosition + 1;
//            }
//            mListView.smoothScrollToPosition(nextPostion);
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mLastLoadDataTime = SystemClock.uptimeMillis();
        } else if (getActivity() != null) {
            mAdapter = new DiscoveryFragmentAdapter(getActivity(), mData, (View.OnClickListener) getActivity());
            mListView.setAdapter(mAdapter);
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
        //getActivity().unregisterReceiver(mMyFollowsChangeReceiver);
        Log.d("DiscoveryFragment", "onDestroyView --");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("DiscoveryFragment", "onDetach --");
    }
}
