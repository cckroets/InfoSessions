package com.sixbynine.infosessions.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.app.MyApplication;
import com.sixbynine.infosessions.data.PreferenceManager;
import com.sixbynine.infosessions.model.NotificationPreference;
import com.sixbynine.infosessions.model.programs.Faculty;
import com.sixbynine.infosessions.model.programs.Program;
import com.sixbynine.infosessions.ui.CheckableTextView;
import com.sixbynine.infosessions.ui.HeaderSubheaderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import roboguice.activity.RoboActionBarActivity;
import roboguice.activity.RoboPreferenceActivity;
import roboguice.fragment.provided.RoboPreferenceFragment;
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
                mPreferenceManager.putBoolean(PreferenceManager.Keys.SHOW_COOP_TAB, isChecked);
                syncViews();
            }
        });
        mGraduateCheckableTextView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPreferenceManager.putBoolean(PreferenceManager.Keys.SHOW_GRADUATE_TAB, isChecked);
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

        mCoopCheckableTextView.setChecked(mPreferenceManager.getBoolean(PreferenceManager.Keys.SHOW_COOP_TAB, true));
        mGraduateCheckableTextView.setChecked(mPreferenceManager.getBoolean(PreferenceManager.Keys.SHOW_GRADUATE_TAB, true));

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
