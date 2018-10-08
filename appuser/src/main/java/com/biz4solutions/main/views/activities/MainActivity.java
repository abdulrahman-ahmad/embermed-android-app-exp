package com.biz4solutions.main.views.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import com.biz4solutions.R;
import com.biz4solutions.account.fragments.AccountSettingFragment;
import com.biz4solutions.activities.LoginActivity;
import com.biz4solutions.activities.OpenTokActivity;
import com.biz4solutions.apiservices.ApiServiceUtil;
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.cardiac.views.fragments.EmsAlertCardiacCallFragment;
import com.biz4solutions.databinding.ActivityMainBinding;
import com.biz4solutions.interfaces.DialogDismissCallBackListener;
import com.biz4solutions.interfaces.FirebaseCallbackListener;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.loginlib.BuildConfig;
import com.biz4solutions.main.views.fragments.DashboardFragment;
import com.biz4solutions.main.views.fragments.EmsAlertUnconsciousFragment;
import com.biz4solutions.main.views.fragments.FeedbackFragment;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.models.OpenTok;
import com.biz4solutions.models.User;
import com.biz4solutions.models.request.FeedbackRequest;
import com.biz4solutions.newsfeed.views.fragments.NewsFeedFragment;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.reports.views.fragments.IncidentReportsListFragment;
import com.biz4solutions.services.FirebaseMessagingService;
import com.biz4solutions.services.GpsServices;
import com.biz4solutions.triage.views.fragments.ProviderReasonFragment;
import com.biz4solutions.triage.views.fragments.TriageCallFeedbackWaitingFragment;
import com.biz4solutions.triage.views.fragments.TriageCallInProgressWaitingFragment;
import com.biz4solutions.triage.views.fragments.TriageCallWaitingFragment;
import com.biz4solutions.tutorial.views.fragments.HowItWorksFragment;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;
import com.biz4solutions.utilities.ExceptionHandler;
import com.biz4solutions.utilities.FacebookUtil;
import com.biz4solutions.utilities.FirebaseAuthUtil;
import com.biz4solutions.utilities.FirebaseEventUtil;
import com.biz4solutions.utilities.GoogleUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    public NavigationView navigationView;
    public TextView toolbarTitle;
    private boolean doubleBackToExitPressedOnce;
    public ActionBarDrawerToggle toggle;
    private BroadcastReceiver logoutBroadcastReceiver;
    public DrawerLayout drawerLayout;
    public static boolean isActivityOpen = false;
    public LinearLayout btnLogOut;
    public LinearLayout btnCall911;
    private boolean isOpenTokActivityOpen = false;
    private static final int PERMISSION_REQUEST_CODE = 121234;
    private EmsRequest tempRequest;
    public FeedbackRequest feedbackRequest;
    public boolean isTutorialMode = false;
    public int tutorialId = 0;

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
            btnLogOut = binding.appBarMain.btnLogOut;
            btnCall911 = binding.appBarMain.btnCall911;
            btnLogOut.setOnClickListener(this);
            btnCall911.setOnClickListener(this);
        }

        logoutBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //method call here
                String action = intent.getAction();
                if (action != null) {
                    switch (action) {
                        case Constants.LOGOUT_RECEIVER:
                            unauthorizedLogOut(intent.getStringExtra(Constants.LOGOUT_MESSAGE));
                            break;
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.LOGOUT_RECEIVER);
        registerReceiver(logoutBroadcastReceiver, intentFilter);
    }

    private void unauthorizedLogOut(String message) {
        if (message != null && !message.isEmpty()) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, R.string.error_session_expired, Toast.LENGTH_SHORT).show();
        }
        doLogOut();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (logoutBroadcastReceiver != null) {
            unregisterReceiver(logoutBroadcastReceiver);
        }
        FirebaseEventUtil.getInstance().removeFirebaseOpenTokEvent();
    }

    private void initView() {
        String userAuthKey = SharedPrefsManager.getInstance().retrieveStringPreference(this, Constants.USER_PREFERENCE, Constants.USER_AUTH_KEY);
        if (userAuthKey == null || userAuthKey.isEmpty()) {
            navigationView.getMenu().findItem(R.id.nav_dashboard).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_log_out).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_log_in).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_triage).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_call_911).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_account_settings).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_incident_reports).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_medical_profile).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_contact_us).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_subscription).setVisible(false);
            openNewsFeedFragment();
        } else {
            navigationView.getMenu().findItem(R.id.nav_dashboard).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_log_out).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_log_in).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_triage).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_call_911).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_account_settings).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_incident_reports).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_medical_profile).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_contact_us).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_subscription).setVisible(true);
            FirebaseMessagingService.setFcmToken(MainActivity.this);
            FirebaseCallbackListener<Boolean> callbackListener = new FirebaseCallbackListener<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
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
            openDashBoardFragment();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityOpen = true;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityOpen = false;
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            boolean userAllowedAllRequestPermissions = true;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    userAllowedAllRequestPermissions = false;
                }
            }

            if (userAllowedAllRequestPermissions) {
                switch (requestCode) {
                    case PERMISSION_REQUEST_CODE:
                        startVideoCall(tempRequest);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        // Handle navigation view item clicks here.
        drawerLayout.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (item.getItemId()) {
                    case R.id.nav_dashboard:
                        reOpenDashBoardFragment();
                        break;
                    case R.id.nav_news_feed:
                        String userAuthKey = SharedPrefsManager.getInstance().retrieveStringPreference(MainActivity.this, Constants.USER_PREFERENCE, Constants.USER_AUTH_KEY);
                        if (userAuthKey != null && !userAuthKey.isEmpty()) {
                            openNewsFeedFragmentWithAnimation();
                        } else {
                            reOpenNewsFeedFragment();
                        }
                        break;
                    case R.id.nav_account_settings:
                        openAccountSettingFragment();
                        break;
                    case R.id.nav_how_it_works:
                        openHowItWorksFragment();
                        break;
                    case R.id.nav_log_out:
                        showLogOutAlertDialog();
                        break;
                    case R.id.nav_log_in:
                        doLogOut();
                        break;
                    case R.id.nav_incident_reports:
                        openIncidentReportsListFragment();
                        break;
                    default:
                        Toast.makeText(MainActivity.this, R.string.coming_soon, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, 250);
        return true;
    }

    private void showLogOutAlertDialog() {
        CommonFunctions.getInstance().showAlertDialog(MainActivity.this, R.string.logout_text, R.string.yes, R.string.no, new DialogDismissCallBackListener<Boolean>() {
            @Override
            public void onClose(Boolean result) {
                if (result) {
                    callLogoutAPI();
                }
            }
        });
    }

    private void callLogoutAPI() {
        if (CommonFunctions.getInstance().isOffline(MainActivity.this)) {
            Toast.makeText(MainActivity.this, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(MainActivity.this);
        new ApiServices().logout(MainActivity.this, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                doLogOut();
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
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
        SharedPrefsManager.getInstance().clearPreference(this, Constants.USER_PREFERENCE);
    }

    private void openLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.putExtra(Constants.ROLE_NAME, Constants.ROLE_NAME_USER);
        startActivityForResult(intent, 149);
    }

    private void firebaseSignOut() {
        FirebaseAuthUtil.getInstance().signOut();
        FirebaseEventUtil.getInstance().removeFirebaseUserEvent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 149:
                initView();
                break;
            case OpenTokActivity.RC_OPENTOK_ACTIVITY:
                isOpenTokActivityOpen = false;
                FirebaseEventUtil.getInstance().removeFirebaseOpenTokEvent();
                if (resultCode == RESULT_OK) {
                    openFeedbackFragment(data.getStringExtra(OpenTokActivity.OPENTOK_REQUEST_ID), false);
                }
                break;
        }
    }

    private void addFirebaseOpenTokEvent(String requestId) {
        FirebaseEventUtil.getInstance().addFirebaseOpenTokEvent(requestId, new FirebaseCallbackListener<OpenTok>() {
            @Override
            public void onSuccess(OpenTok data) {
                if (data != null && data.getVideoCallStatus() != null && data.getVideoCallStatus().equals(Constants.STATUS_END)) {
                    FirebaseEventUtil.getInstance().removeFirebaseOpenTokEvent();
                    Intent intent = new Intent();
                    intent.setAction(OpenTokActivity.OPENTOK_END_CALL_RECEIVER);
                    sendBroadcast(intent);
                }
            }
        });
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

    public void openDashBoardFragmentWithAnimation() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
        if (currentFragment instanceof DashboardFragment) {
            return;
        }
        getSupportFragmentManager().executePendingTransactions();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
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

    private void openNewsFeedFragmentWithAnimation() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
        if (currentFragment instanceof NewsFeedFragment) {
            return;
        }
        getSupportFragmentManager().executePendingTransactions();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.main_container, NewsFeedFragment.newInstance())
                .addToBackStack(NewsFeedFragment.fragmentName)
                .commitAllowingStateLoss();
    }

    private void openAccountSettingFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
        if (currentFragment instanceof AccountSettingFragment) {
            return;
        }
        getSupportFragmentManager().executePendingTransactions();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.main_container, AccountSettingFragment.newInstance())
                .addToBackStack(AccountSettingFragment.fragmentName)
                .commitAllowingStateLoss();
    }

    private void openHowItWorksFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
        if (currentFragment instanceof HowItWorksFragment) {
            return;
        }
        getSupportFragmentManager().executePendingTransactions();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.main_container, HowItWorksFragment.newInstance())
                .addToBackStack(HowItWorksFragment.fragmentName)
                .commitAllowingStateLoss();
    }

    private void openIncidentReportsListFragment() {
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
            if (currentFragment instanceof IncidentReportsListFragment) {
                return;
            }
            getSupportFragmentManager().executePendingTransactions();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.main_container, IncidentReportsListFragment.newInstance())
                    .addToBackStack(IncidentReportsListFragment.fragmentName)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reOpenDashBoardFragment() {
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

    public void reOpenNewsFeedFragment() {
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
            if (currentFragment instanceof NewsFeedFragment) {
                return;
            }
            getSupportFragmentManager().popBackStack(NewsFeedFragment.fragmentName, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (isTutorialMode) {
            return;
        }
        CommonFunctions.getInstance().hideSoftKeyBoard(MainActivity.this);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                return;
            }
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
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
                case EmsAlertUnconsciousFragment.fragmentName:
                    unconsciousOnBackClick();
                    break;
                case EmsAlertCardiacCallFragment.fragmentName:
                    showCancelRequestAlert(((EmsAlertCardiacCallFragment) currentFragment).requestId);
                    break;
                case FeedbackFragment.fragmentName:
                    if (currentFragment instanceof FeedbackFragment) {
                        if (((FeedbackFragment) currentFragment).isFromIncidentReport) {
                            getSupportFragmentManager().popBackStack();
                        } /*else {
                            // do nothing
                        }*/
                    }
                    break;
                case TriageCallWaitingFragment.fragmentName:
                case TriageCallFeedbackWaitingFragment.fragmentName:
                    // not do any think
                    break;
                case ProviderReasonFragment.fragmentName:
                    reOpenDashBoardFragment();
                    break;
                default:
                    getSupportFragmentManager().popBackStack();
                    break;
            }
        }
    }

    public void unconsciousOnBackClick() {
        stopGpsService();
        getSupportFragmentManager().popBackStack();
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

    public void showCancelRequestAlert(final String requestId) {
        CommonFunctions.getInstance().showAlertDialog(MainActivity.this, R.string.cancel_request_message, R.string.yes, R.string.no, new DialogDismissCallBackListener<Boolean>() {
            @Override
            public void onClose(Boolean result) {
                if (result) {
                    cancelRequest(requestId);
                }
            }
        });
    }

    private void cancelRequest(String requestId) {
        if (CommonFunctions.getInstance().isOffline(MainActivity.this)) {
            Toast.makeText(MainActivity.this, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        if (requestId != null && !requestId.isEmpty()) {
            CommonFunctions.getInstance().loadProgressDialog(MainActivity.this);
            new ApiServices().cancelRequest(MainActivity.this, requestId, new RestClientResponse() {
                @Override
                public void onSuccess(Object response, int statusCode) {
                    CommonFunctions.getInstance().dismissProgressDialog();
                    getSupportFragmentManager().popBackStack(DashboardFragment.fragmentName, 0);
                }

                @Override
                public void onFailure(String errorMessage, int statusCode) {
                    CommonFunctions.getInstance().dismissProgressDialog();
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void addFirebaseUserEvent() {
        FirebaseEventUtil.getInstance().addFirebaseUserEvent(MainActivity.this, new FirebaseCallbackListener<User>() {
            @Override
            public void onSuccess(final User data) {
                if (data != null) {
                    if (data.getPatientCurrentRequestId() != null && !data.getPatientCurrentRequestId().isEmpty()) {
                        FirebaseEventUtil.getInstance().getFirebaseRequest(data.getPatientCurrentRequestId(), new FirebaseCallbackListener<EmsRequest>() {
                            @Override
                            public void onSuccess(EmsRequest emsRequest) {
                                handledFirebaseRequestData(emsRequest, data.getDeviceId());
                            }
                        });
                    }
                }
            }
        });
    }

    private void handledFirebaseRequestData(EmsRequest data, String deviceId) {
        System.out.println("aa --------Firebase Request data=" + data);
        System.out.println("aa --------Firebase Request deviceId=" + deviceId);
        if (data != null) {
            if (data.getIsUnconscious()) {
                openEmsAlertCardiacCallFragment(false, data, data.getId());
            } else if (Constants.STATUS_HIGH.equals("" + data.getPriority())) {
                if (deviceId != null && !deviceId.isEmpty() && deviceId.equals(ApiServiceUtil.getInstance().getDeviceID(MainActivity.this))) {
                    if (Constants.STATUS_PENDING.equals("" + data.getTriageCallStatus())) {
                        openTriageCallWaitingFragment(data);
                    } else if (Constants.STATUS_ACCEPTED.equals("" + data.getTriageCallStatus())
                            && Constants.STATUS_START.equals("" + data.getVideoCallStatus())) {
                        startVideoCallWithPermissions(data);
                    } else if (Constants.STATUS_ACCEPTED.equals("" + data.getTriageCallStatus())
                            && !data.getIsPatientFeedbackSubmitted()) {
                        openFeedbackFragment(data.getId(), false);
                    } else if (Constants.STATUS_ACCEPTED.equals("" + data.getTriageCallStatus())
                            && data.getProviderFeedback() != null && !data.getProviderFeedback().isEmpty()) {
                        openProviderReasonFragment(data);
                    } else if (Constants.STATUS_ACCEPTED.equals("" + data.getTriageCallStatus())
                            && data.getIsPatientFeedbackSubmitted()) {
                        openTriageCallFeedbackWaitingFragment(data.getId());
                    }
                } else {
                    openTriageCallInProgressWaitingFragment(data);
                }
            }
        }
    }

    public void startVideoCallWithPermissions(EmsRequest request) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tempRequest = request;
                String[] perms = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
                requestPermissions(perms, PERMISSION_REQUEST_CODE);
            } else {
                startVideoCall(request);
            }
        } else {
            startVideoCall(request);
        }
    }

    private void startVideoCall(final EmsRequest request) {
        FirebaseEventUtil.getInstance().getFirebaseOpenTok(request.getId(), new FirebaseCallbackListener<OpenTok>() {
            @Override
            public void onSuccess(OpenTok data) {
                if (!isOpenTokActivityRunning() && !isOpenTokActivityOpen) {
                    isOpenTokActivityOpen = true;
                    Intent intent = new Intent(MainActivity.this, OpenTokActivity.class);
                    intent.putExtra(OpenTokActivity.OPENTOK_SESSION_ID, data.getSessionId());
                    intent.putExtra(OpenTokActivity.OPENTOK_PUBLISHER_TOKEN, data.getPatientToken());
                    intent.putExtra(OpenTokActivity.OPENTOK_REQUEST_ID, request.getId());
                    intent.putExtra(OpenTokActivity.OPENTOK_CALLER_NAME, request.getProviderName());
                    intent.putExtra(OpenTokActivity.OPENTOK_CALLER_SUB_TEXT, request.getProviderProfession());
                    intent.putExtra(OpenTokActivity.OPENTOK_CALL_START_TIME, data.getStartTime());
                    startActivityForResult(intent, OpenTokActivity.RC_OPENTOK_ACTIVITY);
                    addFirebaseOpenTokEvent(request.getId());
                }
            }
        });
    }

    private boolean isOpenTokActivityRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(Integer.MAX_VALUE);
            for (int i = 0; i < tasksInfo.size(); i++) {
                if (tasksInfo.get(i).baseActivity.getClassName().contains("com.biz4solutions.activities.OpenTokActivity"))
                    return true;
            }
        }
        return false;
    }

    public void openEmsAlertUnconsciousFragment() {
        if (!isTutorialMode) {
            startGpsService();
        }
        getSupportFragmentManager().executePendingTransactions();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.main_container, EmsAlertUnconsciousFragment.newInstance())
                .addToBackStack(EmsAlertUnconsciousFragment.fragmentName)
                .commitAllowingStateLoss();
    }

    public void openEmsAlertCardiacCallFragment(boolean isNeedToShowQue, String requestId) {
        openEmsAlertCardiacCallFragment(isNeedToShowQue, null, requestId);
    }

    public void openEmsAlertCardiacCallFragment(boolean isNeedToShowQue, EmsRequest data, String requestId) {
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
            if (currentFragment instanceof EmsAlertCardiacCallFragment) {
                return;
            }
            getSupportFragmentManager().executePendingTransactions();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.main_container, EmsAlertCardiacCallFragment.newInstance(isNeedToShowQue, data, requestId))
                    .addToBackStack(EmsAlertCardiacCallFragment.fragmentName)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openTriageCallWaitingFragment(EmsRequest data) {
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
            if (currentFragment instanceof TriageCallWaitingFragment) {
                return;
            }
            getSupportFragmentManager().executePendingTransactions();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.main_container, TriageCallWaitingFragment.newInstance(data))
                    .addToBackStack(TriageCallWaitingFragment.fragmentName)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openTriageCallInProgressWaitingFragment(EmsRequest data) {
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
            if (currentFragment instanceof TriageCallInProgressWaitingFragment) {
                return;
            }
            getSupportFragmentManager().executePendingTransactions();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.main_container, TriageCallInProgressWaitingFragment.newInstance(data))
                    .addToBackStack(TriageCallInProgressWaitingFragment.fragmentName)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openFeedbackFragment(String requestId, boolean isFromIncidentReport) {
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
            if (currentFragment instanceof FeedbackFragment) {
                return;
            }
            getSupportFragmentManager().executePendingTransactions();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.main_container, FeedbackFragment.newInstance(requestId, isFromIncidentReport))
                    .addToBackStack(FeedbackFragment.fragmentName)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openTriageCallFeedbackWaitingFragment(String requestId) {
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
            if (currentFragment instanceof TriageCallFeedbackWaitingFragment) {
                return;
            }
            getSupportFragmentManager().executePendingTransactions();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.main_container, TriageCallFeedbackWaitingFragment.newInstance(requestId))
                    .addToBackStack(TriageCallFeedbackWaitingFragment.fragmentName)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openProviderReasonFragment(EmsRequest request) {
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
            if (currentFragment instanceof ProviderReasonFragment) {
                return;
            }
            getSupportFragmentManager().executePendingTransactions();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.main_container, ProviderReasonFragment.newInstance(request))
                    .addToBackStack(ProviderReasonFragment.fragmentName)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (isTutorialMode) {
            return;
        }
        switch (view.getId()) {
            case R.id.btn_log_out:
                showLogOutAlertDialog();
                break;
            case R.id.btn_call_911:
                Toast.makeText(MainActivity.this, R.string.coming_soon, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void reOpenHowItWorksFragment() {
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
            if (currentFragment instanceof HowItWorksFragment) {
                return;
            }
            Toast.makeText(MainActivity.this, R.string.tutorial_end_message, Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().popBackStack(HowItWorksFragment.fragmentName, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}