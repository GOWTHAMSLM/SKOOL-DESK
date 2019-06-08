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
import android.widget.ProgressBar;

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
import java.util.Map;

import app.zerobugz.fcms.ims.appconfig.Config;
import app.zerobugz.fcms.ims.marks.Child;
import app.zerobugz.fcms.ims.marks.Group;
import app.zerobugz.fcms.ims.marks.MyMarkAdapter;
import app.zerobugz.fcms.ims.messages.NetworkController;
import app.zerobugz.fcms.ims.R;

public class Marks extends AppCompatActivity {
    RequestQueue queue;
    RecyclerView recyclerView;

    ArrayList<Group> list = new ArrayList<Group>();
    MyMarkAdapter adapter;
    private String mark_url = "http://api.skooldesk.co.in/api/Mark/MarkDetails";
    private ProgressBar progressBar;
    private View parent_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks);
        initToolbar();

        parent_view = findViewById(android.R.id.content);

        progressBar = (ProgressBar) findViewById(R.id.progressBar_mark);
        //Initialize RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_mark);
        adapter = new MyMarkAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);
        //Getting Instance of Volley Request Queue
        queue = NetworkController.getInstance(this).getRequestQueue();

        loadMarksDetails();
    }

    private void loadMarksDetails() {

        progressBar.setVisibility(View.VISIBLE);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String studentsysid = sharedPreferences.getString(Config.STUDENT_SYS_ID_SHARED_PREF, "Not Available");
        final String token_key = "Bearer " + sharedPreferences.getString(Config.TOKEN_SHARED_PREF, "Not Available");
        final String branchsysid = Config.BRANCH_SYS_ID_SHARED_PREF;


        //Creating a string request
        StringRequest newsReq = new StringRequest(Request.Method.POST, Config.GET_MARKS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responses)
                    {
                        ArrayList<Child> ch_list;

                        try {
                            JSONObject responseObj = new JSONObject(responses);

                            String res_status = responseObj.getString("response");

                            String res_value="success";

                            // Value of Response Key Validation
                            if (res_value.equals(res_status)) {
                                JSONObject success = responseObj.getJSONObject("success");
                                JSONObject result = success.getJSONObject("result");
                                JSONArray marklist = result.getJSONArray("marklist");

                                for (int mrk_lst = 0; mrk_lst < marklist.length(); mrk_lst++)
                                {
                                    try{
                                        Group gru = new Group();
                                        JSONObject mrk_lst_obj = marklist.getJSONObject(mrk_lst);
                                        //Exam Name add to arraylist
                                        String exm_name = mrk_lst_obj.getString("examname");
                                        gru.setName(exm_name);
                                        ch_list = new ArrayList<Child>();

                                        JSONArray markdetails = mrk_lst_obj.getJSONArray("markdetails");

                                        for(int mrk_dtls = 0; mrk_dtls < markdetails.length(); mrk_dtls++)
                                        {
                                            try{

                                                JSONObject sub_mrk_dtls = markdetails.getJSONObject(mrk_dtls);
                                                //Exam subject & Marks add to arraylist
                                                Child ch = new Child();
                                                ch.setName(sub_mrk_dtls.getString("subjectname"));
                                                ch.setMark(sub_mrk_dtls.getString("mark"));

                                                ch_list.add(ch);

                                            }catch (Exception e) {
                                                System.out.println(e.getMessage());

                                            } finally {
                                                //Notify adapter about data changes
                                                adapter.notifyItemChanged(mrk_dtls);
                                            }
                                        }

                                        gru.setItems(ch_list);
                                        list.add(gru);

                                    }catch (Exception e) {
                                        System.out.println(e.getMessage());

                                    }
                                }

                            }else{
                                //If the server response is not success
                                JSONObject failure = responseObj.getJSONObject("failure");
                                String message = failure.getString("message");
                                if(message.equals("No Data...!")){
                                    //Toast.makeText(Marks.this, "NO MARKS!...", Toast.LENGTH_LONG).show();
                                    Snackbar.make(parent_view, "NO MARKS!...", Snackbar.LENGTH_SHORT).show();
                                }else {
                                    //Toast.makeText(Marks.this, "Something went wrong! Try again later!!...", Toast.LENGTH_LONG).show();
                                    Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
                                }
                            }

                        } catch (JSONException e) {
                            //Toast.makeText(Marks.this, "Something went wrong! Try again later!!...", Toast.LENGTH_LONG).show();
                            Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //You can handle error here if you want
                Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
                //Toast.makeText(Marks.this, "Network Error! Try again later!!...", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
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
                params.put("studentsysid", studentsysid);

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
        getSupportActionBar().setTitle("Marks");
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