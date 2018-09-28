package com.biz4solutions.triage.views.fragments;

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
import com.biz4solutions.databinding.FragmentUrgentCareMapBinding;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.models.response.UrgentCaresDataResponse;
import com.biz4solutions.triage.viewmodels.UrgentCareMapViewModel;
import com.biz4solutions.utilities.NavigationUtil;
import com.google.android.gms.maps.SupportMapFragment;

public class UrgentCareMapFragment extends Fragment {

    public static final String fragmentName = "UrgentCareMapFragment";
    private MainActivity mainActivity;
    private FragmentUrgentCareMapBinding binding;
    private final static String URGENT_CARES_RESPONSE = "URGENT_CARES_RESPONSE";
    private UrgentCaresDataResponse urgentCaresDataResponse;
    private UrgentCareMapViewModel viewModel;

    public UrgentCareMapFragment() {
        // Required empty public constructor
    }

    public static UrgentCareMapFragment newInstance(UrgentCaresDataResponse urgentCaresDataResponse) {
        UrgentCareMapFragment fragment = new UrgentCareMapFragment();
        Bundle args = new Bundle();
        args.putSerializable(URGENT_CARES_RESPONSE, urgentCaresDataResponse);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            urgentCaresDataResponse = (UrgentCaresDataResponse) getArguments().getSerializable(URGENT_CARES_RESPONSE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initBindingView(inflater, container);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.urgent_cares_around_you_text);
            NavigationUtil.getInstance().showBackArrow(mainActivity);
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
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_urgent_care_map, container, false);
            viewModel = new ViewModelProvider(this, new UrgentCareMapViewModel.UrgentCareMapFactory(getContext(), urgentCaresDataResponse)).get(UrgentCareMapViewModel.class);
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
                    case 102:
                        viewModel.requestLocationUpdate();
                        break;
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
    public void onDestroyView() {
        super.onDestroyView();
        if (mainActivity != null) {
            NavigationUtil.getInstance().hideBackArrow(mainActivity);
        }
        //destroys the allocated objects
        viewModel.destroy();
    }
}