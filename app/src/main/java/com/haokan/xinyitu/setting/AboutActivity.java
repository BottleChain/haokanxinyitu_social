package com.haokan.xinyitu.setting;

import android.os.Bundle;
import android.widget.TextView;

import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;
import com.haokan.xinyitu.util.CommonUtil;

/**
 * 作者：wangzixu on 2016/4/11 18:12
 * QQ：378320002
 */
public class AboutActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity_layout);

        TextView tvVersion = (TextView) findViewById(R.id.tv_version);
        tvVersion.setText(CommonUtil.getLocalVersionName(this));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
    }
}
