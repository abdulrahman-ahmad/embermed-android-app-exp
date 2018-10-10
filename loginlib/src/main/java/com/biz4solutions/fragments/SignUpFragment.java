package com.biz4solutions.fragments;

import android.app.Activity;
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

import com.biz4solutions.activities.LoginActivity;
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.loginlib.BuildConfig;
import com.biz4solutions.loginlib.R;
import com.biz4solutions.loginlib.databinding.FragmentSignUpBinding;
import com.biz4solutions.models.request.SignUpRequest;
import com.biz4solutions.models.response.LoginResponse;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;
import com.biz4solutions.utilities.FirebaseAuthUtil;

public class SignUpFragment extends Fragment implements View.OnClickListener {

    public static String fragmentName = "SignUpFragment";
    private FragmentSignUpBinding binding;
    private LoginActivity loginActivity;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false);
        loginActivity = (LoginActivity) getActivity();
        binding.btnSignUp.setOnClickListener(this);
        binding.btnBackToLogin.setOnClickListener(this);
        binding.skipLogin.setOnClickListener(this);
        //binding.edtConfirmPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
        binding.edtPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                        binding.btnSignUp.performClick();
                        break;
                }
                return true;
            }
        });
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        CommonFunctions.getInstance().hideSoftKeyBoard(getActivity());
        int i = view.getId();
        if (i == R.id.btn_sign_up) {
            if (isValidName(binding.edtFirstName.getText().toString().trim(), binding.edtLastName.getText().toString().trim())) {
                if (isEmailIdValid(binding.edtEmail.getText().toString().trim())) {
                    if (isPasswordValid(binding.edtPassword.getText().toString().trim()/*, binding.edtConfirmPassword.getText().toString().trim()*/, binding.edtPassword.getText().toString().trim())) {
                        //call sign web service
                        signUpWebServiceCall();
                    }
                }
            }
        } else if (i == R.id.btn_back_to_login) {
            view.setEnabled(false);
            if (getFragmentManager() != null) {
                getFragmentManager().popBackStack();
            }
        } else if (i == R.id.skip_login) {
            loginActivity.finishActivityWithResult(Activity.RESULT_OK);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CommonFunctions.getInstance().hideSoftKeyBoard(getActivity());
    }

    private void signUpWebServiceCall() {
        if (CommonFunctions.getInstance().isOffline(getContext())) {
            Toast.makeText(getContext(), getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(getContext());

        SignUpRequest signUpRequest = new SignUpRequest(Constants.SIGN_UP_TYPE_EMAIL, loginActivity.roleName);
        signUpRequest.setEmail(binding.edtEmail.getText().toString().trim());
        signUpRequest.setFirstName(binding.edtFirstName.getText().toString().trim());
        signUpRequest.setLastName(binding.edtLastName.getText().toString().trim());
        signUpRequest.setPassword(binding.edtPassword.getText().toString().trim());
        //signUpRequest.setConfirmPassword(binding.edtConfirmPassword.getText().toString().trim());
        signUpRequest.setConfirmPassword(binding.edtPassword.getText().toString().trim());

        new ApiServices().signUp(getActivity(), signUpRequest, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                LoginResponse loginResponse = (LoginResponse) response;
                CommonFunctions.getInstance().dismissProgressDialog();
                if (loginResponse.getData() != null) {
                    FirebaseAuthUtil.getInstance().signInUser(loginResponse.getData().getEmail(), BuildConfig.FIREBASE_PASSWORD);
                    SharedPrefsManager.getInstance().storeStringPreference(getContext(), Constants.USER_PREFERENCE, Constants.USER_AUTH_KEY, "Bearer " + loginResponse.getData().getAuthToken());
                    SharedPrefsManager.getInstance().storeUserPreference(getContext(), Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY, loginResponse.getData());
                    loginActivity.finishActivityWithResult(Activity.RESULT_OK);
                }
                Toast.makeText(loginActivity, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorResponse, int statusCode) {
                Toast.makeText(getActivity(), errorResponse, Toast.LENGTH_SHORT).show();
                CommonFunctions.getInstance().dismissProgressDialog();
            }
        });
    }

    private boolean isPasswordValid(String password, String confirmPassword) {
        if (password.isEmpty()) {
            Toast.makeText(getActivity(), R.string.error_empty_password, Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 8 || password.length() > 20) {
            Toast.makeText(getActivity(), R.string.error_invalid_password, Toast.LENGTH_SHORT).show();
            return false;
        } else if (confirmPassword.isEmpty()) {
            Toast.makeText(getActivity(), R.string.error_empty_confirm_password, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(getActivity(), R.string.error_invalid_confirm_password, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

    private boolean isValidName(String firstName, String lastName) {
        if (firstName.isEmpty()) {
            Toast.makeText(getActivity(), R.string.error_empty_first_name, Toast.LENGTH_SHORT).show();
            return false;
        } else if (lastName.isEmpty()) {
            Toast.makeText(getActivity(), R.string.error_empty_last_name, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}