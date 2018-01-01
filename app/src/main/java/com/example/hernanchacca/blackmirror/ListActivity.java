package com.example.hernanchacca.blackmirror;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ListActivity extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener{

    private SimpleGestureFilter detector;
    CustomAdapter adapter;

    ListView simpleList;
    ArrayList<Usuario> usuarios = new ArrayList<Usuario>();

    Intent rateIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Detect touched area
        detector = new SimpleGestureFilter(this,this);


        String url = "http://blackmirrorapi.azurewebsites.net/api/values";
        RequestQueue queue = Volley.newRequestQueue(this);  // this = context

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Succesfull respponse
                Usuario temp;
                System.out.println("respons");
                Log.i("response", "Success");
                try {
                    Iterator<String> iter = response. keys();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        temp = new Usuario();
                        try {
                            JSONObject userObj = (JSONObject) response.get(key);
                            temp.setName(userObj.getString("name"));
                            temp.setId(userObj.getString("id"));
                            temp.setRating(userObj.getInt("rating"));
                            temp.setImagen(getResources().getDrawable(R.drawable.profilepictures));
                            usuarios.add(temp);
                            Log.i("item", temp.getName());
                        } catch (JSONException e) {
                            // Something went wrong!
                            Log.i("exception", "adding");
                        }
                    }
                    System.out.print(response.toString());
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });

        // add it to the RequestQueue
        System.out.println("Queued");
        queue.add(jsObjRequest);

        simpleList = (ListView) findViewById(R.id.listViewId);

        adapter = new CustomAdapter(this, usuarios);

        simpleList.setAdapter(adapter);
        rateIntent = new Intent(this, RateActivity.class);
        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                rateIntent.putExtra("Name", usuarios.get(position).getName());
                rateIntent.putExtra("Rating", usuarios.get(position).getRating());
                startActivity(rateIntent);
                //CODIGO AQUI
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
            case SimpleGestureFilter.SWIPE_UP :
                break;

        }
    }

    @Override
    public void onDoubleTap() {

    }
}
