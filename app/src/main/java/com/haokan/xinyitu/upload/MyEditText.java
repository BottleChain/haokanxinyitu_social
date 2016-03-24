package com.haokan.xinyitu.upload;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class MyEditText extends EditText {
    public MyEditText(Context context) {
        super(context);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (mOnSelectionChangedListener != null) {
            mOnSelectionChangedListener.onSelectionChanged(selStart, selEnd);
        }
    }

    private onSelectionChangedListener mOnSelectionChangedListener;
    public interface onSelectionChangedListener {
        void onSelectionChanged(int selStart, int selEnd);
    }
    public void setOnSelectionChangedListener(onSelectionChangedListener onSelectionChangedListener) {
        mOnSelectionChangedListener = onSelectionChangedListener;
    }
}
