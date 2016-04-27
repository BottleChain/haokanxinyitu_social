package com.haokan.xinyitu.albumfailed;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.haokan.xinyitu.main.FragmentAdapterItemHelper;
import com.haokan.xinyitu.main.discovery.AlbumInfoBean;

import java.util.List;

public class FailedAlbumAdapter extends BaseAdapter {
    private FragmentAdapterItemHelper mHelper;
    private List<AlbumInfoBean> mAlbumInfoBeans;

    public FailedAlbumAdapter(Context context, List<AlbumInfoBean> albumInfoBean
            , View.OnClickListener onClickListener) {
        mAlbumInfoBeans = albumInfoBean;
        mHelper = new FragmentAdapterItemHelper(context, onClickListener);
    }

    public List<AlbumInfoBean> getData() {
        return mAlbumInfoBeans;
    }

    @Override
    public int getCount() {
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
        //用户timeline
        convertView = mHelper.initFailedAlbumItem(position, albumInfoBean, convertView, parent);
        return convertView;
    }

}
