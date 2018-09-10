package com.biz4solutions.provider.triage.views.fragments;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.interfaces.DialogDismissCallBackListener;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.request.IncidentReport;
import com.biz4solutions.models.response.EmptyResponse;
import com.biz4solutions.provider.R;
import com.biz4solutions.provider.databinding.FragmentTriageIncidentReportBinding;
import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.provider.utilities.NavigationUtil;
import com.biz4solutions.utilities.CommonFunctions;

public class TriageIncidentReportFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "TriageIncidentReportFragment";
    private MainActivity mainActivity;
    private FragmentTriageIncidentReportBinding binding;
    private final static String REQUEST_ID = "REQUEST_ID";
    private String requestId;

    public TriageIncidentReportFragment() {
        // Required empty public constructor
    }

    public static TriageIncidentReportFragment newInstance(String requestId) {
        TriageIncidentReportFragment fragment = new TriageIncidentReportFragment();
        Bundle args = new Bundle();
        args.putSerializable(REQUEST_ID, requestId);
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_triage_incident_report, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.triage_call);
            NavigationUtil.getInstance().showMenu(mainActivity);
        }
        initClickListeners();
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initClickListeners() {
        binding.edtComment.setOnTouchListener(CommonFunctions.getInstance().scrollOnTouchListener(binding.edtComment.getId()));
        binding.btnSubmit.setOnClickListener(this);
        binding.tvSeeMore.setOnClickListener(this);
        binding.tvSeeLess.setOnClickListener(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mainActivity != null) {
            NavigationUtil.getInstance().hideMenu(mainActivity);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (isFormValid()) {
                    CommonFunctions.getInstance().showAlertDialog(mainActivity, R.string.submit_incident_report_message, R.string.yes, R.string.no, new DialogDismissCallBackListener<Boolean>() {
                        @Override
                        public void onClose(Boolean result) {
                            if (result) {
                                submitIncidentReport();
                            }
                        }
                    });
                }
                break;
            case R.id.tv_see_more:
                binding.tvSeeMore.setVisibility(View.GONE);
                //binding.tvSeeLess.setVisibility(View.VISIBLE);
                binding.ivThreeDots.setVisibility(View.GONE);
                binding.tvReason.setMaxLines(100);
                break;
            case R.id.tv_see_less:
                //binding.tvSeeLess.setVisibility(View.GONE);
                binding.tvSeeMore.setVisibility(View.VISIBLE);
                binding.ivThreeDots.setVisibility(View.VISIBLE);
                binding.tvReason.setMaxLines(4);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private boolean isFormValid() {
        if (binding.edtTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), R.string.error_empty_incident_report_title, Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.edtComment.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), R.string.error_empty_incident_report_comment, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void submitIncidentReport() {
        if (CommonFunctions.getInstance().isOffline(mainActivity)) {
            Toast.makeText(mainActivity, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(mainActivity);
        IncidentReport body = new IncidentReport();
        body.setTitle(binding.edtTitle.getText().toString().trim());
        body.setComment(binding.edtComment.getText().toString().trim());
        body.setRequestId(requestId);

        new ApiServices().submitIncidentReport(mainActivity, body, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                EmptyResponse createEmsResponse = (EmptyResponse) response;
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, createEmsResponse.getMessage(), Toast.LENGTH_SHORT).show();
                mainActivity.reOpenDashBoardFragment();
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

}