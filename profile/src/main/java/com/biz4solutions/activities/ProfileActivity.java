package com.biz4solutions.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.biz4solutions.fragments.EditProfileFragment;
import com.biz4solutions.fragments.ViewProfileFragment;
import com.biz4solutions.profile.R;
import com.biz4solutions.profile.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        initUi();
        initListeners();
        openViewProfileFragment();
    }


    private void initListeners() {
        binding.toolbarBtnEdit.setOnClickListener(this);
    }

    private void initUi() {
        final Drawable drawable = getResources().getDrawable(R.drawable.ic_arrow_back_btn);
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(drawable);
        }
    }

    public void hideEditOption(boolean hide) {
        if (hide) {
            binding.toolbarBtnEdit.setVisibility(View.GONE);
        } else {
            binding.toolbarBtnEdit.setVisibility(View.VISIBLE);
        }
    }

    public void openViewProfileFragment() {
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_profile);
            if (currentFragment instanceof ViewProfileFragment) {
                return;
            }
            getSupportFragmentManager().executePendingTransactions();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.frame_profile, ViewProfileFragment.newInstance())
                    .addToBackStack(ViewProfileFragment.fragmentName)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reOpenViewProfileFragment() {
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_profile);
            if (currentFragment instanceof ViewProfileFragment) {
                return;
            }
            getSupportFragmentManager().popBackStack();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openEditProfileFragment() {
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_profile);
            if (currentFragment instanceof EditProfileFragment) {
                return;
            }
            getSupportFragmentManager().executePendingTransactions();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.frame_profile, EditProfileFragment.newInstance())
                    .addToBackStack(EditProfileFragment.fragmentName)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return;
        }
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_profile);
        if (currentFragment instanceof ViewProfileFragment) {
            finish();
            return;
        }
        String fragmentName = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        switch (fragmentName) {
            case EditProfileFragment.fragmentName:
                reOpenViewProfileFragment();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.toolbar_btn_edit) {
            openEditProfileFragment();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}