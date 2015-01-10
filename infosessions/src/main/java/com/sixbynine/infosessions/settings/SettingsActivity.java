package com.sixbynine.infosessions.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.google.inject.Inject;
import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.data.PreferenceManager;
import com.sixbynine.infosessions.alarm.NotificationPreference;
import com.sixbynine.infosessions.model.programs.Program;
import com.sixbynine.infosessions.ui.CheckableTextView;
import com.sixbynine.infosessions.ui.HeaderSubheaderView;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by stevenkideckel on 14-12-31.
 */
@ContentView(R.layout.activity_settings)
public class SettingsActivity extends RoboActionBarActivity implements View.OnClickListener {
    @InjectView(R.id.settings_program)
    HeaderSubheaderView mProgramView;
    @InjectView(R.id.settings_coop)
    CheckableTextView mCoopCheckableTextView;
    @InjectView(R.id.settings_graduate)
    CheckableTextView mGraduateCheckableTextView;
    @InjectView(R.id.settings_past)
    CheckableTextView mPastCheckableTextView;
    @InjectView(R.id.settings_today_tab)
    CheckableTextView mTodayCheckableTextView;
    @InjectView(R.id.settings_vibrate)
    CheckableTextView mVibrateCheckableTextView;
    @InjectView(R.id.settings_sound)
    CheckableTextView mSoundCheckableTextView;
    @InjectView(R.id.settings_light)
    CheckableTextView mLightCheckableTextView;

    @Inject
    PreferenceManager mPreferenceManager;

    public static void launchActivityForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProgramView.setOnClickListener(this);
        mCoopCheckableTextView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked && !mGraduateCheckableTextView.isChecked()){
                    mGraduateCheckableTextView.setChecked(true);
                }
                mPreferenceManager.putBoolean(PreferenceManager.Keys.SHOW_COOP, isChecked);
                syncViews();
            }
        });
        mGraduateCheckableTextView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked && !mCoopCheckableTextView.isChecked()){
                    mCoopCheckableTextView.setChecked(true);
                }
                mPreferenceManager.putBoolean(PreferenceManager.Keys.SHOW_GRADUATE, isChecked);
                syncViews();
            }
        });
        mTodayCheckableTextView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPreferenceManager.putBoolean(PreferenceManager.Keys.SHOW_TODAY, isChecked);
                syncViews();
            }
        });
        mPastCheckableTextView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPreferenceManager.putBoolean(PreferenceManager.Keys.SHOW_PAST, isChecked);
                syncViews();
            }
        });
        mVibrateCheckableTextView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                NotificationPreference pref = NotificationPreference.getNotificationPreference(mPreferenceManager);
                if(isChecked){
                    pref.add(NotificationPreference.VIBRATE);
                }else{
                    pref.remove(NotificationPreference.VIBRATE);
                }
                NotificationPreference.saveNotificationPreference(pref, mPreferenceManager);
            }
        });
        mSoundCheckableTextView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                NotificationPreference pref = NotificationPreference.getNotificationPreference(mPreferenceManager);
                if(isChecked){
                    pref.add(NotificationPreference.SOUND);
                }else{
                    pref.remove(NotificationPreference.SOUND);
                }
                NotificationPreference.saveNotificationPreference(pref, mPreferenceManager);
            }
        });
        mLightCheckableTextView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                NotificationPreference pref = NotificationPreference.getNotificationPreference(mPreferenceManager);
                if(isChecked){
                    pref.add(NotificationPreference.LIGHTS);
                }else{
                    pref.remove(NotificationPreference.LIGHTS);
                }
                NotificationPreference.saveNotificationPreference(pref, mPreferenceManager);
            }
        });
        syncViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_program:
                onProgramClicked();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        setResult(RESULT_OK);
    }

    private void syncViews() {
        Set<String> programs = mPreferenceManager.getStrings(PreferenceManager.Keys.INTERESTED_PROGRAMS);
        String text;
        if (programs.isEmpty()) {
            text = getString(R.string.none_selected);
        } else {
            Iterator<String> iter = programs.iterator();
            text = getNameOfProgramOrFaculty(iter.next());
            while (iter.hasNext()) {
                text += ", " + getNameOfProgramOrFaculty(iter.next());
            }
        }
        mProgramView.setSubheaderText(text);

        mCoopCheckableTextView.setChecked(mPreferenceManager.getBoolean(PreferenceManager.Keys.SHOW_COOP, true));
        mGraduateCheckableTextView.setChecked(mPreferenceManager.getBoolean(PreferenceManager.Keys.SHOW_GRADUATE, true));
        mPastCheckableTextView.setChecked(mPreferenceManager.getBoolean(PreferenceManager.Keys.SHOW_PAST, false));
        mTodayCheckableTextView.setChecked(mPreferenceManager.getBoolean(PreferenceManager.Keys.SHOW_TODAY, true));

        NotificationPreference pref = NotificationPreference.getNotificationPreference(mPreferenceManager);
        mVibrateCheckableTextView.setChecked(pref.hasVibrate());
        mSoundCheckableTextView.setChecked(pref.hasSound());
        mLightCheckableTextView.setChecked(pref.hasLights());
    }

    private void onProgramClicked() {
        final Set<String> programs = mPreferenceManager.getStrings(PreferenceManager.Keys.INTERESTED_PROGRAMS);
        final ListView listView = new ListView(this);
        final ProgramListAdapter adapter = new ProgramListAdapter(this, new HashSet<>(programs));
        listView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        listView.setAdapter(adapter);
        listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        new AlertDialog.Builder(this)
                .setTitle(R.string.select_programs)
                .setView(listView)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPreferenceManager.putStrings(PreferenceManager.Keys.INTERESTED_PROGRAMS, adapter.getSelectedList());
                        syncViews();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    private static String getNameOfProgramOrFaculty(String name) {
        try {
            Program program = Program.fromName(name);
            if (program != null) {
                return program.getName() + " (" + program.getFaculty().name() + ")";
            }
        } catch (Exception e) {

        }

        return name;
    }


}
