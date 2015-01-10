package com.sixbynine.infosessions.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.sixbynine.infosessions.app.MyApplication;

/**
 * Created by stevenkideckel on 15-01-02.
 */
public class StoreUtils {

    public static Intent getStoreIntent(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + MyApplication.getInstance().getPackageName()));
        return intent;
    }
}
