package com.example.amitthakkar.myapplication.model;

import java.io.Serializable;

/**
 * Created by amit.thakkar on 9/18/2015.
 */
public class Area implements Serializable {
    public String id = "";
    public String area_name = "";
    public String area_city = "";
    public String area_district = "";
    public String area_state = "";
    public String area_country_code = "";
    public String area_zipcode = "";
    public Double latitude;
    public Double longitude ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getArea_city() {
        return area_city;
    }

    public void setArea_city(String area_city) {
        this.area_city = area_city;
    }

    public String getArea_district() {
        return area_district;
    }

    public void setArea_district(String area_district) {
        this.area_district = area_district;
    }

    public String getArea_state() {
        return area_state;
    }

    public void setArea_state(String area_state) {
        this.area_state = area_state;
    }

    public String getArea_country_code() {
        return area_country_code;
    }

    public void setArea_country_code(String area_country_code) {
        this.area_country_code = area_country_code;
    }

    public String getArea_zipcode() {
        return area_zipcode;
    }

    public void setArea_zipcode(String area_zipcode) {
        this.area_zipcode = area_zipcode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
