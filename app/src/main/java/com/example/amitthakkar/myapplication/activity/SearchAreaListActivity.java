package com.example.amitthakkar.myapplication.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.amitthakkar.myapplication.DatabaseHandler;
import com.example.amitthakkar.myapplication.R;
import com.example.amitthakkar.myapplication.AppPreferences;
import com.example.amitthakkar.myapplication.adapter.AreaListAdapter;
import com.example.amitthakkar.myapplication.adapter.StateListAdapter;
import com.example.amitthakkar.myapplication.model.Area;
import com.example.amitthakkar.myapplication.utility.Utility;


import java.util.ArrayList;

public class SearchAreaListActivity extends AppCompatActivity {


    Utility util;
    private ImageView imgBack;
    private ListView areaListView;
    public static EditText edSearch;
    private AppPreferences preferences;
    private DatabaseHandler dbHandler;
    private StateListAdapter stateListAdapter;
    private AreaListAdapter areaListAdapter;
    private ArrayList<String> stateList;
    private ArrayList<Area> areaList;
    private String SEARCH_TYPE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_area_list);

        util = new Utility(SearchAreaListActivity.this);
        preferences = new AppPreferences(SearchAreaListActivity.this);
        dbHandler = new DatabaseHandler(SearchAreaListActivity.this);
        SEARCH_TYPE = getIntent().getStringExtra("search_type");

        areaListView = (ListView) findViewById(R.id.list_place);
        edSearch = (EditText) findViewById(R.id.edt_filter_area);
        imgBack = (ImageView) findViewById(R.id.img_back);

        if(SEARCH_TYPE.equals("State")){
            stateList = new ArrayList<>();
            stateList = dbHandler.getStateList();
            stateListAdapter = new StateListAdapter(SearchAreaListActivity.this,stateList);
            areaListView.setAdapter(stateListAdapter);
        }else {
            areaList = new ArrayList<>();
            areaList = dbHandler.getAreaList(getIntent().getStringExtra("state_name"));
            areaListAdapter = new AreaListAdapter(SearchAreaListActivity.this,areaList);
            areaListView.setAdapter(areaListAdapter);

        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        edSearch.setHint("Search " + SEARCH_TYPE);
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (SEARCH_TYPE.equals("State")) {
                    if (edSearch.getText().hashCode() == s.hashCode()) {
                        stateListAdapter.getFilter().filter(s.toString());
                    }
                } else {
                    if (edSearch.getText().hashCode() == s.hashCode()) {
                        areaListAdapter.getFilter().filter(s.toString());
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        areaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(SEARCH_TYPE.equalsIgnoreCase("State")){
                    DashBoardActivity.STATE_NAME = (String) areaListView.getItemAtPosition(position);
                    DashBoardActivity.txtState.setText(DashBoardActivity.STATE_NAME);

                }else{
                    DashBoardActivity.selected_area = (Area) areaListView.getItemAtPosition(position);
                    DashBoardActivity.txtAreaName.setText(DashBoardActivity.selected_area.getArea_name()+", "+
                                        DashBoardActivity.selected_area.getArea_city());
                    DashBoardActivity.AREA_NAME = DashBoardActivity.selected_area.getArea_name()+", "+
                            DashBoardActivity.selected_area.getArea_city();
                }
                finish();
            }
        });




    }


    @Override
    protected void onPause(){
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }
}
