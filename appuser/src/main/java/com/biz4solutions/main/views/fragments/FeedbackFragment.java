package com.biz4solutions.main.views.fragments;

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

import com.biz4solutions.R;
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.databinding.FragmentFeedbackBinding;
import com.biz4solutions.interfaces.DialogDismissCallBackListener;
import com.biz4solutions.interfaces.FirebaseCallbackListener;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.models.request.FeedbackRequest;
import com.biz4solutions.models.response.EmptyResponse;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;
import com.biz4solutions.utilities.FirebaseAuthUtil;
import com.biz4solutions.utilities.FirebaseEventUtil;
import com.biz4solutions.utilities.NavigationUtil;

public class FeedbackFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "FeedbackFragment";
    private MainActivity mainActivity;
    private FragmentFeedbackBinding binding;
    private final static String REQUEST_ID = "REQUEST_ID";
    private String requestId;
    private EmsRequest request;
    public boolean isFromIncidentReport;

    public FeedbackFragment() {
        // Required empty public constructor
    }

    public static FeedbackFragment newInstance(String requestId, boolean isFromIncidentReport) {
        FeedbackFragment fragment = new FeedbackFragment();
        Bundle args = new Bundle();
        args.putSerializable(REQUEST_ID, requestId);
        args.putBoolean("isFromIncidentReport", isFromIncidentReport);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        if (getArguments() != null) {
            requestId = getArguments().getString(REQUEST_ID);
            isFromIncidentReport = getArguments().getBoolean("isFromIncidentReport");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feedback, container, false);
        mainActivity = (MainActivity) getActivity();
        setUpViews();
        initListeners();
        addFirebaseRequestEvent();
        return binding.getRoot();
    }

    private void setUpViews() {
        if (isFromIncidentReport) {
            NavigationUtil.getInstance().showBackArrow(mainActivity);
            binding.tvSkip.setVisibility(View.GONE);
        } else {
            if (mainActivity != null) {
                mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
                mainActivity.toolbarTitle.setText(R.string.triage_call);
                NavigationUtil.getInstance().hideMenu(mainActivity);
            }
        }
    }


    private void addFirebaseRequestEvent() {
        FirebaseEventUtil.getInstance().addFirebaseRequestEvent(requestId, new FirebaseCallbackListener<EmsRequest>() {
            @Override
            public void onSuccess(EmsRequest data) {
                request = data;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListeners() {
        binding.edtComment.setOnTouchListener(CommonFunctions.getInstance().scrollOnTouchListener(binding.edtComment.getId()));
        binding.btnSubmit.setOnClickListener(this);
        binding.tvSkip.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isFromIncidentReport) {
            NavigationUtil.getInstance().hideBackArrow(mainActivity);
        } else {
            if (mainActivity != null) {
                NavigationUtil.getInstance().showMenu(mainActivity);
            }
        }
        FirebaseEventUtil.getInstance().removeFirebaseRequestEvent();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (isFormValid()) {
                    CommonFunctions.getInstance().showAlertDialog(mainActivity, R.string.submit_feedback_message, R.string.yes, R.string.no, new DialogDismissCallBackListener<Boolean>() {
                        @Override
                        public void onClose(Boolean result) {
                            if (result) {
                                submitUserFeedBack();
                            }
                        }
                    });
                }
                break;
            case R.id.tv_skip:
                openNextFragment();
                break;
        }
    }

    private boolean isFormValid() {
        if (binding.rbRatingBar.getRating() == 0) {
            Toast.makeText(mainActivity, R.string.error_empty_rating, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void submitUserFeedBack() {
        if (CommonFunctions.getInstance().isOffline(mainActivity)) {
            Toast.makeText(mainActivity, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(mainActivity);
        final FeedbackRequest feedbackRequest = new FeedbackRequest();
        feedbackRequest.setComment(binding.edtComment.getText().toString().trim());
        feedbackRequest.setRequestId(requestId);
        feedbackRequest.setRating(binding.rbRatingBar.getRating());
        new ApiServices().submitUserFeedBack(mainActivity, feedbackRequest, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                EmptyResponse createEmsResponse = (EmptyResponse) response;
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, createEmsResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if (isFromIncidentReport) {
                    mainActivity.feedbackRequest = feedbackRequest;
                    mainActivity.getSupportFragmentManager().popBackStack();
                } else {
                    openNextFragment();
                }
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openNextFragment() {
        FirebaseAuthUtil.getInstance().storeSingleData(Constants.FIREBASE_REQUEST_TABLE, requestId, Constants.FIREBASE_IS_PATIENT_FEEDBACK_SUBMITTED_KEY, true);
        if (request != null && request.getProviderFeedback() != null && !request.getProviderFeedback().isEmpty()) {
            mainActivity.openProviderReasonFragment(request);
        } else {
            mainActivity.openTriageCallFeedbackWaitingFragment(requestId);
        }
    }

}