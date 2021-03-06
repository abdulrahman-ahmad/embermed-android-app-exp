package com.biz4solutions.apiservices;

import android.content.Context;

import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.Location;
import com.biz4solutions.models.ProviderRegistration;
import com.biz4solutions.models.SocialMediaUserData;
import com.biz4solutions.models.User;
import com.biz4solutions.models.request.CreateEmsRequest;
import com.biz4solutions.models.request.FeedbackRequest;
import com.biz4solutions.models.request.IncidentReport;
import com.biz4solutions.models.request.LoginRequest;
import com.biz4solutions.models.request.SignUpRequest;
import com.biz4solutions.models.request.UserDiseaseListRequest;
import com.biz4solutions.utilities.Constants;

import java.util.HashMap;
import java.util.List;

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

    public void completeRequest(final Context context, HashMap<String, Object> body, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .completeRequest(body));
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

    public void getDirections(final Context context, double originLatitude, double originLongitude, double destLatitude, double destLongitude, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(Constants.GOOGLE_MAP_URL)
                .getDirections("metric", originLatitude + "," + originLongitude, destLatitude + "," + destLongitude, "driving", false, BuildConfig.GOOGLE_API_KEY));
    }

    public void getDistanceDuration(final Context context, String units, double originLatitude, double originLongitude, List<Location> locations, final RestClientResponse restClientResponse) {
        StringBuilder locatioStr = new StringBuilder();
        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i) != null) {
                if (i == 0) {
                    locatioStr = new StringBuilder(locations.get(i).getLatitude() + "," + locations.get(i).getLongitude());
                } else {
                    locatioStr.append("|").append(locations.get(i).getLatitude()).append(",").append(locations.get(i).getLongitude());
                }
            }
        }
        if (locatioStr.toString().isEmpty()) {
            return;
        }
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(Constants.GOOGLE_MAP_URL)
                .getDistanceDuration(units, originLatitude + "," + originLongitude, locatioStr.toString(), "driving", false, BuildConfig.GOOGLE_API_KEY));
    }

    public void getSymptomList(final Context context, int page, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .getSymptomList(page, 20));
    }

    public void logout(final Context context, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .logout());
    }

    public void endCall(final Context context, String requestId, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .endCall(requestId));
    }

    public void submitUserFeedBack(final Context context, FeedbackRequest feedbackRequest, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .submitUserFeedBack(feedbackRequest));
    }

    public void submitProviderFeedBack(final Context context, FeedbackRequest feedbackRequest, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .submitProviderFeedBack(feedbackRequest));
    }

    public void getUrgentCareList(final Context context, double latitude, double longitude, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .getUrgentCareList(0, 2000, latitude, longitude));
    }

    public void getAedList(final Context context, double latitude, double longitude, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .getAedList(0, 2000, latitude, longitude));
    }

    public void getUserCompletedRequestList(final Context context, int page, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .getUserCompletedRequestList(page, 20));
    }

    public void getProviderCompletedRequestList(final Context context, int page, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .getProviderCompletedRequestList(page, 20));
    }

    public void getIncidentReportDetail(final Context context, String requestId, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .getIncidentReportDetail(requestId));
    }

    public void getServerTimeDiff(final Context context, long sysTime, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .getServerTimeDiff(sysTime));
    }

    public void updateProviderProfile(final Context context, User user, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .updateProviderProfile(user));
    }

    public void updateUserProfile(final Context context, User user, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .updateUserProfile(user));
    }

    public void registerProvider(final Context context, ProviderRegistration registration, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .registerProvider(registration));
    }

    public void getOccupationList(final Context context, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .getOccupationList());
    }

    public void getCprInstituteList(final Context context, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .getCprInstituteList());
    }

    public void getUserProfile(final Context context, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .getUserProfile());
    }

    public void changePassword(final Context context, HashMap<String, Object> body, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .changePassword(body));
    }

    public void getProviderRegistrationDetails(final Context context, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .getProviderRegistrationDetails());
    }

    public void getNewsFeedDetail(final Context context, double latitude, double longitude, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .getNewsFeedDetail(latitude, longitude));
    }

    public void getMedicalSearchDiseaseList(final Context context, String searchText, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .getMedicalSearchDiseases(searchText));
    }

    public void updateMedicalDiseaseList(final Context context, UserDiseaseListRequest request, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .updateMedicalDiseases(request));
    }

    public void getSelectedMedicalDiseasesList(final Context context, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .getSelectedMedicalDiseasesList());
    }

    public void getSubscriptionOffersList(final Context context, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .getSubscriptionOffersList());
    }

    public void subscribeUser(final Context context, String id, final RestClientResponse restClientResponse) {
        ApiServiceUtil.getInstance().retrofitWebServiceCall(context, restClientResponse, ApiServiceUtil.getInstance().getRestClient(context)
                .subscribeUser(id));
    }
}