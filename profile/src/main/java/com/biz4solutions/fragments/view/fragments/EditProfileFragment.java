package com.biz4solutions.fragments.view.fragments;

import android.arch.lifecycle.ViewModelProvider;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biz4solutions.fragments.view.activities.ProfileActivity;
import com.biz4solutions.fragments.view.viewmodels.EditProfileViewModel;
import com.biz4solutions.models.User;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.profile.R;
import com.biz4solutions.profile.databinding.FragmentEditProfileBinding;
import com.biz4solutions.utilities.Constants;

public class EditProfileFragment extends Fragment {
    public static final String fragmentName = "EditProfileFragment";
    private ProfileActivity activity;
    private EditProfileViewModel viewModel;

    public static EditProfileFragment newInstance() {
        return new EditProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentEditProfileBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this, new EditProfileViewModel.EditProfileViewModelFactory()).get(EditProfileViewModel.class);
        binding.setViewmodel(viewModel);
        initActivity();
        setUserData();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (activity != null)
            activity.hideEditOption(true);
    }

    private void initActivity() {
        activity = (ProfileActivity) getActivity();
    }

    public void setUserData() {
        User user = SharedPrefsManager.getInstance().retrieveUserPreference(activity, Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY);
        viewModel.setUserData(user);
    }
}
