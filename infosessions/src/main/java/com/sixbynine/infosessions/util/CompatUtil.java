package com.sixbynine.infosessions.util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;

import java.util.List;

/**
 * Created by stevenkideckel on 15-01-25.
 */
public class CompatUtil {

    public static boolean canHandleCalendarIntent(Activity context) {
        if (Build.VERSION.SDK_INT >= 14) {
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setType("vnd.android.cursor.item/event");
            PackageManager manager = context.getPackageManager();
            List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
            return infos.size() > 0;
        } else {
            return false;
        }
    }
}
