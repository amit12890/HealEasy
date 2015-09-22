package com.example.amitthakkar.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteAssetHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "zipcode.sqlite";

	// Contacts table name
	private static final String TABLE_NAME = "zipcode";

	private static String DB_PATH = "/data/data/com.example.amitthakkar.myapplication/";

	// Contacts Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_AREA_NAME = "area_name";
	private static final String KEY_AREA_CITY = "area_city";
	private static final String KEY_AREA_DISTRICT = "area_district";
	private static final String KEY_AREA_STATE = "area_state";
	private static final String KEY_AREA_COUNTRY_CODE = "area_country_code";
	private static final String KEY_AREA_ZIPCODE = "area_zipcode";
	private static final String KEY_AREA_LAT = "latitude";
	private static final String KEY_AREA_LON = "longitude";

	private SQLiteDatabase myDatabase;


	public DatabaseHandler(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

	}

	public Cursor getAreaNames() {

		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String [] sqlSelect = {KEY_AREA_ZIPCODE,KEY_AREA_NAME,KEY_AREA_CITY,KEY_AREA_DISTRICT};
		String sqlTables = TABLE_NAME;

		qb.setTables(sqlTables);
		Cursor c = qb.query(db, sqlSelect, null, null,
				null, null, null);

		c.moveToFirst();
		return c;

	}

	// Getting All Contacts
	public List<Area> getAllAreas() {
		List<Area> areaList = new ArrayList<Area>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_NAME;

		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Area area = new Area();
				area.setArea_country_code(cursor.getString(0));
				area.setArea_zipcode(cursor.getString(1));
				area.setArea_name(cursor.getString(2));
				area.setArea_state(cursor.getString(3));
				area.setArea_district(cursor.getString(4));
				area.setArea_city(cursor.getString(5));
				area.setLatitude(Double.valueOf(cursor.getString(6)));
				area.setLongitude(Double.valueOf(cursor.getString(7)));

				areaList.add(area);
			} while (cursor.moveToNext());
		}

		// return contact list
		return areaList;
	}

	public List<Area> getAllAreasForCity(String city) {
		List<Area> areaList = new ArrayList<Area>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_NAME+" WHERE ";

		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Area area = new Area();
				area.setArea_country_code(cursor.getString(0));
				area.setArea_zipcode(cursor.getString(1));
				area.setArea_name(cursor.getString(2));
				area.setArea_state(cursor.getString(3));
				area.setArea_district(cursor.getString(4));
				area.setArea_city(cursor.getString(5));
				area.setLatitude(Double.valueOf(cursor.getString(6)));
				area.setLongitude(Double.valueOf(cursor.getString(7)));

				areaList.add(area);
			} while (cursor.moveToNext());
		}

		// return contact list
		return areaList;
	}


}