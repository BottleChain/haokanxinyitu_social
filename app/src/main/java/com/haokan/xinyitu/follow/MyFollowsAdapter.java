package com.haokan.xinyitu.follow;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.UserInfoBean;
import com.haokan.xinyitu.util.ImageLoaderManager;

import java.util.ArrayList;

public class MyFollowsAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<UserInfoBean> mData;

    public MyFollowsAdapter(Context context, ArrayList<UserInfoBean> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
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

        UserInfoBean userInfoBean = mData.get(position);

        holder.tvname.setText(userInfoBean.getNickname());
        holder.tvdesc.setText(userInfoBean.getMobile());

        String path;
        if (App.sDensity >= 3) {
            path = userInfoBean.getAvatar_url().getS150();
        } else {
            path = userInfoBean.getAvatar_url().getS100();
        }

        if (!TextUtils.isEmpty(path)) {
            ImageLoaderManager.getInstance().asyncLoadCircleImage(holder.ivavatar, path
                    , 0, 0);
        } else {
            holder.ivavatar.setImageResource(R.drawable.icon_login_photo);
        }
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
