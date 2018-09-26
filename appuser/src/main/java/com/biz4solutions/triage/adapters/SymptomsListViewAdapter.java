package com.biz4solutions.triage.adapters;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.biz4solutions.R;
import com.biz4solutions.databinding.SymptomsListItemBinding;
import com.biz4solutions.interfaces.OnTargetClickListener;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.models.Symptom;
import com.biz4solutions.utilities.TargetViewUtil;

import java.util.List;

public class SymptomsListViewAdapter extends BaseAdapter {

    private final OnTargetClickListener onTargetClickListener;
    private final MainActivity mainActivity;
    private List<Symptom> symptoms;
    private String selectedSymptomId;

    public SymptomsListViewAdapter(MainActivity mainActivity, List<Symptom> symptoms, OnTargetClickListener onTargetClickListener) {
        this.mainActivity = mainActivity;
        this.symptoms = symptoms;
        this.onTargetClickListener = onTargetClickListener;
    }

    @Override
    public int getCount() {
        if (symptoms != null) {
            return symptoms.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    @SuppressLint("ViewHolder")
    public View getView(final int position, View convertView, ViewGroup parent) {
        final SymptomsListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.symptoms_list_item, parent, false);
        Symptom symptom = symptoms.get(position);
        binding.txtName.setText(symptom.getName());
        if (selectedSymptomId != null && selectedSymptomId.equals("" + symptom.getId())) {
            binding.txtName.setBackground(ContextCompat.getDrawable(binding.txtName.getContext(), R.drawable.shape_symptoms_border_selected));
            binding.txtName.setTextColor(ContextCompat.getColor(binding.txtName.getContext(), R.color.white));
        } else {
            binding.txtName.setBackground(ContextCompat.getDrawable(binding.txtName.getContext(), R.drawable.shape_symptoms_border));
            binding.txtName.setTextColor(ContextCompat.getColor(binding.txtName.getContext(), R.color.secondary_text_color));
        }
        if ((selectedSymptomId == null || selectedSymptomId.isEmpty()) && mainActivity.isTutorialMode && onTargetClickListener != null) {
            if (position == 3) {
                TargetViewUtil.showTargetRoundedForBtn(mainActivity,
                        binding.txtName, mainActivity.getString(R.string.tutorial_title_symptoms_list),
                        mainActivity.getString(R.string.tutorial_description_symptoms_list),
                        new OnTargetClickListener() {
                            @Override
                            public void onTargetClick() {
                                setSelectedSymptomId("4");
                                onTargetClickListener.onTargetClick();
                            }
                        });
            }
        }

        return binding.getRoot();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void add(List<Symptom> symptoms) {
        this.symptoms = symptoms;
        notifyDataSetChanged();
    }

    public void setSelectedSymptomId(String symptomId) {
        selectedSymptomId = symptomId;
        notifyDataSetChanged();
    }

    public String getSelectedSymptomId() {
        return selectedSymptomId;
    }

}