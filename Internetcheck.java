package app.zerobugz.fcms.ims;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import app.zerobugz.fcms.ims.utils.Common;
import app.zerobugz.fcms.ims.R;

/**
 * Created by Mohanraj on 06/04/2018.
 */

public class Internetcheck extends AppCompatActivity {

    private View parent_view;
    TextView btntry;
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internetcheck);

        parent_view = findViewById(android.R.id.content);

        btntry = (TextView) findViewById(R.id.btntryagain);

        btntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.haveNetworkConnection(Internetcheck.this))
                {
                    finish();
                }else {
                    Snackbar.make(parent_view, "Sorry Please Check it Again!...", Snackbar.LENGTH_SHORT).show();
                }
            }
        });


    }


    @Override
    public void onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!",
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

}
