package com.haokan.xinyitu.setting;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
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
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.JsonUtil;
import com.haokan.xinyitu.util.SecurityUtil;
import com.haokan.xinyitu.util.ToastManager;
import com.haokan.xinyitu.util.UrlsUtil;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Created by wangzixu on 2016/4/21.
 */
public class ChangePswActivity extends BaseActivity implements View.OnClickListener {
    private ImageButton mIbBack;
    private TextView mTvTitle;
    private TextView mTvSave;
    private TextView mTvOriPsw;
    private EditText mEtOriPsw;
    private TextView mTvNewPsw;
    private EditText mEtNewPsw;
    private TextView mTvConfirmPsw;
    private EditText mEtConfirmPsw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepsw_activity_layout);
        assignViews();
    }

    private void assignViews() {
        mIbBack = (ImageButton) findViewById(R.id.ib_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvSave = (TextView) findViewById(R.id.tv_save);
        mTvOriPsw = (TextView) findViewById(R.id.tv_ori_psw);
        mEtOriPsw = (EditText) findViewById(R.id.et_ori_psw);
        mTvNewPsw = (TextView) findViewById(R.id.tv_new_psw);
        mEtNewPsw = (EditText) findViewById(R.id.et_new_psw);
        mTvConfirmPsw = (TextView) findViewById(R.id.tv_confirm_psw);
        mEtConfirmPsw = (EditText) findViewById(R.id.et_confirm_psw);

        mTvSave.setOnClickListener(this);
        mIbBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                onBackPressed();
                break;
            case R.id.tv_save:
                changePsw();
                break;
            default:
                break;
        }
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

    private void changePsw() {
        String oldPsw = mEtOriPsw.getText().toString();
        if (TextUtils.isEmpty(oldPsw)) {
            ToastManager.showShort(this, "请输入原密码");
            return;
        }

        String newPsw = mEtNewPsw.getText().toString();
        if (TextUtils.isEmpty(newPsw)) {
            ToastManager.showShort(this, "请输入新密码");
            return;
        }

        String newConfirmPsw = mEtConfirmPsw.getText().toString();
        if (TextUtils.isEmpty(newConfirmPsw)) {
            ToastManager.showShort(this, "请确认新密码");
            return;
        }

        if (!newPsw.equals(newConfirmPsw)) {
            ToastManager.showShort(this, "两次新密码不一致");
            return;
        }

        showLoadingProgress();
        RequestBeanChangePsw bean = new RequestBeanChangePsw();
        bean.setOldPasswd(SecurityUtil.haokanEncode(oldPsw));
        bean.setNewPasswd(SecurityUtil.haokanEncode(newConfirmPsw));

        String url = UrlsUtil.getModilyPasswdUrl(App.sessionId, JsonUtil.toJson(bean));
        Log.d("wangzixu", "ChangePsw url = " + url);
        HttpClientManager.getInstance(this).getData(url, new BaseJsonHttpResponseHandler<BaseResponseBean>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, BaseResponseBean response) {
                disMissLoadingProgress();
                if (response.getErr_code() == 0) {
                    ToastManager.showShort(ChangePswActivity.this, "修改成功");
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            onBackPressed();
                        }
                    });
                } else {
                    ToastManager.showShort(ChangePswActivity.this, "修改密码失败: " + response.getErr_msg());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, BaseResponseBean errorResponse) {
                disMissLoadingProgress();
                ToastManager.showShort(ChangePswActivity.this, "修改失败，onFailure!");
            }

            @Override
            protected BaseResponseBean parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return JsonUtil.fromJson(rawJsonData, BaseResponseBean.class);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
    }
}
