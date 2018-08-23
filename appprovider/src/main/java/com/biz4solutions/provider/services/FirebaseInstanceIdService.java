package com.biz4solutions.provider.services;

import android.content.Context;

import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

/**
 * firebase token refresh service for retrieving the FCM device token for registration to the server
 * Created by ketan
 */

public class FirebaseInstanceIdService extends com.google.firebase.iid.FirebaseInstanceIdService {

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("aa --------- FirebaseInstanceIdService ------ onCreate");
    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        setFcmToken(FirebaseInstanceIdService.this);
    }

    private static void sendRegistrationToServer(String token, Context context) {
        //System.out.println("aa --------- FirebaseInstanceIdService ------ token= " + token);
        if (CommonFunctions.getInstance().isOffline(context)) {
            return;
        }
        HashMap<String, Object> body = new HashMap<>();
        body.put("fcmToken", token);
        new ApiServices().setFcmToken(context, body);
    }

    public static void setFcmToken(Context context) {
        String token = FirebaseInstanceId.getInstance().getToken();
        String userAuthKey = SharedPrefsManager.getInstance().retrieveStringPreference(context, Constants.USER_PREFERENCE, Constants.USER_AUTH_KEY);
        if (token != null && userAuthKey != null && !userAuthKey.isEmpty()) {
            sendRegistrationToServer(token, context);
        }
    }

}
