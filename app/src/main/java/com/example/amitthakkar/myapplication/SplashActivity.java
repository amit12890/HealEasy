package com.example.amitthakkar.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amitthakkar.myapplication.utility.AppPreferences;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


public class SplashActivity extends Activity implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,Animation.AnimationListener {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 500;
    private GoogleApiClient mGoogleApiClient;
    TextView txtH,txtE,txtA,txtL,txtEE,txtEA,txtES;
    ImageView imgRound,imgY;
    Animation animH,animE,animA,animL,animEE,animEA,animES,animEY,animSymbol;
    AppPreferences preferences;
    // Tag used to cancel the request

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mGoogleApiClient = new GoogleApiClient.Builder(SplashActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        preferences = new AppPreferences(SplashActivity.this);

        imgRound = (ImageView) findViewById(R.id.img_round);
        imgRound.setVisibility(View.INVISIBLE);
        txtH = (TextView) findViewById(R.id.txt_h);
        txtE = (TextView) findViewById(R.id.txt_e);
        txtE.setVisibility(View.INVISIBLE);
        txtA = (TextView) findViewById(R.id.txt_a);
        txtA.setVisibility(View.INVISIBLE);
        txtL = (TextView) findViewById(R.id.txt_l);
        txtL.setVisibility(View.INVISIBLE);
        txtEE = (TextView) findViewById(R.id.txt_ee);
        txtEE.setVisibility(View.INVISIBLE);
        txtEA = (TextView) findViewById(R.id.txt_ea);
        txtEA.setVisibility(View.INVISIBLE);
        txtES = (TextView) findViewById(R.id.txt_s);
        txtES.setVisibility(View.INVISIBLE);
        imgY = (ImageView) findViewById(R.id.img_y);
        imgY.setVisibility(View.INVISIBLE);

        animH = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        animH.setAnimationListener(this);

        animE = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        animE.setAnimationListener(this);

        animA = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        animA.setAnimationListener(this);

        animL = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        animL.setAnimationListener(this);

        animEE = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        animEE.setAnimationListener(this);

        animEA = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        animEA.setAnimationListener(this);

        animES = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        animES.setAnimationListener(this);

        animEY = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        animEY.setAnimationListener(this);

        animSymbol = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.move);
        animSymbol.setAnimationListener(this);


        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                txtH.startAnimation(animH);
                // close this activity
                //finish();
            }
        }, SPLASH_TIME_OUT);

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("TAG", "Connected");

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {

            preferences.setLatitude(String.valueOf(mLastLocation.getLatitude()));
            preferences.setLongitude(String.valueOf(mLastLocation.getLongitude()));

        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("TAG", "Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("TAG", "Failed");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(animation == animH){
            txtH.setVisibility(View.VISIBLE);
            txtE.setVisibility(View.VISIBLE);
            txtE.startAnimation(animE);
        }else  if(animation == animE){
            txtE.setVisibility(View.VISIBLE);
            txtA.setVisibility(View.VISIBLE);
            txtA.startAnimation(animA);
        }else  if(animation == animA){
            txtA.setVisibility(View.VISIBLE);
            txtL.setVisibility(View.VISIBLE);
            txtL.startAnimation(animL);
        }else  if(animation == animL){
            txtL.setVisibility(View.VISIBLE);
            txtEE.setVisibility(View.VISIBLE);
            txtEE.startAnimation(animEE);
        }else  if(animation == animEE){
            txtEE.setVisibility(View.VISIBLE);
            txtEA.setVisibility(View.VISIBLE);
            txtEA.startAnimation(animEA);
        }else  if(animation == animEA){
            txtEA.setVisibility(View.VISIBLE);
            txtES.setVisibility(View.VISIBLE);
            txtES.startAnimation(animES);
        }else  if(animation == animES){
            txtES.setVisibility(View.VISIBLE);
            imgY.setVisibility(View.VISIBLE);
            imgY.startAnimation(animEY);
        }else  if(animation == animEY){

            imgRound.setVisibility(View.VISIBLE);
            imgRound.startAnimation(animSymbol);
        }else  if(animation == animSymbol){
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, DashBoardActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 200);

        }

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onBackPressed() {

    }
}
