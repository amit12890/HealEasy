package com.example.amitthakkar.myapplication.model;

import java.io.Serializable;

/**
 * Created by amit.thakkar on 9/18/2015.
 */
public class PlaceDetails implements Serializable {
    public String id = "";
    public String name = "";
    public String phone="";
    public String internationalPhone = "";
    public String website = "";
    public String isAlwaysOpened = "";
    public String status = "";
    public String googlePlaceURI = "";
    public String price = "";
    public String address = "";
    public String vicinity = "";
    public int reviews = 0;
    public int rating = 0;
    public float distance = 0;
    public String hours = "";
    public String latitude = "";

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String longitude = "";

    public String getInternationalPhone() {
        return internationalPhone;
    }

    public void setInternationalPhone(String internationalPhone) {
        this.internationalPhone = internationalPhone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getIsAlwaysOpened() {
        return isAlwaysOpened;
    }

    public void setIsAlwaysOpened(String isAlwaysOpened) {
        this.isAlwaysOpened = isAlwaysOpened;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGooglePlaceURI() {
        return googlePlaceURI;
    }

    public void setGooglePlaceURI(String googlePlaceURI) {
        this.googlePlaceURI = googlePlaceURI;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public int getReviews() {
        return reviews;
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getId() {



        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
