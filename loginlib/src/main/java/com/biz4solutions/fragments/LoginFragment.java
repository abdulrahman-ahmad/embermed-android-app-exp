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

import com.biz4solutions.activities.LoginActivity;
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.interfaces.CallbackListener;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.loginlib.BuildConfig;
import com.biz4solutions.loginlib.R;
import com.biz4solutions.loginlib.databinding.FragmentLoginBinding;
import com.biz4solutions.models.SocialMediaUserData;
import com.biz4solutions.models.request.LoginRequest;
import com.biz4solutions.models.response.LoginResponse;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;
import com.biz4solutions.utilities.FacebookUtil;
import com.biz4solutions.utilities.FirebaseAuthUtil;
import com.biz4solutions.utilities.GoogleUtil;

import static android.app.Activity.RESULT_OK;

/*
 * Created by ketan on 12/1/2017.
 */
public class LoginFragment extends Fragment implements View.OnClickListener, CallbackListener<SocialMediaUserData>, RestClientResponse {

    public static String fragmentName = "LoginFragment";
    private FragmentLoginBinding binding;
    private LoginActivity loginActivity;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        loginActivity = (LoginActivity) getActivity();
        GoogleUtil.getInstance().initGoogleConfig(getActivity(), BuildConfig.GOOGLE_AUTH_CLIENT_ID);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.edtEmail.setText("");
        binding.edtPassword.setText("");
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnSignIn.setOnClickListener(this);
        binding.btnSignInFacebook.setOnClickListener(this);
        binding.btnSignInGoogle.setOnClickListener(this);
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

    //Login web service integration
    private void loginWithEmail(String email) {
        if (CommonFunctions.getInstance().isOffline(getContext())) {
            Toast.makeText(getContext(), getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(getContext());
        LoginRequest loginRequest = new LoginRequest("EMAIL", loginActivity.roleName);
        loginRequest.setEmail(email);
        loginRequest.setPassword(binding.edtPassword.getText().toString().trim());
        new ApiServices().doLogin(getActivity(), loginRequest, this);
    }

    @Override
    public void onClick(View view) {
        CommonFunctions.getInstance().hideSoftKeyBoard(getActivity());
        int i = view.getId();
        if (i == R.id.btn_sign_in) {
            signInWithEmail();
        } else if (i == R.id.btn_sign_in_facebook) {
            Toast.makeText(loginActivity, R.string.coming_soon, Toast.LENGTH_SHORT).show();
            //signInWithFB();
        } else if (i == R.id.btn_sign_in_google) {
            Toast.makeText(loginActivity, R.string.coming_soon, Toast.LENGTH_SHORT).show();
            //signInWithGoogle();
        } else if (i == R.id.btn_sign_up) {
            showSignUpFragment();
        } else if (i == R.id.txt_forgot_password) {
            showForgotPasswordFragment();
        } else if (i == R.id.skip_login) {
            loginActivity.finishActivityWithResult(RESULT_OK);
        }
    }

    private void signInWithEmail() {
        if (isEmailIdValid(binding.edtEmail.getText().toString().trim())) {
            if (isPasswordValid(binding.edtPassword.getText().toString().trim())) {
                loginWithEmail(binding.edtEmail.getText().toString().trim());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        FacebookUtil.getInstance().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void signInWithFB() {
        if (CommonFunctions.getInstance().isOffline(getContext())) {
            Toast.makeText(getContext(), getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        FacebookUtil.getInstance().registerCallback(getContext(), this);
        FacebookUtil.getInstance().doLogin(getActivity());
    }

    private void signInWithGoogle() {
        if (CommonFunctions.getInstance().isOffline(getContext())) {
            Toast.makeText(getContext(), getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        GoogleUtil.getInstance().signIn(getActivity(), this);
    }

    @Override
    public void onSuccess(SocialMediaUserData data) {
        if (CommonFunctions.getInstance().isOffline(getContext())) {
            Toast.makeText(getContext(), getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        //System.out.println("aa ----------- SocialMediaUserData=" + data.toString());
        new ApiServices().socialAppLogin(getActivity(), data, this);
    }

    @Override
    public void onFailure(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(Object response, int statusCode) {
        LoginResponse loginResponse = (LoginResponse) response;
        CommonFunctions.getInstance().dismissProgressDialog();
        if (loginResponse.getData() != null) {
            FirebaseAuthUtil.getInstance().signInUser(loginResponse.getData().getEmail(), BuildConfig.FIREBASE_PASSWORD);
            SharedPrefsManager.getInstance().storeStringPreference(getContext(), Constants.USER_PREFERENCE, Constants.USER_AUTH_KEY, "Bearer " + loginResponse.getData().getAuthToken());
            SharedPrefsManager.getInstance().storeUserPreference(getContext(), Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY, loginResponse.getData());
            loginActivity.finishActivityWithResult(RESULT_OK);
        }
        Toast.makeText(loginActivity, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(String errorMessage, int statusCode) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
        CommonFunctions.getInstance().dismissProgressDialog();
    }
}