package com.haokan.xinyitu.base;

import android.app.Fragment;

public abstract class BaseFragment extends Fragment {
    protected boolean mIsDestory;

    @Override
    public void onDestroy() {
        mIsDestory = true;
        super.onDestroy();
    }
}
