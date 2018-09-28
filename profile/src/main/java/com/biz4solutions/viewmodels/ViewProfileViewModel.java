package com.biz4solutions.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.biz4solutions.models.User;
import com.biz4solutions.profile.R;

public class ViewProfileViewModel extends ViewModel {

    public MutableLiveData<User> userData;

    private ViewProfileViewModel() {
        super();
        userData = new MutableLiveData<>();
    }

    public void setUserData(User userData) {
        if (userData != null)
            this.userData.setValue(userData);
    }


    public void onChangePwdClick(View v) {
        Toast.makeText(v.getContext(), v.getContext().getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
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
