package com.haokan.xinyitu.upload;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.base.BaseActivity;
import com.haokan.xinyitu.bigimgbrowse.BigImgBrowseActivity;
import com.haokan.xinyitu.main.DemoImgBean;
import com.haokan.xinyitu.util.DisplayUtil;
import com.haokan.xinyitu.util.ImageUtil;
import com.haokan.xinyitu.util.ToastManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class UpLoadGalleryActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private TextView mTvUploadPickfolder;
    private TextView mTvConfirm;
    private GridView mGvUploadGridview;
    private Dialog mProgressDialog;
    private ArrayList<ArrayList<DemoImgBean>> mImgDirs = new ArrayList<>();
    private ArrayList<DemoImgBean> mData = new ArrayList<>();
    private ArrayList<DemoImgBean> mCheckedImgs = new ArrayList<>();
    private Handler mHandler = new Handler();
    /**
     * 读取手机中所有图片资源的url
     */
    final static Uri LOCAL_IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private GalleryAdapter mAdapter;
    private View mRlPopContent;
    private View mRlHeader;
    private ListView mLvPop;
    private View mPopBg;
    private boolean mIsFromAddMore; //是否是从上传编辑页中的添加更过按钮过来的
    private boolean mIsFirstLoad; //是否是第一次加载

    private void assignViews() {
        mIsFirstLoad = true;
        ImageButton ibClose = (ImageButton) findViewById(R.id.ib_close);
        ibClose.setOnClickListener(this);
        mTvUploadPickfolder = (TextView) findViewById(R.id.tv_upload_pickfolder);
        mTvUploadPickfolder.setOnClickListener(this);
        mTvConfirm = (TextView) findViewById(R.id.tv_confirm);
        mTvConfirm.setOnClickListener(this);
        mGvUploadGridview = (GridView) findViewById(R.id.gv_upload_gridview);
        mRlHeader = findViewById(R.id.rl_header);

        //fing的时候暂停加载图片
        mGvUploadGridview.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));

        mIsFromAddMore = getIntent().getBooleanExtra("addmore", false);
        if (mIsFromAddMore) {
            App app = (App) getApplication();
            mImgDirs = app.getImgDirs();
            mData = app.getBigImgData();
            mCheckedImgs = app.getCheckedImgs();
            mAdapter = new GalleryAdapter(this, mData, mCheckedImgs);
            mGvUploadGridview.setAdapter(mAdapter);
        } else {
            mAdapter = new GalleryAdapter(this, mData, mCheckedImgs);
        }

        mGvUploadGridview.setOnItemClickListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadgallery_activity_layout);
        assignViews();
        if (!mIsFromAddMore) {
            loadData();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //从浏览大图页回来后，大图页可能改变了选中的状态，所以此时需要更新一下选中的状态和数字顺序
        if (!mIsFirstLoad && mCheckedImgs != null && mCheckedImgs.size() > 0) {
//            ArrayList<PickImgBean> temp = new ArrayList<>();
//            for (int i = 0; i < mCheckedImgs.size(); i++) {
//                PickImgBean bean = mCheckedImgs.get(i);
//                if (!bean.isChecked()) {
//                    temp.add(bean);
//                }
//            }
//            if (temp.size() > 0) {
//                mCheckedImgs.removeAll(temp);
//            }
            mAdapter.updataChecked(mHandler);
        }
        mIsFirstLoad = false;
    }

    /**
     * 加载手机中所有的图片，并按文件夹形式分好结构
     */
    private void loadData() {
        mProgressDialog = new Dialog(this, R.style.loading_progress);
        mProgressDialog.setContentView(R.layout.loading_layout_progressdialog_titleloading);
        mProgressDialog.show();
        //mProgressDialog = ProgressDialog.show(this, null, "读取图片中...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver contentResolver = UpLoadGalleryActivity.this.getContentResolver();
                Cursor mCursor = contentResolver.query(LOCAL_IMAGE_URI, null, MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?", new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED
                        + " DESC");
                if (mCursor == null) {
                    mProgressDialog.dismiss();
                    Toast.makeText(UpLoadGalleryActivity.this, "读取图片失败！", Toast.LENGTH_SHORT).show();
                    return;
                }

                HashMap<String, ArrayList<DemoImgBean>> tempMap = new HashMap<>();
                try {
                    while (mCursor.moveToNext()) {
                        DemoImgBean bean = new DemoImgBean();
                        // 获取图片路径
                        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        String w = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
                        String h = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
                        if (TextUtils.isEmpty(w) || TextUtils.isEmpty(h)
                                || Integer.valueOf(w) <= 0
                                || Integer.valueOf(h) <= 0) {
                            continue;
                        }
                        Log.d("wangzixu", "loadData w,h = " + w + ", " + h);
                        bean.setWidth(w);
                        bean.setHeight(h);
                        bean.setUrl(ImageDownloader.Scheme.FILE.wrap(path));
                        mData.add(bean);

                        // 获取父文件夹路径
                        String parentName = new File(path).getParentFile().getName();
                        if (tempMap.containsKey(parentName)) {
                            tempMap.get(parentName).add(bean);
                        } else {
                            ArrayList<DemoImgBean> list = new ArrayList<>();
                            list.add(bean);
                            tempMap.put(parentName, list);
                            mImgDirs.add(list);
                        }
                    }
                    mImgDirs.add(0, mData);
                } finally {
                    tempMap.clear();
                    mCursor.close();
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //                        mAdapter.notifyDataSetChanged();
                        mGvUploadGridview.setAdapter(mAdapter);
                        mProgressDialog.dismiss();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        if (mPopWindow != null && mPopWindow.isShowing()) {
            disMissPop();
        } else {
            super.onBackPressed();
            if (mIsFromAddMore) {
                overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
            } else {
                overridePendingTransition(R.anim.activity_in_top2bottom, R.anim.activity_out_top2bottom);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ib_close: //关闭按钮
                if (mPopWindow != null && mPopWindow.isShowing()) {
                    disMissPop();
                } else {
                    super.onBackPressed();
                    if (mIsFromAddMore) {
                        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
                    } else {
                        overridePendingTransition(R.anim.activity_in_top2bottom, R.anim.activity_out_top2bottom);
                    }
                }
                break;
            case R.id.tv_confirm: //完成按钮
                if (mPopWindow != null && mPopWindow.isShowing()) {
                    disMissPop();
                } else if (mCheckedImgs.size() <= 0) {
                    ToastManager.showShort(UpLoadGalleryActivity.this, "您还没有选择图片");
                } else {
                    if (!mIsFromAddMore) {
                        Intent intent = new Intent(UpLoadGalleryActivity.this, UpLoadMainActivity.class);
                        App app = (App) getApplication();
                        app.setCheckedImgs(mCheckedImgs);
                        app.setImgDirs(mImgDirs);
                        app.setBigImgData(mImgDirs.get(mCrrentSelectFolder));
                        startActivity(intent);
                    }
                    super.onBackPressed();
                    if (mIsFromAddMore) {
                        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
                    } else {
                        overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                    }
                }
                break;
            case R.id.tv_upload_pickfolder: //中间选择按钮，弹出选择文件夹popupwindow
                if (mImgDirs == null || mImgDirs.size() == 0) {
                    return;
                }
                initPopwindow();
                if (mPopWindow.isShowing()) {
                    disMissPop();
                } else {
                    mPopWindow.setClippingEnabled(false);
                    mPopWindow.showAtLocation(mRlHeader, Gravity.BOTTOM | Gravity.LEFT, 0, 0);
                    mPopBg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.popupwindow_bg_in));
                    mRlPopContent.startAnimation(AnimationUtils.loadAnimation(this, R.anim.popupwindow_top_in));
                }
                break;
            case R.id.shadow:
                disMissPop();
                break;
            default:
                break;
        }
    }

    private PopupWindow mPopWindow;
    private int mCrrentSelectFolder;
    private void initPopwindow() {
        if (mPopWindow == null) {
            View content = LayoutInflater.from(getApplicationContext()).inflate(R.layout.uploadgallery_selectfolder_popwindow_layout, null);
            mRlPopContent = content.findViewById(R.id.rl_pop_content);
            mLvPop = (ListView) content.findViewById(R.id.listview_popwindow);
            mPopBg = content.findViewById(R.id.shadow);
            mPopBg.setOnClickListener(this);

            PopupListAdapter ada = new PopupListAdapter();
            mLvPop.setAdapter(ada);
            mLvPop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mCrrentSelectFolder = position;
                    mCheckedImgs.clear();
                    mPopWindow.dismiss();
                    PopupListAdapter.ViewHolder holder = (PopupListAdapter.ViewHolder) view.getTag();
                    mTvUploadPickfolder.setText(holder.tvtitle.getText());
                    mAdapter.setData(mImgDirs.get(position));
                    mAdapter.notifyDataSetChanged();
                }
            });

            mPopWindow = new PopupWindow(content, AbsListView.LayoutParams.MATCH_PARENT
                    , DisplayUtil.getRealScreenPoint(UpLoadGalleryActivity.this).y - mRlHeader.getBottom(), true);
            //            mPopWindow = new PopupWindow(content, AbsListView.LayoutParams.MATCH_PARENT
            //                    , AbsListView.LayoutParams.MATCH_PARENT, true);
            mPopWindow.setAnimationStyle(0);
            mPopWindow.setFocusable(false);
        }
    }

    private void disMissPop() {
        Animation outAnim = AnimationUtils.loadAnimation(this, R.anim.popupwindow_bg_out);
        outAnim.setFillAfter(true);
        outAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        mPopWindow.dismiss();
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

        });

        mPopBg.startAnimation(outAnim);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.popupwindow_top_out);
        animation.setFillAfter(true);
        mRlPopContent.startAnimation(animation);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_pickimage_item);
        ImageUtil.changeLight(imageView, true);
        Intent intent = new Intent(this, BigImgBrowseActivity.class);
        intent.putExtra(BigImgBrowseActivity.EXTRA_USED, 2);
        intent.putExtra(BigImgBrowseActivity.EXTRA_INIT_POSITION, position);
        //intent.putParcelableArrayListExtra(BigImgBrowseActivity.EXTRA_IMG_DATA, mData);
        //通过App来传递数据
        App app = (App) getApplication();
        app.setBigImgData(mImgDirs.get(mCrrentSelectFolder));
        app.setCheckedImgs(mCheckedImgs);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_retain);
    }

    @Override
    protected void onDestroy() {
//        App app = (App) getApplication();
//        app.setBigImgData(null);
//        app.setCheckedImgs(null);
        super.onDestroy();
    }

    private class PopupListAdapter extends BaseAdapter {

        public PopupListAdapter() {
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.uploadgallery_selectfolder_popwindow_ietm, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ArrayList<DemoImgBean> list = mImgDirs.get(position);
            DemoImgBean first = list.get(0);
            final String path = first.getUrl();

            // 数量
            holder.tvcount.setText(list.size() + "");

            // 文件夹名称
            if (position == 0) {
                holder.tvtitle.setText("所有图片");
            } else {
                String parentPath = new File(path).getParentFile().getName();
                String title = parentPath.substring(parentPath.lastIndexOf("/") + 1);
                holder.tvtitle.setText(title);
            }
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return mImgDirs.size();
        }

        public class ViewHolder {
            public final TextView tvtitle;
            public final TextView tvcount;
            public final View root;

            public ViewHolder(View root) {
                tvtitle = (TextView) root.findViewById(R.id.tv_title);
                tvcount = (TextView) root.findViewById(R.id.tv_count);
                this.root = root;
            }
        }
    }
}
