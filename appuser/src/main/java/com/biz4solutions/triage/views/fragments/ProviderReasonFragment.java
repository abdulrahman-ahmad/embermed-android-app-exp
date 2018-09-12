package com.biz4solutions.triage.views.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.biz4solutions.R;
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.databinding.FragmentProviderReasonBinding;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.models.User;
import com.biz4solutions.models.response.UrgentCaresDataResponse;
import com.biz4solutions.models.response.UrgentCaresResponse;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;
import com.biz4solutions.utilities.FirebaseAuthUtil;

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
            mainActivity.toolbarTitle.setText(R.string.triage_call);
        }
        initClickListeners();
        initView();
        return binding.getRoot();
    }

    private void initView() {
        if (request != null) {
            FirebaseAuthUtil.getInstance().storeSingleData(Constants.FIREBASE_REQUEST_TABLE, request.getId(), Constants.FIREBASE_TRIAGE_CALL_STATUS_KEY, Constants.STATUS_COMPLETED);
            User user = SharedPrefsManager.getInstance().retrieveUserPreference(mainActivity, Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY);
            if (user != null) {
                FirebaseAuthUtil.getInstance().storeData(Constants.FIREBASE_USER_TABLE, user.getUserId(), null);
            }
            if (request.getProviderFeedbackReason() != null) {
                binding.tvReason.setText(request.getProviderFeedbackReason());
            }
            binding.tvReason.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (binding.tvReason.getLayout().getLineCount() <= 5) {
                        binding.tvSeeMore.performClick();
                    }
                    return true;
                }
            });
            switch ("" + request.getProviderFeedback()) {
                case Constants.TRIAGE_FEEDBACK_ER:
                    binding.tvProviderReason.setText(R.string.go_to_er);
                    binding.llBookUber.setVisibility(View.VISIBLE);
                    binding.llUrgentCares.setVisibility(View.GONE);
                    binding.llBookPcp.setVisibility(View.GONE);
                    break;
                case Constants.TRIAGE_FEEDBACK_URGENT_CARE:
                    binding.tvProviderReason.setText(R.string.go_to_urgent_care);
                    binding.llBookUber.setVisibility(View.GONE);
                    binding.llUrgentCares.setVisibility(View.VISIBLE);
                    binding.llBookPcp.setVisibility(View.GONE);
                    break;
                case Constants.TRIAGE_FEEDBACK_PCP:
                    binding.tvProviderReason.setText(R.string.go_to_pcp);
                    binding.llBookUber.setVisibility(View.GONE);
                    binding.llUrgentCares.setVisibility(View.GONE);
                    binding.llBookPcp.setVisibility(View.VISIBLE);
                    break;
            }
        }
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
            case R.id.btn_book_uber:
                Toast.makeText(mainActivity, R.string.coming_soon, Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_urgent_cares:
                getUrgentCareList();
                break;
            case R.id.tv_see_more:
                binding.tvSeeMore.setVisibility(View.GONE);
                //binding.tvSeeLess.setVisibility(View.VISIBLE);
                binding.ivThreeDots.setVisibility(View.GONE);
                binding.tvReason.setMaxLines(100);
                break;
            case R.id.tv_see_less:
                //binding.tvSeeLess.setVisibility(View.GONE);
                binding.tvSeeMore.setVisibility(View.VISIBLE);
                binding.ivThreeDots.setVisibility(View.VISIBLE);
                binding.tvReason.setMaxLines(5);
                break;
        }
    }

    private void getUrgentCareList() {
        if (CommonFunctions.getInstance().isOffline(mainActivity)) {
            Toast.makeText(mainActivity, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(mainActivity);
        new ApiServices().getUrgentCareList(getContext(), request.getLatitude(), request.getLongitude(), new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                try {
                    UrgentCaresResponse urgentCaresResponse = (UrgentCaresResponse) response;
                    CommonFunctions.getInstance().dismissProgressDialog();
                    Toast.makeText(mainActivity, urgentCaresResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    openUrgentCareMapFragment(urgentCaresResponse.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openUrgentCareMapFragment(UrgentCaresDataResponse urgentCaresDataResponse) {
        mainActivity.getSupportFragmentManager().executePendingTransactions();
        mainActivity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.main_container, UrgentCareMapFragment.newInstance(urgentCaresDataResponse))
                .addToBackStack(UrgentCareMapFragment.fragmentName)
                .commitAllowingStateLoss();
    }
}