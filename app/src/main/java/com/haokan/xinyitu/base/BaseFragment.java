package com.haokan.xinyitu.base;

import android.app.Fragment;

public abstract class BaseFragment extends Fragment {
    protected boolean mIsDestory;

    public void longClik() {

    }

    @Override
    public void onDestroy() {
        mIsDestory = true;
        super.onDestroy();
    }
}
