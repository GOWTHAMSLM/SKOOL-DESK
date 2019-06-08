package app.zerobugz.fcms.ims;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import app.zerobugz.fcms.ims.activity.Signin;
import app.zerobugz.fcms.ims.appintro.MyIntro;
import app.zerobugz.fcms.ims.R;

/**
 * Created by Mohanraj on 06/02/2018.
 */

public class SplashScreen extends AppCompatActivity {


    public boolean isFirstStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Intro App Initialize SharedPreferences
                SharedPreferences getSharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                isFirstStart = getSharedPreferences.getBoolean("firstStart", true);

                //  Check either activity or app is open very first time or not and do action
                if (isFirstStart) {

                    //  Launch application introduction screen
                    Intent i = new Intent(SplashScreen.this, MyIntro.class);
                    startActivity(i);
                    finish();
                    SharedPreferences.Editor e = getSharedPreferences.edit();
                    e.putBoolean("firstStart", false);
                    e.apply();
                }
                else {
                    Thread timerThread = new Thread(){
                        public void run(){
                            try{
                                sleep(3000);
                            }catch(InterruptedException e){
                                e.printStackTrace();
                            }finally{
                                Intent intent = new Intent(SplashScreen.this, Signin.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    };
                    timerThread.start();
                }
            }
        });
        t.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}
