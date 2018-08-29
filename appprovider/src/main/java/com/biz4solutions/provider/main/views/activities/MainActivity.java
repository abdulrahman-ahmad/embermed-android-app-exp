package com.biz4solutions.provider.main.views.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Build;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biz4solutions.activities.LoginActivity;
import com.biz4solutions.apiservices.ApiServiceUtil;
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.interfaces.DialogDismissCallBackListener;
import com.biz4solutions.interfaces.FirebaseCallbackListener;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.loginlib.BuildConfig;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.models.User;
import com.biz4solutions.models.response.EmsRequestDetailsResponse;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.provider.R;
import com.biz4solutions.provider.cardiac.views.fragments.CardiacCallDetailsFragment;
import com.biz4solutions.provider.databinding.ActivityMainBinding;
import com.biz4solutions.provider.main.views.fragments.DashboardFragment;
import com.biz4solutions.provider.main.views.fragments.NewsFeedFragment;
import com.biz4solutions.provider.services.FirebaseMessagingService;
import com.biz4solutions.provider.services.GpsServices;
import com.biz4solutions.provider.utilities.ExceptionHandler;
import com.biz4solutions.provider.utilities.FirebaseEventUtil;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;
import com.biz4solutions.utilities.FacebookUtil;
import com.biz4solutions.utilities.FirebaseAuthUtil;
import com.biz4solutions.utilities.GoogleUtil;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    public NavigationView navigationView;
    public TextView toolbarTitle;
    public LinearLayout btnCallAlerter;
    private boolean doubleBackToExitPressedOnce;
    public ActionBarDrawerToggle toggle;
    private BroadcastReceiver logoutBroadcastReceiver;
    public boolean isSuccessfullyInitFirebase = false;
    public String currentRequestId;
    public boolean isRequestAcceptedByMe = false;
    public boolean isUpdateList = false;
    public DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        if (binding.appBarMain != null) {
            setSupportActionBar(binding.appBarMain.toolbar);
            toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.appBarMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            binding.drawerLayout.addDrawerListener(toggle);
            drawerLayout = binding.drawerLayout;
            toggle.syncState();
        }
        navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);

        initView();

        if (binding.appBarMain != null) {
            toolbarTitle = binding.appBarMain.toolbarTitle;
            btnCallAlerter = binding.appBarMain.btnCallAlerter;
            binding.appBarMain.btnCallAlerter.setOnClickListener(this);
        }

        logoutBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //method call here
                String action = intent.getAction();
                if (action != null) {
                    switch (action) {
                        case Constants.LOGOUT_RECEIVER:
                            Toast.makeText(context, intent.getStringExtra(Constants.LOGOUT_MESSAGE), Toast.LENGTH_SHORT).show();
                            doLogOut();
                            break;
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.LOGOUT_RECEIVER);
        registerReceiver(logoutBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (logoutBroadcastReceiver != null) {
            unregisterReceiver(logoutBroadcastReceiver);
        }
    }

    private void initView() {
        String userAuthKey = SharedPrefsManager.getInstance().retrieveStringPreference(this, Constants.USER_PREFERENCE, Constants.USER_AUTH_KEY);
        if (userAuthKey == null || userAuthKey.isEmpty()) {
            navigationView.getMenu().findItem(R.id.nav_dashboard).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_log_out).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_log_in).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_account_settings).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_incidents_reports).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_medical_profile).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_aed_maps).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_contact_us).setVisible(false);
            openNewsFeedFragment();
        } else {
            navigationView.getMenu().findItem(R.id.nav_dashboard).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_log_out).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_log_in).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_account_settings).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_incidents_reports).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_medical_profile).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_aed_maps).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_contact_us).setVisible(true);
            openDashBoardFragment();
            FirebaseMessagingService.setFcmToken(MainActivity.this);
            FirebaseCallbackListener<Boolean> callbackListener = new FirebaseCallbackListener<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    isSuccessfullyInitFirebase = true;
                    if (data != null && data) {
                        addFirebaseUserEvent();
                    }
                }
            };
            if (FirebaseAuthUtil.getInstance().isFirebaseAuthValid()) {
                FirebaseAuthUtil.getInstance().initDB(callbackListener);
            } else {
                User user = SharedPrefsManager.getInstance().retrieveUserPreference(this, Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY);
                if (user != null) {
                    FirebaseAuthUtil.getInstance().signInUser(user.getEmail(), BuildConfig.FIREBASE_PASSWORD, callbackListener);
                }
            }
        }
    }

    public void addFirebaseUserEvent() {
        FirebaseEventUtil.getInstance().addFirebaseUserEvent(MainActivity.this, new FirebaseCallbackListener<User>() {
            @Override
            public void onSuccess(User data) {
                if (data != null) {
                    if (data.getProviderCurrentRequestId() != null && !data.getProviderCurrentRequestId().isEmpty()) {
                        getRequestDetails(data.getProviderCurrentRequestId(), "");
                    }
                }
            }
        });
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
        drawerLayout.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (id) {
                    case R.id.nav_dashboard:
                        reOpenDashBoardFragment();
                        break;
                    case R.id.nav_news_feed:
                        openNewsFeedFragment();
                        break;
                    case R.id.nav_log_out:
                        CommonFunctions.getInstance().showAlertDialog(MainActivity.this, R.string.logout_text, R.string.yes, R.string.no, new DialogDismissCallBackListener<Boolean>() {
                            @Override
                            public void onClose(Boolean result) {
                                if (result) {
                                    doLogOut();
                                }
                            }
                        });
                        break;
                    case R.id.nav_log_in:
                        doLogOut();
                        break;
                    default:
                        Toast.makeText(MainActivity.this, R.string.coming_soon, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, 250);
        return true;
    }

    private void doLogOut() {
        clearVariables();
        ApiServiceUtil.resetInstance();
        FacebookUtil.getInstance().doLogout();
        GoogleUtil.getInstance().doLogout();
        firebaseSignOut();
        stopGpsService();
        openLoginActivity();
    }

    private void clearVariables() {
        //currentRequestId = null;
        SharedPrefsManager.getInstance().clearPreference(this, Constants.USER_PREFERENCE);
    }

    private void openLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.putExtra(Constants.ROLE_NAME, Constants.ROLE_NAME_PROVIDER);
        startActivityForResult(intent, 149);
    }

    private void firebaseSignOut() {
        FirebaseEventUtil.getInstance().removeFirebaseUserEvent();
        FirebaseEventUtil.getInstance().removeFirebaseAlertEvent();
        //FirebaseAuthUtil.getInstance().signOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 149) {
            initView();
        }
    }

    private void openCardiacCallDetailsFragment(EmsRequest data, String distanceStr) {
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
            if (currentFragment instanceof CardiacCallDetailsFragment) {
                return;
            }
            getSupportFragmentManager().executePendingTransactions();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, CardiacCallDetailsFragment.newInstance(data, distanceStr))
                    .addToBackStack(CardiacCallDetailsFragment.fragmentName)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openDashBoardFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
        if (currentFragment instanceof DashboardFragment) {
            return;
        }
        getSupportFragmentManager().executePendingTransactions();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, DashboardFragment.newInstance())
                .addToBackStack(DashboardFragment.fragmentName)
                .commitAllowingStateLoss();
    }

    private void openNewsFeedFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
        if (currentFragment instanceof NewsFeedFragment) {
            return;
        }
        getSupportFragmentManager().executePendingTransactions();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, NewsFeedFragment.newInstance())
                .addToBackStack(NewsFeedFragment.fragmentName)
                .commitAllowingStateLoss();
    }

    private void reOpenDashBoardFragment() {
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
            if (currentFragment instanceof DashboardFragment) {
                return;
            }
            getSupportFragmentManager().popBackStack(DashboardFragment.fragmentName, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                case NewsFeedFragment.fragmentName:
                    String userAuthKey = SharedPrefsManager.getInstance().retrieveStringPreference(this, Constants.USER_PREFERENCE, Constants.USER_AUTH_KEY);
                    if (userAuthKey != null && !userAuthKey.isEmpty()) {
                        getSupportFragmentManager().popBackStack();
                        break;
                    }
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
                case CardiacCallDetailsFragment.fragmentName:
                    showRejectRequestAlert();
                    break;
                default:
                    getSupportFragmentManager().popBackStack();
                    break;
            }
        }
    }

    public void showRejectRequestAlert() {
        if (!isRequestAcceptedByMe) {
            getSupportFragmentManager().popBackStack();
        } /*else {
            // do nothing
        }*/
    }

    public void getRequestDetails(String requestId, final String distanceStr) {
        if (requestId != null && !requestId.isEmpty()) {
            if (CommonFunctions.getInstance().isOffline(MainActivity.this)) {
                Toast.makeText(MainActivity.this, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
                return;
            }
            CommonFunctions.getInstance().loadProgressDialog(MainActivity.this);
            new ApiServices().getRequestDetails(MainActivity.this, requestId, new RestClientResponse() {
                @Override
                public void onSuccess(Object response, int statusCode) {
                    CommonFunctions.getInstance().dismissProgressDialog();
                    openCardiacCallDetailsFragment(((EmsRequestDetailsResponse) response).getData(), distanceStr);
                }

                @Override
                public void onFailure(String errorMessage, int statusCode) {
                    CommonFunctions.getInstance().dismissProgressDialog();
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void startGpsService() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            startService(new Intent(MainActivity.this, GpsServices.class));
        } else {
            startForegroundService(new Intent(MainActivity.this, GpsServices.class));
        }
    }

    public void stopGpsService() {
        try {
            stopService(new Intent(MainActivity.this, GpsServices.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_call_alerter:
                Toast.makeText(MainActivity.this, R.string.coming_soon, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}