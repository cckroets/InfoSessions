package com.sixbynine.infosessions.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.object.InfoSession;
import com.sixbynine.infosessions.object.company.Company;

/**
 * Created by stevenkideckel on 2014-06-12.
 */
public class CompanyInfoFragment extends Fragment implements InfoSession.OnDataLoadedListener {

    private Callback mCallback;
    private ProgressBar mProgressBar;
    private ScrollView mScrollView;

    private TextView mCompanyNameTextView;

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
        mCompanyNameTextView = (TextView) mScrollView.findViewById(R.id.company_name_text_view);

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

    private void populateFields(InfoSession infoSession) {
        if (infoSession == null) return;
        Company company = infoSession.getCompanyInfo();
        mCompanyNameTextView.setText(company.getName());
    }
}
