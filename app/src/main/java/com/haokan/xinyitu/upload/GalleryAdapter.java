package com.haokan.xinyitu.upload;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haokan.xinyitu.R;
import com.haokan.xinyitu.main.DemoImgBean;
import com.haokan.xinyitu.util.ImageLoaderManager;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends BaseAdapter {
    private Context mContext;
    private List<DemoImgBean> mData;
    private List<DemoImgBean> mCheckedImage;
    private List<ViewHolder> mAllHolders = new ArrayList<>();
    private int mItemWidth = 0, mItemHeight = 0;

    public GalleryAdapter(Context context, List<DemoImgBean> data, List<DemoImgBean> checkedImage) {
        mContext = context;
        mData = data;
        mCheckedImage = checkedImage;
        mItemWidth = context.getResources().getDimensionPixelSize(R.dimen.pickimg_grid_img_width);
        mItemHeight = context.getResources().getDimensionPixelSize(R.dimen.pickimg_grid_img_height);
    }

    public void setData(List<DemoImgBean> data) {
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
            convertView = View.inflate(mContext, R.layout.upload_item_pickimg, null);
            holder = new ViewHolder(convertView);
            holder.mRlChecked.setOnClickListener(new GridViewCheckedOnClickListener());
            holder.mRlChecked.setTag(holder); //点击此view时用
            convertView.setTag(holder);
            mAllHolders.add(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DemoImgBean bean = mData.get(position);
        //改变选中状态时用，记录位置
//        holder.mRlChecked.setTag(position); //记录的是当前条目在mData中的位置，就是在gradview中位置
//        Object o = holder.mTvChecked.getTag(); //获取的是该显示第几个图片
//        if (o != null && o instanceof Integer) {
//            if (((int)0) >  0 &&) {
//
//            }
//        }
        holder.pos = position;
        boolean isChecked = mCheckedImage.contains(bean);
        holder.mRlChecked.setSelected(isChecked);
        if (isChecked) {
            holder.mTvChecked.setText(String.valueOf(mCheckedImage.indexOf(bean) + 1));
        } else {
            holder.mTvChecked.setText("");
        }

        holder.mImageView.setScaleType(ImageView.ScaleType.CENTER);
        holder.mImageView.setImageResource(R.drawable.icon_nopic);
        if (isChecked) {
            holder.mShadow.setVisibility(View.VISIBLE);
        }else{
            holder.mShadow.setVisibility(View.GONE);
        }

        final String path = bean.getPath();
        ImageLoaderManager.getInstance().asyncLoadImage(holder.mImageView, path, mItemWidth, mItemHeight);
        return convertView;
    }

    private class ViewHolder {
        public int pos; //此view在gradview中的位置
        public ImageView mImageView;
        public View mRlChecked;
        public View mShadow;
        public TextView mTvChecked;

        public ViewHolder(View root) {
            mImageView = (ImageView) root.findViewById(R.id.iv_pickimage_item);
            mRlChecked = root.findViewById(R.id.rl_pickimage_item);
            mTvChecked = (TextView) root.findViewById(R.id.tv_pickimage_item);
            mShadow = root.findViewById(R.id.shadow);
        }
    }

    private class GridViewCheckedOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            boolean isChecked = v.isSelected();
            if (mCheckedImage.size() >= 12 && !isChecked) {
                Toast.makeText(mContext, "最多上传12个", Toast.LENGTH_SHORT).show();
                return;
            }

            ViewHolder holder = (ViewHolder) v.getTag();
            // get the item position
            int p = holder.pos;

            DemoImgBean viewBean = mData.get(p);
            if (viewBean == null) {
                return;
            }

            //点击一下变成选中状态
            ImageView img = holder.mImageView;
            if (!isChecked) {
                v.setSelected(true);
                holder.mTvChecked.setSelected(true);
                if (!mCheckedImage.contains(viewBean)) {
                    mCheckedImage.add(viewBean);
                }
                holder.mTvChecked.setText(String.valueOf(mCheckedImage.indexOf(viewBean) + 1));
                holder.mShadow.setVisibility(View.VISIBLE);
            } else {
                v.setSelected(false);
                holder.mTvChecked.setSelected(false);
                final int begin = mCheckedImage.indexOf(viewBean);
                mCheckedImage.remove(viewBean);
                holder.mTvChecked.setText("");
                holder.mShadow.setVisibility(View.GONE);
                //改变其他选中的条目的数字
                img.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = begin; i < mCheckedImage.size(); i++) {
                            DemoImgBean pickImgBean = mCheckedImage.get(i);
                            for (int j = 0; j < mAllHolders.size(); j++) {
                                ViewHolder viewHolder = mAllHolders.get(j);
                                if (mData.get(viewHolder.pos) == pickImgBean) { //说明这个textview正在显示中，需要更新
                                    viewHolder.mTvChecked.setText(String.valueOf(i + 1));
                                }
                            }
                        }
                    }
                });
            }

            if (mCheckedCountChangeLister != null) {
                mCheckedCountChangeLister.onCheckedImageCountChange(mCheckedImage.size());
            }
        }
    }

    public void updataChecked(Handler handler) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (int j = 0; j < mAllHolders.size(); j++) {
                    ViewHolder viewHolder = mAllHolders.get(j);
                    viewHolder.mRlChecked.setSelected(false);
                    viewHolder.mTvChecked.setText("");
                    viewHolder.mShadow.setVisibility(View.GONE);
                }
                for (int i = 0; i < mCheckedImage.size(); i++) {
                    DemoImgBean pickImgBean = mCheckedImage.get(i);
                    for (int j = 0; j < mAllHolders.size(); j++) {
                        ViewHolder viewHolder = mAllHolders.get(j);
                        if (viewHolder.pos >= mData.size()) {
                            continue;
                        }
                        if (mData.get(viewHolder.pos) == pickImgBean) { //说明这个textview正在显示中，需要更新
                            viewHolder.mRlChecked.setSelected(true);
                            viewHolder.mTvChecked.setText(String.valueOf(i + 1));
                            viewHolder.mShadow.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    private OnCheckedImageCountChangeListener mCheckedCountChangeLister;
    public interface OnCheckedImageCountChangeListener {
        void onCheckedImageCountChange(int checkCount);
    }
    public void setOnCheckedImageCountChangeListener(OnCheckedImageCountChangeListener listener) {
        mCheckedCountChangeLister = listener;
    }
}
