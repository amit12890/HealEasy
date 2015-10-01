package com.example.amitthakkar.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.amitthakkar.myapplication.utility.Utility;

import java.io.IOException;


public class DashBoardActivity extends Activity implements View.OnClickListener{


    RelativeLayout layHospital,layPharmacy,layDiagnosticCenter,layMedicalEquipment,layHealthPlanner;
    Utility utility;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        utility = new Utility(DashBoardActivity.this);
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


    }

    @Override
    public void onClick(View v) {


        if(utility.isNetworkAvailable()) {


            Intent placeListIntent = new Intent(DashBoardActivity.this, PlaceListActivity.class);
            Intent comingSoonIntent = new Intent(DashBoardActivity.this, ComingSoonActivity.class);
            switch (v.getId()) {
                case R.id.lay_hospitals:
                    placeListIntent.putExtra("place_type", "hospitals");
                    startActivity(placeListIntent);
                    break;
                case R.id.lay_pharmacy:
                    placeListIntent.putExtra("place_type", "medical_store");
                    startActivity(placeListIntent);
                    break;
                case R.id.lay_labs:
                    placeListIntent.putExtra("place_type", "labs");
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
            }
        }

    }


}
