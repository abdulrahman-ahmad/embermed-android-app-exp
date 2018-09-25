package com.biz4solutions.triage.views.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.biz4solutions.R;
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.customs.LoadMoreListView;
import com.biz4solutions.customs.taptargetview.TapTargetView;
import com.biz4solutions.databinding.FragmentSymptomsBinding;
import com.biz4solutions.interfaces.OnTargetClickListener;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.models.Symptom;
import com.biz4solutions.models.request.CreateEmsRequest;
import com.biz4solutions.models.response.CreateEmsResponse;
import com.biz4solutions.models.response.SymptomResponse;
import com.biz4solutions.triage.adapters.SymptomsListViewAdapter;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.GpsServicesUtil;
import com.biz4solutions.utilities.NavigationUtil;
import com.biz4solutions.utilities.TargetViewUtil;

import java.util.ArrayList;
import java.util.List;

public class SymptomsFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, LoadMoreListView.OnLoadMoreListener {

    public static final String fragmentName = "SymptomsFragment";
    private MainActivity mainActivity;
    private FragmentSymptomsBinding binding;
    private boolean isRequestInProgress = false;
    private SymptomsListViewAdapter adapter;
    private int page = 0;
    private boolean isLoadMore = true;
    private List<Symptom> symptomList;
    private TapTargetView tutorial;

    public SymptomsFragment() {
        // Required empty public constructor
    }

    public static SymptomsFragment newInstance() {
        return new SymptomsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_symptoms, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            if (!mainActivity.isTutorialMode) {
                mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            }
            mainActivity.toolbarTitle.setText(R.string.symptoms);
            NavigationUtil.getInstance().showBackArrow(mainActivity);
        }

        initListView();
        if (mainActivity != null && mainActivity.isTutorialMode) {
            getSymptomListForTutorial();
        } else {
            initswipeContainer();
            getNewSymptomList(true);
            binding.btnSubmit.setOnClickListener(this);
        }

        return binding.getRoot();
    }

    private void initswipeContainer() {
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewSymptomList(false);
            }
        });

        binding.swipeContainer.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.text_color,
                R.color.text_hint_color);
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
            @SuppressLint("InflateParams") RelativeLayout header = (RelativeLayout) mInflater.inflate(R.layout.symptoms_list_header, null, false);
            binding.loadMoreListView.addHeaderView(header);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (adapter != null && adapter.getSelectedSymptomId() != null && !adapter.getSelectedSymptomId().isEmpty()) {
                    getUserLocation();
                } else {
                    Toast.makeText(mainActivity, R.string.error_validation_symptom_select, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void getUserLocation() {
        if (CommonFunctions.getInstance().isOffline(mainActivity)) {
            Toast.makeText(mainActivity, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(mainActivity);
        GpsServicesUtil.getInstance().onLocationCallbackListener(new GpsServicesUtil.LocationCallbackListener() {
            @Override
            public void onSuccess(double latitude, double longitude) {
                if (!isRequestInProgress) {
                    isRequestInProgress = true;
                    createRequest(latitude, longitude);
                }
            }

            @Override
            public void onError() {
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, R.string.error_location_fetch, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createRequest(double latitude, double longitude) {
        CreateEmsRequest body = new CreateEmsRequest();
        body.setLatitude(latitude);
        body.setLongitude(longitude);
        body.setUnconscious(false);
        List<String> symptomId = new ArrayList<>();
        symptomId.add(adapter.getSelectedSymptomId());
        body.setSymptomId(symptomId);
        new ApiServices().createRequest(mainActivity, body, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                isRequestInProgress = false;
                mainActivity.stopGpsService();
                CreateEmsResponse createEmsResponse = (CreateEmsResponse) response;
                CommonFunctions.getInstance().dismissProgressDialog();
                if (createEmsResponse != null && createEmsResponse.getData() != null) {
                    mainActivity.openTriageCallWaitingFragment(createEmsResponse.getData());
                } else {
                    if (createEmsResponse != null) {
                        Toast.makeText(mainActivity, createEmsResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                isRequestInProgress = false;
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLoadMore() {
        if (isLoadMore) {
            getSymptomList(false);
        } else {
            binding.loadMoreListView.onLoadMoreComplete();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (adapter != null && (position - 1 >= 0) && symptomList != null && symptomList.size() > position - 1) {
            adapter.setSelectedSymptomId(symptomList.get(position - 1).getId());
        }
    }

    private void getNewSymptomList(boolean showLoader) {
        page = 0;
        getSymptomList(showLoader);
    }

    private void getSymptomListForTutorial() {
        symptomList = new ArrayList<>();
        symptomList.add(new Symptom("1", "Symptom 1"));
        symptomList.add(new Symptom("2", "Symptom 2"));
        symptomList.add(new Symptom("3", "Symptom 3"));
        symptomList.add(new Symptom("4", "Symptom 4"));
        symptomList.add(new Symptom("5", "Symptom 5"));
        adapter = new SymptomsListViewAdapter(mainActivity, symptomList, new OnTargetClickListener() {
            @Override
            public void onTargetClick() {
                showTutorial();
            }
        });
        binding.loadMoreListView.setAdapter(adapter);
        setErrorView();
    }

    private void getSymptomList(boolean showLoader) {
        if (CommonFunctions.getInstance().isOffline(getContext())) {
            Toast.makeText(getContext(), getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            hideLoader();
            return;
        }
        if (showLoader) {
            CommonFunctions.getInstance().loadProgressDialog(mainActivity);
        }
        new ApiServices().getSymptomList(getContext(), page, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                try {
                    setRequestListViewAdapter((SymptomResponse) response);
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

    private void setRequestListViewAdapter(SymptomResponse response) {
        if (response != null && response.getPage() == page) {
            if (response.getData() != null && response.getData().getList() != null && !response.getData().getList().isEmpty()) {
                isLoadMore = true;
                if (page == 0) {
                    symptomList = response.getData().getList();
                } else {
                    symptomList.addAll(response.getData().getList());
                }
                page++;

                if (adapter == null) {
                    adapter = new SymptomsListViewAdapter(mainActivity, symptomList, null);
                    binding.loadMoreListView.setAdapter(adapter);
                } else {
                    adapter.add(symptomList);
                }
            } else {
                if (adapter != null && page == 0) {
                    symptomList = new ArrayList<>();
                    adapter.add(symptomList);
                }
                isLoadMore = false;
            }
        }
        setErrorView();
    }

    private void setErrorView() {
        if (symptomList == null || symptomList.isEmpty()) {
            binding.emptyAlertLayout.setVisibility(View.VISIBLE);
            binding.viewSubmit.setVisibility(View.GONE);
        } else {
            binding.emptyAlertLayout.setVisibility(View.GONE);
            binding.viewSubmit.setVisibility(View.VISIBLE);
        }
    }

    private void showTutorial() {
        tutorial = TargetViewUtil.showTargetRoundedForBtn(mainActivity,
                binding.btnSubmit, getString(R.string.tutorial_title_symptoms_submit_btn),
                getString(R.string.tutorial_description_symptoms_submit_btn),
                new OnTargetClickListener() {
                    @Override
                    public void onTargetClick() {
                        mainActivity.reHowItWorksFragment();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mainActivity != null) {
            if (mainActivity.isTutorialMode) {
                NavigationUtil.getInstance().showMenu(mainActivity);
            } else {
                NavigationUtil.getInstance().hideBackArrow(mainActivity);
            }
        }
        if (tutorial != null) {
            tutorial.dismiss(false);
        }
    }
}