package com.biz4solutions.cardiac.views.fragments;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.biz4solutions.R;
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.databinding.FragmentEmsAlertCardiacCallBinding;
import com.biz4solutions.interfaces.FirebaseCallbackListener;
import com.biz4solutions.interfaces.OnBackClickListener;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.models.Location;
import com.biz4solutions.models.response.google.GoogleDistanceDurationResponse;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;
import com.biz4solutions.utilities.FirebaseEventUtil;
import com.biz4solutions.utilities.NavigationUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class EmsAlertCardiacCallFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "EmsAlertCardiacCallFragment";
    private MainActivity mainActivity;
    private FragmentEmsAlertCardiacCallBinding binding;
    private final static String IS_NEED_TO_SHOW_QUE = "IS_NEED_TO_SHOW_QUE";
    private final static String REQUEST_DETAILS = "REQUEST_DETAILS";
    private final static String REQUEST_ID = "REQUEST_ID";
    List<Integer> gifList = new ArrayList<>();
    private int gifPosition = 0;
    private boolean isGifPlaying = true;
    private boolean isNeedToShowQue = false;
    private EmsRequest request;
    private boolean isAcceptedOpen = false;
    private boolean isCRCDone = false;
    private boolean isProviderLocationEventAdded = false;
    private boolean isApiInProgress = false;
    private Timer timer = new Timer();
    private TimerTask timerTask;
    private boolean isTimerReset = true;
    private ObservableField<String> arrivalTimeInMin = new ObservableField<>();
    private ObservableBoolean loaderVisibility = new ObservableBoolean();
    private ObservableField<String> arrivalTimeUnit = new ObservableField<>();
    public String requestId;

    public EmsAlertCardiacCallFragment() {
        // Required empty public constructor
    }

    public static EmsAlertCardiacCallFragment newInstance(boolean isNeedToShowQue, EmsRequest data, String requestId) {
        EmsAlertCardiacCallFragment fragment = new EmsAlertCardiacCallFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_NEED_TO_SHOW_QUE, isNeedToShowQue);
        args.putSerializable(REQUEST_DETAILS, data);
        args.putSerializable(REQUEST_ID, requestId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        if (getArguments() != null) {
            isNeedToShowQue = getArguments().getBoolean(IS_NEED_TO_SHOW_QUE, false);
            request = (EmsRequest) getArguments().getSerializable(REQUEST_DETAILS);
            requestId = getArguments().getString(REQUEST_ID);
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
                    mainActivity.showCancelRequestAlert(requestId);
                }
            });
        }
        binding.waitingImage.startAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.heartbeat));
        FirebaseEventUtil.getInstance().addFirebaseRequestEvent(requestId, new FirebaseCallbackListener<EmsRequest>() {
            @Override
            public void onSuccess(EmsRequest data) {
                request = data;
                addFirebaseProviderLocationEvent();
                setCardiacCallView();
            }
        });
        binding.btnNo.setOnClickListener(this);
        binding.btnYes.setOnClickListener(this);
        binding.btnClose.setOnClickListener(this);
        binding.btnPlayCpr.setOnClickListener(this);
        binding.btnNextCpr.setOnClickListener(this);
        binding.btnPreviousCpr.setOnClickListener(this);
        binding.cprTutorialLink.setOnClickListener(this);

        arrivalTimeInMin.set("");
        loaderVisibility.set(true);
        arrivalTimeUnit.set(getString(R.string.mins));
        binding.setArrivalTime(arrivalTimeInMin);
        binding.setLoaderVisibility(loaderVisibility);
        binding.setArrivalTimeUnit(arrivalTimeUnit);

        gifPosition = 0;
        gifList.add(R.drawable.cpr1);
        gifList.add(R.drawable.cpr2);

        if (request != null) {
            setCardiacCallView();
        }

        reSetTimer();

        return binding.getRoot();
    }

    private void reSetTimer() {
        stopTimer();
        timerTask = new TimerTask();
        timer.schedule(timerTask, Constants.DISTANCE_API_DElAY);//30 sec
    }

    private void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timer.purge();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (request != null) {
            isCRCDone = false;
            setCardiacCallView();
        }
    }

    private void addFirebaseProviderLocationEvent() {
        if (request != null && request.getProviderId() != null && !isProviderLocationEventAdded) {
            isProviderLocationEventAdded = true;
            FirebaseEventUtil.getInstance().addFirebaseProviderLocationEvent(request.getProviderId(), new FirebaseCallbackListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (CommonFunctions.getInstance().isOffline(mainActivity) || isApiInProgress || !isTimerReset) {
                        return;
                    }
                    isApiInProgress = true;
                    isTimerReset = false;
                    List<Location> locations = new ArrayList<>();
                    locations.add(location);
                    getDistanceDuration(locations);
                }
            });
        }
    }

    private void getDistanceDuration(List<Location> locations) {
        new ApiServices().getDistanceDuration(mainActivity, "metric", request.getLatitude(), request.getLongitude(), locations, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                try {
                    isApiInProgress = false;
                    GoogleDistanceDurationResponse durationResponse = (GoogleDistanceDurationResponse) response;
                    if (durationResponse != null
                            && durationResponse.getRows() != null
                            && !durationResponse.getRows().isEmpty()
                            && durationResponse.getRows().get(0).getElements() != null
                            && !durationResponse.getRows().get(0).getElements().isEmpty()
                            && durationResponse.getRows().get(0).getElements().get(0).getDuration() != null) {
                        int timeInSec = durationResponse.getRows().get(0).getElements().get(0).getDuration().getValue();
                        int min = timeInSec / 60;
                        //System.out.println("aa ------- min=" + min);
                        if (min <= 1) {
                            min = 1;
                            arrivalTimeUnit.set(getString(R.string.min));
                        } else {
                            arrivalTimeUnit.set(getString(R.string.mins));
                        }
                        loaderVisibility.set(false);
                        arrivalTimeInMin.set("" + min);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                isApiInProgress = false;
                System.out.println("aa ------ errorMessage=" + errorMessage);
            }
        });
    }

    private void setCardiacCallView() {
        //System.out.println("aa ---------- EmsRequest=" + request);
        if (request != null && request.getRequestStatus() != null) {
            switch (request.getRequestStatus()) {
                case Constants.STATUS_ACCEPTED:
                    if (!isAcceptedOpen) {
                        isAcceptedOpen = true;
                        binding.waitingLayout.setVisibility(View.GONE);
                        binding.headerWaiting.setVisibility(View.GONE);
                        binding.headerArriving.setVisibility(View.VISIBLE);
                        binding.ambulanceLayout.setVisibility(View.VISIBLE);
                        binding.ambulanceImage.startAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.enter_from_right));
                        binding.ambulanceTopLight.startAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.blinking));
                        if (!isNeedToShowQue) {
                            binding.btnYes.performClick();
                        }
                    }
                    break;
                case Constants.STATUS_COMPLETED:
                    if (!isCRCDone) {
                        isCRCDone = true;
                        Toast.makeText(mainActivity, R.string.message_request_completed, Toast.LENGTH_SHORT).show();
                        mainActivity.reOpenDashBoardFragment();
                    }
                    break;
                case Constants.STATUS_REJECTED:
                    if (!isCRCDone) {
                        isCRCDone = true;
                        Toast.makeText(mainActivity, R.string.message_request_rejected, Toast.LENGTH_SHORT).show();
                        mainActivity.reOpenDashBoardFragment();
                    }
                    break;
                case Constants.STATUS_CANCELLED:
                    if (!isCRCDone) {
                        isCRCDone = true;
                        Toast.makeText(mainActivity, R.string.message_request_cancelled, Toast.LENGTH_SHORT).show();
                        mainActivity.reOpenDashBoardFragment();
                    }
                    break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mainActivity != null) {
            NavigationUtil.getInstance().hideBackArrow(mainActivity);
        }
        CommonFunctions.getInstance().dismissAlertDialog();
        FirebaseEventUtil.getInstance().removeFirebaseRequestEvent();
        FirebaseEventUtil.getInstance().removeFirebaseProviderLocationEvent();
        stopTimer();
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private class TimerTask extends java.util.TimerTask {
        @Override
        public void run() {
            isTimerReset = true;
            reSetTimer();
        }
    }
}