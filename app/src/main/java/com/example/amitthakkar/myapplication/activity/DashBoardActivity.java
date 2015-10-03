package com.example.amitthakkar.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.amitthakkar.myapplication.AppPreferences;
import com.example.amitthakkar.myapplication.DatabaseHandler;
import com.example.amitthakkar.myapplication.R;
import com.example.amitthakkar.myapplication.model.Area;
import com.example.amitthakkar.myapplication.utility.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.rey.material.widget.TextView;

import se.walkercrou.places.GooglePlaces;


public class DashBoardActivity extends Activity implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {


    RelativeLayout layHospital,layPharmacy,layDiagnosticCenter,layMedicalEquipment,layHealthPlanner;
    public static TextView txtState,txtAreaName;
    Utility utility;
    public static String AREA_NAME="",STATE_NAME="";
    public static Area selected_area = null;
    private AppPreferences preferences;
    private String latitude,longitude;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        utility = new Utility(DashBoardActivity.this);
        preferences = new AppPreferences(DashBoardActivity.this);
        mGoogleApiClient = new GoogleApiClient.Builder(DashBoardActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        txtAreaName = (TextView) findViewById(R.id.txt_area_name);
        txtAreaName.setOnClickListener(this);
        txtState = (TextView) findViewById(R.id.txt_state_name);
        txtState.setOnClickListener(this);
        layHospital = (RelativeLayout) findViewById(R.id.lay_hospitals);
        layHospital.setOnClickListener(this);
        layPharmacy = (RelativeLayout) findViewById(R.id.lay_pharmacy);
        layPharmacy.setOnClickListener(this);
        layDiagnosticCenter = (RelativeLayout) findViewById(R.id.lay_labs);
        layDiagnosticCenter.setOnClickListener(this);
        layMedicalEquipment = (RelativeLayout) findViewById(R.id.lay_medical_equipment);
        layMedicalEquipment.setOnClickListener(this);
        layHealthPlanner = (RelativeLayout) findViewById(R.id.lay_health_planner);
        layHealthPlanner.setOnClickListener(this);

        DatabaseHandler dbHandler = new DatabaseHandler(DashBoardActivity.this);
        dbHandler.getAreaNames();

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            utility.showErrorDialog("For better result, Please enable device GPS.","Enable Now","Not Now");
            utility.errorDialog.getBuilder().callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    super.onPositive(dialog);
                    Intent callGPSSettingIntent = new Intent(
                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(callGPSSettingIntent);
                }

                @Override
                public void onNegative(MaterialDialog dialog) {
                    super.onNegative(dialog);
                    dialog.dismiss();
                }
            });
        }

        if(utility.isNetworkAvailable()){
            updateLocationInfo();
        }


    }

    @Override
    public void onClick(View v) {


        if(utility.isNetworkAvailable()) {


            Intent placeListIntent = new Intent(DashBoardActivity.this, PlaceListActivity.class);

            Intent comingSoonIntent = new Intent(DashBoardActivity.this, ComingSoonActivity.class);
            Intent listIntent = new Intent(DashBoardActivity.this,SearchAreaListActivity.class);
            switch (v.getId()) {
                case R.id.lay_hospitals:
                    placeListIntent.putExtra("place_type", "hospitals");
                    placeListIntent.putExtra("query","hospitalsIn"+AREA_NAME.trim()+STATE_NAME.trim());
                    startActivity(placeListIntent);
                    break;
                case R.id.lay_pharmacy:
                    placeListIntent.putExtra("place_type", "medical_store");
                    placeListIntent.putExtra("query","medical_storeIn"+AREA_NAME.trim()+STATE_NAME.trim());
                    startActivity(placeListIntent);
                    break;
                case R.id.lay_labs:
                    placeListIntent.putExtra("place_type", "labs");
                    placeListIntent.putExtra("query","labsIn"+AREA_NAME.trim()+STATE_NAME.trim());
                    startActivity(placeListIntent);
                    break;
                case R.id.lay_medical_equipment:
                    comingSoonIntent.putExtra("place_type", "medical_equipments");
                    startActivity(comingSoonIntent);
                    break;
                case R.id.lay_health_planner:
                    comingSoonIntent.putExtra("place_type", "health_planner");
                    startActivity(comingSoonIntent);
                    break;
                case R.id.txt_state_name:
                    listIntent.putExtra("search_type","State");
                    startActivity(listIntent);
                    break;
                case R.id.txt_area_name:
                    if(STATE_NAME.toString().length() > 0){
                        listIntent.putExtra("search_type","Area");
                        listIntent.putExtra("state_name",txtState.getText().toString());
                        startActivity(listIntent);
                    }else {
                        Toast.makeText(DashBoardActivity.this,"Please select state first",Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }

    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d("TAG", "Connected");

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {

            preferences.setLatitude(String.valueOf(mLastLocation.getLatitude()));
            preferences.setLongitude(String.valueOf(mLastLocation.getLongitude()));
            createLocationRequest();

        }else if(mLastLocation == null){
            String query = utility.getCityName(Double.parseDouble(preferences.getLatitude()), Double.parseDouble(preferences.getLongitude()));
            if(query.equals(Utility.TIMEOUT_ERROR))
                showTimeOutError();
            else
                updateLocationInfo();
            createLocationRequest();
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
    public void onLocationChanged(Location mLastLocation) {
        if (mLastLocation != null) {
            preferences.setLatitude(String.valueOf(mLastLocation.getLatitude()));
            preferences.setLongitude(String.valueOf(mLastLocation.getLongitude()));
            updateLocationInfo();
            stopLocationUpdates();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    public void showTimeOutError(){
        utility.showErrorDialog(Utility.TIMEOUT_ERROR_MESSAGE, "Try Again", "No");
        utility.errorDialog.getBuilder().callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                String query = utility.getCityName(Double.parseDouble(preferences.getLatitude()),
                        Double.parseDouble(preferences.getLongitude()));
                if (query.equals(Utility.TIMEOUT_ERROR))
                    showTimeOutError();
                else
                    updateLocationInfo();

            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);
                finish();
            }
        });
    }

    public void updateLocationInfo(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        double latitude = Double.parseDouble(preferences.getLatitude());
                        double longitude = Double.parseDouble(preferences.getLongitude());
                        txtAreaName.setText(utility.getAddress(latitude,longitude).getThoroughfare()+","+
                                utility.getAddress(latitude,longitude).getLocality());
                        AREA_NAME = utility.getAddress(latitude,longitude).getThoroughfare()+","+
                                utility.getAddress(latitude,longitude).getLocality();
                        txtState.setText(utility.getAddress(latitude,longitude).getAdminArea());
                        STATE_NAME = utility.getAddress(latitude,longitude).getAdminArea();
                    }
                });

            }
        });
        t.start();

    }
}
