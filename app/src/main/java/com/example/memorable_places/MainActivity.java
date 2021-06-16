package com.example.memorable_places;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static ArrayList<String> places=new ArrayList<String>();
    static ArrayList<LatLng> locations=new ArrayList<LatLng>();
    static ArrayAdapter arrayAdapter;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listView);
        SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.memorable_places", Context.MODE_PRIVATE);
        ArrayList<String> lattitude=new ArrayList<String>();
        ArrayList<String> longitude=new ArrayList<String>();
        places.clear();
        locations.clear();
        try{
            places=(ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<String>())));
            lattitude=(ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("latt",ObjectSerializer.serialize(new ArrayList<String>())));
            longitude=(ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("long",ObjectSerializer.serialize(new ArrayList<String>())));
        }catch (Exception e){
            e.printStackTrace();
        }
        if(places.size()>0 && lattitude.size()>0 && longitude.size()>0){
            if(places.size()==lattitude.size() && places.size()==longitude.size()){
                for(int i=0;i<places.size();i++){
                    locations.add(new LatLng(Double.parseDouble(lattitude.get(i)),Double.parseDouble(longitude.get(i))));
                }
            }
        }else{
            places.add("Add a new place");
            locations.add(new LatLng(0,0));
        }
        arrayAdapter=new ArrayAdapter(this, android.R.layout.simple_list_item_1,places);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("index",Integer.toString(i));
                Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("placenumber",i);
                startActivity(intent);
            }
        });
    }
    /*public void clear_item(View view){
        SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.memorable_places", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.remove("place");
        editor.remove("lats");
        editor.remove("lons");
        editor.commit();
        MapsActivity.lattitude.clear();
        MapsActivity.longitude.clear();
    }*/
}