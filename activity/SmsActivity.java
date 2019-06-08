package app.zerobugz.fcms.ims.activity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.zerobugz.fcms.ims.Internetcheck;
import app.zerobugz.fcms.ims.appconfig.Config;
import app.zerobugz.fcms.ims.appconfig.Forgot_Pass_Config;
import app.zerobugz.fcms.ims.appconfig.MyApplication;
import app.zerobugz.fcms.ims.helper.PrefManagerForgotpass;
import app.zerobugz.fcms.ims.service.HttpService;
import app.zerobugz.fcms.ims.utils.Common;
import app.zerobugz.fcms.ims.R;

/**
 * Created by Mohanraj on 06/04/2018.
 */

public class SmsActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static String TAG = SmsActivity.class.getSimpleName();

    private View parent_view;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private Button btnRequestSms, btnVerifyOtp;
    private EditText inputMobile, inputOtp;
    private TextView go_back;
    private ProgressBar progressBar;
    private PrefManagerForgotpass pref;
    private ImageButton btnEditMobile;
    private TextView txtEditMobile;
    private LinearLayout layoutEditMobile;

    private static final int PERMISSION_REQUEST_SMS = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        parent_view = findViewById(android.R.id.content);

        viewPager = (ViewPager) findViewById(R.id.viewPagerVertical);

        inputMobile = (EditText) findViewById(R.id.inputMobile);
        inputOtp = (EditText) findViewById(R.id.inputOtp);
        go_back = (TextView) findViewById(R.id.go_back);

        btnRequestSms = (Button) findViewById(R.id.btn_request_sms);
        btnVerifyOtp = (Button) findViewById(R.id.btn_verify_otp);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnEditMobile = (ImageButton) findViewById(R.id.btn_edit_mobile);
        txtEditMobile = (TextView) findViewById(R.id.txt_edit_mobile);
        layoutEditMobile = (LinearLayout) findViewById(R.id.layout_edit_mobile);

        // view click listeners
        btnEditMobile.setOnClickListener(this);
        go_back.setOnClickListener(this);
        btnRequestSms.setOnClickListener(this);
        btnVerifyOtp.setOnClickListener(this);

        // hiding the edit mobile number
        layoutEditMobile.setVisibility(View.GONE);

        pref = new PrefManagerForgotpass(this);

        // Checking for user session
        // if user is already logged in, take him to main activity
        if (pref.isLoggedIn()) {
            Intent intent = new Intent(SmsActivity.this, Reset_password.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        /**
         * Checking if the device is waiting for sms
         * showing the user OTP screen
         */
        if (pref.isWaitingForSms()) {
            viewPager.setCurrentItem(1);
            layoutEditMobile.setVisibility(View.VISIBLE);
            txtEditMobile.setText(pref.getMobileNumber());
        }

        showsmsPreview();

    }

    // BEGIN SMS PERMISSION REQUEST SECTION
    private void showsmsPreview() {
        // BEGIN_INCLUDE(startSMS)
        // Check if the Camera permission has been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start SMS Activity
            Toast.makeText(getApplicationContext(), "SMS PERMISSION GRANTED!...", Toast.LENGTH_SHORT).show();

        } else {
            // Permission is missing and must be requested.
            requestsmsPermission();
        }
        // END_INCLUDE(startSMS)
    }

    private void requestsmsPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_SMS)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            // Request the permission
            ActivityCompat.requestPermissions(SmsActivity.this,
                    new String[]{Manifest.permission.READ_SMS},
                    PERMISSION_REQUEST_SMS);

        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS}, PERMISSION_REQUEST_SMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_SMS) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                Snackbar.make(parent_view, "SMS PERMISSION GRANTED!...", Snackbar.LENGTH_SHORT).show();
            } else {
                // Permission request was denied.
                Snackbar.make(parent_view, "SMS PERMISSION REQUEST WAS DENIED!...", Snackbar.LENGTH_SHORT).show();
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }

    // END SMS PERMISSION REQUEST SECTION

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_request_sms:
                if (Common.haveNetworkConnection(SmsActivity.this))
                {
                    validateForm();
                    break;
                }else {
                    Intent i = new Intent(SmsActivity.this, Internetcheck.class);
                    startActivity(i);
                }

            case R.id.btn_verify_otp:
                if (Common.haveNetworkConnection(SmsActivity.this))
                {
                    verifyOtp();
                    break;
                }else {
                    Intent i = new Intent(SmsActivity.this, Internetcheck.class);
                    startActivity(i);
                }

            case R.id.btn_edit_mobile:
                viewPager.setCurrentItem(0);
                layoutEditMobile.setVisibility(View.GONE);
                pref.setIsWaitingForSms(false);
                break;

            case R.id.go_back:
                Intent i = new Intent(SmsActivity.this, Signin.class);
                startActivity(i);
                finish();
        }
    }

    /**
     * Validating user details form
     */
    private void validateForm() {
        String mobile = inputMobile.getText().toString().trim();

        // validating mobile number
        // it should be of 10 digits length
        if (isValidPhoneNumber(mobile)) {

            // request for sms
            progressBar.setVisibility(View.VISIBLE);

            // saving the mobile number in shared preferences
            pref.setMobileNumber(mobile);

            // requesting for sms
            requestForSMS(mobile);

        } else {
            Toast.makeText(getApplicationContext(), "Please enter valid mobile number", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method initiates the SMS request on the server
     *
     * @param mobile user valid mobile number
     */
    private void requestForSMS(final String mobile) {

        final String branchsysid = Config.BRANCH_SYS_ID_SHARED_PREF;

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Forgot_Pass_Config.URL_REQUEST_SMS, new Response.Listener<String>() {

            @Override
            public void onResponse(String responses) {
                Log.d(TAG, responses.toString());

                try {
                    JSONObject responseObj = new JSONObject(responses);

                    // Parsing json object response
                    // response will be a json object
                    String res_status = responseObj.getString("response");
                    String res_value="success";

                    // checking for error, if not error SMS is initiated
                    // device should receive it shortly
                    if (res_value.equals(res_status)) {

                        JSONObject success = responseObj.getJSONObject("success");
                        String message = success.getString("message");

                        // boolean flag saying device is waiting for sms
                        pref.setIsWaitingForSms(true);

                        // moving the screen to next pager item i.e otp screen
                        viewPager.setCurrentItem(1);
                        txtEditMobile.setText(pref.getMobileNumber());
                        layoutEditMobile.setVisibility(View.VISIBLE);

                        //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        Snackbar.make(parent_view, message, Snackbar.LENGTH_SHORT).show();

                    } else {
                        JSONObject failure = responseObj.getJSONObject("failure");
                        String message = failure.getString("message");
                        if(message.equals("Invalid MobileNo...!")){
                            Snackbar.make(parent_view,"Invalid Mobile Number! Try Another Number...", Snackbar.LENGTH_SHORT).show();
                        }else {
                            Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    // hiding the progress bar
                    progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),
                            "Error! Try Again Later!!",
                            Toast.LENGTH_LONG).show();

                    progressBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Error! Try Again Later!!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }) {

            /**
             * Passing user parameters to our server
             * @return
             */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("branchsysid", branchsysid);
                params.put("mobileno", mobile);

                Log.e(TAG, "Posting params: " + params.toString());

                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    /**
     * sending the OTP to server and activating the user
     */
    private void verifyOtp() {
        String otp = inputOtp.getText().toString().trim();
        String mobile_no = pref.getMobileNumber();

        if (!otp.isEmpty()) {
            Intent grapprIntent = new Intent(getApplicationContext(), HttpService.class);
            grapprIntent.putExtra("otp", otp);
            grapprIntent.putExtra("mobile_no", mobile_no);
            startService(grapprIntent);
        } else {
            Toast.makeText(getApplicationContext(), "Please enter the OTP", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Regex to validate the mobile number
     * mobile number should be of 10 digits length
     *
     * @param mobile
     * @return
     */
    private static boolean isValidPhoneNumber(String mobile) {
        String regEx = "^[0-9]{10}$";
        return mobile.matches(regEx);
    }


    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        public Object instantiateItem(View collection, int position) {

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.layout_sms;
                    break;
                case 1:
                    resId = R.id.layout_otp;
                    break;
            }
            return findViewById(resId);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        pref.clearSession();
        Intent intent = new Intent(SmsActivity.this, Signin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
