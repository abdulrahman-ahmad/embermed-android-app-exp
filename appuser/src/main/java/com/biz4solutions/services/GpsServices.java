package com.biz4solutions.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;

import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.utilities.GpsServicesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

/*
 * Created by ketan on 20/11/2017.
 */
public class GpsServices extends Service implements LocationListener {
    private LocationManager mLocationManager;
    public static boolean isLocationUpdateRunning;
    public final long UPDATE_INTERVAL = 500;  /* 0.5 sec */
    public final long FASTEST_INTERVAL = 200; /* 0.2 sec*/
    private static final int NOTIFICATION_ID = 200;
    private static final String CHANNEL_ID = "200";
    private FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    public void onCreate() {
        System.out.println("aa ------GpsServices------ onCreate");
        sendNotification(this, false);
    }

    private void startLocationUpdates() {
        if (!isLocationUpdateRunning) {
            isLocationUpdateRunning = true;
            mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (mLocationManager != null) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_INTERVAL, 0, this);
            }
            startLocationUpdatesWithTime();
        }
    }

    // Trigger new location updates at interval
    @SuppressLint("RestrictedApi")
    protected void startLocationUpdatesWithTime() {
        // Create the location request to start receiving updates
        LocationRequest mLocationRequestTime = new LocationRequest();
        mLocationRequestTime.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestTime.setInterval(UPDATE_INTERVAL);
        mLocationRequestTime.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequestTime);

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (fusedLocationProviderClient == null) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        }
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequestTime, locationCallbackTime, Looper.myLooper());
    }

    LocationCallback locationCallbackTime = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            //System.out.println("aa ------locationResult----- " + locationResult);
            if (locationResult != null) {
                // do work here
                onLocationChanged(locationResult.getLastLocation());
            } else {
                System.out.println("startLocationUpdatesTime locationResult is null");
            }
        }
    };

    public void onLocationChanged(Location location) {
        //System.out.println("aa ------onLocationChanged ----- location=" + location);
        sendNotification(this, true);
        GpsServicesUtil.getInstance().setLatitude(location.getLatitude());
        GpsServicesUtil.getInstance().setLongitude(location.getLongitude());
        GpsServicesUtil.getInstance().sendCallback();
    }

    /*public static void addActions(Service service,NotificationCompat.Builder mNotifyBuilder) {
        //stop intent
        Intent stopReceive = new Intent();
        stopReceive.setAction(Constants.REKON_STOP_MONITORING_ACTION);
        PendingIntent pendingIntentStop = PendingIntent.getBroadcast(service, 12345, stopReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotifyBuilder.addAction(R.drawable.ic_stop_black_24dp, "Stop", pendingIntentStop);
    }*/

    public static void sendNotification(Service service, boolean isUpdate) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(service, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(service, 0, intent, PendingIntent.FLAG_NO_CREATE);

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_ID + " Notification Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) service.getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
            NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(service, CHANNEL_ID)
                    .setSmallIcon(com.biz4solutions.utilities.R.drawable.ic_logo)
                    .setContentTitle(service.getString(com.biz4solutions.utilities.R.string.info_notification_title))
                    .setOngoing(true)
                    .setAutoCancel(false)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(service.getString(com.biz4solutions.utilities.R.string.info_notification_message)))
                    .setContentText(service.getString(com.biz4solutions.utilities.R.string.info_notification_message))
                    .setContentIntent(pendingIntent);
            //addActions(service, mNotifyBuilder);
            Notification notification = mNotifyBuilder.build();
            if (isUpdate) {
                if (notificationManager != null) {
                    notificationManager.notify(NOTIFICATION_ID, notification);
                }
            } else {
                service.startForeground(NOTIFICATION_ID, notification);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // If we get killed, after returning from here, restart
        System.out.println("aa ----------- onStartCommand");

        startLocationUpdates();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    /* Remove the locationlistener updates when Services is stopped */
    @Override
    public void onDestroy() {
        System.out.println("aa ------GpsServices------ onDestroy");
        try {
            stopLocationUpdates();
            stopForeground(true);
            GpsServicesUtil.getInstance().removeCallback();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopLocationUpdates() {
        if (isLocationUpdateRunning) {
            isLocationUpdateRunning = false;
            System.out.println("stopLocationUpdates is called");
            if (mLocationManager != null) {
                mLocationManager.removeUpdates(this);
            }
            if (fusedLocationProviderClient != null) {
                if (locationCallbackTime != null) {
                    fusedLocationProviderClient.removeLocationUpdates(locationCallbackTime);
                }
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}