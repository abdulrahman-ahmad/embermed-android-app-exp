package com.biz4solutions.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.biz4solutions.R;
import com.biz4solutions.databinding.ActivitySplashBinding;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.utilities.Constants;

/*
 * Created by ketan on 11/30/2017.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        binding.splashFooter.startAnimation(slide_up);

        Animation scale_anim = AnimationUtils.loadAnimation(this, R.anim.scale);
        binding.splashLogo.startAnimation(scale_anim);

        String userAuthKey = SharedPrefsManager.getInstance().retrieveStringPreference(getBaseContext(), Constants.USER_PREFERENCE, Constants.USER_AUTH_KEY);
        boolean isSkipLogin = SharedPrefsManager.getInstance().retrieveBooleanPreference(getBaseContext(), Constants.USER_PREFERENCE, Constants.SKIP_LOGIN_KEY);
        if ((userAuthKey != null && !userAuthKey.isEmpty()) || isSkipLogin) {
            navigateToHomeScreen();
        } else {
            //navigateToLoginScreen();
            navigateToHomeScreen();
        }
    }

    private void navigateToLoginScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, Constants.SPLASH_TIMEOUT);
    }

    private void navigateToHomeScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, Constants.SPLASH_TIMEOUT);
    }
}