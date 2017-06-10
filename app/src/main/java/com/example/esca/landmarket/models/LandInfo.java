package com.example.esca.landmarket.models;

/**
 * Created by Esca on 10.06.2017.
 */

public class LandInfo {
    private double  latitude;
    private double  longitude;
    private String placeId;
    private String id;
    private String area;
    private String assignment;
    private String price;
    private String description;
    private String address;
    private String owner;


    public LandInfo(double latitude, double longitude, String placeId, String id, String area, String assignment, String price, String description, String address, String owner) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeId = placeId;
        this.id = id;
        this.area = area;
        this.assignment = assignment;
        this.price = price;
        this.description = description;
        this.address = address;
        this.owner = owner;
    }

    public LandInfo() {

    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAssignment() {
        return assignment;
    }

    public void setAssignment(String assignment) {
        this.assignment = assignment;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
