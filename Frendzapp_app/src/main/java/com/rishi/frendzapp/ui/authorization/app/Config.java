package com.rishi.frendzapp.ui.authorization.app;

public class Config {
    // server URL configuration
//    public static final String URL_REQUEST_SMS = "http://uniquefinds-online.com/Android/Frendzapp/request_sms.php";
//    public static final String URL_VERIFY_OTP = "http://uniquefinds-online.com/Android/Frendzapp/verify_otp.php";
    public static final String URL_REQUEST_SMS = "http://www.frendzapp.com/services/request_sms.php";
    public static final String URL_VERIFY_OTP = "http://www.frendzapp.com/services/verify_otp.php";
    public static final String URL_INVITE_FRIENDS = "http://www.frendzapp.com/services/invite_friends.php";

    public static final String SMS_ORIGIN = "FRENDZ";
    // special character to prefix the otp. Make sure this character appears only once in the sms
    public static final String OTP_DELIMITER = ":";
}
