package app.zerobugz.fcms.ims.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.zerobugz.fcms.ims.activity.Reset_password;
import app.zerobugz.fcms.ims.appconfig.Config;
import app.zerobugz.fcms.ims.appconfig.Forgot_Pass_Config;
import app.zerobugz.fcms.ims.appconfig.MyApplication;
import app.zerobugz.fcms.ims.helper.PrefManagerForgotpass;

/**
 * Created by Mohanraj on 06/04/2018.
 * HTTP SERVICE FOR VERIFY OTP FROM SERVER SIDE
 */
public class HttpService extends IntentService {

    private static String TAG = HttpService.class.getSimpleName();

    public HttpService() {
        super(HttpService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String otp = intent.getStringExtra("otp");
            String mobile_no = intent.getStringExtra("mobile_no");
            verifyOtp(otp, mobile_no);
        }
    }

    /**
     * Posting the OTP to server and activating the user
     *
     * @param otp otp received in the SMS
     */
    private void verifyOtp(final String otp, final String mobile_no) {

        final String branchsysid = Config.BRANCH_SYS_ID_SHARED_PREF;

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Forgot_Pass_Config.URL_VERIFY_OTP, new Response.Listener<String>() {

            @Override
            public void onResponse(String responses) {
                Log.d(TAG, responses.toString());

                try {

                    JSONObject responseObj = new JSONObject(responses);

                    // Parsing json object response
                    // response will be a json object
                    String res_status = responseObj.getString("response");
                    String res_value="success";

                    if (res_value.equals(res_status)) {
                        // parsing the user profile information

                        JSONObject success = responseObj.getJSONObject("success");
                        String message = success.getString("message");

                        PrefManagerForgotpass pref = new PrefManagerForgotpass(getApplicationContext());
                        pref.createLogin(mobile_no);

                        Intent intent = new Intent(HttpService.this, Reset_password.class);
                        intent.putExtra("mob1", mobile_no);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                    } else {
                        JSONObject failure = responseObj.getJSONObject("failure");
                        String message = failure.getString("message");
                        if(message.equals("Invalid OTP...!")){
                            Toast.makeText(getApplicationContext(), "Invalid OTP Number!...", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(getApplicationContext(), "Something went wrong! Try again later!!...", Toast.LENGTH_LONG).show();
                        }

                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),
                            "Error! Try Again Later!!",
                            Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Error! Try Again Later!!", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("currotp", otp);
                params.put("mobileno", mobile_no);
                params.put("branchsysid", branchsysid);

                Log.e(TAG, "Posting params: " + params.toString());
                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

}
