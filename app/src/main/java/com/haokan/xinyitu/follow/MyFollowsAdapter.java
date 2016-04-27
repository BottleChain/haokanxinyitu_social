package com.haokan.xinyitu.follow;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.util.ImageLoaderManager;

import java.util.ArrayList;

public class MyFollowsAdapter extends BaseAdapter {
    public Activity mContext;
    private ArrayList<ResponseBeanOtherUserInfo.FollowUserInfoBean> mData;

    public MyFollowsAdapter(Activity context, ArrayList<ResponseBeanOtherUserInfo.FollowUserInfoBean> data) {
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

    protected View getConvertView() {
        return View.inflate(mContext, R.layout.myfollows_activity_item, null);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = getConvertView();
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            holder.ivavatar.setOnClickListener((View.OnClickListener) mContext);
            holder.ibfollow.setOnClickListener((View.OnClickListener) mContext);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ResponseBeanOtherUserInfo.FollowUserInfoBean userInfoBean = mData.get(position);

        holder.tvname.setText(userInfoBean.getNickname());
        holder.tvdesc.setText(userInfoBean.getMobile());
        holder.ivavatar.setTag(userInfoBean.getUserid());
        holder.ibfollow.setTag(userInfoBean);

        //关系:0.没关系,1.我的关注,2.我的粉丝,3.互粉
        if (userInfoBean.getUserid().equals(App.user_Id)) {
            holder.ibfollow.setVisibility(View.GONE);
        } else {
            holder.ibfollow.setVisibility(View.VISIBLE);
            switch (userInfoBean.getRelation()) {
                case "1":
                    holder.ibfollow.setImageResource(R.drawable.icon_person_hadflloow);
                    break;
                case "0":
                case "2":
                    holder.ibfollow.setImageResource(R.drawable.icon_personnal_add_selector);
                    break;
                case "3":
                    holder.ibfollow.setImageResource(R.drawable.icon_person_eachfllow);
                    break;
                default:
                    break;
            }
        }


        String path;
        if (App.sDensity >= 3) {
            path = userInfoBean.getAvatar_url().getS150();
        } else {
            path = userInfoBean.getAvatar_url().getS100();
        }

        holder.ivavatar.setImageResource(R.drawable.icon_login_photo);
        if (!TextUtils.isEmpty(path)) {
            ImageLoaderManager.getInstance().asyncLoadCircleImage(holder.ivavatar, path
                    , 0, 0);
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
