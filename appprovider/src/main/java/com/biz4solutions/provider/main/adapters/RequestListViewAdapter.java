package com.biz4solutions.provider.main.adapters;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;

import com.biz4solutions.customs.taptargetview.TapRectTargetView;
import com.biz4solutions.interfaces.OnTargetClickListener;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.provider.R;
import com.biz4solutions.provider.databinding.RequestListItemBinding;
import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.utilities.Constants;
import com.biz4solutions.utilities.TargetViewUtil;

import java.util.HashMap;
import java.util.List;

public class RequestListViewAdapter extends BaseAdapter {

    private final MainActivity mainActivity;
    private final OnTargetClickListener onTargetClickListener;
    private HashMap<String, String> distanceHashMap;
    private HashMap<String, String> durationHashMap;
    private List<EmsRequest> emsRequests;
    private TapRectTargetView targetView;

    public RequestListViewAdapter(MainActivity mainActivity, List<EmsRequest> emsRequests, HashMap<String, String> distanceHashMap, HashMap<String, String> durationHashMap, OnTargetClickListener onTargetClickListener) {
        this.mainActivity = mainActivity;
        this.emsRequests = emsRequests;
        this.distanceHashMap = distanceHashMap;
        this.durationHashMap = durationHashMap;
        this.onTargetClickListener = onTargetClickListener;
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
        final RequestListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.request_list_item, parent, false);
        //binding.requestListCardiacItem.cardView.setVisibility(View.GONE);
        //binding.requestListTriageItem.cardView.setVisibility(View.GONE);
        EmsRequest emsRequest = emsRequests.get(position);
        String name = emsRequest.getUserDetails().getFirstName() + " " + emsRequest.getUserDetails().getLastName();
        String genderAge = emsRequest.getUserDetails().getGender() + ", " + emsRequest.getUserDetails().getAge() + "yrs";
        String distance = mainActivity.getString(R.string.away);
        if (distanceHashMap != null && !distanceHashMap.isEmpty()
                && distanceHashMap.get(emsRequest.getId()) != null && !distanceHashMap.get(emsRequest.getId()).isEmpty()) {
            distance = distanceHashMap.get(emsRequest.getId()) + mainActivity.getString(R.string.away);
        }

        if (Constants.STATUS_IMMEDIATE.equals("" + emsRequest.getPriority())) {
            binding.requestListCardiacItem.cardView.setVisibility(View.VISIBLE);
            binding.requestListTriageItem.cardView.setVisibility(View.GONE);
            binding.requestListCardiacItem.txtName.setText(name);
            binding.requestListCardiacItem.txtGenderAge.setText(genderAge);
            binding.requestListCardiacItem.distanceLoader.setVisibility(View.VISIBLE);
            if (distanceHashMap != null && !distanceHashMap.isEmpty()
                    && distanceHashMap.get(emsRequest.getId()) != null && !distanceHashMap.get(emsRequest.getId()).isEmpty()) {
                binding.requestListCardiacItem.distanceLoader.setVisibility(View.GONE);
            }
            if (durationHashMap != null && !durationHashMap.isEmpty()
                    && durationHashMap.get(emsRequest.getId()) != null && !durationHashMap.get(emsRequest.getId()).isEmpty()) {
                binding.requestListCardiacItem.txtTime.setText(durationHashMap.get(emsRequest.getId()));
            }
            binding.requestListCardiacItem.txtDistance.setText(distance);
            if (mainActivity.isTutorialMode && mainActivity.tutorialId == 1 && onTargetClickListener != null) {
                if (position == 1) {
                    binding.requestListCardiacItem.cardView.getViewTreeObserver()
                            .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                @Override
                                public boolean onPreDraw() {
                                    try {
                                        if (targetView == null) {
                                            targetView = TargetViewUtil.showTargetRectForBtn(mainActivity,
                                                    binding.requestListCardiacItem.cardView, mainActivity.getString(R.string.tutorial_title_request_list),
                                                    mainActivity.getString(R.string.tutorial_description_cardiac_request_list),
                                                    onTargetClickListener);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return true;
                                }
                            });
                }
            }
        } else {
            binding.requestListTriageItem.cardView.setVisibility(View.VISIBLE);
            binding.requestListCardiacItem.cardView.setVisibility(View.GONE);
            binding.requestListTriageItem.txtName.setText(name);
            binding.requestListTriageItem.txtGenderAge.setText(genderAge);
            binding.requestListTriageItem.distanceLoader.setVisibility(View.VISIBLE);
            if (distanceHashMap != null && !distanceHashMap.isEmpty()
                    && distanceHashMap.get(emsRequest.getId()) != null && !distanceHashMap.get(emsRequest.getId()).isEmpty()) {
                binding.requestListTriageItem.distanceLoader.setVisibility(View.GONE);
            }
            if (durationHashMap != null && !durationHashMap.isEmpty()
                    && durationHashMap.get(emsRequest.getId()) != null && !durationHashMap.get(emsRequest.getId()).isEmpty()) {
                binding.requestListTriageItem.txtTime.setText(durationHashMap.get(emsRequest.getId()));
            }
            binding.requestListTriageItem.txtDistance.setText(distance);
            if (mainActivity.isTutorialMode && mainActivity.tutorialId == 2 && onTargetClickListener != null) {
                if (position == 2) {
                    binding.requestListTriageItem.cardView.getViewTreeObserver()
                            .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                @Override
                                public boolean onPreDraw() {
                                    try {
                                        if (targetView == null) {
                                            targetView = TargetViewUtil.showTargetRectForBtn(mainActivity,
                                                    binding.requestListTriageItem.cardView, mainActivity.getString(R.string.tutorial_title_request_list),
                                                    mainActivity.getString(R.string.tutorial_description_triage_request_list),
                                                    onTargetClickListener);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return true;
                                }
                            });
                }
            }
        }
        return binding.getRoot();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void add(List<EmsRequest> emsRequests, HashMap<String, String> durationHashMap) {
        this.emsRequests = emsRequests;
        this.durationHashMap = durationHashMap;
        notifyDataSetChanged();
    }

    public void add(HashMap<String, String> distanceHashMap) {
        this.distanceHashMap = distanceHashMap;
        notifyDataSetChanged();
    }

    public void addValue(HashMap<String, String> durationHashMap) {
        this.durationHashMap = durationHashMap;
        notifyDataSetChanged();
    }

}

