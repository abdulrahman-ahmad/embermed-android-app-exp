package com.biz4solutions.fragments;

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
import android.widget.Toast;

import com.biz4solutions.R;
import com.biz4solutions.activities.MainActivity;
import com.biz4solutions.adapters.RequestListViewAdapter;
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.customs.LoadMoreListView;
import com.biz4solutions.databinding.FragmentDashboardBinding;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.models.response.EmsRequestResponse;
import com.biz4solutions.utilities.CommonFunctions;

import java.util.ArrayList;

public class DashboardFragment extends Fragment implements AdapterView.OnItemClickListener, LoadMoreListView.OnLoadMoreListener {

    public static final String fragmentName = "DashboardFragment";
    private MainActivity mainActivity;
    private int page = 0;
    private FragmentDashboardBinding binding;
    private boolean isLoadMore = true;
    private RequestListViewAdapter adapter;

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
                getRequestList();
            }
        });

        binding.swipeContainer.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.text_color,
                R.color.text_hint_color);


        getRequestList();

        binding.loadMoreListView.setOnLoadMoreListener(this);
        binding.loadMoreListView.setOnItemClickListener(this);


        return binding.getRoot();
    }

    private void getRequestList() {
        if (CommonFunctions.getInstance().isOffline(getContext())) {
            Toast.makeText(getContext(), getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            binding.swipeContainer.setRefreshing(false);
            binding.loadMoreListView.onLoadMoreComplete();
            return;
        }
        new ApiServices().getRequestList(getContext(), page, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                try {
                    EmsRequestResponse emsRequestResponse = (EmsRequestResponse) response;
                    System.out.println("aa --------- emsRequestResponse=" + emsRequestResponse);
                    if (emsRequestResponse != null && emsRequestResponse.getData() != null && !emsRequestResponse.getData().isEmpty()) {
                        isLoadMore = true;
                        page++;
                        if (adapter == null) {
                            adapter = new RequestListViewAdapter(mainActivity, emsRequestResponse.getData());
                            binding.loadMoreListView.setAdapter(adapter);
                        } else {
                            adapter.add(emsRequestResponse.getData(), page == 0);
                        }
                    } else {
                        if (adapter != null && page == 0) {
                            adapter.add(new ArrayList<EmsRequest>(), true);
                        }
                        isLoadMore = false;
                    }
                    binding.swipeContainer.setRefreshing(false);
                    binding.loadMoreListView.onLoadMoreComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                try {
                    binding.swipeContainer.setRefreshing(false);
                    binding.loadMoreListView.onLoadMoreComplete();
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("aa ---------- position=" + position);
    }

    @Override
    public void onLoadMore() {
        if (isLoadMore) {
            getRequestList();
        } else {
            binding.loadMoreListView.onLoadMoreComplete();
        }
        System.out.println("aa ---------- onLoadMore=");
    }
}
