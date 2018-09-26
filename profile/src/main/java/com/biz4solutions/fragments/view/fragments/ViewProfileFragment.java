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
import com.biz4solutions.fragments.view.viewmodels.ViewProfileViewModel;
import com.biz4solutions.models.User;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.profile.R;
import com.biz4solutions.profile.databinding.FragmentViewProfileBinding;
import com.biz4solutions.utilities.Constants;

public class ViewProfileFragment extends Fragment {
    public static final String fragmentName = "ViewProfileFragment";
    private FragmentViewProfileBinding binding;
    private ProfileActivity activity;
    private ViewProfileViewModel viewModel;

    public static ViewProfileFragment newInstance() {
        return new ViewProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_profile, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this, new ViewProfileViewModel.ViewProfileViewModelFactory()).get(ViewProfileViewModel.class);
        binding.setViewModel(viewModel);
        initActivity();
        setUserData();
        return binding.getRoot();
    }

    public void setUserData() {
        User user = SharedPrefsManager.getInstance().retrieveUserPreference(activity, Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY);
        viewModel.setUserData(user);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (activity != null)
            activity.hideEditOption(false);
    }

    private void initActivity() {
        activity = (ProfileActivity) getActivity();
    }
}
