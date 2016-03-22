package com.haokan.xinyitu.bigimgbrowse;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;
import com.haokan.xinyitu.customView.ZoomImageView;
import com.haokan.xinyitu.customView.ZoomImageViewPager;
import com.haokan.xinyitu.main.DemoImgBean;

import java.util.ArrayList;

public class BigImgBrowseActivity extends BaseActivity implements View.OnClickListener, ZoomImageViewPager.onCustomClikListener {
    private ZoomImageViewPager mZoomImageViewPager;
    public static final int VIEWPAGE_CACHE_COUNT = 2;
    public static final String EXTRA_INIT_POSITION = "initpos";
    public static final String EXTRA_IMG_DATA = "imgdata";
    private ArrayList<ZoomImageView> mViewsList;
    private Handler mHandler = new Handler();
    /**
     * 用来展示那个页面的大图，1，timeLine点击预览大图，2，上传预览大图，3，上传编辑页大图
     */
    public static final String EXTRA_USED = "used";
    private View mTopBar;
    private TextView mTvUpLoadImgEditTitle;
    /**
     * 用来展示那个页面的大图，1，timeLine点击预览大图，2，上传预览大图，3，上传编辑页大图
     */
    private int mUsed_Mode;
    private ImageButton mIbUpLoadChecked;
    private ArrayList<DemoImgBean> mData;
    private ArrayList<DemoImgBean> mCheckedImgs;
    private PopupWindow mPopupWindow;
    private View mPopBg;
    private View mPopContent;

    private void assignViews() {
        mUsed_Mode = getIntent().getIntExtra(EXTRA_USED, 1);
        if (mUsed_Mode == 1) {
            ImageButton ibBigimgClose = (ImageButton) findViewById(R.id.ib_bigimg_close);
            ibBigimgClose.setOnClickListener(this);
            mTopBar = ibBigimgClose;
        } else if (mUsed_Mode == 2) {
            mTopBar = findViewById(R.id.rl_upload_preview);
            ImageButton ibUpLoadBack = (ImageButton) mTopBar.findViewById(R.id.ib_upload_preview_back);
            mIbUpLoadChecked = (ImageButton) mTopBar.findViewById(R.id.ib_upload_preview_checked);
            ibUpLoadBack.setOnClickListener(this);
            mIbUpLoadChecked.setOnClickListener(this);
        } else if (mUsed_Mode == 3) {
            mTopBar = findViewById(R.id.rl_upload_imgedit);
            ImageButton ibUpLoadBack = (ImageButton) mTopBar.findViewById(R.id.ib_upload_imgedit_back);
            ImageButton delete = (ImageButton) mTopBar.findViewById(R.id.ib_upload_imgedit_delete);
            ibUpLoadBack.setOnClickListener(this);
            delete.setOnClickListener(this);

            mTvUpLoadImgEditTitle = (TextView) mTopBar.findViewById(R.id.tv_upload_imgedit_title);
        } else {
            throw new RuntimeException("请正确设置大图展示页的mUsed_Mode！");
        }

        mTopBar.setVisibility(View.VISIBLE);

        mZoomImageViewPager = (ZoomImageViewPager) findViewById(R.id.zivp_bigimg);
        mZoomImageViewPager.setOnCustomClikListener(this);

        //初始化viewpager相关
        int size = VIEWPAGE_CACHE_COUNT * 2 + 1;
        mViewsList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ZoomImageView view = new ZoomImageView(this);
            view.setId(R.id.iv_for_bigimg);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view.setBackgroundColor(0xFF000000);
            view.setScaleType(ImageView.ScaleType.MATRIX);
            view.setScaleMode(2); //宽边符合宽高的
            view.setParentCanScroll(true);
            //view.setOnClickListener(this);
//            view.setFocusable(false);
//            view.setFocusableInTouchMode(false);
            mViewsList.add(view);
        }
        Intent intent = getIntent();
        int position = intent.getIntExtra(EXTRA_INIT_POSITION, 0);

        if (mUsed_Mode == 1) {
            mData = intent.getParcelableArrayListExtra(EXTRA_IMG_DATA);
        } else if (mUsed_Mode == 2){
            App app = (App) getApplication();
            mData = app.getBigImgData();
            mCheckedImgs = app.getCheckedImgs();
        } else if (mUsed_Mode == 3) {
            App app = (App) getApplication();
            mData = app.getCheckedImgs();
            //mTvUpLoadImgEditTitle.setText(position + "/" + mData.size());
            //mCheckedImgs = app.getCheckedImgs();
        }

        BigImgViewPagerAdapter mAdapter = new BigImgViewPagerAdapter(mData, getApplicationContext(), mViewsList);
        mZoomImageViewPager.setAdapter(mAdapter);
        mZoomImageViewPager.setInitCurrentItem(position);
        handleItem(position);
        mZoomImageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                handleItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private DemoImgBean mCurrentBean;
    private void handleItem(int position) {
        int viewPosition = position % mViewsList.size();
        ZoomImageView imgView = mViewsList.get(viewPosition);
        mZoomImageViewPager.setZoomImageView(imgView);

        mCurrentBean = mData.get(position);
        if (mUsed_Mode == 2) { //需要正确处理右上角的是否选中按钮
            mIbUpLoadChecked.setSelected(mCheckedImgs.contains(mCurrentBean));
        } else if (mUsed_Mode == 3) {
            mTvUpLoadImgEditTitle.setText((position+1) + "/" + mData.size());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bigimgbrowse_activity_layout);
        assignViews();
    }

