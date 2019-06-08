package app.zerobugz.fcms.ims.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.zerobugz.fcms.ims.Internetcheck;
import app.zerobugz.fcms.ims.appconfig.Config;
import app.zerobugz.fcms.ims.appconfig.MyApplication;
import app.zerobugz.fcms.ims.bottomstudentlist.MyStudentBottomListAdapter;
import app.zerobugz.fcms.ims.bottomstudentlist.StudentListFeeds;
import app.zerobugz.fcms.ims.messages.NetworkController;
import app.zerobugz.fcms.ims.utils.Common;
import app.zerobugz.fcms.ims.R;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static app.zerobugz.fcms.ims.utils.Common.fcm_unsubs;

/**
 * Created by Mohanraj on 06/04/2018.
 */

public class Dashboard extends AppCompatActivity {

    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;

    MaterialTapTargetPrompt mFabPrompt;
    public boolean isFirstFabStart;

    private TextView stuname, stu_class;
    private ImageView stu_image;

    boolean doubleBackToExitPressedOnce = false;
    private String fcm_registration_id;
    private boolean send_registration_to_server = false;
    private View parent_view;

    RequestQueue queue;
    private RecyclerView recyclerView;
    List<StudentListFeeds> feedsList = new ArrayList<StudentListFeeds>();
    private MyStudentBottomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        parent_view = findViewById(android.R.id.content);
        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);

        stuname = (TextView) findViewById(R.id.stu_name);
        stu_class = (TextView) findViewById(R.id.stu_class);
        stu_image = (ImageView) findViewById(R.id.stu_image);

        //Load image from url
        /*String url = "http://192.168.1.100:8096/App_Upload/User_Img/avatar.png";
        Glide.with(this).load(url).into(stu_image);*/

        final SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String student_name = sharedPreferences.getString(Config.STUDENT_NAME_SHARED_PREF, "Not Available");
        String group_name = sharedPreferences.getString(Config.GROUP_NAME_SHARED_PREF, "Not Available");
        String image_url = sharedPreferences.getString(Config.STUDENT_IMAGE_SHARED_PREF, "Not Available");
        /*subscribe_status = sharedPreferences.getBoolean(Config.SUBSCRIBE_STATUS, false);

        if(!subscribe_status){

            FirebaseMessaging.getInstance().subscribeToTopic(group_name)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Subscribe success means
                                //Getting editor
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                //Puting the value false for loggedin
                                editor.putBoolean(Config.SUBSCRIBE_STATUS, true);
                                //Saving the sharedpreferences
                                editor.commit();

                            }
                        }
                    });
        }*/

        stuname.setText(student_name);
        stu_class.setText(group_name);

        //Student Image Set Section
        if(image_url.equals("M-NULL")){
            stu_image.setImageResource(R.drawable.student_male_avator);
        }else if(image_url.equals("F-NULL")){
            stu_image.setImageResource(R.drawable.student_female_avator);
        }else{
            Glide.with(this).load(image_url).into(stu_image);
        }


        ((View) findViewById(R.id.home_work)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Common.haveNetworkConnection(Dashboard.this))
                {
                    //  Launch application Homework screen
                    Intent i = new Intent(Dashboard.this, Homework.class);
                    startActivity(i);
                }else {
                    Intent i = new Intent(Dashboard.this, Internetcheck.class);
                    startActivity(i);
                }
            }
        });

        ((View) findViewById(R.id.attendance)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Common.haveNetworkConnection(Dashboard.this))
                {
                    //  Launch application Attendance screen
                    Intent i = new Intent(Dashboard.this, Attendance.class);
                    startActivity(i);
                }else {
                    Intent i = new Intent(Dashboard.this, Internetcheck.class);
                    startActivity(i);
                }
            }
        });

        ((View) findViewById(R.id.messages)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Common.haveNetworkConnection(Dashboard.this))
                {
                    //  Launch application Messages screen
                    Intent i = new Intent(Dashboard.this, Messages.class);
                    startActivity(i);
                }else {
                    Intent i = new Intent(Dashboard.this, Internetcheck.class);
                    startActivity(i);
                }
            }
        });

        ((View) findViewById(R.id.event_gallery)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Common.haveNetworkConnection(Dashboard.this))
                {
                    //  Launch application EventGallery screen
                    Intent i = new Intent(Dashboard.this, EventGallery.class);
                    startActivity(i);
                }else {
                    Intent i = new Intent(Dashboard.this, Internetcheck.class);
                    startActivity(i);
                }
            }
        });

        ((View) findViewById(R.id.marks)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Common.haveNetworkConnection(Dashboard.this))
                {
                    //  Launch application Marks screen
                    Intent i = new Intent(Dashboard.this, Marks.class);
                    startActivity(i);
                }else {
                    Intent i = new Intent(Dashboard.this, Internetcheck.class);
                    startActivity(i);
                }
            }
        });

        ((View) findViewById(R.id.profile)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Common.haveNetworkConnection(Dashboard.this))
                {
                    //  Launch application Profile screen
                    Intent i = new Intent(Dashboard.this, Profile.class);
                    startActivity(i);
                }else {
                    Intent i = new Intent(Dashboard.this, Internetcheck.class);
                    startActivity(i);
                }
            }
        });

        ((View) findViewById(R.id.time_table)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Common.haveNetworkConnection(Dashboard.this))
                {
                    //  Launch application Profile screen
                    Intent i = new Intent(Dashboard.this, Timetable.class);
                    startActivity(i);
                }else {
                    Intent i = new Intent(Dashboard.this, Internetcheck.class);
                    startActivity(i);
                }
            }
        });

        initToolbar();

        // Get Student Details by FAB
        initcomponent();
        // Material Tap Target Prompt Method call
        showFabPrompt();
        // Sending Firebase Device register id to your server
        fcm_registration_id = sharedPreferences.getString(Config.FCM_REGISTRATION_ID, "Not Available");
        if(!fcm_registration_id.equals("Not Available")){
            sendRegistrationToServer(fcm_registration_id);
        }
    }

    private void sendRegistrationToServer(String token) {
        // sending gcm token to server
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        send_registration_to_server = sharedPreferences.getBoolean(Config.SEND_REGISTRATION_TO_SERVER, false);

        if(!send_registration_to_server){
            sendRegistrationid(token);
        }

    }

    /* Send Registration Id to Server Method */
    public void sendRegistrationid(String token) {

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String parentsysid = sharedPreferences.getString(Config.USER_SYS_ID_SHARED_PREF, "Not Available");
        final String token_key = "Bearer " + sharedPreferences.getString(Config.TOKEN_SHARED_PREF, "Not Available");

        final String deviceid = token;

        final String branchsysid = Config.BRANCH_SYS_ID_SHARED_PREF;


        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Config.INSERT_DEVICE_ID_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responses) {

                        try {
                            JSONObject responseObj = new JSONObject(responses);
                            // Parsing json object response
                            // response will be a json object
                            String res_status = responseObj.getString("response");
                            String res_value="success";

                            // Value of Response Key Validation
                            if (res_value.equals(res_status)) {

                                JSONObject success = responseObj.getJSONObject("success");
                                String message = success.getString("message");
                                if (message.equals("Device Id Inserted...!")){
                                    SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME,Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putBoolean(Config.SEND_REGISTRATION_TO_SERVER, true);
                                    editor.commit();
                                }

                            }else{
                                Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", token_key);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
                params.put("parentsysid", parentsysid);
                params.put("deviceid", deviceid);
                params.put("branchsysid", branchsysid);
                //returning parameter
                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(stringRequest);
    }

    // Show Material Tap Target Prompt
    public void showFabPrompt()
    {
        //  Intro App Initialize SharedPreferences
        SharedPreferences getSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        //  Create a new boolean and preference and set it to true
        isFirstFabStart = getSharedPreferences.getBoolean("firstFabStart", true);

        if (isFirstFabStart){

            mFabPrompt = new MaterialTapTargetPrompt.Builder(Dashboard.this)
                    .setTarget(findViewById(R.id.fab_add))
                    .setPrimaryText("Choose Your Children Here")
                    .setBackgroundColour(Color.parseColor("#FF7043"))
                    .setSecondaryText("Click to choose your children")
                    .setAnimationInterpolator(new FastOutSlowInInterpolator())
                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                    {
                        @Override
                        public void onPromptStateChanged(@NonNull MaterialTapTargetPrompt prompt, int state)
                        {
                            if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED
                                    || state == MaterialTapTargetPrompt.STATE_DISMISSING)
                            {
                                mFabPrompt = null;
                                //Do something such as storing a value so that this prompt is never shown again
                            }
                        }
                    })
                    .create();
            mFabPrompt.show();
            SharedPreferences.Editor e = getSharedPreferences.edit();
            e.putBoolean("firstFabStart", false);
            e.apply();
        }

    }

    // FAB Action Method
    public void initcomponent(){
        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

        ((FloatingActionButton) findViewById(R.id.fab_add)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showBottomSheetDialog(v);
            }

        });
    }

    //Show Student Bottom Sheet Method
    private void showBottomSheetDialog(View v) {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.student_sheet_list, null);

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        //Initialize RecyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.recyc_stu_view);
        adapter = new MyStudentBottomListAdapter(this, feedsList, mBottomSheetDialog, stuname, stu_class, stu_image);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);

        //Getting Instance of Volley Request Queue
        queue = NetworkController.getInstance(this).getRequestQueue();

        //boolean result_response = studentlist();
        studentlist(new VolleyCallback(){
            @Override
            public void onSuccess(String result){
                if (result.equals("success")) {
                    mBottomSheetDialog.show();
                }
            }
        });

        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
    }

    public interface VolleyCallback{
        void onSuccess(String result);
    }

    /* Call Load Student List Method */
    private void studentlist(final VolleyCallback callback) {

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String parensysid = sharedPreferences.getString(Config.USER_SYS_ID_SHARED_PREF, "Not Available");
        final String token_key = "Bearer " + sharedPreferences.getString(Config.TOKEN_SHARED_PREF, "Not Available");
        final String branchsysid = Config.BRANCH_SYS_ID_SHARED_PREF;

        //progressBar.setVisibility(View.VISIBLE);
        progressDialog.setMessage("Please Wait!...");
        progressDialog.show();

        //Creating a string request
        StringRequest newsReq = new StringRequest(Request.Method.POST, Config.GET_STUDENT_DETAILS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responses)
                    {
                        //progressBar.setVisibility(View.INVISIBLE);
                        progressDialog.dismiss();

                        try {
                            JSONObject responseObj = new JSONObject(responses);
                            // Parsing json object response
                            // response will be a json object
                            String res_status = responseObj.getString("response");

                            String res_value="success";

                            // Value of Response Key Validation
                            if (res_value.equals(res_status)) {
                                feedsList.clear();
                                recyclerView.setAdapter(adapter);
                                JSONObject success = responseObj.getJSONObject("success");
                                JSONObject result = success.getJSONObject("result");
                                JSONArray studentdetails = result.getJSONArray("studentdetails");

                                for(int i=0; i < studentdetails.length(); i++){
                                    try{
                                        JSONObject obj = studentdetails.getJSONObject(i);
                                        StudentListFeeds feeds = new StudentListFeeds(obj.getString("studentsysId"), obj.getString("studentname"), obj.getString("classname"), obj.getString("sectionname"), obj.getString("groupname"), obj.getString("studentimage"));
                                        feedsList.add(feeds);

                                    }catch (Exception e){
                                        System.out.println(e.getMessage());
                                    } finally {
                                        adapter.notifyItemChanged(i);
                                    }
                                }

                                callback.onSuccess(res_value);

                            }else{
                                //If the server response is not success
                                Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
                                callback.onSuccess("failure");
                            }

                        } catch (JSONException e) {
                            Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //You can handle error here if you want
                //progressBar.setVisibility(View.INVISIBLE);
                progressDialog.dismiss();
                Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", token_key);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
                params.put("branchsysid", branchsysid);
                params.put("parensysid", parensysid);

                //returning parameter
                return params;
            }
        };

        queue.add(newsReq);
    }

    // initializing Toolbar
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Dashboard");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logout){
            //Toast.makeText(getApplicationContext(),item.getTitle(),Toast.LENGTH_SHORT).show();
            //calling logout method when the logout button is clicked
            if (Common.haveNetworkConnection(Dashboard.this))
            {
                logout();
            }else {
                Intent i = new Intent(Dashboard.this, Internetcheck.class);
                startActivity(i);
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        doExitApp();
    }

    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "Press once again to exit!", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    //Logout function
    private void logout(){
        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to logout?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        if (Common.haveNetworkConnection(Dashboard.this))
                        {
                            SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                            send_registration_to_server = sharedPreferences.getBoolean(Config.SEND_REGISTRATION_TO_SERVER, false);
                            if(send_registration_to_server){
                                DeleteDeviceId();
                            }else{
                                    if(RmvSharPrefVal()){
                                        //Starting login activity
                                        Intent intent = new Intent(Dashboard.this, Signin.class);
                                        startActivity(intent);
                                        finish();
                                    }
                            }

                        }else {
                            Intent i = new Intent(Dashboard.this, Internetcheck.class);
                            startActivity(i);
                        }

                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        //Showing the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    /* Send Registration Id to Server Delete Method */
    private void DeleteDeviceId(){
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String parentsysid = sharedPreferences.getString(Config.USER_SYS_ID_SHARED_PREF, "Not Available");
        final String token_key = "Bearer " + sharedPreferences.getString(Config.TOKEN_SHARED_PREF, "Not Available");

        final String deviceid = sharedPreferences.getString(Config.FCM_REGISTRATION_ID, "Not Available");
        //final String deviceid = "34343434343999";

        final String branchsysid = Config.BRANCH_SYS_ID_SHARED_PREF;


        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Config.DELETE_DEVICE_ID_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responses) {

                        try {
                            JSONObject responseObj = new JSONObject(responses);
                            // Parsing json object response
                            // response will be a json object
                            String res_status = responseObj.getString("response");
                            String res_value="success";

                            // Value of Response Key Validation
                            if (res_value.equals(res_status)) {

                                JSONObject success = responseObj.getJSONObject("success");
                                String message = success.getString("message");
                                if (message.equals("Device Id Deleted...!")){
                                    if(RmvSharPrefVal()){
                                        //Starting login activity
                                        Intent intent = new Intent(Dashboard.this, Signin.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                            }else{
                                Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", token_key);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
                params.put("parentsysid", parentsysid);
                params.put("deviceid", deviceid);
                params.put("branchsysid", branchsysid);
                //returning parameter
                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(stringRequest);
    }

    /*Remove All Shared Preference Value from Config*/
    private boolean RmvSharPrefVal()
    {
            //Getting out sharedpreferences
            SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME,Context.MODE_PRIVATE);
            String group_name = preferences.getString(Config.GROUP_NAME_SHARED_PREF, "Not Available");

            String clss_name = preferences.getString(Config.SUBSCRIBE_CLASS_NAME, "Not Available");
            String clss_sec_name = preferences.getString(Config.SUBSCRIBE_CLASS_SECTION_NAME, "Not Available");
            String clss_grp_name = preferences.getString(Config.SUBSCRIBE_CLASS_GROUP_NAME, "Not Available");
            String clss_grp_sec_name = preferences.getString(Config.SUBSCRIBE_CLASS_SECTION_GROUP_NAME, "Not Available");

            if(!clss_name.equals("Not Available") && !clss_name.isEmpty()){
                boolean result_fcm = fcm_unsubs(clss_name);
            }

            if(!clss_sec_name.equals("Not Available") && !clss_sec_name.isEmpty()){
                boolean result_fcm = fcm_unsubs(clss_sec_name);
            }

            if(!clss_grp_name.equals("Not Available") && !clss_grp_name.isEmpty()){
                boolean result_fcm = fcm_unsubs(clss_grp_name);
            }

            if(!clss_grp_sec_name.equals("Not Available")&& !clss_grp_sec_name.isEmpty()){
                boolean result_fcm = fcm_unsubs(clss_grp_sec_name);
            }


            //Getting editor
            SharedPreferences.Editor editor = preferences.edit();

            //Puting the value false for loggedin
            editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);
            editor.putString(Config.TOKEN_SHARED_PREF, "");

            editor.putString(Config.USER_SYS_ID_SHARED_PREF, "");
            editor.putString(Config.USER_NAME_SHARED_PREF, "");
            editor.putString(Config.MOBILE_SHARED_PREF, "");
            editor.putString(Config.BRANCH_SYS_ID_SHARED_PREF, "");
            editor.putString(Config.BRANCH_NAME_SHARED_PREF, "");

            editor.putString(Config.STUDENT_IMAGE_SHARED_PREF, "");
            editor.putString(Config.STUDENT_NAME_SHARED_PREF, "");
            editor.putString(Config.CLASS_NAME_SHARED_PREF, "");
            editor.putString(Config.SECTION_NAME_SHARED_PREF, "");
            editor.putString(Config.STUDENT_SYS_ID_SHARED_PREF, "");
            editor.putString(Config.GROUP_NAME_SHARED_PREF, "");

            editor.putBoolean(Config.SUBSCRIBE_STATUS, false);
            editor.putString(Config.SUBSCRIBE_CLASS_NAME, "");
            editor.putString(Config.SUBSCRIBE_CLASS_SECTION_NAME, "");
            editor.putString(Config.SUBSCRIBE_CLASS_GROUP_NAME, "");
            editor.putString(Config.SUBSCRIBE_CLASS_SECTION_GROUP_NAME, "");
            editor.putBoolean(Config.SEND_REGISTRATION_TO_SERVER, false);

            //Saving the sharedpreferences
            editor.commit();

            return true;
    }

}
