package com.biz4solutions.provider.main.views.activities;

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

import com.biz4solutions.activities.LoginActivity;
import com.biz4solutions.activities.OpenTokActivity;
import com.biz4solutions.apiservices.ApiServiceUtil;
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.interfaces.DialogDismissCallBackListener;
import com.biz4solutions.interfaces.FirebaseCallbackListener;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.loginlib.BuildConfig;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.models.OpenTok;
import com.biz4solutions.models.User;
import com.biz4solutions.models.response.EmsRequestDetailsResponse;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.provider.R;
import com.biz4solutions.provider.cardiac.views.fragments.CardiacCallDetailsFragment;
import com.biz4solutions.provider.cardiac.views.fragments.CardiacIncidentReportFragment;
import com.biz4solutions.provider.databinding.ActivityMainBinding;
import com.biz4solutions.provider.main.views.fragments.DashboardFragment;
import com.biz4solutions.provider.main.views.fragments.NewsFeedFragment;
import com.biz4solutions.provider.reports.view.fragments.IncidentReportsListFragment;
import com.biz4solutions.provider.services.FirebaseMessagingService;
import com.biz4solutions.provider.services.GpsServices;
import com.biz4solutions.provider.triage.views.fragments.FeedbackFragment;
import com.biz4solutions.provider.triage.views.fragments.TriageCallDetailsFragment;
import com.biz4solutions.provider.triage.views.fragments.TriageCallerFeedbackFragment;
import com.biz4solutions.provider.triage.views.fragments.TriageIncidentReportFragment;
import com.biz4solutions.provider.utilities.ExceptionHandler;
import com.biz4solutions.provider.utilities.FirebaseEventUtil;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;
import com.biz4solutions.utilities.FacebookUtil;
import com.biz4solutions.utilities.FirebaseAuthUtil;
import com.biz4solutions.utilities.GoogleUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    public NavigationView navigationView;
    public TextView toolbarTitle;
    public LinearLayout btnCallAlerter;
    private boolean doubleBackToExitPressedOnce;
    public ActionBarDrawerToggle toggle;
    private BroadcastReceiver broadcastReceiver;
    public boolean isSuccessfullyInitFirebase = false;
    public boolean isUpdateList = false;
    public DrawerLayout drawerLayout;
    public static boolean isActivityOpen = false;
    private boolean isOpenTokActivityOpen = false;
    private static final int PERMISSION_REQUEST_CODE = 124;
    private EmsRequest tempRequest;

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

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //method call here
                String action = intent.getAction();
                if (action != null) {
                    switch (action) {
                        case Constants.LOGOUT_RECEIVER:
                            unauthorizedLogOut();
                            break;
                        case Constants.LOCAL_NOTIFICATION_ACTION_VIEW:
                            openNotificationDetailsView(intent);
                            break;
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.LOGOUT_RECEIVER);
        intentFilter.addAction(Constants.LOCAL_NOTIFICATION_ACTION_VIEW);
        registerReceiver(broadcastReceiver, intentFilter);

        openNotificationDetailsView(getIntent());
    }

    private void unauthorizedLogOut() {
        Toast.makeText(MainActivity.this, R.string.error_session_expired, Toast.LENGTH_SHORT).show();
        doLogOut();
    }

    private void openNotificationDetailsView(Intent intent) {
        String requestId = intent.getStringExtra(Constants.NOTIFICATION_REQUEST_ID_KEY);
        if (requestId != null && !requestId.isEmpty()) {
            getRequestDetails(requestId, "", true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        FirebaseEventUtil.getInstance().removeFirebaseOpenTokEvent();
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
                        if (data.getDeviceId() != null && !data.getDeviceId().isEmpty() && !data.getDeviceId().equals(ApiServiceUtil.getInstance().getDeviceID(MainActivity.this))) {
                            Intent intent = new Intent();
                            intent.setAction(OpenTokActivity.OPENTOK_SESSION_EXPIRED_RECEIVER);
                            sendBroadcast(intent);
                        } else {
                            getRequestDetails(data.getProviderCurrentRequestId(), "", true);
                        }
                    }
                }
            }
        });
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
                                    callLogoutAPI();
                                }
                            }
                        });
                        break;
                    case R.id.nav_log_in:
                        doLogOut();
                        break;
                    case R.id.nav_incidents_reports:
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
        switch (requestCode) {
            case 149:
                initView();
                break;
            case OpenTokActivity.RC_OPENTOK_ACTIVITY:
                FirebaseEventUtil.getInstance().removeFirebaseOpenTokEvent();
                isOpenTokActivityOpen = false;
                if (resultCode == RESULT_OK) {
                    openTriageCallerFeedbackFragment(data.getStringExtra(OpenTokActivity.OPENTOK_REQUEST_ID));
                } else if (resultCode == RESULT_CANCELED) {
                    unauthorizedLogOut();
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

    private void openCardiacCallDetailsFragment(EmsRequest data, String distanceStr, boolean isOpenDuplicateFragment) {
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
            if (!isOpenDuplicateFragment) {
                if (currentFragment instanceof CardiacCallDetailsFragment) {
                    return;
                }
            } else {
                if (currentFragment instanceof CardiacCallDetailsFragment) {
                    if (((CardiacCallDetailsFragment) currentFragment).currentRequestId != null
                            && ((CardiacCallDetailsFragment) currentFragment).currentRequestId.equals("" + data.getId())) {
                        return;
                    }
                }
            }
            getSupportFragmentManager().executePendingTransactions();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.main_container, CardiacCallDetailsFragment.newInstance(data, distanceStr))
                    .addToBackStack(CardiacCallDetailsFragment.fragmentName)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openTriageCallDetailsFragment(EmsRequest data, String distanceStr, boolean isOpenDuplicateFragment) {
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
            if (!isOpenDuplicateFragment) {
                if (currentFragment instanceof TriageCallDetailsFragment) {
                    return;
                }
            } else {
                if (currentFragment instanceof TriageCallDetailsFragment) {
                    if (((TriageCallDetailsFragment) currentFragment).currentRequestId != null
                            && ((TriageCallDetailsFragment) currentFragment).currentRequestId.equals("" + data.getId())) {
                        return;
                    }
                }
            }
            getSupportFragmentManager().executePendingTransactions();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.main_container, TriageCallDetailsFragment.newInstance(data, distanceStr))
                    .addToBackStack(TriageCallDetailsFragment.fragmentName)
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

    public void reOpenDashBoardFragment() {
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
            if (currentFragment instanceof DashboardFragment) {
                return;
            }
            isUpdateList = true;
            getSupportFragmentManager().popBackStack(DashboardFragment.fragmentName, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openCardiacIncidentReportFragment(EmsRequest requestDetails) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
        if (currentFragment instanceof CardiacIncidentReportFragment) {
            return;
        }
        getSupportFragmentManager().executePendingTransactions();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.main_container, CardiacIncidentReportFragment.newInstance(requestDetails))
                .addToBackStack(CardiacIncidentReportFragment.fragmentName)
                .commitAllowingStateLoss();
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
                case CardiacIncidentReportFragment.fragmentName:
                case TriageIncidentReportFragment.fragmentName:
                    reOpenDashBoardFragment();
                    break;
                case FeedbackFragment.fragmentName:
                case TriageCallerFeedbackFragment.fragmentName:
                    // do nothing
                    break;
                default:
                    getSupportFragmentManager().popBackStack();
                    break;
            }
        }
    }

    public void showRejectRequestAlert() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
        if (currentFragment instanceof CardiacCallDetailsFragment) {
            if (!((CardiacCallDetailsFragment) currentFragment).isRequestAcceptedByMe) {
                getSupportFragmentManager().popBackStack();
            } /*else {
                // do nothing
            }*/
        }
    }

    public void getRequestDetails(String requestId, final String distanceStr, final boolean isOpenDuplicateFragment) {
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
                    if (response != null) {
                        handledRequestData(((EmsRequestDetailsResponse) response).getData(), distanceStr, isOpenDuplicateFragment);
                    }
                }

                @Override
                public void onFailure(String errorMessage, int statusCode) {
                    CommonFunctions.getInstance().dismissProgressDialog();
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void handledRequestData(final EmsRequest emsRequest, final String distanceStr, final boolean isOpenDuplicateFragment) {
        if (Constants.STATUS_IMMEDIATE.equals("" + emsRequest.getPriority())) {
            openCardiacCallDetailsFragment(emsRequest, distanceStr, isOpenDuplicateFragment);
        } else if (Constants.STATUS_HIGH.equals("" + emsRequest.getPriority())) {
            FirebaseEventUtil.getInstance().getFirebaseRequest(emsRequest.getId(), new FirebaseCallbackListener<EmsRequest>() {
                @Override
                public void onSuccess(EmsRequest data) {
                    if (Constants.STATUS_PENDING.equals("" + data.getTriageCallStatus())) {
                        openTriageCallDetailsFragment(emsRequest, distanceStr, isOpenDuplicateFragment);
                    } else if (Constants.STATUS_ACCEPTED.equals("" + data.getTriageCallStatus())
                            && Constants.STATUS_START.equals("" + data.getVideoCallStatus())) {
                        startVideoCallWithPermissions(data);
                    } else if (Constants.STATUS_ACCEPTED.equals("" + data.getTriageCallStatus())
                            && (data.getProviderFeedback() == null || data.getProviderFeedback().isEmpty())) {
                        openTriageCallerFeedbackFragment(data.getId());
                    }
                }
            });
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
                try {
                    CommonFunctions.getInstance().dismissProgressDialog();
                    if (!isOpenTokActivityRunning() && !isOpenTokActivityOpen) {
                        isOpenTokActivityOpen = true;
                        Intent intent = new Intent(MainActivity.this, OpenTokActivity.class);
                        intent.putExtra(OpenTokActivity.OPENTOK_SESSION_ID, data.getSessionId());
                        intent.putExtra(OpenTokActivity.OPENTOK_PUBLISHER_TOKEN, data.getProviderToken());
                        intent.putExtra(OpenTokActivity.OPENTOK_REQUEST_ID, request.getId());
                        intent.putExtra(OpenTokActivity.OPENTOK_CALLER_NAME, request.getPatientName());
                        intent.putExtra(OpenTokActivity.OPENTOK_CALLER_SUB_TEXT, request.getPatientSymptoms());
                        intent.putExtra(OpenTokActivity.OPENTOK_CALL_START_TIME, data.getStartTime());
                        startActivityForResult(intent, OpenTokActivity.RC_OPENTOK_ACTIVITY);
                        addFirebaseOpenTokEvent(request.getId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

    private void openTriageCallerFeedbackFragment(String requestId) {
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
            if (currentFragment instanceof TriageCallerFeedbackFragment) {
                return;
            }
            getSupportFragmentManager().executePendingTransactions();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.main_container, TriageCallerFeedbackFragment.newInstance(requestId))
                    .addToBackStack(TriageCallerFeedbackFragment.fragmentName)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}