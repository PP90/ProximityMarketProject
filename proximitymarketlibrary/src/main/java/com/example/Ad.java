package com.example;

import java.io.File;
import java.sql.Time;

/**
 * Created by p3 on 19/04/2016.
 */
public class Ad {
    private String operation;
    private String name;
    private File description;
    private File photo;
    private int findOffer;
    private int price;
    private Time validFrom;
    private Time validUntil;
    private String quarter;
    private double latitude;
    private double longitude;

    public Ad(){
    }
    public Ad(String name, File description, File photo, boolean findOffer,
              int price, Time validFrom, Time validUntil, String quarter, double latitude, double longitude ){
        this.name=name;
        this.description=description;
        this.photo=photo;
        this.price=price;
        this.validFrom=validFrom;
        this.validUntil=validUntil;
        this.quarter=quarter;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getDescription() {
        return description;
    }

    public void setDescription(File description) {
        this.description = description;
    }

    public File getPhoto() {
        return photo;
    }

    public void setPhoto(File photo) {
        this.photo = photo;
    }

    public int isFindOffer() {
        return findOffer;
    }

    public void setFindOffer(int findOffer) {
        this.findOffer = findOffer;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Time getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Time validFrom) {
        this.validFrom = validFrom;
    }

    public Time getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Time validUntil) {
        this.validUntil = validUntil;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
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


    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
