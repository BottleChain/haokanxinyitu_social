package com.haokan.xinyitu.follow;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.haokan.xinyitu.R;

public class FollowMeAdapter extends BaseAdapter {
    private Context mContext;

    public FollowMeAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return 20;
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.myfollows_activity_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        final String path = bean.getUrl();
//        ImageLoaderManager.getInstance().asyncLoadImage(holder.mImageView, path, mItemWidth, mItemHeight);
        return convertView;
    }

    private class ViewHolder {
        public final ImageView ivavatar;
        public final TextView tvname;
        public final TextView tvdesc;
        public final ImageButton ibfollow;
        public final View root;

        public ViewHolder(View root) {
            ivavatar = (ImageView) root.findViewById(R.id.iv_avatar);
            tvname = (TextView) root.findViewById(R.id.tv_name);
            tvdesc = (TextView) root.findViewById(R.id.tv_desc);
            ibfollow = (ImageButton) root.findViewById(R.id.ib_follow);
            this.root = root;
        }
    }
}
