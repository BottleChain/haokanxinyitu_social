package com.haokan.xinyitu.notice;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haokan.xinyitu.R;
import com.haokan.xinyitu.util.DataFormatUtil;
import com.haokan.xinyitu.util.ImageLoaderManager;

import java.util.List;

/**
 * Created by wangzixu on 2016/4/28.
 */
public class NoticeMainAdapter extends BaseAdapter {
    public Activity mContext;
    private List<ResponseBeanNoticeContentList.NoticeContentBean> mData;

    public NoticeMainAdapter(Activity context, List<ResponseBeanNoticeContentList.NoticeContentBean> data) {
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
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        int type = 0;
        switch (mData.get(position).getType()) {
            case 1:
                type = 0; //关注
                break;
            case 2:
            case 3:
                type = 1;//评论
                break;
            case 4:
                type = 2;//赞
                break;
            default:
                break;
        }
        return type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ResponseBeanNoticeContentList.NoticeContentBean bean = mData.get(position);
        switch (getItemViewType(position)) {
            case 0: //关注
                ViewHolderGuanZhu guanZhuHolder;
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.notice_main_activity_item_guanzhu, null);
                    guanZhuHolder = new ViewHolderGuanZhu(convertView);
                    convertView.setTag(guanZhuHolder);
                } else {
                    guanZhuHolder = (ViewHolderGuanZhu) convertView.getTag();
                }
                guanZhuHolder.tvtitle.setText(bean.getText());
                guanZhuHolder.tvtime.setText(DataFormatUtil.format(bean.getTimestamp() * 1000));
                break;
            case 1://评论
                ViewHolderPinglun pinglunHolder;
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.notice_main_activity_item_pinglun, null);
                    pinglunHolder = new ViewHolderPinglun(convertView);
                    convertView.setTag(pinglunHolder);
                } else {
                    pinglunHolder = (ViewHolderPinglun) convertView.getTag();
                }
                pinglunHolder.tvtitle.setText(bean.getText());
                pinglunHolder.tvsub.setText(bean.getComment_text());
                pinglunHolder.tvtime.setText(DataFormatUtil.format(bean.getTimestamp() * 1000));
                ImageLoaderManager.getInstance().simpleDisplayImg(pinglunHolder.ivthumb, bean.getImg());
                break;
            case 2://赞
                ViewHolderZan zanHolder;
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.notice_main_activity_item_zan, null);
                    zanHolder = new ViewHolderZan(convertView);
                    convertView.setTag(zanHolder);
                } else {
                    zanHolder = (ViewHolderZan) convertView.getTag();
                }
                zanHolder.tvtitle.setText(bean.getText());
                zanHolder.tvtime.setText(DataFormatUtil.format(bean.getTimestamp() * 1000));
                ImageLoaderManager.getInstance().simpleDisplayImg(zanHolder.ivthumb, bean.getImg());
                break;
            default:
                break;
        }
        return convertView;
    }

    private class ViewHolderZan {
        public final ImageView ivthumb;
        public final TextView tvtitle;
        public final TextView tvtime;
        public final View root;

        public ViewHolderZan(View root) {
            ivthumb = (ImageView) root.findViewById(R.id.iv_thumb);
            tvtitle = (TextView) root.findViewById(R.id.tv_title);
            tvtime = (TextView) root.findViewById(R.id.tv_time);
            this.root = root;
        }
    }

    private class ViewHolderPinglun {
        public final ImageView ivthumb;
        public final TextView tvtitle;
        public final TextView tvtime;
        public final TextView tvsub;
        public final View root;

        public ViewHolderPinglun(View root) {
            ivthumb = (ImageView) root.findViewById(R.id.iv_thumb);
            tvtitle = (TextView) root.findViewById(R.id.tv_title);
            tvsub = (TextView) root.findViewById(R.id.tv_sub);
            tvtime = (TextView) root.findViewById(R.id.tv_time);
            this.root = root;
        }
    }

    private class ViewHolderGuanZhu {
        public final TextView tvtitle;
        public final TextView tvtime;
        public final View root;

        public ViewHolderGuanZhu(View root) {
            tvtitle = (TextView) root.findViewById(R.id.tv_title);
            tvtime = (TextView) root.findViewById(R.id.tv_time);
            this.root = root;
        }
    }
}
