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
import com.biz4solutions.loginlib.databinding.FragmentResetPasswordBinding;

import java.util.HashMap;

public class ResetPasswordFragment extends Fragment implements View.OnClickListener {
    public static String fragmentName = "ResetPasswordFragment";
    private FragmentResetPasswordBinding binding;
    private final static String EMAIL_ID = "EMAIL_ID";
    private final static String OTP = "OTP";
    private String emailId;
    private int otp;
    private LoginActivity loginActivity;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    public static ResetPasswordFragment newInstance(String emailId, int otp) {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL_ID, emailId);
        args.putInt(OTP, otp);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginActivity = (LoginActivity) getActivity();
        if (getArguments() != null) {
            emailId = getArguments().getString(EMAIL_ID);
            otp = getArguments().getInt(OTP);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CommonFunctions.getInstance().hideSoftKeyBoard(getActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reset_password, container, false);
        binding.btnResetPassword.setOnClickListener(this);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });
        binding.edtPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                        binding.btnResetPassword.performClick();
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
        if (i == R.id.btn_reset_password) {
            if (isPasswordValid(binding.edtPassword.getText().toString().trim())) {
                resetPassword();
            }
        }
    }

    //Reset password Otp web service integration
    private void resetPassword() {
        CommonFunctions.getInstance().hideSoftKeyBoard(getActivity());
        if (CommonFunctions.getInstance().isOffline(getContext())) {
            Toast.makeText(getContext(), getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(getContext());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("email", emailId);
        hashMap.put("roleName", loginActivity.roleName);
        hashMap.put("otp", otp);
        hashMap.put("password", binding.edtPassword.getText().toString().trim());
        new ApiServices().resetPassword(getActivity(), hashMap, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                EmptyResponse emptyResponse = (EmptyResponse) response;
                Toast.makeText(getActivity(), emptyResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack(LoginFragment.fragmentName, 0);
                }
            }

            @Override
            public void onFailure(String errorResponse, int statusCode) {
                Toast.makeText(getActivity(), errorResponse, Toast.LENGTH_SHORT).show();
                CommonFunctions.getInstance().dismissProgressDialog();
            }
        });
    }

    private boolean isPasswordValid(String password) {
        if (password.isEmpty()) {
            Toast.makeText(getActivity(), R.string.error_empty_new_password, Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 8 || password.length() > 20) {
            Toast.makeText(getActivity(), R.string.error_invalid_new_password, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}