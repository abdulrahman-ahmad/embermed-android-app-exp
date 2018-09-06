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
import com.biz4solutions.provider.databinding.FragmentTriageCallerFeedbackBinding;
import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.provider.utilities.NavigationUtil;
import com.biz4solutions.utilities.CommonFunctions;

public class TriageCallerFeedbackFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "TriageCallerFeedbackFragment";
    private MainActivity mainActivity;
    private FragmentTriageCallerFeedbackBinding binding;

    public TriageCallerFeedbackFragment() {
        // Required empty public constructor
    }

    public static TriageCallerFeedbackFragment newInstance() {
        return new TriageCallerFeedbackFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_triage_caller_feedback, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.triage_service);
            NavigationUtil.getInstance().hideMenu(mainActivity);
        }
        initView();
        initClickListeners();
        return binding.getRoot();
    }

    private void initView() {
        binding.edtReason.setOnTouchListener(CommonFunctions.getInstance().scrollOnTouchListener(binding.edtReason.getId()));
    }

    private void initClickListeners() {
        binding.btnSubmit.setOnClickListener(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mainActivity != null) {
            NavigationUtil.getInstance().showMenu(mainActivity);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (checkIsWhereCallerGoSelected()) {
                    if (isFormValid()) {
                        Toast.makeText(mainActivity, "Success...", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private boolean isFormValid() {
        if (binding.edtReason.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter reason.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkIsWhereCallerGoSelected() {
        if (binding.rdbGoToEr.isChecked()) {
            return true;
        } else if (binding.rdbGoToUrgentCare.isChecked()) {
            return true;
        } else if (binding.rdbGoToPcp.isChecked()) {
            return true;
        }
        Toast.makeText(mainActivity, "Please select where caller should go.", Toast.LENGTH_SHORT).show();
        return false;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}