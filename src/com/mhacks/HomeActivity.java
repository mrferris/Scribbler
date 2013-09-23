package com.mhacks;




import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;


public class HomeActivity extends Activity {
	
	private ArrayList<String> _stringList;

	@Override
	protected void onCreate(Bundle savedInstanceState)  {
		super.onCreate(savedInstanceState);
		setContentView(com.mhacks.R.layout.activity_home);
		Intent intent = getIntent();
		double[] coordinates = intent.getDoubleArrayExtra("Coordinates");
		String longitude = String.valueOf(coordinates[0]);
		String latitude = String.valueOf(coordinates[1]);
		String request = latitude + "," + longitude;

		ArrayList<String> array = new ArrayList<String>();
				
		
		HashMap<String,Object> map = new HashMap<String,Object>();
		Getter getter = new Getter(map,"http://mhacks-mediashrine.rhcloud.com/load/1345");
		JSONArray json = getter.get();
		try{
			JSONObject object;
			int i;
			for(i=0;i<json.length();i++){
				object = (JSONObject) json.get(i);
				String s = object.getString("text");
				array.add(s);
			}
			System.err.println("json.get succeeded");
		}
		catch(Exception e){
			System.err.println("json.get failed");
		}
		  
		
				
		updateList(array);
		_stringList = array;
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(com.mhacks.R.menu.home, menu);
		return true;
	}
	
	public void updateList(ArrayList<String> array){
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.text_element_view,array);
		ListView listview = (ListView) findViewById(R.id.message_list_view);
		listview.setAdapter(adapter);
		_stringList = array;
	}
	
	public void submitButtonPress(View view) throws UnsupportedEncodingException{
		
		EditText input = (EditText) findViewById(R.id.text_input);
		String newMessage = input.getText().toString();
		input.setText("");
		ListView list = (ListView) findViewById(R.id.message_list_view);
		list.smoothScrollToPosition(5);
		
		Calendar c = Calendar.getInstance();
		Date d = c.getTime();
		int hour = d.getHours();
		int min = d.getMinutes();
		String time = Integer.toString(hour) + ":" + Integer.toString(min);
		HashMap<String,Object> map = new HashMap<String,Object>();
//		Poster poster = new Poster(map,"http://mhacks-mediashrine.rhcloud.com/post/%7B%22time%22:%22"+URLEncoder.encode(time, "UTF-8")+"%22,%22text%22:%22"
//				+URLEncoder.encode(newMessage, "UTF-8")+"%22,%22location%22:%22mhacks%22%7D");
		String JSONString = "{\"time\":\""+time+"\",\"message\":\""+newMessage+"\",\"location\":\"THE_LOCATION\"}";
		Poster getter = new Poster(map, "http://mhacks-mediashrine.rhcloud.com/post/" + 
					    URLEncoder.encode(JSONString, "UTF-8"));
		getter.get();
		if(newMessage!="" && newMessage!= " "){
			_stringList.add(newMessage);
			updateList(_stringList);
		}

		//Everything under here is to update the chat every submit
		_stringList.clear();
		HashMap<String,Object> mapAgain = new HashMap<String,Object>();
		Getter gettergetter = new Getter(map,"http://mhacks-mediashrine.rhcloud.com/load/1345");
		JSONArray json = gettergetter.get();
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
		  
		
		
		updateList(_stringList);
		
		
		//"{\"time\":\"THE_TIME\",\"message\":\"THE_MESSAGE\",\"location\":\"THE_LOCATION\"}"
		
		
		
	}

	
}
