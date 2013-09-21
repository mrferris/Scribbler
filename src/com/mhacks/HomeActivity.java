package com.mhacks;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.IntentSender;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
	
	private final static int
    CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	 private Location mLocation;
	 private TextView mAddress;
	 //mCurrentLocation = mLocationClient.getLastLocation();
	 
	 public void getAddress(View v) {
	        // Ensure that a Geocoder services is available
	        if (Build.VERSION.SDK_INT >=
	                Build.VERSION_CODES.GINGERBREAD
	                            &&
	                Geocoder.isPresent()) {
	            (new GetAddressTask(this)).execute(mLocation);
	        }
	 }
	public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
	}
    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.mhacks.R.layout.activity_home);
		 mAddress = (TextView) findViewById(R.id.address);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(com.mhacks.R.menu.home, menu);
		return true;
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
           // showErrorDialog(connectionResult.getErrorCode());
        }
		
	}
	private class GetAddressTask extends
    AsyncTask<Location, Void, String> {
		
Context mContext;


public GetAddressTask(Context context) {
    super();
    mContext = context;
}
@Override
protected void onPostExecute(String address) {
    // Display the results of the lookup.
    mAddress.setText(address);
}

	@Override 
	protected String doInBackground(Location... params ) {
        Geocoder geocoder =
                new Geocoder(mContext, Locale.getDefault());
        // Get the current location from the input parameter list
        Location loc = params[0];
        // Create a list to contain the result address
        List<Address> addresses = null;
        try {
            /*
             * Return 1 address.
             */
            addresses = geocoder.getFromLocation(loc.getLatitude(),
                    loc.getLongitude(), 1);
        } catch (IOException e1) {
        Log.e("LocationSampleActivity",
                "IO Exception in getFromLocation()");
        e1.printStackTrace();
        return ("IO Exception trying to get address");
        } catch (IllegalArgumentException e2) {
        // Error message to post in the log
        String errorString = "Illegal arguments " +
                Double.toString(loc.getLatitude()) +
                " , " +
                Double.toString(loc.getLongitude()) +
                " passed to address service";
        Log.e("LocationSampleActivity", errorString);
        e2.printStackTrace();
        return errorString;
        }
        // If the reverse geocode returned an address
        if (addresses != null && addresses.size() > 0) {
            // Get the first address
            Address address = addresses.get(0);
            /*
             * Format the first line of address (if available),
             * city, and country name.
             */
            String addressText = String.format(
                    "%s, %s, %s",
                    // If there's a street address, add it
                    address.getMaxAddressLineIndex() > 0 ?
                            address.getAddressLine(0) : "",
                    // Locality is usually a city
                    address.getLocality(),
                    // The country of the address
                    address.getCountryName());
            // Return the text
            return addressText;
        } else {
            return "No address found";
        }
    }
	}

	@Override
	public void onConnected(Bundle arg0) {
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
		
	}
	@Override
	public void onLocationChanged(Location location) {
		String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		
	}
	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

}
