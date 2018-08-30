package com.biz4solutions.provider.triage.views.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.interfaces.DialogDismissCallBackListener;
import com.biz4solutions.interfaces.FirebaseCallbackListener;
import com.biz4solutions.interfaces.OnBackClickListener;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.models.User;
import com.biz4solutions.models.response.EmptyResponse;
import com.biz4solutions.models.response.google.GoogleDirectionResponse;
import com.biz4solutions.models.response.google.GoogleDistanceDurationResponse;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.provider.R;
import com.biz4solutions.provider.databinding.FragmentCardiacCallDetailsBinding;
import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.provider.main.views.fragments.DashboardFragment;
import com.biz4solutions.provider.utilities.FirebaseEventUtil;
import com.biz4solutions.provider.utilities.NavigationUtil;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class TriageCallDetailsFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, LocationListener {

    public static final String fragmentName = "TriageCallDetailsFragment";
    private final static String REQUEST_DETAILS = "REQUEST_DETAILS";
    private final static String DISTANCE_STR = "DISTANCE_STR";
    private MainActivity mainActivity;
    private FragmentCardiacCallDetailsBinding binding;
    private EmsRequest requestDetails;
    private User user;
    private boolean isAcceptedOpen = false;
    private boolean isPageOpen = false;
    private View mapView;
    private GoogleMap googleMap;
    private Marker userMarker;
    private Marker victimMarker;
    private boolean isMapZoom = false;
    public int ANIMATE_SPEED_TURN = 500; // 0.5 sec;
    public int UPDATE_MIN_INTERVAL = 5000;    // 5 sec;
    public int UPDATE_MIN_DISTANCE = 10;    // 5 sec;
    public int ZOOM_LEVEL = 17;
    private LocationManager mLocationManager;
    private Location mLocation;
    private boolean isShowMapDirection = false;
    private Polyline routesPolyline;
    private boolean isApiInProgress = false;
    private String distanceStr;
    private Timer timer = new Timer();
    private TimerTask timerTask;
    private boolean isTimerReset = true;
    private BroadcastReceiver clockBroadcastReceiver;

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
        initBindingView(inflater, container);

        mainActivity = (MainActivity) getActivity();
        isPageOpen = true;

        initMapView();
        initNavView();

        user = SharedPrefsManager.getInstance().retrieveUserPreference(mainActivity, Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY);

        addFirebaseRequestEvent();

        if (requestDetails != null) {
            setCardiacCallView();
        }
        initView();
        binding.btnRespond.setOnClickListener(this);
        binding.btnSubmitReport.setOnClickListener(this);
        binding.btnGetDirection.setOnClickListener(this);
        setDistanceValue(distanceStr);
        reSetTimer();
        addClockBroadcastReceiver();
        return binding.getRoot();
    }

    private void initBindingView(@NonNull LayoutInflater inflater, ViewGroup container) {
        if (binding != null) {
            ViewGroup parent = (ViewGroup) binding.getRoot().getParent();
            if (parent != null) {
                parent.removeView(binding.getRoot());
            }
        }
        try {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cardiac_call_details, container, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initNavView() {
        if (mainActivity != null) {
            mainActivity.isRequestAcceptedByMe = false;
            mainActivity.currentRequestId = requestDetails.getId();
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.cardiac_call);
            mainActivity.btnCallAlerter.setVisibility(View.VISIBLE);
            NavigationUtil.getInstance().showBackArrow(mainActivity, new OnBackClickListener() {
                @Override
                public void onBackPress() {
                    mainActivity.showRejectRequestAlert();
                }
            });
        }
    }

    private void addFirebaseRequestEvent() {
        FirebaseEventUtil.getInstance().addFirebaseRequestEvent(mainActivity.currentRequestId, new FirebaseCallbackListener<EmsRequest>() {
            @Override
            public void onSuccess(EmsRequest data) {
                if (isPageOpen) {
                    if (requestDetails != null) {
                        if (data != null) {
                            requestDetails.setRequestStatus(data.getRequestStatus());
                            requestDetails.setProviderId(data.getProviderId());
                        }
                        setCardiacCallView();
                    }
                }
            }
        });
    }

    private void initMapView() {
        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapView = mMapFragment.getView();
        mMapFragment.getMapAsync(this);
    }

    private void addClockBroadcastReceiver() {
        clockBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (requestDetails != null && intent != null && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                    binding.requestListCardiacItem.txtTime.setText(CommonFunctions.getInstance().getTimeAgo(System.currentTimeMillis() - requestDetails.getRequestTime()));
                }
            }
        };
        mainActivity.registerReceiver(clockBroadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    private void setCardiacCallView() {
        if (requestDetails != null && requestDetails.getRequestStatus() != null) {
            switch (requestDetails.getRequestStatus()) {
                case "ACCEPTED":
                    if (requestDetails.getProviderId() != null) {
                        if (!requestDetails.getProviderId().equals(user.getUserId())) {
                            showAlert(R.string.accepted_request_message);
                        } else {
                            if (!isAcceptedOpen) {
                                isAcceptedOpen = true;
                                showMapRouteView();
                            }
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
        if (requestDetails != null) {
            if (requestDetails.getUserDetails() != null) {
                String name = requestDetails.getUserDetails().getFirstName() + " " + requestDetails.getUserDetails().getLastName();
                binding.requestListCardiacItem.txtName.setText(name);
                String genderAge = requestDetails.getUserDetails().getGender() + ", " + requestDetails.getUserDetails().getAge() + "yrs";
                binding.requestListCardiacItem.txtGenderAge.setText(genderAge);
            }
            if (requestDetails.getPatientDisease() != null) {
                binding.cardiacPatientDiseaseItem.txtPatientDisease.setText(requestDetails.getPatientDisease());
            }
            String btnRespondText = getString(R.string.respond_for_) + "" + requestDetails.getAmount();
            binding.btnRespond.setText(btnRespondText);
            binding.requestListCardiacItem.txtTime.setText(CommonFunctions.getInstance().getTimeAgo(System.currentTimeMillis() - requestDetails.getRequestTime()));
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
        mainActivity.btnCallAlerter.setVisibility(View.GONE);
        FirebaseEventUtil.getInstance().removeFirebaseRequestEvent();
        if (mainActivity.isRequestAcceptedByMe) {
            mainActivity.isUpdateList = true;
            NavigationUtil.getInstance().showMenu(mainActivity);
        } else {
            NavigationUtil.getInstance().hideBackArrow(mainActivity);
        }
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }
        stopTimer();
        if (clockBroadcastReceiver != null) {
            mainActivity.unregisterReceiver(clockBroadcastReceiver);
        }
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
            case R.id.btn_submit_report:
                CommonFunctions.getInstance().showAlertDialog(mainActivity, R.string.complete_request_message, R.string.yes, R.string.no, new DialogDismissCallBackListener<Boolean>() {
                    @Override
                    public void onClose(Boolean result) {
                        if (result) {
                            completeRequest();
                        }
                    }
                });
                break;
            case R.id.btn_get_direction:
                openGoogleMapApp();
                break;
        }
    }

    private void openGoogleMapApp() {
        try {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + requestDetails.getLatitude() + "," + requestDetails.getLongitude());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void acceptRequest() {
        if (CommonFunctions.getInstance().isOffline(mainActivity)) {
            Toast.makeText(mainActivity, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(mainActivity);
        new ApiServices().acceptRequest(mainActivity, mainActivity.currentRequestId, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                EmptyResponse createEmsResponse = (EmptyResponse) response;
                CommonFunctions.getInstance().dismissProgressDialog();
                showMapRouteView();
                Toast.makeText(mainActivity, createEmsResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showMapRouteView() {
        mainActivity.isRequestAcceptedByMe = true;
        binding.btnRespond.setVisibility(View.GONE);
        binding.btnSubmitReport.setVisibility(View.VISIBLE);
        binding.btnGetDirection.setVisibility(View.VISIBLE);
        NavigationUtil.getInstance().hideBackArrow(mainActivity);
        NavigationUtil.getInstance().hideMenu(mainActivity);
        if (googleMap != null) {
            showDirections();
        } else {
            isShowMapDirection = true;
        }
    }

    private void showDirections() {
        if (mLocation != null && googleMap != null) {
            if (CommonFunctions.getInstance().isOffline(mainActivity)) {
                Toast.makeText(mainActivity, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
                return;
            }
            new ApiServices().getDirections(mainActivity, mLocation.getLatitude(), mLocation.getLongitude(), requestDetails.getLatitude(), requestDetails.getLongitude(), new RestClientResponse() {
                @Override
                public void onSuccess(Object googleResponse, int statusCode) {
                    if (routesPolyline != null) {
                        routesPolyline.remove();
                    }
                    GoogleDirectionResponse response = (GoogleDirectionResponse) googleResponse;
                    if (response != null && response.getRoutes() != null && !response.getRoutes().isEmpty()) {
                        String encodedString = response.getRoutes().get(0).getOverviewPolyline().getPoints();
                        List<LatLng> list = decodePoly(encodedString);
                        routesPolyline = googleMap.addPolyline(new PolylineOptions()
                                .addAll(list)
                                .width(15)
                                .color(Color.argb(255, 11, 172, 244))
                                .geodesic(true)
                        );
                    }
                }

                @Override
                public void onFailure(String errorMessage, int statusCode) {
                    Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            isShowMapDirection = true;
        }
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    private void completeRequest() {
        if (CommonFunctions.getInstance().isOffline(mainActivity)) {
            Toast.makeText(mainActivity, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(mainActivity);
        new ApiServices().completeRequest(mainActivity, mainActivity.currentRequestId, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                EmptyResponse createEmsResponse = (EmptyResponse) response;
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, createEmsResponse.getMessage(), Toast.LENGTH_SHORT).show();
                mainActivity.getSupportFragmentManager().popBackStack(DashboardFragment.fragmentName, 0);
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*private void submitIncidentReport() {
        if (CommonFunctions.getInstance().isOffline(mainActivity)) {
            Toast.makeText(mainActivity, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(mainActivity);
        IncidentReport body = new IncidentReport();
        body.setComment("Test Comment");
        body.setVictimLifeSaved(true);
        body.setRequestId(requestId);
        body.setTitle("Test Title");

        new ApiServices().submitIncidentReport(mainActivity, body, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                EmptyResponse createEmsResponse = (EmptyResponse) response;
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, createEmsResponse.getMessage(), Toast.LENGTH_SHORT).show();
                mainActivity.getSupportFragmentManager().popBackStack(DashboardFragment.fragmentName, 0);
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        initMap();
    }

    void initMap() {
        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(false);
        googleMap.clear();
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        addVictimMarker(requestDetails.getLatitude(), requestDetails.getLongitude());
        try {
            if (mapView != null &&
                    mapView.findViewById(Integer.parseInt("1")) != null) {
                // Get the button view
                View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                if (locationButton != null) {
                    // and next place it, on bottom right (as Google Maps app)
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                    // position on right bottom
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    layoutParams.setMargins(0, 0, 30, 30);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mLocationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
        if (mLocationManager != null) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_MIN_INTERVAL, UPDATE_MIN_DISTANCE, this);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_MIN_INTERVAL, UPDATE_MIN_DISTANCE, this);
        }
    }

    private void addVictimMarker(double latitude, double longitude) {
        try {
            if (googleMap != null) {
                LatLng latLng = new LatLng(latitude, longitude);
                if (victimMarker == null) {
                    victimMarker = googleMap.addMarker(new MarkerOptions()
                            .title("")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_victim))
                            .position(latLng));
                    if (!isMapZoom) {
                        isMapZoom = true;
                        animateCamera(latLng, ZOOM_LEVEL);
                    }
                } else {
                    animateMarker(victimMarker, latLng);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addProviderMarker(double latitude, double longitude) {
        try {
            if (googleMap != null) {
                LatLng latLng = new LatLng(latitude, longitude);
                if (userMarker == null) {
                    userMarker = googleMap.addMarker(new MarkerOptions()
                            .title("")
                            .anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_current_location))
                            .position(latLng));

                    if (!isMapZoom) {
                        isMapZoom = true;
                        animateCamera(latLng, ZOOM_LEVEL);
                    }
                } else {
                    animateMarker(userMarker, latLng);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void animateMarker(final Marker marker, final LatLng toPosition) {
        try {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            Projection proj = googleMap.getProjection();
            Point startPoint = proj.toScreenLocation(marker.getPosition());
            final LatLng startLatLng = proj.fromScreenLocation(startPoint);
            final long duration = 500;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (toPosition != null) {
                            long elapsed = SystemClock.uptimeMillis() - start;
                            float t = interpolator.getInterpolation((float) elapsed
                                    / duration);
                            double lng = t * toPosition.longitude + (1 - t)
                                    * startLatLng.longitude;
                            double lat = t * toPosition.latitude + (1 - t)
                                    * startLatLng.latitude;
                            marker.setPosition(new LatLng(lat, lng));

                            if (t < 1.0) {
                                // Post again 16ms later.
                                handler.postDelayed(this, 16);
                            } else {
                                marker.setVisible(true);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void animateCamera(LatLng position, float zoomLevel) {
        try {
            if (googleMap != null) {
                CameraPosition cameraPosition =
                        new CameraPosition.Builder()
                                .target(position)
                                //.bearing(45)
                                .tilt(90)
                                .zoom(zoomLevel)
                                .build();
                googleMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(cameraPosition),
                        ANIMATE_SPEED_TURN,
                        null
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null) {
                mLocation = location;
                addProviderMarker(location.getLatitude(), location.getLongitude());
                if (isShowMapDirection) {
                    isShowMapDirection = false;
                    showDirections();
                }
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

    private void setDistanceValue(String distanceTxt) {
        binding.requestListCardiacItem.distanceLoader.setVisibility(View.VISIBLE);
        String distance = mainActivity.getString(R.string.away);
        if (distanceTxt != null && !distanceTxt.isEmpty()) {
            binding.requestListCardiacItem.distanceLoader.setVisibility(View.GONE);
            distance = distanceTxt + mainActivity.getString(R.string.away);
        }
        binding.requestListCardiacItem.txtDistance.setText(distance);
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