package com.biz4solutions.reports.views.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biz4solutions.R;
import com.biz4solutions.databinding.FragmentIncidentReportDetailsBinding;
import com.biz4solutions.main.views.activities.MainActivity;


public class IncidentReportDetailsFragment extends Fragment {

    public static final String fragmentName = "IncidentReport";
    private MainActivity mainActivity;
    private FragmentIncidentReportDetailsBinding binding;

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_incident_report_details, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {

            //TODO: need to change the below lines
            mainActivity.navigationView.setCheckedItem(R.id.nav_incidents_reports);
            mainActivity.toolbarTitle.setText(R.string.incidents_reports);
        }
        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
