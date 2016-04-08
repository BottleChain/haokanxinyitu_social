package com.haokan.xinyitu.main;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haokan.xinyitu.App;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.main.discovery.ResponseBeanAlbumInfo;
import com.haokan.xinyitu.main.discovery.ViewHolderDiscoveryItem0;
import com.haokan.xinyitu.main.discovery.ViewHolderDiscoveryItem1;
import com.haokan.xinyitu.main.discovery.ViewHolderDiscoveryItem2;
import com.haokan.xinyitu.main.mypersonalcenter.ViewHolderPersonnalCenterItem0;
import com.haokan.xinyitu.util.DataFormatUtil;
import com.haokan.xinyitu.util.DisplayUtil;
import com.haokan.xinyitu.util.ImageLoaderManager;
import com.haokan.xinyitu.util.ImgAndTagWallManager;

import java.util.List;

public class FragmentAdapterItemHelper {
    private Context mContext;
    private LayoutInflater mInflater;
    private final ColorStateList mTagTextColor;
    private final View.OnClickListener mOnClickListener;
    private int mAvatarW, mAvatarH;
    //private static int[] sBgColors = {0xff2f2f2f, 0xff373737, 0xff3f3f3f, 0xff474747};

    public FragmentAdapterItemHelper(Context context, View.OnClickListener onClickListener) {
        mContext = context;
        mOnClickListener = onClickListener;
        mInflater = LayoutInflater.from(context);
//        mScreenW = context.getResources().getDisplayMetrics().widthPixels;
//        mDividerW = DisplayUtil.dip2px(mContext, 3);
//        mBaseHeigh = DisplayUtil.dip2px(mContext, 110);
//        mMinItemH = DisplayUtil.dip2px(mContext, 60);
//        mMinItemImgW = DisplayUtil.dip2px(mContext, 40);
//
//        mTagTextSize = DisplayUtil.sp2px(mContext, 14);
//        mTagTextPadding = DisplayUtil.dip2px(mContext, 5);
        mAvatarW = mAvatarH = DisplayUtil.sp2px(mContext, 46);
        mTagTextColor = context.getResources().getColorStateList(R.color.click_huang_1);
    }

