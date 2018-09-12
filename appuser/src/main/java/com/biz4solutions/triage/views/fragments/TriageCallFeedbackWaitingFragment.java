package com.biz4solutions.triage.views.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biz4solutions.R;
import com.biz4solutions.databinding.FragmentTriageCallFeedbackWaitingBinding;
import com.biz4solutions.interfaces.FirebaseCallbackListener;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.utilities.FirebaseEventUtil;
import com.biz4solutions.utilities.NavigationUtil;

public class TriageCallFeedbackWaitingFragment extends Fragment {

    public static final String fragmentName = "TriageCallFeedbackWaitingFragment";
    private MainActivity mainActivity;
    private final static String REQUEST_ID = "REQUEST_ID";
    private String requestId;

    public TriageCallFeedbackWaitingFragment() {
        // Required empty public constructor
    }

    public static TriageCallFeedbackWaitingFragment newInstance(String requestId) {
        TriageCallFeedbackWaitingFragment fragment = new TriageCallFeedbackWaitingFragment();
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
        FragmentTriageCallFeedbackWaitingBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_triage_call_feedback_waiting, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.triage_call);
            NavigationUtil.getInstance().hideMenu(mainActivity);
        }
        addFirebaseRequestEvent();

        return binding.getRoot();
    }

    private void addFirebaseRequestEvent() {
        FirebaseEventUtil.getInstance().addFirebaseRequestEvent(requestId, new FirebaseCallbackListener<EmsRequest>() {
            @Override
            public void onSuccess(EmsRequest data) {
                setRequestView(data);
            }
        });
    }

    private void setRequestView(EmsRequest request) {
        if (request != null && request.getProviderFeedback() != null && !request.getProviderFeedback().isEmpty()) {
            mainActivity.openProviderReasonFragment(request);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mainActivity != null) {
            NavigationUtil.getInstance().showMenu(mainActivity);
        }
        FirebaseEventUtil.getInstance().removeFirebaseRequestEvent();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}