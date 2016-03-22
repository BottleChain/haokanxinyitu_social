package com.haokan.xinyitu.bigimgbrowse;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.haokan.xinyitu.customView.ZoomImageView;
import com.haokan.xinyitu.main.DemoImgBean;
import com.haokan.xinyitu.util.ImageLoaderManager;

import java.util.List;

public class BigImgViewPagerAdapter extends PagerAdapter {
    private List<? extends DemoImgBean> mData;
    private Context mContext;
    private List<ZoomImageView> mViewsList;
    //private int mScreenW, mScreenH;

    public BigImgViewPagerAdapter(List<? extends DemoImgBean> data, Context context, List<ZoomImageView> viewsList) {
        mData = data;
        mContext = context;
        mViewsList = viewsList;
//        mScreenH = context.getResources().getDisplayMetrics().heightPixels;
//        mScreenW = context.getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int viewPosition = position % mViewsList.size();
        final ZoomImageView imageView = mViewsList.get(viewPosition);
        DemoImgBean imageViewBean = mData.get(position);
        if (imageView.getParent() != null) {
            ((ViewGroup)imageView.getParent()).removeView(imageView);
            imageView.setImageBitmap(null);
        }
        container.addView(imageView);

        final String path = imageViewBean.getPath();
        ImageLoaderManager.getInstance().loadLocalPic(path, imageView, null);

        return imageView;
    }
}
