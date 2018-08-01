package com.biz4solutions.apiservices;

import android.content.Context;

import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.LoginRequest;
import com.biz4solutions.models.SignUpRequest;

import java.util.HashMap;

import osiris.com.socialmedialib.models.SocialMediaUserData;

/*
 * Created by Ketan on 12/1/2017.
 */
public class ApiServices {

    public void doLogin(final Context context, LoginRequest loginRequest, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .doLogin(loginRequest));
    }

    public void socialAppLogin(final Context context, SocialMediaUserData socialSignInDTO, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .socialAppLogin(socialSignInDTO));
    }

    public void signUp(final Context context, SignUpRequest signUpRequest, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .signUp(signUpRequest));
    }

    public void requestOtp(final Context context, HashMap<String, String> body, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .requestOtp(body));
    }

    public void resendOtp(final Context context, HashMap<String, String> body, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .resendOtp(body));
    }

    public void verifyOtp(final Context context, HashMap<String, Object> body, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .verifyOtp(body));
    }

    public void resetPassword(final Context context, HashMap<String, Object> body, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .resetPassword(body));
    }

    public void setFcmToken(final Context context, HashMap<String, Object> body) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, null, ApiServiceUtil.getInstance().getRestClient(context)
                .setFcmToken(body));
    }

}
