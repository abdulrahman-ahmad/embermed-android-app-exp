package com.biz4solutions.provider.registration.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class SpinnerAdapter<T> extends ArrayAdapter<T> {
    private final List<T> data;
    private final int resourceId;
    private final int dropDownViewId;
    private final LayoutInflater inflater;

    public SpinnerAdapter(Context context, int resourceId, List<T> data, int dropDownViewId) {
        super(context, resourceId, data);
        this.data = data;
        this.resourceId = resourceId;
        this.dropDownViewId = dropDownViewId;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView == null)
            view = inflater.inflate(dropDownViewId, parent, false);

//        TextView textView = (TextView) view.findViewById(R.id.textView);
//        if (data.get(position) instanceof String) {
//        }
//        if (position == 0) {
//            textView.setEnabled(false);
//        } else {
//            textView.setEnabled(true);
//        }

        return view;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView == null)
            view = inflater.inflate(resourceId, parent, false);

//        TextView textView = (TextView) view.findViewById(R.id.textView);
//        if (data.get(position) instanceof CustomerDTO) {
//            textView.setText(((CustomerDTO) data.get(position)).getName());
//        }
//            textView.setText(displayname);
//        if (position == 0) {
//            textView.setEnabled(false);
//        } else {
//            textView.setEnabled(true);
//        }

        return view;
    }
}


