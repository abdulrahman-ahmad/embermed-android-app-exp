package com.biz4solutions.newsfeed.views.fragments;

import android.animation.ValueAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.biz4solutions.R;
import com.biz4solutions.databinding.FragmentNewsFeedBinding;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.models.response.NewsFeedDataResponse;
import com.biz4solutions.newsfeed.viewmodels.NewsFeedViewModel;
import com.biz4solutions.newsfeed.viewpresenters.NewsFeedPresenter;
import com.biz4solutions.utilities.NavigationUtil;
import com.google.android.gms.maps.SupportMapFragment;

public class NewsFeedFragment extends Fragment implements NewsFeedPresenter {

    public static final String fragmentName = "NewsFeedFragment";
    private MainActivity mainActivity;

    private NewsFeedViewModel viewModel;
    private FragmentNewsFeedBinding binding;
    private boolean isAnimationEnd = false;

    public NewsFeedFragment() {
        // Required empty public constructor
    }

    public static NewsFeedFragment newInstance() {
        return new NewsFeedFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    private void initBindingView(@NonNull LayoutInflater inflater, ViewGroup container) {
        if (binding != null) {
            ViewGroup parent = (ViewGroup) binding.getRoot().getParent();
            if (parent != null) {
                parent.removeView(binding.getRoot());
            }
        }
        try {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_news_feed, container, false);
            if (viewModel == null) {
                viewModel = ViewModelProviders.of(this, new NewsFeedViewModel.NewsFeedFactory(getContext(), this)).get(NewsFeedViewModel.class);
            }
            binding.setViewModel(viewModel);
            binding.setLifecycleOwner(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initBindingView(inflater, container);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_news_feed);
            mainActivity.toolbarTitle.setText(R.string.news_feed);
            NavigationUtil.getInstance().showMenu(mainActivity);
        }
        initMapView();
        return binding.getRoot();
    }

    //init map fragment and fire viewmodel
    private void initMapView() {
        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        viewModel.initMapView(mMapFragment);
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
                    case 101:
                        viewModel.initMap();
                        break;
                    case 102:
                        viewModel.requestLocationUpdate();
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.destroy();
    }

    @Override
    public void toastMsg(String msg) {
        Toast.makeText(mainActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startAnimation(final NewsFeedDataResponse newsFeedData) {
        try {
            binding.bottomLayout.setVisibility(View.VISIBLE);
            long mCountDownTimerDuration = 0;
            if (!isAnimationEnd) {
                isAnimationEnd = true;
                mCountDownTimerDuration = 1000;
                binding.bottomLayout.startAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.enter_from_bottom));
            }
            final int[] providerCount = {0};
            final int[] lifeSaveCount = {0};
            final int[] triageCallCount = {0};

            int count = 0;
            if (newsFeedData != null) {
                if (newsFeedData.getProviderLocationList() != null) {
                    count = newsFeedData.getProviderLocationList().size();
                }
                if (count < newsFeedData.getTotalLifeSave()) {
                    count = (int) newsFeedData.getTotalLifeSave();
                }
                if (count < newsFeedData.getTotalTriageCall()) {
                    count = (int) newsFeedData.getTotalTriageCall();
                }
            }
            final ValueAnimator animator = new ValueAnimator();
            animator.setObjectValues(0, count);// here you set the range, from 0 to "count" value
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    providerCount[0]++;
                    lifeSaveCount[0]++;
                    triageCallCount[0]++;
                    if (newsFeedData != null) {
                        if (newsFeedData.getProviderLocationList() != null
                                && providerCount[0] <= newsFeedData.getProviderLocationList().size()) {
                            binding.tvProviderCount.setText(String.format("%s", providerCount[0]));
                        }
                        if (lifeSaveCount[0] <= newsFeedData.getTotalLifeSave()) {
                            binding.tvLifeSaveCount.setText(String.format("%s", lifeSaveCount[0]));
                        }
                        if (triageCallCount[0] <= newsFeedData.getTotalTriageCall()) {
                            binding.tvTriageCallCount.setText(String.format("%s", triageCallCount[0]));
                        }
                    }
                }
            });
            animator.setDuration(30000); //30 sec // here you set the duration of the anim
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    animator.start();
                }
            }, mCountDownTimerDuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}