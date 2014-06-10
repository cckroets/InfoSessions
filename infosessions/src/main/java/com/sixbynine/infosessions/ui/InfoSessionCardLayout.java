package com.sixbynine.infosessions.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.sixbynine.infosessions.R;

/**
 * @author curtiskroetsch
 */
public class InfoSessionCardLayout extends RelativeLayout {

    private boolean lastCategory = false;

    private static int[] MORE_STATES = new int[] { R.attr.state_last_of_category };

    public InfoSessionCardLayout(Context context) {
        super(context);
    }

    public InfoSessionCardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] state =  super.onCreateDrawableState(extraSpace + MORE_STATES.length);
        if (lastCategory) {
            mergeDrawableStates(state, MORE_STATES);
        }
        return state;
    }

    public void setLastCategory(boolean val) {
        this.lastCategory = val;
        refreshDrawableState();
    }


}
