package com.biz4solutions.triage.views.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.biz4solutions.R;
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.databinding.FragmentFeedbackBinding;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.models.request.FeedbackRequest;
import com.biz4solutions.models.response.EmptyResponse;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.NavigationUtil;

public class FeedbackFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "FeedbackFragment";
    private MainActivity mainActivity;
    private FragmentFeedbackBinding binding;

    public FeedbackFragment() {
        // Required empty public constructor
    }

    public static FeedbackFragment newInstance() {
        return new FeedbackFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feedback, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.feedback);
            NavigationUtil.getInstance().hideMenu(mainActivity);
        }
        initView();
        initClickListeners();
        return binding.getRoot();
    }

    private void initClickListeners() {
        binding.btnSubmit.setOnClickListener(this);
        binding.tvSkip.setOnClickListener(this);
    }

    private void initView() {
        binding.edtComment.setOnTouchListener(CommonFunctions.getInstance().scrollOnTouchListener(binding.edtComment.getId()));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mainActivity != null) {
            NavigationUtil.getInstance().showMenu(mainActivity);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (isFormValid()) {
                    submitUserFeedBack();
                }
                break;
            case R.id.tv_skip:
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
        FeedbackRequest feedbackRequest = new FeedbackRequest();
        feedbackRequest.setComment(binding.edtComment.getText().toString().trim());
        feedbackRequest.setRequestId("d1b7681d-56f9-44db-8963-71f8a8000981");
        feedbackRequest.setRating(4);
        new ApiServices().submitUserFeedBack(mainActivity, feedbackRequest, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                EmptyResponse createEmsResponse = (EmptyResponse) response;
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, createEmsResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

}