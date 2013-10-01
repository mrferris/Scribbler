package com.mhacks;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class Gps {
	
	private String _locationMechanism;
	private LocationManager _locationManager;
	
	public Gps(LocationManager locationManager){
		
		_locationManager = locationManager;
		
	}
	
	public Location getLocation(){
		return null;
	}
	
	public boolean gpsStatus(){
		return false;
	}
	
}
