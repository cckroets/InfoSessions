package com.sixbynine.infosessions.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sixbynine.infosessions.BuildConfig;
import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.object.InfoSession;
import com.sixbynine.infosessions.object.InfoSessionWaterlooApiDAO;
import com.sixbynine.infosessions.object.company.Company;
import com.sixbynine.infosessions.object.company.Website;
import com.sixbynine.infosessions.object.company.WebsiteCatalogue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by stevenkideckel on 2014-06-12.
 */
public class CompanyInfoFragment extends Fragment implements InfoSession.OnDataLoadedListener {

    private static SimpleDateFormat sStartTimeFormat = new SimpleDateFormat("K:mm");
    private static SimpleDateFormat sEndTimeFormat = new SimpleDateFormat("K:mma");
    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("MMMM dd, yyyy");

    private Callback mCallback;
    private ProgressBar mProgressBar;

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


    public interface Callback {
        public InfoSession getSelectedInfoSession();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Callback) {
            mCallback = (Callback) activity;
        } else {
            throw new IllegalStateException(activity.getClass().toString() + " must implement Callback interface");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company_info, null);

        mScrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        mLogoImageView = (ImageView) view.findViewById(R.id.image_view_logo);
        mCompanySocialMedia = (LinearLayout) view.findViewById(R.id.companySocialMedia);
        mCompanyNameView = (TextView) view.findViewById(R.id.companyName);
        mCompanyHQView = (TextView) view.findViewById(R.id.companyHQ);
        mCompanyWebsiteView = (TextView) view.findViewById(R.id.companyWebsite);
        mCompanyShortDescView = (TextView) view.findViewById(R.id.companyDesc);
        mSessionInfoView = (TextView) view.findViewById(R.id.sessionDesc);
        mSessionLocationView = (TextView) view.findViewById(R.id.sessionLocation);
        mSessionDateView = (TextView) view.findViewById(R.id.sessionDate);
        mSessionTimeView = (TextView) view.findViewById(R.id.sessionTime);

        mSessionCoopView = (CheckBox) view.findViewById(R.id.sessionCoop);
        mSessionGradView = (CheckBox) view.findViewById(R.id.sessionGraduate);

        if (mCallback.getSelectedInfoSession() != null) {
            if (mCallback.getSelectedInfoSession().getCompanyInfo() == null) {
                mProgressBar.setVisibility(View.VISIBLE);
                mScrollView.setVisibility(View.GONE);
                mCallback.getSelectedInfoSession().addOnDataLoadedListener(this);
            } else {
                mProgressBar.setVisibility(View.GONE);
                mScrollView.setVisibility(View.VISIBLE);
                populateFields(mCallback.getSelectedInfoSession());
            }
        } else {
            mProgressBar.setVisibility(View.GONE);
            mScrollView.setVisibility(View.GONE);
        }


        return view;
    }

    @Override
    public void onDataLoaded(InfoSession infoSession) {
        mProgressBar.setVisibility(View.GONE);
        mScrollView.setVisibility(View.VISIBLE);
        populateFields(infoSession);
    }

    private void populateLogo(Bitmap logo) {
        if (logo == null) {
            if (BuildConfig.DEBUG) Log.w("InfoSessions", "Logo was null");
        } else {
            int logoHeight = getResources().getDimensionPixelOffset(R.dimen.company_logo_height);
            double aspectRatio = logo.getWidth() / logo.getHeight();
            int newWidth = (int) (aspectRatio * logoHeight);
            mLogoImageView.setImageBitmap(Bitmap.createScaledBitmap(logo, newWidth, logoHeight, false));
        }
    }

    private String getTimePeriod(Calendar cal1, Calendar cal2) {

        String start = sStartTimeFormat.format(cal1.getTime());
        String end = sEndTimeFormat.format(cal2.getTime());
        return String.format("%s - %s", start, end);
    }

    private String getDate(Calendar date) {
        return sDateFormat.format(date.getTime());
    }

    private static Map<String, Integer> sWebsiteIcons = new HashMap<String, Integer>();
    static {
        sWebsiteIcons.put("facebook", R.drawable.facebook);
        sWebsiteIcons.put("twitter", R.drawable.twitter);
        sWebsiteIcons.put("pinterest", R.drawable.pinterest);
        sWebsiteIcons.put("instagram", R.drawable.instagram);
        sWebsiteIcons.put("linkedin", R.drawable.linkedin);
        // TODO: Homepage and Angellist
    }

    private void populateSocialMedia(WebsiteCatalogue websites) {

        mCompanySocialMedia.removeAllViews();
        for (final Website website : websites) {
            Integer iconId = sWebsiteIcons.get(website.getTitle());
            if (iconId != null) {
                ImageView mediaButton = new ImageView(this.getActivity());
                mediaButton.setImageResource(iconId);
                mediaButton.setPadding(10,0,0,0);
                mediaButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO
                        Toast toast = Toast.makeText(getActivity(), website.getUrl(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                mCompanySocialMedia.addView(mediaButton);
            }
        }
    }

    private void populateFields(InfoSession infoSession) {
        if (infoSession == null) return;
        Company company = infoSession.getCompanyInfo();
        InfoSessionWaterlooApiDAO session = infoSession.getWaterlooApiDAO();

        populateLogo(company.getPrimaryImageBitmap());
        populateSocialMedia(company.getWebsites());
        mCompanyNameView.setText(company.getName());
        mCompanyHQView.setText(company.getHeadquarters().getLocality());
        mCompanyShortDescView.setText(company.getShortDescription());
        mCompanyWebsiteView.setText(session.getWebsite());

        mSessionTimeView.setText(getTimePeriod(session.getStartTime(), session.getEndTime()));
        mSessionLocationView.setText(session.getLocation());
        mSessionDateView.setText(getDate(session.getStartTime()));
        mSessionCoopView.setChecked(session.isForCoopStudents());
        mSessionGradView.setChecked(session.isForGraduatingStudents());
        mSessionInfoView.setText(session.getDescription());
    }
}
