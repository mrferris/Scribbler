package com.mhacks;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
//import org.json.JSONArray;

public class OpeningActivity extends Activity implements Runnable  {
	
	private LocationManager locationManager=null;
	private LocationListener locationListener=null;
	
	private static final String TAG = "Debug";
	private Boolean flag = false;
	private OpeningActivity singleton;
	private Boolean _proceed;
	
	private Intent _intent;

	@Override
	public void onCreate(Bundle savedInstanceState)  {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opening);
		singleton = this;
		_proceed = false;
		
		//if you want to lock screen for always Portrait mode  
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		getGps();
		
		new Thread(this).start();

	}

	public void getGps() {
		flag = displayGpsStatus();
		if (flag) {			
			
			locationListener = new MyLocationListener();
			
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10,locationListener);
			
		} else {
			alertbox("Gps Status!!", "Your GPS is: OFF");
		}

	}

	/*----------Method to Check GPS is enable or disable ------------- */
	private Boolean displayGpsStatus() {
		System.out.println("gps");
		ContentResolver contentResolver = getBaseContext().getContentResolver();
		boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(
				contentResolver, LocationManager.GPS_PROVIDER);
		if (gpsStatus) {
			return true;

		} else {
			return false;
		}
	}
	
	@Override
	public void run(){
		try{Thread.sleep(5000);}
		catch(Exception e){
			System.err.println("sleep screwed up");
		}
		startActivity(_intent);
	}

	/*----------Method to create an AlertBox ------------- */
	protected void alertbox(String title, String mymessage) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Your Device's GPS is Disabled")
				.setCancelable(false)
				.setTitle("** Gps Status **")
				.setPositiveButton("Gps On",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// finish the current activity
								// AlertBoxAdvance.this.finish();
								Intent myIntent = new Intent(
										Settings.ACTION_SECURITY_SETTINGS);
								startActivity(myIntent);
								dialog.cancel();
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// cancel the dialog box
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	/*----------Listener class to get coordinates ------------- */
	private class MyLocationListener implements LocationListener {
		
		TextView textView;
		String text;
		
        @Override
        public void onLocationChanged(Location loc) {
          
        
    		    
    		    double longitude = loc.getLongitude();
    		    double latitude = loc.getLatitude();
    		    
    		    /*----------to get City-Name from coordinates ------------- */
    		    String cityName=null;      		      
    		    Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());      		     
    		    List<Address>  addresses;  
    		    try {  
    		     addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);  
    		     if (addresses.size() > 0)  
    		      System.out.println(addresses.get(0).getLocality());  
    		     cityName=addresses.get(0).getLocality();  
    		    } catch (IOException e) {    		      
    		     e.printStackTrace();  
    		    } 
    		    
    			    
    		    _intent = new Intent(singleton,HomeActivity.class);
    		    double[] coordinates = new double[2];
    		    coordinates[0] = longitude;
    		    coordinates[1] = latitude;
    		    _intent.putExtra("Coordinates",coordinates);
    		    
        }
        
        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub        	
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub        	
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub        	
        }
        
        
        
       
        
    }
	
	

}