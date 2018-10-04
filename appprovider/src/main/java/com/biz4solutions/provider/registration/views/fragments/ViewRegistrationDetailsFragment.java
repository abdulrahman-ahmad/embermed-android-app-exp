package com.biz4solutions.provider.registration.views.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.biz4solutions.models.User;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.provider.R;
import com.biz4solutions.provider.databinding.FragmentViewRegistrationDetailsBinding;
import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.provider.registration.viewmodels.RegistrationViewModel;
import com.biz4solutions.provider.registration.viewmodels.ViewRegistrationDetailsViewModel;
import com.biz4solutions.utilities.Constants;

public class ViewRegistrationDetailsFragment extends Fragment {

    public static final String fragmentName = "ViewRegistrationDetailsFragment";
    private MainActivity mainActivity;
    private ViewRegistrationDetailsViewModel viewModel;
    private FragmentViewRegistrationDetailsBinding binding;


    public ViewRegistrationDetailsFragment() {
        // Required empty public constructor
    }

    public static ViewRegistrationDetailsFragment newInstance() {
        return new ViewRegistrationDetailsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_registration_details, container, false);
        viewModel = ViewModelProviders.of(this, new ViewRegistrationDetailsViewModel.ViewRegistrationDetailsFactory(mainActivity.getApplication())).get(ViewRegistrationDetailsViewModel.class);
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
                Toast.makeText(mainActivity, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initActivity() {
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_registration);
            mainActivity.toolbarTitle.setText(R.string.registration);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setUserData() {
        User user = SharedPrefsManager.getInstance().retrieveUserPreference(mainActivity, Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY);
        viewModel.setUser(user);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
