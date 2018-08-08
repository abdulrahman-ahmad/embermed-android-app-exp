package com.biz4solutions.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biz4solutions.R;
import com.biz4solutions.activities.MainActivity;
import com.biz4solutions.databinding.FragmentEmsAlertUnconsciousBinding;

public class EmsAlertUnconsciousFragment extends Fragment {

    public static final String fragmentName = "EmsAlertUnconsciousFragment";
    private MainActivity mainActivity;

    public EmsAlertUnconsciousFragment() {
        // Required empty public constructor
    }

    public static EmsAlertUnconsciousFragment newInstance() {
        return new EmsAlertUnconsciousFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentEmsAlertUnconsciousBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ems_alert_unconscious, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.news_feed);
        }
        return binding.getRoot();
    }
}
