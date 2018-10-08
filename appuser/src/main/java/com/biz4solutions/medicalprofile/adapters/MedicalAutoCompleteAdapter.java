package com.biz4solutions.medicalprofile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.biz4solutions.R;
import com.biz4solutions.models.MedicalDisease;

import java.util.ArrayList;
import java.util.Collection;


public class MedicalAutoCompleteAdapter extends ArrayAdapter {
    private final int itemLayout;

    private Context context;
    private ArrayList<MedicalDisease> arrayList;

    public MedicalAutoCompleteAdapter(@NonNull Context context, int resource, ArrayList<MedicalDisease> arrayList) {
        super(context, resource, arrayList);
        this.context = context;
        this.arrayList = arrayList;
        itemLayout = resource;
    }


    @Override
    public void addAll(@NonNull Collection collection) {
        MedicalDisease md = new MedicalDisease();
        md.setId("0");
        md.setName("No disease found.");
        if (collection != null && collection.size() > 0) {
            arrayList.clear();
            arrayList.addAll(collection);
            notifyDataSetChanged();
        } else {
            arrayList.clear();
            arrayList.add(md);
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(itemLayout, parent, false);
        }

        view.findViewById(R.id.iv_item_medical_profile_cancel).setVisibility(View.GONE);
        view.findViewById(R.id.iv_item_medical_profile_divider).setVisibility(View.GONE);
        TextView diseaseName = view.findViewById(R.id.tv_item_medical_profile);
        diseaseName.setText(((MedicalDisease) getItem(position)).getName());
        return view;
    }


    @Nullable
    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public int getCount() {
        if (arrayList == null || arrayList.size() == 0)
            return 0;
        return arrayList.size();
    }
}
