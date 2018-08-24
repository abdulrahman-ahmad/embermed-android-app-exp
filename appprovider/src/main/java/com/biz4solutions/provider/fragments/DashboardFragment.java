package com.biz4solutions.provider.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.customs.LoadMoreListView;
import com.biz4solutions.interfaces.FirebaseCallbackListener;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.models.Location;
import com.biz4solutions.models.response.EmsRequestResponse;
import com.biz4solutions.models.response.google.GoogleDistanceDurationResponse;
import com.biz4solutions.provider.R;
import com.biz4solutions.provider.activities.MainActivity;
import com.biz4solutions.provider.adapters.RequestListViewAdapter;
import com.biz4solutions.provider.application.Application;
import com.biz4solutions.provider.databinding.FragmentDashboardBinding;
import com.biz4solutions.provider.utilities.FirebaseEventUtil;
import com.biz4solutions.provider.utilities.GpsServicesUtil;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

public class DashboardFragment extends Fragment implements AdapterView.OnItemClickListener, LoadMoreListView.OnLoadMoreListener {

    public static final String fragmentName = "DashboardFragment";
    private MainActivity mainActivity;
    private int page = 0;
    private FragmentDashboardBinding binding;
    private boolean isLoadMore = true;
    private RequestListViewAdapter adapter;
    private List<EmsRequest> emsRequests = new ArrayList<>();
    private HashMap<String, String> distanceHashMap = new HashMap<>();
    private HashMap<String, String> durationHashMap = new HashMap<>();
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private boolean isApiInProgress = false;
    private Timer timer = new Timer();
    private TimerTask timerTask;
    private BroadcastReceiver clockBroadcastReceiver;

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

        initswipeContainer();
        addFirebaseEvent();
        initListView();

        if (mainActivity.isUpdateList) {
            mainActivity.isUpdateList = false;
            getNewRequestList(true);
        }
        checkLocationPermissionGranted();
        reSetTimer();
        addClockBroadcastReceiver();

