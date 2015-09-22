package com.example.amitthakkar.myapplication;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amitthakkar.myapplication.utility.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.rey.material.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Param;
import se.walkercrou.places.Place;
import se.walkercrou.places.exception.NoResultsFoundException;

public class PlaceListActivity extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,OnMapReadyCallback {

    private GoogleApiClient mGoogleApiClient;
    private String latitude,longitude;
    GooglePlaces gPlace;
    Utility util;
    String type = "hospital";
    private List<Place> placesList;
    private ArrayList<PlaceDetails> placesDetailList;
    private ListView placeListView;
    private EditText edSearch;
    private ImageView imgSearch;
    private TextView txtNoData,txtTitle;
    private PlaceDetails placeDetails;
    private String PAGE_TOKEN="";
    private boolean has_more_data = false;
    PlaceListAdapter placeListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(PlaceListActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        util = new Utility(PlaceListActivity.this);
        type = getIntent().getStringExtra("place_type");
        gPlace = new GooglePlaces(AppController.API_KEY);
        placeDetails = new PlaceDetails();
        placesList = new ArrayList<>();
        placesDetailList = new ArrayList<>();
        placeListAdapter = new PlaceListAdapter(PlaceListActivity.this,R.id.list_item,placesDetailList);
        placeListView = (ListView) findViewById(R.id.list);
        edSearch = (EditText) findViewById(R.id.edt_city);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText(AppController.placeTypeMap.get(type));
        txtNoData = (TextView) findViewById(R.id.txt_no_data);
        imgSearch = (ImageView) findViewById(R.id.img_search);
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placesList.clear();

                getPlacesByQuery(edSearch.getText().toString());
            }
        });
        placeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlaceDetails place = placesDetailList.get(position);
                //clonePlaceObject(place);
                Intent i = new Intent(PlaceListActivity.this, SinglePlaceActivity.class);
                i.putExtra("place", placeDetails);
                startActivity(i);
            }
        });
        placeListView.setAdapter(placeListAdapter);

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("TAG", "Connected");

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            AppController.LATITUDE = mLastLocation.getLatitude();
            AppController.LONGITUDE = mLastLocation.getLongitude();

        }

        getPlacesList("Ahmedabad", PAGE_TOKEN);
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
    public void onMapReady(GoogleMap googleMap) {
        Log.d("TAG", "Connected");
    }


    private void getPlacesByQuery(final String query) {
        util.showLoadingDialog("Please Wait");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    placesList = gPlace.getPlacesByQuery(type+"in"+query,GooglePlaces.MAXIMUM_RESULTS);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtNoData.setVisibility(View.GONE);
                            for (int i = 0; i < placesList.size(); i++) {
                                Log.d("PlaceDetails " + String.valueOf(i), placesList.get(i).getName());
                                util.hideLoadingDialog();
                            }


                        }

                    });
                }catch (Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.hideLoadingDialog();
                            txtNoData.setVisibility(View.VISIBLE);
                        }
                    });

                }



            }
        });

        if (Utility.isNetworkAvailable(PlaceListActivity.this, util)) {
            t.start();

        }


    }

    private void getPlacesList(final String query,final String page_token) {
        util.showLoadingDialog("Please Wait");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    String response = util.getPlaceList(type+"in"+query,page_token);

                    JSONObject responseObj = new JSONObject(response);
                    if(responseObj.has("status")){
                        if(responseObj.getString("status").equals("OK")){
                            if(responseObj.has("next_page_token")){
                                has_more_data = true;
                                PAGE_TOKEN = responseObj.getString("next_page_token");

                            }else{
                                has_more_data = false;
                            }

                            if(responseObj.has("results")){
                                if(responseObj.get("results") instanceof JSONArray){
                                    JSONArray resultsArray = responseObj.getJSONArray("results");
                                    for (int i = 0; i < resultsArray.length(); i++) {
                                        JSONObject resultObj = resultsArray.getJSONObject(i);
                                        placesDetailList.add(parsePlaces(resultObj));
                                    }
                                }else{
                                    JSONObject resultObj = responseObj.getJSONObject("results");
                                    placesDetailList.add(parsePlaces(resultObj));
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        util.hideLoadingDialog();
                                        placeListAdapter.addAll(placesDetailList);
                                        placeListView.setAdapter(placeListAdapter);
                                        placeListAdapter.notifyDataSetChanged();
                                    }
                                });


                            }
                        }
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                util.hideLoadingDialog();
                                txtNoData.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.hideLoadingDialog();
                            txtNoData.setVisibility(View.VISIBLE);
                        }
                    });

                }



            }
        });

        if (Utility.isNetworkAvailable(PlaceListActivity.this, util)) {
            t.start();

        }


    }

    public PlaceDetails parsePlaces(JSONObject areaObj){
        PlaceDetails placeDetails = new PlaceDetails();

        try{
            placeDetails.setName(areaObj.getString("name"));
            placeDetails.setId(areaObj.getString("place_id"));
            placeDetails.setAddress(areaObj.getString("formatted_address"));
        }catch (JSONException e){
            e.printStackTrace();
        }
        return  placeDetails;
    }


    public void clonePlaceObject(Place place){
        placeDetails.setId(place.getPlaceId());
        placeDetails.setName(place.getName());
        placeDetails.setPhone(place.getPhoneNumber());
        placeDetails.setInternationalPhone(place.getInternationalPhoneNumber());
        placeDetails.setWebsite(place.getWebsite());
       // placeDetails.setIsAlwaysOpened(String.valueOf(place.isAlwaysOpened()));
        placeDetails.setStatus(place.getStatus().toString());
        placeDetails.setGooglePlaceURI(place.getGoogleUrl());
        //placeDetails.setPrice(String.valueOf(place.getPrice()));
        placeDetails.setAddress(place.getAddress());
        placeDetails.setVicinity(place.getVicinity());
        placeDetails.setLatitude(String.valueOf(place.getLatitude()));
        placeDetails.setLongitude(String.valueOf(place.getLongitude()));


    }
}
