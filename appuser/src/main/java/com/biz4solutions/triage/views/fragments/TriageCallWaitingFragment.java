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
import com.biz4solutions.databinding.FragmentTriageCallWaitingBinding;
import com.biz4solutions.interfaces.FirebaseCallbackListener;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.utilities.FirebaseEventUtil;
import com.biz4solutions.utilities.NavigationUtil;

public class TriageCallWaitingFragment extends Fragment {

    public static final String fragmentName = "TriageCallWaitingFragment";
    public static final String REQUEST_DETAILS = "REQUEST_DETAILS";
    private MainActivity mainActivity;
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
            mainActivity.toolbarTitle.setText(R.string.ems_alert);
            NavigationUtil.getInstance().hideMenu(mainActivity);
        }
        FirebaseEventUtil.getInstance().addFirebaseRequestEvent(mainActivity.currentRequestId, new FirebaseCallbackListener<EmsRequest>() {
            @Override
            public void onSuccess(EmsRequest data) {
                request = data;
                //setCardiacCallView();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mainActivity != null) {
            NavigationUtil.getInstance().showMenu(mainActivity);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}