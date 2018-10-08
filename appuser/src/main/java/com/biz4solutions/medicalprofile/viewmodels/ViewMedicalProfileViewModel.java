package com.biz4solutions.medicalprofile.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.MedicalDisease;
import com.biz4solutions.models.response.GenericResponse;
import com.biz4solutions.utilities.CommonFunctions;

import java.util.ArrayList;

public class ViewMedicalProfileViewModel extends AndroidViewModel implements RestClientResponse {
    private ApiServices apiServices;
    private MutableLiveData<ArrayList<MedicalDisease>> diseaseMutableLiveData;
    private MutableLiveData<String> toastMsg;

    private ViewMedicalProfileViewModel(@NonNull Application application) {
        super(application);
        apiServices = new ApiServices();
        diseaseMutableLiveData = new MutableLiveData<>();
        toastMsg = new MutableLiveData<>();
        getDiseaseListData();
    }

    private void getDiseaseListData() {
        CommonFunctions.getInstance().loadProgressDialog(getApplication().getBaseContext());
        apiServices.getSelectedMedicalDiseasesList(getApplication().getBaseContext(), this);

    }

    public LiveData<ArrayList<MedicalDisease>> getDiseaseMutableLiveData() {
        return diseaseMutableLiveData;
    }

    public MutableLiveData<String> getToastMsg() {
        return toastMsg;
    }


    @Override
    public void onSuccess(Object response, int statusCode) {
        CommonFunctions.getInstance().dismissProgressDialog();
        if (response != null) {
            ArrayList<MedicalDisease> diseaseArrayList = (ArrayList<MedicalDisease>) ((GenericResponse) response).getData();
            diseaseMutableLiveData.setValue(diseaseArrayList);
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public void onFailure(String errorMessage, int statusCode) {
        CommonFunctions.getInstance().dismissProgressDialog();
        toastMsg.setValue(errorMessage);
    }

    public static class ViewMedicalProfileViewModelFactory extends ViewModelProvider.NewInstanceFactory {

        private Application application;

        public ViewMedicalProfileViewModelFactory(Application application) {
            super();
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ViewMedicalProfileViewModel(application);
        }
    }
}
