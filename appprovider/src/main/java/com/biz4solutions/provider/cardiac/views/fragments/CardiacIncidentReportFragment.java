package com.biz4solutions.provider.cardiac.views.fragments;

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
import com.biz4solutions.interfaces.OnBackClickListener;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.models.request.IncidentReport;
import com.biz4solutions.models.response.EmptyResponse;
import com.biz4solutions.provider.R;
import com.biz4solutions.provider.databinding.FragmentCardiacIncidentReportBinding;
import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.provider.utilities.NavigationUtil;
import com.biz4solutions.utilities.CommonFunctions;

public class CardiacIncidentReportFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "CardiacIncidentReportFragment";
    private MainActivity mainActivity;
    private final static String REQUEST_DETAILS = "REQUEST_DETAILS";
    private EmsRequest requestDetails;
    private FragmentCardiacIncidentReportBinding binding;

    public CardiacIncidentReportFragment() {
        // Required empty public constructor
    }

    public static CardiacIncidentReportFragment newInstance(EmsRequest requestDetails) {
        CardiacIncidentReportFragment fragment = new CardiacIncidentReportFragment();
        Bundle args = new Bundle();
        args.putSerializable(REQUEST_DETAILS, requestDetails);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        if (getArguments() != null) {
            requestDetails = (EmsRequest) getArguments().getSerializable(REQUEST_DETAILS);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cardiac_incident_report, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            NavigationUtil.getInstance().showBackArrow(mainActivity, new OnBackClickListener() {
                @Override
                public void onBackPress() {
                    mainActivity.reOpenDashBoardFragment();
                }
            });
        }
        initView();
        initClickListeners();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mainActivity != null) {
            NavigationUtil.getInstance().hideBackArrow(mainActivity);
        }
    }

    private void initClickListeners() {
        binding.btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void initView() {
        binding.edtComment.setOnTouchListener(CommonFunctions.getInstance().scrollOnTouchListener(binding.edtComment.getId()));
        binding.requestListCardiacItem.txtTime.setVisibility(View.GONE);
        binding.requestListCardiacItem.txtDistance.setVisibility(View.GONE);
        binding.requestListCardiacItem.distanceLoader.setVisibility(View.GONE);
        if (requestDetails != null) {
            if (requestDetails.getUserDetails() != null) {
                String name = requestDetails.getUserDetails().getFirstName() + " " + requestDetails.getUserDetails().getLastName();
                binding.requestListCardiacItem.txtName.setText(name);
                String genderAge = requestDetails.getUserDetails().getGender() + ", " + requestDetails.getUserDetails().getAge() + "yrs";
                binding.requestListCardiacItem.txtGenderAge.setText(genderAge);
            }
            if (requestDetails.getPatientDisease() != null) {
                binding.cardiacPatientDiseaseItem.txtPatientDisease.setText(requestDetails.getPatientDisease());
            }
            binding.requestListCardiacItem.txtTime.setText(CommonFunctions.getInstance().getTimeAgo(System.currentTimeMillis() - requestDetails.getRequestTime()));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                if (checkIsVictimsLifeSaved()) {
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
                }
                break;
        }
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

    private boolean checkIsVictimsLifeSaved() {
        if (binding.rdbVictimLifeSavedYes.isChecked()) {
            return true;
        } else if (binding.rdbVictimLifeSavedNo.isChecked()) {
            return true;
        }
        Toast.makeText(mainActivity, R.string.error_select_is_victims_life_saved, Toast.LENGTH_SHORT).show();
        return false;
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
        body.setVictimLifeSaved(binding.rdbVictimLifeSavedYes.isChecked());
        body.setRequestId(requestDetails.getId());

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
