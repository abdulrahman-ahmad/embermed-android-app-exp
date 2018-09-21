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
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.models.User;
import com.biz4solutions.utilities.NavigationUtil;


public class IncidentReportDetailsFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "IncidentReport";
    private MainActivity mainActivity;
    private FragmentIncidentReportDetailsBinding binding;
    private final static String REQUEST_DETAILS = "REQUEST_DETAILS";
    private final static String USER_DETAILS = "USER_DETAILS";

    public IncidentReportDetailsFragment() {
        // Required empty public constructor
    }

    public static IncidentReportDetailsFragment newInstance(EmsRequest request, User userDetails) {
        IncidentReportDetailsFragment fragment = new IncidentReportDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(REQUEST_DETAILS, request);
        args.putSerializable(USER_DETAILS, userDetails);
        fragment.setArguments(args);
        return fragment;
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
            mainActivity.navigationView.setCheckedItem(R.id.nav_incident_reports);
            mainActivity.toolbarTitle.setText(R.string.incident_report);
            NavigationUtil.getInstance().showBackArrow(mainActivity);
        }
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mainActivity != null) {
            NavigationUtil.getInstance().hideBackArrow(mainActivity);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListeners();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    private void initListeners() {
        binding.layoutCallerPending.llCallerPending.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.layoutCallerPending.llCallerPending.getId()) {
            openFeedbackFragment();
        }
    }

    private void openFeedbackFragment() {
        if (mainActivity != null)
            mainActivity.openFeedbackFragment("abc", true);
    }
}
