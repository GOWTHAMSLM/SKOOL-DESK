package app.zerobugz.fcms.ims.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.zerobugz.fcms.ims.Internetcheck;
import app.zerobugz.fcms.ims.Profile.MyStudentProfileAdapter;
import app.zerobugz.fcms.ims.Profile.StudentFeeds;
import app.zerobugz.fcms.ims.appconfig.Config;
import app.zerobugz.fcms.ims.appconfig.MyApplication;
import app.zerobugz.fcms.ims.messages.NetworkController;
import app.zerobugz.fcms.ims.utils.Common;
import app.zerobugz.fcms.ims.R;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    RequestQueue queue;
    private RecyclerView recyclerView;
    List<StudentFeeds> feedsList = new ArrayList<StudentFeeds>();
    private MyStudentProfileAdapter adapter;

    private View parent_view;
    private TextView usr_name, usr_mobile;
    private EditText newpass, curpass;
    private Button pass_update;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        parent_view = findViewById(android.R.id.content);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String user_name = sharedPreferences.getString(Config.USER_NAME_SHARED_PREF, "Not Available");
        final String user_mobile = sharedPreferences.getString(Config.MOBILE_SHARED_PREF, "Not Available");

        usr_name = (TextView) findViewById(R.id.usr_name);
        usr_mobile = (TextView) findViewById(R.id.usr_mobile);

        if(user_name.equals("null")){
            usr_name.setText("");
        }else{
            usr_name.setText(user_name);
        }

        usr_mobile.setText(user_mobile);

        //Initialize RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recycview);
        adapter = new MyStudentProfileAdapter(this, feedsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);
        //Getting Instance of Volley Request Queue
        queue = NetworkController.getInstance(this).getRequestQueue();

        initToolbar();
        studentlist();
        curpass = (EditText) findViewById(R.id.current_password);
        newpass = (EditText) findViewById(R.id.new_password);
        pass_update = (Button) findViewById(R.id.pass_update);

        progressDialog = new ProgressDialog(this);

        pass_update.setOnClickListener(this);

    }

    /* Call Load Student List Method */
    private void studentlist() {

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String parensysid = sharedPreferences.getString(Config.USER_SYS_ID_SHARED_PREF, "Not Available");
        final String token_key = "Bearer " + sharedPreferences.getString(Config.TOKEN_SHARED_PREF, "Not Available");
        final String branchsysid = Config.BRANCH_SYS_ID_SHARED_PREF;

        //Creating a string request
        StringRequest newsReq = new StringRequest(Request.Method.POST, Config.GET_STUDENT_DETAILS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responses)
                    {

                        try {
                            JSONObject responseObj = new JSONObject(responses);

                            // Parsing json object response
                            // response will be a json object
                            String res_status = responseObj.getString("response");

                            String res_value="success";

                            // Value of Response Key Validation
                            if (res_value.equals(res_status)) {
                                JSONObject success = responseObj.getJSONObject("success");
                                JSONObject result = success.getJSONObject("result");
                                JSONArray studentdetails = result.getJSONArray("studentdetails");

                                for(int i=0; i < studentdetails.length(); i++){
                                    try{
                                        JSONObject obj = studentdetails.getJSONObject(i);
                                        StudentFeeds feeds = new StudentFeeds(obj.getString("studentsysId"), obj.getString("studentname"), obj.getString("groupname"), obj.getString("studentimage"));

                                        feedsList.add(feeds);

                                    }catch (Exception e){
                                        System.out.println(e.getMessage());
                                    } finally {
                                        adapter.notifyItemChanged(i);
                                    }
                                }

                            }else{
                                //If the server response is not success
                                Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //You can handle error here if you want
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

    /* Call Update password Method */
    public void updatepass() {

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String parentsysid = sharedPreferences.getString(Config.USER_SYS_ID_SHARED_PREF, "Not Available");
        final String token_key = "Bearer " + sharedPreferences.getString(Config.TOKEN_SHARED_PREF, "Not Available");

        final String currpassword = curpass.getText().toString().trim();
        final String newpassword = newpass.getText().toString().trim();

        final String branchsysid = Config.BRANCH_SYS_ID_SHARED_PREF;


        progressDialog.setMessage("Updating Password...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Config.PASSWORD_RESET_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responses) {
                        progressDialog.dismiss();

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
                                Snackbar.make(parent_view, message, Snackbar.LENGTH_SHORT).show();
                            }else{
                                JSONObject failure = responseObj.getJSONObject("failure");
                                String message = failure.getString("message");
                                if(message.equals("Invalid Current Password...!")){
                                    Snackbar.make(parent_view, message, Snackbar.LENGTH_SHORT).show();
                                }else {
                                    Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Snackbar.make(parent_view, "Something went wrong! Try Again!!...", Snackbar.LENGTH_SHORT).show();
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
                        params.put("currpassword", currpassword);
                        params.put("newpassword", newpassword);
                        params.put("branchsysid", branchsysid);
                        //returning parameter
                        return params;
                    }
                };

                MyApplication.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View view) {
        if (view == pass_update)
        {
            if (Common.haveNetworkConnection(Profile.this)) {
                updatepass();
            } else {
                Intent i = new Intent(Profile.this, Internetcheck.class);
                startActivity(i);
            }
        }
    }

    // initializing Toolbar
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    // Back Button Action Here
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(this, Dashboard.class);
                startActivity(intent);
                this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
