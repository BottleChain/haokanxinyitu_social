package com.haokan.xinyitu.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;
import com.haokan.xinyitu.clipphoto.ClipPhotoActivity;
import com.haokan.xinyitu.clipphoto.ClipPhotoManager;
import com.haokan.xinyitu.login_register.ResponseBeanUploadPh;
import com.haokan.xinyitu.util.ConstantValues;
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.ImageLoaderManager;
import com.haokan.xinyitu.util.JsonUtil;
import com.haokan.xinyitu.util.ToastManager;
import com.haokan.xinyitu.util.UrlsUtil;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.io.File;

import cz.msebera.android.httpclient.Header;

/**
 * Created by wangzixu on 2016/4/20.
 */
public class ChangeDataMainActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout mRlHeader;
    private ImageButton mIbBack;
    private RelativeLayout mRlSettingAvatar;
    private ImageView mIvAvatar;
    private TextView mTvEdit;
    private RelativeLayout mRlChangeNickname;
    private TextView mTvNickname;
    private RelativeLayout mRlChangeDesc;
    private TextView mTvDesc;
    private RelativeLayout mRlChangeGetlocation;
    private Switch mStLocation;
    private RelativeLayout mRlChangePsw;
    private RelativeLayout mRlBindPhone;
    private TextView mTvBindPhone;
    private RelativeLayout mRlBindWeixin;
    private TextView mTvBindWeixin;
    private RelativeLayout mRlBindWeibo;
    private TextView mTvBindWeibo;
    private RelativeLayout mRlBindQq;
    private TextView mTvBindQq;
    private String mNewAvatarPath;
    private String mOldNickname;
    private String mOldDesc;
    private SharedPreferences mDefaultSharedPreferences;
    private String mOldAvatarUrl;

    private void assignViews() {
        mRlHeader = (RelativeLayout) findViewById(R.id.rl_header);
        mIbBack = (ImageButton) findViewById(R.id.ib_back);
        mRlSettingAvatar = (RelativeLayout) findViewById(R.id.rl_setting_avatar);
        mIvAvatar = (ImageView) findViewById(R.id.iv_avatar);
        mTvEdit = (TextView) findViewById(R.id.tv_edit);
        mRlChangeNickname = (RelativeLayout) findViewById(R.id.rl_change_nickname);
        mTvNickname = (TextView) findViewById(R.id.tv_nickname_1);
        mRlChangeDesc = (RelativeLayout) findViewById(R.id.rl_change_desc);
        mTvDesc = (TextView) findViewById(R.id.tv_desc_1);
        mRlChangeGetlocation = (RelativeLayout) findViewById(R.id.rl_change_getlocation);
        mStLocation = (Switch) findViewById(R.id.st_location);
        mRlChangePsw = (RelativeLayout) findViewById(R.id.rl_change_psw);
        mRlBindPhone = (RelativeLayout) findViewById(R.id.rl_bind_phone);
        mTvBindPhone = (TextView) findViewById(R.id.tv_bind_phone);
        mRlBindWeixin = (RelativeLayout) findViewById(R.id.rl_bind_weixin);
        mTvBindWeixin = (TextView) findViewById(R.id.tv_bind_weixin);
        mRlBindWeibo = (RelativeLayout) findViewById(R.id.rl_bind_weibo);
        mTvBindWeibo = (TextView) findViewById(R.id.tv_bind_weibo);
        mRlBindQq = (RelativeLayout) findViewById(R.id.rl_bind_qq);
        mTvBindQq = (TextView) findViewById(R.id.tv_bind_qq);

        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mOldAvatarUrl = mDefaultSharedPreferences.getString(ConstantValues.KEY_SP_AVATAR_URL, "");
        String phonenum = mDefaultSharedPreferences.getString(ConstantValues.KEY_SP_PHONENUM, "");
        mTvBindPhone.setText(phonenum);
        mTvBindPhone.setSelected(true);
        if (!TextUtils.isEmpty(mOldAvatarUrl)) {
            ImageLoaderManager.getInstance().asyncLoadCircleImage(mIvAvatar, mOldAvatarUrl
                    , mIvAvatar.getWidth(), mIvAvatar.getHeight());
        }

        mRlSettingAvatar.setOnClickListener(this);
        mRlChangePsw.setOnClickListener(this);
        mRlChangeNickname.setOnClickListener(this);
        mRlChangeDesc.setOnClickListener(this);
        mIbBack.setOnClickListener(this);
        mStLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("wangzixu", "ChangePersonnalData onCheckedChanged isChecked = " + isChecked);
                mDefaultSharedPreferences.edit().putBoolean(ConstantValues.KEY_SP_GETLOCATION, isChecked);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mOldNickname = mDefaultSharedPreferences.getString(ConstantValues.KEY_SP_NICKNAME, "应该从服务器获取的昵称");
        mOldDesc = mDefaultSharedPreferences.getString(ConstantValues.KEY_SP_DESC, "");
        mTvNickname.setText(mOldNickname);
        mTvDesc.setText(mOldDesc);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepersonaldata_activity_layout);
        assignViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                onBackPressed();
                break;
            case R.id.rl_setting_avatar:
                ClipPhotoManager.startPickImg(ChangeDataMainActivity.this, ClipPhotoManager.REQUEST_SELECT_PICK);
                break;
            case R.id.rl_change_nickname:
                Intent intent1 = new Intent(ChangeDataMainActivity.this, ChangeNickNameActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            case R.id.rl_change_desc:
                Intent intent2 = new Intent(ChangeDataMainActivity.this, ChangeDescActivity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            case R.id.rl_change_psw: //改密
                Intent intent = new Intent(ChangeDataMainActivity.this, ChangePswActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_in_left2right,R.anim.activity_out_left2right);
    }

    private void saveAvatar() {
        if (!TextUtils.isEmpty(mNewAvatarPath)) { //更改了头像
            String url = UrlsUtil.getUploadAvatarUrl(App.sessionId);
            Log.d("wangzixu", "ChangePersonData savePersonanData upavatar url = " + url);
            File file = new File(mNewAvatarPath);
            if (file.exists() && file.length() > 0) {
                HttpClientManager.getInstance(ChangeDataMainActivity.this).upLoadAvatarFile(url, file, new BaseJsonHttpResponseHandler<ResponseBeanUploadPh>() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanUploadPh response) {
                        //ToastManager.showShort(Login_Register_Activity.this, "上传成功 = " + response.getErr_msg());
                        if (response.getErr_code() == 0) {
                            Log.d("wangzixu", "ChangePersonData savePersonanData 头像修改成功");
                            ToastManager.showShort(ChangeDataMainActivity.this, "头像上传成功");
                            mDefaultSharedPreferences.edit().putString(ConstantValues.KEY_SP_AVATAR_URL
                                    , ImageDownloader.Scheme.FILE.wrap(mNewAvatarPath))
                                    .apply(); //保存头像信息
                        } else {
                            ToastManager.showShort(ChangeDataMainActivity.this, "头像上传失败 = " + response.getErr_msg());
                            if (!TextUtils.isEmpty(mOldAvatarUrl)) {
                                ImageLoaderManager.getInstance().asyncLoadCircleImage(mIvAvatar, mOldAvatarUrl
                                        , mIvAvatar.getWidth(), mIvAvatar.getHeight());
                            } else {
                                mIvAvatar.setImageResource(R.drawable.icon_login_photo);
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanUploadPh errorResponse) {
                        ToastManager.showShort(ChangeDataMainActivity.this, rawJsonData);
                        Log.i("wangzixu", "ChangePersonData savePersonanData 头像上传失败 = " + rawJsonData);
                        if (!TextUtils.isEmpty(mOldAvatarUrl)) {
                            ImageLoaderManager.getInstance().asyncLoadCircleImage(mIvAvatar, mOldAvatarUrl
                                    , mIvAvatar.getWidth(), mIvAvatar.getHeight());
                        } else {
                            mIvAvatar.setImageResource(R.drawable.icon_login_photo);
                        }
                    }

                    @Override
                    protected ResponseBeanUploadPh parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                        Log.d("wangzixu", "ChangePersonData savePersonanData rawJsonData = " + "[]" + rawJsonData);
                        return JsonUtil.fromJson(rawJsonData, ResponseBeanUploadPh.class);
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == ClipPhotoManager.REQUEST_SELECT_PICK && data != null ) {
            Log.d("wangzixu", "onActivityResult data = " + data.getData());
            String path = ClipPhotoManager.onPickImgResult(this, data);
            if (!TextUtils.isEmpty(path)) {
                Intent intent = new Intent(ChangeDataMainActivity.this, ClipPhotoActivity.class);
                intent.putExtra(ClipPhotoManager.PICK_IMG_PATH, path);
                startActivityForResult(intent, ClipPhotoManager.REQUEST_CLIP_PIC);
            }
        } else if (requestCode == ClipPhotoManager.REQUEST_CLIP_PIC && data != null) {
            mNewAvatarPath = data.getStringExtra(ClipPhotoManager.KEY_CLIP_PATH);
            if (!TextUtils.isEmpty(mNewAvatarPath)) {
                ImageLoaderManager.getInstance().asyncLoadCircleImage(mIvAvatar, ImageDownloader.Scheme.FILE.wrap(mNewAvatarPath)
                        , mIvAvatar.getWidth(), mIvAvatar.getHeight());
                saveAvatar();
            }
        }
    }
}
