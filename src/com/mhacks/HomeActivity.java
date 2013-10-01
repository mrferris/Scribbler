package com.mhacks;




import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;


public class HomeActivity extends Activity {
	
	private ArrayList<String> _stringList;
	private Location _location;
	
	/**
	 * Implements splash screen. Need to rethink. 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)  {
		super.onCreate(savedInstanceState);
		setContentView(com.mhacks.R.layout.activity_opening);
		_stringList = new ArrayList<String>();
		
		//Display splash screen for a few seconds. Should remove/rethink. Splash screens are for lames.
		findViewById(android.R.id.content).postDelayed(new Runnable(){
			@Override
			public void run(){
				setContentView(R.layout.activity_home);
				updateScribbles();
			}
		}, 2000);
		
	}
	
	/**
	 * implements actionbar's menu button (i think?)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(com.mhacks.R.menu.home, menu);
		return true;
	}
	
	
	/**
	 * Defines submit button's action. Puts user's message to database and calls updateScribbles().
	 * @param view, system parameter from button
	 * @throws UnsupportedEncodingException, figure out how to catch this
	 */
	public void submitButtonPress(View view) throws UnsupportedEncodingException{
		
		//Get message from input EditText view
		EditText input = (EditText) findViewById(R.id.text_input);
		String newMessage = input.getText().toString();
		input.setText("");
		ListView list = (ListView) findViewById(R.id.message_list_view);
		
		//Try to scroll to bottom, doesn't work. Need to figure out how to do it.
		list.smoothScrollToPosition(5);
		
		//If message is valid, post it
		if(newMessage!="" && !isEmpty(newMessage)){

			//Get time
			Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int min = c.get(Calendar.MINUTE);
			String time = Integer.toString(hour) + ":" + Integer.toString(min);
			
			//Post message to database
			HashMap<String,Object> map = new HashMap<String,Object>();
			String JSONString = "{\"time\":\""+time+"\",\"message\":\""+newMessage+"\",\"location\":\"THE_LOCATION\"}";
			Poster getter = new Poster(map, "http://mhacks-mediashrine.rhcloud.com/post/" + 
						    URLEncoder.encode(JSONString, "UTF-8"));
			getter.get();
			
			//add message to local list, update UI
			_stringList.add(newMessage);
			updateScribbles();
		}
		
	}
	
	
	/**
	 * 	Updates UI. This whole method is redundant and should be done differently.
	 */
	public void updateScribbles(){
		
		_stringList.clear();
		HashMap<String,Object> mapAgain = new HashMap<String,Object>();//Not used, just for Getter implementation
		Getter gettergetter = new Getter(mapAgain,"http://mhacks-mediashrine.rhcloud.com/load/1345");
		JSONArray json = gettergetter.get();
		
		//Parse
		try{
			JSONObject object;
			int i;
			for(i=0;i<json.length();i++){
				object = (JSONObject) json.get(i);
				String s = object.getString("text");
				_stringList.add(s);
			}
			System.err.println("json.get succeeded");
		}
		catch(Exception e){
			System.err.println("json.get failed");
		}
		  
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.text_element_view,_stringList);
		ListView listview = (ListView) findViewById(R.id.message_list_view);
		listview.setAdapter(adapter);
		
	}
	
	/**
	 * Checks if user message is valid
	 * @param s, string to check
	 * @return true if string is empty, false otherwise
	 */
	public boolean isEmpty(String s){
		int i;
		int length = s.length();
		for(i=0;i<length;i++){
			if(s.charAt(i) != ' ')
				return false;
		}
		return true;
	}
	
	/**
	 * Private class to implement GPS functionality. 
	 *
	 */
	private class Gps{

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
	
}
