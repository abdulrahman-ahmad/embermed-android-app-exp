package com.biz4solutions.provider.reports.view.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.customs.LoadMoreListView;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.models.User;
import com.biz4solutions.models.response.EmsRequestDetailsResponse;
import com.biz4solutions.models.response.EmsRequestResponse;
import com.biz4solutions.provider.R;
import com.biz4solutions.provider.databinding.FragmentDashboardBinding;
import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.provider.reports.view.adapters.ReportsListViewAdapter;
import com.biz4solutions.utilities.CommonFunctions;

import java.util.ArrayList;
import java.util.List;

public class IncidentReportsListFragment extends Fragment implements AdapterView.OnItemClickListener, LoadMoreListView.OnLoadMoreListener {

    public static final String fragmentName = "IncidentReportsListFragment";
    private MainActivity mainActivity;
    private int page = 0;
    private FragmentDashboardBinding binding;
    private boolean isLoadMore = true;
    private ReportsListViewAdapter adapter;
    private List<EmsRequest> emsRequests = new ArrayList<>();

    public IncidentReportsListFragment() {
        // Required empty public constructor
    }

    public static IncidentReportsListFragment newInstance() {
        return new IncidentReportsListFragment();
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
            mainActivity.navigationView.setCheckedItem(R.id.nav_incidents_reports);
            mainActivity.toolbarTitle.setText(R.string.dashboard);
        }

        initswipeContainer();
        initListView();

        if (adapter == null) {
            getNewRequestList(true);
        }
        return binding.getRoot();
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

    private void getRequestList(boolean showLoader) {
        if (CommonFunctions.getInstance().isOffline(getContext())) {
            Toast.makeText(getContext(), getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            hideLoader();
            return;
        }
        if (showLoader) {
            CommonFunctions.getInstance().loadProgressDialog(mainActivity);
        }
        new ApiServices().getProviderCompletedRequestList(getContext(), page, new RestClientResponse() {
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

                if (adapter == null) {
                    adapter = new ReportsListViewAdapter(emsRequests);
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
        try {
            int index = position - 1;
            if (emsRequests != null && emsRequests.size() > index && index >= 0) {
                getIncidentReportDetail(emsRequests.get(index));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getIncidentReportDetail(final EmsRequest request) {
        if (request != null && !request.getId().isEmpty()) {
            if (CommonFunctions.getInstance().isOffline(mainActivity)) {
                Toast.makeText(mainActivity, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
                return;
            }
            CommonFunctions.getInstance().loadProgressDialog(mainActivity);
            new ApiServices().getIncidentReportDetail(mainActivity, request.getId(), new RestClientResponse() {
                @Override
                public void onSuccess(Object response, int statusCode) {
                    CommonFunctions.getInstance().dismissProgressDialog();
                    if (response != null && ((EmsRequestDetailsResponse) response).getData() != null) {
                        openIncidentReportDetailsFragment(((EmsRequestDetailsResponse) response).getData(), request.getUserDetails());
                    }
                }

                @Override
                public void onFailure(String errorMessage, int statusCode) {
                    CommonFunctions.getInstance().dismissProgressDialog();
                    Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void openIncidentReportDetailsFragment(EmsRequest request, User userDetails) {
        Fragment currentFragment = mainActivity.getSupportFragmentManager().findFragmentById(R.id.main_container);
        if (currentFragment instanceof IncidentReportDetailsFragment) {
            return;
        }
        mainActivity.getSupportFragmentManager().executePendingTransactions();
        mainActivity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.main_container, IncidentReportDetailsFragment.newInstance(request, userDetails))
                .addToBackStack(IncidentReportDetailsFragment.fragmentName)
                .commitAllowingStateLoss();
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

}