package com.haokan.xinyitu.tagtimeline;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.haokan.xinyitu.main.FragmentAdapterItemHelper;
import com.haokan.xinyitu.main.discovery.AlbumInfoBean;

import java.util.ArrayList;

public class TagTimelineFragmentAdapter extends BaseAdapter {
    private FragmentAdapterItemHelper mHelper;
    private ArrayList<AlbumInfoBean> mAlbumInfoBeans;
    private boolean mIsMy; //是否是自己的个人中心界面，如果是需要显示删除，如果不是不能显示删除

    public TagTimelineFragmentAdapter(Context context, ArrayList<AlbumInfoBean> albumInfoBean
            , View.OnClickListener onClickListener, boolean isMy) {
        mAlbumInfoBeans = albumInfoBean;
        mIsMy = isMy;
        mHelper = new FragmentAdapterItemHelper(context, onClickListener);
    }

    @Override
    public int getCount() {
        if (mAlbumInfoBeans == null) {
            return 0;
        }
        return mAlbumInfoBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AlbumInfoBean albumInfoBean = mAlbumInfoBeans.get(position);
        //timeline
        convertView = mHelper.initDiscoveryItem0(position, albumInfoBean, convertView, parent, mIsMy);
        return convertView;
    }

}
