package com.example.hernanchacca.blackmirror;

import android.app.ActivityOptions;
import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
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

import org.json.JSONObject;

public class RateActivity extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener{

    private SimpleGestureFilter detector;


    MediaPlayer mp1, mp2, mp3, mp4, mp5;
    String star1, star2, star3, star4, star5;

    RatingBar ratingBar;
    TextView userName;
    Usuario user = new Usuario();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        // Detect touched area
        detector = new SimpleGestureFilter(this,this);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        userName = (TextView) findViewById(R.id.userName);

        user.setName(getIntent().getStringExtra("Name"));
        user.setId(getIntent().getStringExtra("id"));
        user.setRating(getIntent().getIntExtra("Rating", 0));
        user.setImagen(getDrawable(R.drawable.profilepictures));
        user.setNumberOQualification(getIntent().getIntExtra("nRates", 0));

        int numStar = user.getRating();
        Log.i("start", numStar+"");
        ratingBar.setRating(0);
        userName.setText("@"+ user.getName() + " " + numStar + " Stars");

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        star1 = "android.resource://" + this.getPackageName() + "/raw/star1";
        star2 = "android.resource://" + this.getPackageName() + "/raw/star2";
        star3 = "android.resource://" + this.getPackageName() + "/raw/star3";
        star4 = "android.resource://" + this.getPackageName() + "/raw/star4";
        star5 = "android.resource://" + this.getPackageName() + "/raw/star5";

        mp1 = MediaPlayer.create(this, R.raw.star1);
        mp2 = MediaPlayer.create(this, R.raw.star2);
        mp3 = MediaPlayer.create(this, R.raw.star3);
        mp4 = MediaPlayer.create(this, R.raw.star4);
        mp5 = MediaPlayer.create(this, R.raw.star5);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (v <= 1.0) {
                    mp1.start();
                } else if (v <= 2.0) {
                    mp2.start();
                } else if (v <= 3.0) {
                    mp3.start();
                } else if (v <= 4.0) {
                    mp4.start();
                } else if (v <= 5.0) {
                    mp5.start();
                }
            }
        });
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
                this.finish();
                break;
            case SimpleGestureFilter.SWIPE_LEFT :
                break;
            case SimpleGestureFilter.SWIPE_DOWN :
                break;
            case SimpleGestureFilter.SWIPE_UP :    str = "Rating Sent";
                sendPuntuation(user);
                Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
                break;

        }


    }

    public void sendPuntuation(Usuario user) {
        System.out.println("sending");
        String url = "http://blackmirrorapi.azurewebsites.net/api/near/" + user.getId();
        JSONObject obj =  new JSONObject();
        try {
            obj.put("rate", ratingBar.getRating());
        } catch (Exception e) {
            System.out.println(e);
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), "Ratin sent", Toast.LENGTH_SHORT).show();
            }
        },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("error sending rate");
                    System.out.println(error);
                }
        });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
    @Override
    public void onDoubleTap() {

    }
}
