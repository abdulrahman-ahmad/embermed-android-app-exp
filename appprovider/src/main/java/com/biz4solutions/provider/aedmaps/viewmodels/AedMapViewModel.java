package com.biz4solutions.provider.aedmaps.viewmodels;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.ObservableBoolean;
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
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.biz4solutions.models.UrgentCare;
import com.biz4solutions.models.response.UrgentCaresDataResponse;
import com.biz4solutions.provider.BuildConfig;
import com.biz4solutions.provider.R;
import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.provider.utilities.CustomMapClusterRenderer;
import com.biz4solutions.provider.utilities.MapClusterItem;
import com.biz4solutions.utilities.CommonFunctions;
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

/*
 * Created by saurabh.asati on 9/24/2018.
 */
public class AedMapViewModel extends ViewModel implements OnMapReadyCallback, LocationListener {

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private final UrgentCaresDataResponse urgentCaresDataResponse;
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
    private MapClusterItem selectedClusterItem;
    private boolean isMapZoom = false;
    private ClusterManager<MapClusterItem> clusterManager;
    private ArrayList<MapClusterItem> clusterItems;
    public final ObservableBoolean showUberLayout;

    private AedMapViewModel(Context context, UrgentCaresDataResponse urgentCaresDataResponse) {
        //initMapView();
        this.context = context;
        showUberLayout = new ObservableBoolean();
        this.urgentCaresDataResponse = urgentCaresDataResponse;
        if (googleMap != null) {
            startLocationUpdates();
        }
    }

    public void initMapView(SupportMapFragment mapFragment) {
        if (googleMap == null) {
            mapFragment.getMapAsync(this);
        } else {
            startLocationUpdates();
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
        ArrayList<UrgentCare> urgentCares = urgentCaresDataResponse.getList();
        showClusterItems(urgentCares);
        try {
            startLocationUpdates();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearItems() {
        for (int i = 0; clusterItems != null && i < clusterItems.size(); i++) {
            clusterItems.get(i).setMarker(null);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null) {
                addCurrentLocationMarker(location.getLatitude(), location.getLongitude());
                if (BuildConfig.DEBUG)
                    Log.d("location", "lat:" + location.getLatitude() + " lon:" + location.getLongitude());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showClusterItems(ArrayList<UrgentCare> urgentCares) {
        if (context == null) {
            return;
        }

        if (clusterManager == null) {
            clusterManager = new ClusterManager<>(context, googleMap);
        }

        clusterManager.setRenderer(new CustomMapClusterRenderer<>(context, googleMap, clusterManager));
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
                showUberLayout.set(false);
            }
        });
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MapClusterItem>() {
            @Override
            public boolean onClusterItemClick(MapClusterItem clusterItem) {
                selectedClusterItem = clusterItem;
                //showUberLayout.set(true);
                showUberLayout.set(false); // Client Change - mail - 10 Oct 2018 11:24 AM
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

    //click on uber btn
    public void onBookUberClick(View view) {
        Toast.makeText(view.getContext(), view.getContext().getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
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
    }

    //clear all objects & context classes objects
    public void destroy() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }
    }

    public class MyCustomAdapterForItems implements GoogleMap.InfoWindowAdapter {
        private final View myContentsView;

        @SuppressLint("InflateParams")
        MyCustomAdapterForItems() {
            myContentsView = ((MainActivity) context).getLayoutInflater().inflate(R.layout.info_window, null);
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

    @SuppressWarnings("unchecked")
    public static class AedMapFactory extends ViewModelProvider.NewInstanceFactory {
        private final Context context;
        private final UrgentCaresDataResponse urgentCaresDataResponse;

        public AedMapFactory(Context context, UrgentCaresDataResponse urgentCaresDataResponse) {
            this.context = context;
            this.urgentCaresDataResponse = urgentCaresDataResponse;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new AedMapViewModel(context, urgentCaresDataResponse);
        }
    }

}
