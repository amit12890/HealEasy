package com.example.amitthakkar.myapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amitthakkar.myapplication.model.PlaceDetails;
import com.example.amitthakkar.myapplication.R;

import java.util.ArrayList;

public class PlaceListAdapter extends ArrayAdapter<PlaceDetails> {

    Context context;
    private static LayoutInflater inflater=null;
    private ArrayList<PlaceDetails> placeArrayList;

    public PlaceListAdapter(Activity activity,int layoutResourceId, ArrayList<PlaceDetails> placeArrayList) {
        super(activity,layoutResourceId,placeArrayList);
        // TODO Auto-generated constructor stub
        context=activity;
        this.placeArrayList = placeArrayList;
        inflater = ( LayoutInflater )context.
                 getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return placeArrayList.size();
    }

    @Override
    public PlaceDetails getItem(int position) {
        // TODO Auto-generated method stub
        return placeArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView txtName,txtDistance;
        ImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView = inflater.inflate(R.layout.list_item, null);

        holder.txtName = (TextView) rowView.findViewById(R.id.txt_name);
        holder.txtDistance =(TextView) rowView.findViewById(R.id.txt_distance);

        float distance = placeArrayList.get(position).getDistance();
        String distance_string = String.format("%.02f", distance);
        holder.txtName.setText(placeArrayList.get(position).getName());
        holder.txtDistance.setText("Distance : "+ distance_string +" KM away");

        return rowView;
    }

}