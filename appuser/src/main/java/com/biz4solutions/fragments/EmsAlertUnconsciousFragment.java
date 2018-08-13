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
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.services.GpsServices;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;
import com.biz4solutions.utilities.NavigationUtil;

public class EmsAlertUnconsciousFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "EmsAlertUnconsciousFragment";
    private MainActivity mainActivity;

    public EmsAlertUnconsciousFragment() {
        // Required empty public constructor
    }

    public static EmsAlertUnconsciousFragment newInstance() {
        return new EmsAlertUnconsciousFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentEmsAlertUnconsciousBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ems_alert_unconscious, container, false);
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
                createRequest();
                break;
            case R.id.btn_no:
                Toast.makeText(mainActivity, R.string.coming_soon, Toast.LENGTH_SHORT).show();
                //mainActivity.stopGpsService();
                break;
        }
    }

    private void createRequest() {
        if (CommonFunctions.getInstance().isOffline(mainActivity)) {
            Toast.makeText(mainActivity, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(mainActivity);
        CreateEmsRequest body = new CreateEmsRequest();
        body.setLatitude(GpsServices.getLatitude());
        body.setLatitude(GpsServices.getLongitude());
        body.setUnconscious(true);
        new ApiServices().createRequest(mainActivity, body, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                CreateEmsResponse createEmsResponse = (CreateEmsResponse) response;
                CommonFunctions.getInstance().dismissProgressDialog();
                if (createEmsResponse != null && createEmsResponse.getData() != null) {
                    SharedPrefsManager.getInstance().storeStringPreference(mainActivity, Constants.USER_PREFERENCE, Constants.USER_CURRENT_REQUEST_ID_KEY,
                            createEmsResponse.getData().getId());
                    openEmsAlertCardiacCallFragment();
                } else {
                    if (createEmsResponse != null) {
                        Toast.makeText(mainActivity, createEmsResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openEmsAlertCardiacCallFragment() {
        mainActivity.getSupportFragmentManager().executePendingTransactions();
        mainActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, EmsAlertCardiacCallFragment.newInstance())
                .addToBackStack(EmsAlertCardiacCallFragment.fragmentName)
                .commitAllowingStateLoss();
    }
}
