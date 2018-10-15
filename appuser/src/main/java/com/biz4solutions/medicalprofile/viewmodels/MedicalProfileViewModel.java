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
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.models.MedicalDisease;
import com.biz4solutions.models.request.UserDiseaseListRequest;
import com.biz4solutions.models.response.EmptyResponse;
import com.biz4solutions.models.response.GenericResponse;
import com.biz4solutions.utilities.CommonFunctions;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class MedicalProfileViewModel extends AndroidViewModel implements RestClientResponse {
    private MutableLiveData<ArrayList<MedicalDisease>> diseaseMutableLiveData;
    private MutableLiveData<String> toastMsg;

    private MedicalProfileViewModel(@NonNull Application application) {
        super(application);
        diseaseMutableLiveData = new MutableLiveData<>();
        toastMsg = new MutableLiveData<>();
    }

    public LiveData<ArrayList<MedicalDisease>> getDiseaseMutableLiveData() {
        return diseaseMutableLiveData;
    }

    public MutableLiveData<String> getToastMsg() {
        return toastMsg;
    }

    public void getSearchTextList(String searchText) {
        if (CommonFunctions.getInstance().isOffline(getApplication().getBaseContext())) {
            toastMsg.setValue(getApplication().getBaseContext().getString(com.biz4solutions.profile.R.string.error_network_unavailable));
            return;
        }
        new ApiServices().getMedicalSearchDiseaseList(getApplication().getBaseContext(), searchText, this);
    }

    public void updateDataToServer(final MainActivity mainActivity, final ArrayList<MedicalDisease> newDiseaseArrayList, final ArrayList<MedicalDisease> backStackList) {
        if (CommonFunctions.getInstance().isOffline(getApplication().getBaseContext())) {
            toastMsg.setValue(getApplication().getBaseContext().getString(com.biz4solutions.profile.R.string.error_network_unavailable));
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(mainActivity);

        UserDiseaseListRequest request = new UserDiseaseListRequest();
        request.setData(newDiseaseArrayList);

        new ApiServices().updateMedicalDiseaseList(mainActivity, request, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                toastMsg.setValue((((EmptyResponse) response).getMessage()));
                CommonFunctions.getInstance().dismissProgressDialog();
                backStackList.clear();
                backStackList.addAll(newDiseaseArrayList);
                mainActivity.getSupportFragmentManager().popBackStack();
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                toastMsg.setValue(errorMessage);
            }
        });
    }

    @Override
    public void onSuccess(Object response, int statusCode) {
        if (response != null) {
            ArrayList<MedicalDisease> diseaseArrayList = (ArrayList<MedicalDisease>) ((GenericResponse) response).getData();
            diseaseMutableLiveData.setValue(diseaseArrayList);
        }
    }

    @Override
    public void onFailure(String errorMessage, int statusCode) {
        toastMsg.setValue(errorMessage);
    }

    public static class MedicalProfileViewModelFactory extends ViewModelProvider.NewInstanceFactory {

        private Application application;

        public MedicalProfileViewModelFactory(Application application) {
            super();
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new MedicalProfileViewModel(application);
        }
    }

}