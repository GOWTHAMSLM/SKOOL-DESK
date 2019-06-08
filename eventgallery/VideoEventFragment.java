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
import app.zerobugz.fcms.ims.eventgallery.adapter.InnerGalleryAdapter;
import app.zerobugz.fcms.ims.eventgallery.model.InnerImage;
import app.zerobugz.fcms.ims.eventgallery.youtubeplayer.YoutubePlayer;
import app.zerobugz.fcms.ims.utils.Tools;
import app.zerobugz.fcms.ims.R;


public class VideoEventFragment extends Fragment {

    private String TAG = ImageEventFragment.class.getSimpleName();
    private ArrayList<InnerImage> inner_vid_images;
    private View parent_view;
    private ProgressBar progressBar;
    private InnerGalleryAdapter mAdapter;
    private RecyclerView recyclerView;

    public VideoEventFragment() {
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
        View v = inflater.inflate(R.layout.fragment_video_event, container, false);
        parent_view = v.findViewById(android.R.id.content);

        recyclerView = (RecyclerView) v.findViewById(R.id.inner_recyc_vid_view);

        progressBar = (ProgressBar) v.findViewById(R.id.progBar_inner_vid_eve);
        inner_vid_images = new ArrayList<>();

        mAdapter = new InnerGalleryAdapter(getActivity(), inner_vid_images);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getContext(), 3), true));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new InnerGalleryAdapter.RecyclerTouchListener(getContext(), recyclerView, new InnerGalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                String video_id = inner_vid_images.get(position).getSmall();

                Intent intent = new Intent(getActivity(), YoutubePlayer.class);
                intent.putExtra("video_id", video_id);
                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        String gallery_sys_id = getActivity().getIntent().getExtras().getString("gallerysysid");
        if(!gallery_sys_id.isEmpty()){
            loadInnerVideoEventDetails(gallery_sys_id);
        }else{
            //Toast.makeText(getApplicationContext(), "No Data!...", Toast.LENGTH_SHORT).show();
            Snackbar.make(parent_view, "No Video Data!...", Snackbar.LENGTH_SHORT).show();
        }

        return v;
    }

    private void loadInnerVideoEventDetails(final String gallerysysid) {

        /*pDialog.setMessage("Downloading json...");
        pDialog.show();*/

        progressBar.setVisibility(ProgressBar.VISIBLE);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String token_key = "Bearer " + sharedPreferences.getString(Config.TOKEN_SHARED_PREF, "Not Available");
        final String branchsysid = Config.BRANCH_SYS_ID_SHARED_PREF;

        //Creating a string request
        StringRequest newsReq = new StringRequest(Request.Method.POST, Config.GET_SPECIFIC_EVENT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responses)
                    {
                        //pDialog.hide();
                        progressBar.setVisibility(ProgressBar.GONE);
                        inner_vid_images.clear();

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
                                JSONObject galleryindividuallist = result.getJSONObject("galleryindividuallist");

                                /*String get_title_name = galleryindividuallist.getString("eventname");

                                if(!get_title_name.isEmpty()){
                                    ((InnerEventActivity) getActivity()).getSupportActionBar().setTitle(get_title_name);
                                    //toolbar.setTitle(get_title_name);
                                }else{
                                    ((InnerEventActivity) getActivity()).getSupportActionBar().setTitle("Oops!...");
                                    //toolbar.setTitle("Oops!...");
                                }*/

                                JSONArray videourllist = galleryindividuallist.getJSONArray("videourllist");

                                for (int j=0; j < videourllist.length(); j++){
                                    try{

                                        JSONObject video_obj = videourllist.getJSONObject(j);
                                        InnerImage image = new InnerImage(video_obj.getString("videoid"), video_obj.getString("videoimg"), video_obj.getString("videoimg"), video_obj.getString("videoimg"), "video");
                                        // adding movie to movies array
                                        inner_vid_images.add(image);

                                    }catch (Exception e) {
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
                                    Snackbar.make(parent_view, "NO VIDEO GALLERY!...", Snackbar.LENGTH_SHORT).show();
                                    //Toast.makeText(InnerEventActivity.this, "NO GALLERY!...", Toast.LENGTH_LONG).show();
                                }else {
                                    Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
                                    //Toast.makeText(InnerEventActivity.this, "Something went wrong! Try again later!!...", Toast.LENGTH_LONG).show();
                                }
                            }

                        } catch (JSONException e) {
                            Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
                            //Toast.makeText(InnerEventActivity.this, "Something went wrong! Try again later!!...", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //You can handle error here if you want
                Snackbar.make(parent_view, "Something went wrong! Try again later!!...", Snackbar.LENGTH_SHORT).show();
                // Toast.makeText(InnerEventActivity.this, "Something went wrong! Try again later!!...", Toast.LENGTH_LONG).show();
                //pDialog.hide();
                progressBar.setVisibility(ProgressBar.GONE);
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
                params.put("gallerysysid", gallerysysid);
                //returning parameter
                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(newsReq);
    }



}
