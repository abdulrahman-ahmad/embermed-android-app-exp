package com.biz4solutions.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biz4solutions.R;
import com.biz4solutions.activities.MainActivity;
import com.biz4solutions.databinding.FragmentCardiacCallDetailsBinding;
import com.biz4solutions.utilities.NavigationUtil;

public class CardiacCallDetailsFragment extends Fragment {

    public static final String fragmentName = "CardiacCallDetailsFragment";
    private final static String REQUEST_ID = "REQUEST_ID";
    private MainActivity mainActivity;
    private String requestId;

    public CardiacCallDetailsFragment() {
        // Required empty public constructor
    }

    public static CardiacCallDetailsFragment newInstance(String requestId) {
        CardiacCallDetailsFragment fragment = new CardiacCallDetailsFragment();
        Bundle args = new Bundle();
        args.putString(REQUEST_ID, requestId);
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
        FragmentCardiacCallDetailsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cardiac_call_details, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.cardiac_call);
            NavigationUtil.getInstance().showBackArrow(mainActivity);
        }
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        NavigationUtil.getInstance().hideBackArrow(mainActivity);
    }
}