    public View initPersonnalCenterItem0(int position, ResponseBeanAlbumInfo.DataEntity beanDiscovery, View convertView, ViewGroup parent) {
        ViewHolderPersonnalCenterItem0 holder;
        boolean isShowFadeIn;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.mypersonnalcenter_item_0, parent, false);
            holder = new ViewHolderPersonnalCenterItem0(convertView);
            convertView.setTag(holder);
            isShowFadeIn = true;
            holder.ibitem0more.setOnClickListener(mOnClickListener);
            holder.tvlike1.setOnClickListener(mOnClickListener);
            holder.tvcomment1.setOnClickListener(mOnClickListener);
        } else {
            holder = (ViewHolderPersonnalCenterItem0) convertView.getTag();
            isShowFadeIn = holder.pos != position;
        }
        holder.pos = position;

        //显示图片
        List<DemoImgBean> imgs= beanDiscovery.getImages();
        holder.rlimgcontainer.removeAllViewsInLayout();
        for (int i = 0; i < imgs.size(); i++) {
            DemoImgBean bean = imgs.get(i);
            ImageView img = new ImageView(mContext);
            img.setScaleType(ImageView.ScaleType.CENTER);
//            img.setBackgroundColor(sBgColors[i % 4]);
            img.setImageResource(R.drawable.icon_nopic);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(bean.getItemWidth(), bean.getItemHeigh());
            lp.leftMargin = bean.getMarginLeft();
            lp.topMargin = bean.getMarginTop();
            Log.d("wangzixu", "initDiscoveryItem0 pos, i,l,t,w,h = " + position + ", " + i + ", " + bean.getMarginLeft() + ", " + bean.getMarginTop()
                    + ", " + bean.getItemWidth() + ", " + bean.getItemHeigh());
            //Log.d("wangzixu", "getView pos acturlItemW,H = " + position + ", " + bean.getItemWidth() + ", " + bean.getItemHeigh());
            img.setLayoutParams(lp);
            holder.rlimgcontainer.addView(img);
            img.setTag(R.string.TAG_KEY_IS_FADEIN, isShowFadeIn);
            img.setTag(R.string.TAG_KEY_POSITION, i);
            img.setTag(R.string.TAG_KEY_BEAN_FOR_BIGIMG, imgs);
            img.setId(R.id.iv_for_bigimg);
            img.setOnClickListener(mOnClickListener);
            ImageLoaderManager.getInstance().asyncLoadImage(img, bean.getUrl()
                    , bean.getItemWidth(), bean.getItemHeigh());
        }

        //显示标签
        List<DemoTagBean> tags = beanDiscovery.getTags();
        holder.rltagcontainer.removeAllViewsInLayout();
        int textPadding = ImgAndTagWallManager.getInstance(mContext).getTagTextPadding();
        for (int i = 0; i < tags.size(); i++) {
            DemoTagBean bean = tags.get(i);
            String tag = bean.getName();
            TextView tv = new TextView(mContext);
            tv.setIncludeFontPadding(false);
            tv.setTypeface(Typeface.DEFAULT);
            tv.setText(tag);
            tv.setTextColor(mTagTextColor);
            tv.setSingleLine();
            tv.setEllipsize(TextUtils.TruncateAt.END);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(textPadding, textPadding, textPadding, textPadding);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = bean.getMarginLeft();
            lp.topMargin = bean.getMarginTop();
//            Log.d("wangzixu", "initDiscoveryItem0 i,l,t,w,h = " + i + ", " + bean.getMarginLeft() + ", " + bean.getMarginTop()
//                    + ", " + bean.getItemWidth() + ", " + bean.getItemHeigh());
            tv.setLayoutParams(lp);
            holder.rltagcontainer.addView(tv);
            tv.setId(R.id.tv_tag);
            tv.setOnClickListener(mOnClickListener);
        }
        return convertView;
    }

    /**
     * 用户timeline
     */
    public View initDiscoveryItem0(int position, ResponseBeanAlbumInfo.DataEntity beanDiscovery, View convertView, ViewGroup parent) {
        ViewHolderDiscoveryItem0 holder;
        boolean isShowFadeIn;
        boolean isHost = App.user_Id.equals(beanDiscovery.getUser_id());// 判断是否是自己发的
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.distovery_item_0, parent, false);
            holder = new ViewHolderDiscoveryItem0(convertView);
            convertView.setTag(holder);
            isShowFadeIn = true;
            holder.ivmore1.setOnClickListener(mOnClickListener);
            holder.tvlike1.setOnClickListener(mOnClickListener);
            holder.tvcomment1.setOnClickListener(mOnClickListener);
            holder.rl1.setOnClickListener(mOnClickListener);
            holder.ib1.setOnClickListener(mOnClickListener);
        } else {
            holder = (ViewHolderDiscoveryItem0) convertView.getTag();
            isShowFadeIn = holder.pos != position;
        }
        holder.pos = position;

        if (isHost) { //显示或隐藏关注按钮
            holder.ib1.setVisibility(View.INVISIBLE);
        } else {
            holder.ib1.setVisibility(View.VISIBLE);
            holder.ib1.setTag(beanDiscovery);
            holder.ib1.setSelected(beanDiscovery.isFollowed());
        }

        //显示头像，日期，和名称
        if (beanDiscovery.getAvatar_url() != null && !TextUtils.isEmpty(beanDiscovery.getAvatar_url().getS150())) {
            String path;
            if (App.sDensity >= 3) {
                path = beanDiscovery.getAvatar_url().getS150();
            } else {
                path = beanDiscovery.getAvatar_url().getS100();
            }
            ImageLoaderManager.getInstance().asyncLoadCircleImage(holder.imagePh, path
                    , mAvatarW, mAvatarH);
        } else {
            holder.imagePh.setImageResource(R.drawable.icon_login_photo);
        }
        holder.rl1.setTag(beanDiscovery.getUser_id());

        holder.tv1.setText(DataFormatUtil.format(Long.valueOf(beanDiscovery.getCreatetime()) * 1000));
        holder.tv2.setText(beanDiscovery.getNickname());
        if (TextUtils.isEmpty(beanDiscovery.getAlbum_desc())) {
            holder.tv3.setVisibility(View.GONE);
        } else {
            holder.tv3.setVisibility(View.VISIBLE);
            holder.tv3.setText(beanDiscovery.getAlbum_desc());
        }

        //显示图片
        List<DemoImgBean> imgs= beanDiscovery.getImages();
        holder.rl2.removeAllViewsInLayout();
        //Log.d("wangzixu", "initDiscoveryItem0 ----pos, imgCount = " + position + ", " + imgs.size());
        for (int i = 0; i < imgs.size(); i++) {
            DemoImgBean bean = imgs.get(i);
            ImageView img = new ImageView(mContext);
            img.setScaleType(ImageView.ScaleType.CENTER);
//            img.setBackgroundColor(sBgColors[i % 4]);
            img.setImageResource(R.drawable.icon_nopic);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(bean.getItemWidth(), bean.getItemHeigh());
            lp.leftMargin = bean.getMarginLeft();
            lp.topMargin = bean.getMarginTop();
            Log.d("wangzixu", "initDiscoveryItem0 pos, i,l,t,w,h = " + position + ", " + i + ", " + bean.getMarginLeft() + ", " + bean.getMarginTop()
                    + ", " + bean.getItemWidth() + ", " + bean.getItemHeigh());
            //Log.d("wangzixu", "getView pos acturlItemW,H = " + position + ", " + bean.getItemWidth() + ", " + bean.getItemHeigh());
            img.setLayoutParams(lp);
            holder.rl2.addView(img);
            img.setTag(R.string.TAG_KEY_IS_FADEIN, isShowFadeIn);
            img.setTag(R.string.TAG_KEY_POSITION, i);
            img.setTag(R.string.TAG_KEY_BEAN_FOR_BIGIMG, imgs);
            img.setId(R.id.iv_for_bigimg);
            img.setOnClickListener(mOnClickListener);
            ImageLoaderManager.getInstance().asyncLoadImage(img, bean.getUrl()
                    , bean.getItemWidth(), bean.getItemHeigh());
        }

        //显示标签
        List<DemoTagBean> tags = beanDiscovery.getTags();
        holder.rl3.removeAllViewsInLayout();
        int textPadding = ImgAndTagWallManager.getInstance(mContext).getTagTextPadding();
        for (int i = 0; i < tags.size(); i++) {
            DemoTagBean bean = tags.get(i);
            String tag = bean.getName();
            TextView tv = new TextView(mContext);
            tv.setIncludeFontPadding(false);
            tv.setTypeface(Typeface.DEFAULT);
            tv.setText(tag);
            tv.setTextColor(mTagTextColor);
            tv.setSingleLine();
            tv.setEllipsize(TextUtils.TruncateAt.END);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(textPadding, textPadding, textPadding, textPadding);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = bean.getMarginLeft();
            lp.topMargin = bean.getMarginTop();
//            Log.d("wangzixu", "initDiscoveryItem0 i,l,t,w,h = " + i + ", " + bean.getMarginLeft() + ", " + bean.getMarginTop()
//                    + ", " + bean.getItemWidth() + ", " + bean.getItemHeigh());
            tv.setLayoutParams(lp);
            holder.rl3.addView(tv);
            tv.setId(R.id.tv_tag);
            tv.setOnClickListener(mOnClickListener);
        }
        return convertView;
    }

    /**
     * 今日最佳图片
     */
    public View initDiscoveryItem1(int position, ResponseBeanAlbumInfo.DataEntity beanDiscovery, View convertView, ViewGroup parent) {
        ViewHolderDiscoveryItem1 holder;
        boolean isShowFadeIn;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.distovery_item_1, parent, false);
            holder = new ViewHolderDiscoveryItem1(convertView);
            convertView.setTag(holder);
            isShowFadeIn = true;
            holder.tv2.setOnClickListener(mOnClickListener);
        } else {
            holder = (ViewHolderDiscoveryItem1) convertView.getTag();
            isShowFadeIn = holder.pos != position;
        }
        holder.pos = position;

        //显示图片
        List<DemoImgBean> imgs= beanDiscovery.getImages();
        holder.rl2.removeAllViewsInLayout();
        //Log.d("wangzixu", "initDiscoveryItem0 ----pos, imgCount = " + position + ", " + imgs.size());
        for (int i = 0; i < imgs.size(); i++) {
            DemoImgBean bean = imgs.get(i);
            ImageView img = new ImageView(mContext);
            img.setScaleType(ImageView.ScaleType.CENTER);
            //            img.setBackgroundColor(sBgColors[i % 4]);
            img.setImageResource(R.drawable.icon_nopic);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(bean.getItemWidth(), bean.getItemHeigh());
            lp.leftMargin = bean.getMarginLeft();
            lp.topMargin = bean.getMarginTop();
            //Log.d("wangzixu", "initDiscoveryItem0 i,l,t,w,h = " + i + ", " + bean.getMarginLeft() + ", " + bean.getMarginTop()
            //+ ", " + bean.getItemWidth() + ", " + bean.getItemHeigh());
            //Log.d("wangzixu", "getView pos acturlItemW,H = " + position + ", " + bean.getItemWidth() + ", " + bean.getItemHeigh());
            img.setLayoutParams(lp);
            holder.rl2.addView(img);
            img.setTag(R.string.TAG_KEY_IS_FADEIN, isShowFadeIn);
            img.setTag(R.string.TAG_KEY_POSITION, i);
            img.setTag(R.string.TAG_KEY_BEAN_FOR_BIGIMG, imgs);
            img.setId(R.id.iv_for_bigimg);
            img.setOnClickListener(mOnClickListener);
            ImageLoaderManager.getInstance().asyncLoadImage(img, bean.getUrl()
                    , bean.getItemWidth(), bean.getItemHeigh());
        }

        return convertView;
    }

    /**
     * 优秀摄影师推荐
     */
    public View initDiscoveryItem2(int position, ResponseBeanAlbumInfo.DataEntity beanDiscovery, View convertView, ViewGroup parent) {
        ViewHolderDiscoveryItem2 holder;
        boolean isShowFadeIn;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.distovery_item_2, parent, false);
            holder = new ViewHolderDiscoveryItem2(convertView);
            convertView.setTag(holder);
            isShowFadeIn = true;
            holder.rl1.setOnClickListener(mOnClickListener);
            holder.ib1.setOnClickListener(mOnClickListener);
        } else {
            holder = (ViewHolderDiscoveryItem2) convertView.getTag();
            isShowFadeIn = holder.pos != position;
        }
        holder.pos = position;
        holder.ib1.setTag(beanDiscovery);
        holder.ib1.setSelected(beanDiscovery.isFollowed());
        holder.rl1.setTag(beanDiscovery.getUser_id());

        //显示图片
        List<DemoImgBean> imgs= beanDiscovery.getImages();
        holder.rl2.removeAllViewsInLayout();
        //Log.d("wangzixu", "initDiscoveryItem0 ----pos, imgCount = " + position + ", " + imgs.size());
        for (int i = 0; i < imgs.size(); i++) {
            DemoImgBean bean = imgs.get(i);
            ImageView img = new ImageView(mContext);
            img.setScaleType(ImageView.ScaleType.CENTER);
//            img.setBackgroundColor(sBgColors[i % 4]);
            img.setImageResource(R.drawable.icon_nopic);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(bean.getItemWidth(), bean.getItemHeigh());
            lp.leftMargin = bean.getMarginLeft();
            lp.topMargin = bean.getMarginTop();
            //Log.d("wangzixu", "initDiscoveryItem0 i,l,t,w,h = " + i + ", " + bean.getMarginLeft() + ", " + bean.getMarginTop()
            //+ ", " + bean.getItemWidth() + ", " + bean.getItemHeigh());
            //Log.d("wangzixu", "getView pos acturlItemW,H = " + position + ", " + bean.getItemWidth() + ", " + bean.getItemHeigh());
            img.setLayoutParams(lp);
            holder.rl2.addView(img);
            img.setTag(R.string.TAG_KEY_IS_FADEIN, isShowFadeIn);
            img.setTag(R.string.TAG_KEY_POSITION, i);
            img.setTag(R.string.TAG_KEY_BEAN_FOR_BIGIMG, imgs);
            img.setId(R.id.iv_for_bigimg);
            img.setOnClickListener(mOnClickListener);
            ImageLoaderManager.getInstance().asyncLoadImage(img, bean.getUrl()
                    , bean.getItemWidth(), bean.getItemHeigh());
        }
        return convertView;
    }

    public View initDiscoveryItem3(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.distovery_item_3, parent, false);
        }
        return convertView;
    }

