package com.biz4solutions.reports.views.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biz4solutions.R;
import com.biz4solutions.databinding.FragmentCompleteLocationMapsBinding;
import com.biz4solutions.databinding.InfoWindowBinding;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.utilities.NavigationUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CompleteLocationMapFragment extends Fragment implements OnMapReadyCallback {

    public static final String fragmentName = "CompleteLocationMapFragment";
    private final static String LATITUDE = "LATITUDE";
    private final static String LONGITUDE = "LONGITUDE";
    private MainActivity mainActivity;
    private FragmentCompleteLocationMapsBinding binding;
    private GoogleMap googleMap;
    private Marker victimMarker;
    private boolean isMapZoom = false;
    public int ANIMATE_SPEED_TURN = 1500; // 1.5 sec;
    public int ZOOM_LEVEL = 17;
    private double latitude;
    private double longitude;

    public CompleteLocationMapFragment() {
        // Required empty public constructor
    }

    public static CompleteLocationMapFragment newInstance(double latitude, double longitude) {
        CompleteLocationMapFragment fragment = new CompleteLocationMapFragment();
        Bundle args = new Bundle();
        args.putDouble(LATITUDE, latitude);
        args.putDouble(LONGITUDE, longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        if (getArguments() != null) {
            latitude = getArguments().getDouble(LATITUDE);
            longitude = getArguments().getDouble(LONGITUDE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (binding != null) {
            ViewGroup parent = (ViewGroup) binding.getRoot().getParent();
            if (parent != null) {
                parent.removeView(binding.getRoot());
            }
        }
        try {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_complete_location_maps, container, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainActivity = (MainActivity) getActivity();

        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.incident_report);
            NavigationUtil.getInstance().showBackArrow(mainActivity);
        }

        if (googleMap == null) {
            initMapView();
        }
        return binding.getRoot();
    }

    private void initMapView() {
        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        NavigationUtil.getInstance().hideBackArrow(mainActivity);
    }

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
        googleMap.setInfoWindowAdapter(new MyCustomAdapterForItems());
        addVictimMarker(latitude, longitude);
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
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (victimMarker != null) {
                                    victimMarker.setTag(getString(R.string.request_completed_marker_message));
                                    victimMarker.showInfoWindow();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, ANIMATE_SPEED_TURN);

                }
            }
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

    public class MyCustomAdapterForItems implements GoogleMap.InfoWindowAdapter {
        private final InfoWindowBinding infoWindowBinding;

        MyCustomAdapterForItems() {
            infoWindowBinding = DataBindingUtil.inflate(mainActivity.getLayoutInflater(), R.layout.info_window, null, false);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            if (marker.getTag() != null) {
                infoWindowBinding.txtTitle.setText(marker.getTag().toString());
            }
            return infoWindowBinding.getRoot();
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

}