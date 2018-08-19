package com.biz4solutions.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.biz4solutions.R;
import com.biz4solutions.activities.MainActivity;
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.databinding.FragmentCardiacCallDetailsBinding;
import com.biz4solutions.interfaces.DialogDismissCallBackListener;
import com.biz4solutions.interfaces.FirebaseCallbackListener;
import com.biz4solutions.interfaces.OnBackClickListener;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.models.request.IncidentReport;
import com.biz4solutions.models.response.EmptyResponse;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.FirebaseEventUtil;
import com.biz4solutions.utilities.NavigationUtil;

public class CardiacCallDetailsFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "CardiacCallDetailsFragment";
    private final static String REQUEST_ID = "REQUEST_ID";
    private MainActivity mainActivity;
    private String requestId;
    private FragmentCardiacCallDetailsBinding binding;

    public CardiacCallDetailsFragment() {
        // Required empty public constructor
    }

    public static CardiacCallDetailsFragment newInstance(String requestId) {
        CardiacCallDetailsFragment fragment = new CardiacCallDetailsFragment();
        Bundle args = new Bundle();
        args.putString(REQUEST_ID, requestId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        if (getArguments() != null) {
            requestId = getArguments().getString(REQUEST_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cardiac_call_details, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.isRequestAcceptedByMe = false;
            mainActivity.currentRequestId = requestId;
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.cardiac_call);
            NavigationUtil.getInstance().showBackArrow(mainActivity, new OnBackClickListener() {
                @Override
                public void onBackPress() {
                    mainActivity.showRejectRequestAlert();
                }
            });
        }
        if (requestId != null) {
            FirebaseEventUtil.getInstance().addFirebaseRequestEvent(requestId, new FirebaseCallbackListener<EmsRequest>() {
                @Override
                public void onSuccess(EmsRequest data) {
                    System.out.println("aa ---------- EmsRequest = " + data);
                }
            });
        }
        binding.btnRespond.setOnClickListener(this);
        binding.btnSubmitReport.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        NavigationUtil.getInstance().hideBackArrow(mainActivity);
        FirebaseEventUtil.getInstance().removeFirebaseRequestEvent();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_respond:
                CommonFunctions.getInstance().showAlertDialog(mainActivity, R.string.accept_request_message, R.string.yes, R.string.no, new DialogDismissCallBackListener<Boolean>() {
                    @Override
                    public void onClose(Boolean result) {
                        if (result) {
                            acceptRequest();
                        }
                    }
                });
                break;
            case R.id.btn_submit_report:
                CommonFunctions.getInstance().showAlertDialog(mainActivity, R.string.complete_request_message, R.string.yes, R.string.no, new DialogDismissCallBackListener<Boolean>() {
                    @Override
                    public void onClose(Boolean result) {
                        if (result) {
                            completeRequest();
                        }
                    }
                });
                break;
        }
    }

    private void acceptRequest() {
        if (CommonFunctions.getInstance().isOffline(mainActivity)) {
            Toast.makeText(mainActivity, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(mainActivity);
        new ApiServices().acceptRequest(mainActivity, requestId, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                mainActivity.isRequestAcceptedByMe = true;
                EmptyResponse createEmsResponse = (EmptyResponse) response;
                CommonFunctions.getInstance().dismissProgressDialog();
                binding.btnRespond.setVisibility(View.GONE);
                binding.btnSubmitReport.setVisibility(View.VISIBLE);
                Toast.makeText(mainActivity, createEmsResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void completeRequest() {
        if (CommonFunctions.getInstance().isOffline(mainActivity)) {
            Toast.makeText(mainActivity, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(mainActivity);
        new ApiServices().completeRequest(mainActivity, requestId, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                mainActivity.isRequestAcceptedByMe = false;
                EmptyResponse createEmsResponse = (EmptyResponse) response;
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, createEmsResponse.getMessage(), Toast.LENGTH_SHORT).show();
                mainActivity.getSupportFragmentManager().popBackStack(DashboardFragment.fragmentName, 0);
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void submitIncidentReport() {
        if (CommonFunctions.getInstance().isOffline(mainActivity)) {
            Toast.makeText(mainActivity, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(mainActivity);
        IncidentReport body = new IncidentReport();
        body.setComment("Test Comment");
        body.setVictimLifeSaved(true);
        body.setRequestId(requestId);
        body.setTitle("Test Title");

        new ApiServices().submitIncidentReport(mainActivity, body, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                EmptyResponse createEmsResponse = (EmptyResponse) response;
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, createEmsResponse.getMessage(), Toast.LENGTH_SHORT).show();
                mainActivity.getSupportFragmentManager().popBackStack(DashboardFragment.fragmentName, 0);
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
