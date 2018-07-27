package com.biz4solutions.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.biz4solutions.R;
import com.biz4solutions.apiservices.ApiServiceUtil;
import com.biz4solutions.databinding.ActivityMainBinding;
import com.biz4solutions.fragments.DashboardFragment;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.services.FirebaseInstanceIdService;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;
import com.biz4solutions.utilities.ExceptionHandler;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public NavigationView navigationView;
    public TextView toolbarTitle;
    private boolean doubleBackToExitPressedOnce;
    public ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        if (binding.appBarMain != null) {
            setSupportActionBar(binding.appBarMain.toolbar);
            toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.appBarMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            binding.drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }
        navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);

        String userAuthKey = SharedPrefsManager.getInstance().retrieveStringPreference(this, Constants.USER_PREFERENCE, Constants.USER_AUTH_KEY);
        if (userAuthKey == null || userAuthKey.isEmpty()) {
            navigationView.getMenu().findItem(R.id.nav_driving_records).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_track_the_car).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_settings).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_log_out).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_log_in).setVisible(false);
        } else {
            navigationView.getMenu().findItem(R.id.nav_log_in).setVisible(false);
            FirebaseInstanceIdService.setFcmToken(MainActivity.this);
        }
        navigationView.getMenu().findItem(R.id.nav_test).setVisible(false);
        if (binding.appBarMain != null) {
            toolbarTitle = binding.appBarMain.toolbarTitle;
        }
        openDashBoardFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (id) {
                    case R.id.nav_dashboard:
                        reOpenDashBoardFragment();
                        break;
                    case R.id.nav_log_out:
                        doLogOut();
                        break;
                    case R.id.nav_log_in:
                        doLogOut();
                        break;
                }
            }
        }, 250);
        return true;
    }

    private void doLogOut() {
        SharedPrefsManager.getInstance().clearPreference(this, Constants.USER_PREFERENCE);
        SharedPrefsManager.getInstance().clearPreference(this, Constants.TH_PREFERENCE);
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        ApiServiceUtil.resetInstance();
        finish();
    }

    private void openDashBoardFragment() {
        getSupportFragmentManager().executePendingTransactions();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, DashboardFragment.newInstance())
                .addToBackStack(DashboardFragment.fragmentName)
                .commitAllowingStateLoss();
    }

    private void reOpenDashBoardFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
        if (currentFragment instanceof DashboardFragment) {
            return;
        }
        getSupportFragmentManager().popBackStack(DashboardFragment.fragmentName, 0);
    }

    @Override
    public void onBackPressed() {
        CommonFunctions.getInstance().hideSoftKeyBoard(MainActivity.this);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //String prevFragmentName = "";
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                return;
            }
            String fragmentName = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
            switch (fragmentName) {
                case DashboardFragment.fragmentName:
                    if (doubleBackToExitPressedOnce) {
                        finish();
                        return;
                    }
                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, R.string.back_btn_message, Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                    break;
                default:
                    getSupportFragmentManager().popBackStack();
                    break;
            }
        }
    }
}