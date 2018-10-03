package com.biz4solutions.viewmodels;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.biz4solutions.activities.ProfileActivity;
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.response.EmptyResponse;
import com.biz4solutions.profile.R;
import com.biz4solutions.utilities.CommonFunctions;

import java.util.HashMap;

public class ChangePasswordViewModel extends ViewModel {

    private final MutableLiveData<String> toastMsg;
    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private String oldPassword;
    private String newPassword;

    private ChangePasswordViewModel(Context context) {
        super();
        toastMsg = new MutableLiveData<>();
        this.context = context;
    }

    public MutableLiveData<String> getToastMsg() {
        return toastMsg;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    //save btn click
    public void onSaveBtnClick(View v) {
        //api call
        if (CommonFunctions.getInstance().isOffline(v.getContext())) {
            toastMsg.setValue(v.getContext().getString(R.string.error_network_unavailable));
            return;
        }
        CommonFunctions.getInstance().hideSoftKeyBoard((ProfileActivity) context);
        if (validateInfo(v.getContext())) {
            CommonFunctions.getInstance().loadProgressDialog(v.getContext());
            HashMap<String, Object> body = new HashMap<>();
            body.put("oldPassword", oldPassword);
            body.put("newPassword", newPassword);
            new ApiServices().changePassword(v.getContext(), body, new RestClientResponse() {
                @Override
                public void onSuccess(Object response, int statusCode) {
                    (((ProfileActivity) context)).reOpenViewProfileFragment();
                    CommonFunctions.getInstance().dismissProgressDialog();
                    toastMsg.setValue(((EmptyResponse) response).getMessage());
                    CommonFunctions.getInstance().hideSoftKeyBoard((ProfileActivity) context);
                }

                @Override
                public void onFailure(String errorMessage, int statusCode) {
                    CommonFunctions.getInstance().dismissProgressDialog();
                    toastMsg.setValue(errorMessage);
                }
            });
        }
    }

    private boolean validateInfo(Context context) {
        if (oldPassword == null || oldPassword.isEmpty()) {
            toastMsg.setValue(context.getString(R.string.error_empty_old_password));
            return false;
        } else if (oldPassword.length() < 8 || oldPassword.length() > 20) {
            toastMsg.setValue(context.getString(R.string.error_invalid_old_password));
            return false;
        } else if (newPassword == null || newPassword.isEmpty()) {
            toastMsg.setValue(context.getString(R.string.error_empty_new_password));
            return false;
        } else if (newPassword.length() < 8 || newPassword.length() > 20) {
            toastMsg.setValue(context.getString(R.string.error_invalid_new_password));
            return false;
        }
        return true;
    }

    //watcher for oldPassword
    public void oldPasswordWatcher(CharSequence s, int start, int before, int count) {
        oldPassword = s.toString();
    }

    //watcher for newPassword
    public void newPasswordWatcher(CharSequence s, int start, int before, int count) {
        newPassword = s.toString();
    }

    @SuppressWarnings("unchecked")
    public static class ChangePasswordViewModelFactory extends ViewModelProvider.NewInstanceFactory {

        private final Context context;

        public ChangePasswordViewModelFactory(Context context) {
            super();
            this.context = context;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ChangePasswordViewModel(context);
        }
    }
}