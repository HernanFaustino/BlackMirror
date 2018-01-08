package com.example.hernanchacca.blackmirror;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener{

    private SimpleGestureFilter detector;
    Intent intent;
    private boolean isBound = false;
    private static final int LOCATION_REQUEST_CODE = 122;
    TrackerService localTrackerService;
    Usuario user = new Usuario();

    RatingBar ratingBar;
    TextView userName;
    TextView userRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        userName = (TextView) findViewById(R.id.userName);
        userRate = (TextView) findViewById(R.id.userRate);

        // Detect touched area
        detector = new SimpleGestureFilter(this,this);

        // Ask the user for Location Permissions
        String[] PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_REQUEST_CODE);
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("NO PERMISOS");
            return;
        }

        final String userId = getIntent().getStringExtra("id");
        String url = "http://blackmirrorapi.azurewebsites.net/api/values/" + userId;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    user.setId(userId);
                    user.setName(response.getString("name"));
                    user.setNumberOQualification(response.getInt("nRates"));
                    user.setRating(response.getInt("rating"));
                } catch (Exception e) {

                }
                System.out.println(user.getName());
                System.out.println(user.getRating());
                ratingBar.setRating(user.getRating());
                userName.setText("@" + user.getName());
                userRate.setText(Float.toString(user.getRating()));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });

        // add it to the RequestQueue
        System.out.println("Main activityi request Queued");
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {
        String str = "";

        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT :
                break;
            case SimpleGestureFilter.SWIPE_LEFT :
                Intent listIntent = new Intent(this, ListActivity.class);
                listIntent.putExtra("id", user.getId());
                startActivity(listIntent);
                break;
            case SimpleGestureFilter.SWIPE_DOWN :
                break;
            case SimpleGestureFilter.SWIPE_UP :
                break;

        }

    }

    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    restarService();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }

    public void stopTrakingService() {

        // Stop the tracker service
        if (isBound) {
            unbindService(connection);
            stopService(intent);
            isBound = false;
        }

    }

    public void restarService() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("NO PERMISOS");
            return;
        }

        if (intent != null)
            return;
        intent = new Intent(this, TrackerService.class);
        intent.putExtra("id", user.getId());
        intent.putExtra("name", user.getName());
        intent.putExtra("rating", user.getRating());
        intent.putExtra("nRate", user.getNumberOQualification());
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        System.out.println("Restarted");

    }

    // Service Connection  that connect with the TrackerService
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TrackerService.LocalBinder binder = (TrackerService.LocalBinder) iBinder;
            localTrackerService = binder.getService();
            localTrackerService.updateUser(user);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
