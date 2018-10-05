package com.biz4solutions.models;

import com.biz4solutions.models.response.EmptyResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProviderRegistration extends EmptyResponse {

    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("cprCertificateLink")
    @Expose
    private String cprCertificateLink;
    @SerializedName("cprExpiryDate")
    @Expose
    private long cprExpiryDate;
    @SerializedName("cprTrainingInstitution")
    @Expose
    private String cprTrainingInstitution;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("instituteName")
    @Expose
    private String instituteName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("medicalLiceneceLink")
    @Expose
    private String medicalLiceneceLink;
    @SerializedName("medicalLicenseNumber")
    @Expose
    private String medicalLicenseNumber;
    @SerializedName("optForTriage")
    @Expose
    private Boolean optForTriage;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("practiceState")
    @Expose
    private String practiceState;
    @SerializedName("professionName")
    @Expose
    private String professionName;
    @SerializedName("speciality")
    @Expose
    private String speciality;
    @SerializedName("profileUrl")
    @Expose
    private String profileUrl;
    @SerializedName("dob")
    @Expose
    private Long dob;
    @SerializedName("gender")
    @Expose
    private String gender;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Long getDob() {
        return dob;
    }

    public void setDob(Long dob) {
        this.dob = dob;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCprCertificateLink() {
        return cprCertificateLink;
    }

    public void setCprCertificateLink(String cprCertificateLink) {
        this.cprCertificateLink = cprCertificateLink;
    }

    public long getCprExpiryDate() {
        return cprExpiryDate;
    }

    public void setCprExpiryDate(long cprExpiryDate) {
        this.cprExpiryDate = cprExpiryDate;
    }

    public String getCprTrainingInstitution() {
        return cprTrainingInstitution;
    }

    public void setCprTrainingInstitution(String cprTrainingInstitution) {
        this.cprTrainingInstitution = cprTrainingInstitution;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getInstituteName() {
        return instituteName;
    }

    public void setInstituteName(String instituteName) {
        this.instituteName = instituteName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMedicalLiceneceLink() {
        return medicalLiceneceLink;
    }

    public void setMedicalLiceneceLink(String medicalLiceneceLink) {
        this.medicalLiceneceLink = medicalLiceneceLink;
    }

    public String getMedicalLicenseNumber() {
        return medicalLicenseNumber;
    }

    public void setMedicalLicenseNumber(String medicalLicenseNumber) {
        this.medicalLicenseNumber = medicalLicenseNumber;
    }

    public Boolean getOptForTriage() {
        return optForTriage;
    }

    public void setOptForTriage(Boolean optForTriage) {
        this.optForTriage = optForTriage;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPracticeState() {
        return practiceState;
    }

    public void setPracticeState(String practiceState) {
        this.practiceState = practiceState;
    }

    public String getProfessionName() {
        return professionName;
    }

    public void setProfessionName(String professionName) {
        this.professionName = professionName;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    @Override
    public String toString() {
        return "ProviderRegistration{" +
                "address='" + address + '\'' +
                ", cprCertificateLink='" + cprCertificateLink + '\'' +
                ", cprExpiryDate='" + cprExpiryDate + '\'' +
                ", cprTrainingInstitution='" + cprTrainingInstitution + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", instituteName='" + instituteName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", medicalLiceneceLink='" + medicalLiceneceLink + '\'' +
                ", medicalLicenseNumber='" + medicalLicenseNumber + '\'' +
                ", optForTriage=" + optForTriage +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", practiceState='" + practiceState + '\'' +
                ", professionName='" + professionName + '\'' +
                ", speciality='" + speciality + '\'' +
                '}';
    }
}
