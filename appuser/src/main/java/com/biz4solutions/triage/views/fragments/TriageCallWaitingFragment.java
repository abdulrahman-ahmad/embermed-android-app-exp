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
import com.biz4solutions.databinding.FragmentTriageCallWaitingBinding;
import com.biz4solutions.interfaces.FirebaseCallbackListener;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.utilities.Constants;
import com.biz4solutions.utilities.FirebaseEventUtil;
import com.biz4solutions.utilities.NavigationUtil;

public class TriageCallWaitingFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "TriageCallWaitingFragment";
    public static final String REQUEST_DETAILS = "REQUEST_DETAILS";
    private MainActivity mainActivity;
    private boolean isCRCDone = false;
    private EmsRequest request;

    public TriageCallWaitingFragment() {
        // Required empty public constructor
    }

    public static TriageCallWaitingFragment newInstance(EmsRequest data) {
        TriageCallWaitingFragment fragment = new TriageCallWaitingFragment();
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
        FragmentTriageCallWaitingBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_triage_call_waiting, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.triage_call);
            NavigationUtil.getInstance().hideMenu(mainActivity);
        }
        FirebaseEventUtil.getInstance().addFirebaseRequestEvent(request.getId(), new FirebaseCallbackListener<EmsRequest>() {
            @Override
            public void onSuccess(EmsRequest data) {
                request = data;
                setRequestView();
            }
        });
        binding.btnCancelRequest.setOnClickListener(this);
        setRequestView();
        return binding.getRoot();
    }

    private void setRequestView() {
        if (request != null && request.getRequestStatus() != null) {
            switch (request.getRequestStatus()) {
                case Constants.STATUS_ACCEPTED:
                    mainActivity.startVideoCallWithPermissions(request.getId());
                    break;
                case Constants.STATUS_COMPLETED:
                    /*if (!isCRCDone) {
                        isCRCDone = true;
                        Toast.makeText(mainActivity, R.string.message_request_completed, Toast.LENGTH_SHORT).show();
                        mainActivity.reOpenDashBoardFragment();
                    }
                    break;*/
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel_request:
                mainActivity.showCancelRequestAlert(request.getId());
                break;
        }
    }
}