package com.sixbynine.infosessions.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sixbynine.infosessions.R;

import java.text.SimpleDateFormat;

import roboguice.fragment.RoboFragment;

/**
 * Created by stevenkideckel on 2014-06-12.
 */
public class CompanyInfoFragment extends RoboFragment {

    private static SimpleDateFormat sStartTimeFormat = new SimpleDateFormat("K:mm");
    private static SimpleDateFormat sEndTimeFormat = new SimpleDateFormat("K:mma");
    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("MMMM dd, yyyy");

    private ProgressBar mProgressBar;
    private MenuItem mFavourite;

    private ScrollView mScrollView;
    private ImageView mLogoImageView;
    private LinearLayout mCompanySocialMedia;
    private TextView mCompanyNameView;
    private TextView mCompanyHQView;
    private TextView mCompanyWebsiteView;
    private TextView mCompanyShortDescView;

    private TextView mSessionInfoView;
    private TextView mSessionLocationView;
    private TextView mSessionDateView;
    private TextView mSessionTimeView;
    private CheckBox mSessionCoopView;
    private CheckBox mSessionGradView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        /*mFavourite = menu.findItem(R.id.action_favourite);
        final int sid = mCallback.getSelectedInfoSession().getWaterlooApiDAO().getId();
        boolean fav = getSharedPreferences().getBoolean(sid + "", false);
        if (BuildConfig.DEBUG) Log.d("FAV", sid + (fav ? " is favourite" : " is not favourite"));
        mFavourite.setChecked(! fav);
        onFavouritePress();*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_favourite) {
            //onFavouritePress();
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_company_info, container, false);
    }


}
