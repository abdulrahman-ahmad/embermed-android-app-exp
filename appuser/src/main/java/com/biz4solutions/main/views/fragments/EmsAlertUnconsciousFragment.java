package com.biz4solutions.main.views.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.biz4solutions.R;
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.customs.taptargetview.TapTargetView;
import com.biz4solutions.databinding.FragmentEmsAlertUnconsciousBinding;
import com.biz4solutions.interfaces.OnBackClickListener;
import com.biz4solutions.interfaces.OnTargetClickListener;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.models.request.CreateEmsRequest;
import com.biz4solutions.models.response.CreateEmsResponse;
import com.biz4solutions.triage.views.fragments.SymptomsFragment;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.GpsServicesUtil;
import com.biz4solutions.utilities.NavigationUtil;
import com.biz4solutions.utilities.TargetViewUtil;

public class EmsAlertUnconsciousFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "EmsAlertUnconsciousFragment";
    private MainActivity mainActivity;
    private FragmentEmsAlertUnconsciousBinding binding;
    private boolean isRequestInProgress = false;
    private TapTargetView tutorial;

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
            mainActivity.toolbarTitle.setText(R.string.ems_alert);
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            NavigationUtil.getInstance().showBackArrow(mainActivity, new OnBackClickListener() {
                @Override
                public void onBackPress() {
                    if (!mainActivity.isTutorialMode) {
                        mainActivity.unconsciousOnBackClick();
                    }
                }
            });
        }
        if (mainActivity != null && mainActivity.isTutorialMode) {
            showTutorial();
        } else {
            binding.btnYes.setOnClickListener(this);
            binding.btnNo.setOnClickListener(this);
        }
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                binding.btnYes.setEnabled(false);
                getUserLocation();
                break;
            case R.id.btn_no:
                openSymptomsFragment();
                break;
        }
    }

    private void openSymptomsFragment() {
        Fragment currentFragment = mainActivity.getSupportFragmentManager().findFragmentById(R.id.main_container);
        if (currentFragment instanceof SymptomsFragment) {
            return;
        }
        mainActivity.getSupportFragmentManager().executePendingTransactions();
        mainActivity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.main_container, SymptomsFragment.newInstance())
                .addToBackStack(SymptomsFragment.fragmentName)
                .commitAllowingStateLoss();
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
                if (!isRequestInProgress) {
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
                    mainActivity.openEmsAlertCardiacCallFragment(true, createEmsResponse.getData().getId());
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

    private void showTutorial() {
        if (mainActivity.tutorialId == 1) {
            tutorial = TargetViewUtil.showTargetRoundedForBtn(mainActivity,
                    binding.btnYes, getString(R.string.tutorial_title_ems_yes_btn),
                    getString(R.string.tutorial_description_ems_yes_btn),
                    new OnTargetClickListener() {
                        @Override
                        public void onTargetClick() {
                            mainActivity.reOpenHowItWorksFragment();
                        }
                    });
        } else if (mainActivity.tutorialId == 2) {
            tutorial = TargetViewUtil.showTargetRoundedForBtn(mainActivity,
                    binding.btnNo, getString(R.string.tutorial_title_ems_no_btn),
                    getString(R.string.tutorial_description_ems_no_btn),
                    new OnTargetClickListener() {
                        @Override
                        public void onTargetClick() {
                            openSymptomsFragment();
                        }
                    });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        NavigationUtil.getInstance().hideBackArrow(mainActivity);
        if (tutorial != null) {
            tutorial.dismiss(false);
        }
    }
}