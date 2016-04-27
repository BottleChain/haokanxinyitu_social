package com.haokan.xinyitu.setting;

import android.os.Bundle;
import android.view.View;

import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;

/**
 * 作者：wangzixu on 2016/4/11 18:12
 * QQ：378320002
 */
public class SuggestionActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggestion_activity_layout);
        findViewById(R.id.ib_back).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
