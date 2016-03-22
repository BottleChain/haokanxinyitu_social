package com.haokan.xinyitu.main.discovery;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

public class DiscoveryFragment extends BaseFragment {

    private ListView mPtrLv;
    private View mLoadingLayout;
    private View mNetErrorLayout;

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

        mPtrLv.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        DiscoveryFragmentAdapter adapter = new DiscoveryFragmentAdapter(getActivity(), (View.OnClickListener)getActivity());
        mPtrLv.setAdapter(adapter);
        mLoadingLayout.setVisibility(View.GONE);
        return view;
    }

    private void loadData() {

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
}
