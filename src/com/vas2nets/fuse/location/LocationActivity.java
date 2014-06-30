package com.vas2nets.fuse.location;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;
import com.vas2nets.fuse.R.menu;

public class LocationActivity extends Activity {
	
	private GoogleMap googleMap;
    private int mapType = GoogleMap.MAP_TYPE_NORMAL;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		this.getActionBar().setTitle("Location");
		
		FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment =  (MapFragment) fragmentManager.findFragmentById(R.id.map);
        googleMap = mapFragment.getMap();

        LatLng sfLatLng = new LatLng(37.7750, -122.4183);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.addMarker(new MarkerOptions()
                .position(sfLatLng)
                .title("San Francisco")
                .snippet("Population: 776733")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        LatLng sLatLng = new LatLng(37.857236, -122.486916);
        googleMap.addMarker(new MarkerOptions()
                .position(sLatLng)
                .title("Sausalito")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));


        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);


        LatLng cameraLatLng = sfLatLng;
        float cameraZoom = 10;

        if(savedInstanceState != null){
            mapType = savedInstanceState.getInt("map_type", GoogleMap.MAP_TYPE_NORMAL);

            double savedLat = savedInstanceState.getDouble("lat");
            double savedLng = savedInstanceState.getDouble("lng");
            cameraLatLng = new LatLng(savedLat, savedLng);

            cameraZoom = savedInstanceState.getFloat("zoom", 10);
        }

        googleMap.setMapType(mapType);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cameraLatLng, cameraZoom));
 
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location, menu);
		return true;
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save the map type so when we change orientation, the mape type can be restored
        LatLng cameraLatLng = googleMap.getCameraPosition().target;
        float cameraZoom = googleMap.getCameraPosition().zoom;
        outState.putInt("map_type", mapType);
        outState.putDouble("lat", cameraLatLng.latitude);
        outState.putDouble("lng", cameraLatLng.longitude);
        outState.putFloat("zoom", cameraZoom);
    }

}
