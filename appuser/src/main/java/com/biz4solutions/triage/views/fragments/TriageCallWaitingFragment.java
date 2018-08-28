package com.biz4solutions.triage.views.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biz4solutions.R;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.databinding.FragmentTriageCallWaitingBinding;
import com.biz4solutions.interfaces.OnBackClickListener;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.utilities.NavigationUtil;

public class TriageCallWaitingFragment extends Fragment {

    public static final String fragmentName = "TriageCallWaitingFragment";
    private MainActivity mainActivity;

    public TriageCallWaitingFragment() {
        // Required empty public constructor
    }

    public static TriageCallWaitingFragment newInstance(EmsRequest data) {
        return new TriageCallWaitingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentTriageCallWaitingBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_triage_call_waiting, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.ems_alert);
            NavigationUtil.getInstance().showBackArrow(mainActivity, new OnBackClickListener() {
                @Override
                public void onBackPress() {
                    mainActivity.unconsciousOnBackClick();
                }
            });
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}