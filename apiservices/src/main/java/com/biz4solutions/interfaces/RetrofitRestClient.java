package com.biz4solutions.interfaces;

import com.biz4solutions.models.SocialMediaUserData;
import com.biz4solutions.models.request.CreateEmsRequest;
import com.biz4solutions.models.request.IncidentReport;
import com.biz4solutions.models.request.LoginRequest;
import com.biz4solutions.models.request.SignUpRequest;
import com.biz4solutions.models.response.CreateEmsResponse;
import com.biz4solutions.models.response.EmptyResponse;
import com.biz4solutions.models.response.EmsRequestDetailsResponse;
import com.biz4solutions.models.response.EmsRequestResponse;
import com.biz4solutions.models.response.LoginResponse;
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

    @POST("v1/users/login")
    Call<LoginResponse> doLogin(@Body LoginRequest loginRequest);

    @POST("v1/provider/login")
    Call<LoginResponse> doProviderLogin(@Body LoginRequest loginRequest);

    @POST("v1/users/socialAppLogin")
    Call<LoginResponse> socialAppLogin(@Body SocialMediaUserData socialSignInDTO);

    @POST("v1/users/signup")
    Call<LoginResponse> signUp(@Body SignUpRequest signUpRequest);

    @POST("v1/otp/requestOtp")
    Call<EmptyResponse> requestOtp(@Body HashMap<String, String> body);

    @POST("v1/otp/resendOtp")
    Call<EmptyResponse> resendOtp(@Body HashMap<String, String> body);

    @POST("v1/otp/verify")
    Call<EmptyResponse> verifyOtp(@Body HashMap<String, Object> body);

    @POST("v1/user/resetPassword")
    Call<EmptyResponse> resetPassword(@Body HashMap<String, Object> body);

    @PUT("v1/users/updateFcmToken")
    Call<EmptyResponse> setFcmToken(@Body HashMap<String, Object> body);

    @POST("v1/users/sendRequest")
    Call<CreateEmsResponse> createRequest(@Body CreateEmsRequest body);

    @DELETE("v1/users/cancelRequest")
    Call<EmptyResponse> cancelRequest(@Query("requestId") String query);

    @GET("v1/provider/request/list")
    Call<EmsRequestResponse> getRequestList(@Query("page") int page, @Query("size") int size);

    @PUT("v1/provider/request/accept")
    Call<EmptyResponse> acceptRequest(@Query("requestId") String requestId);

    @POST("v1/provider/incidentReport/submit")
    Call<EmptyResponse> submitIncidentReport(@Body IncidentReport body);

    @DELETE("v1/provider/request/reject")
    Call<EmptyResponse> rejectRequest(@Query("requestId") String requestId);

    @PUT("v1/provider/request/complete")
    Call<EmptyResponse> completeRequest(@Query("requestId") String requestId);

    @GET("v1/provider/requestDetail")
    Call<EmsRequestDetailsResponse> getRequestDetails(@Query("requestId") String requestId);

    @GET("api/directions/json")
    Call<GoogleDirectionResponse> getDirections(@Query("units") String units, @Query("origin") String origin, @Query("destination") String destination, @Query("mode") String mode, @Query("sensor") boolean sensor/*, @Query("key") String key*/);

    @GET("api/distancematrix/json")
    Call<GoogleDistanceDurationResponse> getDistanceDuration(@Query("units") String units, @Query("origins") String origins, @Query("destinations") String destinations, @Query("mode") String mode, @Query("sensor") boolean sensor/*, @Query("key") String key*/);
}