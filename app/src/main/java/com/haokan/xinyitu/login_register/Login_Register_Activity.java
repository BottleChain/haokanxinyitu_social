package com.haokan.xinyitu.login_register;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;
import com.haokan.xinyitu.base.BaseBean;
import com.haokan.xinyitu.clipphoto.ClipPhotoActivity;
import com.haokan.xinyitu.clipphoto.ClipPhotoManager;
import com.haokan.xinyitu.main.MainActivity;
import com.haokan.xinyitu.util.CommonUtil;
import com.haokan.xinyitu.util.ConstantValues;
import com.haokan.xinyitu.util.HttpClientManager;
import com.haokan.xinyitu.util.ImageLoaderManager;
import com.haokan.xinyitu.util.ImageUtil;
import com.haokan.xinyitu.util.JsonUtil;
import com.haokan.xinyitu.util.SecurityUtil;
import com.haokan.xinyitu.util.ToastManager;
import com.haokan.xinyitu.util.UrlsUtil;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class Login_Register_Activity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout mRlContent;
    private ImageView mIvBg;
    private ImageButton mClose;
    private ImageButton mBack;
    private TextView mTvPersonalData;
    private TextView mTvSkip;
    private ImageView mWelcome;
    private TextView mWelcomeSub;
    private ViewStub mStubRegister;
    private ViewStub mStubLogin;
    private ViewStub mStubLoginSms;
    private ViewStub mStubPersondata;
    private View mLayoutRegister;
    private View mLayoutLogin;
    private View mLayoutLoginSms;
    private View mLayoutPersondata;
    private int mScreenW;
    private Handler mHandler = new Handler();
    private long mSwAnimDelta = 80;
    private CountDownTimer mCountDownTimer;
    private String mClipedHpPath;
    private SharedPreferences mDefaultSharedPreferences;
    private int[] mBgIds = {R.drawable.bg_login_1, R.drawable.bg_login_2, R.drawable.bg_login_3};

    private void assignViews() {
        mRlContent = (RelativeLayout) findViewById(R.id.rl_content);
        mIvBg = (ImageView) findViewById(R.id.iv_bg);
        int i = new Random().nextInt(3);
        mIvBg.setImageResource(mBgIds[i]);
        //mIvBg.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        mClose = (ImageButton) findViewById(R.id.close);
        mBack = (ImageButton) findViewById(R.id.back);
        mTvPersonalData = (TextView) findViewById(R.id.tv_1);
        mTvSkip = (TextView) findViewById(R.id.tv_4);
        mWelcome = (ImageView) findViewById(R.id.tv_2);
        mWelcomeSub = (TextView) findViewById(R.id.tv_3);
        mStubRegister = (ViewStub) findViewById(R.id.layout_register);
        mStubLogin = (ViewStub) findViewById(R.id.layout_login);
        mStubLoginSms = (ViewStub) findViewById(R.id.layout_login_sms);
        mStubPersondata = (ViewStub) findViewById(R.id.layout_persondata);

        mBack.setOnClickListener(this);
        mClose.setOnClickListener(this);
        mTvSkip.setOnClickListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_register_activity_layout);

        mScreenW = getResources().getDisplayMetrics().widthPixels;
        assignViews();
        showRegisterView();
    }

    //注册模块及其用的view
    private EditText mEtRegisterPhoneNum;
    private EditText mEtRegisterVerfyCode;
    private EditText mEtRegisterInviteCode;
    private TextView mTvRegisterGetVerfyCode;
    private TextView mTvRegisterRegister;
    private TextView mTvRegisterLogin;
    private void showRegisterView() {
        if (mLayoutRegister == null) {
            mLayoutRegister = mStubRegister.inflate();
            mEtRegisterPhoneNum = (EditText) mLayoutRegister.findViewById(R.id.et_1);
            mEtRegisterVerfyCode = (EditText) mLayoutRegister.findViewById(R.id.et_2);
            mEtRegisterInviteCode = (EditText) mLayoutRegister.findViewById(R.id.et_3);
            mTvRegisterGetVerfyCode = (TextView) mLayoutRegister.findViewById(R.id.tv_3);
            mTvRegisterRegister = (TextView) mLayoutRegister.findViewById(R.id.tv_4);
            mTvRegisterLogin = (TextView) mLayoutRegister.findViewById(R.id.tv_6);

            View.OnClickListener listener = new RegisterClickListener();
            mTvRegisterGetVerfyCode.setOnClickListener(listener);
            mTvRegisterRegister.setOnClickListener(listener);
            mTvRegisterLogin.setOnClickListener(listener);
        } else {
            mLayoutRegister.setVisibility(View.VISIBLE);
        }
    }

    //登录模块及其用的view
    private EditText mEtLoginPhoneNum;
    private EditText mEtLoginPassword;
    private TextView mTvLoginLogin;
    private TextView mTvLoginLoginSms;
    private void showLoginView() {
        if (mLayoutLogin == null) {
            mLayoutLogin = mStubLogin.inflate();
            mEtLoginPhoneNum = (EditText) mLayoutLogin.findViewById(R.id.et_1);
            mEtLoginPassword = (EditText) mLayoutLogin.findViewById(R.id.et_2);
            mTvLoginLogin = (TextView) mLayoutLogin.findViewById(R.id.tv_4);
            mTvLoginLoginSms = (TextView) mLayoutLogin.findViewById(R.id.tv_6);

            View.OnClickListener listener = new LoginClickListener();
            mTvLoginLogin.setOnClickListener(listener);
            mTvLoginLoginSms.setOnClickListener(listener);
        } else {
            mLayoutLogin.setVisibility(View.VISIBLE);
        }
    }

    //短信认证登录模块及其用的view
    private EditText mEtLoginSmsPhoneNum;
    private EditText mEtLoginSmsVerfyCode;
    private TextView mTvLoginSmsGetVerfyCode;
    private TextView mTvLoginSmsLogin;
    private void showLoginSmsView() {
        if (mLayoutLoginSms == null) {
            mLayoutLoginSms = mStubLoginSms.inflate();
            mEtLoginSmsPhoneNum = (EditText) mLayoutLoginSms.findViewById(R.id.et_1);
            mEtLoginSmsVerfyCode = (EditText) mLayoutLoginSms.findViewById(R.id.et_2);
            mTvLoginSmsGetVerfyCode = (TextView) mLayoutLoginSms.findViewById(R.id.tv_3);
            mTvLoginSmsLogin = (TextView) mLayoutLoginSms.findViewById(R.id.tv_4);

            View.OnClickListener listener = new LoginSmsClickListener();
            mTvLoginSmsGetVerfyCode.setOnClickListener(listener);
            mTvLoginSmsLogin.setOnClickListener(listener);
        } else {
            mLayoutLoginSms.setVisibility(View.VISIBLE);
        }
    }

    //填写个人资料模块及其用的view
    private ImageView mIvPersonDataPhoto;
    private EditText mEtPersonDataNickName;
    private EditText mEtPersonDataPassWord;
    private TextView mTvPersonDataConfirm;
    private void showPersonDataView() {
        if (mLayoutPersondata == null) {
            mLayoutPersondata = mStubPersondata.inflate();
            mIvPersonDataPhoto = (ImageView) mLayoutPersondata.findViewById(R.id.civ_1);
            mEtPersonDataNickName = (EditText) mLayoutPersondata.findViewById(R.id.et_1);
            mEtPersonDataPassWord = (EditText) mLayoutPersondata.findViewById(R.id.et_2);
            mTvPersonDataConfirm = (TextView) mLayoutPersondata.findViewById(R.id.tv_4);
            View.OnClickListener listener = new PersonDataClickListener();
            mIvPersonDataPhoto.setOnClickListener(listener);
            mTvPersonDataConfirm.setOnClickListener(listener);
        } else {
            mLayoutPersondata.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 注册页跳转到登录页，右到左
     */
    private void register2login() {
        showLoginView();
        mBack.setVisibility(View.VISIBLE);
        mLayoutLogin.setTranslationX(mScreenW);
        mBack.setTranslationX(mScreenW);

        final ValueAnimator animout = Login_Register_SwAnim_Manager.rightToLeftOut(mScreenW, mLayoutRegister, mClose);
        final ValueAnimator animin = Login_Register_SwAnim_Manager.rightToLeftIn(mScreenW, mLayoutLogin, mBack);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                animout.start();
            }
        });
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animin.start();
            }
        }, mSwAnimDelta);
    }

    /**
     * 登录也回转到注册页，左到右
     */
    private void login2register() {
        showRegisterView();
        mClose.setVisibility(View.VISIBLE);
        mLayoutRegister.setTranslationX(-mScreenW);
        mClose.setTranslationX(-mScreenW);

        final ValueAnimator animout = Login_Register_SwAnim_Manager.leftToRightOut(mScreenW, mLayoutLogin, mBack);
        final ValueAnimator animin = Login_Register_SwAnim_Manager.leftToRightIn(mScreenW, mLayoutRegister, mClose);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                animout.start();
            }
        });
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animin.start();
            }
        }, mSwAnimDelta);
    }

    /**
     * 登录页跳转到短信验证登录页，右到左
     */
    private void login2loginsms() {
        showLoginSmsView();
        mLayoutLoginSms.setTranslationX(mScreenW);

        final ValueAnimator animout = Login_Register_SwAnim_Manager.rightToLeftOut(mScreenW, mLayoutLogin);
        final ValueAnimator animin = Login_Register_SwAnim_Manager.rightToLeftIn(mScreenW, mLayoutLoginSms);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                animout.start();
            }
        });
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animin.start();
            }
        }, mSwAnimDelta);
    }

    /**
     * 短信登录页回转到登录页，左到右
     */
    private void loginsms2login() {
        showLoginView();
        mLayoutLogin.setTranslationX(-mScreenW);

        final ValueAnimator animout = Login_Register_SwAnim_Manager.leftToRightOut(mScreenW, mLayoutLoginSms);
        final ValueAnimator animin = Login_Register_SwAnim_Manager.leftToRightIn(mScreenW, mLayoutLogin);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                animout.start();
            }
        });
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animin.start();
            }
        }, mSwAnimDelta);
    }

    /**
     * 注册页跳转到填写个人资料页，右到左
     */
    private void register2persondata() {
        showPersonDataView();
        mLayoutPersondata.setTranslationX(mScreenW);
        mTvPersonalData.setVisibility(View.VISIBLE);
        mTvPersonalData.setTranslationX(mScreenW);
        mTvSkip.setVisibility(View.VISIBLE);
        mTvSkip.setTranslationX(mScreenW);

        final ValueAnimator animout = Login_Register_SwAnim_Manager.rightToLeftOut(mScreenW
                , mLayoutRegister, mWelcome, mWelcomeSub);
        final ValueAnimator animin = Login_Register_SwAnim_Manager.rightToLeftIn(mScreenW
                , mLayoutPersondata, mTvPersonalData, mTvSkip);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                animout.start();
            }
        });
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animin.start();
            }
        }, mSwAnimDelta);
    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
        int id = v.getId();
        switch (id) {
            case R.id.close:
            case R.id.back:
                onBackPressed();
                break;
            case R.id.tv_4:
                goHomePageActivity();
                break;
            default:
                break;
        }
    }

    class RegisterClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (CommonUtil.isQuickClick()) {
                return;
            }
            int id = v.getId();
            switch (id) {
                case R.id.tv_3: //获取验证码
                    getVerfyCodeToRegister();
                    break;
                case R.id.tv_4: //注册
//                    register();
                    register2persondata();
                    break;
                case R.id.tv_6: //登录，往登录界面切换
                    register2login();
                    break;
                default:
                    break;
            }
        }
    }

    private void register() {
        String verfycode = mEtRegisterVerfyCode.getText().toString().trim();
        if (TextUtils.isEmpty(verfycode)) {
            ToastManager.showShort(this, "验证码不能为空");
            return;
        }
        String invitecode = mEtRegisterInviteCode.getText().toString().trim();
        if (TextUtils.isEmpty(invitecode)) {
            ToastManager.showShort(this, ".邀请码不能为空.");
            return;
        }

        String num = mEtRegisterPhoneNum.getText().toString().trim();
        boolean isnum = isPhoneNum(num);
        if (!isnum) {
            return;
        }

        showLoadingProgress();
        RequestBeanRegister beanRegister = new RequestBeanRegister();
        beanRegister.setMobile(SecurityUtil.haokanEncode(num));
        beanRegister.setSmscode(SecurityUtil.haokanEncode(verfycode));
        beanRegister.setInvite(SecurityUtil.haokanEncode(invitecode));
        String url = UrlsUtil.getRegisterUrl(getApplicationContext(), JsonUtil.toJson(beanRegister));
        Log.d("wangzixu", "register url = " + url);
        HttpClientManager.getInstance(Login_Register_Activity.this).getData(url, new BaseJsonHttpResponseHandler<ResponseBeanRegister>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanRegister response) {
                disMissLoadingProgress();
                if (response != null) {
                    if (response.getErr_code() == 0) {
                        register2persondata();
                    } else {
                        ToastManager.showShort(Login_Register_Activity.this, response.getErr_msg());
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanRegister errorResponse) {
                disMissLoadingProgress();
                ToastManager.showShort(Login_Register_Activity.this, "访问服务器失败");
            }

            @Override
            protected ResponseBeanRegister parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return JsonUtil.fromJson(rawJsonData, ResponseBeanRegister.class);
            }
        });
    }

    private void getVerfyCodeToRegister() {
        String s = mEtRegisterPhoneNum.getText().toString().trim();
        boolean is = isPhoneNum(s);
        if (is) {
            String url = UrlsUtil.getSendSmsUrl(getApplicationContext()
                    , "{\"mobile\":\"" + SecurityUtil.haokanEncode(s) +"\",\"do\":\"reg\"}");
            Log.d("wangzixu", "getVerfyCodeToRegister url = " + url);

            startCountDown(mTvRegisterGetVerfyCode);
            HttpClientManager.getInstance(Login_Register_Activity.this).getData(url, new BaseJsonHttpResponseHandler<BaseBean>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, BaseBean response) {
                    if (response != null && response.getErr_code() != 0) {
                        ToastManager.showShort(Login_Register_Activity.this, response.getErr_msg());
                        resetGetVerfyCodeTv(mTvRegisterGetVerfyCode);
                    } else {
                        ToastManager.showShort(Login_Register_Activity.this, "获取验证码成功");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, BaseBean errorResponse) {
                    ToastManager.showShort(Login_Register_Activity.this, "访问服务器失败");
                    resetGetVerfyCodeTv(mTvRegisterGetVerfyCode);
                }

                @Override
                protected BaseBean parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    return JsonUtil.fromJson(rawJsonData, BaseBean.class);
                }
            });
        }
    }

    /**
     * 获取验证码倒计时60s
     */
    private void startCountDown(final TextView tv) {
        tv.setClickable(false);
        tv.setTextColor(getResources().getColor(R.color.hei_80));
        mCountDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String s = String.valueOf(millisUntilFinished / 1000);
                Log.d("wangzixu", "startCountDown s = " + s);
                tv.setText(s);
            }

            @Override
            public void onFinish() {
                resetGetVerfyCodeTv(tv);
            }
        };
        mCountDownTimer.start();
    }

    private void resetGetVerfyCodeTv(TextView tv) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        tv.setClickable(true);
        tv.setText(R.string.getindentifycode);
        tv.setTextColor(getResources().getColor(R.color.anhuang));
    }

    /**
     * 验证手机格式
     */
    private boolean isPhoneNum(String num) {
        /*
        移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
        联通：130、131、132、152、155、156、185、186
        电信：133、153、180、189、（1349卫通）
        总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
        */
        String telRegex = "^((1[358][0-9])|(14[57])|(17[0678]))\\d{8}$";
        if (TextUtils.isEmpty(num)) {
            ToastManager.showShort(this, "手机号码不能为空");
            return false;
        } else {
            Pattern p = Pattern.compile(telRegex);
            Matcher matcher = p.matcher(num);
            boolean isPhoneNum = matcher.matches();
            if (isPhoneNum) {
                return true;
            } else {
                ToastManager.showShort(this, "请输入正确的手机号码");
                return false;
            }
        }
    }

    class LoginClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (CommonUtil.isQuickClick()) {
                return;
            }
            int id = v.getId();
            switch (id) {
                case R.id.tv_4: //登录
                    goHomePageActivity();
                    break;
                case R.id.tv_6: //短信验证登录
                    login2loginsms();
                    break;
                default:
                    break;
            }
        }
    }

    class PersonDataClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (CommonUtil.isQuickClick()) {
                return;
            }
            int id = v.getId();
            switch (id) {
                case R.id.civ_1: //头像选取
                    //ToastManager.showShort(Login_Register_Activity.this, "头像选取");
                    ImageUtil.changeLight(mIvPersonDataPhoto, true);

                    ClipPhotoManager.startPickImg(Login_Register_Activity.this, ClipPhotoManager.REQUEST_SELECT_PICK);
                    break;
                case R.id.tv_4: //确认
                    if (!TextUtils.isEmpty(mClipedHpPath)) {
                        if (mDefaultSharedPreferences == null) {
                            mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(Login_Register_Activity.this);
                        }
                        String sessionId = mDefaultSharedPreferences.getString(ConstantValues.KEY_SP_SESSIONID, "");
                        if (TextUtils.isEmpty(sessionId)) {
                            ToastManager.showShort(Login_Register_Activity.this, "sessionId null, return!");
                            return;
                        } else {
                            Log.d("wangzixu", "PersonDataClickListener sessionId = " + sessionId);
                        }
                        String url = UrlsUtil.getUploadUrl(sessionId);
                        Log.d("wangzixu", "PersonDataClickListener url = " + url);
                        HttpClientManager.getInstance(Login_Register_Activity.this).LogCookcie();
                        File file = new File(mClipedHpPath);
                        if (file.exists() && file.length() > 0) {
                            HttpClientManager.getInstance(Login_Register_Activity.this).upLoadFile(url, file, new BaseJsonHttpResponseHandler<ResponseBeanUploadPh>() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanUploadPh response) {
                                    ToastManager.showShort(Login_Register_Activity.this, "上传成功 = " + response.getErr_msg());
                                    Log.d("wangzixu", "PersonDataClickListener 上传成功 = " + rawJsonResponse);
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanUploadPh errorResponse) {
                                    ToastManager.showShort(Login_Register_Activity.this, "失败 ");
                                    Log.d("wangzixu", "PersonDataClickListener 上传失败 = " + rawJsonData);
                                }

                                @Override
                                protected ResponseBeanUploadPh parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                                    return JsonUtil.fromJson(rawJsonData, ResponseBeanUploadPh.class);
                                }
                            });
                        }
                    }
                    //goHomePageActivity();
