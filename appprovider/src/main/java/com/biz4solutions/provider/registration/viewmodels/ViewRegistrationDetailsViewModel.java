package com.biz4solutions.provider.registration.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.biz4solutions.models.ProviderRegistration;
import com.biz4solutions.provider.R;
import com.biz4solutions.utilities.Constants;

import java.text.SimpleDateFormat;

public class ViewRegistrationDetailsViewModel extends AndroidViewModel {

    public ObservableField<ProviderRegistration> registration;

    private ViewRegistrationDetailsViewModel(@NonNull Application application, ProviderRegistration providerRegistration) {
        super(application);
        registration = new ObservableField<>();
        registration.set(providerRegistration);
    }

    public String getFormattedDate(long date) {
        if (date > 0) {
            @SuppressLint("simpledateformat")
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
            return sdf.format(date);
        }
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    public int getStatusBackgroundColor() {
        //pending
        if (registration.get() != null) {
            if (registration.get().getApproved() == null) {
                return R.color.txt_status_bg_orange;
            } else if (registration.get().getApproved()) //approved
            {
                return R.color.txt_status_bg_green;
            } else if (!registration.get().getApproved()) //rejected
            {
                return R.color.txt_status_bg_red;
            }
        }
        return R.color.white;
    }

    @SuppressWarnings("ConstantConditions")
    public int getStatusTextColor() {
        //pending
        if (registration.get() != null) {
            if (registration.get().getApproved() == null) {
                return R.color.txt_status_orange;
            } else if (registration.get().getApproved()) {//approved
                return R.color.txt_status_green;
            } else if (!registration.get().getApproved()) { //rejected
                return R.color.txt_status_red;
            }
        }
        return R.color.bpblack;
    }

    @SuppressWarnings("ConstantConditions")
    public int getStatusDescTextColor() {
        //pending
        if (registration.get() != null) {
            if (registration.get().getApproved() == null) {
                return R.color.txt_status_desc_light_pending;
            } else if (registration.get().getApproved()) { //approved
                return R.color.txt_status_desc_light_green;
            } else if (!registration.get().getApproved()) { //rejected
                return R.color.txt_status_desc_light_red;
            }
        }
        return R.color.bpblack;
    }

    @SuppressWarnings("ConstantConditions")
    public String getStatus() {
        //pending
        if (registration.get() != null) {
            if (registration.get().getApproved() == null) {
                registration.get().setAdminComment(getApplication().getBaseContext().getString(R.string.txt_pending_info));
                return "PENDING";
            } else if (registration.get().getApproved()) { //approved
                return "APPROVED";
            } else if (!registration.get().getApproved()) { //rejected
                return "REJECTED";
            }
        }
        return null;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        registration = null;
    }

    @SuppressWarnings("unchecked")
    public static class ViewRegistrationDetailsFactory extends ViewModelProvider.NewInstanceFactory {

        private final ProviderRegistration providerRegistration;
        private Application application;

        public ViewRegistrationDetailsFactory(Application application, ProviderRegistration providerRegistration) {
            this.application = application;
            this.providerRegistration = providerRegistration;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ViewRegistrationDetailsViewModel(application, providerRegistration);
        }
    }
}