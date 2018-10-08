package com.biz4solutions.provider.newsfeed.viewmodels;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.biz4solutions.provider.R;
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.models.response.NewsFeedDataResponse;
import com.biz4solutions.models.response.NewsFeedResponse;
import com.biz4solutions.provider.newsfeed.viewpresenters.NewsFeedPresenter;
import com.biz4solutions.utilities.CommonFunctions;
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

/**
 * Created by ketan on 9/28/2018.
 */
public class NewsFeedViewModel extends ViewModel implements OnMapReadyCallback, LocationListener {

    private final NewsFeedPresenter newsFeedPresenter;
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private GoogleMap googleMap;
    @SuppressLint("StaticFieldLeak")
    private LocationManager mLocationManager;
    @SuppressWarnings("FieldCanBeLocal")
    private final int UPDATE_MIN_INTERVAL = 5000;    // 5 sec;
    @SuppressWarnings("FieldCanBeLocal")
    private final int UPDATE_MIN_DISTANCE = 0;       // 0 meter
    @SuppressWarnings("FieldCanBeLocal")
    private final int ANIMATE_SPEED_TURN = 1500;      // 1.5 sec;
    private Marker userMarker;
    private boolean isMapZoom = false;
    private NewsFeedDataResponse newsFeedData;
    private boolean isNewsFeedApiCall = false;
    private Location location;

    private NewsFeedViewModel(Context context, NewsFeedPresenter newsFeedPresenter) {
        this.context = context;
        this.newsFeedPresenter = newsFeedPresenter;
    }

    public void initMapView(SupportMapFragment mapFragment) {
        if (googleMap == null) {
            mapFragment.getMapAsync(this);
        } else {
            startLocationUpdates();
            if (location != null) {
                //addCurrentLocationMarker(location.getLatitude(), location.getLongitude());
                plotNewsFeedData(100);
            }
        }
    }

    private void startLocationUpdates() {
        if (isLocationPermissionGranted(102) && CommonFunctions.getInstance().isGPSEnabled(context)) {
            requestLocationUpdate();
        }
    }

    public void requestLocationUpdate() {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (mLocationManager != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_MIN_INTERVAL, UPDATE_MIN_DISTANCE, this);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_MIN_INTERVAL, UPDATE_MIN_DISTANCE, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            this.location = location;
            if (location != null) {
                addCurrentLocationMarker(location.getLatitude(), location.getLongitude());
                getNewsFeeds(location.getLatitude(), location.getLongitude());
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

    private void getNewsFeeds(final double latitude, final double longitude) {
        if (!isNewsFeedApiCall && newsFeedData == null) {
            if (CommonFunctions.getInstance().isOffline(context)) {
                newsFeedPresenter.toastMsg(context.getString(com.biz4solutions.profile.R.string.error_network_unavailable));
                return;
            }
            isNewsFeedApiCall = true;
            new ApiServices().getNewsFeedDetail(context, latitude, longitude, new RestClientResponse() {
                @Override
                public void onSuccess(Object response, int statusCode) {
                    NewsFeedResponse newsFeedResponse = (NewsFeedResponse) response;
                    if (newsFeedResponse != null) {
                        newsFeedData = newsFeedResponse.getData();
                    }
                    plotNewsFeedData(2000);
                }

                @Override
                public void onFailure(String errorMessage, int statusCode) {
                    newsFeedPresenter.toastMsg(errorMessage);
                    isNewsFeedApiCall = false;
                    //getNewsFeeds(latitude, longitude);
                }
            });
        }
    }

    private void plotNewsFeedData(long delayMillis) {
        if (googleMap != null && newsFeedData != null) {
            newsFeedPresenter.startAnimation(newsFeedData);
            googleMap.clear();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (location != null) {
                        addCurrentLocationMarker(location.getLatitude(), location.getLongitude());
                    }
                    if (newsFeedData.getProviderLocationList() != null && !newsFeedData.getProviderLocationList().isEmpty()) {
                        for (com.biz4solutions.models.Location location : newsFeedData.getProviderLocationList()) {
                            addProviderMarker(location.getLatitude(), location.getLongitude());
                        }
                    }
                }
            }, delayMillis);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (isLocationPermissionGranted(101) && CommonFunctions.getInstance().isGPSEnabled(context)) {
            initMap();
        }
    }

    public void initMap() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(false);
        googleMap.clear();
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);

        try {
            startLocationUpdates();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isLocationPermissionGranted(int requestCode) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ((MainActivity) context).requestPermissions(perms, requestCode);
            } else {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

    //click on location btn
    public void onLocationBtnClick() {
        if (userMarker != null && googleMap != null) {
            animateCamera(userMarker.getPosition());
        }
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
                        animateCamera(latLng);
                    }
                } else {
                    animateMarker(userMarker, latLng);
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
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .title("")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_victim))
                        .position(latLng));
                marketAnimation(marker);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void marketAnimation(final Marker marker) {
        //Make the marker bounce
        final Handler handler = new Handler();

        final long startTime = SystemClock.uptimeMillis();
        final long duration = 2000;

        Projection proj = googleMap.getProjection();
        final LatLng markerLatLng = marker.getPosition();
        Point startPoint = proj.toScreenLocation(markerLatLng);
        startPoint.offset(0, -200);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * markerLatLng.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * markerLatLng.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    private void animateCamera(LatLng position) {
        try {
            if (googleMap != null) {
                CameraPosition cameraPosition =
                        new CameraPosition.Builder()
                                .target(position)
                                //.bearing(45)
                                //.tilt(90)
                                .zoom((float) 11)
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

    private void animateMarker(final Marker marker, final LatLng toPosition) {
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
    protected void onCleared() {
        super.onCleared();
        if (context != null)
            context = null;
    }

    //clear all objects & context classes objects
    public void destroy() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }
    }

    @SuppressWarnings("unchecked")
    public static class NewsFeedFactory extends ViewModelProvider.NewInstanceFactory {
        private final Context context;
        private final NewsFeedPresenter newsFeedPresenter;
        //private final UrgentCaresDataResponse urgentCaresDataResponse;

        public NewsFeedFactory(Context context, NewsFeedPresenter newsFeedPresenter) {
            this.context = context;
            this.newsFeedPresenter = newsFeedPresenter;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new NewsFeedViewModel(context, newsFeedPresenter);
        }
    }

}