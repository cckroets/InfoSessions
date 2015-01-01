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
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.app.MyApplication;
import com.sixbynine.infosessions.data.PreferenceManager;
import com.sixbynine.infosessions.model.programs.Faculty;
import com.sixbynine.infosessions.model.programs.Program;
import com.sixbynine.infosessions.ui.HeaderSubheaderView;

import java.util.ArrayList;
import java.util.HashMap;
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
public class SettingsActivity extends RoboActionBarActivity implements View.OnClickListener{

    @InjectView(R.id.settings_program)
    HeaderSubheaderView mProgramView;

    @Inject
    PreferenceManager mPreferenceManager;

    public static void launchActivityForResult(Activity activity, int requestCode){
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProgramView.setOnClickListener(this);
        syncViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.settings_program:
               onProgramClicked();
                break;
        }
    }

    private void syncViews(){
        Set<String> programs = mPreferenceManager.getStrings(PreferenceManager.Keys.INTERESTED_PROGRAMS);
        String text;
        switch(programs.size()){
            case 0:
                text="None selected";
                break;
            case 1:
                Iterator<String> iter = programs.iterator();
                text = getNameOfProgramOrFaculty(iter.next());
                break;
            case 2:
                Iterator<String> iter2 = programs.iterator();
                text = getNameOfProgramOrFaculty(iter2.next());
                text += ", " + getNameOfProgramOrFaculty(iter2.next());
                break;
            default:
                text = "Multiple Selected";

        }
        mProgramView.setSubheaderText(text);
    }

    private void onProgramClicked(){
        final Set<String> programs = mPreferenceManager.getStrings(PreferenceManager.Keys.INTERESTED_PROGRAMS);
        final ListView listView = new ListView(this);
        final ProgramListAdapter adapter = new ProgramListAdapter(this, programs);
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


        /*//assemble a String array from the available programs and faculties to choose from
        Map<Faculty, ArrayList<Program>> facultyMap = Program.getFacultyMap();

        int size = Faculty.values().length;
        for(ArrayList<Program> list : facultyMap.values()){
            size += list.size();
        }

        String[] choices = new String[size];
        boolean[] selected = new boolean[size];
        final Map<Integer, Object> indexMap = new HashMap<>(size);
        int index = 0;
        for(Faculty f : Faculty.values()){
            indexMap.put(index, f);
            choices[index] = getResources().getString(R.string.faculty_all, f.name());
            selected[index] = programs.contains(f.name());
            index++;
            for(Program p : facultyMap.get(f)){
                indexMap.put(index, p);
                choices[index] = f.name() + " - " + p.getName();
                selected[index] = programs.contains(p.name());
                index++;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_programs)
                .setMultiChoiceItems(choices, selected, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        Object o = indexMap.get(which);
                        if(isChecked){
                            if(o instanceof Program){
                                programs.add(((Program) o).name());
                            }else if(o instanceof Faculty){
                                programs.add(((Faculty) o).name());
                            }
                        }else{
                            if(o instanceof Program){
                                programs.remove(((Program) o).name());
                            }else if(o instanceof Faculty){
                                programs.remove(((Faculty) o).name());
                            }
                        }
                    }
                })
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPreferenceManager.putStrings(KEY_PROGRAMS, programs);
                        syncViews();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();*/

    }

    private static String getNameOfProgramOrFaculty(String name){
        try{
            Program program = Program.fromName(name);
            if(program != null){
                return program.getName() + " (" + program.getFaculty().name() + ")";
            }
        }catch(Exception e){

        }

        return name;
    }


}
