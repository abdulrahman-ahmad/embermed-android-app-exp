package com.biz4solutions.fragments.view.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.biz4solutions.models.User;
import com.biz4solutions.profile.R;

public class EditProfileViewModel extends ViewModel {

    public ObservableField<Integer> radioBtnId;
    public MutableLiveData<User> userData;

    private EditProfileViewModel() {
        super();
        radioBtnId = new ObservableField<>();
        userData = new MutableLiveData<>();
    }

    public void setUserData(User userData) {
        if (userData != null) {
            this.userData.setValue(userData);
            setRadioBtnSelection(userData.getGender());
        }
    }

    private void setRadioBtnSelection(String gender) {
        if (gender == null)
            return;
        switch (gender.toLowerCase()) {
            case "male":
                radioBtnId.set(R.id.rdb_male);
                break;
            case "female":
                radioBtnId.set(R.id.rdb_female);
                break;
            case "other":
                radioBtnId.set(R.id.rdb_other);
                break;
        }
    }

    //watcher for firstName
    public void firstNameWatcher(CharSequence s, int start, int before, int count) {
        userData.getValue().setFirstName(s.toString());
    }

    //watcher for lastName
    public void lastNameWatcher(CharSequence s, int start, int before, int count) {
        userData.getValue().setLastName(s.toString());
    }

    //save btn click
    public void onSaveBtnClick(View v) {

    }

    //listener for radio btn
    public void onRadioBtnChanged(RadioGroup radioGroup, int id) {
//        Toast.makeText(radioGroup.getContext(), "" + id + " " + ((RadioButton) radioGroup.findViewById(id)).getText(), Toast.LENGTH_SHORT).show();
        userData.getValue().setGender(((RadioButton) radioGroup.findViewById(id)).getText().toString());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public static class EditProfileViewModelFactory extends ViewModelProvider.NewInstanceFactory {
        public EditProfileViewModelFactory() {
            super();
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new EditProfileViewModel();
        }
    }
}
