package com.haokan.xinyitu.main.mypersonalcenter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.haokan.xinyitu.main.FragmentAdapterItemHelper;
import com.haokan.xinyitu.main.discovery.AlbumInfoBean;

import java.util.ArrayList;

public class PersonnalcenterFragmentAdapter extends BaseAdapter {
    private FragmentAdapterItemHelper mHelper;
    private ArrayList<AlbumInfoBean> mAlbumInfoBeans;
    private boolean mIsMy; //是否是自己的个人中心界面，如果是需要显示删除，如果不是不能显示删除

    public PersonnalcenterFragmentAdapter(Context context, ArrayList<AlbumInfoBean> albumInfoBean
            , View.OnClickListener onClickListener, boolean isMy) {
        mAlbumInfoBeans = albumInfoBean;
        mIsMy = isMy;
        mHelper = new FragmentAdapterItemHelper(context, onClickListener);
    }

    public ArrayList<AlbumInfoBean> getData() {
        return mAlbumInfoBeans;
    }

    @Override
    public int getCount() {
        if (mAlbumInfoBeans == null) { //说明一个数据没有
            return 1;
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
        if (mAlbumInfoBeans == null) {
            convertView = mHelper.initPersonnalCenterNoItem(parent);
        } else {
            AlbumInfoBean albumInfoBean = mAlbumInfoBeans.get(position);
            //用户timeline
            convertView = mHelper.initPersonnalCenterItem0(position, albumInfoBean, convertView, parent, mIsMy);
        }
        return convertView;
    }

}
