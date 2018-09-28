package com.biz4solutions.provider.aedmaps.views.fragments;

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

import com.biz4solutions.models.response.UrgentCaresDataResponse;
import com.biz4solutions.provider.R;
import com.biz4solutions.provider.aedmaps.viewmodels.AedMapViewModel;
import com.biz4solutions.provider.databinding.FragmentAedMapBinding;
import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.provider.utilities.NavigationUtil;
import com.google.android.gms.maps.SupportMapFragment;

public class AedMapFragment extends Fragment {

    public static final String fragmentName = "AedMapFragment";
    private MainActivity mainActivity;
    private FragmentAedMapBinding binding;
    private final static String URGENT_CARES_RESPONSE = "URGENT_CARES_RESPONSE";
    private UrgentCaresDataResponse urgentCaresDataResponse;
    private AedMapViewModel viewModel;

    public AedMapFragment() {
        // Required empty public constructor
    }

    public static AedMapFragment newInstance(UrgentCaresDataResponse urgentCaresDataResponse) {
        AedMapFragment fragment = new AedMapFragment();
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
            mainActivity.toolbarTitle.setText(R.string.aed_maps);
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
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_aed_map, container, false);
            viewModel = new ViewModelProvider(this, new AedMapViewModel.AedMapFactory(getContext(), urgentCaresDataResponse)).get(AedMapViewModel.class);
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