//    public void processImgWallAndTags(ArrayList<ResponseBeanDiscovery> data) {
//        for (int i = 0; i <data.size(); i++) {
//            ResponseBeanDiscovery beanDiscovery = data.get(i);
//            int type = beanDiscovery.getType();
//            if (type != 3) { //只有类型3不要排图片墙
//                ArrayList<DemoImgBean> imgs = beanDiscovery.getImgs();
//                initImgsRL(imgs);
//            }
//            if (type == 0) { //带标签的类型，需要处理标签
//                ArrayList<DemoTagBean> tags = beanDiscovery.getTags();
//                initTagsRl(tags);
//            }
//        }
//    }

//    private void initTagsRl(ArrayList<DemoTagBean> tags) {
//        int textPadding = 2 * mTagTextPadding; //左右上下各有5dp的padding，好点击
//        int rlWidth = mScreenW - DisplayUtil.dip2px(mContext, 30); //左右各15dp
//        int baseW = DisplayUtil.dip2px(mContext, 19); //第一行的左边偏移量
//        int currentTop = 0;
//
//        for (int i = 0; i < tags.size(); i++) {
//            DemoTagBean bean = tags.get(i);
//            String tag = bean.getName();
//            int tagw = tag.length() * mTagTextSize + textPadding;
//            bean.setMarginTop(currentTop);
//            bean.setMarginLeft(baseW);
//            baseW = baseW + tagw;
//            int delta = rlWidth - baseW; //当前行右边还剩下多少空间
//            if (delta < 0) {//加上此行溢出了，所以要换行。textPadding + 2 * mTagTextSize
//                i--;
//                currentTop = currentTop + mTagTextSize + textPadding;
//                baseW = 0;
//            }
//        }
//    }

