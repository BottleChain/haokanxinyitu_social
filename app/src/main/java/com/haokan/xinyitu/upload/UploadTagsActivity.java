package com.haokan.xinyitu.upload;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;
import com.haokan.xinyitu.main.DemoTagBean;
import com.haokan.xinyitu.util.DisplayUtil;
import com.haokan.xinyitu.util.ImgAndTagWallManager;
import com.haokan.xinyitu.util.ToastManager;

import java.util.ArrayList;
import java.util.Random;

public class UploadTagsActivity extends BaseActivity implements View.OnClickListener, TagWithDeleteIcon.onDeleteClickListener {
    private ImageButton mIbBack;
    private TextView mTvConfirm;
    private EditText mEtUploadEdit;
    private TextView mTvTextcount;
    private TextView mTvUsedtags;
    private RelativeLayout mRlUsedtags;
    private TextView mTvHottags;
    private TextView mTvCickAddTag;
    private RelativeLayout mRlHottags;
    private RelativeLayout mRlTagContainer;
    private ArrayList<DemoTagBean> mListUsed;
    private ArrayList<DemoTagBean> mListHot;
    private Handler mHandler = new Handler();
    private int mTagTextPaddingH;
    private int mTagTextPaddingW;
    private int mTagTextSizePx; //pix
    private int mTagTextSize; //sp
    private int mTagDrawablePading;
    private int mTagDrawableWidth;
    private int mTagRlWidth;
    private int mCurrentTagCount;
    private ArrayList<DemoTagBean> mTagsAdded = new ArrayList<>(); //所有已经添加的tag
    private App mApp;

    private void assignViews() {
        mIbBack = (ImageButton) findViewById(R.id.ib_back);
        mTvConfirm = (TextView) findViewById(R.id.tv_confirm);
        mTvCickAddTag = (TextView) findViewById(R.id.tv_click_add);
        mEtUploadEdit = (EditText) findViewById(R.id.et_edit);
        mTvTextcount = (TextView) findViewById(R.id.tv_added);
        mTvUsedtags = (TextView) findViewById(R.id.tv_usedtags);
        mRlUsedtags = (RelativeLayout) findViewById(R.id.rl_usedtags);
        mTvHottags = (TextView) findViewById(R.id.tv_hottags);
        mRlHottags = (RelativeLayout) findViewById(R.id.rl_hottags);
        mRlTagContainer = (RelativeLayout) findViewById(R.id.rl_tags_container);

        mIbBack.setOnClickListener(this);
        mTvConfirm.setOnClickListener(this);
        mTvCickAddTag.setOnClickListener(this);

//        mEtUploadEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                Log.i("wangzixu", "UploadTags--- onEditorAction text, textSize, actionId, event = " + v.getText() + ", " + v.getText().length()
//                        + ", " + actionId + ", " + event);
//                if (event != null) {
//                    if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
//                        //拦截回车键，形成一个标签
//                        String s = mEtUploadEdit.getText().toString();
//                        if (TextUtils.isEmpty(s.trim())) {
//                            return true;
//                        }
//                        if (mCurrentTagCount < 5) {
//                            TextView tag = getTag(s);
//                            mRlTagContainer.addView(tag);
//                            mCurrentTagCount ++;
//                            mTvTextcount.setText(mCurrentTagCount + "/5");
//                        } else {
//                            ToastManager.showShort(UploadTagsActivity.this, "最多添加5个标签");
//                        }
//                        mEtUploadEdit.setText("");
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });

        mEtUploadEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i("wangzixu", "UploadTags--- setOnKeyListener  keyCode, event = " + keyCode + ", " + event);
                if (event != null) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            //拦截回车键，形成一个标签
                            addTag();
                            return true;
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        mApp = (App)getApplication();
        final ArrayList<DemoTagBean> tagsTemp = mApp.getTagsTemp();
        if (tagsTemp != null && tagsTemp.size() > 0) {
            mRlTagContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mRlTagContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    for (int i = 0; i < tagsTemp.size(); i++) {
                        String s = tagsTemp.get(i).getName();
                        dynamicAddTag(s);
                    }
                }
            });
        }
