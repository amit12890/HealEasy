package com.example.amitthakkar.myapplication.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.amitthakkar.myapplication.AppController;
import com.example.amitthakkar.myapplication.model.PlaceDetails;
import com.example.amitthakkar.myapplication.R;
import com.example.amitthakkar.myapplication.AppPreferences;
import com.example.amitthakkar.myapplication.utility.JSONParser;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SinglePlaceActivity extends Activity implements OnMapReadyCallback {
	// flag for Internet connection status
	private PlaceDetails place;
	Utility utility;
	private String latitude,longitude;
	private MapFragment mapFragment,fullMapFragment;
	private ImageView imgBack,imgSave, imgWhatsApp, imgFullScreen;
	private RelativeLayout layFullScreen;
	private boolean isMapFullScreen = false;
	private GoogleMap gMap;
	private PolylineOptions poly;
	private boolean isWhatsApp = false;
	private AppPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_place);

		place = (PlaceDetails) getIntent().getSerializableExtra("place");
		utility = new Utility(SinglePlaceActivity.this);
		preferences = new AppPreferences(SinglePlaceActivity.this);

		mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		fullMapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map_full);
		fullMapFragment.getView().setVisibility(View.VISIBLE);
		layFullScreen = (RelativeLayout)findViewById(R.id.lay_fullscreen);

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
					layFullScreen.setVisibility(View.GONE);
					isMapFullScreen = false;
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
				if(isMapFullScreen){
					takeScreenShot(R.id.lay_fullscreen,fullMapFragment);
				}else {
					takeScreenShot(R.id.lay_details,mapFragment);
				}
			}
		});

		imgWhatsApp = (ImageView) findViewById(R.id.img_whatsapp);
		imgWhatsApp.setColorFilter(Color.parseColor("#A2A2A2"));
		/*if(isPackageInstalled("com.whatsapp"))
			imgWhatsApp.setVisibility(View.VISIBLE);
		else
			imgWhatsApp.setVisibility(View.INVISIBLE);*/

		imgWhatsApp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//isWhatsApp = true;
				/*if(isMapFullScreen){
					takeScreenShot(R.id.lay_fullscreen,fullMapFragment);
				}else {
					takeScreenShot(R.id.lay_details,mapFragment);
				}*/
				shareOnWhatsApp(null);
			}
		});

		imgFullScreen = (ImageView) findViewById(R.id.img_fullscreen);
		imgFullScreen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				layFullScreen.setVisibility(View.VISIBLE);
				isMapFullScreen = true;
			}
		});

		latitude = place.getLatitude();
		longitude = place.getLongitude();


		parsePlaceDetails(place.getId());



	}

	public void parsePlaceDetails(final String place_id){
		utility.showLoadingDialog("Please Wait");
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				final String response = utility.getPlaceDetails(place_id);
				if(response.equals(Utility.ERROR)) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							utility.showErrorDialog(Utility.ERROR_MESSAGE, "OK", "");
							utility.errorDialog.getBuilder().callback(new MaterialDialog.ButtonCallback() {
								@Override
								public void onPositive(MaterialDialog dialog) {
									super.onPositive(dialog);
									finish();
								}
							});
							utility.hideLoadingDialog();
							return;
						}
					});
				}else if(response.equals(Utility.TIMEOUT_ERROR)) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							utility.showErrorDialog(Utility.TIMEOUT_ERROR_MESSAGE, "Try Again", "No");
							utility.errorDialog.getBuilder().callback(new MaterialDialog.ButtonCallback() {
								@Override
								public void onPositive(MaterialDialog dialog) {
									super.onPositive(dialog);
									parsePlaceDetails(place.getId());
								}

								@Override
								public void onNegative(MaterialDialog dialog) {
									super.onNegative(dialog);
									finish();
								}
							});
							utility.hideLoadingDialog();
							return;
						}
					});
				}
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
		if(utility.isNetworkAvailable())
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
		gMap = mapFragment.getMap();

		poly = new PolylineOptions()
				.color(Color.RED)
				.width(5)
				.visible(true)
				.zIndex(30);
		//gMap.addPolyline(poly);


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
					showDirection();
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

		ArrayList<LatLng> arrayList = new ArrayList<>();
		arrayList.add(new LatLng(Double.parseDouble(place.getLatitude()),Double.parseDouble(place.getLongitude())));
		PolylineOptions poly = new PolylineOptions()
				.color(Color.BLUE)
				.width(5)
				.visible(true)
				.zIndex(30);
		poly.addAll(arrayList);
		//gMap.addPolyline(poly);
		//mapFragment.getMap().addPolyline(poly);

		lbl_phone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + place.getPhone()));
				startActivity(intent);
			}
		});


	}

	@Override
	public void onBackPressed() {

		if(isMapFullScreen){
			layFullScreen.setVisibility(View.GONE);
			isMapFullScreen = false;
		}else {
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

	}


	public void takeScreenShot(final int layout_id,MapFragment mapFragment){

		GoogleMap gmap = mapFragment.getMap();
		GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback()
		{
			@Override
			public void onSnapshotReady(Bitmap snapshot) {
				try {

					View v1 = findViewById(layout_id);
					v1.setDrawingCacheEnabled(true);
					Bitmap backBitmap = Bitmap.createBitmap(v1.getDrawingCache());
					Bitmap bmOverlay = Bitmap.createBitmap(
							backBitmap.getWidth(), backBitmap.getHeight(),
							backBitmap.getConfig());

					Canvas canvas = new Canvas(bmOverlay);
					canvas.drawBitmap(backBitmap, 0, 0, null);
					canvas.drawBitmap(snapshot, new Matrix(), null);

					String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
							"/HealEasy";
					Long tsLong = System.currentTimeMillis()/1000;
					String ts = tsLong.toString();

					File dir = new File(filePath);
					if(!dir.exists())
						dir.mkdirs();
					File imageFile = new File(dir, "HealEasy_" + ts + ".png");

					FileOutputStream out = new FileOutputStream(imageFile);

					bmOverlay.compress(Bitmap.CompressFormat.PNG, 90, out);

					if(isWhatsApp)
						shareOnWhatsApp(imageFile);
					else
						openScreenshot(imageFile);


				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		gmap.snapshot(callback);

	}

	private void openScreenshot(File imageFile) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		Uri uri = Uri.fromFile(imageFile);
		intent.setDataAndType(uri, "image/*");
		startActivity(intent);
	}

	private void shareOnWhatsApp(File file){

		isWhatsApp = false;
		Uri uri = Uri.fromFile(file);

		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, place.getAddress().replace(",", "\n") +
				"\n\nPhone No: " + place.getPhone());

		//intent.setType("image/*");
		//intent.putExtra(Intent.EXTRA_STREAM, uri);
		intent.setPackage("com.whatsapp");
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		startActivity(intent);
	}

	private boolean isPackageInstalled(String packagename) {
		PackageManager pm = getPackageManager();
		try {
			pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}

	//Code for making direction API call and get all route between two points

	public void showDirection() {
		//utility.showLoadingDialog("Please Wait");
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				JSONParser jParser = new JSONParser();
				final String json = jParser.getJSONFromUrl(makeURL(Double.parseDouble(preferences.getLatitude()),
						Double.parseDouble(preferences.getLongitude()),
						Double.parseDouble(place.getLatitude()),
						Double.parseDouble(place.getLongitude())));

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//Log.d("Tag", response);
						utility.hideLoadingDialog();
						if (json != null) {
							drawPath(json.toString(),mapFragment.getMap());
							drawPath(json.toString(),fullMapFragment.getMap());
						}
					}
				});


			}
		});
		if (utility.isNetworkAvailable()) {
			t.start();

		}
	}

	public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
		StringBuilder urlString = new StringBuilder();
		urlString.append("https://maps.googleapis.com/maps/api/directions/json");
		urlString.append("?origin=");// from
		urlString.append(Double.toString(sourcelat));
		urlString.append(",");
		urlString
				.append(Double.toString(sourcelog));
		urlString.append("&destination=");// to
		urlString
				.append(Double.toString(destlat));
		urlString.append(",");
		urlString.append(Double.toString(destlog));
		urlString.append("&sensor=false&mode=driving&alternatives=true");
		urlString.append("&key="+ AppController.API_KEY);
		return urlString.toString();
	}

	public void drawPath(String  result,GoogleMap mMap) {

		try {
			//Tranform the string into a json object
			final JSONObject json = new JSONObject(result);
			JSONArray routeArray = json.getJSONArray("routes");
			JSONObject routes = routeArray.getJSONObject(0);
			JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
			String encodedString = overviewPolylines.getString("points");
			List<LatLng> list = decodePoly(encodedString);
			Polyline line = mMap.addPolyline(new PolylineOptions()
							.addAll(list)
							.width(12)
							.color(Color.parseColor("#E61A5F"))//Google maps blue color
							.geodesic(true)
			);

		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private List<LatLng> decodePoly(String encoded) {

		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng( (((double) lat / 1E5)),
					(((double) lng / 1E5) ));
			poly.add(p);
		}

		return poly;
	}

}
