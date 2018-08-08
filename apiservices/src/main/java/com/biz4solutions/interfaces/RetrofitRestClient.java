package com.biz4solutions.interfaces;

import com.biz4solutions.models.SocialMediaUserData;
import com.biz4solutions.models.request.LoginRequest;
import com.biz4solutions.models.request.SignUpRequest;
import com.biz4solutions.models.response.EmptyResponse;
import com.biz4solutions.models.response.LoginResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

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

    @POST("v1/users/fcmToken")
    Call<EmptyResponse> setFcmToken(@Body HashMap<String, Object> body);
}