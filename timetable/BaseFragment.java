package app.zerobugz.fcms.ims.timetable;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.zerobugz.fcms.ims.appconfig.Config;
import app.zerobugz.fcms.ims.messages.NetworkController;

public class BaseFragment extends Fragment {

    RequestQueue queue;

    //Load Timetable Details method here
    public void loadtimetabledetails(RecyclerView recyclerView, final List<TimetableFeeds> feedsList, final MyTimetableAdapter adapter, final ProgressBar progressBar, final View parent_view, final TextView timetable_text, final ImageView timetable_icon, final String day) {

        //Getting Instance of Volley Request Queue
        queue = NetworkController.getInstance(getActivity()).getRequestQueue();

        progressBar.setVisibility(ProgressBar.VISIBLE);

        feedsList.clear();
        recyclerView.setAdapter(adapter);

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String stusysid = sharedPreferences.getString(Config.STUDENT_SYS_ID_SHARED_PREF, "Not Available");
        final String token_key = "Bearer " + sharedPreferences.getString(Config.TOKEN_SHARED_PREF, "Not Available");
        final String branchsysid = Config.BRANCH_SYS_ID_SHARED_PREF;

        //Creating a string request
        StringRequest newsReq = new StringRequest(Request.Method.POST, Config.GET_TIMETABLE_URL,
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
                                JSONArray timetabledetails = result.getJSONArray("timetabledetails");

                                for(int i=0; i < timetabledetails.length(); i++){
                                    try{
                                        JSONObject obj = timetabledetails.getJSONObject(i);
                                        TimetableFeeds feeds = new TimetableFeeds(obj.getString("name"), obj.getString("fromtime"), obj.getString("endtime"));
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
                                    //Snackbar.make(parent_view, "NO " + day.toUpperCase() + " TIMETABLE! ...", Snackbar.LENGTH_SHORT).show();
                                    timetable_text.setVisibility(View.VISIBLE);
                                    timetable_icon.setVisibility(View.VISIBLE);
                                    timetable_text.setText("NO " + day.toUpperCase() +" TIMETABLE ! ...");
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
                params.put("studentSysid", stusysid);
                params.put("day", day);

                //returning parameter
                return params;
            }
        };

        queue.add(newsReq);
    }



}
