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
import android.widget.AdapterView;

import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.CprInstitute;
import com.biz4solutions.models.Occupation;
import com.biz4solutions.models.ProviderRegistration;
import com.biz4solutions.models.User;
import com.biz4solutions.models.response.CprTrainingInstitutesResponse;
import com.biz4solutions.models.response.EmptyResponse;
import com.biz4solutions.models.response.OccupationResponse;
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
    private final ProviderRegistration registration;
    private User user;
    private Uri profileImageUri;
    private Uri cprCertificateUri;
    private Uri medicalCertificateUri;
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
    private Long selectedBirthDateValue = null;


    private RegistrationViewModel(Context context) {
        super();
        this.context = context;
        occupationLiveList = new MutableLiveData<>();
        cprInstituteLiveList = new MutableLiveData<>();
        registration = new ProviderRegistration();
        apiServices = new ApiServices();
        toastMsg = new MutableLiveData<>();
        email = new MutableLiveData<>();
        expiry = new ObservableField<>();
        displayDateOfBirth = new ObservableField<>();
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
                CommonFunctions.getInstance().dismissProgressDialog();
                OccupationResponse response1 = (OccupationResponse) response;
                occupationLiveList.setValue(response1.getData());
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
                CommonFunctions.getInstance().dismissProgressDialog();
                CprTrainingInstitutesResponse response1 = (CprTrainingInstitutesResponse) response;
                cprInstituteLiveList.setValue(response1.getData());
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

    //spinner item select listener
    public void onOccupationSelectItem(@SuppressWarnings("unused") AdapterView<?> parent, @SuppressWarnings("unused") View view, int pos, @SuppressWarnings("unused") long id) {
        //Toast.makeText(view.getContext(), "" + occupationLiveList.getValue().get(pos), Toast.LENGTH_SHORT).show();
//        registration.setProfessionName(occupationLiveList.get().get(pos));
    }

    //spinner item select listener
    public void onCprInstituteSelectItem(@SuppressWarnings("unused") AdapterView<?> parent, @SuppressWarnings("unused") View view, int pos, @SuppressWarnings("unused") long id) {
        //Toast.makeText(view.getContext(), "" + occupationLiveList.getValue().get(pos), Toast.LENGTH_SHORT).show();
//        registration.setCprTrainingInstitution(cprInstituteLiveList.get().get(pos));
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
        registration.setProfessionName(s.toString().trim());
    }

    //watcher for institute name
    public void instituteNameWatcher(CharSequence s, int start, int before, int count) {
        registration.setInstituteName(s.toString().trim());
    }

    //medical license section

    //watcher for institute name
    public void licenseNpeNumberWatcher(CharSequence s, int start, int before, int count) {
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


    public void selectBirthDate() {

        Calendar previousSelectedDate = Calendar.getInstance();
        int previousSelectedDateYear, previousSelectedDateMonth, previousSelectedDateDay;
        if (selectedBirthDateValue != null) {
            previousSelectedDate.setTimeInMillis(selectedBirthDateValue);
        } else {
            previousSelectedDate.setTimeInMillis(todayDate.getTimeInMillis());
        }
        previousSelectedDateYear = previousSelectedDate.get(Calendar.YEAR);
        previousSelectedDateMonth = previousSelectedDate.get(Calendar.MONTH);
        previousSelectedDateDay = previousSelectedDate.get(Calendar.DAY_OF_MONTH);
        int MIN_YEAR_INTERVAL = 18;


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
                            CommonFunctions.getInstance().loadProgressDialog(context);
                            FirebaseUploadUtil.uploadImageToFirebase(user.getUserId(), user.getRoleName(), profileImageUri, this);
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
        @SuppressLint("simpledateformat") SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
        return sdf.format(calendar.getTime());
    }

    private boolean validatePersonalData() {
        if (profileImageUri == null) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_empty_profile));
            return false;
        } else if (registration.getFirstName() == null || registration.getFirstName().isEmpty()) {
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
        if (registration.getProfessionName() == null || registration.getProfessionName().isEmpty()) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_unselected_occupation));
            return false;
        } else if (registration.getProfessionName() != null && registration.getProfessionName().equalsIgnoreCase("others") && registration.getProfessionName().isEmpty()) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_empty_occupation));
            return false;
        } else if (registration.getInstituteName() == null || registration.getInstituteName().isEmpty()) {
            toastMsg.setValue(context.getString(com.biz4solutions.profile.R.string.error_empty_institute_name));
            return false;
        }
        return true;
    }

    private boolean validateCprData() {
        if (registration.getCprTrainingInstitution() == null || registration.getCprTrainingInstitution().isEmpty()) {
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
        int profileImageFileCode = 101;
        int cprFileCode = 102;
        int medicalFileCode = 103;

        if (fileCode == profileImageFileCode) {
            registration.setProfileUrl(imageUrl);
            CommonFunctions.getInstance().loadProgressDialog(context);
            FirebaseUploadUtil.uploadMultipleFileToFirebase(user.getUserId(), "cprCertificate" + cprFileExt, cprCertificateUri, cprFileCode, this);
        } else if (fileCode == cprFileCode) {
            CommonFunctions.getInstance().loadProgressDialog(context);
            registration.setCprCertificateLink(imageUrl);
            FirebaseUploadUtil.uploadMultipleFileToFirebase(user.getUserId(), "medicalCertificate" + medicalFileExt, medicalCertificateUri, medicalFileCode, this);
        } else if (fileCode == medicalFileCode) {
            CommonFunctions.getInstance().loadProgressDialog(context);
            registration.setMedicalLiceneceLink(imageUrl);
            //upload data to server
            registerProvider();
        }
    }

    @Override
    public void uploadError(String exceptionMsg, int fileCode) {
        toastMsg.setValue(exceptionMsg);
        CommonFunctions.getInstance().dismissProgressDialog();
    }

    @Override
    public void onSuccess(Object response, int statusCode) {
        CommonFunctions.getInstance().dismissProgressDialog();
        toastMsg.setValue(((EmptyResponse) response).getMessage());
        ((MainActivity) context).reOpenDashBoardFragment();
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
        cprInstituteLiveList = null;
        occupationLiveList = null;
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
