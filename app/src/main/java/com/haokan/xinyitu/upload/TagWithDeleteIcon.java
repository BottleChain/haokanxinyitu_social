package com.haokan.xinyitu.upload;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haokan.xinyitu.R;
import com.haokan.xinyitu.util.CommonUtil;
import com.haokan.xinyitu.util.DisplayUtil;
import com.haokan.xinyitu.util.ImageUtil;

public class TagWithDeleteIcon extends RelativeLayout implements View.OnClickListener {

    private final TextView mTvName;
    private final ImageView mIvDelete;
    private String mName;
    private int mTextSizePx;
    private final int mOtherW;
    private final int mTotalH;

    public TagWithDeleteIcon(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.tag_widh_delicon_layout, this, true);
        mTvName = (TextView) findViewById(R.id.tv_title);
        mIvDelete = (ImageView) findViewById(R.id.iv_delete);
        mTextSizePx = DisplayUtil.sp2px(context, 14);
        mOtherW = DisplayUtil.sp2px(context, 58); // 左右两个icon的宽，和各种padding
        mTotalH = DisplayUtil.sp2px(context, 15) + mTextSizePx; // 上下padding
        mIvDelete.setOnClickListener(this);
    }

    public void setTagName(String name) {
        mTvName.setText(name);
        mName = name;
    }

    public int getTotalWidth() {
        return mName.length() * mTextSizePx + mOtherW;
    }

    public int getTotalHeigh() {
        return mTotalH;
    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
        if (mOnDeleteClickListener != null) {
            ImageUtil.changeLight((ImageView) v, true);
            mOnDeleteClickListener.onDeleteIconClick(v);
        }
    }

    private onDeleteClickListener mOnDeleteClickListener;
    public interface onDeleteClickListener {
        void onDeleteIconClick(View view);
    }
    public void setOnDeleteClickListener(onDeleteClickListener onDeleteClickListener) {
        mOnDeleteClickListener = onDeleteClickListener;
    }

    public ImageView getDeleteIcon() {
        return mIvDelete;
    }
}