//    /**
//     * 乱序显示照片墙，给出的是没个图片的相对父布局的位置和每个图片的宽高信息，用来在一个relative中不规则显示照片
//     * @param data
//     */
//    private void initImgsRL(ArrayList<DemoImgBean> data) {
//        int baseW = 0; //每行的当前的右边界, 屏幕两遍无边界
//        int currentTop = 0; //当前行的marginTop
//        boolean newline = true; //是否是新起一行
//
//        ArrayList<DemoImgBean> item = new ArrayList<>(); //数据结构上每行为一个单位，每行为一个item
//        for (int i = 0; i < data.size(); i++) {
//            if (newline) { //新的一行开始，初始化一些数据
//                newline = false;
//                item.clear();
//                baseW = 0;
//            }
//            DemoImgBean bean = data.get(i);
//            int imgW = (int)(mBaseHeigh * ((float)bean.getWidth() / bean.getHeigh()) + 0.5f);
//            int delta = mScreenW - (imgW + baseW); //当前行加上当前图片后，条目右边还剩下多少空间
//            if (item.size() > 0) {
//                delta -= mDividerW; //第一张图片开始，中间有间隙
//            }
//            if (delta > 0) { //加上当前的bean后, 同一行还可以再加一个，当前行没完成，继续加下一个bean
//                item.add(bean);
//                baseW = baseW + imgW;
//                if (item.size() > 1) {
//                    baseW += mDividerW; //第一张图片开始，中间有间隙
//                }
//                if (item.size() >= mMaxCountInItem) {//此行已经添加了太多图片了，强制换行
//                    currentTop = completeItem(item, baseW, currentTop);
//                    newline = true;
//                }
//            } else if (delta < -mBaseHeigh / 2) { //加上当前的bean后过宽了，所以当前Bean不计算在内, 当前item已完成，开始下一个item
//                if (item.size() > 0) { //item中至少有一张图片，才可以展示
//                    currentTop = completeItem(item, baseW, currentTop);
//                    i--; //当前bean不能添加到当前item，新起一个item，并且还是添加当前bean
//                    newline = true;
//                } else {
//                    /* item中一张图片都没有，这张图片又太扁且宽，
//                     * 所以导致仅把这一张图片高度放到sBaseH时，宽度已经超出屏幕太多，这种情况需要特殊处理
//                     * 若还是原比例展示，则需要把宽度缩小到屏幕宽度，高度此时可能很小，影响视觉，所以需要规定一个
//                     * 最小的高度，让图片剪裁显示
//                     */
//                    item.add(bean);
//                    int itemW = mScreenW; //这行只有这一张图，行宽就确定了
//                    int itemH = (int) (bean.getHeigh() * (itemW /(float)bean.getWidth()));
//                    if (itemH < mMinItemH) {
//                        itemH = mMinItemH;
//                    }
//                    bean.setItemHeigh(itemH);
//                    bean.setItemWidth(itemW); //最宽不能超过屏幕，只能剪裁了
//                    bean.setMarginLeft(0);
//                    bean.setMarginTop(currentTop);
//                    currentTop = currentTop + itemH + mDividerW;
//                    newline = true;
//                }
//            } else {//加上当前bean之后 正好不过宽，也没法继续加了，当前item已完成，开始下一个item
//                item.add(bean);
//                baseW = baseW + imgW;
//                if (item.size() > 1) {
//                    baseW += mDividerW; //第一张图片开始，中间有间隙
//                }
//                currentTop = completeItem(item, baseW, currentTop);
//                newline = true;
//            }
//        }
//
//        if (newline) {
//            item.clear();
//            baseW = 0;
//        }
//
//        if (item.size() != 0) { //有可能当前行还没有完成，就没有更多数据了，所以此时这个条目还没有complete
//            completeItem(item, baseW, currentTop);
//            item.clear();
//        }
//    }

