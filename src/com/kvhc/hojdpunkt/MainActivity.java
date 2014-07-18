package com.kvhc.hojdpunkt;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Dialog;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.kvhc.hojdpunkt.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements LocationListener {

	GoogleMap googleMap;
	
	Marker minMarker;
	double minAltitude = Double.MAX_VALUE;
	Location minLocation;
	Marker maxMarker;
	double maxAltitude = Double.MIN_VALUE;
	Location maxLocation;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
		if (status != ConnectionResult.SUCCESS) {
			int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
		} else {
			setupMap();
		}
	}


	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        double altitude = location.getAltitude();
        
		if (altitude > maxAltitude) {
			maxLocation = location;
			maxAltitude = altitude;
			redoMarkers();
		} else if (altitude < minAltitude) {
			minLocation = location;
			minAltitude = altitude;
			redoMarkers();
		}
		
		TextView tvLocation = (TextView) findViewById(R.id.tv_location);
        // Showing the current location in Google Map
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
        // Zoom in the Google Map
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        
        tvLocation.setText("Current Altitude: " + altitude +
        				   "\n Min Altitude: " + minAltitude + 
        				   "\n Highest Altitude: " + maxAltitude);        
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub	
	}
	
	private void setupMap() {
		// Getting reference to the SupportMapFragment of activity_main.xml
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        // Getting GoogleMap object from the fragment
        googleMap = fm.getMap();
        // Enabling MyLocation Layer of Google Map
        googleMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        //Location location = locationManager.getLastKnownLocation(provider);
       	redoMarkers();
       	
        locationManager.requestLocationUpdates(provider, 20000, 0, this);
	}
	
	private void redoMarkers() {
		googleMap.clear();
		newMarker(minLocation, minMarker, "Min Altitude", BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
		newMarker(maxLocation, maxMarker, "Max Altitude", BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
	}
	
	private void newMarker(Location location, Marker marker, String text, BitmapDescriptor hue) {
		if (location != null) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			
			MarkerOptions options = new MarkerOptions();
			options.position(new LatLng(latitude, longitude));
			options.title(text);
			options.icon(hue);
			
			marker = googleMap.addMarker(options);
		}
	}
}
