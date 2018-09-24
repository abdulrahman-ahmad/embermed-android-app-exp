package com.biz4solutions.main.views.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biz4solutions.R;
import com.biz4solutions.application.Application;
import com.biz4solutions.customs.taptargetview.TapCircleTargetView;
import com.biz4solutions.databinding.FragmentDashboardBinding;
import com.biz4solutions.interfaces.OnTargetClickListener;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.NavigationUtil;
import com.biz4solutions.utilities.TargetViewUtil;

public class DashboardFragment extends Fragment {

    public static final String fragmentName = "DashboardFragment";
    private MainActivity mainActivity;
    private FragmentDashboardBinding binding;
    private TapCircleTargetView tutorial;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            if (mainActivity.isTutorialMode) {
                NavigationUtil.getInstance().hideMenu(mainActivity);
            } else {
                mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
                NavigationUtil.getInstance().showMenu(mainActivity);
            }
            mainActivity.toolbarTitle.setText(R.string.dashboard);
        }
        binding.mainRippleBackground.startRippleAnimation();
        if (mainActivity.isTutorialMode) {
            showTutorial();
        } else {
            binding.alertBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isLocationPermissionGranted(102) && CommonFunctions.getInstance().isGPSEnabled(mainActivity)) {
                        mainActivity.openEmsAlertUnconsciousFragment();
                    }
                    vibrateEffect();
                }
            });
            isLocationPermissionGranted(101);
        }
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (!mainActivity.isTutorialMode) {
                mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
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
                CommonFunctions.getInstance().isGPSEnabled(mainActivity);
                return true;
            }
        } else {
            ((Application) mainActivity.getApplication()).createLoggerFile();
            CommonFunctions.getInstance().isGPSEnabled(mainActivity);
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
                        mainActivity.openEmsAlertUnconsciousFragment();
                        break;
                    case 101:
                        ((Application) mainActivity.getApplication()).createLoggerFile();
                        CommonFunctions.getInstance().isGPSEnabled(mainActivity);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showTutorial() {
        tutorial = TargetViewUtil.showTargetCircleForBigBtn(mainActivity,
                binding.alertBtn, getString(R.string.tutorial_title_dashboard),
                getString(R.string.tutorial_description_dashboard),
                new OnTargetClickListener() {
                    @Override
                    public void onTargetClick() {
                        vibrateEffect();
                        mainActivity.openEmsAlertUnconsciousFragment();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (tutorial != null) {
            tutorial.dismiss(false);
        }
        if (mainActivity.isTutorialMode) {
            NavigationUtil.getInstance().showMenu(mainActivity);
        }
    }
}