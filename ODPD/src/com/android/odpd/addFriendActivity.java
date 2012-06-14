package com.android.odpd;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class addFriendActivity extends ListActivity{

	public static final String PREF_NAME = "ODPD";
	public static String friends;
	public ArrayList<HashMap<String, Object>> searchResults;
	public ArrayList<HashMap<String, Object>> originalResults;
	EditText searchBox;
	
	//public String[] value;
	//public String[] searchValues;
	//public Bitmap[] image;
	public SharedPreferences settings;
	public void onCreate(Bundle savedInstanceState) {          
		super.onCreate(savedInstanceState);   
		setContentView(R.layout.add_friends);
		searchBox = (EditText)findViewById(R.id.editText1);
		try{
			settings = getSharedPreferences(PREF_NAME, 0);
			friends = settings.getString("friends", "No friends");
			
			if(friends == "No friends"){
				Toast.makeText(this, "No friends found", Toast.LENGTH_LONG);
			}
			else{
				
				new Thread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						getFriends();
					}
					
				}).start();
			}
		}
    	catch(Exception e){
    		Toast.makeText(this, e.toString(), Toast.LENGTH_LONG);
    	}
    }
	
	public void getFriends(){
		try{
			JSONObject friendArray = new JSONObject(friends);
			JSONArray friendList = friendArray.getJSONArray("data");
		
			//String[] values = new String[friendList.length()];
			//Bitmap[] images = new Bitmap[friendList.length()];
			originalResults = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> temp;
			for(int i = 0; i < friendList.length(); i++){
				JSONObject friend = friendList.getJSONObject(i);
				temp = new HashMap<String, Object>();
				temp.put("name", friend.getString("name"));
				temp.put("id", friend.getString("id"));
				originalResults.add(temp);
				//values[i] = friend.getString("name");
				//URL picUrl = new URL("http://graph.facebook.com/"+friend.getString("id")+"/picture");
				//Bitmap mIcon1 = BitmapFactory.decodeStream(picUrl.openConnection().getInputStream());
	    		//images[i] = mIcon1;	
			}


			searchResults=new ArrayList<HashMap<String,Object>>(originalResults);
			
			addFriendActivity.this.runOnUiThread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					updateList();
				}
				
			});
			
		}
		catch(Exception e){
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG);
		}
	}
	
	public void updateList(){
		final friendsListAdapter adapter = new friendsListAdapter(this, R.layout.friends_row, searchResults);
		setListAdapter(adapter);
		
		searchBox.addTextChangedListener(new TextWatcher() {
			 
			 public void onTextChanged(CharSequence s, int start, int before, int count) {
				 //get the text in the EditText
				 String searchString=searchBox.getText().toString();
				 int textLength=searchString.length();
			 
			          //clear the initial data set
				 searchResults.clear();
			 
				 for(int i=0;i<originalResults.size();i++)
				 {
					 String playerName=originalResults.get(i).get("name").toString();
					 if(textLength<=playerName.length()){
						 //	compare the String in EditText with Names in the ArrayList
						 if(searchString.equalsIgnoreCase(playerName.substring(0,textLength))){
							 searchResults.add(originalResults.get(i));
							 //value[i] = originalResults.get(i).get("name").toString();
						 }
					 }
				 }
			 
				 adapter.notifyDataSetChanged();
			 }
			 
			 public void beforeTextChanged(CharSequence s, int start, int count,
			     int after) {
			 
			 
			   }


			 @Override
			 public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			 }
		});
	
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		 // TODO Auto-generated method stub
		 //super.onListItemClick(l, v, position, id);
		try{
		 String selection = l.getItemAtPosition(position).toString();
		 //JSONObject selected = new JSONObject(selection);
		 Intent startNewActivityOpen = new Intent(getBaseContext(), addFormActivity.class);
		 startNewActivityOpen.putExtra("id", searchResults.get(position).get("id").toString());
		 startNewActivityOpen.putExtra("name", searchResults.get(position).get("name").toString());
		 startActivity(startNewActivityOpen);
		 finish();
		 //Toast.makeText(this, selection, Toast.LENGTH_LONG).show();
		}
		catch(Exception e){
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		}
	}
}
