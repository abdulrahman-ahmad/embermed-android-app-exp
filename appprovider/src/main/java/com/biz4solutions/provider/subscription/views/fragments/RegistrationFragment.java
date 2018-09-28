package com.biz4solutions.provider.subscription.views.fragments;

import android.arch.lifecycle.ViewModelProvider;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biz4solutions.provider.R;
import com.biz4solutions.provider.databinding.FragmentRegistrationBinding;
import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.provider.subscription.viewmodels.RegistrationViewModel;

public class RegistrationFragment extends Fragment {

    public static final String fragmentName = "RegistrationFragment";
    private MainActivity mainActivity;
    private RegistrationViewModel viewModel;
    private FragmentRegistrationBinding binding;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    public static RegistrationFragment newInstance() {
        return new RegistrationFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registration, container, false);
        viewModel = new ViewModelProvider(this, new RegistrationViewModel.RegistrationFactory()).get(RegistrationViewModel.class);
        binding.setViewModel(viewModel);
        initActivity();
        return binding.getRoot();
    }

    private void initActivity() {
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_registration);
            mainActivity.toolbarTitle.setText(R.string.registration);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
