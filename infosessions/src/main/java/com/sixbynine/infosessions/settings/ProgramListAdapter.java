package com.sixbynine.infosessions.settings;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import com.google.inject.Inject;
import com.sixbynine.infosessions.R;
import com.sixbynine.infosessions.data.PreferenceManager;
import com.sixbynine.infosessions.model.programs.Faculty;
import com.sixbynine.infosessions.model.programs.Program;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;


/**
 * Created by stevenkideckel on 15-01-01.
 */
public class ProgramListAdapter extends BaseAdapter{

    private Context mContext;
    private int mSize;
    private Object[] mProgramsAndFaculties;
    private Set<String> mSelectedProgramsAndFaculties;

    public ProgramListAdapter(Context context, Set<String> selectedProgramsAndFaculties){
        mContext = context;
        mSelectedProgramsAndFaculties = selectedProgramsAndFaculties;

        Map<Faculty, ArrayList<Program>> facultyMap = Program.getFacultyMap();
        mSize = Faculty.values().length;
        for(ArrayList<Program> list : facultyMap.values()){
            mSize += list.size();
        }


        mProgramsAndFaculties = new Object[mSize];

        int index = 0;
        for(Faculty f : Faculty.values()){
            mProgramsAndFaculties[index] = f;
            index++;
            for(Program p : facultyMap.get(f)){
                mProgramsAndFaculties[index] = p;
                index++;
            }
        }
    }

    @Override
    public int getCount() {
        return mSize;
    }

    @Override
    public Object getItem(int position) {
        return mProgramsAndFaculties[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        int unit = TypedValue.COMPLEX_UNIT_DIP;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.list_program, null);
            convertView.setTag(convertView.findViewById(R.id.checkbox));
        }
        CompoundButton button = (CompoundButton) convertView.getTag();

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) button.getLayoutParams();

        button.setOnCheckedChangeListener(null);

        Object o = mProgramsAndFaculties[position];
        if(o instanceof Program){
            Program p = (Program) o;
            button.setChecked(mSelectedProgramsAndFaculties.contains(p.name()));
            button.setText(p.getFaculty().name() + " - " + p.getName());
            params.leftMargin = (int) TypedValue.applyDimension(unit, 32, dm);
            button.setTag(p);
        }else if(o instanceof Faculty){
            Faculty f = (Faculty) o;
            button.setChecked(mSelectedProgramsAndFaculties.contains(f.name()));
            button.setText("ALL " + f.name().toUpperCase());
            params.leftMargin = (int) TypedValue.applyDimension(unit, 16, dm);
            button.setTag(f);
        }
        button.setLayoutParams(params);

        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Object o = buttonView.getTag();
                    if(o instanceof Program){
                        mSelectedProgramsAndFaculties.add(((Program) o).name());
                        mSelectedProgramsAndFaculties.add(((Program) o).getFaculty().name());
                        notifyDataSetChanged();
                    }else if(o instanceof Faculty){
                        mSelectedProgramsAndFaculties.add(((Faculty) o).name());
                    }
                }else{
                    Object o = buttonView.getTag();
                    if(o instanceof Program){
                        mSelectedProgramsAndFaculties.remove(((Program) o).name());
                    }else if(o instanceof Faculty){
                        mSelectedProgramsAndFaculties.remove(((Faculty) o).name());
                    }
                }
            }
        });

        return convertView;
    }



    public Set<String> getSelectedList(){
        return mSelectedProgramsAndFaculties;
    }
}
