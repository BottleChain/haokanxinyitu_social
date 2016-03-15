package com.haokan.xinyitu.main;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haokan.xinyitu.R;

public class ViewHolderItem0 {
    public int pos;
    public final ImageView imagePh;
    public final TextView tv1;
    public final TextView tv2;
    public final RelativeLayout rl1;
    public final ImageButton ib1;
    public final RelativeLayout rl2;
    public final TextView tv3;
    public final ImageView ivtag1;
    public final RelativeLayout rl3;
    public final ImageButton ivmore1;
    public final TextView tvlike1;
    public final TextView tvcomment1;
    public final View root;

    public ViewHolderItem0(View root) {
        imagePh = (ImageView) root.findViewById(R.id.image_1);
        tv1 = (TextView) root.findViewById(R.id.tv_1);
        tv2 = (TextView) root.findViewById(R.id.tv_2);
        rl1 = (RelativeLayout) root.findViewById(R.id.rl_item0_1);
        ib1 = (ImageButton) root.findViewById(R.id.ib_1);
        rl2 = (RelativeLayout) root.findViewById(R.id.rl_2);
        tv3 = (TextView) root.findViewById(R.id.tv_3);
        ivtag1 = (ImageView) root.findViewById(R.id.iv_tag_1);
        rl3 = (RelativeLayout) root.findViewById(R.id.rl_3);
        ivmore1 = (ImageButton) root.findViewById(R.id.ib_item0_more);
        tvlike1 = (TextView) root.findViewById(R.id.tv_like_1);
        tvcomment1 = (TextView) root.findViewById(R.id.tv_comment_1);
        this.root = root;
    }
}
