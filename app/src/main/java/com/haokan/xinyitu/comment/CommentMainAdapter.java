package com.haokan.xinyitu.comment;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.util.DataFormatUtil;
import com.haokan.xinyitu.util.DisplayUtil;
import com.haokan.xinyitu.util.ImageLoaderManager;

import java.util.List;

/**
 * Created by wangzixu on 2016/4/18.
 */
public class CommentMainAdapter extends BaseAdapter {
    private Context mContext;
    private List<ResponseBeanGetCommentInfo.CommentInfoBean> mData;
    private int mAuthorTextWidth1;
    private int mAuthorTextWidth2;

    public CommentMainAdapter(Context context, List<ResponseBeanGetCommentInfo.CommentInfoBean> data) {
        mContext = context;
        mData = data;
        mAuthorTextWidth1 = DisplayUtil.dip2px(context, 120);
        mAuthorTextWidth2 = DisplayUtil.dip2px(context, 250);
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
            convertView = View.inflate(mContext, R.layout.commentmain_activity_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            holder.ivavatar.setOnClickListener((View.OnClickListener) mContext);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ResponseBeanGetCommentInfo.CommentInfoBean infoBean = mData.get(position);

        holder.tvAuthor.setText(infoBean.getUser_nickname());
        holder.tvdesc.setText(infoBean.getContent());
        holder.ivavatar.setTag(infoBean.getUser_id());
        holder.tvTime.setText(DataFormatUtil.getCustomFormatTime(Long.valueOf(infoBean.getCreate_time()) * 1000));

        if (infoBean.getTo_user_id() != 0) {
            holder.tvReplay.setVisibility(View.VISIBLE);
            holder.tvAuthorTo.setVisibility(View.VISIBLE);
            holder.tvAuthor.setMaxWidth(mAuthorTextWidth1);
            holder.tvAuthorTo.setText(infoBean.getTo_user_nickname());
        } else {
            holder.tvReplay.setVisibility(View.INVISIBLE);
            holder.tvAuthorTo.setVisibility(View.INVISIBLE);
            holder.tvAuthor.setMaxWidth(mAuthorTextWidth2);
        }

        String path;
        if (App.sDensity >= 3) {
            path = infoBean.getUser_avatar_url().getS150();
        } else {
            path = infoBean.getUser_avatar_url().getS100();
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
        public final TextView tvAuthor;
        public final TextView tvReplay;
        public final TextView tvAuthorTo;
        public final TextView tvdesc;
        public final TextView tvTime;
        public final View root;

        public ViewHolder(View root) {
            ivavatar = (ImageView) root.findViewById(R.id.iv_avatar);
            tvAuthor = (TextView) root.findViewById(R.id.tv_author);
            tvReplay = (TextView) root.findViewById(R.id.tv_replay);
            tvAuthorTo = (TextView) root.findViewById(R.id.tv_author_to);
            tvdesc = (TextView) root.findViewById(R.id.tv_desc);
            tvTime = (TextView) root.findViewById(R.id.tv_time);
            this.root = root;
        }
    }
}
