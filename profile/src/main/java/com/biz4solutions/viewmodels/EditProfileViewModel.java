package com.biz4solutions.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.ContextWrapper;
import android.databinding.ObservableField;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.User;
import com.biz4solutions.models.response.EmptyResponse;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.profile.R;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;
import com.biz4solutions.utilities.DatePickerDialog;
import com.biz4solutions.utilities.FirebaseUploadUtil;

public class EditProfileViewModel extends ViewModel implements DatePickerDialog.CustomDateInterface, FirebaseUploadUtil.FirebaseUploadInterface, RestClientResponse {

    public ObservableField<Integer> radioBtnId;
    public final MutableLiveData<User> userData;
    public ObservableField<String> dateOfBirth;
    private MutableLiveData<String> toastMsg;
    private User tempUser;
    private Uri capturedUri;
    private ApiServices apiServices;
    private Context context;

    private EditProfileViewModel(Context context) {
        super();
        this.context = context;
        radioBtnId = new ObservableField<>();
        userData = new MutableLiveData<>();
        toastMsg = new MutableLiveData<>();
        dateOfBirth = new ObservableField<>();
        apiServices = new ApiServices();
    }

    public MutableLiveData<String> getToastMsg() {
        return toastMsg;
    }

    public void setUserData(User userData) {
        if (userData != null) {
            tempUser = userData;
            setRadioBtnSelection(userData.getGender());
        } else {
            tempUser = new User();
        }
        this.userData.setValue(tempUser);
    }


    public void setCapturedUri(Uri capturedUri) {
        this.capturedUri = capturedUri;
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

    private boolean validateInfo(Context context) {

        //todo:need to change msg's;
        if (tempUser.getFirstName() == null || tempUser.getFirstName().isEmpty()) {
            toastMsg.setValue(context.getString(R.string.error_empty_first_name));
            return false;
        } else if (tempUser.getLastName() == null || tempUser.getLastName().isEmpty()) {
            toastMsg.setValue(context.getString(R.string.error_empty_last_name));
            return false;
        } else if (tempUser.getDob() == null || tempUser.getDob().isEmpty()) {
            toastMsg.setValue(context.getString(R.string.error_empty_last_name));
            return false;
        } else if (tempUser.getGender() == null || tempUser.getGender().isEmpty()) {
            toastMsg.setValue(context.getString(R.string.error_empty_last_name));
            return false;
        } else if (capturedUri == null || capturedUri.getPath().isEmpty()) {
            toastMsg.setValue(context.getString(R.string.error_empty_last_name));
            return false;
        }
        return true;
    }


    //watcher for firstName
    public void firstNameWatcher(CharSequence s, int start, int before, int count) {
        tempUser.setFirstName(s.toString());
    }

    //watcher for lastName
    public void lastNameWatcher(CharSequence s, int start, int before, int count) {
        tempUser.setLastName(s.toString());
    }

    //save btn click
    public void onSaveBtnClick(View v) {
        //api call

        if (CommonFunctions.getInstance().isOffline(context)) {
            Toast.makeText(context, context.getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        if (validateInfo(v.getContext())) {
            FirebaseUploadUtil.uploadImageToFirebase(v.getContext(), tempUser.getUserId() + "/", capturedUri, this);
        }
    }

    //listener for radio btn
    public void onRadioBtnChanged(RadioGroup radioGroup, int id) {
        tempUser.setGender(((RadioButton) radioGroup.findViewById(id)).getText().toString());
    }


    //Date of  birth textview click.
    public void onDobEdtClick(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog();
        datePickerDialog.registerListener(this, ((ContextWrapper) v.getContext()).getBaseContext());
        datePickerDialog.showDatePickerDialog(isProvider() != null && isProvider());
    }

    @Override
    public void onDateSetListener(String selectedDateToSendServer, String formattedDate) {
        setDob(selectedDateToSendServer, formattedDate);
    }

    private void setDob(@SuppressWarnings("unused") String dob, String formattedDate) {
        dateOfBirth.set(formattedDate);
        tempUser.setDob(dob);
    }


    private void updateProviderProfile() {
        //internet check
        if (CommonFunctions.getInstance().isOffline(context)) {
            Toast.makeText(context, context.getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(context);
        apiServices.updateProviderProfile(context, tempUser, this);
    }

    private void updateUserProfile() {
        //internet check
        if (CommonFunctions.getInstance().isOffline(context)) {
            Toast.makeText(context, context.getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(context);
        apiServices.updateUserProfile(context, tempUser, this);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        //remove all observable fields
        if (radioBtnId != null)
            radioBtnId = null;
        if (dateOfBirth != null)
            dateOfBirth = null;
        if (context != null)
            context = null;
    }

    @Override
    public void uploadSuccess(String imageUrl) {
        if (imageUrl != null) {
            //call api
            tempUser.setProfileUrl(imageUrl);
            if (isProvider()) {
                //call provider api
                updateProviderProfile();
            } else {
                //call user api
                updateUserProfile();
            }
        } else {
            toastMsg.setValue("Something went wrong, please try after some time");
        }
    }


    //check customer is user or provider
    private Boolean isProvider() {
        if (tempUser.getRoleName() == null)
            return null;

        return tempUser.getRoleName() == null || !tempUser.getRoleName().toLowerCase().equals("user");
    }

    @Override
    public void uploadError(String exceptionMsg) {
        Toast.makeText(context, "" + exceptionMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(Object response, int statusCode) {
        CommonFunctions.getInstance().dismissProgressDialog();
        toastMsg.setValue(((EmptyResponse) response).getMessage());
        updatePreferences();
        //update to sharedPreferences
    }

    private void updatePreferences() {
        SharedPrefsManager.getInstance().storeUserPreference(context, Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY, tempUser);
    }

    @Override
    public void onFailure(String errorMessage, int statusCode) {
        CommonFunctions.getInstance().dismissProgressDialog();
        toastMsg.setValue(errorMessage);
    }


    @SuppressWarnings("unchecked")
    public static class EditProfileViewModelFactory extends ViewModelProvider.NewInstanceFactory {

        private Context context;

        public EditProfileViewModelFactory(Context context) {
            super();
            this.context = context;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new EditProfileViewModel(context);
        }
    }
}
