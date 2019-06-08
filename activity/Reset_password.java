package app.zerobugz.fcms.ims.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.zerobugz.fcms.ims.Internetcheck;
import app.zerobugz.fcms.ims.appconfig.Config;
import app.zerobugz.fcms.ims.appconfig.Forgot_Pass_Config;
import app.zerobugz.fcms.ims.appconfig.MyApplication;
import app.zerobugz.fcms.ims.helper.PrefManagerForgotpass;
import app.zerobugz.fcms.ims.utils.Common;
import app.zerobugz.fcms.ims.R;

/**
 * Created by Mohanraj on 06/04/2018.
 */

public class Reset_password extends AppCompatActivity implements View.OnClickListener {

    private View parent_view;
    private EditText newpass, compass;
    private Button update;
    private ProgressDialog progressDialog;
    private PrefManagerForgotpass pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        parent_view = findViewById(android.R.id.content);

        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        newpass = (EditText) findViewById(R.id.edt_newpass);
        compass = (EditText) findViewById(R.id.edt_compass);
        update = (Button) findViewById(R.id.btn_update);

        progressDialog = new ProgressDialog(this);

        update.setOnClickListener(this);

        pref = new PrefManagerForgotpass(getApplicationContext());
    }

    /* Call Registration Method */
    public void updatepass() {
        Bundle mob2=getIntent().getExtras();
        final String mobile=mob2.getString("mob1");
        //final String mobile="9524980800";
        final String password = newpass.getText().toString().trim();

        final String branchsysid = Config.BRANCH_SYS_ID_SHARED_PREF;

        progressDialog.setMessage("Updating Password...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Forgot_Pass_Config.URL_PASS_UPD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responses) {
                        progressDialog.dismiss();

                        try {
                            JSONObject responseObj = new JSONObject(responses);

                            String res_status = responseObj.getString("response");
                            String res_value="success";

                            if (res_value.equals(res_status)) {
                                pref.clearSession();
                                JSONObject success = responseObj.getJSONObject("success");
                                String message = success.getString("message");
                                Intent it = new Intent(Reset_password.this, Signin.class);
                                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(it);
                                finish();
                                Snackbar.make(parent_view, message, Snackbar.LENGTH_SHORT).show();
                            }else{
                                Snackbar.make(parent_view, "Update Password Failed! Try Again Later!!...", Snackbar.LENGTH_SHORT).show();
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
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("changepassword", password);
                params.put("mobileno", mobile);
                params.put("branchsysid", branchsysid);
                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(stringRequest);
    }

    /* Call Validate Method */
    public boolean validateForm() {
        String pass = newpass.getText().toString().trim();
        String conpass = compass.getText().toString().trim();
        boolean havevalidate = true;

        // validating empty password and conform password
        if (pass.length() == 0 || conpass.length() == 0) {
            Snackbar.make(parent_view, "Please enter your password", Snackbar.LENGTH_SHORT).show();
            havevalidate = false;
            return havevalidate;
        } else if (!newpass.getText().toString().equals(compass.getText().toString())) {
            Snackbar.make(parent_view, "Password not matched!", Snackbar.LENGTH_SHORT).show();
            havevalidate = false;
            return havevalidate;
        }
        return havevalidate;
    }

    @Override
    public void onClick(View view) {
        if (view == update)
        {
            if (validateForm())
            {
                if (Common.haveNetworkConnection(Reset_password.this)) {
                    updatepass();
                } else {
                    Intent i = new Intent(Reset_password.this, Internetcheck.class);
                    startActivity(i);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        pref.clearSession();
        Intent intent = new Intent(Reset_password.this, Signin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.finish();
    }

}
