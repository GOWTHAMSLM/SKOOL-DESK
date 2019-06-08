package app.zerobugz.fcms.ims.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.zerobugz.fcms.ims.Internetcheck;
import app.zerobugz.fcms.ims.appconfig.Config;
import app.zerobugz.fcms.ims.homework.HomeworkFeeds;
import app.zerobugz.fcms.ims.homework.MyHomeworkAdapter;
import app.zerobugz.fcms.ims.messages.NetworkController;
import app.zerobugz.fcms.ims.utils.Common;
import app.zerobugz.fcms.ims.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Homework extends AppCompatActivity implements OnDateSelectedListener {
    //private ProgressDialog progressDialog;
    private ProgressBar progressBar;

    private View parent_view;
    RequestQueue queue;
    RecyclerView recyclerView;
    List<HomeworkFeeds> feedsList = new ArrayList<HomeworkFeeds>();
    MyHomeworkAdapter adapter;

    @BindView(R.id.calendarWeekView)
    MaterialCalendarView widget;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework);
        ButterKnife.bind(this);
        parent_view = findViewById(android.R.id.content);
        //Include Toolbar
        initToolbar();

        //Initialize RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        adapter = new MyHomeworkAdapter(this, feedsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //Getting Instance of Volley Request Queue
        queue = NetworkController.getInstance(this).getRequestQueue();
        //progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_cyclic);

        Calendar instance = Calendar.getInstance();
        widget.setSelectedDate(instance.getTime());

        //Get Default Month & Year Value.
        SimpleDateFormat cur_mnth = new SimpleDateFormat("MMMM");
        SimpleDateFormat cur_dt = new SimpleDateFormat("d");
        String month_name = cur_mnth.format(instance.getTime());
        String date_value = cur_dt.format(instance.getTime());

        Calendar instance1 = Calendar.getInstance();
        instance1.add(Calendar.MONTH, -1);

        widget.state().edit()
                .setMinimumDate(instance1.getTime())
                .setMaximumDate(instance.getTime())
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .commit();
        widget.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        widget.setTopbarVisible(false);
        widget.setShowOtherDates(MaterialCalendarView.SHOW_OUT_OF_RANGE);
        widget.setOnDateChangedListener(this);

        loadhomeworkdetails(month_name, date_value);

    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

        SimpleDateFormat month_df = new SimpleDateFormat("MMMM");
        SimpleDateFormat date_df = new SimpleDateFormat("d");
        String month_name = month_df.format(date.getDate());
        String date_val = date_df.format(date.getDate());
        if (Common.haveNetworkConnection(Homework.this))
        {
            loadhomeworkdetails(month_name, date_val);
        }else {
            Intent i = new Intent(Homework.this, Internetcheck.class);
            startActivity(i);
        }

    }

    //Load Homework Details method here
    private void loadhomeworkdetails(String month_name, String date_val) {

        //progressDialog.setMessage("Please Wait!...");
        //progressDialog.show();

        progressBar.setVisibility(ProgressBar.VISIBLE);

        feedsList.clear();
        recyclerView.setAdapter(adapter);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String stusysid = sharedPreferences.getString(Config.STUDENT_SYS_ID_SHARED_PREF, "Not Available");
        final String token_key = "Bearer " + sharedPreferences.getString(Config.TOKEN_SHARED_PREF, "Not Available");
        final String branchsysid = Config.BRANCH_SYS_ID_SHARED_PREF;

        final String month = month_name;
        final String day = date_val;

        //Creating a string request
        StringRequest newsReq = new StringRequest(Request.Method.POST, Config.GET_HOMEWORK_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responses)
                    {
                        //progressDialog.dismiss();
                        progressBar.setVisibility(ProgressBar.INVISIBLE);

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
                                JSONArray homeworkdetails = result.getJSONArray("homeworkdetails");

                                for(int i=0; i < homeworkdetails.length(); i++){
                                    try{
                                        JSONObject obj = homeworkdetails.getJSONObject(i);
                                        HomeworkFeeds feeds = new HomeworkFeeds(obj.getString("subjectname"), obj.getString("workdetails"), obj.getString("reference"));
                                        feedsList.add(feeds);

                                    }catch (Exception e){
                                        System.out.println(e.getMessage());
                                    } finally {
                                        adapter.notifyItemChanged(i);
                                    }
                                }

                            }else{
                                //If the server response is not success
                                JSONObject failure = responseObj.getJSONObject("failure");
                                String message = failure.getString("message");
                                if(message.equals("No Data...!")){
                                    Snackbar.make(parent_view, "NO HOMEWORK! ...", Snackbar.LENGTH_SHORT).show();
                                }else {
                                    Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
                                }
                            }

                        } catch (JSONException e) {
                            Snackbar.make(parent_view, "Something went wrong!...", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //You can handle error here if you want
                //progressDialog.dismiss();
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), "Something went wrong!...", Toast.LENGTH_SHORT).show();
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
                params.put("month", month);
                params.put("day", day);

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
        getSupportActionBar().setTitle("Homework");
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
