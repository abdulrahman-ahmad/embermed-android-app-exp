package com.biz4solutions.main.viewmodels;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.biz4solutions.R;
import com.biz4solutions.main.views.activities.MainActivity;
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
public class NewsFeedViewModel extends ViewModel implements OnMapReadyCallback {

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private GoogleMap googleMap;
    @SuppressLint("StaticFieldLeak")
    private View mapView;
    @SuppressWarnings("FieldCanBeLocal")
    private final int ANIMATE_SPEED_TURN = 500;      // 0.5 sec;
    private Marker userMarker;
    private boolean isMapZoom = false;

    private NewsFeedViewModel(Context context) {
        this.context = context;
        //this.urgentCaresDataResponse = urgentCaresDataResponse;
    }

    public void initMapView(SupportMapFragment mapFragment) {
        if (googleMap == null) {
            mapView = mapFragment.getView();
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (isLocationPermissionGranted() && CommonFunctions.getInstance().isGPSEnabled(context)) {
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
        //addCurrentLocationMarker(location.getLatitude(), location.getLongitude());
        //ArrayList<UrgentCare> urgentCares = urgentCaresDataResponse.getList();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isLocationPermissionGranted() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ((MainActivity) context).requestPermissions(perms, 101);
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

    private void animateCamera(LatLng position) {
        try {
            if (googleMap != null) {
                CameraPosition cameraPosition =
                        new CameraPosition.Builder()
                                .target(position)
                                //.bearing(45)
                                .tilt(90)
                                .zoom((float) 17)
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
        if (mapView != null)
            mapView = null;
    }

    //clear all objects & context classes objects
    public void destroy() {

    }

    @SuppressWarnings("unchecked")
    public static class NewsFeedFactory extends ViewModelProvider.NewInstanceFactory {
        private final Context context;
        //private final UrgentCaresDataResponse urgentCaresDataResponse;

        public NewsFeedFactory(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new NewsFeedViewModel(context);
        }
    }

}