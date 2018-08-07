package com.biz4solutions.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.biz4solutions.activities.LoginActivity;
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.loginlib.R;
import com.biz4solutions.models.response.EmptyResponse;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.loginlib.databinding.FragmentOtpVerificationBinding;

import java.util.HashMap;

public class OtpVerificationFragment extends Fragment implements View.OnClickListener {
    public final static String fragmentName = "OtpVerificationFragment";
    private final static String EMAIL_ID = "EMAIL_ID";
    private FragmentOtpVerificationBinding binding;
    private String emailId;
    private LoginActivity loginActivity;

    public OtpVerificationFragment() {
        // Required empty public constructor
    }

    public static OtpVerificationFragment newInstance(String emailId) {
        OtpVerificationFragment fragment = new OtpVerificationFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL_ID, emailId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginActivity = (LoginActivity) getActivity();
        if (getArguments() != null) {
            emailId = getArguments().getString(EMAIL_ID);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CommonFunctions.getInstance().hideSoftKeyBoard(getActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_otp_verification, container, false);
        binding.btnVerifyOtp.setOnClickListener(this);
        binding.txtResend.setOnClickListener(this);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });
        binding.edtOtp.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                        binding.btnVerifyOtp.performClick();
                        break;
                }
                return true;
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        CommonFunctions.getInstance().hideSoftKeyBoard(loginActivity);
        int i = view.getId();
        if (i == R.id.btn_verify_otp) {
            if (isOtpValid(binding.edtOtp.getText().toString().trim())) {
                verifyOtp();
            }
        } else if (i == R.id.txt_resend) {
            resendOtp();
        }
    }

    private boolean isOtpValid(String otp) {
        if (otp.isEmpty()) {
            Toast.makeText(getActivity(), R.string.error_empty_otp, Toast.LENGTH_SHORT).show();
            return false;
        } else if (otp.length() < 4) {
            Toast.makeText(getActivity(), R.string.error_invalid_otp, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //Request Otp web service integration
    private void resendOtp() {
        CommonFunctions.getInstance().hideSoftKeyBoard(getActivity());
        if (CommonFunctions.getInstance().isOffline(getContext())) {
            Toast.makeText(getContext(), getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(getContext());

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("email", emailId);
        hashMap.put("roleName", loginActivity.roleName);
        new ApiServices().resendOtp(getActivity(), hashMap, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                EmptyResponse emptyResponse = (EmptyResponse) response;
                Toast.makeText(getActivity(), emptyResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorResponse, int statusCode) {
                Toast.makeText(getActivity(), errorResponse, Toast.LENGTH_SHORT).show();
                CommonFunctions.getInstance().dismissProgressDialog();
            }
        });

    }

    //Verify Otp web service integration
    private void verifyOtp() {
        CommonFunctions.getInstance().hideSoftKeyBoard(getActivity());
        if (CommonFunctions.getInstance().isOffline(getContext())) {
            Toast.makeText(getContext(), getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(getContext());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("otp", Integer.parseInt(binding.edtOtp.getText().toString().trim()));
        hashMap.put("email", emailId);
        hashMap.put("roleName", loginActivity.roleName);
        new ApiServices().verifyOtp(getActivity(), hashMap, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                EmptyResponse emptyResponse = (EmptyResponse) response;
                Toast.makeText(getActivity(), emptyResponse.getMessage(), Toast.LENGTH_SHORT).show();
                showResetPasswordFragment();
            }

            @Override
            public void onFailure(String errorResponse, int statusCode) {
                Toast.makeText(getActivity(), errorResponse, Toast.LENGTH_SHORT).show();
                CommonFunctions.getInstance().dismissProgressDialog();
            }
        });
    }

    private void showResetPasswordFragment() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().executePendingTransactions();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.container, ResetPasswordFragment.newInstance(emailId, Integer.parseInt(binding.edtOtp.getText().toString().trim())))
                    .addToBackStack(ResetPasswordFragment.fragmentName)
                    .commitAllowingStateLoss();
        }
    }
}
