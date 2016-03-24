package com.haokan.xinyitu.upload;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;
import com.haokan.xinyitu.main.DemoTagBean;
import com.haokan.xinyitu.util.DisplayUtil;
import com.haokan.xinyitu.util.ImgAndTagWallManager;

import java.util.ArrayList;
import java.util.Random;

public class UploadTagsActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout mRlHeader;
    private ImageButton mIbBack;
    private TextView mTvConfirm;
    private MyEditText mEtUploadEdit;
    private TextView mTvTextcount;
    private View mDevider1;
    private TextView mTvUsedtags;
    private RelativeLayout mRlUsedtags;
    private TextView mTvHottags;
    private RelativeLayout mRlHottags;
    private ArrayList<DemoTagBean> mListUsed;
    private ArrayList<DemoTagBean> mListHot;
    private Handler mHandler = new Handler();
    private int mTagTextPadding;
    private int mTagTextSize;
    private int mTagDrawablePading;
    private int mTagDrawableWidth;
    private int mTagRlWidth;

    private int mTagStart; //生成标签的起始位置
    private Html.ImageGetter mImageGetter;

    private void assignViews() {
        mRlHeader = (RelativeLayout) findViewById(R.id.rl_header);
        mIbBack = (ImageButton) findViewById(R.id.ib_back);
        mTvConfirm = (TextView) findViewById(R.id.tv_confirm);
        mEtUploadEdit = (MyEditText) findViewById(R.id.et_upload_edit);
        mTvTextcount = (TextView) findViewById(R.id.tv_textcount);
        mDevider1 = findViewById(R.id.devider_1);
        mTvUsedtags = (TextView) findViewById(R.id.tv_usedtags);
        mRlUsedtags = (RelativeLayout) findViewById(R.id.rl_usedtags);
        mTvHottags = (TextView) findViewById(R.id.tv_hottags);
        mRlHottags = (RelativeLayout) findViewById(R.id.rl_hottags);

        mIbBack.setOnClickListener(this);
        mTvConfirm.setOnClickListener(this);

//        mImageGetter = new Html.ImageGetter() {
//            @Override
//            public Drawable getDrawable(String source) {
//                int id = Integer.parseInt(source);
//                //根据id从资源文件中获取图片对象
//                Drawable d = getResources().getDrawable(id);
//                if (d != null) {
//                    d.setBounds(0, 0, d.getIntrinsicWidth(),d.getIntrinsicHeight());
//                    return d;
//                } else {
//                    return null;
//                }
//            }
//        };

        mEtUploadEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.i("wangzixu", "UploadTags--- onEditorAction text, textSize, actionId, event = " + v.getText() + ", " + v.getText().length()
                        + ", " + actionId + ", " + event);
                if (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    //拦截回车键，形成一个标签
                    mEtUploadEdit.append(getTagIconSpan("   d ", 3, 4));
                    return true;
                }
                return false;
            }
        });

