package com.biz4solutions.interfaces;

import com.biz4solutions.models.ProviderRegistration;
import com.biz4solutions.models.SocialMediaUserData;
import com.biz4solutions.models.User;
import com.biz4solutions.models.request.CreateEmsRequest;
import com.biz4solutions.models.request.FeedbackRequest;
import com.biz4solutions.models.request.IncidentReport;
import com.biz4solutions.models.request.LoginRequest;
import com.biz4solutions.models.request.SignUpRequest;
import com.biz4solutions.models.response.CprTrainingInstitutesResponse;
import com.biz4solutions.models.response.CreateEmsResponse;
import com.biz4solutions.models.response.EmptyResponse;
import com.biz4solutions.models.response.EmsRequestDetailsResponse;
import com.biz4solutions.models.response.EmsRequestResponse;
import com.biz4solutions.models.response.LoginResponse;
import com.biz4solutions.models.response.OccupationResponse;
import com.biz4solutions.models.response.ServerTimeDiffResponse;
import com.biz4solutions.models.response.SymptomResponse;
import com.biz4solutions.models.response.UrgentCaresResponse;
import com.biz4solutions.models.response.google.GoogleDirectionResponse;
import com.biz4solutions.models.response.google.GoogleDistanceDurationResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface RetrofitRestClient {

    @POST("/api/v1/users/login")
    Call<LoginResponse> doLogin(@Body LoginRequest loginRequest);

    @POST("/api/v1/provider/login")
    Call<LoginResponse> doProviderLogin(@Body LoginRequest loginRequest);

    @POST("/api/v1/users/socialAppLogin")
    Call<LoginResponse> socialAppLogin(@Body SocialMediaUserData socialSignInDTO);

    @POST("/api/v1/users/signup")
    Call<LoginResponse> signUp(@Body SignUpRequest signUpRequest);

    @POST("/api/v1/otp/requestOtp")
    Call<EmptyResponse> requestOtp(@Body HashMap<String, String> body);

    @POST("/api/v1/otp/resendOtp")
    Call<EmptyResponse> resendOtp(@Body HashMap<String, String> body);

    @POST("/api/v1/otp/verify")
    Call<EmptyResponse> verifyOtp(@Body HashMap<String, Object> body);

    @POST("/api/v1/user/resetPassword")
    Call<EmptyResponse> resetPassword(@Body HashMap<String, Object> body);

    @PUT("/api/v1/users/updateFcmToken")
    Call<EmptyResponse> setFcmToken(@Body HashMap<String, Object> body);

    @POST("/api/v1/users/sendRequest")
    Call<CreateEmsResponse> createRequest(@Body CreateEmsRequest body);

    @DELETE("/api/v1/users/cancelRequest")
    Call<EmptyResponse> cancelRequest(@Query("requestId") String query);

    @GET("/api/v1/provider/request/list")
    Call<EmsRequestResponse> getRequestList(@Query("page") int page, @Query("size") int size);

    @PUT("/api/v1/provider/request/accept")
    Call<EmptyResponse> acceptRequest(@Query("requestId") String requestId);

    @POST("/api/v1/provider/incidentReport/submit")
    Call<EmptyResponse> submitIncidentReport(@Body IncidentReport body);

    @DELETE("/api/v1/provider/request/reject")
    Call<EmptyResponse> rejectRequest(@Query("requestId") String requestId);

    @PUT("/api/v1/provider/request/complete")
    Call<EmptyResponse> completeRequest(@Body HashMap<String, Object> body);

    @GET("/api/v1/provider/requestDetail")
    Call<EmsRequestDetailsResponse> getRequestDetails(@Query("requestId") String requestId);

    // google api
    @GET("api/directions/json")
    Call<GoogleDirectionResponse> getDirections(@Query("units") String units, @Query("origin") String origin, @Query("destination") String destination, @Query("mode") String mode, @Query("sensor") boolean sensor, @Query("key") String key);

    // google api
    @GET("api/distancematrix/json")
    Call<GoogleDistanceDurationResponse> getDistanceDuration(@Query("units") String units, @Query("origins") String origins, @Query("destinations") String destinations, @Query("mode") String mode, @Query("sensor") boolean sensor, @Query("key") String key);

    @GET("/api/v1/symptom/list")
    Call<SymptomResponse> getSymptomList(@Query("page") int page, @Query("size") int size);

    @DELETE("/api/v1/users/logout")
    Call<EmptyResponse> logout();

    @POST("/api/v1/triage/endCall")
    Call<EmptyResponse> endCall(@Query("requestId") String requestId);

    @POST("/api/v1/users/submitUserFeedBack")
    Call<EmptyResponse> submitUserFeedBack(@Body FeedbackRequest body);

    @POST("/api/v1/provider/submitProviderFeedBack")
    Call<EmptyResponse> submitProviderFeedBack(@Body FeedbackRequest body);

    @GET("/api/v1/users/getUrgentCareList")
    Call<UrgentCaresResponse> getUrgentCareList(@Query("page") int page, @Query("size") int size, @Query("latitude") double latitude, @Query("longitude") double longitude);

    @GET("/api/v1/provider/getAedList")
    Call<UrgentCaresResponse> getAedList(@Query("page") int page, @Query("size") int size, @Query("latitude") double latitude, @Query("longitude") double longitude);

    @GET("/api/v1/users/getUserCompletedRequestList")
    Call<EmsRequestResponse> getUserCompletedRequestList(@Query("page") int page, @Query("size") int size);

    @GET("/api/v1/provider/getProviderCompletedRequestList")
    Call<EmsRequestResponse> getProviderCompletedRequestList(@Query("page") int page, @Query("size") int size);

    @GET("/api/v1/incidentReport/getIncidentReportDetail")
    Call<EmsRequestDetailsResponse> getIncidentReportDetail(@Query("requestId") String requestId);

    @GET("/api/v1/user/getDateDiff")
    Call<ServerTimeDiffResponse> getServerTimeDiff(@Query("date") long sysTime);

    @POST("/api/v1/provider/updateProfileDetail")
    Call<EmptyResponse> updateProviderProfile(@Body User body);

    @POST("/api/v1/users/updateProfileDetail")
    Call<EmptyResponse> updateUserProfile(@Body User body);

    @GET("/api/v1/users/getProfileDetail")
    Call<LoginResponse> getUserProfile();

    @POST("/api/v1/user/changePassword")
    Call<EmptyResponse> changePassword(@Body HashMap<String, Object> body);

    @POST("/api/v1/provider/updateProviderRegistration")
    Call<EmptyResponse> registerProvider(@Body ProviderRegistration registration);

    @GET("/api/v1/provider/getProfessionList")
    Call<OccupationResponse> getOccupationList();

    @GET("/api/v1/provider/getCprTrainingInstituteList ")
    Call<CprTrainingInstitutesResponse> getCprInstituteList();
}