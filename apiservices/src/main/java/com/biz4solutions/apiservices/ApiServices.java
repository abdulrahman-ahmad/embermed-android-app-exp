package com.biz4solutions.apiservices;

import android.content.Context;

import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.SocialMediaUserData;
import com.biz4solutions.models.request.CreateEmsRequest;
import com.biz4solutions.models.request.IncidentReport;
import com.biz4solutions.models.request.LoginRequest;
import com.biz4solutions.models.request.SignUpRequest;
import com.biz4solutions.utilities.Constants;

import java.util.HashMap;

/*
 * Created by Ketan on 12/1/2017.
 */
public class ApiServices {

    public void doLogin(final Context context, LoginRequest loginRequest, final RestClientResponse restClientResponse) {
        if (loginRequest.getRoleName().equals(Constants.ROLE_NAME_USER)) {
            ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                    .doLogin(loginRequest));
        } else {
            ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                    .doProviderLogin(loginRequest));
        }
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

    public void createRequest(final Context context, CreateEmsRequest body, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .createRequest(body));
    }

    public void cancelRequest(final Context context, String query, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .cancelRequest(query));
    }

    public void getRequestList(final Context context, int page, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .getRequestList(page, 20));
    }

    public void acceptRequest(final Context context, String requestId, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .acceptRequest(requestId));
    }

    public void completeRequest(final Context context, String requestId, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .completeRequest(requestId));
    }

    public void submitIncidentReport(final Context context, IncidentReport body, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .submitIncidentReport(body));
    }

    public void rejectRequest(final Context context, String requestId, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .rejectRequest(requestId));
    }

    public void getRequestDetails(final Context context, String requestId, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .getRequestDetails(requestId));
    }

}