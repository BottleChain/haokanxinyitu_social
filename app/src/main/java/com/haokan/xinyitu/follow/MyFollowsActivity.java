package com.haokan.xinyitu.follow;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;

public class MyFollowsActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout mRlHeader;
    private ImageButton mIbBack;
    private TextView mTvTitle;
    private View mDivider1;
    private ListView mLvFollows;


    private void assignViews() {
        mRlHeader = (RelativeLayout) findViewById(R.id.rl_header);
        mIbBack = (ImageButton) findViewById(R.id.ib_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mDivider1 = findViewById(R.id.divider_1);
        mLvFollows = (ListView) findViewById(R.id.lv_follows);

        mIbBack.setOnClickListener(this);
        MyFollowsAdapter adapter = new MyFollowsAdapter(this);
        mLvFollows.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myfollows_activity_layout);
        assignViews();
        loadData();
    }

    private void loadData() {

    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
    }
}
