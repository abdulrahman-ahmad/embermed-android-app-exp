package com.biz4solutions.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.biz4solutions.models.User;

public class ViewProfileViewModel extends ViewModel {

    public ObservableField<String> imageUrl;
    public MutableLiveData<User> userData;

    private ViewProfileViewModel() {
        super();
        imageUrl = new ObservableField<>();
        userData = new MutableLiveData<>();
    }

    public void setUserData(User userData) {
        if (userData != null)
            this.userData.setValue(userData);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public static class ViewProfileViewModelFactory extends ViewModelProvider.NewInstanceFactory {
        public ViewProfileViewModelFactory() {
            super();
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ViewProfileViewModel();
        }
    }
}
