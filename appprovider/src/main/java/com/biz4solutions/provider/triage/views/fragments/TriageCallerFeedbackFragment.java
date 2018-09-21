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
import com.biz4solutions.models.response.EmptyResponse;
import com.biz4solutions.provider.R;
import com.biz4solutions.provider.databinding.FragmentTriageCallerFeedbackBinding;
import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.provider.main.views.fragments.FeedbackFragment;
import com.biz4solutions.provider.utilities.NavigationUtil;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;

import java.util.HashMap;

public class TriageCallerFeedbackFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "TriageCallerFeedbackFragment";
    private MainActivity mainActivity;
    private FragmentTriageCallerFeedbackBinding binding;
    private final static String REQUEST_ID = "REQUEST_ID";
    private String requestId;

    public TriageCallerFeedbackFragment() {
        // Required empty public constructor
    }

    public static TriageCallerFeedbackFragment newInstance(String requestId) {
        TriageCallerFeedbackFragment fragment = new TriageCallerFeedbackFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_triage_caller_feedback, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.triage_call);
            NavigationUtil.getInstance().hideMenu(mainActivity);
        }
        initListeners();
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListeners() {
        binding.edtReason.setOnTouchListener(CommonFunctions.getInstance().scrollOnTouchListener(binding.edtReason.getId()));
        binding.btnSubmit.setOnClickListener(this);
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
                if (checkIsWhereCallerGoSelected()) {
                    if (isFormValid()) {
                        CommonFunctions.getInstance().showAlertDialog(mainActivity, R.string.submit_feedback_message, R.string.yes, R.string.no, new DialogDismissCallBackListener<Boolean>() {
                            @Override
                            public void onClose(Boolean result) {
                                if (result) {
                                    completeRequest();
                                }
                            }
                        });
                    }
                }
                break;
        }
    }

    private void completeRequest() {
        if (CommonFunctions.getInstance().isOffline(mainActivity)) {
            Toast.makeText(mainActivity, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(mainActivity);
        HashMap<String, Object> body = new HashMap<>();
        body.put("requestId", requestId);
        body.put("reason", binding.edtReason.getText().toString().trim());
        String providerFeedback = "";
        if (binding.rdbGoToEr.isChecked()) {
            providerFeedback = Constants.TRIAGE_FEEDBACK_ER;
        } else if (binding.rdbGoToUrgentCare.isChecked()) {
            providerFeedback = Constants.TRIAGE_FEEDBACK_URGENT_CARE;
        } else if (binding.rdbGoToPcp.isChecked()) {
            providerFeedback = Constants.TRIAGE_FEEDBACK_PCP;
        }
        body.put("providerFeedback", providerFeedback);//ER//URGENT_CARE//PCP
        new ApiServices().completeRequest(mainActivity, body, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                EmptyResponse createEmsResponse = (EmptyResponse) response;
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, createEmsResponse.getMessage(), Toast.LENGTH_SHORT).show();
                openFeedbackFragment();
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFeedbackFragment() {
        try {
            Fragment currentFragment = mainActivity.getSupportFragmentManager().findFragmentById(R.id.main_container);
            if (currentFragment instanceof FeedbackFragment) {
                return;
            }
            mainActivity.getSupportFragmentManager().executePendingTransactions();
            mainActivity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.main_container, FeedbackFragment.newInstance(requestId))
                    .addToBackStack(FeedbackFragment.fragmentName)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean isFormValid() {
        if (binding.edtReason.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), R.string.error_empty_reason, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkIsWhereCallerGoSelected() {
        if (binding.rdbGoToEr.isChecked()) {
            return true;
        } else if (binding.rdbGoToUrgentCare.isChecked()) {
            return true;
        } else if (binding.rdbGoToPcp.isChecked()) {
            return true;
        }
        Toast.makeText(mainActivity, R.string.error_select_caller_feedback, Toast.LENGTH_SHORT).show();
        return false;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}