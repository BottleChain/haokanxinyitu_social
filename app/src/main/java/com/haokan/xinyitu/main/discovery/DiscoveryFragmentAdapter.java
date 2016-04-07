package com.haokan.xinyitu.main.discovery;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.haokan.xinyitu.main.FragmentAdapterItemHelper;

import java.util.ArrayList;

public class DiscoveryFragmentAdapter extends BaseAdapter {
    private Context mContext;
    private FragmentAdapterItemHelper mHelper;
    private ArrayList<ResponseBeanAlbumInfo.DataEntity> mAlbumInfoBeans;

    public DiscoveryFragmentAdapter(Context context, ArrayList<ResponseBeanAlbumInfo.DataEntity> albumInfoBean, View.OnClickListener onClickListener) {
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
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        return mAlbumInfoBeans.get(position).getType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ResponseBeanAlbumInfo.DataEntity dataEntity = mAlbumInfoBeans.get(position);
        int type = dataEntity.getType();
        switch (type) {
            case 0: //用户timeline
                convertView = mHelper.initDiscoveryItem0(position, dataEntity, convertView, parent);
                break;
            case 1: //今天最热图片
                convertView = mHelper.initDiscoveryItem1(position, dataEntity, convertView, parent);
                break;
            case 2: //摄影师推荐
                convertView = mHelper.initDiscoveryItem2(position, dataEntity, convertView, parent);
                break;
            case 3: //这些家伙很牛B，推荐6个用户头像
                convertView = mHelper.initDiscoveryItem3(position, convertView, parent);
                break;
            default:
                break;
        }
        return convertView;
    }
}
