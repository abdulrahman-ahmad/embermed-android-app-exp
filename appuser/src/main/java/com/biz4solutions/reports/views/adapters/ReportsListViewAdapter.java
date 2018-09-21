package com.biz4solutions.reports.views.adapters;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.biz4solutions.R;
import com.biz4solutions.databinding.ReportsListItemBinding;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.utilities.Constants;

import java.util.List;

public class ReportsListViewAdapter extends BaseAdapter {

    private List<EmsRequest> emsRequests;

    public ReportsListViewAdapter(List<EmsRequest> emsRequests) {
        this.emsRequests = emsRequests;
    }

    @Override
    public int getCount() {
        if (emsRequests != null) {
            return emsRequests.size();
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
        ReportsListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.reports_list_item, parent, false);
        binding.requestListCardiacItem.cardView.setVisibility(View.GONE);
        binding.requestListTriageItem.cardView.setVisibility(View.GONE);
        EmsRequest emsRequest = emsRequests.get(position);

        String name = emsRequest.getUserDetails().getFirstName() + " " + emsRequest.getUserDetails().getLastName();
        String genderAge = emsRequest.getUserDetails().getGender() + ", " + emsRequest.getUserDetails().getAge() + "yrs";

        if (Constants.STATUS_IMMEDIATE.equals("" + emsRequest.getPriority())) {
            binding.requestListCardiacItem.cardView.setVisibility(View.VISIBLE);
            binding.requestListCardiacItem.txtName.setText(name);
            binding.requestListCardiacItem.txtGenderAge.setText(genderAge);
            binding.requestListCardiacItem.distanceLoader.setVisibility(View.GONE);
            binding.requestListCardiacItem.txtTime.setVisibility(View.GONE);
            binding.requestListCardiacItem.txtDistance.setVisibility(View.GONE);
        } else {
            binding.requestListTriageItem.cardView.setVisibility(View.VISIBLE);
            binding.requestListTriageItem.txtName.setText(name);
            binding.requestListTriageItem.txtGenderAge.setText(genderAge);
            binding.requestListTriageItem.distanceLoader.setVisibility(View.GONE);
            binding.requestListTriageItem.txtTime.setVisibility(View.GONE);
            binding.requestListTriageItem.txtDistance.setVisibility(View.GONE);
        }
        return binding.getRoot();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void add(List<EmsRequest> emsRequests) {
        this.emsRequests = emsRequests;
        notifyDataSetChanged();
    }

}