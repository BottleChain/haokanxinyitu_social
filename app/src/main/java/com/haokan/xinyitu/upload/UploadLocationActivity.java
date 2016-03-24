package com.haokan.xinyitu.upload;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;

public class UploadLocationActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout mRlHeader;
    private ImageButton mIbBack;
    private TextView mTvUploadPickfolder;
    private EditText mEtUploadEdit;
    private TextView mTvEditBg;
    private ListView mLvLocation;

    private void assignViews() {
        mRlHeader = (RelativeLayout) findViewById(R.id.rl_header);
        mIbBack = (ImageButton) findViewById(R.id.ib_back);
        mTvUploadPickfolder = (TextView) findViewById(R.id.tv_upload_pickfolder);
        mEtUploadEdit = (EditText) findViewById(R.id.et_upload_edit);
        mTvEditBg = (TextView) findViewById(R.id.tv_edit_bg);
        mLvLocation = (ListView) findViewById(R.id.lv_location);

        mIbBack.setOnClickListener(this);
        mEtUploadEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    mTvEditBg.setVisibility(View.VISIBLE);
                } else {
                    mTvEditBg.setVisibility(View.GONE);
                }
            }
        });

        UpLoadLocationAdapter adapter = new UpLoadLocationAdapter(this);
        mLvLocation.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadlocation_activity_layout);
        assignViews();
    }

    @Override
    public void onClick(View v) {

    }
}
