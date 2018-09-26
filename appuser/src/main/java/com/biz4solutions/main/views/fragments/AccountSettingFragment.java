package com.biz4solutions.main.views.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biz4solutions.R;
import com.biz4solutions.databinding.FragmentAccountSettingBinding;
import com.biz4solutions.fragments.view.activities.ProfileActivity;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.utilities.NavigationUtil;

public class AccountSettingFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "AccountSettingFragment";
    private MainActivity mainActivity;
    private FragmentAccountSettingBinding binding;

    public AccountSettingFragment() {
        // Required empty public constructor
    }

    public static AccountSettingFragment newInstance() {
        return new AccountSettingFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_setting, container, false);
        initActivity();
        return binding.getRoot();
    }


    private void initActivity() {
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_account_settings);
            mainActivity.toolbarTitle.setText(R.string.account_settings);
            NavigationUtil.getInstance().showBackArrow(mainActivity);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        NavigationUtil.getInstance().hideBackArrow(mainActivity);
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
        binding.cvMyProfile.setOnClickListener(this);
        binding.cvSavedCards.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.cvMyProfile.getId()) {
            // mainActivity.openProfileFragment();
            startActivity(new Intent(mainActivity, ProfileActivity.class));
        } else if (v.getId() == binding.cvSavedCards.getId()) {
            //mainActivity.openEditProfileFragment();
        }
    }
}
