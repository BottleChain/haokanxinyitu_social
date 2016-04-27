package com.haokan.xinyitu.main.MyFollowsTimeLine;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haokan.xinyitu.R;
import com.haokan.xinyitu.main.FragmentAdapterItemHelper;
import com.haokan.xinyitu.main.discovery.AlbumInfoBean;

import java.util.ArrayList;

public class MyFollowTimelineFragmentAdapter extends BaseAdapter {
    private Context mContext;
    private FragmentAdapterItemHelper mHelper;
    private ArrayList<AlbumInfoBean> mAlbumInfoBeans;
    private int mType; // 对应三种类型的type，0，登录且有数据，1，您还未登录， 2，暂时没有好友发布的组图

    public MyFollowTimelineFragmentAdapter(Context context, ArrayList<AlbumInfoBean> albumInfoBean, View.OnClickListener onClickListener) {
        mContext = context;
        mAlbumInfoBeans = albumInfoBean;
        mHelper = new FragmentAdapterItemHelper(context, onClickListener);
    }

    public MyFollowTimelineFragmentAdapter(Context context, ArrayList<AlbumInfoBean> albumInfoBean, int type, View.OnClickListener onClickListener) {
        mContext = context;
        mAlbumInfoBeans = albumInfoBean;
        mType = type;
    }

    public int getType() {
        return mType;
    }

    @Override
    public int getCount() {
        if (mType != 0) {
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
        if (mType != 0) {
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.myfollowstimeline_fragment_layout_nocontentitem, null);
                TextView tv = (TextView) convertView.findViewById(R.id.tv_no_item);
                if (mType == 1) {
                    tv.setText("您还未登录");
                } else {
                    tv.setText("暂时没有好友发布的组图");
                }
                Log.d("wangizu", "loadDataErrCodeError getView = ");
            }
        } else {
            AlbumInfoBean albumInfoBean = mAlbumInfoBeans.get(position);
            //用户timeline
            convertView = mHelper.initDiscoveryItem0(position, albumInfoBean, convertView, parent, false, false);
        }
        return convertView;
    }

}
