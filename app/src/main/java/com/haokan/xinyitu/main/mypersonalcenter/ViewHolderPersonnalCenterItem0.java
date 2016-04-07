package com.haokan.xinyitu.main.mypersonalcenter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haokan.xinyitu.R;

public class ViewHolderPersonnalCenterItem0 {
    public int pos;
    public final RelativeLayout rlimgcontainer;
    public final TextView tvdesc;
    public final ImageView ivtag1;
    public final RelativeLayout rltagcontainer;
    public final ImageButton ibitem0more;
    public final TextView tvlike1;
    public final TextView tvcomment1;
    public final View root;

    public ViewHolderPersonnalCenterItem0(View root) {
        rlimgcontainer = (RelativeLayout) root.findViewById(R.id.rl_img_container);
        tvdesc = (TextView) root.findViewById(R.id.tv_desc);
        ivtag1 = (ImageView) root.findViewById(R.id.iv_tag_1);
        rltagcontainer = (RelativeLayout) root.findViewById(R.id.rl_tag_container);
        ibitem0more = (ImageButton) root.findViewById(R.id.ib_item0_more);
        tvlike1 = (TextView) root.findViewById(R.id.tv_like_1);
        tvcomment1 = (TextView) root.findViewById(R.id.tv_comment_1);
        this.root = root;
    }
}
