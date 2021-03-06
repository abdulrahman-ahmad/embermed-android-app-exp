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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
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
import com.biz4solutions.models.ProviderRegistration;
import com.biz4solutions.models.Subscription;
import com.biz4solutions.models.User;
import com.biz4solutions.models.request.FeedbackRequest;
import com.biz4solutions.models.response.EmsRequestDetailsResponse;
import com.biz4solutions.models.response.UrgentCaresDataResponse;
import com.biz4solutions.models.response.UrgentCaresResponse;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.provider.R;
import com.biz4solutions.provider.account.fragments.AccountSettingFragment;
import com.biz4solutions.provider.aedmaps.views.fragments.AedMapFragment;
import com.biz4solutions.provider.cardiac.views.fragments.CardiacCallDetailsFragment;
import com.biz4solutions.provider.cardiac.views.fragments.CardiacIncidentReportFragment;
import com.biz4solutions.provider.databinding.ActivityMainBinding;
import com.biz4solutions.provider.main.views.fragments.DashboardFragment;
import com.biz4solutions.provider.main.views.fragments.FeedbackFragment;
import com.biz4solutions.provider.newsfeed.views.fragments.NewsFeedFragment;
import com.biz4solutions.provider.registration.views.fragments.RegistrationFragment;
import com.biz4solutions.provider.registration.views.fragments.ViewRegistrationDetailsFragment;
import com.biz4solutions.provider.reports.view.fragments.IncidentReportsListFragment;
import com.biz4solutions.provider.services.FirebaseMessagingService;
import com.biz4solutions.provider.services.GpsServices;
import com.biz4solutions.provider.triage.views.fragments.TriageCallDetailsFragment;
import com.biz4solutions.provider.triage.views.fragments.TriageCallerFeedbackFragment;
import com.biz4solutions.provider.triage.views.fragments.TriageIncidentReportFragment;
import com.biz4solutions.provider.tutorial.views.fragments.HowItWorksFragment;
import com.biz4solutions.provider.utilities.ExceptionHandler;
import com.biz4solutions.provider.utilities.FileUtils;
import com.biz4solutions.provider.utilities.FirebaseEventUtil;
import com.biz4solutions.provider.utilities.GpsServicesUtil;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;
import com.biz4solutions.utilities.FacebookUtil;
import com.biz4solutions.utilities.FirebaseAuthUtil;
import com.biz4solutions.utilities.GoogleUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public NavigationView navigationView;
    public TextView toolbarTitle;
    public LinearLayout btnCallAlerter;
    private boolean doubleBackToExitPressedOnce;
    public ActionBarDrawerToggle toggle;
    private BroadcastReceiver broadcastReceiver;
    public boolean isSuccessfullyInitFirebase = false;
    public boolean isUpdateList = false;
    public boolean isUpdateIncidentReportList = false;
    public DrawerLayout drawerLayout;
    public static boolean isActivityOpen = false;
    private boolean isOpenTokActivityOpen = false;
    private static final int PERMISSION_REQUEST_CODE = 121254;
    private static final int PERMISSION_REQUEST_CODE_FOR_AED_LIST = 10122;
    private EmsRequest tempRequest;
    public FeedbackRequest feedbackRequest;
    private boolean isAedApiInProgress = false;
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

        initView(false);

        if (binding.appBarMain != null) {
            toolbarTitle = binding.appBarMain.toolbarTitle;
            btnCallAlerter = binding.appBarMain.btnCallAlerter;
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //method call here
                String action = intent.getAction();
                if (action != null) {
                    switch (action) {
                        case Constants.LOGOUT_RECEIVER:
                            unauthorizedLogOut(intent.getStringExtra(Constants.LOGOUT_MESSAGE));
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

    private void unauthorizedLogOut(String message) {
        if (message != null && !message.isEmpty()) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, R.string.error_session_expired, Toast.LENGTH_SHORT).show();
        }
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

    private void initView(boolean isOnlyUpdateMenu) {
        String userAuthKey = SharedPrefsManager.getInstance().retrieveStringPreference(this, Constants.USER_PREFERENCE, Constants.USER_AUTH_KEY);
        User user = SharedPrefsManager.getInstance().retrieveUserPreference(this, Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY);
        boolean isProviderSubscribed = false;
        boolean isApproved = false;
        if (user != null) {
            isProviderSubscribed = user.getIsProviderSubscribed() != null && user.getIsProviderSubscribed();
            isApproved = user.getIsApproved() != null && user.getIsApproved();
        }
        if (userAuthKey == null || userAuthKey.isEmpty()) {
            navigationView.getMenu().findItem(R.id.nav_dashboard).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_log_out).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_log_in).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_account_settings).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_incident_reports).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_medical_profile).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_aed_maps).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_contact_us).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_registration).setVisible(false);
            if (!isOnlyUpdateMenu) {
                openNewsFeedFragment();
            }
        } else {
            navigationView.getMenu().findItem(R.id.nav_log_out).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_log_in).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_account_settings).setVisible(true);
            if (isProviderSubscribed) {
                navigationView.getMenu().findItem(R.id.nav_registration).setVisible(false);
                if (isApproved) {
                    navigationView.getMenu().findItem(R.id.nav_dashboard).setVisible(true);
                    navigationView.getMenu().findItem(R.id.nav_incident_reports).setVisible(true);
                    navigationView.getMenu().findItem(R.id.nav_aed_maps).setVisible(true);
                    //need to re-add when functionality
                    navigationView.getMenu().findItem(R.id.nav_medical_profile).setVisible(false);
                    navigationView.getMenu().findItem(R.id.nav_contact_us).setVisible(false);
                    if (!isOnlyUpdateMenu) {
                        openDashBoardFragment();
                    }
                } else {
                    navigationView.getMenu().findItem(R.id.nav_dashboard).setVisible(false);
                    navigationView.getMenu().findItem(R.id.nav_incident_reports).setVisible(false);
                    navigationView.getMenu().findItem(R.id.nav_medical_profile).setVisible(false);
                    navigationView.getMenu().findItem(R.id.nav_aed_maps).setVisible(false);
                    navigationView.getMenu().findItem(R.id.nav_contact_us).setVisible(false);
                    if (!isOnlyUpdateMenu) {
                        openNewsFeedFragment();
                    }
                }
            } else {
                navigationView.getMenu().findItem(R.id.nav_dashboard).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_incident_reports).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_medical_profile).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_aed_maps).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_contact_us).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_registration).setVisible(true);
                if (!isOnlyUpdateMenu) {
                    openNewsFeedFragment();
                }
            }

            if (!isOnlyUpdateMenu) {
                FirebaseMessagingService.setFcmToken(MainActivity.this);
                FirebaseCallbackListener<Boolean> callbackListener = new FirebaseCallbackListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {
                        isSuccessfullyInitFirebase = true;
                        if (data != null && data) {
                            addFirebaseUserEvent();
                            addFirebaseSubscriptionEvent();
                        }
                    }
                };
                if (FirebaseAuthUtil.getInstance().isFirebaseAuthValid()) {
                    FirebaseAuthUtil.getInstance().initDB(callbackListener);
                } else {
                    if (user != null) {
                        FirebaseAuthUtil.getInstance().signInUser(user.getEmail(), BuildConfig.FIREBASE_PASSWORD, callbackListener);
                    }
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

    public void addFirebaseSubscriptionEvent() {
        FirebaseEventUtil.getInstance().addFirebaseSubscriptionEvent(MainActivity.this, new FirebaseCallbackListener<Subscription>() {
            @Override
            public void onSuccess(Subscription data) {
                if (data != null) {
                    User user = SharedPrefsManager.getInstance().retrieveUserPreference(MainActivity.this, Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY);
                    if (user != null) {
                        if (user.getIsProviderSubscribed() == null) {
                            user.setIsProviderSubscribed(data.getIsProviderSubscribed());
                        }
                        if (user.getIsApproved() == null) {
                            user.setIsApproved(data.getIsApproved());
                        }
                        SharedPrefsManager.getInstance().storeUserPreference(MainActivity.this, Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY, user);
                    }
                    initView(true);
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
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
            if (currentFragment instanceof NewsFeedFragment) {
                currentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                return;
            }
            boolean userAllowedAllRequestPermissions = true;
            if (grantResults.length == 0) {
                userAllowedAllRequestPermissions = false;
            }
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
                    case PERMISSION_REQUEST_CODE_FOR_AED_LIST:
                        getAedList();
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
                    case R.id.nav_aed_maps:
                        getAedList();
                        break;
                    case R.id.nav_registration:
                        openRegistrationFragment();
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
                    case R.id.nav_incident_reports:
                        openIncidentReportsListFragment();
                        break;
                    case R.id.nav_how_it_works:
                        openHowItWorksFragment();
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
        FileUtils.deleteStoredFiles();
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
        FirebaseEventUtil.getInstance().removeFirebaseSubscriptionEvent();
        FirebaseEventUtil.getInstance().removeFirebaseAlertEvent();
        //FirebaseAuthUtil.getInstance().signOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 149:
                initView(false);
                break;
            case OpenTokActivity.RC_OPENTOK_ACTIVITY:
                FirebaseEventUtil.getInstance().removeFirebaseOpenTokEvent();
                isOpenTokActivityOpen = false;
                if (resultCode == RESULT_OK) {
                    openTriageCallerFeedbackFragment(data.getStringExtra(OpenTokActivity.OPENTOK_REQUEST_ID));
                } else if (resultCode == RESULT_CANCELED) {
                    unauthorizedLogOut(null);
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

    private void openRegistrationFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
        if (currentFragment instanceof RegistrationFragment) {
            return;
        }
        getSupportFragmentManager().executePendingTransactions();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, RegistrationFragment.newInstance())
                .addToBackStack(RegistrationFragment.fragmentName)
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

    public void openFeedbackFragment(String requestId, boolean isFromIncidentReport) {
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
    }

    public void reOpenDashBoardFragment() {
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
            if (currentFragment instanceof DashboardFragment) {
                return;
            }
            isUpdateList = true;

            boolean isDashBoardFound = false;
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                    try {
                        String fragmentName = getSupportFragmentManager().getBackStackEntryAt(i).getName();
                        if (fragmentName.equals(DashboardFragment.fragmentName)) {
                            isDashBoardFound = true;
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (isDashBoardFound) {
                getSupportFragmentManager().popBackStack(DashboardFragment.fragmentName, 0);
            } else {
                openDashBoardFragment();
            }
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
            boolean isNewsFeedFound = false;
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                    try {
                        String fragmentName = getSupportFragmentManager().getBackStackEntryAt(i).getName();
                        if (fragmentName.equals(NewsFeedFragment.fragmentName)) {
                            isNewsFeedFound = true;
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (isNewsFeedFound) {
                getSupportFragmentManager().popBackStack(NewsFeedFragment.fragmentName, 0);
            } else {
                openNewsFeedFragment();
            }
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

    public void openViewRegistrationFragment(ProviderRegistration data) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
        if (currentFragment instanceof ViewRegistrationDetailsFragment) {
            return;
        }
        getSupportFragmentManager().executePendingTransactions();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.main_container, ViewRegistrationDetailsFragment.newInstance(data))
                .addToBackStack(ViewRegistrationDetailsFragment.fragmentName)
                .commitAllowingStateLoss();
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
            //String prevFragmentName = "";
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                return;
            }
            String fragmentName = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
            switch (fragmentName) {
                case NewsFeedFragment.fragmentName:
                    String userAuthKey = SharedPrefsManager.getInstance().retrieveStringPreference(this, Constants.USER_PREFERENCE, Constants.USER_AUTH_KEY);
                    User user = SharedPrefsManager.getInstance().retrieveUserPreference(this, Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY);
                    boolean isProviderSubscribed = false;
                    boolean isApproved = false;
                    if (user != null) {
                        isProviderSubscribed = user.getIsProviderSubscribed() != null && user.getIsProviderSubscribed();
                        isApproved = user.getIsApproved() != null && user.getIsApproved();
                    }
                    if (userAuthKey != null && !userAuthKey.isEmpty() && isProviderSubscribed && isApproved) {
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
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
                    if (currentFragment instanceof FeedbackFragment) {
                        if (((FeedbackFragment) currentFragment).isFromIncidentReport) {
                            getSupportFragmentManager().popBackStack();
                        } /*else {
                            // do nothing
                        }*/
                    }
                    break;
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

    public void handledRequestDataForTutorial(final EmsRequest emsRequest, final String distanceStr, final boolean isOpenDuplicateFragment) {
        if (Constants.STATUS_IMMEDIATE.equals("" + emsRequest.getPriority())) {
            openCardiacCallDetailsFragment(emsRequest, distanceStr, isOpenDuplicateFragment);
        } else if (Constants.STATUS_HIGH.equals("" + emsRequest.getPriority())) {
            openTriageCallDetailsFragment(emsRequest, distanceStr, isOpenDuplicateFragment);
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

    private boolean isLocationPermissionGranted() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                requestPermissions(perms, PERMISSION_REQUEST_CODE_FOR_AED_LIST);
            } else {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

    private void getAedList() {
        if (!isLocationPermissionGranted() || !CommonFunctions.getInstance().isGPSEnabled(this)) {
            return;
        }

        if (CommonFunctions.getInstance().isOffline(this)) {
            Toast.makeText(this, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(this);
        GpsServicesUtil.getInstance().onLocationCallbackListener(new GpsServicesUtil.LocationCallbackListener() {
            @Override
            public void onSuccess(double latitude, double longitude) {
                if (isAedApiInProgress) {
                    return;
                }
                if (CommonFunctions.getInstance().isOffline(MainActivity.this)) {
                    CommonFunctions.getInstance().dismissProgressDialog();
                    Toast.makeText(MainActivity.this, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
                    return;
                }
                isAedApiInProgress = true;
                new ApiServices().getAedList(MainActivity.this, latitude, longitude, new RestClientResponse() {
                    @Override
                    public void onSuccess(Object response, int statusCode) {
                        isAedApiInProgress = false;
                        CommonFunctions.getInstance().dismissProgressDialog();
                        try {
                            UrgentCaresResponse urgentCaresResponse = (UrgentCaresResponse) response;
                            Toast.makeText(MainActivity.this, urgentCaresResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            openAedMapFragment(urgentCaresResponse.getData());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage, int statusCode) {
                        isAedApiInProgress = false;
                        CommonFunctions.getInstance().dismissProgressDialog();
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError() {
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(MainActivity.this, R.string.error_location_fetch, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openAedMapFragment(UrgentCaresDataResponse response) {
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
            if (currentFragment instanceof AedMapFragment) {
                return;
            }
            getSupportFragmentManager().executePendingTransactions();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.main_container, AedMapFragment.newInstance(response))
                    .addToBackStack(AedMapFragment.fragmentName)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openHowItWorksFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
        if (currentFragment instanceof HowItWorksFragment) {
            return;
        }
        getSupportFragmentManager().executePendingTransactions();
        getSupportFragmentManager().beginTransaction()
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