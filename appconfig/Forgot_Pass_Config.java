package app.zerobugz.fcms.ims.appconfig;

/**
 * Created by Mohanraj on 06/04/2018.
 */

public class Forgot_Pass_Config {

    // server URL configuration
    public static final String URL_REQUEST_SMS = "http://api.skooldesk.co.in/api/LoginRelated/OTPGeneration";
    public static final String URL_VERIFY_OTP = "http://api.skooldesk.co.in/api/LoginRelated/OTPValidate";
    public static final String URL_PASS_UPD = "http://api.skooldesk.co.in/api/LoginRelated/UpdatePasswordByOTP";
    // SMS provider identification
    // It should match with your SMS gateway origin
    // You can use  MSGIND, TESTER and ALERTS as sender ID
    // If you want custom sender Id, approve MSG91 to get one
    public static final String SMS_ORIGIN = "SKDESK";

    // special character to prefix the otp. Make sure this character appears only once in the sms
    public static final String OTP_DELIMITER = ":";

}
