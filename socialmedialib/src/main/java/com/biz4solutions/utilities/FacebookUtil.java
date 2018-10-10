package com.biz4solutions.utilities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.biz4solutions.interfaces.CallbackListener;
import com.biz4solutions.models.SocialMediaUserData;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by ketan on 12/4/2017.
 */
public class FacebookUtil {

    private static final int FACEBOOK_REQUEST_CODE = 64206;
    private static FacebookUtil instance;
    private static CallbackManager callbackManager;

    private FacebookUtil() {
    }

    public static FacebookUtil getInstance() {
        if (instance == null) {
            instance = new FacebookUtil();
        }
        return instance;
    }

    public void registerCallback(final Context context, final CallbackListener<SocialMediaUserData> facebookCallback) {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        System.out.println("loginResult = " + loginResult);
                        final String accessToken = loginResult.getAccessToken().getToken();
                        final GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                // Get facebook data from login
                                //System.out.println("------------response: " + response);
                                try {
                                    String emailId = response.getJSONObject().has("email") ? response.getJSONObject().get("email").toString() : null;
                                    if (emailId == null) {
                                        Toast.makeText(context, "Unable to fetch email from Facebook. Your facebook account doesn't allow to fetch email id.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    String firstName = response.getJSONObject().has("first_name") ? response.getJSONObject().get("first_name").toString() : "";
                                    String lastName = response.getJSONObject().has("last_name") ? response.getJSONObject().get("last_name").toString() : "";
                                    //String gender = response.getJSONObject().get("gender").toString();
                                    String facebookId = response.getJSONObject().has("id") ? response.getJSONObject().get("id").toString() : "";
                                    SocialMediaUserData userData = new SocialMediaUserData();
                                    userData.setEmail(emailId);
                                    userData.setFirstName(firstName);
                                    userData.setLastName(lastName);
                                    userData.setSocialLoginId(facebookId);
                                    userData.setSocialLoginToken(accessToken);
                                    userData.setSignupType(Constants.SIGN_UP_TYPE_FB);
                                    facebookCallback.onSuccess(userData);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,first_name,last_name,email,gender");
                        request.setParameters(parameters);
                        request.executeAsync();
                        // App code
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        //System.out.println("------------onCancel");
                        Toast.makeText(context, "Unable to login with Facebook.Please try again later.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Toast.makeText(context, "Unable to login with Facebook.Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void doLogin(FragmentActivity fragment) {
        List<String> permissions = new ArrayList<>();
        permissions.add("public_profile");
        permissions.add("email");
        LoginManager.getInstance().logInWithReadPermissions(fragment, permissions);
    }

    public void doLogout() {
        try {
            LoginManager.getInstance().logOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getRequestCode() {
        return FACEBOOK_REQUEST_CODE;
    }
}