//        else {
//            mRlTagContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    TextView tag = getTag(" ");
//                    mRlTagContainer.addView(tag);
//                    mRlTagContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                }
//            });
//        }


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
                    mTvCickAddTag.setTextColor(getResources().getColor(R.color.hei_20));
                } else {
                    mTvCickAddTag.setTextColor(getResources().getColorStateList(R.color.click_huang_2));
                }
            }
        });

        mTagTextSize = 14;
        mTagTextSizePx = DisplayUtil.sp2px(this, mTagTextSize);
        mTagTextPaddingH = DisplayUtil.dip2px(this, 10);
        mTagTextPaddingW = DisplayUtil.dip2px(this, 12);
        mTagRlWidth = getResources().getDisplayMetrics().widthPixels - DisplayUtil.dip2px(this, 14);
        mTagDrawablePading = DisplayUtil.dip2px(this, 4);
        mTagDrawableWidth = DisplayUtil.dip2px(this, 13);
    }

    private void addTag() {
        //拦截回车键，形成一个标签
        String s = mEtUploadEdit.getText().toString();
        if (TextUtils.isEmpty(s.trim())) {
            ToastManager.showShort(this, "标签不能为空");
            return;
        }
        if (s.contains("@")) {
            ToastManager.showShort(UploadTagsActivity.this, "输入内容中不能含有 @ 符号");
            return;
        }
        boolean success = dynamicAddTag(s);
        if (success) {
            mEtUploadEdit.setText("");
        }
    }

    private boolean dynamicAddTag(String s) {
        if (mCurrentTagCount < 5) {
            if (mTagsAdded.size() == 0) {
                mCurrentTagCount = 0;
                mRlTagContainer.removeAllViewsInLayout();
                mBaseW = 0;
                mCurrentTagLineCount = 0;
                mCurrentTop = 0;
            }
            for (int i = 0; i < mTagsAdded.size(); i++) {
                if (s.equals(mTagsAdded.get(i).getName())) {
                    ToastManager.showShort(UploadTagsActivity.this, "已经添加了该标签");
                    return false;
                }
            }

            TagWithDeleteIcon tag = getTag(s);

            mRlTagContainer.addView(tag);
            mCurrentTagCount++;
            mTvTextcount.setText("已添加标签(" + mCurrentTagCount + "/5)");
            DemoTagBean bean = new DemoTagBean();
            bean.setName(s);
            mTagsAdded.add(bean);
            tag.setOnDeleteClickListener(this);
            tag.getDeleteIcon().setTag(bean);
            return true;
        } else {
            ToastManager.showShort(UploadTagsActivity.this, "最多添加5个标签");
            return false;
        }
    }

    private int mBaseW;
    private int mCurrentTop;
    private int mCurrentTagLineCount; //动态添加tag，tag要添加的行中已经有几个tag了
    /**
     * 这里添加标签是动态的一个一个添加，所以需要自己计算，不能用标签工具类计算，
     * 标签工具类是用来把已知数量的标签填到容器中用的, 用到的是自定义的带删除号的textview
     */
    private TagWithDeleteIcon getTag(String string) {
        TagWithDeleteIcon tag = new TagWithDeleteIcon(this);
        tag.setTagName(string);

//        TextView tv = new TextView(UploadTagsActivity.this);
//        tv.setIncludeFontPadding(false);
//        tv.setTypeface(Typeface.DEFAULT);
//        tv.setText(string);
//        tv.setTextSize(mTagTextSize);
//        tv.setTextColor(getResources().getColorStateList(R.color.click_hei60));
//        tv.setSingleLine();
//        tv.setEllipsize(TextUtils.TruncateAt.END);
//        tv.setId(R.id.tv_tag_added);
//        tv.setPadding(mTagTextPaddingW, mTagTextPaddingH, mTagTextPaddingW, mTagTextPaddingH);
//        tv.setCompoundDrawablePadding(mTagDrawablePading);
//        tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_upload_tag, 0, 0, 0);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = mBaseW;
        lp.topMargin = mCurrentTop;

        int tagw = tag.getTotalWidth(); //tag有多宽，字数*字宽 + xxx
        mBaseW = mBaseW + tagw;

        int delta = mRlTagContainer.getWidth() - mBaseW; //当前行右边还剩下多少空间
        if (delta < 0 && mCurrentTagLineCount > 0) {//加上此行溢出了，所以要换行。textPadding + 2 * mTagTextSize
            mCurrentTagLineCount = 0;
            mCurrentTop = mCurrentTop + tag.getTotalHeigh(); //此处tag上下间隙是15dp
            lp.leftMargin = 0;
            lp.topMargin = mCurrentTop;
            mBaseW = tagw;
        } else {
            mCurrentTagLineCount ++;
        }
        tag.setLayoutParams(lp);
        return tag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadtags_activity_layout);
        assignViews();
        
        loadData();
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
                ImgAndTagWallManager.getInstance(UploadTagsActivity.this).initTagsWall(mListUsed, mTagTextPaddingW *2, mTagTextPaddingH *2
                        ,mTagRlWidth, 0, 0, mTagTextSizePx, mTagTextSizePx, mTagDrawablePading, mTagDrawableWidth , 0 , 0);
                ImgAndTagWallManager.getInstance(UploadTagsActivity.this).initTagsWall(mListHot, mTagTextPaddingW *2, mTagTextPaddingH *2
                        ,mTagRlWidth, 0, 0, mTagTextSizePx, mTagTextSizePx, mTagDrawablePading, mTagDrawableWidth , 0 , 0);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mRlUsedtags.removeAllViewsInLayout();
                        for (int i = 0; i < mListUsed.size(); i++) {
                            DemoTagBean bean = mListUsed.get(i);
                            String tag = bean.getName();
                            TextView tv = new TextView(UploadTagsActivity.this);
                            tv.setIncludeFontPadding(false);
                            tv.setTypeface(Typeface.DEFAULT);
                            tv.setText(tag);
                            tv.setTextSize(mTagTextSize);
                            tv.setTextColor(getResources().getColorStateList(R.color.click_hei40));
                            tv.setSingleLine();
                            tv.setEllipsize(TextUtils.TruncateAt.END);
                            tv.setId(R.id.tv_tag);
                            tv.setPadding(mTagTextPaddingW, mTagTextPaddingH, mTagTextPaddingW, mTagTextPaddingH);
                            tv.setCompoundDrawablePadding(mTagDrawablePading);
                            tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_upload_tab_light, 0, 0, 0);
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                                    , ViewGroup.LayoutParams.WRAP_CONTENT);
                            lp.leftMargin = bean.getMarginLeft();
                            lp.topMargin = bean.getMarginTop();
                            tv.setLayoutParams(lp);
                            mRlUsedtags.addView(tv);
                            tv.setOnClickListener(UploadTagsActivity.this);
                        }

                        mRlHottags.removeAllViewsInLayout();
                        for (int i = 0; i < mListHot.size(); i++) {
                            DemoTagBean bean = mListHot.get(i);
                            String tag = bean.getName();
                            TextView tv = new TextView(UploadTagsActivity.this);
                            tv.setIncludeFontPadding(false);
                            tv.setTypeface(Typeface.DEFAULT);
                            tv.setText(tag);
                            tv.setTextSize(mTagTextSize);
                            tv.setTextColor(getResources().getColorStateList(R.color.click_hei40));
                            tv.setSingleLine();
                            tv.setEllipsize(TextUtils.TruncateAt.END);
                            tv.setPadding(mTagTextPaddingW, mTagTextPaddingH, mTagTextPaddingW, mTagTextPaddingH);
                            tv.setCompoundDrawablePadding(mTagDrawablePading);
                            tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_upload_tab_light, 0, 0, 0);
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                                    , ViewGroup.LayoutParams.WRAP_CONTENT);
                            lp.leftMargin = bean.getMarginLeft();
                            lp.topMargin = bean.getMarginTop();
                            //            Log.d("wangzixu", "initDiscoveryItem0 i,l,t,w,h = " + i + ", " + bean.getMarginLeft() + ", " + bean.getMarginTop()
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
            case R.id.tv_confirm:
                onBackPressed();
                break;
            case R.id.tv_click_add:
                addTag();
                break;
            case R.id.tv_tag:
                TextView view = (TextView) v;
                String s = view.getText().toString();
                dynamicAddTag(s);
                break;
            default:
                break;
        }
    }

    private void deleteTag(DemoTagBean bean) {
        mTagsAdded.remove(bean);
        mCurrentTagCount --;
        ArrayList<DemoTagBean>  tagsTemp = mTagsAdded;
        mTagsAdded = new ArrayList<>();
        mRlTagContainer.removeAllViewsInLayout();
        if (tagsTemp.size() == 0) {
            mCurrentTagCount = 0;
            mTvTextcount.setText("已添加标签(" + mCurrentTagCount + "/5)");
        } else {
            for (int i = 0; i < tagsTemp.size(); i++) {
                String s = tagsTemp.get(i).getName();
                dynamicAddTag(s);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (mTagsAdded.size() > 0) {
            mApp.setTagsTemp(mTagsAdded);
        } else {
            mApp.setTagsTemp(null);
        }
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public void onDeleteIconClick(View view) {
        DemoTagBean bean = (DemoTagBean) view.getTag();
        deleteTag(bean);
    }
}
