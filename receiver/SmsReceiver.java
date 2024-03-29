package app.zerobugz.fcms.ims.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import app.zerobugz.fcms.ims.appconfig.Forgot_Pass_Config;
import app.zerobugz.fcms.ims.helper.PrefManagerForgotpass;
import app.zerobugz.fcms.ims.service.HttpService;

/**
 * Created by Mohanraj on 06/04/2018.
 * SMS RECEIVER FROM OTP FORGET PASSWORD
 */
public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = SmsReceiver.class.getSimpleName();
    private PrefManagerForgotpass pref;

    @Override
    public void onReceive(Context context, Intent intent) {

        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String senderAddress = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    Log.e(TAG, "Received SMS: " + message + ", Sender: " + senderAddress);

                    // if the SMS is not from our gateway, ignore the message
                    if (!senderAddress.toLowerCase().contains(Forgot_Pass_Config.SMS_ORIGIN.toLowerCase())) {
                        return;
                    }

                    // verification code from sms
                    String verificationCode = getVerificationCode(message);

                    Log.e(TAG, "OTP received: " + verificationCode);

                    pref = new PrefManagerForgotpass(context);
                    String mobile_no = pref.getMobileNumber();

                    Intent hhtpIntent = new Intent(context, HttpService.class);
                    hhtpIntent.putExtra("otp", verificationCode);
                    hhtpIntent.putExtra("mobile_no", mobile_no);
                    context.startService(hhtpIntent);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Getting the OTP from sms message body
     * ':' is the separator of OTP from the message
     *
     * @param message
     * @return
     */
    private String getVerificationCode(String message) {
        String code = null;
        int index = message.indexOf(Forgot_Pass_Config.OTP_DELIMITER);

        if (index != -1) {
            int start = index + 1;
            int length = 6;
            code = message.substring(start, start + length);
            return code;
        }

        return code;
    }
}
