package com.biz4solutions.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biz4solutions.R;
import com.biz4solutions.activities.MainActivity;
import com.biz4solutions.application.Application;
import com.biz4solutions.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    public static final String fragmentName = "DashboardFragment";
    private MainActivity mainActivity;
    private AlertDialog.Builder alertDialog;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentDashboardBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.dashboard);
        }
        binding.mainRippleBackground.startRippleAnimation();
        binding.alertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLocationPermissionGranted(102) && isGPSEnabled()) {
                    openEmsAlertUnconsciousFragment();
                }
                vibrateEffect();
            }
        });
        isLocationPermissionGranted(101);
        return binding.getRoot();
    }

    private void vibrateEffect() {
        // Vibrate for 300 milliseconds
        Vibrator vibrator = (Vibrator) mainActivity.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                vibrator.vibrate(300);
            }
        }
    }

    private boolean isLocationPermissionGranted(int requestCode) {
        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(perms, requestCode);
            } else {
                ((Application) mainActivity.getApplication()).createLoggerFile();
                isGPSEnabled();
                return true;
            }
        } else {
            ((Application) mainActivity.getApplication()).createLoggerFile();
            isGPSEnabled();
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
                        openEmsAlertUnconsciousFragment();
                        break;
                    case 101:
                        ((Application) mainActivity.getApplication()).createLoggerFile();
                        isGPSEnabled();
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openEmsAlertUnconsciousFragment() {
        mainActivity.startGpsService();

        mainActivity.getSupportFragmentManager().executePendingTransactions();
        mainActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, EmsAlertUnconsciousFragment.newInstance())
                .addToBackStack(EmsAlertUnconsciousFragment.fragmentName)
                .commitAllowingStateLoss();
    }

    // Getting GPS status
    private boolean isGPSEnabled() {
        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        LocationManager locationManager;
        boolean isGPSEnabled = false;
        if (mainActivity != null) {
            locationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!isGPSEnabled) {
                    showSettingsAlert();
                }
            }
        }
        return isGPSEnabled;
    }

    /**
     * Function to show settings alert dialog.
     * On pressing the Settings button it will launch Settings Options.
     */
    public void showSettingsAlert() {
        if (alertDialog != null) {
            return;
        }
        alertDialog = new AlertDialog.Builder(mainActivity);

        // Setting Dialog Title
        //alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage(R.string.error_gps_off);

        // On pressing the Settings button.
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mainActivity.startActivity(intent);
                alertDialog = null;
            }
        });
        alertDialog.setCancelable(false);
        // On pressing the cancel button
        /*alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });*/

        // Showing Alert Message
        alertDialog.show();
    }
}
