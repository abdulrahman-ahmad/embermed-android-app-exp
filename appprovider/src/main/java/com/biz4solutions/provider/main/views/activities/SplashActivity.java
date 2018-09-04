package com.biz4solutions.provider.main.views.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.biz4solutions.provider.R;
import com.biz4solutions.provider.databinding.ActivitySplashBinding;
import com.biz4solutions.utilities.Constants;

/*
 * Created by ketan on 11/30/2017.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        Animation scale_anim = AnimationUtils.loadAnimation(this, R.anim.scale);
        binding.splashLogo.startAnimation(scale_anim);
        navigateToHomeScreen();
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