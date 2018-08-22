package com.biz4solutions.provider.adapters;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.provider.R;
import com.biz4solutions.provider.activities.MainActivity;
import com.biz4solutions.provider.databinding.RequestListItemBinding;

import java.util.HashMap;
import java.util.List;

public class RequestListViewAdapter extends BaseAdapter {

    private final MainActivity mainActivity;
    private HashMap<String, String> distanceHashMap;
    private List<EmsRequest> emsRequests;

    public RequestListViewAdapter(MainActivity mainActivity, List<EmsRequest> emsRequests, HashMap<String, String> distanceHashMap) {
        this.mainActivity = mainActivity;
        this.emsRequests = emsRequests;
        this.distanceHashMap = distanceHashMap;
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
        binding.requestListCardiacItem.distanceLoader.setVisibility(View.VISIBLE);
        String distance = mainActivity.getString(R.string.away);
        if (distanceHashMap != null && !distanceHashMap.isEmpty()
                && distanceHashMap.get(emsRequest.getId()) != null && !distanceHashMap.get(emsRequest.getId()).isEmpty()) {
            binding.requestListCardiacItem.distanceLoader.setVisibility(View.GONE);
            distance = distanceHashMap.get(emsRequest.getId()) + mainActivity.getString(R.string.away);
        }
        binding.requestListCardiacItem.txtDistance.setText(distance);

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

    public void add(HashMap<String, String> distanceHashMap) {
        this.distanceHashMap = distanceHashMap;
        notifyDataSetChanged();
    }

}

