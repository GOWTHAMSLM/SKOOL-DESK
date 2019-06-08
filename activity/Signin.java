package app.zerobugz.fcms.ims.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.zerobugz.fcms.ims.Internetcheck;
import app.zerobugz.fcms.ims.appconfig.Config;
import app.zerobugz.fcms.ims.utils.Common;
import app.zerobugz.fcms.ims.utils.NotificationUtils;
import app.zerobugz.fcms.ims.R;

import static app.zerobugz.fcms.ims.utils.Common.rmv_dup_fcm_subs;

/**
 * Created by Mohanraj on 06/02/2018.
 */
public class Signin extends AppCompatActivity {

    private View parent_view;
    private EditText mobileedtxt, passedtxt;
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    private ProgressDialog progressDialog;

    private static final String TAG = Signin.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView txtRegId, txtMessage;

    //boolean variable to check user is logged in or not
    //initially it is false
    private boolean loggedIn = false;
    private String fcm_registration_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        txtRegId = (TextView) findViewById(R.id.txt_reg_id);
        txtMessage = (TextView) findViewById(R.id.txt_push_message);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    txtMessage.setText(message);
                }
            }
        };

       // displayFirebaseRegId();
        createNotificationChannel();

        parent_view = findViewById(android.R.id.content);
        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);

        mobileedtxt = (EditText) findViewById(R.id.mobile_no);
        passedtxt = (EditText) findViewById(R.id.pass_txt);

        ((View) findViewById(R.id.sign_in)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Common.haveNetworkConnection(Signin.this))
                {
                    //Calling the login function
                    login();
                }else {
                    Intent i = new Intent(Signin.this, Internetcheck.class);
                    startActivity(i);
                }
            }
        });


        //Forgot password section
        ((View) findViewById(R.id.forgot_password)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(parent_view, "Forgot Password", Snackbar.LENGTH_SHORT).show();

                Intent sa = new Intent(Signin.this, SmsActivity.class);
                startActivity(sa);
            }
        });
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_NAME, 0);
        String regId = pref.getString(Config.FCM_REGISTRATION_ID, null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            //txtRegId.setText("Firebase Reg Id: " + regId);
            Toast.makeText(getApplicationContext(), "Firebase Reg Id: " + regId, Toast.LENGTH_LONG).show();
        else
            //txtRegId.setText("Firebase Reg Id is not received yet!");
            Toast.makeText(getApplicationContext(), "Firebase Reg Id is not received yet!" , Toast.LENGTH_LONG).show();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channel_id = "skooldesknotify";
            String name = "channel_ids";
            String description = "welcome";

            int importance = NotificationManager.IMPORTANCE_HIGH;

           /* NotificationChannel channel = new NotificationChannel(channel_id, name, importance);
            channel.setDescription(description);*/
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            /*notificationManager.createNotificationChannel(channel);*/

            notificationManager.createNotificationChannel(new NotificationChannel(channel_id,
                    name, importance));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());

        //In onresume fetching value from sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        //Fetching the boolean value form sharedpreferences
        loggedIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);

        //If we will get true
        if(loggedIn){
            if (Common.haveNetworkConnection(Signin.this))
            {
                //We will start the Main Activity
                Intent intent = new Intent(Signin.this, Dashboard.class);
                startActivity(intent);
            }else {
                Intent i = new Intent(Signin.this, Internetcheck.class);
                startActivity(i);
            }
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }

    private void login(){
        //Getting values from edit texts
        final String authtype = "Parent";
        final String username = mobileedtxt.getText().toString().trim();
        final String password = passedtxt.getText().toString().trim();
        final String branchid = Config.BRANCH_SYS_ID_SHARED_PREF;

        String get_client_value = Common.getDeviceName();
        final String client = get_client_value.isEmpty() ? "UNKNOWN" : get_client_value;

        String get_version_value = Build.VERSION.RELEASE;
        final String agent = get_version_value.isEmpty() ? "UNKNOWN" : get_version_value;

        String get_ip_address = Common.getMACAddress("eth0");
        final String ip = get_ip_address.isEmpty() ? "192.168.1.100" : get_ip_address;

        progressDialog.setMessage("Please Wait!...");
        progressDialog.show();


        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responses)
                    {
                        progressDialog.dismiss();

                        try {
                            JSONObject responseObj = new JSONObject(responses);

                            // Parsing json object response
                            // response will be a json object
                            String res_status = responseObj.getString("response");

                            String res_value="success";

                            // checking for error, if not error SMS is initiated
                            // device should receive it shortly
                            if (res_value.equals(res_status)) {

                                //Creating a shared preference
                                SharedPreferences sharedPreferences = Signin.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                                //Creating editor to store values to shared preferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                ArrayList<String> cls_obj = new ArrayList<String>();
                                ArrayList<String> cls_sec_obj = new ArrayList<String>();
                                ArrayList<String> cls_grp_obj = new ArrayList<String>();
                                ArrayList<String> cls_grp_sec_obj = new ArrayList<String>();

                                    JSONObject success = responseObj.getJSONObject("success");
                                    JSONObject result = success.getJSONObject("result");
                                    JSONObject tokenresponse = result.getJSONObject("TokenResponse");
                                    JSONObject auth = tokenresponse.getJSONObject("auth");

                                    //Adding values to editor
                                    editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
                                    editor.putString(Config.TOKEN_SHARED_PREF, auth.getString("token"));

                                    JSONObject user_details = tokenresponse.getJSONObject("user");

                                    editor.putString(Config.USER_SYS_ID_SHARED_PREF, user_details.getString("usersysid"));
                                    editor.putString(Config.USER_NAME_SHARED_PREF, user_details.getString("username"));
                                    editor.putString(Config.MOBILE_SHARED_PREF, user_details.getString("usermobileno"));
                                    editor.putString(Config.BRANCH_SYS_ID_SHARED_PREF, user_details.getString("branchsysid"));
                                    editor.putString(Config.BRANCH_NAME_SHARED_PREF, user_details.getString("branchname"));


                                    JSONArray studentdetails = tokenresponse.getJSONArray("student");

                                    for (int i = 0; i < studentdetails.length(); i++) {
                                        try {

                                            JSONObject inner_obj = studentdetails.getJSONObject(i);

                                            editor.putString(Config.STUDENT_IMAGE_SHARED_PREF, inner_obj.getString("studentimage"));
                                            editor.putString(Config.STUDENT_NAME_SHARED_PREF, inner_obj.getString("studentname"));
                                            editor.putString(Config.CLASS_NAME_SHARED_PREF, inner_obj.getString("classname"));
                                            editor.putString(Config.SECTION_NAME_SHARED_PREF, inner_obj.getString("sectionname"));
                                            editor.putString(Config.STUDENT_SYS_ID_SHARED_PREF, inner_obj.getString("studentsysId"));
                                            editor.putString(Config.GROUP_NAME_SHARED_PREF, inner_obj.getString("groupname"));

                                        } catch (Exception e) {
                                            System.out.println(e.getMessage());

                                        }
                                    }


                                JSONArray subscriptionlist = tokenresponse.getJSONArray("subscriptionlist");
                                for (int j = 0; j < subscriptionlist.length(); j++) {
                                    try{
                                        JSONObject inner_list = subscriptionlist.getJSONObject(j);
                                        String grp = inner_list.getString("group");

                                        if(grp.equals("null")){
                                            String clss_name = inner_list.getString("classname");
                                            cls_obj.add(clss_name);
                                            String cls_sec_name = inner_list.getString("groupname");
                                            cls_sec_obj.add(cls_sec_name);

                                        }else{
                                            String clss_name = inner_list.getString("classname");
                                            String cls_sec_name = clss_name +"-"+ inner_list.getString("sectionname");
                                            String grp_name = inner_list.getString("groupname");
                                            String cls_grp = clss_name +"-"+ grp;
                                            cls_obj.add(clss_name);
                                            cls_sec_obj.add(cls_sec_name);
                                            cls_grp_obj.add(cls_grp);
                                            cls_grp_sec_obj.add(grp_name);
                                        }

                                    }catch (Exception e){
                                        System.out.println(e.getMessage());
                                    }
                                }

                                if(!cls_obj.isEmpty()){
                                    String result_class = rmv_dup_fcm_subs(cls_obj);
                                    editor.putString(Config.SUBSCRIBE_CLASS_NAME, result_class);
                                }

                                if(!cls_sec_obj.isEmpty()){
                                    String result_clss_sec = rmv_dup_fcm_subs(cls_sec_obj);
                                    editor.putString(Config.SUBSCRIBE_CLASS_SECTION_NAME, result_clss_sec);
                                }

                                if(!cls_grp_obj.isEmpty()){
                                    String result_clss_grp = rmv_dup_fcm_subs(cls_grp_obj);
                                    editor.putString(Config.SUBSCRIBE_CLASS_GROUP_NAME, result_clss_grp);
                                }

                                if(!cls_grp_sec_obj.isEmpty()){
                                    String result_clss_grp_sec = rmv_dup_fcm_subs(cls_grp_sec_obj);
                                    editor.putString(Config.SUBSCRIBE_CLASS_SECTION_GROUP_NAME, result_clss_grp_sec);
                                }

                                //Saving values to editor
                                editor.commit();

                                //  Launch application Dashboard screen
                                Intent i = new Intent(Signin.this, Dashboard.class);
                                startActivity(i);

                            }else{
                                //If the server response is not success
                                JSONObject failure = responseObj.getJSONObject("failure");
                                String message = failure.getString("message");
                                if(message.equals("Invalid username or password...!")){
                                    //Toast.makeText(signin.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                                    Snackbar.make(parent_view, message, Snackbar.LENGTH_SHORT).show();
                                }else {
                                    Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
                                }
                            }

                        } catch (JSONException e) {
                            //Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();
                            Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //You can handle error here if you want
                progressDialog.dismiss();
                Snackbar.make(parent_view, "Network Error! Try Again Later!!...", Snackbar.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
                params.put("username", username);
                params.put("password", password);
                params.put("authtype", authtype);
                params.put("branchid", branchid);
                params.put("client", client);
                params.put("agent", agent);
                params.put("ip", ip);
                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}

