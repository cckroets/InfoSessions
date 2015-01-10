package com.sixbynine.infosessions.model.social;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * @author curtiskroetsch
 */
public class SocialMediaWebsite implements SocialMedium {

    int mResourceId;

    public SocialMediaWebsite(int res) {
        this.mResourceId = res;
    }

    public static Intent createWebIntent(String url) {
        final Intent socialIntent = new Intent(Intent.ACTION_VIEW);
        socialIntent.setData(Uri.parse(url));
        return socialIntent;
    }

    @Override
    public int getResource() {
        return mResourceId;
    }

    @Override
    public Intent getIntent(String url, Context context) {
        return createWebIntent(url);
    }
}