    final long[] hits = new long[2];
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ib_bigimg_close:
            case R.id.ib_upload_imgedit_back:
            case R.id.ib_upload_preview_back:
                onBackPressed();
                break;
            case R.id.ib_upload_preview_checked:
                boolean checked = mCheckedImgs.contains(mCurrentBean);
                if (checked) {
                    mCheckedImgs.remove(mCurrentBean);
                } else {
                    mCheckedImgs.add(mCurrentBean);
                }
                v.setSelected(!checked);
                break;
            case R.id.ib_upload_imgedit_delete:
                if (mPopupWindow == null) {
                    initDeletePopupWindow();
                }

                if (mPopupWindow.isShowing()) {
                    return;
                }
                mPopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
                mPopBg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.popupwindow_bg_in));
                mPopContent.startAnimation(AnimationUtils.loadAnimation(this, R.anim.popupwindow_bottom_in));
                break;
            case R.id.pop_shadow:
            case R.id.tv_pop_cancel:
                disMissPop(null);
                break;
            case R.id.tv_popup_delete:
                if (mUsed_Mode == 3 && mData != null) {
                    mData.remove(mCurrentBean);
                    disMissPop(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });
                }
                break;
            default:
                break;
        }
    }

    private void disMissPop(final Runnable endRunnable) {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            Animation outAnim = AnimationUtils.loadAnimation(this, R.anim.popupwindow_bg_out);
            outAnim.setFillAfter(true);
            outAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mPopupWindow.dismiss();
                        }
                    });
                    if (endRunnable != null) {
                        handler.post(endRunnable);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

            });
            mPopBg.startAnimation(outAnim);

            Animation animation = AnimationUtils.loadAnimation(this, R.anim.popupwindow_bottom_out);
            animation.setFillAfter(true);
            mPopContent.startAnimation(animation);
        }
    }

    private void initDeletePopupWindow() {
        View v = LayoutInflater.from(this).inflate(R.layout.upload_bigimg_edit_popupwindow, null);
        mPopupWindow = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setFocusable(false);
        mPopupWindow.setAnimationStyle(0);

        mPopBg = v.findViewById(R.id.pop_shadow);
        mPopContent = v.findViewById(R.id.ll_morepop_content);

        mPopBg.setOnClickListener(this);
        v.findViewById(R.id.tv_pop_cancel).setOnClickListener(this);
        v.findViewById(R.id.tv_popup_delete).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            disMissPop(null);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        switch (mUsed_Mode) {
            case 1:
                super.finish();
                overridePendingTransition(R.anim.activity_retain, R.anim.activity_fade_out);
                break;
            case 2:
                super.finish();
                overridePendingTransition(R.anim.activity_retain, R.anim.activity_fade_out);
                break;
            case 3:
                super.finish();
                overridePendingTransition(R.anim.activity_retain, R.anim.activity_fade_out);
                break;
            default:
                super.finish();
                overridePendingTransition(R.anim.activity_retain, R.anim.activity_fade_out);
                break;
        }
    }

    private int mTopBarBottom;
    private Runnable onSingleClikRun = new Runnable() {
        @Override
        public void run() {
            if (mTopBar.getVisibility() == View.VISIBLE) {
                //开始隐藏动画
                mTopBarBottom = mTopBar.getBottom();
                final ValueAnimator anim = ValueAnimator.ofFloat(0, mTopBarBottom);
                anim.setDuration(200);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mTopBar.setVisibility(View.GONE);
                    }
                });
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Float i = -(Float) animation.getAnimatedValue();
                        mTopBar.setTranslationY(i);
                    }
                });
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        anim.start();
                    }
                });
            } else {
                //开始显示动画
                mTopBar.setVisibility(View.VISIBLE);
                //mTopBarBottom = mIbBigimgClose.getBottom();
                final ValueAnimator anim = ValueAnimator.ofFloat(mTopBarBottom, 0);
                anim.setDuration(200);
                //anim.setInterpolator(Login_Register_SwAnim_Manager.sInterpolatorIn);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Float i = -(Float) animation.getAnimatedValue();
                        mTopBar.setTranslationY(i);
                    }
                });
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        anim.start();
                    }
                });
            }
        }
    };

    @Override
    public void onCustomClick(ZoomImageView v) { //点击事件
        // src 要拷贝的源数组
        // srcPos 从源数组的哪个位子开始拷贝
        // dst 要拷贝的目标数组
        // dstPos 目标数组的哪个位子开始拷贝
        // length 要拷贝多少个元素.
        // 思路就是使数组每次点击左移一位，判断最后一位和第一位的时间差
        Log.d("wangzixu", "zivp_bigimg on click");
        System.arraycopy(hits, 1, hits, 0, hits.length - 1);
        hits[hits.length - 1] = SystemClock.uptimeMillis();
        if ((hits[hits.length - 1] - hits[0]) <= 310) {
            mHandler.removeCallbacks(onSingleClikRun);
            v.onDubleClick();
        } else {
            mHandler.postDelayed(onSingleClikRun, 320);
        }
    }
}
