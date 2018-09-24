package com.biz4solutions.tutorial.views.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.biz4solutions.R;
import com.biz4solutions.databinding.FragmentHowItWorksBinding;
import com.biz4solutions.main.views.activities.MainActivity;

public class HowItWorksFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "HowItWorksFragment";
    private MainActivity mainActivity;
    private FragmentHowItWorksBinding binding;

    public HowItWorksFragment() {
        // Required empty public constructor
    }

    public static HowItWorksFragment newInstance() {
        return new HowItWorksFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_how_it_works, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_how_it_works);
            mainActivity.toolbarTitle.setText(R.string.how_it_works);
        }
        return binding.getRoot();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListeners();
    }

    private void initListeners() {
        binding.cvCardiacService.setOnClickListener(this);
        binding.cvTriageService.setOnClickListener(this);
        binding.cvAddSubscription.setOnClickListener(this);
        binding.cvChangeSubscription.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_cardiac_service:
                openAlertFragment();
                break;
            case R.id.cv_triage_service:
                break;
            default:
                Toast.makeText(mainActivity, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void openAlertFragment() {
        mainActivity.reOpenDashBoardFragment();
    }
}