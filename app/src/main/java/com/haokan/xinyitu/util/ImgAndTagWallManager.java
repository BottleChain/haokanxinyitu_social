package com.haokan.xinyitu.util;

import android.content.Context;
import android.util.Log;

import com.haokan.xinyitu.main.DemoImgBean;
import com.haokan.xinyitu.main.DemoTagBean;

import java.util.ArrayList;
import java.util.List;

public class ImgAndTagWallManager {
    private int mScreenW;
    private int mBaseHeigh; //每行的基准高度
    private int mDividerW; //分割线的宽度
    private int mMinItemH; //特别扁的图片的最小高度
    private int mMinItemImgW; //特别长的图片的最小宽度
    private int mMaxCountInItem = 5; //每个条目最多容许几张图片

    private int mTagTextSize; //tag的字体大小
    private int mTagTextPadding; //左右上下各有5dp的padding，好点击
    private int mTagRlMargin; //左右各15dp margin
    private int mTagBaseW; //tags第一行的左边偏移量

    private static ImgAndTagWallManager singInstance;
    public static ImgAndTagWallManager getInstance(Context context) {
        if (singInstance == null) {
            synchronized (ImgAndTagWallManager.class) {
                if (singInstance == null) {
                    singInstance = new ImgAndTagWallManager(context);
                }
            }
        }
        return singInstance;
    }

    public int getTagTextPadding() {
        return mTagTextPadding;
    }

    private ImgAndTagWallManager(Context context) {
        mScreenW = context.getResources().getDisplayMetrics().widthPixels;
        mDividerW = DisplayUtil.dip2px(context, 3);
        mBaseHeigh = DisplayUtil.dip2px(context, 110);
        mMinItemH = DisplayUtil.dip2px(context, 60);
        mMinItemImgW = DisplayUtil.dip2px(context, 40);

        mTagTextSize = DisplayUtil.sp2px(context, 13);
        mTagTextPadding = DisplayUtil.dip2px(context, 5);
        mTagBaseW = DisplayUtil.dip2px(context, 18);
        mTagRlMargin = DisplayUtil.dip2px(context, 15);
    }

    public void initTagsWallForItem0(List<DemoTagBean> tags) {
        int textPadding = 2 * mTagTextPadding; //左右上下各有5dp的padding，好点击，所以罗列高度时需要加上2倍padding值
        int rlWidth = mScreenW - mTagRlMargin * 2; //左右各15dp
        int baseW = mTagBaseW;
        int currentTop = 0;
        initTagsWall(tags, textPadding, textPadding, rlWidth, baseW, currentTop, mTagTextSize, mTagTextSize, 0, 0, textPadding, 0);
    }

    /**
     * 标签显示的位置计算，暂时只考虑了水平有drawable情况，竖直没有drawable。
     * @param tags 标签实体类
     * @param textPaddingW 标签的水平padding，左padding+右padding
     * @param textPaddingH 标签的竖直padding，上padding + 下padding
     * @param rlWidth 标签父容器的width
     * @param baseW 第一行tag的左偏移量
     * @param currentTop 第一行tag的顶部起始偏移量
     * @param textSize 字体大小
     * @param textHeight 字体高，可能drawable比较高，所以字高不一定能用textsize
     * @param drawablePadding 水平drawable padding
     * @param drawableWidth 水平drawable宽
     */
    public void initTagsWall(List<DemoTagBean> tags, int textPaddingW, int textPaddingH
            , int rlWidth, int baseW, int currentTop, int textSize, int textHeight, int drawablePadding, int drawableWidth
            , int textMarginW, int textMarginH) {

        int currentLineCount = 0; //当前行有几个标签了，每行至少有一个标签，长过一行，后面省略
        for (int i = 0; i < tags.size(); i++) {
            DemoTagBean bean = tags.get(i);
            String tag = bean.getName();
            int tagw = tag.length() * textSize + textPaddingW + drawablePadding + drawableWidth + textMarginW; //tag有多宽，字数*字宽 + xxx
            bean.setMarginTop(currentTop);
            bean.setMarginLeft(baseW);
            baseW = baseW + tagw;
            int delta = rlWidth - baseW; //当前行右边还剩下多少空间
            if (delta < 0 && currentLineCount > 0) {//加上此行溢出了，所以要换行。textPadding + 2 * mTagTextSize
                currentLineCount = 0;
                i--;
                currentTop = currentTop + textHeight + textPaddingH + textMarginH;
                baseW = 0;
            } else {
                currentLineCount ++;
            }
        }
    }

//    public void processImgAndTagWallForItem0(ArrayList<ResponseBeanDiscovery> data) {
//        for (int i = 0; i <data.size(); i++) {
//            ResponseBeanDiscovery beanDiscovery = data.get(i);
//            int type = beanDiscovery.getType();
//            if (type != 3) { //只有类型3不要排图片墙
//                ArrayList<DemoImgBean> imgs = beanDiscovery.getImgs();
//                initImgsWall(imgs);
//            }
//            if (type == 0) { //带标签的类型，需要处理标签
//                ArrayList<DemoTagBean> tags = beanDiscovery.getTags();
//                initTagsWallForItem0(tags);
//            }
//        }
//    }

