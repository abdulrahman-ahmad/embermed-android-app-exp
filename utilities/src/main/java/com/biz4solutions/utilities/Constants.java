package com.biz4solutions.utilities;

import java.io.Serializable;

public class Constants implements Serializable {
    public static final String SEND_LOG_EMAIL_ADDRESS1 = "ketan@biz4solutions.com";

    public static final String GOOGLE_MAP_URL = "https://maps.googleapis.com/maps/";

    public static final String FILE_DIRECTORY = "Ember";
    public static final String DOCS_DIRECTORY = "Ember_Documents";
    public static final String SUPPORT_DIRECTORY = "Support";
    public static final String SUPPORT_FILE_NAME = "SupportLog.txt";
    public static final String SUPPORT_LOG_EXTRA_SUBJECT = "Ember Support Log file";
    public static final String SUPPORT_LOG_EXTRA_TEXT = "Log file attached.";

    public static final int SPLASH_TIMEOUT = 1200;
    public static final int SEND_LOG_EMAIL = 100;
    public static final int UNAUTHORIZED_ERROR_CODE = 401;
    public static final int BLOCK_ERROR_CODE = 410;
    public static final int DISTANCE_API_DElAY = 30000; // 30 sec

    public static final String USER_PREFERENCE = "USER_PREFERENCE";
    public static final String USER_PREFERENCE_KEY = "USER_PREFERENCE_KEY";
    public static final String USER_AUTH_KEY = "USER_AUTH_KEY";
    //    public static final String USER_CURRENT_REQUEST_ID_KEY = "USER_CURRENT_REQUEST_ID_KEY";
    public static final String ROLE_NAME = "ROLE_NAME";
    public static final String ROLE_NAME_USER = "USER";
    public static final String ROLE_NAME_PROVIDER = "PROVIDER";
    public static final String LOGOUT_RECEIVER = "LOGOUT_RECEIVER";
    public static final String LOGOUT_MESSAGE = "message";

    public static final String FIREBASE_PROVIDER_LOCATION_TABLE = "ProviderLocation";
    public static final String FIREBASE_USER_TABLE = "User";
    public static final String FIREBASE_SUBSCRIPTION_TABLE = "Subscription";
    public static final String FIREBASE_REQUEST_TABLE = "Request";
    public static final String FIREBASE_ALERT_TABLE = "Alert";
    public static final String FIREBASE_OPEN_TOK_TABLE = "OpenTok";
    public static final String FIREBASE_IS_PATIENT_FEEDBACK_SUBMITTED_KEY = "isPatientFeedbackSubmitted";
    public static final String FIREBASE_TRIAGE_CALL_STATUS_KEY = "triageCallStatus";

    public static final String EMBER_MEDICS_CHANNEL_ID = "200";
    public static final String EMBER_MEDICS_CARDIAC_CHANNEL_ID = "201";
    public static final String EMBER_CHANNEL_ID = "202";

    public static final String NOTIFICATION_ACTION_VIEW = "com.ember.medics.EMBER_MEDICS_NOTIFICATION_ACTION_VIEW";
    public static final String PATIENT_NOTIFICATION_ACTION_VIEW = "com.ember.patient.EMBER_MEDICS_NOTIFICATION_ACTION_VIEW";
    public static final String LOCAL_NOTIFICATION_ACTION_VIEW = "com.ember.medics.EMBER_MEDICS_LOCAL_NOTIFICATION_ACTION_VIEW";
    public static final String NOTIFICATION_REQUEST_ID_KEY = "NOTIFICATION_REQUEST_ID_KEY";
    public static final String NOTIFICATION_ID_KEY = "NOTIFICATION_ID_KEY";

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_IMMEDIATE = "IMMEDIATE";
    public static final String STATUS_HIGH = "HIGH";
    public static final String STATUS_ACCEPTED = "ACCEPTED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_END = "END";
    public static final String STATUS_START = "START";

    public static final String TRIAGE_FEEDBACK_ER = "ER";
    public static final String TRIAGE_FEEDBACK_URGENT_CARE = "URGENT_CARE";
    public static final String TRIAGE_FEEDBACK_PCP = "PCP";

    public static final String DATE_FORMAT = "dd MMM, yyyy";
    public static final String TIME_FORMAT = "hh:mm a";

    public static final String EMAIL_REGEX_SIGNUP = "^(\\s*|([A-Z0-9a-z](([A-Z0-9a-z]|\\.(?!\\.))|([A-Z0-9a-z]|\\_(?!\\_))){0,100}+@[A-Z0-9a-z.-]+\\.[A-Za-z]{2,4})+)$";
    public static final int MINIMUM_CLUSTER_SIZE = 2;


    public static String BOUNDS_COUNTRY = "US";
    public static int AUTO_COMPLETE_THRESHOLD = 1;

    public static final String SIGN_UP_TYPE_EMAIL = "EMAIL";
    static final String SIGN_UP_TYPE_FB = "FB";
    static final String SIGN_UP_TYPE_GP = "GP";
    public static final String SELECT_OCCUPATION = "Select Occupation";
    public static final String SELECT_INSTITUTE = "Select Institute";

    public static String CPR_FILE_NAME = "CprCertificate";
    public static String MEDICAL_FILE_NAME = "MedicalCertificate";


}