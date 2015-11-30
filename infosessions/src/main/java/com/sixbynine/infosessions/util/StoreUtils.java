package com.sixbynine.infosessions.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.sixbynine.infosessions.BuildConfig;
import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.app.MyApplication;

/**
 * Created by stevenkideckel on 15-01-02.
 */
public class StoreUtils {

    public static String getShareMessage() {
        final String appPackageName = MyApplication.getInstance().getPackageName();
        if (BuildConfig.AMAZON) {
            return MyApplication.getInstance().getString(R.string.share_app_message,
                    "http://www.amazon.com/gp/mas/dl/android?id=" + appPackageName);
        } else {
            return MyApplication.getInstance().getString(R.string.share_app_message,
                    "https://play.google.com/store/apps/details?id=" + appPackageName);
        }
    }

    public static void shareApp(Activity context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, getShareMessage());
        intent.setType("text/plain");
        context.startActivity(intent);
    }

    public static void launchStoreIntent(Activity context) {
        final String appPackageName = context.getPackageName();
        if (BuildConfig.AMAZON) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("amzn://apps/android?p=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.amazon.com/gp/mas/dl/android?id=" + appPackageName)));
            }
        } else {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }

    }
}
