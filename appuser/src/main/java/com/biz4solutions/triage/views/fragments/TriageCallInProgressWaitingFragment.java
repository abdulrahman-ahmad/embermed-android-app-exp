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
import com.biz4solutions.databinding.FragmentTriageCallInProgressWaitingBinding;
import com.biz4solutions.interfaces.FirebaseCallbackListener;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.utilities.FirebaseEventUtil;
import com.biz4solutions.utilities.NavigationUtil;

public class TriageCallInProgressWaitingFragment extends Fragment {

    public static final String fragmentName = "TriageCallInProgressWaitingFragment";
    public static final String REQUEST_DETAILS = "REQUEST_DETAILS";
    private MainActivity mainActivity;
    private EmsRequest request;

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
        FragmentTriageCallInProgressWaitingBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_triage_call_in_progress_waiting, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.triage_service);
            NavigationUtil.getInstance().hideMenu(mainActivity);
        }
        FirebaseEventUtil.getInstance().addFirebaseRequestEvent(mainActivity.currentRequestId, new FirebaseCallbackListener<EmsRequest>() {
            @Override
            public void onSuccess(EmsRequest data) {
                request = data;
                //setCardiacCallView();
            }
        });
        mainActivity.btnLogOut.setVisibility(View.VISIBLE);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mainActivity != null) {
            NavigationUtil.getInstance().showMenu(mainActivity);
            mainActivity.btnLogOut.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}