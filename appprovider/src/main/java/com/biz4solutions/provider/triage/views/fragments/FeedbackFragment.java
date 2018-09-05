package com.biz4solutions.provider.triage.views.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biz4solutions.provider.R;
import com.biz4solutions.provider.databinding.FragmentFeedbackBinding;
import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.provider.utilities.NavigationUtil;

public class FeedbackFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "FeedbackFragment";
    private MainActivity mainActivity;
    private FragmentFeedbackBinding binding;

    public FeedbackFragment() {
        // Required empty public constructor
    }

    public static FeedbackFragment newInstance() {
        return new FeedbackFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feedback, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.triage_service);
            NavigationUtil.getInstance().hideMenu(mainActivity);
        }
        initClickListeners();
        return binding.getRoot();
    }

    private void initClickListeners() {
        binding.btnSubmit.setOnClickListener(this);
        binding.tvSkip.setOnClickListener(this);
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
                break;
            case R.id.tv_skip:
                break;
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}