        return binding.getRoot();
    }

    private void addClockBroadcastReceiver() {
        clockBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent != null && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                    //System.out.println("aa ------------ ---");
                    setDurationHashMap();
                }
            }
        };

        mainActivity.registerReceiver(clockBroadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    private void setDurationHashMap() {
        if (emsRequests != null
                && !emsRequests.isEmpty()) {
            durationHashMap.clear();
            for (EmsRequest request : emsRequests) {
                durationHashMap.put(request.getId(),
                        CommonFunctions.getInstance().getTimeAgo(System.currentTimeMillis() - request.getRequestTime()));
            }
            if (adapter != null) {
                adapter.addValue(durationHashMap);
            }
        }
    }

    private void initListView() {
        if (adapter != null) {
            binding.loadMoreListView.setAdapter(adapter);
            setErrorView();
        }
        binding.loadMoreListView.setOnLoadMoreListener(this);
        binding.loadMoreListView.setOnItemClickListener(this);
        LayoutInflater mInflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (mInflater != null) {
            @SuppressLint("InflateParams") RelativeLayout header = (RelativeLayout) mInflater.inflate(R.layout.request_list_header, null, false);
            binding.loadMoreListView.addHeaderView(header);
        }
    }

    private void initswipeContainer() {
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewRequestList(false);
            }
        });

        binding.swipeContainer.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.text_color,
                R.color.text_hint_color);
    }

    private void getNewRequestList(boolean showLoader) {
        if (mainActivity.isSuccessfullyInitFirebase) {
            page = 0;
            getRequestList(showLoader);
        } else {
            hideLoader();
        }
    }

    private void addFirebaseEvent() {
        if (mainActivity.isSuccessfullyInitFirebase) {
            if (mRunnable != null) {
                mHandler.removeCallbacks(mRunnable);
            }
            FirebaseEventUtil.getInstance().addFirebaseAlertEvent(mainActivity, new FirebaseCallbackListener<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    if (data) {
                        getNewRequestList(false);
                    } else {
                        setErrorView();
                    }
                }
            });
        } else {
            if (mRunnable == null) {
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        addFirebaseEvent();
                    }
                };
            }
            mHandler.postDelayed(mRunnable, 30000); // 30 secs
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FirebaseEventUtil.getInstance().removeFirebaseAlertEvent();
        if (mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        stopTimer();
        if (clockBroadcastReceiver != null) {
            mainActivity.unregisterReceiver(clockBroadcastReceiver);
        }
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
                startGpsService();
            }
        } else {
            startGpsService();
        }
    }

    private void startGpsService() {
        ((Application) mainActivity.getApplication()).createLoggerFile();
        CommonFunctions.getInstance().isGPSEnabled(mainActivity);
        mainActivity.startGpsService();
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
                        startGpsService();
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
            hideLoader();
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
                    setErrorView();
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
        if (response != null && response.getPage() == page) {
            if (response.getData() != null && !response.getData().isEmpty()) {
                isLoadMore = true;
                if (page == 0) {
                    emsRequests = response.getData();
                } else {
                    emsRequests.addAll(response.getData());
                }
                page++;

                setDurationHashMap();
                if (adapter == null) {
                    adapter = new RequestListViewAdapter(mainActivity, emsRequests, distanceHashMap, durationHashMap);
                    binding.loadMoreListView.setAdapter(adapter);
                } else {
                    adapter.add(emsRequests, durationHashMap);
                }
            } else {
                if (adapter != null && page == 0) {
                    emsRequests = new ArrayList<>();
                    setDurationHashMap();
                    adapter.add(emsRequests, durationHashMap);
                }
                isLoadMore = false;
            }
        }
        setErrorView();
    }

    private void setErrorView() {
        binding.txtPullToRefresh.setVisibility(View.VISIBLE);
        binding.txtNoAlertsAvailable.setVisibility(View.VISIBLE);
        if (emsRequests == null || emsRequests.isEmpty()) {
            binding.emptyAlertLayout.setVisibility(View.VISIBLE);
        } else {
            binding.emptyAlertLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (emsRequests != null && emsRequests.size() > position - 1) {
            mainActivity.getRequestDetails(emsRequests.get(position - 1).getId(), distanceHashMap.get(emsRequests.get(position - 1).getId()));
        }
    }

    @Override
    public void onLoadMore() {
        if (isLoadMore) {
            getRequestList(false);
        } else {
            binding.loadMoreListView.onLoadMoreComplete();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void getDistance() {
        GpsServicesUtil.getInstance().onLocationCallbackListener(new GpsServicesUtil.LocationCallbackListener() {
            @Override
            public void onSuccess(double latitude, double longitude) {
                if (CommonFunctions.getInstance().isOffline(mainActivity) || isApiInProgress || emsRequests == null || emsRequests.isEmpty()) {
                    return;
                }
                isApiInProgress = true;
                List<Location> locations = new ArrayList<>();
                distanceHashMap.clear();
                final List<EmsRequest> tempEmsRequests = new ArrayList<>();
                for (EmsRequest request : emsRequests) {
                    tempEmsRequests.add(request);
                    locations.add(new Location(request.getLatitude(), request.getLongitude()));
                    distanceHashMap.put(request.getId(), "");
                }
                new ApiServices().getDistanceDuration(mainActivity, "imperial", latitude, longitude, locations, new RestClientResponse() {
                    @Override
                    public void onSuccess(Object response, int statusCode) {
                        isApiInProgress = false;
                        GoogleDistanceDurationResponse durationResponse = (GoogleDistanceDurationResponse) response;
                        //System.out.println("aa ------------ durationResponse" + durationResponse);
                        if (durationResponse != null
                                && durationResponse.getRows() != null
                                && !durationResponse.getRows().isEmpty()
                                && durationResponse.getRows().get(0).getElements() != null
                                && !durationResponse.getRows().get(0).getElements().isEmpty()) {
                            for (int i = 0; i < durationResponse.getRows().get(0).getElements().size(); i++) {
                                if (durationResponse.getRows().get(0).getElements().get(i).getDistance() != null
                                        && !tempEmsRequests.isEmpty()
                                        && i < tempEmsRequests.size()) {
                                    distanceHashMap.put(tempEmsRequests.get(i).getId(), durationResponse.getRows().get(0).getElements().get(i).getDistance().getText());
                                }
                            }
                            if (adapter != null) {
                                adapter.add(distanceHashMap);
                            }
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage, int statusCode) {
                        isApiInProgress = false;
                        System.out.println("aa ------ errorMessage=" + errorMessage);
                    }
                });
            }

            @Override
            public void onError() {

            }
        });
    }

    private void reSetTimer() {
        stopTimer();
        timerTask = new TimerTask();
        timer.schedule(timerTask, Constants.DISTANCE_API_DElAY);//30 sec
        getDistance();
    }

    private void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timer.purge();
        }
    }

    private class TimerTask extends java.util.TimerTask {
        @Override
        public void run() {
            reSetTimer();
        }
    }
}