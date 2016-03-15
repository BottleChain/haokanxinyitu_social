package com.haokan.xinyitu.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;
import com.haokan.xinyitu.login_register.Login_Register_Activity;
import com.haokan.xinyitu.util.CommonUtil;
import com.haokan.xinyitu.util.ImageUtil;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton mIvBottomBar1;
    private ImageButton mIvBottomBar2;
    private ImageButton mIvBottomBar3;
    private ImageButton mIvBottomBar4;
    private ImageButton mIvBottomBar5;
    private FrameLayout mFlContent;
    private FragmentManager mFm;
    private PopupWindow mMorePopupWindow;
    private View mMorePopBg;
    private View mMorePopContent;

    private void assignViews() {
        mIvBottomBar1 = (ImageButton) findViewById(R.id.iv_bottom_bar_1);
        mIvBottomBar2 = (ImageButton) findViewById(R.id.iv_bottom_bar_2);
        mIvBottomBar3 = (ImageButton) findViewById(R.id.iv_bottom_bar_3);
        mIvBottomBar4 = (ImageButton) findViewById(R.id.iv_bottom_bar_4);
        mIvBottomBar5 = (ImageButton) findViewById(R.id.iv_bottom_bar_5);
        mFlContent = (FrameLayout) findViewById(R.id.fl_content);

        mIvBottomBar1.setOnClickListener(this);
        mIvBottomBar2.setOnClickListener(this);
        mIvBottomBar3.setOnClickListener(this);
        mIvBottomBar4.setOnClickListener(this);
        mIvBottomBar5.setOnClickListener(this);

        mFm = getFragmentManager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_activity_layout);
        assignViews();

        setDiscoveryFragment();
    }

    private void setDiscoveryFragment() {
        mIvBottomBar1.setSelected(true);
        FragmentTransaction transaction = mFm.beginTransaction();
        Fragment discoveryFragment = new DiscoveryFragment();
        transaction.replace(R.id.fl_content, discoveryFragment);
        transaction.commitAllowingStateLoss();
    }


    @Override
    public void onClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
        if (v instanceof ImageView) {
            ImageUtil.changeLight((ImageView) v, true);
        }
        int id = v.getId();
        switch (id) {
            case R.id.iv_bottom_bar_5:
                Intent i = new Intent(MainActivity.this, Login_Register_Activity.class);
                startActivity(i);
                break;
            case R.id.ib_item0_more://条目0的更多按钮
                if (mMorePopupWindow == null) {
                    initMorePopupWindow();
                }

                if (mMorePopupWindow.isShowing()) {
                    return;
                }
                mMorePopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
                mMorePopBg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.popupwindow_bg_in));
                mMorePopContent.startAnimation(AnimationUtils.loadAnimation(this, R.anim.popupwindow_bottom_in));
                break;
            case R.id.tv_morepop_cancel:
            case R.id.pop_shadow:
                disMissMorePop();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initMorePopupWindow() {
        View v = LayoutInflater.from(this).inflate(R.layout.homepage_more_popupwindow, null);
        mMorePopupWindow = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mMorePopupWindow.setFocusable(true);
        mMorePopupWindow.setAnimationStyle(0);

        mMorePopBg = v.findViewById(R.id.pop_shadow);
        mMorePopContent = v.findViewById(R.id.rl_content);

        mMorePopBg.setOnClickListener(this);
        v.findViewById(R.id.tv_morepop_cancel).setOnClickListener(this);
        v.findViewById(R.id.tv_morepop_qq).setOnClickListener(this);
        v.findViewById(R.id.tv_morepop_qqzone).setOnClickListener(this);
        v.findViewById(R.id.tv_morepop_weibo).setOnClickListener(this);
        v.findViewById(R.id.tv_morepop_weixin).setOnClickListener(this);
        v.findViewById(R.id.tv_morepop_pengyouquan).setOnClickListener(this);
        v.findViewById(R.id.tv_morepop_jubao).setOnClickListener(this);
        v.findViewById(R.id.tv_morepop_download).setOnClickListener(this);
        v.findViewById(R.id.tv_morepop_delete).setOnClickListener(this);
    }

    private void disMissMorePop() {
        if (mMorePopupWindow != null && mMorePopupWindow.isShowing()) {
            Animation outAnim = AnimationUtils.loadAnimation(this, R.anim.popupwindow_bg_out);
            outAnim.setFillAfter(true);
            outAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            mMorePopupWindow.dismiss();
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

            });
            mMorePopBg.startAnimation(outAnim);

            Animation animation = AnimationUtils.loadAnimation(this, R.anim.popupwindow_bottom_out);
            animation.setFillAfter(true);
            mMorePopContent.startAnimation(animation);
        }
    }
}
