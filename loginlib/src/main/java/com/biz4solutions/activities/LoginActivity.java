package com.biz4solutions.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.biz4solutions.apiservices.ApiServiceUtil;
import com.biz4solutions.fragments.LoginFragment;
import com.biz4solutions.loginlib.R;
import com.biz4solutions.utilities.Constants;

/*
 * Created by ketan on 11/30/2017.
 */
public class LoginActivity extends AppCompatActivity {

    public String roleName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        roleName = getIntent().getStringExtra(Constants.ROLE_NAME);
        showLoginFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showLoginFragment() {
        getSupportFragmentManager().executePendingTransactions();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, LoginFragment.newInstance())
                .addToBackStack(LoginFragment.fragmentName)
                .commitAllowingStateLoss();
    }

    public void finishActivityWithResult(int resultCode) {
        ApiServiceUtil.resetInstance();
        Intent intent = new Intent();
        setResult(resultCode, intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (currentFragment instanceof LoginFragment) {
            finishActivityWithResult(RESULT_CANCELED);
            //android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            // If not manage back key according fragment stack
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }
}
