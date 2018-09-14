package com.biz4solutions.profile.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biz4solutions.profile.R;
import com.biz4solutions.profile.databinding.FragmentEditProfileBinding;

public class EditProfileFragment extends Fragment {
    public static final String fragmentName = "EditProfileFragment";

    private FragmentEditProfileBinding binding;

    public static EditProfileFragment newInstance() {
        return new EditProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false);
        /*mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.triage_call);
            NavigationUtil.getInstance().hideMenu(mainActivity);
        }*/
        return binding.getRoot();
    }
}
