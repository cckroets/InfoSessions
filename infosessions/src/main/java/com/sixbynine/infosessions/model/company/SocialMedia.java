package com.sixbynine.infosessions.model.company;

import android.support.annotation.DrawableRes;

import com.sixbynine.infosessions.R;

/**
 * @author curtiskroetsch
 */
public enum SocialMedia {
    LINKEDIN(R.drawable.ic_logo_linkedin),
    FACEBOOK(R.drawable.ic_logo_facebook),
    INSTAGRAM(R.drawable.ic_logo_instagram),
    TWITTER(R.drawable.ic_logo_twitter);

    @DrawableRes
    int mDrawableResId;

    private SocialMedia(@DrawableRes int resource) {
        mDrawableResId = resource;
    }

    public int getLogoDrawableId() {
        return mDrawableResId;
    }

}
