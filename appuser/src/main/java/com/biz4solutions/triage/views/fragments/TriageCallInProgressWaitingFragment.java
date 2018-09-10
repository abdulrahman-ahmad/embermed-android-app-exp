package com.biz4solutions.triage.views.fragments;

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
import com.biz4solutions.databinding.FragmentTriageCallInProgressWaitingBinding;
import com.biz4solutions.interfaces.FirebaseCallbackListener;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.utilities.Constants;
import com.biz4solutions.utilities.FirebaseEventUtil;
import com.biz4solutions.utilities.NavigationUtil;

public class TriageCallInProgressWaitingFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "TriageCallInProgressWaitingFragment";
    public static final String REQUEST_DETAILS = "REQUEST_DETAILS";
    private MainActivity mainActivity;
    private EmsRequest request;
    private boolean isCRCDone = false;
    private FragmentTriageCallInProgressWaitingBinding binding;

    public TriageCallInProgressWaitingFragment() {
        // Required empty public constructor
    }

    public static TriageCallInProgressWaitingFragment newInstance(EmsRequest data) {
        TriageCallInProgressWaitingFragment fragment = new TriageCallInProgressWaitingFragment();
        Bundle args = new Bundle();
        args.putSerializable(REQUEST_DETAILS, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        if (getArguments() != null) {
            request = (EmsRequest) getArguments().getSerializable(REQUEST_DETAILS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_triage_call_in_progress_waiting, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.triage_call);
            NavigationUtil.getInstance().hideMenu(mainActivity);
        }
        FirebaseEventUtil.getInstance().addFirebaseRequestEvent(mainActivity.currentRequestId, new FirebaseCallbackListener<EmsRequest>() {
            @Override
            public void onSuccess(EmsRequest request) {
                setRequestView(request);
            }
        });
        mainActivity.btnLogOut.setVisibility(View.VISIBLE);
        binding.btnCancelRequest.setOnClickListener(this);
        setRequestView(request);
        return binding.getRoot();
    }

    private void setRequestView(EmsRequest request) {
        if (request != null && request.getRequestStatus() != null) {
            switch (request.getRequestStatus()) {
                case Constants.STATUS_ACCEPTED:
                    binding.btnCancelRequest.setVisibility(View.GONE);
                    break;
                case Constants.STATUS_COMPLETED:
                    if (!isCRCDone) {
                        isCRCDone = true;
                        Toast.makeText(mainActivity, R.string.message_request_completed, Toast.LENGTH_SHORT).show();
                        mainActivity.reOpenDashBoardFragment();
                    }
                    break;
                case Constants.STATUS_REJECTED:
                    if (!isCRCDone) {
                        isCRCDone = true;
                        Toast.makeText(mainActivity, R.string.message_request_rejected, Toast.LENGTH_SHORT).show();
                        mainActivity.reOpenDashBoardFragment();
                    }
                    break;
                case Constants.STATUS_CANCELLED:
                    if (!isCRCDone) {
                        isCRCDone = true;
                        Toast.makeText(mainActivity, R.string.message_request_cancelled, Toast.LENGTH_SHORT).show();
                        mainActivity.reOpenDashBoardFragment();
                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel_request:
                mainActivity.showCancelRequestAlert();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mainActivity != null) {
            NavigationUtil.getInstance().showMenu(mainActivity);
            mainActivity.btnLogOut.setVisibility(View.GONE);
        }
        FirebaseEventUtil.getInstance().removeFirebaseRequestEvent();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}