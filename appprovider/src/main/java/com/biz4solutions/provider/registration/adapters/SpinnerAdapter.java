package com.biz4solutions.provider.registration.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.biz4solutions.models.CprInstitute;
import com.biz4solutions.models.Occupation;
import com.biz4solutions.provider.R;

import java.util.ArrayList;

public class SpinnerAdapter<T> extends ArrayAdapter<T> {
    private final ArrayList<T> data;
    private final int resourceId;
    private final int dropDownViewId;
    private final LayoutInflater inflater;

    public SpinnerAdapter(Context context, int resourceId, ArrayList<T> data, int dropDownViewId) {
        super(context, resourceId, data);
        this.data = data;
        this.resourceId = resourceId;
        this.dropDownViewId = dropDownViewId;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateData(ArrayList<T> newData) {
        data.clear();
        data.addAll(newData);
        if (newData != null && newData.size() > 0) {
            if ((data.get(0)) instanceof Occupation) {
                Occupation occupation = new Occupation();
                occupation.setName("Select Occupation");
                data.add(0, (T) occupation);
            } else {
                CprInstitute cprInstitute = new CprInstitute();
                cprInstitute.setName("Select Institute");
                data.add(0, (T) cprInstitute);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView == null)
            view = inflater.inflate(dropDownViewId, parent, false);

        TextView textView = view.findViewById(R.id.textView);
        if (data.get(position) instanceof Occupation) {
            textView.setText(((Occupation) data.get(position)).getName());
        }
        if (data.get(position) instanceof CprInstitute) {
            textView.setText(((CprInstitute) data.get(position)).getName());
        }
        if (position == 0) {
            textView.setEnabled(false);
        } else {
            textView.setEnabled(true);
        }
        return view;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView == null)
            view = inflater.inflate(resourceId, parent, false);

        TextView textView = view.findViewById(R.id.textView);
        if (data.get(position) instanceof Occupation) {
            textView.setText(((Occupation) data.get(position)).getName());
        }
        if (data.get(position) instanceof CprInstitute) {
            textView.setText(((CprInstitute) data.get(position)).getName());
        }
        if (position == 0) {
            textView.setEnabled(false);
        } else {
            textView.setEnabled(true);
        }
        return view;
    }
}


