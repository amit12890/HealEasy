package com.example.amitthakkar.myapplication.utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.amitthakkar.myapplication.AppController;
import com.rey.material.widget.ProgressView;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by amit.thakkar on 7/6/2015.
 */
public class Utility {

    Activity activity;
    public static MaterialDialog errorDialog,loadingDialog;
    public static ProgressView progressView;
    RelativeLayout progressLay;
    private boolean isDailogueVisible = false;
    public Dialog progressDialog;
    DefaultHttpClient client;
    protected String response;
    ArrayList<NameValuePair> headers;

    public static String TIMEOUT_ERROR = "timeout_error",ERROR = "error";
    public static String TIMEOUT_ERROR_MESSAGE = "It takes longer time than expected.\n" +
            "Please try again later.";
    public static String ERROR_MESSAGE = "Some error has occurred.\n Please try again later";

    final int TIMEOUT = 10;

    public static org.apache.http.client.CookieStore cookieStore;

    public Utility(Activity activity){
        this.activity = activity;

        client = new DefaultHttpClient();
        client.setCookieStore(cookieStore);

        HttpConnectionParams.setConnectionTimeout(client.getParams(), TIMEOUT * 1000);
        HttpConnectionParams.setSoTimeout(client.getParams(), TIMEOUT * 1000);
        response = "";
        headers = new ArrayList<NameValuePair>();

       // progressDialog = new Dialog(activity,android.R.style.Theme_Black_NoTitleBar);

       // showAnimatedLogoProgressBar();

    }


    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param activity Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Activity activity){
        Resources resources = activity.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param activity Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Activity activity){
        Resources resources = activity.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public String loadJSONFromAsset(String path) {
        String json = null;
        try {

            InputStream is = activity.getAssets().open(path);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    public void showErrorDialog(String message,String positiveText,String negativeText){
        errorDialog = new MaterialDialog.Builder(activity)
                .content(message)
                .positiveText(positiveText)
                .negativeText(negativeText).build();

        if(!errorDialog.isShowing()){
            errorDialog.show();
        }

    }

    public void hideErrorDialog(){
        errorDialog.dismiss();
    }

    public void showLoadingDialog(String message){

        progressView = new ProgressView(activity);

        loadingDialog = new MaterialDialog.Builder(activity)
                .content(message)
                .progress(true, 0)
                .cancelable(false)
                .build();
        if(!loadingDialog.isShowing()){
            loadingDialog.show();
            //progressView.start();
        }


    }

    public void hideLoadingDialog(){
        loadingDialog.dismiss();
        //progressView.stop();
    }

    public String getPlaceList(String query,String page_token) {

        HttpGet request;
        try {
            if(page_token.length() > 0){
                request = new HttpGet(AppController.URI+"place/textsearch/json?query="+query+"&pagetoken="+
                        page_token+"&key="+ AppController.API_KEY);
            }else{
                request = new HttpGet(AppController.URI+"place/textsearch/json?query="+query+"&key="+ AppController.API_KEY);
            }


            //request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);

            client.getConnectionManager().shutdown();
            Log.d("Place Details", response);
            return response;

        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }
    }

    public String getPlaceDetails(String placeId) {

        try {
            HttpGet request = new HttpGet(AppController.URI+"place/details/json?placeid="+placeId+"&key="+ AppController.API_KEY);

            //request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);


            client.getConnectionManager().shutdown();
            Log.d("Place Details", response);
            return response;

        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }
    }





    public void getHashKey() {

        // Add code to print out the key hash
        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo(
                    "com.fashion.krish",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void changeStatusBarColor(Activity activity,int color){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            float[] hsv = new float[3];

            Color.colorToHSV(color, hsv);
            hsv[2] *= 0.8f; // value component
            color = Color.HSVToColor(hsv);

            Window window = activity.getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
            window.setStatusBarColor(color);
        }

    }


    public boolean isDailogueVisible(){
        return isDailogueVisible;
    }

    public int getScreenWidth(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        return displaymetrics.widthPixels;
    }

    public int getScreenHeight(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        return displaymetrics.heightPixels;
    }

    public static boolean isNetworkAvailable(Activity activity, final Utility utility){
        try {

            boolean haveConnectedWifi = false;
            boolean haveConnectedMobile = false;

            ConnectivityManager cm = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }
            if(!(haveConnectedWifi || haveConnectedMobile))
            {
                if(utility!=null)
                {
                    new Handler().post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub

                        }
                    });

                }
                //Toast.makeText(activity, activity.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                //showErrorDialog("Error",activity.getString(R.string.no_internet), activity);
            }
            return haveConnectedWifi || haveConnectedMobile;

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
    }
}
