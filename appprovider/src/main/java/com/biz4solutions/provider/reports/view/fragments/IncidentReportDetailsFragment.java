package com.biz4solutions.provider.reports.view.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biz4solutions.provider.R;
import com.biz4solutions.provider.databinding.FragmentNewsFeedBinding;
import com.biz4solutions.provider.main.views.activities.MainActivity;

public class IncidentReportDetailsFragment extends Fragment {

    public static final String fragmentName = "IncidentReportDetailsFragment";
    private MainActivity mainActivity;

    public IncidentReportDetailsFragment() {
        // Required empty public constructor
    }

    public static IncidentReportDetailsFragment newInstance() {
        return new IncidentReportDetailsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentNewsFeedBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_incident_report_details, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_news_feed);
            mainActivity.toolbarTitle.setText(R.string.news_feed);
        }
        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
