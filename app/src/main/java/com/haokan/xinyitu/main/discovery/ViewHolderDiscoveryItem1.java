package com.haokan.xinyitu.main.discovery;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haokan.xinyitu.R;

public class ViewHolderDiscoveryItem1 {
    public int pos;
    public final RelativeLayout rl2;
    public final TextView tv2;
    public final View root;

    public ViewHolderDiscoveryItem1(View root) {
        rl2 = (RelativeLayout) root.findViewById(R.id.rl_img_container);
        tv2 = (TextView) root.findViewById(R.id.tv_2);
        this.root = root;
    }
}
