package com.haokan.xinyitu.main.discovery;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haokan.xinyitu.R;

public class ViewHolderDiscoveryItem2 {
    public int pos;
    public final ImageButton ib1;
    public final ImageView image1;
    public final TextView tv2;
    public final TextView tv3;
    public final RelativeLayout rl1;
    public final RelativeLayout rl2;
    public final View root;

    public ViewHolderDiscoveryItem2(View root) {
        ib1 = (ImageButton) root.findViewById(R.id.ib_1);
        image1 = (ImageView) root.findViewById(R.id.image_1);
        tv2 = (TextView) root.findViewById(R.id.tv_2);
        tv3 = (TextView) root.findViewById(R.id.tv_desc);
        rl1 = (RelativeLayout) root.findViewById(R.id.rl_item0_1);
        rl2 = (RelativeLayout) root.findViewById(R.id.rl_img_container);
        this.root = root;
    }
}
