package com.biz4solutions.opentok.sdk;

import android.webkit.URLUtil;

public class OpenTokConfig {
    // *** Fill the following variables using your own Project info from the OpenTok dashboard  ***
    // ***                      https://dashboard.tokbox.com/projects                           ***

    // Replace with your OpenTok API key
    public static final String API_KEY = "46169442";
    // Replace with a generated Session ID
    //public static final String SESSION_ID = "1_MX40NjE2OTQ0Mn5-MTUzNDQwMTQyODA2OH4wOWpyK1IxTlk0KzBrOVoyZG5lTldQVWV-fg";
    // Replace with a generated token (from the dashboard or using an OpenTok server SDK)
    //public static final String TOKEN = "T1==cGFydG5lcl9pZD00NjE2OTQ0MiZzaWc9OTI4ODcxN2RlMjdjY2QzNmFjYjhhODk4MWM1YWQwZjE0ZTNlY2UxNzpzZXNzaW9uX2lkPTFfTVg0ME5qRTJPVFEwTW41LU1UVXpORFF3TVRReU9EQTJPSDR3T1dweUsxSXhUbGswS3pCck9Wb3laRzVsVGxkUVZXVi1mZyZjcmVhdGVfdGltZT0xNTM0NDAxNTA1Jm5vbmNlPTAuNTg3Mzg1Njg2MzM4MzE2MiZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNTM0NDIzMTAzJmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";

    /*                           ***** OPTIONAL *****
     If you have set up a server to provide session information replace the null value
     in CHAT_SERVER_URL with it.

     For example: "https://yoursubdomain.com"
    */
    public static final String CHAT_SERVER_URL = null;//"https://yoursubdomain.com";
    public static final String SESSION_INFO_ENDPOINT = CHAT_SERVER_URL + "/session";


    // *** The code below is to validate this configuration file. You do not need to modify it  ***

    public static String webServerConfigErrorMessage;
    public static String hardCodedConfigErrorMessage;

    public static boolean isWebServerConfigUrlValid(){
        if (OpenTokConfig.CHAT_SERVER_URL == null || OpenTokConfig.CHAT_SERVER_URL.isEmpty()) {
            webServerConfigErrorMessage = "CHAT_SERVER_URL in OpenTokConfig.java must not be null or empty";
            return false;
        } else if ( !( URLUtil.isHttpsUrl(OpenTokConfig.CHAT_SERVER_URL) || URLUtil.isHttpUrl(OpenTokConfig.CHAT_SERVER_URL)) ) {
            webServerConfigErrorMessage = "CHAT_SERVER_URL in OpenTokConfig.java must be specified as either http or https";
            return false;
        } else if ( !URLUtil.isValidUrl(OpenTokConfig.CHAT_SERVER_URL) ) {
            webServerConfigErrorMessage = "CHAT_SERVER_URL in OpenTokConfig.java is not a valid URL";
            return false;
        } else {
            return true;
        }
    }
}
