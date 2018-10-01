package com.biz4solutions.viewmodels;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.biz4solutions.models.User;
import com.biz4solutions.profile.R;
import com.biz4solutions.utilities.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ViewProfileViewModel extends ViewModel {

    public MutableLiveData<User> userData;
    public ObservableField<String> displayDateOfBirth;

    private ViewProfileViewModel() {
        super();
        displayDateOfBirth = new ObservableField<>();
        userData = new MutableLiveData<>();
    }

    public void setUserData(User userData) {
        if (userData != null) {
            this.userData.setValue(userData);
            displayDateOfBirth.set(formatDate(userData.getDob()));
        }
    }

    private String formatDate(long millis) {
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

    @SuppressWarnings("unchecked")
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