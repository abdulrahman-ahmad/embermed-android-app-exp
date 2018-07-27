package com.biz4solutions.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.biz4solutions.R;
import com.biz4solutions.activities.MainActivity;
import com.biz4solutions.apiservices.ApiServiceUtil;
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.databinding.FragmentLoginBinding;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.LoginRequest;
import com.biz4solutions.models.LoginResponseDTO;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;

/*
 * Created by ketan on 12/1/2017.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    public static String fragmentName = "LoginFragment";
    private FragmentLoginBinding binding;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnSignIn.setOnClickListener(this);
        binding.btnSignUp.setOnClickListener(this);
        binding.txtForgotPassword.setOnClickListener(this);
        binding.skipLogin.setOnClickListener(this);
        binding.edtPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                        binding.btnSignIn.performClick();
                        break;
                }
                return true;
            }
        });
    }

    private void openMainActivity() {
        ApiServiceUtil.resetInstance();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    //Login web service integration
    private void loginWithEmailOrPhoneNo(String email) {
        if (CommonFunctions.getInstance().isOffline(getContext())) {
            Toast.makeText(getContext(), getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(getContext());
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAppVersion(ApiServiceUtil.getInstance().getAppVersion());
        loginRequest.setDeviceId(ApiServiceUtil.getInstance().getDeviceID(getContext()));
        loginRequest.setDeviceName(ApiServiceUtil.getInstance().getDeviceName());
        loginRequest.setEmail(email);
        loginRequest.setDeviceType(ApiServiceUtil.getInstance().getDeviceType());
        loginRequest.setPassword(binding.edtPassword.getText().toString().trim());

        new ApiServices().doLogin(getActivity(), loginRequest, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                LoginResponseDTO loginResponseDTO = (LoginResponseDTO) response;
                SharedPrefsManager.getInstance().storeStringPreference(getContext(), Constants.USER_PREFERENCE, Constants.USER_AUTH_KEY, "Bearer " + loginResponseDTO.getToken());
                SharedPrefsManager.getInstance().storeUserPreference(getContext(), Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY, loginResponseDTO.getData());
                SharedPrefsManager.getInstance().storeBooleanPreference(getContext(), Constants.USER_PREFERENCE, Constants.SKIP_LOGIN_KEY, false);
                CommonFunctions.getInstance().dismissProgressDialog();
                openMainActivity();
            }

            @Override
            public void onFailure(String errorResponse, int statusCode) {
                Toast.makeText(getActivity(), errorResponse, Toast.LENGTH_SHORT).show();
                CommonFunctions.getInstance().dismissProgressDialog();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sign_in:
                signInWithEmailOrPhoneNo();
                break;
            case R.id.btn_sign_up:
                showSignUpFragment();
                break;
            case R.id.txt_forgot_password:
                showForgotPasswordFragment();
                break;
            case R.id.skip_login:
                SharedPrefsManager.getInstance().storeBooleanPreference(getContext(), Constants.USER_PREFERENCE, Constants.SKIP_LOGIN_KEY, true);
                openMainActivity();
                break;
        }
    }

    private void signInWithEmailOrPhoneNo() {
        if (isEmailIdValid(binding.edtEmail.getText().toString().trim())) {
            if (isPasswordValid(binding.edtPassword.getText().toString().trim())) {
                loginWithEmailOrPhoneNo(binding.edtEmail.getText().toString().trim());
            }
        }
    }

    private boolean isEmailIdValid(String emailId) {
        if (emailId.isEmpty()) {
            Toast.makeText(getActivity(), R.string.error_empty_email, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!emailId.matches(Constants.EMAIL_REGEX_SIGNUP)) {
            Toast.makeText(getActivity(), R.string.error_invalid_email, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password) {
        if (password.isEmpty()) {
            Toast.makeText(getActivity(), R.string.error_empty_password, Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 8) {
            Toast.makeText(getActivity(), R.string.error_incorrect_password, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CommonFunctions.getInstance().hideSoftKeyBoard(getActivity());
    }

    private void showSignUpFragment() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().executePendingTransactions();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.container, SignUpFragment.newInstance())
                    .addToBackStack(SignUpFragment.fragmentName)
                    .commitAllowingStateLoss();
        }
    }

    private void showForgotPasswordFragment() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().executePendingTransactions();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.container, ForgotPasswordFragment.newInstance())
                    .addToBackStack(ForgotPasswordFragment.fragmentName)
                    .commitAllowingStateLoss();
        }
    }
}