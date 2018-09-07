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
import com.biz4solutions.databinding.FragmentProviderReasonBinding;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.models.EmsRequest;

public class ProviderReasonFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "ProviderReasonFragment";
    private MainActivity mainActivity;
    private FragmentProviderReasonBinding binding;
    private final static String REQUEST_DETAILS = "REQUEST_DETAILS";
    private EmsRequest request;

    public ProviderReasonFragment() {
        // Required empty public constructor
    }

    public static ProviderReasonFragment newInstance(EmsRequest data) {
        ProviderReasonFragment fragment = new ProviderReasonFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_provider_reason, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_dashboard);
            mainActivity.toolbarTitle.setText(R.string.triage_service);
        }
        initClickListeners();
        return binding.getRoot();
    }

    private void initClickListeners() {
        binding.btnBookUber.setOnClickListener(this);
        binding.tvSeeMore.setOnClickListener(this);
        binding.tvSeeLess.setOnClickListener(this);
        binding.tvUrgentCares.setOnClickListener(this);
        binding.btnBookPcp.setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_book_pcp:
            case R.id.tv_urgent_cares:
            case R.id.btn_book_uber:
                Toast.makeText(mainActivity, R.string.coming_soon, Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_see_more:
                binding.tvSeeMore.setVisibility(View.GONE);
                binding.tvSeeLess.setVisibility(View.VISIBLE);
                binding.ivThreeDots.setVisibility(View.GONE);
                binding.tvReason.setMaxLines(100);
                break;
            case R.id.tv_see_less:
                binding.tvSeeLess.setVisibility(View.GONE);
                binding.tvSeeMore.setVisibility(View.VISIBLE);
                binding.ivThreeDots.setVisibility(View.VISIBLE);
                binding.tvReason.setMaxLines(5);
                break;
        }
    }

}