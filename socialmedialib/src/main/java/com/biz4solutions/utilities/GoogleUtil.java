package com.biz4solutions.utilities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.biz4solutions.interfaces.CallbackListener;
import com.biz4solutions.models.SocialMediaUserData;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

/*
 * Created by ketan on 12/4/2017.
 */
public class GoogleUtil implements GoogleApiClient.OnConnectionFailedListener {

    private static GoogleUtil instance;
    private static GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 1062;
    private CallbackListener<SocialMediaUserData> googleCallback;

    private GoogleUtil() {
    }

    public static GoogleUtil getInstance() {
        if (instance == null) {
            instance = new GoogleUtil();
        }
        return instance;
    }

    public void initGoogleConfig(Activity activity, String googleAuthClientId) {
        if (mGoogleApiClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(googleAuthClientId)
                    .requestEmail()
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .enableAutoManage((FragmentActivity) activity, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        googleCallback.onFailure(connectionResult.getErrorMessage());
    }

    public void signIn(Activity activity, CallbackListener<SocialMediaUserData> googleSignInCallback) {
        this.googleCallback = googleSignInCallback;
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void doLogout() {
        try {
            mGoogleApiClient.connect();
            mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {

                    if (mGoogleApiClient.isConnected()) {
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    }
                }

                @Override
                public void onConnectionSuspended(int i) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, Intent data) {
        // Pass the activity result to the login button.
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                SocialMediaUserData userData = new SocialMediaUserData();
                userData.setSignupType("GP");
                if (acct.getEmail() != null) {
                    userData.setEmail(acct.getEmail());
                }
                if (acct.getDisplayName() != null) {
                    userData.setFirstName(acct.getDisplayName());
                }
                if (acct.getId() != null) {
                    userData.setUserId(acct.getId());
                }
                if (acct.getIdToken() != null) {
                    userData.setToken(acct.getIdToken());
                }
                googleCallback.onSuccess(userData);
            } else {
                googleCallback.onFailure("Unable to login with Google. Please try again later.");
            }
        }
    }

    public int getRequestCode() {
        return RC_SIGN_IN;
    }
}
