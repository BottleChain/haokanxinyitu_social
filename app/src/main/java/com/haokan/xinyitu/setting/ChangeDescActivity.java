package com.haokan.xinyitu.setting;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;
import com.haokan.xinyitu.base.BaseResponseBean;
import com.haokan.xinyitu.util.ConstantValues;
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.JsonUtil;
import com.haokan.xinyitu.util.ToastManager;
import com.haokan.xinyitu.util.UrlsUtil;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Created by wangzixu on 2016/4/22.
 */
public class ChangeDescActivity extends BaseActivity implements View.OnClickListener {
    private ImageButton mIbBack;
    private TextView mTvSave;
    private EditText mEtChange;
    private SharedPreferences mDefaultSharedPreferences;
    private String mOldDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changedesc_activity_layout);
        assignViews();
    }


    private void assignViews() {
        mIbBack = (ImageButton) findViewById(R.id.ib_back);
        mTvSave = (TextView) findViewById(R.id.tv_save);
        mEtChange = (EditText) findViewById(R.id.et_change);

        mIbBack.setOnClickListener(this);
        mTvSave.setOnClickListener(this);

        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mOldDesc = mDefaultSharedPreferences.getString(ConstantValues.KEY_SP_DESC, "");
        mEtChange.setText(mOldDesc);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                onBackPressed();
                break;
            case R.id.tv_save:
                saveContent();
                break;
            default:
                break;
        }
    }

    private void saveContent() {
        final String s = mEtChange.getText().toString().trim();
        if (TextUtils.isEmpty(s)) {
            ToastManager.showShort(this, "昵称不能为空");
            return;
        }

        if (s.equals(mOldDesc)) {
            onBackPressed();
            return;
        }

        String url = UrlsUtil.getModilyDescUrl(App.sessionId, s);
        Log.d("wangzixu", "ChangePersonData savePersonanData getModilyDescUrl url = " + url);

        showLoadingProgress();
        HttpClientManager.getInstance(this).getData(url, new BaseJsonHttpResponseHandler<BaseResponseBean>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, BaseResponseBean response) {
                if (response.getErr_code() != 0) {
                    ToastManager.showShort(ChangeDescActivity.this, "修改失败 = " + response.getErr_msg());
                } else {
                    Log.d("wangzixu", "ChangePersonData savePersonanData 描述修改成功");
                    ToastManager.showShort(ChangeDescActivity.this, "修改成功");
                    mDefaultSharedPreferences.edit().putString(ConstantValues.KEY_SP_DESC, s).apply();
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            onBackPressed();
                        }
                    });
                }
                disMissLoadingProgress();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, BaseResponseBean errorResponse) {
                disMissLoadingProgress();
                ToastManager.showShort(ChangeDescActivity.this, "修改失败 = onFailure");
                Log.d("wangzixu", "ChangePersonData savePersonanData 描述修改失败 = " + rawJsonData);
            }

            @Override
            protected BaseResponseBean parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return JsonUtil.fromJson(rawJsonData, BaseResponseBean.class);
            }
        });
    }

    private Dialog mLoadingProgress;
    private void showLoadingProgress() {
        if (mLoadingProgress == null) {
//            mLoadingProgress = ProgressDialog.show(this, null, null);
            mLoadingProgress = new Dialog(this, R.style.dialog);
            mLoadingProgress.setContentView(R.layout.loading_layout_progressdialog_notitle);
            mLoadingProgress.setCancelable(true);
        }
        mLoadingProgress.setContentView(R.layout.loading_layout_progressdialog_notitle);
//        new Handler().postDelayed(new Runnable() {
//            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public void run() {
//                mLoadingProgress.setContentView(R.layout.progressdialog_loading_layout_end);
//            }
//        }, 3000);
        mLoadingProgress.show();
    }


    private void disMissLoadingProgress() {
        if (mLoadingProgress != null && mLoadingProgress.isShowing()) {
            mLoadingProgress.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_in_left2right,R.anim.activity_out_left2right);
    }
}
