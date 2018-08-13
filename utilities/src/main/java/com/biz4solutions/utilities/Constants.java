package com.biz4solutions.utilities;

import java.io.Serializable;

public class Constants implements Serializable {
    public static final String SEND_LOG_EMAIL_ADDRESS1 = "ketan@biz4solutions.com";

    public static final String FILE_DIRECTORY = "OsirisBio";
    public static final String SUPPORT_DIRECTORY = "Support";
    public static final String SUPPORT_FILE_NAME = "SupportLog.txt";
    public static final String SUPPORT_LOG_EXTRA_SUBJECT = "Osiris Bio Support Log file";
    public static final String SUPPORT_LOG_EXTRA_TEXT = "Log file attached.";

    public static final int SPLASH_TIMEOUT = 1200;
    public static final int SEND_LOG_EMAIL = 100;

    public static final String USER_PREFERENCE = "USER_PREFERENCE";
    public static final String USER_PREFERENCE_KEY = "USER_PREFERENCE_KEY";
    public static final String USER_AUTH_KEY = "USER_AUTH_KEY";
    public static final String USER_CURRENT_REQUEST_ID_KEY = "USER_CURRENT_REQUEST_ID_KEY";
    public static final String ROLE_NAME = "ROLE_NAME";
    public static final String ROLE_NAME_USER = "USER";
    public static final String ROLE_NAME_PROVIDER = "PROVIDER";

    public static final String FIREBASE_PATIENT_LOCATION_TABLE = "PatientLocation";
    public static final String FIREBASE_USER_TABLE = "User";

    public static final String EMAIL_REGEX_SIGNUP = "^(\\s*|([A-Z0-9a-z](([A-Z0-9a-z]|\\.(?!\\.))|([A-Z0-9a-z]|\\_(?!\\_))){0,100}+@[A-Z0-9a-z.-]+\\.[A-Za-z]{2,4})+)$";

}