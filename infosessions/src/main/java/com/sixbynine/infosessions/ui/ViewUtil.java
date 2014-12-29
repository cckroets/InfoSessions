package com.sixbynine.infosessions.ui;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;

import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Transformation;

/**
 * @author curtiskroetsch
 */
public final class ViewUtil {

    private ViewUtil() {
    }

    public static Drawable getTintedDrawable(Resources resources, @DrawableRes int drawableId, int color) {
        final Drawable drawable = resources.getDrawable(drawableId);
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    public static void animateDrawable(final Drawable drawable, int defaultColor, int color) {
        final ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), defaultColor, color);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final int deltaColor = (Integer) animation.getAnimatedValue();
                drawable.setColorFilter(deltaColor, PorterDuff.Mode.MULTIPLY);
            }
        });
        animator.start();
    }

    public static Transformation createLogoTransformation() {
        return new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(2)
                .cornerRadiusDp(2)
                .oval(false)
                .build();
    }
}
