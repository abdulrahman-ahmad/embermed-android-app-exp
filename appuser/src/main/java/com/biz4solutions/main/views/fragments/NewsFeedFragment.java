package com.biz4solutions.main.views.fragments;

import android.arch.lifecycle.ViewModelProvider;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biz4solutions.R;
import com.biz4solutions.databinding.FragmentNewsFeedBinding;
import com.biz4solutions.main.viewmodels.NewsFeedViewModel;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.utilities.NavigationUtil;
import com.google.android.gms.maps.SupportMapFragment;

public class NewsFeedFragment extends Fragment {

    public static final String fragmentName = "NewsFeedFragment";
    private MainActivity mainActivity;

    private NewsFeedViewModel viewModel;
    private FragmentNewsFeedBinding binding;

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

    private void initBindingView(@NonNull LayoutInflater inflater, ViewGroup container) {
        if (binding != null) {
            ViewGroup parent = (ViewGroup) binding.getRoot().getParent();
            if (parent != null) {
                parent.removeView(binding.getRoot());
            }
        }
        try {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_news_feed, container, false);
            viewModel = new ViewModelProvider(this, new NewsFeedViewModel.NewsFeedFactory(getContext())).get(NewsFeedViewModel.class);
            binding.setViewModel(viewModel);
            binding.setLifecycleOwner(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void onDestroy() {
        super.onDestroy();
        viewModel.destroy();
    }
}