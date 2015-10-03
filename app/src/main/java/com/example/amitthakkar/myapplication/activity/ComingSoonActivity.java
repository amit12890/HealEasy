package com.example.amitthakkar.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amitthakkar.myapplication.AppController;
import com.example.amitthakkar.myapplication.R;


public class ComingSoonActivity extends Activity {



    private ImageView imgComingSoon;
    private TextView txtTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coming_soon);
        
        imgComingSoon = (ImageView) findViewById(R.id.img_coming_soon);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText(AppController.placeTypeMap.get(getIntent().getStringExtra("place_type")));

        if(getIntent().getStringExtra("place_type").equals("medical_equipments"))
            imgComingSoon.setImageResource(R.drawable.medical_equipments);
        else if(getIntent().getStringExtra("place_type").equals("health_planner"))
            imgComingSoon.setImageResource(R.drawable.health_planner);

    }



}
