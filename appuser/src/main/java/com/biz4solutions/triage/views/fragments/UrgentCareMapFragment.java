package com.biz4solutions.triage.views.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
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
import android.widget.TextView;
import android.widget.Toast;

import com.biz4solutions.R;
import com.biz4solutions.databinding.FragmentUrgentCareMapBinding;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.models.UrgentCare;
import com.biz4solutions.models.response.UrgentCaresDataResponse;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.CustomMapClusterRenderer;
import com.biz4solutions.utilities.MapClusterItem;
import com.biz4solutions.utilities.NavigationUtil;
import com.google.android.gms.maps.CameraUpdate;
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
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;

public class UrgentCareMapFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, LocationListener {

    public static final String fragmentName = "UrgentCareMapFragment";
    private MainActivity mainActivity;
    private FragmentUrgentCareMapBinding binding;
    private GoogleMap googleMap;
    private View mapView;
    private LocationManager mLocationManager;
    public int UPDATE_MIN_INTERVAL = 5000;    // 5 sec;
    public int UPDATE_MIN_DISTANCE = 0;       // 0 meter
    public int ZOOM_LEVEL = 17;
    private Marker userMarker;
    private boolean isMapZoom = false;
    public int ANIMATE_SPEED_TURN = 500;      // 0.5 sec;
    private ClusterManager<MapClusterItem> clusterManager;
    private final static String URGENT_CARES_RESPONSE = "URGENT_CARES_RESPONSE";
    private UrgentCaresDataResponse urgentCaresDataResponse;
    private MapClusterItem selectedClusterItem;

    public UrgentCareMapFragment() {
        // Required empty public constructor
    }

    public static UrgentCareMapFragment newInstance(UrgentCaresDataResponse urgentCaresDataResponse) {
        UrgentCareMapFragment fragment = new UrgentCareMapFragment();
        Bundle args = new Bundle();
        args.putSerializable(URGENT_CARES_RESPONSE, urgentCaresDataResponse);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            urgentCaresDataResponse = (UrgentCaresDataResponse) getArguments().getSerializable(URGENT_CARES_RESPONSE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initBindingView(inflater, container);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.urgent_cares_around_you_text);
            NavigationUtil.getInstance().showBackArrow(mainActivity);
        }
        if (googleMap == null) {
            initMapView();
        } else {
            startLocationUpdates();
        }
        initClickListeners();
        return binding.getRoot();
    }

    private void initClickListeners() {
        binding.btnBookUber.setOnClickListener(this);
        binding.myLocation.setOnClickListener(this);
    }

    private void initBindingView(@NonNull LayoutInflater inflater, ViewGroup container) {
        if (binding != null) {
            ViewGroup parent = (ViewGroup) binding.getRoot().getParent();
            if (parent != null) {
                parent.removeView(binding.getRoot());
            }
        }
        try {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_urgent_care_map, container, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initMapView() {
        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapView = mMapFragment.getView();
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (isLocationPermissionGranted(101) && CommonFunctions.getInstance().isGPSEnabled(mainActivity)) {
            initMap();
        }
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
//        clusterManager = new ClusterManager<>(getActivity(), googleMap);

        ArrayList<UrgentCare> urgentCares = urgentCaresDataResponse.getList();
        showClusterItems(urgentCares);
        /*if (urgentCares != null) {
            for (int i = 0; i < urgentCares.size(); i++) {
                addUrgentCareMarker(urgentCares.get(i).getLatitude(), urgentCares.get(i).getLongitude());
            }
        }*/
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
            startLocationUpdates();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ArrayList<MapClusterItem> clusterItems;

    void clearItems() {
        for (int i = 0; clusterItems != null && i < clusterItems.size(); i++) {
            clusterItems.get(i).setMarker(null);
        }
    }

    private void showClusterItems(ArrayList<UrgentCare> urgentCares) {
        if (getActivity() == null) {
            return;
        }
        if (clusterManager == null) {
            clusterManager = new ClusterManager<>(getActivity(), googleMap);
        }

        clusterManager.setRenderer(new CustomMapClusterRenderer<>(getActivity(), googleMap, clusterManager));
        clusterManager.clearItems();
        clearItems();
        googleMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
        googleMap.setOnMarkerClickListener(clusterManager);
        //noinspection deprecation
        googleMap.setOnCameraChangeListener(clusterManager);
        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MapClusterItem>() {
            @Override
            public boolean onClusterClick(Cluster<MapClusterItem> cluster) {
                if (cluster != null && googleMap != null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(cluster.getPosition())
                            .zoom(googleMap.getCameraPosition().zoom + 2).build();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                    googleMap.moveCamera(cameraUpdate);
                }
                return false;
            }
        });
        clusterItems = new ArrayList<>();
        clusterManager.getMarkerCollection().setOnInfoWindowAdapter(
                new MyCustomAdapterForItems());
        googleMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
            @Override
            public void onInfoWindowClose(Marker marker) {
                binding.llBookUber.setVisibility(View.GONE);
            }
        });
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MapClusterItem>() {
            @Override
            public boolean onClusterItemClick(MapClusterItem clusterItem) {
                //Toast.makeText(mainActivity, clusterItem.getUserId(), Toast.LENGTH_SHORT).show();
                selectedClusterItem = clusterItem;
                binding.llBookUber.setVisibility(View.VISIBLE);
                return false;
            }
        });
        for (int i = 0; i < urgentCares.size(); i++) {
            UrgentCare item = urgentCares.get(i);
            LatLng latLngCg = new LatLng(item.getLatitude(), item.getLongitude());
            MapClusterItem urgentCareItem = new MapClusterItem(latLngCg, item.getId(), item.getName());
            clusterItems.add(urgentCareItem);
            clusterManager.addItem(urgentCareItem);
        }

        try {
            clusterManager.cluster();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startLocationUpdates() {
        if (isLocationPermissionGranted(102) && CommonFunctions.getInstance().isGPSEnabled(mainActivity)) {
            requestLocationUpdate();
        }
    }

    private void requestLocationUpdate() {
        mLocationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
        if (mLocationManager != null) {
            if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_MIN_INTERVAL, UPDATE_MIN_DISTANCE, this);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_MIN_INTERVAL, UPDATE_MIN_DISTANCE, this);
        }
    }

    private boolean isLocationPermissionGranted(int requestCode) {
        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                requestPermissions(perms, requestCode);
            } else {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            boolean userAllowedAllRequestPermissions = true;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    userAllowedAllRequestPermissions = false;
                }
            }

            if (userAllowedAllRequestPermissions) {
                switch (requestCode) {
                    case 102:
                        requestLocationUpdate();
                        break;
                    case 101:
                        initMap();
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mainActivity != null) {
            NavigationUtil.getInstance().hideBackArrow(mainActivity);
        }
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null) {
                addCurrentLocationMarker(location.getLatitude(), location.getLongitude());
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void addCurrentLocationMarker(double latitude, double longitude) {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_book_uber:
                Toast.makeText(mainActivity, R.string.coming_soon, Toast.LENGTH_SHORT).show();
                break;
            case R.id.my_location:
                if (userMarker != null && googleMap != null) {
                    animateCamera(userMarker.getPosition(), ZOOM_LEVEL);
                }
                break;
        }
    }

    public class MyCustomAdapterForItems implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        @SuppressLint("InflateParams")
        MyCustomAdapterForItems() {
            myContentsView = getLayoutInflater().inflate(R.layout.info_window, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            TextView tvTitle = myContentsView.findViewById(R.id.txtTitle);
            tvTitle.setText(selectedClusterItem.getName());
            return myContentsView;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

    }

}