package com.example.hernanchacca.blackmirror;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hernanchacca on 12/27/17.
 */

public class CustomAdapter extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<Usuario> items;

    public CustomAdapter (Activity activity, ArrayList<Usuario> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    public void addAll(ArrayList<Usuario> category) {
        for (int i =  0; i < category.size(); i++) {
            items.add(category.get(i));
        }
    }

    @Override
    public Object getItem(int arg0) {
        return items.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.usuariocercano, null);
        }

        Usuario dir = items.get(position);

        TextView title = (TextView) v.findViewById(R.id.category);
        title.setText(dir.getName());

        TextView description = (TextView) v.findViewById(R.id.texto);
        description.setText(dir.getRating() + " stars");

        ImageView imagen = (ImageView) v.findViewById(R.id.profileimage);
        imagen.setImageDrawable(dir.getImage());

        return v;
    }
}