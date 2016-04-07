package com.haokan.xinyitu.main.MyFollowsTimeLine;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.haokan.xinyitu.main.FragmentAdapterItemHelper;
import com.haokan.xinyitu.main.discovery.ResponseBeanAlbumInfo;

import java.util.ArrayList;

public class MyFollowFragmentAdapter extends BaseAdapter {
    private Context mContext;
    private FragmentAdapterItemHelper mHelper;
    private ArrayList<ResponseBeanAlbumInfo.DataEntity> mAlbumInfoBeans;

    public MyFollowFragmentAdapter(Context context, ArrayList<ResponseBeanAlbumInfo.DataEntity> albumInfoBean, View.OnClickListener onClickListener) {
        mContext = context;
        mAlbumInfoBeans = albumInfoBean;
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
        ResponseBeanAlbumInfo.DataEntity dataEntity = mAlbumInfoBeans.get(position);
        //用户timeline
        convertView = mHelper.initDiscoveryItem0(position, dataEntity, convertView, parent);
        return convertView;
    }

}
