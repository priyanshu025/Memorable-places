package com.example.memorable_places;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    public void centerMapOnLocation(Location location,String title){
        if(location!=null) {
            LatLng user = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(user).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 10));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
         mMap = googleMap;
        //mMap.setOnMapLongClickListener(this);
        SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.memorable_places",Context.MODE_PRIVATE);
        Intent intent=getIntent();
        Toast.makeText(this, Integer.toString(intent.getIntExtra("placenumber",0)), Toast.LENGTH_SHORT).show();
        if(intent.getIntExtra("placenumber",0)==0){
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(@NonNull LatLng latLng) {
                    Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
                    String place="";
                    try {
                        ArrayList<Address> addresses= (ArrayList<Address>) geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
                        Log.i("address",addresses.get(0).toString());
                        if(addresses!=null && addresses.size()>0)
                            place+=addresses.get(0).getAddressLine(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(place.equals("")){
                        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm YYYY-MM-dd");
                        place+=sdf.format(new Date());
                    }
                    MainActivity.places.add(place);
                    MainActivity.locations.add(latLng);
                    MainActivity.arrayAdapter.notifyDataSetChanged();

                    ArrayList<String> lattitude=new ArrayList<String>();
                    ArrayList<String> longitude=new ArrayList<String>();
                    for(LatLng coord:MainActivity.locations){
                        lattitude.add(Double.toString(coord.latitude));
                        longitude.add(Double.toString(coord.longitude));
                    }
                    try {
                        sharedPreferences.edit().putString("place",ObjectSerializer.serialize(MainActivity.places)).apply();
                        sharedPreferences.edit().putString("lats",ObjectSerializer.serialize(lattitude)).apply();
                        sharedPreferences.edit().putString("lons",ObjectSerializer.serialize(longitude)).apply();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mMap.addMarker(new MarkerOptions().position(latLng).title(place));
                }
            });
            locationManager= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
             locationListener=new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                  centerMapOnLocation(location,"yourLocation");
                }

                 @Override
                 public void onStatusChanged(String provider, int status, Bundle extras) {

                 }
             };
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastknownlocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                 centerMapOnLocation(lastknownlocation,"LastKnownLocation");
            }
        }else{
            Location newlocation=new Location(LocationManager.GPS_PROVIDER);
            newlocation.setLatitude(MainActivity.locations.get(intent.getIntExtra("placenumber",0)).latitude);
            newlocation.setLongitude(MainActivity.locations.get(intent.getIntExtra("placenumber",0)).longitude);
            centerMapOnLocation(newlocation,MainActivity.places.get(intent.getIntExtra("placenumber",0)));
            /*mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(@NonNull LatLng latLng) {

                }
            });*/
        }
    }

    /*@Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        String place="";
        try {
            ArrayList<Address> addresses= (ArrayList<Address>) geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            Log.i("address",addresses.get(0).toString());
            if(addresses!=null && addresses.size()>0)
                place+=addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(place.equals("")){
            SimpleDateFormat sdf=new SimpleDateFormat("HH:mm YYYY-MM-dd");
            place+=sdf.format(new Date());
        }
        MainActivity.places.add(place);
        MainActivity.locations.add(latLng);
        MainActivity.arrayAdapter.notifyDataSetChanged();
        SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.memorable_places",Context.MODE_PRIVATE);
        ArrayList<String> lattitude=new ArrayList<String>();
        ArrayList<String> longitude=new ArrayList<String>();
        for(LatLng coord:MainActivity.locations){
            lattitude.add(Double.toString(coord.latitude));
            longitude.add(Double.toString(coord.longitude));
        }
        try {
            sharedPreferences.edit().putString("place",ObjectSerializer.serialize(MainActivity.places)).apply();
            sharedPreferences.edit().putString("lats",ObjectSerializer.serialize(lattitude)).apply();
            sharedPreferences.edit().putString("lons",ObjectSerializer.serialize(longitude)).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title(place));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
    }*/
}