    /**
     * 乱序显示照片墙，给出的是没个图片的相对父布局的位置和每个图片的宽高信息，用来在一个relative中不规则显示照片
     * @param data
     */
    public void initImgsWall(List<DemoImgBean> data) {
        int baseW = 0; //每行的当前的右边界, 屏幕两遍无边界
        int currentTop = 0; //当前行的marginTop
        boolean newline = true; //是否是新起一行

        ArrayList<DemoImgBean> item = new ArrayList<>(); //数据结构上每行为一个单位，每行为一个item
        for (int i = 0; i < data.size(); i++) {
            if (newline) { //新的一行开始，初始化一些数据
                newline = false;
                item.clear();
                baseW = 0;
            }
            DemoImgBean bean = data.get(i);
            int imgW = (int)(mBaseHeigh * ((float)bean.getWidth() / bean.getHeight()) + 0.5f);
            int delta = mScreenW - (imgW + baseW); //当前行加上当前图片后，条目右边还剩下多少空间
            if (item.size() > 0) {
                delta -= mDividerW; //第一张图片开始，中间有间隙
            }
            if (delta > 0) { //加上当前的bean后, 同一行还可以再加一个，当前行没完成，继续加下一个bean
                item.add(bean);
                baseW = baseW + imgW;
                if (item.size() > 1) {
                    baseW += mDividerW; //第一张图片开始，中间有间隙
                }
                if (item.size() >= mMaxCountInItem) {//此行已经添加了太多图片了，强制换行
                    currentTop = completeItem(item, baseW, currentTop);
                    newline = true;
                }
            } else if (delta < -mBaseHeigh / 2) { //加上当前的bean后过宽了，所以当前Bean不计算在内, 当前item已完成，开始下一个item
                if (item.size() > 0) { //item中至少有一张图片，才可以展示
                    currentTop = completeItem(item, baseW, currentTop);
                    i--; //当前bean不能添加到当前item，新起一个item，并且还是添加当前bean
                    newline = true;
                } else {
                    /* item中一张图片都没有，这张图片又太扁且宽，
                     * 所以导致仅把这一张图片高度放到sBaseH时，宽度已经超出屏幕太多，这种情况需要特殊处理
                     * 若还是原比例展示，则需要把宽度缩小到屏幕宽度，高度此时可能很小，影响视觉，所以需要规定一个
                     * 最小的高度，让图片剪裁显示
                     */
                    item.add(bean);
                    int itemW = mScreenW; //这行只有这一张图，行宽就确定了
                    int itemH = (int) (bean.getHeight() * (itemW /(float)bean.getWidth()));
                    if (itemH < mMinItemH) {
                        itemH = mMinItemH;
                    }
                    bean.setItemHeigh(itemH);
                    bean.setItemWidth(itemW); //最宽不能超过屏幕，只能剪裁了
                    bean.setMarginLeft(0);
                    bean.setMarginTop(currentTop);
                    currentTop = currentTop + itemH + mDividerW;
                    newline = true;
                }
            } else {//加上当前bean之后 正好不过宽，也没法继续加了，当前item已完成，开始下一个item
                item.add(bean);
                baseW = baseW + imgW;
                if (item.size() > 1) {
                    baseW += mDividerW; //第一张图片开始，中间有间隙
                }
                currentTop = completeItem(item, baseW, currentTop);
                newline = true;
            }
        }

        if (newline) {
            item.clear();
            baseW = 0;
        }

        if (item.size() != 0) { //有可能当前行还没有完成，就没有更多数据了，所以此时这个条目还没有complete
            completeItem(item, baseW, currentTop);
            item.clear();
        }
    }

    /**
     * 当前item完成，完成后就可以开始下一条item, 不包括一些baseW特宽等的极端情况，极端情况的判断应该放在产生
     * 极端情况的判断里单独实现，不能每次正常使用都判断
     * @param item item，图片集合
     * @param baseItemW 目前根据baseH计算出的item有多宽，不会超过ScreenW太多，最大 screenW + mBaseHeigh / 2，所以不用
     *              做acturlItemH 过小的处理，这里面包括了图片间距，计算时应该除去间距，因为行间距是不会方法缩小的，是固定的
     * @return 返回加上此行后的高度，也就是下一行的marginTop
     */
    private int completeItem(ArrayList<DemoImgBean> item, int baseItemW, int currentTop) {
        int gapW = Math.max(0, (item.size() - 1) * mDividerW);
        int acturlItemH = (int) (mBaseHeigh * ((mScreenW - gapW) / (float)(baseItemW - gapW)) + 0.5f);
        int overflowW = 0; //宽度的溢出量，因为有些条目可能太窄且长，放在行中就特别窄，我们只能取规定的最小宽度，所以最后条目总宽度就会溢出
        DemoImgBean maxWidthBean = null; //此条目中宽度最宽的图片，万一有宽度溢出时，用来吸收溢出的宽度
        int marginLeft = 0;
        for (int i = 0; i < item.size(); i++) {
            DemoImgBean bean = item.get(i);
            int acturlImgW = (int) (acturlItemH * ((float)bean.getWidth() / bean.getHeight()) + 0.5f);
            if (acturlImgW < mMinItemImgW) {
                //有张图太窄了，最小宽度有限制
                overflowW = overflowW + (mMinItemImgW - acturlImgW);
                acturlImgW = mMinItemImgW;
            }
            bean.setItemHeigh(acturlItemH);
            bean.setItemWidth(acturlImgW);
            bean.setMarginLeft(marginLeft);
            bean.setMarginTop(currentTop);
            if (maxWidthBean == null || maxWidthBean.getItemWidth() < acturlImgW) {
                maxWidthBean = bean;
            }
            marginLeft = marginLeft + mDividerW + acturlImgW;
        }
        if (overflowW > 0) { //有益处的宽度，需要平摊到此行剩下的图片中
            Log.d("wangzixu", "completeItem overflowW = " + overflowW);
            maxWidthBean.setItemWidth(maxWidthBean.getItemWidth() - overflowW);
        }
        return currentTop + acturlItemH + mDividerW; //下一行的marginTop
    }
}
