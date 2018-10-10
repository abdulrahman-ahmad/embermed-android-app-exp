package com.biz4solutions.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.biz4solutions.activities.ProfileActivity;
import com.biz4solutions.profile.R;
import com.biz4solutions.profile.databinding.FragmentChangePasswordBinding;
import com.biz4solutions.viewmodels.ChangePasswordViewModel;

public class ChangePasswordFragment extends Fragment {
    public static final String fragmentName = "ChangePasswordFragment";
    private ProfileActivity activity;
    private ChangePasswordViewModel viewModel;

    public static ChangePasswordFragment newInstance() {
        return new ChangePasswordFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentChangePasswordBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_password, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this, new ChangePasswordViewModel.ChangePasswordViewModelFactory(getContext())).get(ChangePasswordViewModel.class);
        binding.setViewModel(viewModel);
        activity = (ProfileActivity) getActivity();
        if (activity != null) {
            activity.toolbarTitle.setText(R.string.change_password);
        }
        initListeners();
        return binding.getRoot();
    }

    private void initListeners() {
        viewModel.getToastMsg().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
