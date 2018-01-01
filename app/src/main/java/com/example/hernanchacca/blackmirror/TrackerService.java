package com.example.hernanchacca.blackmirror;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hernanchacca on 12/27/17.
 */

public class TrackerService extends Service{
    int mStartMode;       // indicates how to behave if the service is killed
    IBinder mBinder = new LocalBinder();      // interface for clients that bind
    boolean mAllowRebind; // indicates whether onRebind should be used

    // Acquire a reference to the system Location Manager
    LocationManager locationManager ;
    LocationListener locationListener;
    private static final int LOCATION_REQUEST_CODE = 122;
    private  boolean locationChecked = true;


    @Override
    public void onCreate() {

        // The service is being created
        // Locacion Manager is Created
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                sendInformation(location);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (locationChecked)
            startUpdatingLocation();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // The service is starting, due to a call to startService()
        startUpdatingLocation();
        return START_NOT_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;

    }

    @Override
    public boolean onUnbind(Intent intent) {

        // All clients have unbound with unbindService()
        return mAllowRebind;

    }
    @Override
    public void onRebind(Intent intent) {

        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called

    }
    @Override
    public void onDestroy() {

        // The service is no longer used and is being destroyed
        super.onDestroy();
        System.out.print("Destroyed");

    }

    // Send location information to a heroku app that show the data in a plataform
    // via RestFull API
    public void  sendInformation(Location location) {

        Map<String, String> obj = new HashMap<String, String>();
        JSONObject jsonObj  = null;

        try {

            obj.put("Lat", location.getLatitude() + "");
            obj.put("Alt", location.getAltitude() + "");
            obj.put("lon", location.getLongitude() + "");
            obj.put("spe", location.getSpeed() + "");
            obj.put("year", Calendar.getInstance().get(Calendar.YEAR) + "");
            obj.put("month", Calendar.getInstance().get(Calendar.MONTH) + "");
            obj.put("day", Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "");
            obj.put("hour", Calendar.getInstance().get(Calendar.HOUR) + "");
            jsonObj = new JSONObject(obj);

        } catch (Exception e) {
            // Somthing went wrong
        }

        //sendPostRequest("https://radiant-atoll-24808.herokuapp.com/api/location/", jsonObj);
        System.out.println("Service Sent:" + location.getLatitude());

    }


    // Start Location listener
    public void startUpdatingLocation() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Service says NO PERMISOS");
            return;
        }

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        //locationManager.removeUpdates(locationListener);
        System.out.println("Hola desde Servicio");
        locationChecked = true;
    }

    // Stop listening the location changes
    public void stopUpdatingLocation() {
        locationManager.removeUpdates(locationListener);
        locationChecked = false;
    }


    // Init the Local Binder
    public class LocalBinder extends Binder {
        TrackerService getService() {
            return TrackerService.this;
        }
    }


    // Post request wrapper
    public void sendPostRequest(String url, JSONObject jsonObj) {

        //String url = "https://radiant-atoll-24808.herokuapp.com/api/videos";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Succesfull respponse
                System.out.print("Succesfull");
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error Response
                        System.out.print(error.getMessage());
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

}
