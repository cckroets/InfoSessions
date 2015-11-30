package com.sixbynine.infosessions.model.social;

import android.content.Context;
import android.content.Intent;

/**
 * @author curtiskroetsch
 */
public interface SocialMedium {

    int getResource();

    Intent getIntent(String url, Context context);
}
