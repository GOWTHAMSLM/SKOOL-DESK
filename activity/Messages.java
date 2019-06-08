package app.zerobugz.fcms.ims.activity;

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

import app.zerobugz.fcms.ims.appconfig.Config;
import app.zerobugz.fcms.ims.messages.MyRecyclerAdapter;
import app.zerobugz.fcms.ims.messages.NetworkController;
import app.zerobugz.fcms.ims.messages.NewsFeeds;
import app.zerobugz.fcms.ims.R;

public class Messages extends AppCompatActivity {

    RequestQueue queue;
    RecyclerView recyclerView;
    List<NewsFeeds> feedsList = new ArrayList<NewsFeeds>();
    MyRecyclerAdapter adapter;
    private View parent_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        initToolbar();

        parent_view = findViewById(android.R.id.content);

        //Initialize RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        adapter = new MyRecyclerAdapter(this, feedsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);
        //Getting Instance of Volley Request Queue
        queue = NetworkController.getInstance(this).getRequestQueue();
        //Volley's inbuilt class to make Json array request
        loadmessagedetails();

    }

    private void loadmessagedetails() {

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String stusysid = sharedPreferences.getString(Config.STUDENT_SYS_ID_SHARED_PREF, "Not Available");
        final String token_key = "Bearer " + sharedPreferences.getString(Config.TOKEN_SHARED_PREF, "Not Available");
        final String branchsysid = Config.BRANCH_SYS_ID_SHARED_PREF;

        //Creating a string request
        StringRequest newsReq = new StringRequest(Request.Method.POST, Config.GET_MESSAGE_URL,
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
                                JSONObject notificationdetails = result.getJSONObject("notificationdetails");

                                JSONArray detailslist = notificationdetails.getJSONArray("detailslist");

                                for (int i = 0; i < detailslist.length(); i++) {
                                    try {

                                        JSONObject obj = detailslist.getJSONObject(i);
                                        NewsFeeds feeds = new NewsFeeds(obj.getString("title"), obj.getString("message"), obj.getString("ntdate"), obj.getString("nttime"));

                                        // adding movie to movies array
                                        feedsList.add(feeds);

                                    } catch (Exception e) {
                                        System.out.println(e.getMessage());

                                    } finally {
                                        //Notify adapter about data changes
                                        adapter.notifyItemChanged(i);
                                    }
                                }

                            }else{
                                //If the server response is not success
                                JSONObject failure = responseObj.getJSONObject("failure");
                                String message = failure.getString("message");
                                if(message.equals("No Data...!")){
                                    //Toast.makeText(signin.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                                    Snackbar.make(parent_view, "NO MESSAGE!...", Snackbar.LENGTH_SHORT).show();
                                }else {
                                    Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
                                }
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
                params.put("stusysid", stusysid);

                //returning parameter
                return params;
            }
        };

        queue.add(newsReq);
    }


    // initializing Toolbar
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Messages");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

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
