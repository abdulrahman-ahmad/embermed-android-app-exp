package com.biz4solutions.provider.triage.views.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.biz4solutions.provider.R;
import com.biz4solutions.provider.databinding.FragmentTriageIncidentReportBinding;
import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.provider.utilities.NavigationUtil;
import com.biz4solutions.utilities.CommonFunctions;

public class TriageIncidentReportFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "TriageIncidentReportFragment";
    private MainActivity mainActivity;
    private FragmentTriageIncidentReportBinding binding;

    public TriageIncidentReportFragment() {
        // Required empty public constructor
    }

    public static TriageIncidentReportFragment newInstance() {
        return new TriageIncidentReportFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_triage_incident_report, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.triage_service);
            NavigationUtil.getInstance().showMenu(mainActivity);
        }
        initView();
        initClickListeners();
        return binding.getRoot();
    }

    private void initClickListeners() {
        binding.btnSubmit.setOnClickListener(this);
        binding.tvSeeMore.setOnClickListener(this);
        binding.tvSeeLess.setOnClickListener(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mainActivity != null) {
            NavigationUtil.getInstance().hideMenu(mainActivity);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (isFormValid()) {
                    Toast.makeText(mainActivity, "Success...", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_see_more:
                binding.tvSeeMore.setVisibility(View.GONE);
                binding.tvSeeLess.setVisibility(View.VISIBLE);
                binding.ivThreeDots.setVisibility(View.GONE);
                binding.tvReason.setMaxLines(100);
                break;
            case R.id.tv_see_less:
                binding.tvSeeLess.setVisibility(View.GONE);
                binding.tvSeeMore.setVisibility(View.VISIBLE);
                binding.ivThreeDots.setVisibility(View.VISIBLE);
                binding.tvReason.setMaxLines(4);
                break;
        }
    }

    private void initView() {
        binding.edtComment.setOnTouchListener(CommonFunctions.getInstance().scrollOnTouchListener(binding.edtComment.getId()));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private boolean isFormValid() {
        if (binding.edtTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), R.string.error_empty_incident_report_title, Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.edtComment.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), R.string.error_empty_incident_report_comment, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}