package com.biz4solutions.provider.main.views.fragments;

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
import com.biz4solutions.interfaces.FirebaseCallbackListener;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.models.request.FeedbackRequest;
import com.biz4solutions.models.response.EmptyResponse;
import com.biz4solutions.provider.R;
import com.biz4solutions.provider.databinding.FragmentFeedbackBinding;
import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.provider.triage.views.fragments.TriageIncidentReportFragment;
import com.biz4solutions.provider.utilities.FirebaseEventUtil;
import com.biz4solutions.provider.utilities.NavigationUtil;
import com.biz4solutions.utilities.CommonFunctions;

public class FeedbackFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "FeedbackFragment";
    private MainActivity mainActivity;
    private FragmentFeedbackBinding binding;
    private final static String REQUEST_ID = "REQUEST_ID";
    private String requestId;
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
            NavigationUtil.getInstance().showMenu(mainActivity);
        }
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
                                submitProviderFeedBack();
                            }
                        }
                    });
                }
                break;
            case R.id.tv_skip:
                openTriageIncidentReportFragment();
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

    private void submitProviderFeedBack() {
        if (CommonFunctions.getInstance().isOffline(mainActivity)) {
            Toast.makeText(mainActivity, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(mainActivity);
        final FeedbackRequest feedbackRequest = new FeedbackRequest();
        feedbackRequest.setComment(binding.edtComment.getText().toString().trim());
        feedbackRequest.setRequestId(requestId);
        feedbackRequest.setRating(binding.rbRatingBar.getRating());
        new ApiServices().submitProviderFeedBack(mainActivity, feedbackRequest, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                EmptyResponse createEmsResponse = (EmptyResponse) response;
                //CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, createEmsResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if (isFromIncidentReport) {
                    CommonFunctions.getInstance().dismissProgressDialog();
                    mainActivity.feedbackRequest = feedbackRequest;
                    mainActivity.getSupportFragmentManager().popBackStack();
                } else {
                    openTriageIncidentReportFragment();
                }
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openTriageIncidentReportFragment() {
        try {
            FirebaseEventUtil.getInstance().getFirebaseRequest(requestId, new FirebaseCallbackListener<EmsRequest>() {
                @Override
                public void onSuccess(EmsRequest data) {
                    CommonFunctions.getInstance().dismissProgressDialog();
                    Fragment currentFragment = mainActivity.getSupportFragmentManager().findFragmentById(R.id.main_container);
                    if (currentFragment instanceof TriageIncidentReportFragment) {
                        return;
                    }
                    mainActivity.getSupportFragmentManager().executePendingTransactions();
                    mainActivity.getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .replace(R.id.main_container, TriageIncidentReportFragment.newInstance(data))
                            .addToBackStack(TriageIncidentReportFragment.fragmentName)
                            .commitAllowingStateLoss();
                }
            });
        } catch (Exception e) {
            CommonFunctions.getInstance().dismissProgressDialog();
            e.printStackTrace();
        }
    }

}