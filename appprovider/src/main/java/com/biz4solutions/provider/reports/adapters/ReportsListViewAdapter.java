package com.biz4solutions.provider.reports.adapters;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.provider.R;
import com.biz4solutions.provider.databinding.ReportsListItemBinding;
import com.biz4solutions.utilities.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReportsListViewAdapter extends BaseAdapter {

    private List<EmsRequest> emsRequests;
    private SimpleDateFormat formatterDate = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
    private Calendar calendar = Calendar.getInstance();

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
        String requestDate = "";
        if (emsRequest.getRequestTime() > 0) {
            calendar.setTimeInMillis(emsRequest.getRequestTime());
            requestDate = ("" + formatterDate.format(calendar.getTime()));
        }

        if (Constants.STATUS_IMMEDIATE.equals("" + emsRequest.getPriority())) {
            binding.requestListCardiacItem.cardView.setVisibility(View.VISIBLE);
            binding.requestListCardiacItem.txtName.setText(name);
            binding.requestListCardiacItem.txtGenderAge.setText(genderAge);
            binding.requestListCardiacItem.distanceLoader.setVisibility(View.GONE);
            binding.requestListCardiacItem.txtDistance.setVisibility(View.GONE);
            binding.requestListCardiacItem.txtTime.setText(requestDate);
            binding.requestListCardiacItem.txtBottomTime.setVisibility(View.VISIBLE);
            if (!emsRequest.getIsIncidentReportSubmitted()) {
                binding.requestListCardiacItem.txtBottomTime.setText(R.string.pending);
            } else {
                binding.requestListCardiacItem.txtBottomTime.setText("");
            }
        } else {
            binding.requestListTriageItem.cardView.setVisibility(View.VISIBLE);
            binding.requestListTriageItem.txtName.setText(name);
            binding.requestListTriageItem.txtGenderAge.setText(genderAge);
            binding.requestListTriageItem.distanceLoader.setVisibility(View.GONE);
            binding.requestListTriageItem.txtDistance.setVisibility(View.GONE);
            binding.requestListTriageItem.txtTime.setText(requestDate);
            binding.requestListTriageItem.txtBottomTime.setVisibility(View.VISIBLE);
            if (!emsRequest.getIsIncidentReportSubmitted()) {
                binding.requestListTriageItem.txtBottomTime.setText(R.string.pending);
            } else {
                binding.requestListTriageItem.txtBottomTime.setText("");
            }
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