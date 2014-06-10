package com.sixbynine.infosessions.ui;

import android.view.View;
import android.widget.TextView;

/**
 * @author curtiskroetsch
 */
public final class UIUtil {

    /**
     * Set the text for an inner text view inside of view
     *
     * @param id The id of the inner text view
     * @param view The parent of the text view
     * @param text The new text for the text view
     */
    public static void setTextForView(int id, View view, String text) {
        ((TextView) view.findViewById(id)).setText(text);
    }
}
