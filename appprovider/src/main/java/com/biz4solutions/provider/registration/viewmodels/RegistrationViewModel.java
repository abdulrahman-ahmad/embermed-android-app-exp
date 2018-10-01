package com.biz4solutions.provider.registration.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.biz4solutions.models.ProviderRegistration;

import java.util.ArrayList;

public class RegistrationViewModel extends ViewModel {
    public final MutableLiveData<ArrayList<String>> occupationLiveList;
    private final ProviderRegistration registration;
    private Uri profileImageUri;
    private Uri cprCertificateUri;
    private Uri medicalCertificateUri;

    public void setProfileImageUri(Uri profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    public void setCprCertificateUri(Uri cprCertificateUri) {
        this.cprCertificateUri = cprCertificateUri;
    }

    public void setMedicalCertificateUri(Uri medicalCertificateUri) {
        this.medicalCertificateUri = medicalCertificateUri;
    }

    private RegistrationViewModel() {
        super();
        occupationLiveList = new MutableLiveData<>();
        registration = new ProviderRegistration();
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("doctor");
        arrayList.add("raju");
        occupationLiveList.setValue(arrayList);
    }

    //spinner item select listener
    public void onOccupationSelectItem(@SuppressWarnings("unused") AdapterView<?> parent, @SuppressWarnings("unused") View view, int pos, @SuppressWarnings("unused") long id) {
        Toast.makeText(view.getContext(), "" + occupationLiveList.getValue().get(pos), Toast.LENGTH_SHORT).show();
    }
    //watchers

    //watcher for firstName
    public void firstNameWatcher(CharSequence s, int start, int before, int count) {
        registration.setFirstName(s.toString());
    }

    //watcher for lastName
    public void lastNameWatcher(CharSequence s, int start, int before, int count) {
        registration.setLastName(s.toString());
    }


    //watcher for phone number
    public void phoneNumberWatcher(CharSequence s, int start, int before, int count) {
        registration.setPhoneNumber(s.toString());
    }

    //watcher for address
    public void addressWatcher(CharSequence s, int start, int before, int count) {
        registration.setAddress(s.toString());
    }

    //profession-section

    //watcher for occupation
    public void occupationWatcher(CharSequence s, int start, int before, int count) {
        registration.setProfessionName(s.toString());
    }

    //watcher for institute name
    public void instituteNameWatcher(CharSequence s, int start, int before, int count) {
        registration.setInstituteName(s.toString());
    }

    //medical license section

    //watcher for institute name
    public void licenseNpeNumberWatcher(CharSequence s, int start, int before, int count) {
        registration.setMedicalLicenseNumber(s.toString());
    }

    //watcher for speciality
    public void specialityWatcher(CharSequence s, int start, int before, int count) {
        registration.setSpeciality(s.toString());
    }

    //watcher for state
    public void stateWatcher(CharSequence s, int start, int before, int count) {
        registration.setPracticeState(s.toString());
    }


    //for switch button
    public void onSwitchChanged(boolean checked) {
        registration.setOptForTriage(checked);
    }

    //submit click
    public void onSubmitClick(View v) {

    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }


    @SuppressWarnings("unchecked")
    public static class RegistrationFactory extends ViewModelProvider.NewInstanceFactory {

        public RegistrationFactory() {
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new RegistrationViewModel();
        }
    }
}
