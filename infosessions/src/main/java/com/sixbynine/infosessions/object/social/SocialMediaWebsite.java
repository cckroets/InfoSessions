package com.sixbynine.infosessions.object.social;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * @author curtiskroetsch
 */
public class SocialMediaWebsite implements SocialMedium {

    int res;

    public SocialMediaWebsite(int res) {
        this.res = res;
    }

    public static Intent createWebIntent(String url) {
        Intent socialIntent = new Intent(Intent.ACTION_VIEW);
        socialIntent.setData(Uri.parse(url));
        return socialIntent;
    }

    @Override
    public int getResource() {
        return res;
    }

    @Override
    public Intent getIntent(String url, Context context) {
        return createWebIntent(url);
    }
}
