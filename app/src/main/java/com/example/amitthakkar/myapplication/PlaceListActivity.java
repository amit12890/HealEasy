package com.example.amitthakkar.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.amitthakkar.myapplication.utility.AppPreferences;
import com.example.amitthakkar.myapplication.utility.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.rey.material.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Place;

public class PlaceListActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {


    private String latitude,longitude;
    GooglePlaces gPlace;
    private GoogleApiClient mGoogleApiClient;
    Utility util;
    private ImageView imgBack;
    String type = "hospital";
    private List<Place> placesList;
    private ArrayList<PlaceDetails> placesDetailList,tempArrayList;
    private ListView placeListView;
    private EditText edSearch;
    private ImageView imgSearch;
    private TextView txtNoData,txtTitle;
    private PlaceDetails placeDetails;
    private String PAGE_TOKEN="";
    private boolean has_more_data = false;
    private boolean is_loading_list =false;
    PlaceListAdapter placeListAdapter;
    LinearLayout layLoading ;
    String query;
    private AppPreferences preferences;
    private LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        util = new Utility(PlaceListActivity.this);
        preferences = new AppPreferences(PlaceListActivity.this);

        mGoogleApiClient = new GoogleApiClient.Builder(PlaceListActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        type = getIntent().getStringExtra("place_type");
        gPlace = new GooglePlaces(AppController.API_KEY);
        placeDetails = new PlaceDetails();
        placesList = new ArrayList<>();
        placesDetailList = new ArrayList<>();
        tempArrayList = new ArrayList<>();
        placeListAdapter = new PlaceListAdapter(PlaceListActivity.this,R.id.list_item,placesDetailList);

        layLoading = (LinearLayout) findViewById(R.id.lay_loading);
        placeListView = (ListView) findViewById(R.id.list);
        edSearch = (EditText) findViewById(R.id.edt_city);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText(AppController.placeTypeMap.get(type));

        imgBack = (ImageView) findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String hintType = AppController.placeTypeMap.get(type);
        hintType = String.valueOf(hintType.charAt(0)).toUpperCase() + hintType.substring(1, hintType.length()).toLowerCase();
        edSearch.setHint("Search " + hintType);
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(placesDetailList.size() > 0 && s.length() == 0){
                    placesList.clear();
                    placesDetailList.clear();
                    PAGE_TOKEN = "";
                    query = getCityName(Double.parseDouble(preferences.getLatitude()),Double.parseDouble(preferences.getLongitude()));
                    getPlacesList(query,PAGE_TOKEN);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtNoData = (TextView) findViewById(R.id.txt_no_data);
        imgSearch = (ImageView) findViewById(R.id.img_search);
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edSearch.getText().toString().trim().length() > 0) {
                    placesList.clear();
                    placesDetailList.clear();
                    PAGE_TOKEN = "";
                    query = edSearch.getText().toString();
                    getPlacesList(query, PAGE_TOKEN);
                }

            }
        });

        placeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlaceDetails place = (PlaceDetails) placeListView.getItemAtPosition(position);
                //clonePlaceObject(place);

                Intent i = new Intent(PlaceListActivity.this, SinglePlaceActivity.class);
                i.putExtra("place", place);
                startActivity(i);
            }
        });

        placeListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int l = visibleItemCount + firstVisibleItem;
                if (has_more_data) {
                    if (l >= totalItemCount && !is_loading_list) {

                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getPlacesList(query, PAGE_TOKEN);
                            }
                        }, 3000);

                        is_loading_list = true;
                        layLoading.setVisibility(View.VISIBLE);
                        //layLoading.setVisibility(View.VISIBLE);
                    }
                }

            }
        });

        placeListView.setAdapter(placeListAdapter);


    }

    private void getPlacesList(final String query,final String page_token) {
        tempArrayList.clear();
        if(page_token.length() == 0)
            util.showLoadingDialog("Please Wait");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = util.getPlaceList(type+query.replace(" ",""),page_token);
                    if(response.equals(Utility.ERROR)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                util.showErrorDialog(Utility.ERROR_MESSAGE, "OK", "");
                                util.errorDialog.getBuilder().callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        super.onPositive(dialog);
                                        finish();
                                    }
                               });
                                util.hideLoadingDialog();
                                return;
                            }
                        });
                    }else if(response.equals(Utility.TIMEOUT_ERROR)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                util.showErrorDialog(Utility.TIMEOUT_ERROR_MESSAGE, "Try Again", "No");
                                util.errorDialog.getBuilder().callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        super.onPositive(dialog);
                                        String query = getCityName(Double.parseDouble(preferences.getLatitude()),
                                                Double.parseDouble(preferences.getLongitude()));
                                        getPlacesList(query, PAGE_TOKEN);
                                    }

                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        super.onNegative(dialog);
                                        finish();
                                    }
                                });
                                util.hideLoadingDialog();
                                return;
                            }
                        });
                    }
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
                                        tempArrayList.add(parsePlaces(resultObj));

                                    }
                                }else{
                                    JSONObject resultObj = responseObj.getJSONObject("results");
                                    //placesDetailList.add(parsePlaces(resultObj));
                                    tempArrayList.add(parsePlaces(resultObj));
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        util.hideLoadingDialog();
                                        placeListAdapter.addAll(tempArrayList);
                                        //placeListView.setAdapter(placeListAdapter);
                                        placeListAdapter.notifyDataSetChanged();
                                        //placesDetailList.addAll(tempArrayList);
                                        txtNoData.setVisibility(View.GONE);
                                        is_loading_list = false;
                                        layLoading.setVisibility(View.GONE);
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

        if (util.isNetworkAvailable()) {
            t.start();

        }


    }

    public PlaceDetails parsePlaces(JSONObject areaObj){
        PlaceDetails placeDetails = new PlaceDetails();

        try{
            placeDetails.setName(areaObj.getString("name"));
            placeDetails.setId(areaObj.getString("place_id"));
            placeDetails.setAddress(areaObj.getString("formatted_address"));
            placeDetails.setLatitude(areaObj.getJSONObject("geometry").getJSONObject("location").getString("lat"));
            placeDetails.setLongitude(areaObj.getJSONObject("geometry").getJSONObject("location").getString("lng"));

            Location homeLocation = new Location("");
            homeLocation.setLatitude(Double.parseDouble(preferences.getLatitude()));
            homeLocation.setLongitude(Double.parseDouble(preferences.getLongitude()));

            Location placeLocation = new Location("");
            placeLocation.setLatitude(Double.parseDouble(placeDetails.getLatitude()));
            placeLocation.setLongitude(Double.parseDouble(placeDetails.getLongitude()));

            placeDetails.setDistance(homeLocation.distanceTo(placeLocation) / 1000);

        }catch (JSONException e){
            e.printStackTrace();
        }
        return  placeDetails;
    }

    public String getCityName(Double latitude,Double longitude){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String cityName = addresses.get(0).getAddressLine(0);
        String stateName = addresses.get(0).getAddressLine(1);
        String countryName = addresses.get(0).getAddressLine(2);

        return addresses.get(0).getLocality();
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
            query = getCityName(Double.parseDouble(preferences.getLatitude()),Double.parseDouble(preferences.getLongitude()));
            getPlacesList(query, PAGE_TOKEN);
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
            resetList();
            preferences.setLatitude(String.valueOf(mLastLocation.getLatitude()));
            preferences.setLongitude(String.valueOf(mLastLocation.getLongitude()));
            query = getCityName(Double.parseDouble(preferences.getLatitude()),Double.parseDouble(preferences.getLongitude()));
            getPlacesList(query, PAGE_TOKEN);
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

    public void resetList(){
        placesList.clear();
        placesDetailList.clear();
        PAGE_TOKEN = "";
    }

}
