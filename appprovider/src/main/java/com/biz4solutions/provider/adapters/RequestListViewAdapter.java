package com.biz4solutions.provider.adapters;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.biz4solutions.provider.R;
import com.biz4solutions.provider.databinding.RequestListItemBinding;
import com.biz4solutions.models.EmsRequest;

import java.util.List;

public class RequestListViewAdapter extends BaseAdapter {

    private List<EmsRequest> emsRequests;

    public RequestListViewAdapter(List<EmsRequest> emsRequests) {
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
        RequestListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.request_list_item, parent, false);
        EmsRequest emsRequest = emsRequests.get(position);

        String name = emsRequest.getUserDetails().getFirstName() + " " + emsRequest.getUserDetails().getLastName();
        binding.requestListCardiacItem.txtName.setText(name);
        String genderAge = emsRequest.getUserDetails().getGender() + ", " + emsRequest.getUserDetails().getAge() + "yrs";
        binding.requestListCardiacItem.txtGenderAge.setText(genderAge);

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

