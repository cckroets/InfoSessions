package com.sixbynine.infosessions.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.data.InfoSessionManager;
import com.sixbynine.infosessions.data.ResponseHandler;
import com.sixbynine.infosessions.model.WaterlooInfoSession;
import com.sixbynine.infosessions.model.company.Address;
import com.sixbynine.infosessions.model.company.Company;
import com.sixbynine.infosessions.model.company.Website;
import com.sixbynine.infosessions.ui.ViewUtil;

import java.text.SimpleDateFormat;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Created by stevenkideckel on 2014-06-12.
 */
public class CompanyInfoFragment extends RoboFragment {

    private static final String KEY_SESSION = "session";

    private static SimpleDateFormat sStartTimeFormat = new SimpleDateFormat("K:mm");
    private static SimpleDateFormat sEndTimeFormat = new SimpleDateFormat("K:mma");
    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("MMMM dd, yyyy");

    @InjectView(R.id.company_name)
    TextView mCompanyName;

    @InjectView(R.id.company_hq)
    TextView mCompanyHq;

    @InjectView(R.id.company_desc)
    TextView mCompanyDescription;

    @InjectView(R.id.company_social_media)
    LinearLayout mSocialMedia;

    @InjectView(R.id.session_date)
    TextView mSessionDate;

    @InjectView(R.id.session_location)
    TextView mSessionLocation;

    @InjectView(R.id.session_time)
    TextView mSessionTime;

    @InjectView(R.id.session_coop)
    CheckBox mSessionCoop;

    @InjectView(R.id.session_graduate)
    CheckBox mSessionGraduate;

    @InjectView(R.id.session_desc)
    TextView mSessionDescription;

    @InjectView(R.id.company_row_hq)
    TableRow mTableRowHq;

    @Inject
    InfoSessionManager mInfoSessionManager;

    Company mCompany;

    WaterlooInfoSession mWaterlooInfoSession;

    public static CompanyInfoFragment createInstance(WaterlooInfoSession session) {
        final Gson gson = new Gson();
        final String sessionJson = gson.toJson(session);
        final CompanyInfoFragment fragment = new CompanyInfoFragment();
        final Bundle args = new Bundle();
        args.putString(KEY_SESSION, sessionJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        final Gson gson = new Gson();
        final String sessionJson = getArguments().getString(KEY_SESSION);
        mWaterlooInfoSession = gson.fromJson(sessionJson, WaterlooInfoSession.class);
        mInfoSessionManager.getCompanyFromSession(mWaterlooInfoSession.getId(), new ResponseHandler<Company>() {
            @Override
            public void onSuccess(Company object) {
                mCompany = object;
            }

            @Override
            public void onFailure(Exception error) {

            }
        });
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateSessionInfo();
        updateCompanyInfo();
    }

    private void updateSessionInfo() {
        mSessionDate.setText(sDateFormat.format(mWaterlooInfoSession.getStartTime().getTimeInMillis()));
        mSessionTime.setText(getTimeString());
        mSessionLocation.setText(mWaterlooInfoSession.getLocation());
        mSessionCoop.setChecked(mWaterlooInfoSession.isForCoops());
        mSessionGraduate.setChecked(mWaterlooInfoSession.isForGraduates());
        ViewUtil.setTextOrGone(mSessionDescription, mWaterlooInfoSession.getDescription());
    }

    private void updateCompanyInfo() {
        if (mCompany == null) {
            return;
        }
        mCompanyName.setText(mCompany.getName());
        mCompanyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCompany.getHomePageUrl() != null) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mCompany.getHomePageUrl())));
                }
            }
        });
        ViewUtil.setTextOrGone(mCompanyDescription, mCompany.getShortDescription());
        getActivity().setTitle(mCompany.getName());
        updateSocialMedia();
        updateHeadquarters();
    }

    private void updateHeadquarters() {
        final Address hq = mCompany.getHeadquarters();
        if (hq == null) {
            mTableRowHq.setVisibility(View.GONE);
        } else if (hq.getCity() != null && hq.getRegion() != null) {
            final String addr = hq.getCity() + ", " + hq.getRegion();
            mCompanyHq.setText(addr);
        } else if (hq.getCity() != null && hq.getCountry() != null) {
            final String addr = hq.getCity() + ", " + hq.getCountry();
            mCompanyHq.setText(addr);
        } else if (hq.getRegion() != null) {
            mCompanyHq.setText(hq.getRegion());
        } else if (hq.getCity() != null) {
            mCompanyHq.setText(hq.getCity());
        } else if (hq.getCountry() != null) {
            mCompanyHq.setText(hq.getCountry());
        } else {
            mTableRowHq.setVisibility(View.GONE);
        }
    }

    private void updateSocialMedia() {
        mSocialMedia.removeAllViews();
        for (final Website website : mCompany.getWebsites()) {
            if (website.getType() == null) {
                continue;
            }
            Log.d(CompanyInfoFragment.class.getName(), website.getType().name() + " : " + website
                    .getUrl());
            final ImageView logo = new ImageView(getActivity());
            logo.setImageResource(website.getType().getLogoDrawableId());
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(website.getUrl()));
                    startActivity(intent);
                }
            });
            final int size = getResources().getDimensionPixelSize(R.dimen
                    .company_social_media_size);
            final int marginEnd = getResources().getDimensionPixelSize(R.dimen
                    .company_social_media_margin);

            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(0, 0, marginEnd, 0);
            mSocialMedia.addView(logo, params);
        }
    }

    private String getTimeString() {
        final String startTime = sStartTimeFormat.format(mWaterlooInfoSession.getStartTime().getTime());
        final String endTime = sEndTimeFormat.format(mWaterlooInfoSession.getEndTime().getTime());
        return startTime + " - " + endTime;
    }
}
