package com.biz4solutions.adapters;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.biz4solutions.R;
import com.biz4solutions.activities.MainActivity;
import com.biz4solutions.databinding.RequestListItemBinding;
import com.biz4solutions.models.EmsRequest;

import java.util.List;

public class RequestListViewAdapter extends BaseAdapter {

    private final MainActivity mainActivity;
    private List<EmsRequest> emsRequests;

    public RequestListViewAdapter(MainActivity activity, List<EmsRequest> emsRequests) {
        this.mainActivity = activity;
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

