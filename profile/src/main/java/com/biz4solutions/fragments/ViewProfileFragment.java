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
import com.biz4solutions.models.User;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.profile.R;
import com.biz4solutions.profile.databinding.FragmentViewProfileBinding;
import com.biz4solutions.utilities.Constants;
import com.biz4solutions.viewmodels.ViewProfileViewModel;

public class ViewProfileFragment extends Fragment {
    public static final String fragmentName = "ViewProfileFragment";
    private ProfileActivity activity;
    private ViewProfileViewModel viewModel;

    public static ViewProfileFragment newInstance() {
        return new ViewProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentViewProfileBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_profile, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this, new ViewProfileViewModel.ViewProfileViewModelFactory(getContext())).get(ViewProfileViewModel.class);
        binding.setViewModel(viewModel);
        initActivity();
        initListeners();
        setUserData();
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

    public void setUserData() {
        User user = SharedPrefsManager.getInstance().retrieveUserPreference(activity, Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY);
        viewModel.setUserData(user);
        if (user != null && Constants.ROLE_NAME_USER.equals("" + user.getRoleName())) {
            viewModel.getUserDetails();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (activity != null) {
            activity.hideEditOption(false);
        }
    }

    private void initActivity() {
        activity = (ProfileActivity) getActivity();
    }
}
