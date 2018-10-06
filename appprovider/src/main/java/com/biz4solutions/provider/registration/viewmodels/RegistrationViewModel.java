package com.biz4solutions.provider.registration.viewmodels;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.ObservableField;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.interfaces.DialogDismissCallBackListener;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.CprInstitute;
import com.biz4solutions.models.Occupation;
import com.biz4solutions.models.ProviderRegistration;
import com.biz4solutions.models.User;
import com.biz4solutions.models.response.CprTrainingInstitutesResponse;
import com.biz4solutions.models.response.EmptyResponse;
import com.biz4solutions.models.response.OccupationResponse;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.provider.R;
import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;
import com.biz4solutions.utilities.FirebaseUploadUtil;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class RegistrationViewModel extends ViewModel implements FirebaseUploadUtil.FirebaseUploadInterface, RestClientResponse {
    private MutableLiveData<ArrayList<Occupation>> occupationLiveList;
    private MutableLiveData<ArrayList<CprInstitute>> cprInstituteLiveList;
    public ObservableField<Integer> radioBtnId;
    private final ProviderRegistration registration;
    private User user;
    private Uri profileImageUri;
    private Uri cprCertificateUri;
    private Uri medicalCertificateUri;
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private ApiServices apiServices;
    private MutableLiveData<String> toastMsg;
    public MutableLiveData<String> email;
    private String cprFileExt;
    private String medicalFileExt;
    private Long selectedDateValue = null;
    private Calendar todayDate = Calendar.getInstance();
    public ObservableField<String> expiry;
    public ObservableField<String> displayDateOfBirth;
    public ObservableField<String> firstName;
    public ObservableField<String> lastName;
    private Long selectedBirthDateValue = null;
    private String tempOccupation;
    private String tempEdtOccupation;
    private static final int profileImageFileCode = 101;
    private static final int cprFileCode = 102;
    private static final int medicalFileCode = 103;

    private RegistrationViewModel(Context context) {
        super();
        this.context = context;
        occupationLiveList = new MutableLiveData<>();
        cprInstituteLiveList = new MutableLiveData<>();
        radioBtnId = new ObservableField<>();
        registration = new ProviderRegistration();
        toastMsg = new MutableLiveData<>();
        email = new MutableLiveData<>();
        expiry = new ObservableField<>();
        displayDateOfBirth = new ObservableField<>();
        firstName = new ObservableField<>();
        lastName = new ObservableField<>();
        apiServices = new ApiServices();
        getCprListData();
        getOccupationListData();
    }

    private void getOccupationListData() {
        if (CommonFunctions.getInstance().isOffline(context)) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_network_unavailable));
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(context);
        apiServices.getOccupationList(context, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                try {
                    CommonFunctions.getInstance().dismissProgressDialog();
                    OccupationResponse response1 = (OccupationResponse) response;
                    if (occupationLiveList != null) {
                        occupationLiveList.setValue(response1.getData());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                toastMsg.setValue(errorMessage);
            }
        });
    }

    public void setOccupation(String occupation) {
        registration.setProfessionName(occupation);
        tempOccupation = occupation;
    }

    public void setCprInstitute(String cprInstitute) {
        registration.setCprTrainingInstitution(cprInstitute);
    }

    public MutableLiveData<ArrayList<Occupation>> getOccupationLiveList() {
        return occupationLiveList;
    }

    public MutableLiveData<ArrayList<CprInstitute>> getCprInstituteLiveList() {
        return cprInstituteLiveList;
    }

    private void getCprListData() {
        if (CommonFunctions.getInstance().isOffline(context)) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_network_unavailable));
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(context);
        apiServices.getCprInstituteList(context, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                try {
                    CommonFunctions.getInstance().dismissProgressDialog();
                    CprTrainingInstitutesResponse response1 = (CprTrainingInstitutesResponse) response;
                    if (cprInstituteLiveList != null) {
                        cprInstituteLiveList.setValue(response1.getData());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                toastMsg.setValue(errorMessage);
            }
        });
    }

    public Uri getCprCertificateUri() {
        return cprCertificateUri;
    }

    public Uri getMedicalCertificateUri() {
        return medicalCertificateUri;
    }

    public void setCprFileExt(String cprFileExt) {
        this.cprFileExt = cprFileExt;
    }

    public void setMedicalFileExt(String medicalFileExt) {
        this.medicalFileExt = medicalFileExt;
    }

    public LiveData<String> getToastMsg() {
        return toastMsg;
    }

    public void setUser(User user) {
        if (user != null) {
            this.user = user;
            email.setValue(user.getEmail());
            firstName.set(user.getFirstName());
            lastName.set(user.getLastName());
            registration.setFirstName(user.getFirstName());
            registration.setLastName(user.getLastName());
            registration.setEmail(user.getEmail());
        }
    }

    public void setProfileImageUri(Uri profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    public void setCprCertificateUri(Uri cprCertificateUri) {
        this.cprCertificateUri = cprCertificateUri;
    }

    public void setMedicalCertificateUri(Uri medicalCertificateUri) {
        this.medicalCertificateUri = medicalCertificateUri;
    }

    //watcher for firstName
    public void firstNameWatcher(CharSequence s, int start, int before, int count) {
        registration.setFirstName(s.toString().trim());
    }

    //watcher for lastName
    public void lastNameWatcher(CharSequence s, int start, int before, int count) {
        registration.setLastName(s.toString().trim());
    }

    //watcher for phone number
    public void phoneNumberWatcher(CharSequence s, int start, int before, int count) {
        registration.setPhoneNumber(s.toString().trim());
    }

    public void setAddress(String address) {
        registration.setAddress(address.trim());
    }

    //profession-section
    //watcher for occupation
    public void occupationWatcher(CharSequence s, int start, int before, int count) {
        if (tempOccupation != null && tempOccupation.equalsIgnoreCase("other")) {
            registration.setProfessionName(s.toString().trim());
            tempEdtOccupation = s.toString().trim();
        }
    }

    //watcher for institute name
    public void instituteNameWatcher(CharSequence s, int start, int before, int count) {
        registration.setInstituteName(s.toString().trim());
    }

    //medical license section
    //watcher for institute name
    public void licenseNpiNumberWatcher(CharSequence s, int start, int before, int count) {
        registration.setMedicalLicenseNumber(s.toString().toUpperCase().trim());
    }

    //watcher for speciality
    public void specialityWatcher(CharSequence s, int start, int before, int count) {
        registration.setSpeciality(s.toString().trim());
    }

    //watcher for state
    public void stateWatcher(CharSequence s, int start, int before, int count) {
        registration.setPracticeState(s.toString().trim());
    }

    //for switch button
    public void onSwitchChanged(boolean checked) {
        registration.setOptForTriage(checked);
    }

    //listener for radio btn
    public void onRadioBtnChanged(RadioGroup radioGroup, int id) {
        if (radioGroup.findViewById(id) != null) {
            registration.setGender(((RadioButton) radioGroup.findViewById(id)).getText().toString());
        }
    }

    public void selectBirthDate() {

        int MIN_YEAR_INTERVAL = 18;

        Calendar previousSelectedDate = Calendar.getInstance();
        int previousSelectedDateYear, previousSelectedDateMonth, previousSelectedDateDay;
        if (selectedBirthDateValue != null) {
            previousSelectedDate.setTimeInMillis(selectedBirthDateValue);
            previousSelectedDateYear = previousSelectedDate.get(Calendar.YEAR);
        } else {
            previousSelectedDate.setTimeInMillis(todayDate.getTimeInMillis());
            previousSelectedDateYear = previousSelectedDate.get(Calendar.YEAR) - MIN_YEAR_INTERVAL;
        }
        previousSelectedDateMonth = previousSelectedDate.get(Calendar.MONTH);
        previousSelectedDateDay = previousSelectedDate.get(Calendar.DAY_OF_MONTH);

        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                    @Override
                    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                        displayDateOfBirth.set(formatDate(year, monthOfYear, dayOfMonth, true));
                        registration.setDob(selectedBirthDateValue);
                    }
                })
                .setDoneText("OK")
                .setCancelText("CANCEL")
                .setThemeCustom(com.biz4solutions.profile.R.style.MyCustomBetterPickersDialogs)
                .setPreselectedDate(previousSelectedDateYear, previousSelectedDateMonth, previousSelectedDateDay)
                .setDateRange(new MonthAdapter.CalendarDay(todayDate.get(Calendar.YEAR) - 99, todayDate.get(Calendar.MONTH), todayDate.get(Calendar.DAY_OF_MONTH)),
                        new MonthAdapter.CalendarDay(todayDate.get(Calendar.YEAR) - MIN_YEAR_INTERVAL, todayDate.get(Calendar.MONTH), todayDate.get(Calendar.DAY_OF_MONTH)));

        cdp.show(((MainActivity) context).getSupportFragmentManager(), "fragment");
    }

    //expiry date
    public void selectDate() {

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

        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                    @Override
                    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                        expiry.set(formatDate(year, monthOfYear, dayOfMonth, false));
                        registration.setCprExpiryDate(selectedDateValue);
                    }
                })
                .setDoneText("OK")
                .setCancelText("CANCEL")
                .setThemeCustom(com.biz4solutions.profile.R.style.MyCustomBetterPickersDialogs)
                .setPreselectedDate(previousSelectedDateYear, previousSelectedDateMonth, previousSelectedDateDay)
                .setDateRange(new MonthAdapter.CalendarDay(todayDate.get(Calendar.YEAR), todayDate.get(Calendar.MONTH), todayDate.get(Calendar.DAY_OF_MONTH) + 1),
                        new MonthAdapter.CalendarDay(todayDate.get(Calendar.YEAR) + 99, todayDate.get(Calendar.MONTH), todayDate.get(Calendar.DAY_OF_MONTH)));

        cdp.show(((MainActivity) context).getSupportFragmentManager(), "fragment");

    }

    //submit click
    public void onSubmitClick(View v) {
        CommonFunctions.getInstance().hideSoftKeyBoard(((MainActivity) v.getContext()));
        if (CommonFunctions.getInstance().isOffline(context)) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_network_unavailable));
            return;
        }
        //validate data
        if (validatePersonalData()) {
            if (validateProfessionData()) {
                if (validateCprData()) {
                    if (validateMedicalData()) {
                        if (validateOtherData()) {
                            CommonFunctions.getInstance().showAlertDialog(v.getContext(), R.string.submit_registration, R.string.yes, R.string.no, new DialogDismissCallBackListener<Boolean>() {
                                @Override
                                public void onClose(Boolean result) {
                                    if (result) {
                                        CommonFunctions.getInstance().loadProgressDialog(context);
                                        if (profileImageUri != null) {
                                            FirebaseUploadUtil.uploadImageToFirebase(user.getUserId(), profileImageUri, RegistrationViewModel.this);
                                        } else {
                                            FirebaseUploadUtil.uploadMultipleFileToFirebase(user.getUserId(), Constants.CPR_FILE_NAME + cprFileExt, cprCertificateUri, cprFileCode, RegistrationViewModel.this);
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    private void registerProvider() {
        CommonFunctions.getInstance().loadProgressDialog(context);
        apiServices.registerProvider(context, registration, this);
    }

    private String formatDate(int year, int month, int day, boolean isBirthDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        if (!isBirthDate)
            selectedDateValue = calendar.getTimeInMillis();
        else
            selectedBirthDateValue = calendar.getTimeInMillis();
        @SuppressLint("simpledateformat")
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
        return sdf.format(calendar.getTime());
    }

    private boolean validatePersonalData() {
        /*if (profileImageUri == null) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_empty_profile));
            return false;
        } else */
        if (registration.getFirstName() == null || registration.getFirstName().isEmpty()) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_empty_first_name));
            return false;
        } else if (registration.getLastName() == null || registration.getLastName().isEmpty()) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_empty_last_name));
            return false;
        } else if (registration.getDob() == null) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_empty_dob));
            return false;
        } else if (registration.getDob() > Calendar.getInstance().getTimeInMillis()) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_invalid_dob));
            return false;
        } else if (registration.getGender() == null || registration.getGender().isEmpty()) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_empty_gender));
            return false;
        } else if (registration.getPhoneNumber() == null || registration.getPhoneNumber().isEmpty()) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_empty_phone));
            return false;
        } else if (registration.getPhoneNumber() != null && registration.getPhoneNumber().length() < 7) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_invalid_phone));
            return false;
        }
        return true;
    }

    private boolean validateProfessionData() {
        if (tempOccupation == null || tempOccupation.isEmpty() || tempOccupation.equalsIgnoreCase(Constants.SELECT_OCCUPATION)) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_unselected_occupation));
            return false;
        } else if ((tempEdtOccupation == null || tempEdtOccupation.isEmpty()) && (tempOccupation.equalsIgnoreCase("other") || tempOccupation.equalsIgnoreCase("others"))) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_empty_occupation));
            return false;
        } else if (registration.getInstituteName() == null || registration.getInstituteName().isEmpty()) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_empty_institute_name));
            return false;
        }
        return true;
    }

    private boolean validateCprData() {
        if (registration.getCprTrainingInstitution() == null || registration.getCprTrainingInstitution().isEmpty() || registration.getCprTrainingInstitution().equalsIgnoreCase(Constants.SELECT_INSTITUTE)) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_unselected_cpr_institute));
            return false;
        } else if (registration.getCprExpiryDate() == 0) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_unselected_cpr_expiry));
            return false;
        } else if (cprCertificateUri == null) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_unselected_cpr_certificate));
            return false;
        }
        return true;
    }

    private boolean validateMedicalData() {
        if (registration.getMedicalLicenseNumber() == null || registration.getMedicalLicenseNumber().isEmpty()) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_empty_license_npi_number));
            return false;
        } else if (medicalCertificateUri == null) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_unselected_medical_certificate));
            return false;
        }
        return true;
    }

    private boolean validateOtherData() {
        if (registration.getSpeciality() == null || registration.getSpeciality().isEmpty()) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_empty_speciality));
            return false;
        } else if (registration.getPracticeState() == null || registration.getPracticeState().isEmpty()) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_empty_state));
            return false;
        }
        return true;
    }

    @Override
    public void uploadSuccess(String imageUrl, int fileCode) {
        CommonFunctions.getInstance().dismissProgressDialog();
        if (fileCode == profileImageFileCode) {
            registration.setProfileUrl(imageUrl);
            CommonFunctions.getInstance().loadProgressDialog(context);
            FirebaseUploadUtil.uploadMultipleFileToFirebase(user.getUserId(), Constants.CPR_FILE_NAME + cprFileExt, cprCertificateUri, cprFileCode, this);
        } else if (fileCode == cprFileCode) {
            CommonFunctions.getInstance().loadProgressDialog(context);
            registration.setCprCertificateLink(imageUrl);
            FirebaseUploadUtil.uploadMultipleFileToFirebase(user.getUserId(), Constants.MEDICAL_FILE_NAME + medicalFileExt, medicalCertificateUri, medicalFileCode, this);
        } else if (fileCode == medicalFileCode) {
            CommonFunctions.getInstance().loadProgressDialog(context);
            registration.setMedicalLicenseLink(imageUrl);
            //upload data to server
            registerProvider();
        }
    }

    @Override
    public void uploadError(String exceptionMsg, int fileCode) {
        toastMsg.setValue(exceptionMsg);
        CommonFunctions.getInstance().dismissProgressDialog();
        if (fileCode == profileImageFileCode) {
            CommonFunctions.getInstance().loadProgressDialog(context);
            FirebaseUploadUtil.uploadMultipleFileToFirebase(user.getUserId(), Constants.CPR_FILE_NAME + cprFileExt, cprCertificateUri, cprFileCode, this);
        }
    }

    @Override
    public void onSuccess(Object response, int statusCode) {
        user.setFirstName(registration.getFirstName());
        user.setLastName(registration.getLastName());
        user.setGender(registration.getGender());
        user.setDob(registration.getDob());
        user.setProfileUrl(registration.getProfileUrl());
        SharedPrefsManager.getInstance().storeUserPreference(context, Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY, user);
        CommonFunctions.getInstance().dismissProgressDialog();
        toastMsg.setValue(((EmptyResponse) response).getMessage());
        ((MainActivity) context).reOpenNewsFeedFragment();
    }

    @Override
    public void onFailure(String errorMessage, int statusCode) {
        CommonFunctions.getInstance().dismissProgressDialog();
        toastMsg.setValue(errorMessage);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        context = null;
        if (cprInstituteLiveList != null)
            cprInstituteLiveList = null;
        if (occupationLiveList != null)
            occupationLiveList = null;
        if (radioBtnId != null)
            radioBtnId = null;
    }

    @SuppressWarnings("unchecked")
    public static class RegistrationFactory extends ViewModelProvider.NewInstanceFactory {

        private Context context;

        public RegistrationFactory(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new RegistrationViewModel(context);
        }
    }
}