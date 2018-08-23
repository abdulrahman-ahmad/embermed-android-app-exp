package com.biz4solutions.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.biz4solutions.R;
import com.biz4solutions.activities.MainActivity;
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.databinding.FragmentEmsAlertUnconsciousBinding;
import com.biz4solutions.interfaces.OnBackClickListener;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.request.CreateEmsRequest;
import com.biz4solutions.models.response.CreateEmsResponse;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.GpsServicesUtil;
import com.biz4solutions.utilities.NavigationUtil;

public class EmsAlertUnconsciousFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "EmsAlertUnconsciousFragment";
    private MainActivity mainActivity;
    private FragmentEmsAlertUnconsciousBinding binding;
    private boolean isRequestInProgress = false;

    public EmsAlertUnconsciousFragment() {
        // Required empty public constructor
    }

    public static EmsAlertUnconsciousFragment newInstance() {
        return new EmsAlertUnconsciousFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ems_alert_unconscious, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.ems_alert);
            NavigationUtil.getInstance().showBackArrow(mainActivity, new OnBackClickListener() {
                @Override
                public void onBackPress() {
                    mainActivity.unconsciousOnBackClick();
                }
            });
        }
        binding.btnYes.setOnClickListener(this);
        binding.btnNo.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mainActivity != null) {
            NavigationUtil.getInstance().hideBackArrow(mainActivity);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                binding.btnYes.setEnabled(false);
                getUserLocation();
                break;
            case R.id.btn_no:
                Toast.makeText(mainActivity, R.string.coming_soon, Toast.LENGTH_SHORT).show();
                //mainActivity.stopGpsService();
                break;
        }
    }

    private void getUserLocation() {
        if (CommonFunctions.getInstance().isOffline(mainActivity)) {
            Toast.makeText(mainActivity, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            binding.btnYes.setEnabled(true);
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(mainActivity);
        GpsServicesUtil.getInstance().onLocationCallbackListener(new GpsServicesUtil.LocationCallbackListener() {
            @Override
            public void onSuccess(double latitude, double longitude) {
                binding.btnYes.setEnabled(true);
                if(!isRequestInProgress) {
                    isRequestInProgress = true;
                    createRequest(latitude, longitude);
                }
            }

            @Override
            public void onError() {
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, R.string.error_location_fetch, Toast.LENGTH_SHORT).show();
                binding.btnYes.setEnabled(true);
            }
        });
    }

    private void createRequest(double latitude, double longitude) {
        CreateEmsRequest body = new CreateEmsRequest();
        body.setLatitude(latitude);
        body.setLongitude(longitude);
        body.setUnconscious(true);
        new ApiServices().createRequest(mainActivity, body, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                isRequestInProgress = false;
                mainActivity.stopGpsService();
                CreateEmsResponse createEmsResponse = (CreateEmsResponse) response;
                CommonFunctions.getInstance().dismissProgressDialog();
                if (createEmsResponse != null && createEmsResponse.getData() != null) {
                    mainActivity.currentRequestId = createEmsResponse.getData().getId();
                    mainActivity.openEmsAlertCardiacCallFragment(true);
                } else {
                    if (createEmsResponse != null) {
                        Toast.makeText(mainActivity, createEmsResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                isRequestInProgress = false;
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}