package com.haokan.xinyitu.upload;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haokan.xinyitu.R;

import java.util.List;

public class UpLoadLocationAdapter extends BaseAdapter {
    private Context mContext;
    private int mSelectPos;
    private List<ResponseBeanLocation.LocationAddressBean> mData;

    public UpLoadLocationAdapter(Context context, List<ResponseBeanLocation.LocationAddressBean> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        if (mData == null || mData.size() == 0) {
            return 0;
        }
        return mData.size() + 1;
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
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position > 1) {
            return 1;
        } else {
            return position;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        switch (type) {
            case 0:
                ViewHolder01 holder0;
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.uploadlocation_activity_item_0, null);
                    holder0 = new ViewHolder01(convertView);
                    convertView.setTag(holder0);
                } else {
                    holder0 = (ViewHolder01)convertView.getTag();
                }
                if (mSelectPos == position) {
                    holder0.ivlocationitemselect.setVisibility(View.VISIBLE);
                } else {
                    holder0.ivlocationitemselect.setVisibility(View.GONE);
                }
                break;
            case 1:
                ViewHolder01 holder1;
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.uploadlocation_activity_item_1, null);
                    holder1 = new ViewHolder01(convertView);
                    convertView.setTag(holder1);
                } else {
                    holder1 = (ViewHolder01)convertView.getTag();
                }
                if (mSelectPos == position) {
                    holder1.ivlocationitemselect.setVisibility(View.VISIBLE);
                } else {
                    holder1.ivlocationitemselect.setVisibility(View.GONE);
                }
                holder1.tvlocationitemtitle.setText("北京市");
                break;
            case 2:
                ViewHolder2 holder2;
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.uploadlocation_activity_item_2, null);
                    holder2 = new ViewHolder2(convertView);
                    convertView.setTag(holder2);
                } else {
                    holder2 = (ViewHolder2)convertView.getTag();
                }
                if (mSelectPos == position) {
                    holder2.ivlocationitemselect.setVisibility(View.VISIBLE);
                } else {
                    holder2.ivlocationitemselect.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
        return convertView;
    }

    private class ViewHolder01 {
        public final TextView tvlocationitemtitle;
        public final ImageView ivlocationitemselect;
        public final View root;

        public ViewHolder01(View root) {
            tvlocationitemtitle = (TextView) root.findViewById(R.id.tv_locationitem_title);
            ivlocationitemselect = (ImageView) root.findViewById(R.id.iv_locationitem_select);
            this.root = root;
        }
    }

    private class ViewHolder2 extends ViewHolder01 {
        public final TextView tvlocationitemsub;

        public ViewHolder2(View root) {
            super(root);
            tvlocationitemsub = (TextView) root.findViewById(R.id.tv_sub);
        }
    }
}
