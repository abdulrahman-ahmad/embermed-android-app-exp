package com.biz4solutions.utilities;

import android.os.Handler;

public class GpsServicesUtil {
    private static GpsServicesUtil instance = null;
    private LocationCallbackListener mCallbackListener;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private double latitude = 0;
    private double longitude = 0;

    public static GpsServicesUtil getInstance() {
        if (instance == null) {
            instance = new GpsServicesUtil();
        }
        return instance;
    }

    public void onLocationCallbackListener(LocationCallbackListener callbackListener) {
        onLocationCallbackListener(callbackListener, 15000);
    }

    public void onLocationCallbackListener(final LocationCallbackListener callbackListener, int maxFetchLocationTime) {
        mCallbackListener = callbackListener;
        if (latitude > 0 || longitude > 0) {
            callbackListener.onSuccess(latitude, longitude);
            return;
        }
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mCallbackListener != null) {
                    if (latitude > 0 || longitude > 0) {
                        mCallbackListener.onSuccess(latitude, longitude);
                    } else {
                        mCallbackListener.onError();
                    }
                }
                removeCallback();
            }
        };
        if (maxFetchLocationTime <= 0) {
            maxFetchLocationTime = 15000; // update to 15 sec
        }
        mHandler.postDelayed(mRunnable, maxFetchLocationTime);
    }

    public void removeCallback() {
        try {
            if(mHandler != null && mRunnable != null) {
                mHandler.removeCallbacks(mRunnable);
            }
            mCallbackListener = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendCallback() {
        if (mCallbackListener != null && (latitude > 0 || longitude > 0)) {
            mCallbackListener.onSuccess(latitude, longitude);
        }
        removeCallback();
    }

    public interface LocationCallbackListener {
        void onSuccess(double latitude, double longitude);

        void onError();
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
