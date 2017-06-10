package com.example.esca.landmarket.models;

/**
 * Created by Esca on 09.06.2017.
 */

public class LandRegister {

    private String area;
    private String assignment;
    private String price;
    private String description;
    private String address;
    private String owner;

    public LandRegister(String area, String assignment, String price, String description, String address, String owner) {
        this.area = area;
        this.assignment = assignment;
        this.price = price;
        this.description = description;
        this.address = address;
        this.owner = owner;
    }

    public LandRegister() {

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
