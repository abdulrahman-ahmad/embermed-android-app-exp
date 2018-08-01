package com.biz4solutions.interfaces;

import com.biz4solutions.models.EmptyResponse;
import com.biz4solutions.models.LoginRequest;
import com.biz4solutions.models.LoginResponseDTO;
import com.biz4solutions.models.OtpResponseDTO;
import com.biz4solutions.models.SignUpRequest;

import java.util.HashMap;

import osiris.com.socialmedialib.models.SocialMediaUserData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitRestClient {

    @POST("v1/users/login")
    Call<LoginResponseDTO> doLogin(@Body LoginRequest loginRequest);

    @POST("v1/users/socialAppLogin")
    Call<LoginResponseDTO> socialAppLogin(@Body SocialMediaUserData socialSignInDTO);

    @POST("v1/users/signup")
    Call<LoginResponseDTO> signUp(@Body SignUpRequest signUpRequest);

    @POST("v1/otp/requestOtp")
    Call<OtpResponseDTO> requestOtp(@Body HashMap<String, String> body);

    @POST("v1/otp/resendOtp")
    Call<OtpResponseDTO> resendOtp(@Body HashMap<String, String> body);

    @POST("v1/otp/verify")
    Call<EmptyResponse> verifyOtp(@Body HashMap<String, Object> body);

    @POST("v1/user/resetPassword")
    Call<EmptyResponse> resetPassword(@Body HashMap<String, Object> body);

    @POST("v1/users/fcmToken")
    Call<EmptyResponse> setFcmToken(@Body HashMap<String, Object> body);
}