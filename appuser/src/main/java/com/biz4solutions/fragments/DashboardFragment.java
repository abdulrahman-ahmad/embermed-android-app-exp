package com.biz4solutions.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.biz4solutions.R;
import com.biz4solutions.activities.MainActivity;
import com.biz4solutions.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    public static final String fragmentName = "DashboardFragment";
    private MainActivity mainActivity;

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
        final Vibrator vibrator = (Vibrator) mainActivity.getSystemService(Context.VIBRATOR_SERVICE);
        binding.alertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mainActivity, R.string.coming_soon, Toast.LENGTH_SHORT).show();
                openEmsAlertUnconsciousFragment();
                // Vibrate for 300 milliseconds
                if (vibrator != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        vibrator.vibrate(300);
                    }
                }
            }
        });

        return binding.getRoot();
    }

    private void openEmsAlertUnconsciousFragment() {
        mainActivity.getSupportFragmentManager().executePendingTransactions();
        mainActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, EmsAlertUnconsciousFragment.newInstance())
                .addToBackStack(EmsAlertUnconsciousFragment.fragmentName)
                .commitAllowingStateLoss();
    }
}
