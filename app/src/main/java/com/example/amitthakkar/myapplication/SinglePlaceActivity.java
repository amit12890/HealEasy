package com.example.amitthakkar.myapplication;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.amitthakkar.myapplication.utility.Utility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import se.walkercrou.places.Place;

public class SinglePlaceActivity extends Activity implements OnMapReadyCallback {
	// flag for Internet connection status
	private PlaceDetails place;
	Utility utility;
	private String latitude,longitude;
	private MapFragment mapFragment,fullMapFragment;
	private ImageView imgBack,imgSave;
	private boolean isMapFullScreen = false;
	private GoogleMap gMap;
	private PolylineOptions poly;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_place);

		place = (PlaceDetails) getIntent().getSerializableExtra("place");


		mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		fullMapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map_full);
		fullMapFragment.getView().setVisibility(View.GONE);

		GoogleMapOptions options = new GoogleMapOptions();
		options.zoomControlsEnabled(true)
				.rotateGesturesEnabled(false);
		mapFragment.getMapAsync(this);
		fullMapFragment.getMapAsync(this);

		imgBack = (ImageView) findViewById(R.id.img_back);
		imgBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isMapFullScreen) {
					fullMapFragment.getView().setVisibility(View.GONE);
				} else {
					onBackPressed();
				}

			}
		});

		mapFragment.getView().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fullMapFragment.getView().setVisibility(View.VISIBLE);
				isMapFullScreen = true;
			}
		});

		imgSave = (ImageView) findViewById(R.id.img_save);
		imgSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				takeScreenShot();
			}
		});

		latitude = place.getLatitude();
		longitude = place.getLongitude();

		utility = new Utility(SinglePlaceActivity.this);
		parsePlaceDetails(place.getId());



	}

	public void parsePlaceDetails(final String place_id){
		utility.showLoadingDialog("Please Wait");
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				final String response = utility.getPlaceDetails(place_id);

				try{
					JSONObject jsonObject = new JSONObject(response);

					if(jsonObject.has("status")){
						if(jsonObject.getString("status").equals("OK")){
							if(jsonObject.has("result")){
								JSONObject resultObj = jsonObject.getJSONObject("result");
								parseResultObj(resultObj);
							}
						}
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							//Log.d("Tag", response);
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
				.title(place.getAddress().replace(",", "\n"))
				.position(current));
		gMap = googleMap;

		poly = new PolylineOptions()
				.color(Color.RED)
				.width(5)
				.visible(true)
				.zIndex(30);
		gMap.addPolyline(poly);


	}

	public void parseResultObj(JSONObject resultObj){
		try {
			if(resultObj.has("formatted_address"))
				place.setAddress(resultObj.getString("formatted_address"));
			else
				place.setAddress("N/A");

			if(resultObj.has("formatted_phone_number"))
				place.setPhone(resultObj.getString("formatted_phone_number"));
			else
				place.setPhone("N/A");

			if(resultObj.has("international_phone_number"))
				place.setInternationalPhone(resultObj.getString("international_phone_number"));
			else
				place.setInternationalPhone("N/A");

			if(resultObj.has("website"))
				place.setWebsite(resultObj.getString("website"));
			else
				place.setWebsite("N/A");

			if(resultObj.has("rating"))
				place.setRating(resultObj.getInt("rating"));
			else
				place.setRating(0);

			if(resultObj.has("reviews"))
				place.setReviews(resultObj.getJSONArray("reviews").length());
			else
				place.setReviews(0);



			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					fillPlaceDetails();
				}
			});


		}catch (JSONException e){
			e.printStackTrace();
		}
	}

	private void fillPlaceDetails(){

		TextView lbl_name = (TextView) findViewById(R.id.name);
		TextView lbl_address = (TextView) findViewById(R.id.address);
		TextView lbl_phone = (TextView) findViewById(R.id.phone);
		TextView lbl_distance = (TextView) findViewById(R.id.distance);
		TextView lbl_rating = (TextView) findViewById(R.id.rating);
		TextView lbl_review = (TextView) findViewById(R.id.review);

		lbl_name.setText(place.getName());
		lbl_address.setText(place.getAddress().replace(",", "\n"));
		lbl_phone.setText(place.getPhone());

		lbl_rating.setText(String.valueOf(place.getRating()));
		lbl_review.setText(String.valueOf(place.getReviews()));

		float distance = place.getDistance();
		String distance_string = String.format("%.02f", distance);
		lbl_distance.setText("Distance: " + distance_string + " KM AWAY");

		//gMap.clear();
		/*Polyline line = gMap.addPolyline(new PolylineOptions()
				.add(new LatLng(Double.parseDouble(place.getLatitude()),Double.parseDouble(place.getLongitude())),
						new LatLng(AppController.LATITUDE, AppController.LONGITUDE))
				.width(10)
				.color(Color.BLUE));*/

		PolylineOptions poly = new PolylineOptions()
				.color(Color.BLUE)
				.width(5)
				.visible(true)
				.zIndex(30);
		gMap.addPolyline(poly);
		poly.add(new LatLng(22.69391261, 72.85596371));
		poly.add(new LatLng(22.69178, 72.8678));

		Polyline line = gMap.addPolyline(new PolylineOptions()
				.add(new LatLng(51.5, -0.1), new LatLng(40.7, -74.0))
				.width(5)
				.color(Color.RED));


	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		FragmentManager fm = getFragmentManager();
		Fragment fragment = (fm.findFragmentById(R.id.map));
		FragmentTransaction ft = fm.beginTransaction();
		ft.remove(fragment);
		ft.commit();

		Fragment fragment1 = (fm.findFragmentById(R.id.map_full));
		FragmentTransaction ft1 = fm.beginTransaction();
		ft1.remove(fragment1);
		ft1.commit();
	}


	public void takeScreenShot(){
		Date now = new Date();
		android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

		try {
			// image naming and path  to include sd card  appending name you choose for file
			String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
					"/HealEasy";
			Long tsLong = System.currentTimeMillis()/1000;
			String ts = tsLong.toString();

			File dir = new File(file_path);
			if(!dir.exists())
				dir.mkdirs();
			File imageFile = new File(dir, "HealEasy_" + ts + ".jpg");

			// create bitmap screen capture
			//View v1 = getWindow().getDecorView().getRootView();
			View v1 = findViewById(R.id.lay_details);
			v1.setDrawingCacheEnabled(true);
			v1.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
					View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
			//v1.layout(0, 0, v1.getMeasuredWidth(), v1.getMeasuredHeight());
			v1.buildDrawingCache(true);

			mapFragment.getView().buildDrawingCache();
			mapFragment.getView().setDrawingCacheEnabled(true);
			mapFragment.getView().measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
					View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

			Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());

			FileOutputStream outputStream = new FileOutputStream(imageFile);
			int quality = 100;
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
			outputStream.flush();
			outputStream.close();

			v1.destroyDrawingCache();

			openScreenshot(imageFile);
		} catch (Throwable e) {
			// Several error may come out with file handling or OOM
			e.printStackTrace();
		}
	}

	private void openScreenshot(File imageFile) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		Uri uri = Uri.fromFile(imageFile);
		intent.setDataAndType(uri, "image/*");
		startActivity(intent);
	}

	private File saveBitmapAsFile(Bitmap bmp){
		String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
				"/HealEasy";
		Long tsLong = System.currentTimeMillis()/1000;
		String ts = tsLong.toString();

		File dir = new File(file_path);
		if(!dir.exists())
			dir.mkdirs();
		File file = new File(dir, "HealEasy_" + ts + ".png");
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		return dir;
	}
}
