package com.haokan.xinyitu.base;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.bugtags.library.Bugtags;
import com.haokan.xinyitu.R;
import com.haokan.xinyitu.util.SystemBarTintManager;

/**
 * activity基类，实现了状态栏半透明黑的效果
 */
public class BaseActivity extends Activity {
    protected boolean mIsDestory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (hasStatusBar() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
            // 设置状态栏状态
            systemBarTintManager.setStatusBarTintEnabled(true);
            // 设置状态栏颜色
            systemBarTintManager.setStatusBarTintColor(getResources().getColor(R.color.main_color_actionbar_item01));
        }
    }

    protected void onStatusBarClick() {

    }

    protected boolean hasStatusBar() {
        return true;
    }

    protected void onResume() {
        super.onResume();
        Bugtags.onResume(this);
    }

    protected void onPause() {
        super.onPause();
        Bugtags.onPause(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //注：回调 3
        Bugtags.onDispatchTouchEvent(this, event);
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        System.gc();
        mIsDestory = true;
        super.onDestroy();
    }
}
