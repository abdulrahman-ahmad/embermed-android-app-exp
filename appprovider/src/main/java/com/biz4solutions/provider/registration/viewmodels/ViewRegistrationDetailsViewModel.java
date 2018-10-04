package com.biz4solutions.provider.registration.viewmodels;

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
import com.biz4solutions.utilities.CommonFunctions;

public class ViewRegistrationDetailsViewModel extends AndroidViewModel implements RestClientResponse {

    public MutableLiveData<User> user;
    private MutableLiveData<String> toastMsg;
    public ObservableField<ProviderRegistration> registration;

    private ViewRegistrationDetailsViewModel(@NonNull Application application) {
        super(application);
        toastMsg = new MutableLiveData<>();
        user = new MutableLiveData<>();
        ApiServices apiServices = new ApiServices();
        registration = new ObservableField<>();
        CommonFunctions.getInstance().loadProgressDialog(application.getBaseContext());
        apiServices.getProviderRegistrationDetails(application.getBaseContext(), this);
    }


    public MutableLiveData<String> getToastMsg() {
        return toastMsg;
    }

    @Override
    public void onSuccess(Object response, int statusCode) {
        CommonFunctions.getInstance().dismissProgressDialog();
        if(registration!=null)
        registration.set((ProviderRegistration) ((GenericResponse) response).getData());
    }

    @Override
    public void onFailure(String errorMessage, int statusCode) {
        CommonFunctions.getInstance().dismissProgressDialog();
        toastMsg.setValue(errorMessage);
    }

    public void setUser(User user) {
        this.user.setValue(user);
    }

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
