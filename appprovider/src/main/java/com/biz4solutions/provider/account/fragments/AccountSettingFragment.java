package com.biz4solutions.provider.account.fragments;

import android.content.Intent;
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
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.ProviderRegistration;
import com.biz4solutions.models.User;
import com.biz4solutions.models.response.GenericResponse;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.provider.R;
import com.biz4solutions.provider.databinding.FragmentAccountSettingBinding;
import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;

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
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_account_settings);
            mainActivity.toolbarTitle.setText(R.string.account_settings);
        }
        User user = SharedPrefsManager.getInstance().retrieveUserPreference(mainActivity, Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY);
        boolean isProviderSubscribed = false;
        if (user != null) {
            isProviderSubscribed = user.getIsProviderSubscribed() != null && user.getIsProviderSubscribed();
        }
        if (!isProviderSubscribed) {
            binding.cvRegistration.setVisibility(View.GONE);
        }
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
        binding.cvRegistration.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_my_profile:
                startActivity(new Intent(mainActivity, ProfileActivity.class));
                break;
            case R.id.cv_registration:
                getProviderRegistrationDetails();
                break;
            default:
                Toast.makeText(mainActivity, R.string.coming_soon, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void getProviderRegistrationDetails() {
        if (CommonFunctions.getInstance().isOffline(mainActivity)) {
            Toast.makeText(mainActivity, R.string.error_network_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(mainActivity);
        new ApiServices().getProviderRegistrationDetails(mainActivity, new RestClientResponse() {
            @SuppressWarnings("unchecked")
            @Override
            public void onSuccess(Object response, int statusCode) {
                GenericResponse<ProviderRegistration> responseData = (GenericResponse<ProviderRegistration>) response;
                CommonFunctions.getInstance().dismissProgressDialog();
                if (responseData != null) {
                    mainActivity.openViewRegistrationFragment(responseData.getData());
                }
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}