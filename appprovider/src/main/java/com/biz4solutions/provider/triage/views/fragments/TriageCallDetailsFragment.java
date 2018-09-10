package com.biz4solutions.provider.triage.views.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.interfaces.DialogDismissCallBackListener;
import com.biz4solutions.interfaces.FirebaseCallbackListener;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.models.User;
import com.biz4solutions.models.response.EmptyResponse;
import com.biz4solutions.models.response.google.GoogleDistanceDurationResponse;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.provider.R;
import com.biz4solutions.provider.databinding.FragmentTriageCallDetailsBinding;
import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.provider.main.views.fragments.DashboardFragment;
import com.biz4solutions.provider.utilities.FirebaseEventUtil;
import com.biz4solutions.provider.utilities.NavigationUtil;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class TriageCallDetailsFragment extends Fragment implements View.OnClickListener, LocationListener {

    public static final String fragmentName = "TriageCallDetailsFragment";
    private final static String REQUEST_DETAILS = "REQUEST_DETAILS";
    private final static String DISTANCE_STR = "DISTANCE_STR";
    private MainActivity mainActivity;
    private FragmentTriageCallDetailsBinding binding;
    private EmsRequest requestDetails;
    private User user;
    private boolean isPageOpen = false;
    private String distanceStr;
    private BroadcastReceiver clockBroadcastReceiver;
    public String currentRequestId;
    private Timer timer = new Timer();
    private TimerTask timerTask;
    private boolean isTimerReset = true;
    private boolean isApiInProgress = false;
    private LocationManager mLocationManager;
    public int UPDATE_MIN_INTERVAL = 5000;    // 5 sec;
    public int UPDATE_MIN_DISTANCE = 10;    // 5 sec;

    public TriageCallDetailsFragment() {
        // Required empty public constructor
    }

    public static TriageCallDetailsFragment newInstance(EmsRequest requestDetails, String distanceStr) {
        TriageCallDetailsFragment fragment = new TriageCallDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(REQUEST_DETAILS, requestDetails);
        args.putString(DISTANCE_STR, distanceStr);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        if (getArguments() != null) {
            requestDetails = (EmsRequest) getArguments().getSerializable(REQUEST_DETAILS);
            distanceStr = getArguments().getString(DISTANCE_STR);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_triage_call_details, container, false);

        mainActivity = (MainActivity) getActivity();
        isPageOpen = true;

        if (mainActivity != null) {
            currentRequestId = requestDetails.getId();
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.triage_call);
            NavigationUtil.getInstance().showBackArrow(mainActivity);
        }

        user = SharedPrefsManager.getInstance().retrieveUserPreference(mainActivity, Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY);
        addFirebaseRequestEvent();
        if (requestDetails != null) {
            setRequestView();
        }
        initView();
        binding.btnRespond.setOnClickListener(this);
        setDistanceValue(distanceStr);
        addClockBroadcastReceiver();
        startLocationUpdates();
        reSetTimer();
        return binding.getRoot();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
        if (mLocationManager != null) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_MIN_INTERVAL, UPDATE_MIN_DISTANCE, this);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_MIN_INTERVAL, UPDATE_MIN_DISTANCE, this);
        }
    }

    private void addFirebaseRequestEvent() {
        FirebaseEventUtil.getInstance().addFirebaseRequestEvent(currentRequestId, new FirebaseCallbackListener<EmsRequest>() {
            @Override
            public void onSuccess(EmsRequest data) {
                if (isPageOpen) {
                    if (requestDetails != null) {
                        if (data != null) {
                            requestDetails.setRequestStatus(data.getRequestStatus());
                            requestDetails.setProviderId(data.getProviderId());
                        }
                        setRequestView();
                    }
                }
            }
        });
    }

    private void addClockBroadcastReceiver() {
        clockBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (requestDetails != null && intent != null && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                    binding.requestListTriageItem.txtTime.setText(CommonFunctions.getInstance().getTimeAgo(System.currentTimeMillis() - requestDetails.getRequestTime()));
                }
            }
        };
        mainActivity.registerReceiver(clockBroadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    private void setRequestView() {
        if (requestDetails != null && requestDetails.getRequestStatus() != null) {
            switch (requestDetails.getRequestStatus()) {
                case "ACCEPTED":
                    if (requestDetails.getProviderId() != null) {
                        if (!requestDetails.getProviderId().equals(user.getUserId())) {
                            showAlert(R.string.accepted_request_message);
                        }
                    }
                    break;
                case "CANCELLED":
                    showAlert(R.string.canceled_request_message);
                    break;
            }
        }
    }

    private void initView() {
        binding.cardiacPatientDiseaseItem.txtPatientDiseaseTitle.setText(R.string.patient_symptoms);
        if (requestDetails != null) {
            if (requestDetails.getUserDetails() != null) {
                String name = requestDetails.getUserDetails().getFirstName() + " " + requestDetails.getUserDetails().getLastName();
                binding.requestListTriageItem.txtName.setText(name);
                String genderAge = requestDetails.getUserDetails().getGender() + ", " + requestDetails.getUserDetails().getAge() + "yrs";
                binding.requestListTriageItem.txtGenderAge.setText(genderAge);
            }
            if (requestDetails.getPatientSymptoms() != null) {
                binding.cardiacPatientDiseaseItem.txtPatientDisease.setText(requestDetails.getPatientSymptoms());
            }
            String btnRespondText = getString(R.string.respond_for_) + "" + requestDetails.getAmount();
            binding.btnRespond.setText(btnRespondText);
            binding.requestListTriageItem.txtTime.setText(CommonFunctions.getInstance().getTimeAgo(System.currentTimeMillis() - requestDetails.getRequestTime()));
        }
    }

    private void showAlert(int message) {
        CommonFunctions.getInstance().showAlertDialog(mainActivity, message, true, new DialogDismissCallBackListener<Boolean>() {
            @Override
            public void onClose(Boolean result) {
                if (result) {
                    mainActivity.isUpdateList = true;
                    mainActivity.getSupportFragmentManager().popBackStack(DashboardFragment.fragmentName, 0);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isPageOpen = false;
        FirebaseEventUtil.getInstance().removeFirebaseRequestEvent();
        NavigationUtil.getInstance().hideBackArrow(mainActivity);
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }
        if (clockBroadcastReceiver != null) {
            mainActivity.unregisterReceiver(clockBroadcastReceiver);
        }
        stopTimer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_respond:
                CommonFunctions.getInstance().showAlertDialog(mainActivity, R.string.accept_request_message, R.string.yes, R.string.no, new DialogDismissCallBackListener<Boolean>() {
                    @Override
                    public void onClose(Boolean result) {
                        if (result) {
                            acceptRequest();
                        }
                    }
                });
                break;
        }
    }

    private void acceptRequest() {
        if (CommonFunctions.getInstance().isOffline(mainActivity)) {
            Toast.makeText(mainActivity, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(mainActivity);
        new ApiServices().acceptRequest(mainActivity, currentRequestId, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                final EmptyResponse createEmsResponse = (EmptyResponse) response;
                mainActivity.startVideoCallWithPermissions(currentRequestId);
                if (mainActivity != null) {
                    Toast.makeText(mainActivity, createEmsResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void setDistanceValue(String distanceTxt) {
        binding.requestListTriageItem.distanceLoader.setVisibility(View.VISIBLE);
        String distance = mainActivity.getString(R.string.away);
        if (distanceTxt != null && !distanceTxt.isEmpty()) {
            binding.requestListTriageItem.distanceLoader.setVisibility(View.GONE);
            distance = distanceTxt + mainActivity.getString(R.string.away);
        }
        binding.requestListTriageItem.txtDistance.setText(distance);
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null) {
                getDistance(location);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void getDistance(Location location) {
        if (CommonFunctions.getInstance().isOffline(mainActivity) || isApiInProgress || !isTimerReset) {
            return;
        }
        isApiInProgress = true;
        isTimerReset = false;
        List<com.biz4solutions.models.Location> locations = new ArrayList<>();
        locations.add(new com.biz4solutions.models.Location(location.getLatitude(), location.getLongitude()));

        new ApiServices().getDistanceDuration(mainActivity, "imperial", requestDetails.getLatitude(), requestDetails.getLongitude(), locations, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                try {
                    isApiInProgress = false;
                    GoogleDistanceDurationResponse durationResponse = (GoogleDistanceDurationResponse) response;
                    //System.out.println("aa ------------ durationResponse" + durationResponse);
                    if (durationResponse != null
                            && durationResponse.getRows() != null
                            && !durationResponse.getRows().isEmpty()
                            && durationResponse.getRows().get(0).getElements() != null
                            && !durationResponse.getRows().get(0).getElements().isEmpty()
                            && durationResponse.getRows().get(0).getElements().get(0).getDistance() != null) {
                        setDistanceValue(durationResponse.getRows().get(0).getElements().get(0).getDistance().getText());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                isApiInProgress = false;
                System.out.println("aa ------ errorMessage=" + errorMessage);
            }
        });
    }

    private void reSetTimer() {
        stopTimer();
        timerTask = new TimerTask();
        timer.schedule(timerTask, Constants.DISTANCE_API_DElAY);//30 sec
    }

    private void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timer.purge();
        }
    }

    private class TimerTask extends java.util.TimerTask {
        @Override
        public void run() {
            isTimerReset = true;
            reSetTimer();
        }
    }

}