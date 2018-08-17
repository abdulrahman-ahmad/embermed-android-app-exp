package com.biz4solutions.apiservices;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.interfaces.RetrofitRestClient;
import com.biz4solutions.models.response.EmptyResponse;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * Created by ketan on 12/1/2017.
 */
public class ApiServiceUtil {
    private static final int INTERNAL_SERVER_ERROR_CODE = 999;
    private static final String USER_PREFERENCE = "USER_PREFERENCE";
    private static final String USER_AUTH_KEY = "USER_AUTH_KEY";
    private static ApiServiceUtil instance = null;
    private static RetrofitRestClient sRestClient;

    private ApiServiceUtil() {
    }

    public static ApiServiceUtil getInstance() {
        if (instance == null) {
            instance = new ApiServiceUtil();
        }
        return instance;
    }

    public static void resetInstance() {
        ApiServiceUtil.instance = null;
        ApiServiceUtil.sRestClient = null;
        getInstance();
    }

    RetrofitRestClient getRestClient(Context context) {
        if (sRestClient == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getOkHttpClient(getCommonHeaders(context)))
                    .build();
            sRestClient = retrofit.create(RetrofitRestClient.class);
        }
        return sRestClient;
    }

    RetrofitRestClient getRestClient(String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient(null))
                .build();
        return retrofit.create(RetrofitRestClient.class);
    }

    private OkHttpClient getOkHttpClient(final HashMap<String, String> commonHeadersMap) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        if (commonHeadersMap != null) {
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    String authToken = "";
                    if (commonHeadersMap.containsKey("Authorization")) {
                        authToken = commonHeadersMap.get("Authorization");
                    }
                    Request request = original.newBuilder()
                            .header("Content-Type", "application/json")
                            .header("Accept-Language", commonHeadersMap.get("Accept-Language"))
                            .header("deviceId", commonHeadersMap.get("deviceId"))
                            .header("appVersion", commonHeadersMap.get("appVersion"))
                            .header("osVersion", commonHeadersMap.get("osVersion"))
                            .header("deviceType", commonHeadersMap.get("deviceType"))
                            .header("Authorization", authToken)
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            });
        }

        return httpClient
                .readTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .build();
    }

    private HashMap<String, String> getCommonHeaders(Context context) {
        HashMap<String, String> commonHeaders = new HashMap<>();
        commonHeaders.put("Accept-Language", getDeviceLanguage());
        commonHeaders.put("deviceId", getDeviceID(context));
        commonHeaders.put("appVersion", getAppVersion());
        commonHeaders.put("osVersion", "" + getOsVersion());
        commonHeaders.put("deviceType", getDeviceType());
        String authToken = getAuthToken(context);
        if (authToken != null && !authToken.isEmpty()) {
            commonHeaders.put("Authorization", authToken);
        } else {
            commonHeaders.put("Authorization", "");
        }
        return commonHeaders;
    }

    /*use to get Accept-Language*/
    private String getDeviceLanguage() {
        String localeLan = Locale.getDefault().getLanguage();
        return localeLan != null && !localeLan.isEmpty() ? localeLan : "en";
    }

    /*use to get deviceId*/
    @SuppressLint("HardwareIds")
    private String getDeviceID(Context context) {
        try {
            if (context != null) {
                return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /*use to get appVersion*/
    private String getAppVersion() {
        return BuildConfig.VERSION_NAME;
    }

    /*use to get osVersion*/
    private int getOsVersion() {
        return Build.VERSION.SDK_INT;
    }

    /*use to get deviceType*/
    private String getDeviceType() {
        return "ANDROID_PHONE";
    }

    /* to get authToken*/
    private String getAuthToken(Context context) {
        return SharedPrefsManager.getInstance().retrieveStringPreference(context, USER_PREFERENCE, USER_AUTH_KEY);
    }

    /*use to get device name*/
    public String getDeviceName() {
        return Build.BRAND + " " + Build.MODEL;
    }

    <A> void retrofitWebServiceCall(final Context context, final RestClientResponse restClientResponse, Call<A> call) {
        try {
            call.enqueue(new Callback<A>() {
                @Override
                public void onResponse(Call<A> call, Response<A> response) {
                    if (restClientResponse != null) {
                        if (response.isSuccessful()) {
                            restClientResponse.onSuccess(response.body(), response.code());
                        } else if (response.errorBody() != null) {
                            try {
                                EmptyResponse emptyResponse = new Gson().getAdapter(EmptyResponse.class).fromJson(response.errorBody().string());
                                if (response.code() == Constants.UNAUTHORIZED_ERROR_CODE) {
                                    CommonFunctions.getInstance().doLogOut(context, emptyResponse.getMessage());
                                } else {
                                    restClientResponse.onFailure(emptyResponse.getMessage(), emptyResponse.getCode());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                restClientResponse.onFailure(response.message(), response.code());
                            }
                        } else {
                            restClientResponse.onFailure(response.message(), response.code());
                        }
                    }
                }

                @Override
                public void onFailure(Call<A> call, Throwable t) {
                    try {
                        if (restClientResponse != null) {
                            if (t instanceof SocketTimeoutException || t instanceof ConnectException) {
                                restClientResponse.onFailure(context.getString(R.string.error_server_connect_error), INTERNAL_SERVER_ERROR_CODE);
                            } else if (t instanceof JsonSyntaxException) {
                                restClientResponse.onFailure(context.getString(R.string.error_invalid_server_data), INTERNAL_SERVER_ERROR_CODE);
                            } else {
                                restClientResponse.onFailure(context.getString(R.string.error_internal_server_error) + ", " + t.getMessage(), INTERNAL_SERVER_ERROR_CODE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
