package com.example.amitthakkar.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import java.io.IOException;


public class DashBoardActivity extends Activity implements View.OnClickListener{


    RelativeLayout layHospital,layPharmacy,layDiagnosticCenter,layMedicalEquipment,layHealthPlanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

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


    }


    @Override
    public void onClick(View v) {
        Intent placeListIntent = new Intent(DashBoardActivity.this,PlaceListActivity.class);
        Intent comingSoonIntent = new Intent(DashBoardActivity.this,ComingSoonActivity.class);
        switch (v.getId()){
            case R.id.lay_hospitals:
                placeListIntent.putExtra("place_type","hospitals");
                startActivity(placeListIntent);
                break;
            case R.id.lay_pharmacy:
                placeListIntent.putExtra("place_type","medical_store");
                startActivity(placeListIntent);
                break;
            case R.id.lay_labs:
                placeListIntent.putExtra("place_type","labs");
                startActivity(placeListIntent);
                break;
            case R.id.lay_medical_equipment:
                comingSoonIntent.putExtra("place_type","medical_equipments");
                startActivity(comingSoonIntent);
                break;
            case R.id.lay_health_planner:
                comingSoonIntent.putExtra("place_type", "health_planner");
                startActivity(comingSoonIntent);
                break;
        }

    }
}
