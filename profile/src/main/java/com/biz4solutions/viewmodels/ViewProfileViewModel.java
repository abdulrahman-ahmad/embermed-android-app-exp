package com.biz4solutions.viewmodels;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.User;
import com.biz4solutions.models.response.LoginResponse;
import com.biz4solutions.profile.R;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ViewProfileViewModel extends ViewModel {

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    public MutableLiveData<User> userData;
    public ObservableField<String> displayDateOfBirth;
    private final MutableLiveData<String> toastMsg;

    private ViewProfileViewModel(Context context) {
        super();
        displayDateOfBirth = new ObservableField<>();
        toastMsg = new MutableLiveData<>();
        userData = new MutableLiveData<>();
        this.context = context;
    }

    public MutableLiveData<String> getToastMsg() {
        return toastMsg;
    }

    public void setUserData(User userData) {
        if (userData != null) {
            this.userData.setValue(userData);
            displayDateOfBirth.set(formatDate(userData.getDob()));
        }
    }

    private String formatDate(long millis) {
        if (millis <= 0) {
            return "";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
        return sdf.format(calendar.getTime());
    }

    public void onChangePwdClick(View v) {
        Toast.makeText(v.getContext(), v.getContext().getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public boolean isShowChangePassword() {
        return userData.getValue() != null && Constants.SIGN_UP_TYPE_EMAIL.equals("" + userData.getValue().getSignupType());
    }

    public void getUserDetails() {
        //internet check
        if (CommonFunctions.getInstance().isOffline(context)) {
            toastMsg.setValue(context.getString(R.string.error_network_unavailable));
            return;
        }
        new ApiServices().getUserProfile(context, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                LoginResponse loginResponse = (LoginResponse) response;
                if (loginResponse != null && loginResponse.getData() != null) {
                    setUserData(loginResponse.getData());
                }
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {

            }
        });
    }

    @SuppressWarnings("unchecked")
    public static class ViewProfileViewModelFactory extends ViewModelProvider.NewInstanceFactory {
        private final Context context;

        public ViewProfileViewModelFactory(Context context) {
            super();
            this.context = context;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ViewProfileViewModel(context);
        }
    }
}