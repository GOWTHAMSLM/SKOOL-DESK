package app.zerobugz.fcms.ims.eventgallery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
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
import app.zerobugz.fcms.ims.appconfig.MyApplication;
import app.zerobugz.fcms.ims.appintro.widget.SpacingItemDecoration;
import app.zerobugz.fcms.ims.eventgallery.adapter.GalleryAdapter;
import app.zerobugz.fcms.ims.eventgallery.model.Image;
import app.zerobugz.fcms.ims.utils.Tools;
import app.zerobugz.fcms.ims.R;

public class AllEventFragment extends Fragment {
    private View parent_view;
    private ProgressBar progressBar;

    private ArrayList<Image> images;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;

    public AllEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_all_event, container, false);
        parent_view = getActivity().findViewById(android.R.id.content);
        recyclerView = (RecyclerView) v.findViewById(R.id.alleventrecyclerview);
        //pDialog = new ProgressDialog(getActivity());
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar_eve);
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getActivity(), images);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getContext(), 11), true));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String galleryname = images.get(position).getEventname();
                String gallerysysid = images.get(position).getEventsysid();
                //Toast.makeText(getApplicationContext(), ss + po, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), InnerEventActivity.class);
                intent.putExtra("gallerysysid", gallerysysid);
                intent.putExtra("galleryname", galleryname);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        loadEventDetails();

        return v;
    }

    private void loadEventDetails() {

        //pDialog.setMessage("Downloading json...");
        //pDialog.show();
        progressBar.setVisibility(ProgressBar.VISIBLE);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String token_key = "Bearer " + sharedPreferences.getString(Config.TOKEN_SHARED_PREF, "Not Available");
        final String branchsysid = Config.BRANCH_SYS_ID_SHARED_PREF;

        //Creating a string request
        StringRequest newsReq = new StringRequest(Request.Method.POST, Config.GET_ALL_EVENT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responses)
                    {
                        //pDialog.hide();
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        images.clear();

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
                                JSONArray overallgallerylist = result.getJSONArray("overallgallerylist");

                                for (int i = 0; i < overallgallerylist.length(); i++) {
                                    try {

                                        JSONObject obj = overallgallerylist.getJSONObject(i);
                                        Image image = new Image(obj.getString("eventname"), obj.getString("eventsysid"), obj.getString("imageurl"), obj.getString("eventdate"));
                                        // adding movie to movies array
                                        images.add(image);

                                    } catch (Exception e) {
                                        System.out.println(e.getMessage());

                                    } finally {
                                        //Notify adapter about data changes
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }

                            }else{
                                //If the server response is not success
                                JSONObject failure = responseObj.getJSONObject("failure");
                                String message = failure.getString("message");
                                if(message.equals("No Data...!")){
                                    Toast.makeText(getActivity(), "NO EVENTS!...", Toast.LENGTH_LONG).show();
                                }else {
                                    Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
                                    //Toast.makeText(getActivity(), "Something went wrong! Try again later!!...", Toast.LENGTH_LONG).show();
                                }
                            }

                        } catch (JSONException e) {
                            Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
                            //Toast.makeText(getActivity(), "Something went wrong! Try again later!!...", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //You can handle error here if you want
                Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), "Something went wrong! Try again later!!...", Toast.LENGTH_LONG).show();
                //pDialog.hide();
                progressBar.setVisibility(ProgressBar.INVISIBLE);
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
                //returning parameter
                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(newsReq);
    }


}
