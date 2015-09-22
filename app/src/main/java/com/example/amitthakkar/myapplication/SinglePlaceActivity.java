package com.example.amitthakkar.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.example.amitthakkar.myapplication.utility.Utility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import se.walkercrou.places.Place;

public class SinglePlaceActivity extends Activity implements OnMapReadyCallback {
	// flag for Internet connection status
	private PlaceDetails place;
	Utility utility;
	private String latitude,longitude;
	private MapFragment mapFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_place);

		place = (PlaceDetails) getIntent().getSerializableExtra("place");
		TextView lbl_name = (TextView) findViewById(R.id.name);
		TextView lbl_address = (TextView) findViewById(R.id.address);
		TextView lbl_phone = (TextView) findViewById(R.id.phone);
		TextView lbl_location = (TextView) findViewById(R.id.location);

		mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		GoogleMapOptions options = new GoogleMapOptions();
		options.zoomControlsEnabled(true)
				.rotateGesturesEnabled(false);
		mapFragment.getMapAsync(this);

		latitude = place.getLatitude();
		longitude = place.getLongitude();
		lbl_name.setText(place.getName());
		lbl_address.setText(place.getAddress());
		lbl_phone.setText(place.getPhone());

		utility = new Utility(SinglePlaceActivity.this);
		parsePlaceDetails(place.getId());
		//lbl_name.setText(place.getName());


	}


	public void parsePlaceDetails(final String place_id){
		utility.showLoadingDialog("Please Wait");
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				final String response = utility.getPlaceDetails(place_id);

				try{
					JSONObject jsonObject = new JSONObject(response);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Log.d("Tag", response);
							utility.hideLoadingDialog();
						}
					});

				}catch (JSONException e){
					e.printStackTrace();
				}

			}
		});
		if(utility.isNetworkAvailable(SinglePlaceActivity.this, utility))
		{
			t.start();

		}

	}

	@Override
	public void onMapReady(GoogleMap googleMap) {

		LatLng current = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
		googleMap.getUiSettings().setZoomControlsEnabled(true);
		googleMap.setMyLocationEnabled(true);
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 13));

		googleMap.addMarker(new MarkerOptions()
				.title(place.getAddress())
				.position(current));

	}
}
