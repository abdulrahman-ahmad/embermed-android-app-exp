package com.biz4solutions.provider.registration.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.ProviderRegistration;
import com.biz4solutions.models.User;
import com.biz4solutions.models.response.GenericResponse;
import com.biz4solutions.provider.R;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;

import java.text.SimpleDateFormat;

public class ViewRegistrationDetailsViewModel extends AndroidViewModel implements RestClientResponse {

//    public MutableLiveData<User> user;
    public MutableLiveData<Boolean> isDataChanged;
    private MutableLiveData<String> toastMsg;
    public ObservableField<ProviderRegistration> registration;


    private ViewRegistrationDetailsViewModel(@NonNull Application application) {
        super(application);
        toastMsg = new MutableLiveData<>();
        isDataChanged = new MutableLiveData<>();
//        user = new MutableLiveData<>();
        ApiServices apiServices = new ApiServices();
        registration = new ObservableField<>();
        CommonFunctions.getInstance().loadProgressDialog(application.getBaseContext());
        apiServices.getProviderRegistrationDetails(application.getBaseContext(), this);
    }

    public MutableLiveData<String> getToastMsg() {
        return toastMsg;
    }

    public MutableLiveData<Boolean> getIsDataChanged() {
        return isDataChanged;
    }

    @Override
    public void onSuccess(Object response, int statusCode) {
        CommonFunctions.getInstance().dismissProgressDialog();
        if (registration != null) {
            registration.set((ProviderRegistration) ((GenericResponse) response).getData());
            isDataChanged.setValue(true);
        }
    }

    @Override
    public void onFailure(String errorMessage, int statusCode) {
        CommonFunctions.getInstance().dismissProgressDialog();
        toastMsg.setValue(errorMessage);
    }

    public String getFormattedDate(long date) {
        if (date > 0) {
            @SuppressLint("simpledateformat")
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
            return sdf.format(date);
        }
        return null;
    }


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

    public int getStatusTextColor() {
        //pending
        if (registration.get() != null) {
            if (registration.get().getApproved() == null) {
                return R.color.txt_status_orange;
            } else if (registration.get().getApproved()) //approved
            {
                return R.color.txt_status_green;
            } else if (!registration.get().getApproved()) //rejected
            {
                return R.color.txt_status_red;
            }
        }
        return R.color.bpblack;
    }


    public int getStatusDescTextColor() {
        //pending
        if (registration.get() != null) {
            if (registration.get().getApproved() == null) {
                return R.color.txt_status_desc_light_pending;
            } else if (registration.get().getApproved()) //approved
            {
                return R.color.txt_status_desc_light_green;
            } else if (!registration.get().getApproved()) //rejected
            {
                return R.color.txt_status_desc_light_red;
            }
        }
        return R.color.bpblack;
    }

    public String getStatus() {
        //pending
        if (registration.get() != null) {
            if (registration.get().getApproved() == null) {
                registration.get().setAdminComment(getApplication().getBaseContext().getString(R.string.txt_pending_info));
                return "PENDING";
            } else if (registration.get().getApproved()) //approved
            {
                return "APPROVED";
            } else if (!registration.get().getApproved()) //rejected
            {
                return "REJECTED";
            }
        }
        return null;
    }


//    public void setUser(User user) {
//        this.user.setValue(user);
//    }

    @Override
    protected void onCleared() {
        super.onCleared();
        registration = null;
    }

    @SuppressWarnings("unchecked")
    public static class ViewRegistrationDetailsFactory extends ViewModelProvider.NewInstanceFactory {

        private Application application;

        public ViewRegistrationDetailsFactory(Application application) {
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ViewRegistrationDetailsViewModel(application);
        }
    }
}
