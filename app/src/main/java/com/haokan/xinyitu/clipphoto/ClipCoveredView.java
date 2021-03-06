package com.haokan.xinyitu.clipphoto;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.haokan.xinyitu.R;

public class ClipCoveredView extends View {
    private int mRadius = 300;
    private int mBgColor = 0xcc000000;
    private int mCenterX, mCenterY;
    private int mScreenW, mScreenH;
    private Paint mCirclePaint;
    private Rect mBoundRect;

    public ClipCoveredView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mScreenW = getResources().getDisplayMetrics().widthPixels;
        mScreenH = getResources().getDisplayMetrics().heightPixels;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClipCoverView, defStyleAttr, 0);
        mRadius = a.getDimensionPixelSize(R.styleable.ClipCoverView_ccv_radius, 400);
        mBgColor = a.getColor(R.styleable.ClipCoverView_ccv_bg_color, 0x7f000000);
//        mCenterX = a.getDimensionPixelSize(R.styleable.ClipCoverView_ccv_centerX, mScreenW / 2);
//        mCenterY = a.getDimensionPixelSize(R.styleable.ClipCoverView_ccv_centerY, mScreenH / 2 - DisplayUtil.dip2px(context, 30));        mCenterX = a.getDimensionPixelSize(R.styleable.ClipCoverView_ccv_centerX, mScreenW / 2);
        mCenterX = mScreenW / 2;
        mCenterY = mScreenH / 2;
        a.recycle();

        init();
    }

    public ClipCoveredView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipCoveredView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(0xff000000);
        PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        mCirclePaint.setXfermode(xfermode);
        mBoundRect = new Rect(mCenterX - mRadius, mCenterY - mRadius, mCenterX + mRadius, mCenterY + mRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //原理是先在屏幕上画个暗背景，再在中间画个圆，paint设置了遮罩，重叠的部分挖掉，
        //在硬件加速的层上无法使用带透明度或者抠图的遮罩，所以需要自己搞个图层在自己的图层上画
        int scaveCount = canvas.saveLayer(0,0,mScreenW,mScreenH,null, Canvas.ALL_SAVE_FLAG);
        canvas.drawColor(mBgColor);
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mCirclePaint);
        canvas.restoreToCount(scaveCount);
    }

    public Rect getBoundRect() {
        return mBoundRect;
    }
}
