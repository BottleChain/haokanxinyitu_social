package com.haokan.xinyitu.tipoff;

import android.os.Bundle;

import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;

/**
 * 作者：wangzixu on 2016/4/11 18:12
 * QQ：378320002
 */
public class TipOffActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tipoff_activity_layout);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_retain, R.anim.activity_out_top2bottom);
    }
}
