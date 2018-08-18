package com.biz4solutions.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;

import java.util.ArrayList;
import java.util.List;

public class EmsAlertCardiacCallFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "EmsAlertCardiacCallFragment";
    private MainActivity mainActivity;
    private FragmentEmsAlertCardiacCallBinding binding;
    private final static String IS_NEED_TO_SHOW_QUE = "IS_NEED_TO_SHOW_QUE";
    List<Integer> gifList = new ArrayList<>();
    private int gifPosition = 0;
    private boolean isGifPlaying = true;
    private boolean isNeedToShowQue = false;

    public EmsAlertCardiacCallFragment() {
        // Required empty public constructor
    }

    public static EmsAlertCardiacCallFragment newInstance(boolean isNeedToShowQue) {
        EmsAlertCardiacCallFragment fragment = new EmsAlertCardiacCallFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_NEED_TO_SHOW_QUE, isNeedToShowQue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        if (getArguments() != null) {
            isNeedToShowQue = getArguments().getBoolean(IS_NEED_TO_SHOW_QUE, false);
        }
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
        binding.btnClose.setOnClickListener(this);
        binding.btnPlayCpr.setOnClickListener(this);
        binding.btnNextCpr.setOnClickListener(this);
        binding.btnPreviousCpr.setOnClickListener(this);
        binding.cprTutorialLink.setOnClickListener(this);

        gifPosition = 0;
        gifList.add(R.drawable.cpr1);
        gifList.add(R.drawable.cpr2);

        return binding.getRoot();
    }

    private void setCardiacCallView(EmsRequest data) {
        System.out.println("aa ---------- EmsRequest=" + data);
        if (data != null
                && data.getRequestStatus() != null
                && data.getRequestStatus().equals("ACCEPTED")) {
            binding.waitingLayout.setVisibility(View.GONE);
            binding.ambulanceLayout.setVisibility(View.VISIBLE);
            binding.ambulanceImage.startAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.enter_from_right));
            if (!isNeedToShowQue) {
                binding.btnYes.performClick();
            }
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
            case R.id.cpr_tutorial_link:
            case R.id.btn_no:
                binding.queLayout.setVisibility(View.GONE);
                binding.yesAnsLayout.setVisibility(View.GONE);
                binding.ambulanceImage.setVisibility(View.GONE);
                binding.cprTutorialLayout.setVisibility(View.VISIBLE);
                playGif();
                break;
            case R.id.btn_yes:
                binding.queLayout.setVisibility(View.GONE);
                binding.yesAnsLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_close:
                binding.yesAnsLayout.setVisibility(View.VISIBLE);
                binding.ambulanceImage.setVisibility(View.VISIBLE);
                binding.cprTutorialLayout.setVisibility(View.GONE);
                break;
            case R.id.btn_next_cpr:
                if (gifPosition == gifList.size() - 1) {
                    gifPosition = 0;
                } else {
                    gifPosition++;
                }
                playGif();
                break;
            case R.id.btn_previous_cpr:
                if (gifPosition == 0) {
                    gifPosition = gifList.size() - 1;
                } else {
                    gifPosition--;
                }
                playGif();
                break;
            case R.id.btn_play_cpr:
                if (isGifPlaying) {
                    binding.btnPlay.setVisibility(View.VISIBLE);
                    binding.btnPause.setVisibility(View.GONE);
                    ((GifDrawable) binding.gifImage.getDrawable()).stop();
                } else {
                    binding.btnPlay.setVisibility(View.GONE);
                    binding.btnPause.setVisibility(View.VISIBLE);
                    ((GifDrawable) binding.gifImage.getDrawable()).start();
                }
                isGifPlaying = !isGifPlaying;
                break;
        }
    }

    private void playGif() {
        Glide.with(mainActivity)
                .load(gifList.get(gifPosition))
                .into(binding.gifImage);
        isGifPlaying = true;
        binding.btnPlay.setVisibility(View.GONE);
        binding.btnPause.setVisibility(View.VISIBLE);
    }
}
