package com.biz4solutions.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.biz4solutions.R;
import com.biz4solutions.activities.MainActivity;
import com.biz4solutions.adapters.RequestListViewAdapter;
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.application.Application;
import com.biz4solutions.customs.LoadMoreListView;
import com.biz4solutions.databinding.FragmentDashboardBinding;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.models.response.EmsRequestResponse;
import com.biz4solutions.utilities.CommonFunctions;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment implements AdapterView.OnItemClickListener, LoadMoreListView.OnLoadMoreListener {

    public static final String fragmentName = "DashboardFragment";
    private MainActivity mainActivity;
    private int page = 0;
    private FragmentDashboardBinding binding;
    private boolean isLoadMore = true;
    private RequestListViewAdapter adapter;
    private List<EmsRequest> emsRequests = new ArrayList<>();

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.dashboard);
        }

        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                getRequestList(false);
            }
        });

        binding.swipeContainer.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.text_color,
                R.color.text_hint_color);

        getRequestList(true);

        binding.loadMoreListView.setOnLoadMoreListener(this);
        binding.loadMoreListView.setOnItemClickListener(this);
        LayoutInflater mInflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (mInflater != null) {
            @SuppressLint("InflateParams") RelativeLayout header = (RelativeLayout) mInflater.inflate(R.layout.request_list_header, null, false);
            binding.loadMoreListView.addHeaderView(header);
        }
        checkLocationPermissionGranted();
        return binding.getRoot();
    }

    private void checkLocationPermissionGranted() {
        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(perms, 101);
            } else {
                ((Application) mainActivity.getApplication()).createLoggerFile();
                CommonFunctions.getInstance().isGPSEnabled(mainActivity);
            }
        } else {
            ((Application) mainActivity.getApplication()).createLoggerFile();
            CommonFunctions.getInstance().isGPSEnabled(mainActivity);
        }
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
                        ((Application) mainActivity.getApplication()).createLoggerFile();
                        CommonFunctions.getInstance().isGPSEnabled(mainActivity);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getRequestList(boolean showLoader) {
        if (CommonFunctions.getInstance().isOffline(getContext())) {
            Toast.makeText(getContext(), getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            binding.swipeContainer.setRefreshing(false);
            binding.loadMoreListView.onLoadMoreComplete();
            return;
        }
        if (showLoader) {
            CommonFunctions.getInstance().loadProgressDialog(mainActivity);
        }
        new ApiServices().getRequestList(getContext(), page, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                try {
                    setRequestListViewAdapter((EmsRequestResponse) response);
                    hideLoader();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                try {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    hideLoader();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void hideLoader() {
        binding.swipeContainer.setRefreshing(false);
        binding.loadMoreListView.onLoadMoreComplete();
        CommonFunctions.getInstance().dismissProgressDialog();
    }

    private void setRequestListViewAdapter(EmsRequestResponse response) {
        if (response != null && response.getData() != null && !response.getData().isEmpty()) {
            isLoadMore = true;
            if (page == 0) {
                emsRequests = response.getData();
            } else {
                emsRequests.addAll(response.getData());
            }
            page++;

            if (adapter == null) {
                adapter = new RequestListViewAdapter(emsRequests);
                binding.loadMoreListView.setAdapter(adapter);
            } else {
                adapter.add(emsRequests);
            }
        } else {
            if (adapter != null && page == 0) {
                emsRequests = new ArrayList<>();
                adapter.add(emsRequests);
            }
            isLoadMore = false;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("aa ---------- position=" + position);
        Toast.makeText(mainActivity, R.string.coming_soon, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadMore() {
        if (isLoadMore) {
            getRequestList(false);
        } else {
            binding.loadMoreListView.onLoadMoreComplete();
        }
    }
}
