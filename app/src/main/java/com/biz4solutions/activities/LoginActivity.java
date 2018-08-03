package com.biz4solutions.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.biz4solutions.R;
import com.biz4solutions.fragments.LoginFragment;

import com.biz4solutions.utilities.FacebookUtil;
import com.biz4solutions.utilities.GoogleUtil;

/*
 * Created by ketan on 11/30/2017.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        showLoginFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FacebookUtil.getInstance().getRequestCode()) {
            FacebookUtil.getInstance().onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == GoogleUtil.getInstance().getRequestCode()) {
            GoogleUtil.getInstance().onActivityResult(requestCode, data);
        }
    }

    public void showLoginFragment() {
        getSupportFragmentManager().executePendingTransactions();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, LoginFragment.newInstance())
                .addToBackStack(LoginFragment.fragmentName)
                .commitAllowingStateLoss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (currentFragment instanceof LoginFragment) {
            finish();
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
