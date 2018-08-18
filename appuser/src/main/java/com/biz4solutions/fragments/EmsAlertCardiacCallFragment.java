package com.biz4solutions.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.biz4solutions.R;
import com.biz4solutions.activities.MainActivity;
import com.biz4solutions.databinding.FragmentEmsAlertCardiacCallBinding;
import com.biz4solutions.interfaces.FirebaseCallbackListener;
import com.biz4solutions.interfaces.OnBackClickListener;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.utilities.FirebaseEventUtil;
import com.biz4solutions.utilities.NavigationUtil;

public class EmsAlertCardiacCallFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "EmsAlertCardiacCallFragment";
    private MainActivity mainActivity;
    private FragmentEmsAlertCardiacCallBinding binding;

    public EmsAlertCardiacCallFragment() {
        // Required empty public constructor
    }

    public static EmsAlertCardiacCallFragment newInstance() {
        return new EmsAlertCardiacCallFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ems_alert_cardiac_call, container, false);
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
        binding.waitingImage.startAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.heartbeat));
        FirebaseEventUtil.getInstance().addFirebaseRequestEvent(mainActivity.currentRequestId, new FirebaseCallbackListener<EmsRequest>() {
            @Override
            public void onSuccess(EmsRequest data) {
                setCardiacCallView(data);
            }
        });
        binding.btnNo.setOnClickListener(this);
        binding.btnYes.setOnClickListener(this);
        return binding.getRoot();
    }

    private void setCardiacCallView(EmsRequest data) {
        System.out.println("aa ---------- EmsRequest=" + data);
        if (data != null
                && data.getRequestStatus() != null
                && data.getRequestStatus().equals("ACCEPTED")) {
            binding.waitingLayout.setVisibility(View.GONE);
            binding.ambulanceLayout.setVisibility(View.GONE);
            binding.ambulanceImage.startAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.enter_from_right));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mainActivity != null) {
            NavigationUtil.getInstance().hideBackArrow(mainActivity);
        }
        FirebaseEventUtil.getInstance().removeFirebaseRequestEvent();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_no:
                binding.queLayout.setVisibility(View.GONE);
                break;
            case R.id.btn_yes:
                binding.queLayout.setVisibility(View.GONE);
                binding.yesAnsLayout.setVisibility(View.VISIBLE);
                break;
        }
    }
}
