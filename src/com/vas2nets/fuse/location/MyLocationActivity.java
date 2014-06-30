package com.vas2nets.fuse.location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;
import com.vas2nets.fuse.R.menu;
import com.vas2nets.fuse.gps.GPSTracker;

public class MyLocationActivity extends Activity {
	private GoogleMap googleMap;
	GPSTracker gps;
	private double longitude;
	private double latitude;
	
	//private ProgressDialog pDialog;
	
	Geocoder geocoder;
	List<Address> addresses;
	String addy;
	String city;
	String country;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_location);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		this.getActionBar().setTitle("Location");
		
		gps = new GPSTracker(MyLocationActivity.this);
		// check if GPS enabled     
        if(gps.canGetLocation()){
             
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
             
            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();    
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
		
        initilizeMap();
        //new GetMap().execute();
		
		
		// get address from latitude and longitude
		geocoder = new Geocoder(this, Locale.getDefault());
		
		try {
			addresses = geocoder.getFromLocation(latitude, longitude, 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (addresses != null && addresses.size() > 0) {
			Address address = addresses.get(0);
			addy = address.getAddressLine(0);
			city = address.getAddressLine(1);
			country = address.getCountryName();
		}
		
		Toast.makeText(getApplicationContext(), "Your Address is - \nAddress: " + addy + "\nCity: " + city + "\nCountry: "+ country, Toast.LENGTH_LONG).show();  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_location, menu);
		return true;
	}
	
	 /**
     * function to load map. If map is not created it will create it for you
     * */
	
    @TargetApi(Build.VERSION_CODES.HONEYCOMB) 
    @SuppressLint("NewApi") 
    private void initilizeMap() {
    	
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
 
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
            
         // create marker
    		MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Hello Maps ");
    		// GREEN color icon
    		marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
    		 
    		// adding marker
    		googleMap.addMarker(marker);
    		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    		//showing user current position
    		googleMap.setMyLocationEnabled(true); // false to disable
    		//enable zooming button
    		googleMap.getUiSettings().setZoomControlsEnabled(true); // true to enable
    		
    		googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            
        }
    }
    
    class GetMap extends AsyncTask<String, String, String> {
    	/*
    	 @Override
			protected void onPreExecute() {
				super.onPreExecute();
				try{
					pDialog = new ProgressDialog(MyLocationActivity.this);
					pDialog.setMessage("Loading Map...");
					pDialog.setIndeterminate(false);
					pDialog.setCancelable(true);
					pDialog.show();
				}catch(Exception e){
					
				}
				
		 }*/


		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			initilizeMap();
			return null;
		}
		
		protected void onPostExecute(String file_url) {
			//pDialog.dismiss();
		}
    	
    }
    /*
    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }*/
 

}
