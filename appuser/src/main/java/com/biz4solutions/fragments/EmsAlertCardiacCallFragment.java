package com.biz4solutions.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biz4solutions.R;
import com.biz4solutions.activities.MainActivity;
import com.biz4solutions.databinding.FragmentEmsAlertCardiacCallBinding;
import com.biz4solutions.interfaces.OnBackClickListener;
import com.biz4solutions.utilities.NavigationUtil;

public class EmsAlertCardiacCallFragment extends Fragment {

    public static final String fragmentName = "EmsAlertCardiacCallFragment";
    private MainActivity mainActivity;

    public EmsAlertCardiacCallFragment() {
        // Required empty public constructor
    }

    public static EmsAlertCardiacCallFragment newInstance() {
        return new EmsAlertCardiacCallFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentEmsAlertCardiacCallBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ems_alert_cardiac_call, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.ems_alert);
            NavigationUtil.getInstance().showBackArrow(mainActivity, new OnBackClickListener() {
                @Override
                public void onBackPress() {
                    mainActivity.showCancelRequestAlert();
                }
            });
        }
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mainActivity != null) {
            NavigationUtil.getInstance().hideBackArrow(mainActivity);
        }
    }
}
