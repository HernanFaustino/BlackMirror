package com.example.hernanchacca.blackmirror;

import android.graphics.drawable.Drawable;
import android.location.Location;

/**
 * Created by hernanchacca on 12/27/17.
 */

public class Usuario {
    private String name;
    private String id;
    private Drawable imagen;

    int rating;

    int numberOQualification;

    Location lastLocation ;
    public Usuario() {
        super();
    }

    public Usuario(String id, String name, int rating, int number, Drawable imagen, Location location) {
        super();
        this.name = name;
        this.imagen = imagen;
        this.id = id;
        this.rating = rating;
        this.lastLocation = location;
        this.numberOQualification = number;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Drawable getImage() {
        return imagen;
    }

    public void setImagen(Drawable imagen) {
        this.imagen = imagen;
    }

    public String getId(){return id;}


    public void setId(String id){this.id = id;}

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getNumberOQualification() {
        return numberOQualification;
    }

    public void setNumberOQualification(int numberOQualification) {
        this.numberOQualification = numberOQualification;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }
}
