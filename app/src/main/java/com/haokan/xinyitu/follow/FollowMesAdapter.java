package com.haokan.xinyitu.follow;

import android.app.Activity;
import android.view.View;

import com.haokan.xinyitu.R;

import java.util.ArrayList;

public class FollowMesAdapter extends MyFollowsAdapter {

    public FollowMesAdapter(Activity context, ArrayList<ResponseBeanOtherUserInfo.FollowUserInfoBean> data) {
        super(context, data);
    }

    @Override
    protected View getConvertView() {
        return View.inflate(mContext, R.layout.followme_activity_item, null);
    }
}