//                    Intent i = new Intent(Login_Register_Activity.this, ClipPhotoActivity.class);
//                    startActivity(i);
                    break;
                default:
                    break;
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
                Intent intent = new Intent(Login_Register_Activity.this, ClipPhotoActivity.class);
                intent.putExtra(ClipPhotoManager.PICK_IMG_PATH, path);
                startActivityForResult(intent, ClipPhotoManager.REQUEST_CLIP_PIC);
            }
        } else if (requestCode == ClipPhotoManager.REQUEST_CLIP_PIC && data != null) {
            mClipedHpPath = data.getStringExtra(ClipPhotoManager.KEY_CLIP_PATH);
            if (!TextUtils.isEmpty(mClipedHpPath)) {
                ImageLoaderManager.getInstance().loadLocalPic(mClipedHpPath, mIvPersonDataPhoto, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        mIvPersonDataPhoto.setImageBitmap(loadedImage);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                    }
                });
            }
        }
    }

    class LoginSmsClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (CommonUtil.isQuickClick()) {
                return;
            }
            int id = v.getId();
            switch (id) {
                case R.id.tv_3: //获取验证码
                    getVerfyCodeToLoginsms();
                    break;
                case R.id.tv_4: //登录
                    loginSms();
                    //goHomePageActivity();
                    break;
                default:
                    break;
            }
        }
    }

    private void loginSms() {
        String num = mEtLoginSmsPhoneNum.getText().toString().trim();
        boolean isnum = isPhoneNum(num);
        if (!isnum) {
            return;
        }

        String verfycode = mEtLoginSmsVerfyCode.getText().toString().trim();
        if (TextUtils.isEmpty(verfycode)) {
            ToastManager.showShort(this, "验证码不能为空");
            return;
        }

        showLoadingProgress();
        RequestBeanLoginSms bean = new RequestBeanLoginSms();
        bean.setMobile(SecurityUtil.haokanEncode(num));
        bean.setSmscode(SecurityUtil.haokanEncode(verfycode));
        String url = UrlsUtil.getLoginSmsUrl(getApplicationContext(), JsonUtil.toJson(bean));
        Log.d("wangzixu", "loginSms url = " + url);

        HttpClientManager.getInstance(Login_Register_Activity.this).getData(url, new BaseJsonHttpResponseHandler<ResponseBeanLogin>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResponseBeanLogin response) {
                disMissLoadingProgress();
                if (response != null) {
                    if (response.getErr_code() == 0) {
                        ToastManager.showShort(Login_Register_Activity.this, "登录成功!");

                        String userId = response.getData().getUserId();
                        String sessionId = response.getData().getSessionId();

                        if (mDefaultSharedPreferences == null) {
                            mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(Login_Register_Activity.this);
                        }
                        SharedPreferences.Editor edit = mDefaultSharedPreferences.edit();
                        edit.putString(ConstantValues.KEY_SP_USERID, userId);
                        edit.putString(ConstantValues.KEY_SP_SESSIONID, sessionId);
                        edit.apply();

                        goHomePageActivity();
                    } else {
                        ToastManager.showShort(Login_Register_Activity.this, response.getErr_msg());
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResponseBeanLogin errorResponse) {
                disMissLoadingProgress();
                ToastManager.showShort(Login_Register_Activity.this, "访问服务器失败");
            }

            @Override
            protected ResponseBeanLogin parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return JsonUtil.fromJson(rawJsonData, ResponseBeanLogin.class);
            }
        });
    }

    private void getVerfyCodeToLoginsms() {
        String s = mEtLoginSmsPhoneNum.getText().toString().trim();
        boolean is = isPhoneNum(s);
        if (is) {
            String url = UrlsUtil.getSendSmsUrl(getApplicationContext()
                    , "{\"mobile\":\"" + SecurityUtil.haokanEncode(s) + "\",\"do\":\"login\"}");
            Log.d("wangzixu", "getVerfyCodeToRegister url = " + url);

            startCountDown(mTvLoginSmsGetVerfyCode);
            HttpClientManager.getInstance(Login_Register_Activity.this).getData(url, new BaseJsonHttpResponseHandler<BaseBean>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, BaseBean response) {
                    if (response != null && response.getErr_code() != 0) {
                        ToastManager.showShort(Login_Register_Activity.this, response.getErr_msg());
                    } else {
                        ToastManager.showShort(Login_Register_Activity.this, "获取验证码成功");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, BaseBean errorResponse) {
                    ToastManager.showShort(Login_Register_Activity.this, "访问服务器失败");
                    resetGetVerfyCodeTv(mTvLoginSmsGetVerfyCode);
                }

                @Override
                protected BaseBean parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    return JsonUtil.fromJson(rawJsonData, BaseBean.class);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (mLayoutLogin != null && mLayoutLogin.getVisibility() == View.VISIBLE) {
            login2register();
        } else if (mLayoutLoginSms != null && mLayoutLoginSms.getVisibility() == View.VISIBLE) {
            loginsms2login();
        } else {
            goHomePageActivity();
        }
    }

    private void goHomePageActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    private Dialog mLoadingProgress;
    private void showLoadingProgress() {
        if (mLoadingProgress == null) {
            mLoadingProgress = new Dialog(this, R.style.loading_progress);
            mLoadingProgress.setContentView(R.layout.loading_layout);
            mLoadingProgress.setCancelable(false);
        }
        mLoadingProgress.show();
    }
    private void disMissLoadingProgress() {
        if (mLoadingProgress != null && mLoadingProgress.isShowing()) {
            mLoadingProgress.dismiss();
        }
    }
}