//        mEtUploadEdit.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                Log.i("wangzixu", "UploadTags--- setOnKeyListener  keyCode, event = " + keyCode + ", " + event);
//                if (event != null) {
//                    if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
//                        //拦截回车键，形成一个标签
//                        mEtUploadEdit.append(getTagIconSpan("   d ", 3, 4));
//                        return true;
//                    } else if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_UP) { //拦截删除键
//                        if (mTagPositions.size() == 1) {
//
//                        }
//                    }
//                }
//                return true;
//            }
//        });

        mEtUploadEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("wangzixu", "UploadTags--- ---TextChanged before ");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("wangzixu", "UploadTags--- ---TextChanged on ");
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("wangzixu", "UploadTags--- ---TextChanged after ");
            }
        });

        mEtUploadEdit.setOnSelectionChangedListener(new MyEditText.onSelectionChangedListener() {
            @Override
            public void onSelectionChanged(int selStart, int selEnd) {
                Log.i("wangzixu", "UploadTags--- onSelectionChanged s,e = " + selStart + ", " + selEnd);

            }
        });

        mEtUploadEdit.append(getTagIconSpan("d ", 0, 1));
        TagPosition pos1 = new TagPosition();
        pos1.start = 0;
        pos1.end = mEtUploadEdit.length();
        mTagPositions.add(pos1);

        mTagTextSize = DisplayUtil.sp2px(this, 14);
        mTagTextPadding = DisplayUtil.dip2px(this, 10);
        mTagRlWidth = getResources().getDisplayMetrics().widthPixels - DisplayUtil.dip2px(this, 14);
        mTagDrawablePading = DisplayUtil.dip2px(this, 4);
        mTagDrawableWidth = DisplayUtil.dip2px(this, 13);
    }

    private int mLastCursorPosStart;
    private int mLastCursorPosEnd;
    private SpannableString getTagIconSpan(String string, int tagStart, int end) {
        Drawable drawable=getResources().getDrawable(R.drawable.icon_upload_tag);
        SpannableString ss = new SpannableString(string);
        if (drawable != null) {
            drawable.setBounds(0,0, drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
            ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
            ss.setSpan(imageSpan, tagStart, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
//        Spanned spanned = Html.fromHtml("<img src='" + R.drawable.icon_upload_tag + "'/> ", mImageGetter, null);
        return ss;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadtags_activity_layout);
        assignViews();
        
        loadData();
    }

    ArrayList<TagPosition> mTagPositions = new ArrayList<>();
    public static class TagPosition {
        public int start;
        public int end;
        public String tagString;
    }


    /**
     * 加载一些曾经使用过的标签和热门标签数据
     */
    private void loadData() {
        // TODO: 2016/3/23 暂时造假数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                //----------------start 假数据----------------------
                String[] tags = {"风景如画", "猫", "好看摄影大赛第一季", "中国好声音", "动物", "北京雾霾天", "丝竹"};
                mListUsed = new ArrayList<>();
                Random random = new Random();
                for (int k = 0; k < 9; k++) {
                    DemoTagBean tag = new DemoTagBean();
                    int tagindex = random.nextInt(7);
                    tag.setName(tags[tagindex]);
                    mListUsed.add(tag);
                }

                mListHot = new ArrayList<>();
                for (int k = 0; k < 9; k++) {
                    DemoTagBean tag = new DemoTagBean();
                    int tagindex = random.nextInt(7);
                    tag.setName(tags[tagindex]);
                    mListHot.add(tag);
                }
                //----------------end 假数据----------------------
                //把tag标签位置计算出来
                ImgAndTagWallManager.getInstance(UploadTagsActivity.this).initTagsWall(mListUsed, mTagTextPadding *2, mTagTextPadding *2
                        ,mTagRlWidth, 0, 0, mTagTextSize, mTagTextSize, mTagDrawablePading, mTagDrawableWidth , 0 , 0);
                ImgAndTagWallManager.getInstance(UploadTagsActivity.this).initTagsWall(mListHot, mTagTextPadding *2, mTagTextPadding *2
                        ,mTagRlWidth, 0, 0, mTagTextSize, mTagTextSize, mTagDrawablePading, mTagDrawableWidth , 0 , 0);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mRlUsedtags.removeAllViewsInLayout();
                        for (int i = 0; i < mListUsed.size(); i++) {
                            DemoTagBean bean = mListUsed.get(i);
                            String tag = bean.getName();
                            TextView tv = new TextView(UploadTagsActivity.this);
                            tv.setText(tag);
                            tv.setTextColor(getResources().getColorStateList(R.color.click_hei60));
                            tv.setSingleLine();
                            tv.setEllipsize(TextUtils.TruncateAt.END);
                            tv.setPadding(mTagTextPadding, mTagTextPadding, mTagTextPadding, mTagTextPadding);
                            tv.setCompoundDrawablePadding(mTagDrawablePading);
                            tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_upload_tag, 0, 0, 0);
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                                    , ViewGroup.LayoutParams.WRAP_CONTENT);
                            lp.leftMargin = bean.getMarginLeft();
                            lp.topMargin = bean.getMarginTop();
                            //            Log.d("wangzixu", "initItem0 i,l,t,w,h = " + i + ", " + bean.getMarginLeft() + ", " + bean.getMarginTop()
                            //                    + ", " + bean.getItemWidth() + ", " + bean.getItemHeigh());
                            tv.setLayoutParams(lp);
                            mRlUsedtags.addView(tv);
                            tv.setId(R.id.tv_tag);
                            tv.setOnClickListener(UploadTagsActivity.this);
                        }

                        mRlHottags.removeAllViewsInLayout();
                        for (int i = 0; i < mListHot.size(); i++) {
                            DemoTagBean bean = mListHot.get(i);
                            String tag = bean.getName();
                            TextView tv = new TextView(UploadTagsActivity.this);
                            tv.setText(tag);
                            tv.setTextColor(getResources().getColorStateList(R.color.click_hei60));
                            tv.setSingleLine();
                            tv.setEllipsize(TextUtils.TruncateAt.END);
                            tv.setPadding(mTagTextPadding, mTagTextPadding, mTagTextPadding, mTagTextPadding);
                            tv.setCompoundDrawablePadding(mTagDrawablePading);
                            tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_upload_tag, 0, 0, 0);
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                                    , ViewGroup.LayoutParams.WRAP_CONTENT);
                            lp.leftMargin = bean.getMarginLeft();
                            lp.topMargin = bean.getMarginTop();
                            //            Log.d("wangzixu", "initItem0 i,l,t,w,h = " + i + ", " + bean.getMarginLeft() + ", " + bean.getMarginTop()
                            //                    + ", " + bean.getItemWidth() + ", " + bean.getItemHeigh());
                            tv.setLayoutParams(lp);
                            mRlHottags.addView(tv);
                            tv.setId(R.id.tv_tag);
                            tv.setOnClickListener(UploadTagsActivity.this);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ib_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
