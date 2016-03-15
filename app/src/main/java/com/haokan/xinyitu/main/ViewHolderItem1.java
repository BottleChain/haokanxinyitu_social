package com.haokan.xinyitu.main;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haokan.xinyitu.R;

public class ViewHolderItem1 {
    public int pos;
    public final RelativeLayout rl2;
    public final TextView tv2;
    public final View root;

    public ViewHolderItem1(View root) {
        rl2 = (RelativeLayout) root.findViewById(R.id.rl_2);
        tv2 = (TextView) root.findViewById(R.id.tv_2);
        this.root = root;
    }
}