//    /**
//     * 当前item完成，完成后就可以开始下一条item, 不包括一些baseW特宽等的极端情况，极端情况的判断应该放在产生
//     * 极端情况的判断里单独实现，不能每次正常使用都判断
//     * @param item item，图片集合
//     * @param baseItemW 目前根据baseH计算出的item有多宽，不会超过ScreenW太多，最大 screenW + mBaseHeigh / 2，所以不用
//     *              做acturlItemH 过小的处理，这里面包括了图片间距，计算时应该除去间距，因为行间距是不会方法缩小的，是固定的
//     * @return 返回加上此行后的高度，也就是下一行的marginTop
//     */
//    private int completeItem(ArrayList<DemoImgBean> item, int baseItemW, int currentTop) {
//        int gapW = Math.max(0, (item.size() - 1) * mDividerW);
//        int acturlItemH = (int) (mBaseHeigh * ((mScreenW - gapW) / (float)(baseItemW - gapW)) + 0.5f);
//        int overflowW = 0; //宽度的溢出量，因为有些条目可能太窄且长，放在行中就特别窄，我们只能取规定的最小宽度，所以最后条目总宽度就会溢出
//        DemoImgBean maxWidthBean = null; //此条目中宽度最宽的图片，万一有宽度溢出时，用来吸收溢出的宽度
//        int marginLeft = 0;
//        for (int i = 0; i < item.size(); i++) {
//            DemoImgBean bean = item.get(i);
//            int acturlImgW = (int) (acturlItemH * ((float)bean.getWidth() / bean.getHeigh()) + 0.5f);
//            if (acturlImgW < mMinItemImgW) {
//                //有张图太窄了，最小宽度有限制
//                overflowW = overflowW + (mMinItemImgW - acturlImgW);
//                acturlImgW = mMinItemImgW;
//            }
//            bean.setItemHeigh(acturlItemH);
//            bean.setItemWidth(acturlImgW);
//            bean.setMarginLeft(marginLeft);
//            bean.setMarginTop(currentTop);
//            if (maxWidthBean == null || maxWidthBean.getItemWidth() < acturlImgW) {
//                maxWidthBean = bean;
//            }
//            marginLeft = marginLeft + mDividerW + acturlImgW;
//        }
//        if (overflowW > 0) { //有益处的宽度，需要平摊到此行剩下的图片中
//            Log.d("wangzixu", "completeItem overflowW = " + overflowW);
//            maxWidthBean.setItemWidth(maxWidthBean.getItemWidth() - overflowW);
//        }
//        return currentTop + acturlItemH + mDividerW; //下一行的marginTop
//    }
}
