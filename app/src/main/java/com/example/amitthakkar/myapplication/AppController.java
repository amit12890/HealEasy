package com.example.amitthakkar.myapplication;

import android.app.Application;


import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by amit.thakkar on 7/6/2015.
 */
public class AppController extends Application{

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "xXmScGx8IFLvQGoKJCdF5PCYD";
    private static final String TWITTER_SECRET = "G5SKk1LIAON5eIiPCZCXhpXmSgbSKZJQF8V3I8ttcNeRZgEGSV";


    public static final String TAG = AppController.class.getSimpleName();
    public static String URL = "http://10.16.16.121/coupcommerce/jsonconnect";
    //public static String URL = "http://coupcommerce.magentoprojects.net/jsonconnect";
    public static HashMap<String,String> FACEBOOK_DETAILS;
    public static HashMap<String,String> TWITTER_DETAILS;
    public static HashMap<String,String> GOOGLE_P_DETAILS;
    public static HashMap<String,String> LINKED_IN_DETAILS;
    public static String PRIMARY_COLOR="",SECONDARY_COLOR="";
    public static double LATITUDE=23.022505,LONGITUDE=72.5713621,RADIUS= 5000;
    public static String URI = "https://maps.googleapis.com/maps/api/";
    public static String API_KEY = "AIzaSyABRDkeNFSEG-0QrbUfIdqxD9VRXBEXuD8";
    public static HashMap<String,String> placeTypeMap = new HashMap<>();



    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        //TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_DETAILS.get(""), TWITTER_DETAILS.get(""));
        //Fabric.with(this, new Crashlytics(), new Twitter(authConfig));
        mInstance = this;

        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);


        FACEBOOK_DETAILS = new HashMap<>();
        TWITTER_DETAILS = new HashMap<>();
        GOOGLE_P_DETAILS = new HashMap<>();
        LINKED_IN_DETAILS = new HashMap<>();

        placeTypeMap.put("hospitals","HOSPITALS");
        placeTypeMap.put("medical_store","MEDICAL STORES");
        placeTypeMap.put("labs","DIAGNOSTICS CENTERS");
        placeTypeMap.put("medical_equipments","MEDICAL EQUIPMENTS");
        placeTypeMap.put("health_planner","HEALTH PLANNER");
        // END - UNIVERSAL IMAGE LOADER SETUP
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }


}
