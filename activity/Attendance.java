package app.zerobugz.fcms.ims.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.zerobugz.fcms.ims.appconfig.Config;
import app.zerobugz.fcms.ims.appconfig.MyApplication;
import app.zerobugz.fcms.ims.attendance.EventDecorator;
import app.zerobugz.fcms.ims.attendance.EventMonthFeeds;
import app.zerobugz.fcms.ims.attendance.MyAttendanceAdapter;
import app.zerobugz.fcms.ims.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Attendance extends AppCompatActivity implements OnMonthChangedListener {

    private ProgressDialog progressDialog;
    private View parent_view;

    List<EventMonthFeeds> feedsList = new ArrayList<EventMonthFeeds>();
    MyAttendanceAdapter adapter;


    @BindView(R.id.recyc_atten_event_view)
    RecyclerView recyclerView;

    @BindView(R.id.mon_eve_title)
    TextView mon_eve_title;

    @BindView(R.id.calendarView)
    MaterialCalendarView widget;

    @BindView(R.id.present)
    TextView present;

    @BindView(R.id.absent)
    TextView absent;

    @BindView(R.id.leave)
    TextView leave;

    @BindView(R.id.holiday)
    TextView holiday;

    @BindView(R.id.forenoon_absent)
    TextView forenoon_absent;

    @BindView(R.id.afternoon_absent)
    TextView afternoon_absent;

    @BindView(R.id.forenoon_leave)
    TextView forenoon_leave;

    @BindView(R.id.afternoon_leave)
    TextView afternoon_leave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        ButterKnife.bind(this);
        initToolbar();

        parent_view = findViewById(android.R.id.content);
        progressDialog = new ProgressDialog(this);

        //Initialize RecyclerView
        adapter = new MyAttendanceAdapter(this, feedsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);

        Calendar instance = Calendar.getInstance();
        widget.setSelectedDate(instance.getTime());

        //Get Default Month & Year Value.
        SimpleDateFormat cur_mnth = new SimpleDateFormat("MMMM");
        SimpleDateFormat cur_yr = new SimpleDateFormat("yyyy");
        String month_name = cur_mnth.format(instance.getTime());
        String year_value = cur_yr.format(instance.getTime());

        //Set Month Limitaion Start to End.
        int CurrentMonth = (instance.getInstance().get(Calendar.MONTH));

        if(CurrentMonth<6){
            //MIN
            Calendar instance1 = Calendar.getInstance();
            instance1.set(instance1.get(Calendar.YEAR)-1, Calendar.JUNE, 1);
            Calendar instance2 = Calendar.getInstance();
            int lastDate = instance2.getActualMaximum(Calendar.DATE);
            instance2.set(instance2.get(Calendar.YEAR), instance2.get(Calendar.MONTH), lastDate);

            widget.state().edit()
                    .setMinimumDate(instance1.getTime())
                    .setMaximumDate(instance2.getTime())
                    .commit();

        }else{
            //MAX
            Calendar instance1 = Calendar.getInstance();
            instance1.set(instance1.get(Calendar.YEAR), Calendar.JUNE, 1);
            Calendar instance2 = Calendar.getInstance();
            int lastDate = instance2.getActualMaximum(Calendar.DATE);
            instance2.set(instance2.get(Calendar.YEAR), instance2.get(Calendar.MONTH), lastDate);

            widget.state().edit()
                    .setMinimumDate(instance1.getTime())
                    .setMaximumDate(instance2.getTime())
                    .commit();
        }

        widget.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);
        widget.setTitleAnimationOrientation(MaterialCalendarView.HORIZONTAL);
        widget.setOnMonthChangedListener(this);
        //new ApiSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());
        UpdateAttendance(month_name, year_value);
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {


        SimpleDateFormat month_df = new SimpleDateFormat("MMMM");
        SimpleDateFormat year_df = new SimpleDateFormat("yyyy");
        String month = month_df.format(date.getDate());
        String year = year_df.format(date.getDate());
        //Toast.makeText(this, month+year, Toast.LENGTH_SHORT).show();
        UpdateAttendance(month, year);
    }


    /* Call Update Attendance Method */
    public void UpdateAttendance(String month, String year) {

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String student_sysid = sharedPreferences.getString(Config.STUDENT_SYS_ID_SHARED_PREF, "Not Available");
        final String token_key = "Bearer " + sharedPreferences.getString(Config.TOKEN_SHARED_PREF, "Not Available");

        final String month_name = month;
        final String year_value = year;

        final String branchsysid = Config.BRANCH_SYS_ID_SHARED_PREF;


        progressDialog.setMessage("Please Wait!...");
        progressDialog.show();
        feedsList.clear();
        recyclerView.setAdapter(adapter);

        present.setText("0");
        absent.setText("0");
        leave.setText("0");
        holiday.setText("0");
        forenoon_absent.setText("0");
        afternoon_absent.setText("0");
        forenoon_leave.setText("0");
        afternoon_leave.setText("0");

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Config.GET_ATTENDANCE_URL,
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
                                JSONObject result = success.getJSONObject("result");
                                JSONObject attendance = result.getJSONObject("Attendance");

                                JSONArray overalldetails = attendance.getJSONArray("overalldetails");
                                JSONArray information = attendance.getJSONArray("information");
                                JSONArray gallerydetails = attendance.getJSONArray("gallerydetails");

                                for (int i = 0; i < overalldetails.length(); i++) {
                                    try {
                                        JSONObject obj = overalldetails.getJSONObject(i);
                                        present.setText(obj.getString("present"));
                                        absent.setText(obj.getString("absent"));
                                        leave.setText(obj.getString("leave"));
                                        holiday.setText(obj.getString("holiday"));
                                        forenoon_absent.setText(obj.getString("fnabsent"));
                                        afternoon_absent.setText(obj.getString("anabsent"));
                                        forenoon_leave.setText(obj.getString("fnleave"));
                                        afternoon_leave.setText(obj.getString("anleave"));

                                        ArrayList<CalendarDay> present_dates = new ArrayList<>();
                                        ArrayList<CalendarDay> absent_dates = new ArrayList<>();
                                        ArrayList<CalendarDay> leave_dates = new ArrayList<>();
                                        ArrayList<CalendarDay> holiday_dates = new ArrayList<>();
                                        ArrayList<CalendarDay> fnabsent_dates = new ArrayList<>();
                                        ArrayList<CalendarDay> anabsent_dates = new ArrayList<>();
                                        ArrayList<CalendarDay> fnleave_dates = new ArrayList<>();
                                        ArrayList<CalendarDay> anleave_dates = new ArrayList<>();

                                        for (int j = 0; j < information.length(); j++) {
                                            try{
                                                JSONObject info_obj = information.getJSONObject(j);
                                                String attendancedate = info_obj.getString("attendancedate");
                                                String attendancestatus = info_obj.getString("attendancestatus");
                                                Date d1= new Date();
                                                d1 = new SimpleDateFormat("dd-MM-yyyy").parse(attendancedate);
                                                CalendarDay day = CalendarDay.from(d1);

                                                if(attendancestatus.equals("P")){
                                                    present_dates.add(day);
                                                }else if(attendancestatus.equals("A")){

                                                    absent_dates.add(day);
                                                }else if(attendancestatus.equals("L")){

                                                    leave_dates.add(day);
                                                }else if(attendancestatus.equals("H")){

                                                    holiday_dates.add(day);
                                                }else if(attendancestatus.equals("FNA")){

                                                    fnabsent_dates.add(day);
                                                }else if(attendancestatus.equals("ANA")){

                                                    anabsent_dates.add(day);
                                                }else if(attendancestatus.equals("FNL")){

                                                    fnleave_dates.add(day);
                                                }else if(attendancestatus.equals("ANL")){

                                                    anleave_dates.add(day);
                                                }

                                            }catch (Exception e){
                                                System.out.println(e.getMessage());
                                            }
                                        }

                                        //Assign Span color to each Dates
                                        if(!present_dates.isEmpty()){
                                            widget.addDecorator(new EventDecorator(Color.parseColor("#66BB6A"), present_dates));
                                        }

                                        if(!absent_dates.isEmpty()){
                                            widget.addDecorator(new EventDecorator(Color.parseColor("#FF1744"), absent_dates));
                                        }
                                        if(!leave_dates.isEmpty()){
                                            widget.addDecorator(new EventDecorator(Color.parseColor("#42A5F5"), leave_dates));
                                        }
                                        if(!holiday_dates.isEmpty()){
                                            widget.addDecorator(new EventDecorator(Color.parseColor("#263238"), holiday_dates));
                                        }
                                        if(!fnabsent_dates.isEmpty()){
                                            widget.addDecorator(new EventDecorator(Color.parseColor("#0D47A1"), fnabsent_dates));
                                        }
                                        if(!anabsent_dates.isEmpty()){
                                            widget.addDecorator(new EventDecorator(Color.parseColor("#FFA726"), anabsent_dates));
                                        }
                                        if(!fnleave_dates.isEmpty()){
                                            widget.addDecorator(new EventDecorator(Color.parseColor("#AB47BC"), fnleave_dates));
                                        }
                                        if(!anleave_dates.isEmpty()){
                                            widget.addDecorator(new EventDecorator(Color.parseColor("#F06292"), anleave_dates));
                                        }
                                    } catch (Exception e) {
                                        System.out.println(e.getMessage());
                                    }
                                }

                                //Monthwise Event details bind
                                if(gallerydetails.length()> 0){

                                    for (int k = 0; k < gallerydetails.length(); k++) {
                                        try {

                                            JSONObject obj = gallerydetails.getJSONObject(k);
                                            EventMonthFeeds feeds = new EventMonthFeeds(obj.getString("eventsysid"), obj.getString("entrydate"), obj.getString("eventname"), obj.getString("totalimages"), obj.getString("totalvideos"));

                                            feedsList.add(feeds);

                                        } catch (Exception e) {
                                            System.out.println(e.getMessage());

                                        } finally {
                                            //Notify adapter about data changes
                                            adapter.notifyItemChanged(k);
                                        }
                                    }


                                }else {
                                    mon_eve_title.setText("No event for this month...");
                                }



                            }else{
                                JSONObject failure = responseObj.getJSONObject("failure");
                                String message = failure.getString("message");
                                if(message.equals("No Data...!")){
                                    Snackbar.make(parent_view, "NO ATTENDANCE!...", Snackbar.LENGTH_SHORT).show();
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
                params.put("stusysid", student_sysid);
                params.put("Month", month_name);
                params.put("year", year_value);
                params.put("branchsysid", branchsysid);
                //returning parameter
                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(stringRequest);
    }

    // initializing Toolbar
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Attendance");
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
