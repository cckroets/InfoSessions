package com.sixbynine.infosessions.object.social;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
* @author curtiskroetsch
*/
public interface SocialMedium {

    public int getResource();

    public Intent getIntent(String url, Context context);

}
