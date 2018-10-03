package com.biz4solutions.viewmodels;

import android.annotation.SuppressLint;
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

import com.biz4solutions.activities.ProfileActivity;
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.User;
import com.biz4solutions.models.response.EmptyResponse;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.profile.R;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;
import com.biz4solutions.utilities.FirebaseUploadUtil;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditProfileViewModel extends ViewModel implements FirebaseUploadUtil.FirebaseUploadInterface, RestClientResponse {

    public ObservableField<Integer> radioBtnId;
    public final MutableLiveData<User> userData;
    public ObservableField<String> displayDateOfBirth;
    private final MutableLiveData<String> toastMsg;
    private User tempUser;
    private Uri capturedUri;
    private ApiServices apiServices;
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private Long selectedDateValue = null;
    private Calendar todayDate = Calendar.getInstance();

    private EditProfileViewModel(Context context) {
        super();
        this.context = context;
        radioBtnId = new ObservableField<>();
        userData = new MutableLiveData<>();
        toastMsg = new MutableLiveData<>();
        displayDateOfBirth = new ObservableField<>();
        apiServices = new ApiServices();
    }

    public MutableLiveData<String> getToastMsg() {
        return toastMsg;
    }

    public void setUserData(User userData) {
        if (userData != null) {
            tempUser = userData;
            displayDateOfBirth.set(formatDate(tempUser.getDob()));
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
        if (tempUser.getFirstName() == null || tempUser.getFirstName().isEmpty()) {
            toastMsg.setValue(context.getString(R.string.error_empty_first_name));
            return false;
        } else if (tempUser.getLastName() == null || tempUser.getLastName().isEmpty()) {
            toastMsg.setValue(context.getString(R.string.error_empty_last_name));
            return false;
        } else if (tempUser.getDob() <= 0) {
            toastMsg.setValue(context.getString(R.string.error_empty_dob));
            return false;
        } else if (tempUser.getDob() > Calendar.getInstance().getTimeInMillis()) {
            toastMsg.setValue(context.getString(R.string.error_invalid_dob));
            return false;
        } else if (tempUser.getGender() == null || tempUser.getGender().isEmpty()) {
            toastMsg.setValue(context.getString(R.string.error_empty_gender));
            return false;
        } else if (isProvider()
                && (tempUser.getProfileUrl() == null || tempUser.getProfileUrl().isEmpty())
                && (capturedUri == null || capturedUri.getPath() == null || capturedUri.getPath().isEmpty())) {
            toastMsg.setValue(context.getString(R.string.error_empty_image));
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
            CommonFunctions.getInstance().loadProgressDialog(v.getContext());
            if (capturedUri != null) {
                FirebaseUploadUtil.uploadImageToFirebase(tempUser.getUserId(), capturedUri, this);
            } else {
                saveUserData();
            }
        }
    }

    private void saveUserData() {
        if (isProvider()) {
            updateProviderProfile();
        } else {
            updateUserProfile();
        }
    }

    //listener for radio btn
    public void onRadioBtnChanged(RadioGroup radioGroup, int id) {
        tempUser.setGender(((RadioButton) radioGroup.findViewById(id)).getText().toString());
    }

    //Date of  birth textview click.
    public void onDobEdtClick(View v) {
        selectDate(((ContextWrapper) v.getContext()).getBaseContext());
    }

    private void selectDate(Context context) {

        Calendar previousSelectedDate = Calendar.getInstance();
        int previousSelectedDateYear, previousSelectedDateMonth, previousSelectedDateDay;
        if (selectedDateValue != null) {
            previousSelectedDate.setTimeInMillis(selectedDateValue);
        } else {
            previousSelectedDate.setTimeInMillis(todayDate.getTimeInMillis());
        }
        previousSelectedDateYear = previousSelectedDate.get(Calendar.YEAR);
        previousSelectedDateMonth = previousSelectedDate.get(Calendar.MONTH);
        previousSelectedDateDay = previousSelectedDate.get(Calendar.DAY_OF_MONTH);
        int MIN_YEAR_INTERVAL = 0;
        if (isProvider()) {
            MIN_YEAR_INTERVAL = 18;
        }

        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                    @Override
                    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                        displayDateOfBirth.set(formatDate(year, monthOfYear, dayOfMonth));
                        tempUser.setDob(selectedDateValue);
                    }
                })
                .setDoneText("OK")
                .setCancelText("CANCEL")
                .setThemeCustom(R.style.MyCustomBetterPickersDialogs)
                .setPreselectedDate(previousSelectedDateYear, previousSelectedDateMonth, previousSelectedDateDay)
                .setDateRange(new MonthAdapter.CalendarDay(todayDate.get(Calendar.YEAR) - 99, todayDate.get(Calendar.MONTH), todayDate.get(Calendar.DAY_OF_MONTH)),
                        new MonthAdapter.CalendarDay(todayDate.get(Calendar.YEAR) - MIN_YEAR_INTERVAL, todayDate.get(Calendar.MONTH), todayDate.get(Calendar.DAY_OF_MONTH)));
        if (context instanceof ProfileActivity) {
            cdp.show(((ProfileActivity) context).getSupportFragmentManager(), "fragment");
        }
    }

    private String formatDate(long millis) {
        if (millis <= 0) {
            return "";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return formatDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private String formatDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        selectedDateValue = calendar.getTimeInMillis();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
        return sdf.format(calendar.getTime());
    }

    private void updateProviderProfile() {
        //internet check
        if (CommonFunctions.getInstance().isOffline(context)) {
            toastMsg.setValue(context.getString(R.string.error_network_unavailable));
            CommonFunctions.getInstance().dismissProgressDialog();
            return;
        }
        apiServices.updateProviderProfile(context, tempUser, this);
    }

    private void updateUserProfile() {
        //internet check
        if (CommonFunctions.getInstance().isOffline(context)) {
            toastMsg.setValue(context.getString(R.string.error_network_unavailable));
            CommonFunctions.getInstance().dismissProgressDialog();
            return;
        }
        apiServices.updateUserProfile(context, tempUser, this);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        //remove all observable fields
        if (radioBtnId != null)
            radioBtnId = null;
        if (displayDateOfBirth != null)
            displayDateOfBirth = null;
        if (context != null)
            context = null;
    }

    //check customer is user or provider
    private boolean isProvider() {
        if (tempUser.getRoleName() == null) {
            return false;
        }
        return tempUser.getRoleName() == null || !tempUser.getRoleName().toLowerCase().equals("user");
    }

    @Override
    public void uploadSuccess(String imageUrl) {
        if (imageUrl != null) {
            tempUser.setProfileUrl(imageUrl);
        }
        saveUserData();
    }

    @Override
    public void uploadError(String exceptionMsg) {
        CommonFunctions.getInstance().dismissProgressDialog();
        toastMsg.setValue(exceptionMsg);
    }

    @Override
    public void onSuccess(Object response, int statusCode) {
        CommonFunctions.getInstance().dismissProgressDialog();
        toastMsg.setValue(((EmptyResponse) response).getMessage());
        updatePreferences();
    }

    private void updatePreferences() {
        SharedPrefsManager.getInstance().storeUserPreference(context, Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY, tempUser);
        (((ProfileActivity) context)).reOpenViewProfileFragment();
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