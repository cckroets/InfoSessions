package com.sixbynine.infosessions.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by stevenkideckel on 14-12-31.
 */
public class CheckableTextView extends RelativeLayout{

    private TextView mMainTextView;
    private TextView mSubTextView;
    private CheckBox mCheckBox;

    public CheckableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
