package com.android.odpd;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

//import com.android.odpd.MainActivity.AddUser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class addFormActivity extends Activity {

	private static SharedPreferences settings;
	public static final String PREF_NAME = "ODPD";
	private static final String SERVICE_URI = "http://odpd.cloudapp.net/RestServiceImpl.svc";
	public String id;
	public String friendId;
	public String friendName;
	public String ower;
	public String lender;
	public double amount;
	public String title;
	public int isExternal;
	public boolean isOwer = false;
	//public DateTime date;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addform);
        
        settings = getSharedPreferences(PREF_NAME, 0);
        String me = settings.getString("me", "me");
        try{
        JSONObject json = new JSONObject(me);
        id = json.getString("id");
        }
        catch(Exception e){}
		//Iterator keys = json.keys();
		//map = new HashMap<String, String>();
		//while(keys.hasNext()){
		//	String key = (String)keys.next();
		//	map.put(key, json.getString(key));
		//}
        final String friendId = getIntent().getStringExtra("id");
        final String name = getIntent().getStringExtra("name");
        this.friendId = friendId;
        this.friendName = name;
        TextView nameView = (TextView)findViewById(R.id.nameTextView);
        nameView.setText(name);
        
        //To display the profile picture
        new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{
					URL picUrl = new URL("http://graph.facebook.com/"+friendId+"/picture");
					final Bitmap mIcon1 = BitmapFactory.decodeStream(picUrl.openConnection().getInputStream());
					//bmp = mIcon1;
					addFormActivity.this.runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							ImageView img = (ImageView)findViewById(R.id.imageView1);
							img.setImageBitmap(mIcon1);
						}
						
					});
				}
				catch(Exception e){
					//error = e.toString();
					//Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
				}
			}
        	
        }).start();
	
	
	}
	
	public void onOkClick(View v){
		new AddLog().execute(SERVICE_URI + "/AddLog");
		TextView amt = (TextView)findViewById(R.id.amtText);
		TextView t = (TextView)findViewById(R.id.titleText);
		RadioGroup rbgrp = (RadioGroup)findViewById(R.id.radioGroup1);
		int rbid = rbgrp.getCheckedRadioButtonId();
		View rb = rbgrp.findViewById(rbid);
		int idx = rbgrp.indexOfChild(rb);
		if(idx == 0){
			ower = friendId;
			lender = id;
			isOwer = false;
		}
		else if(idx == 1){
			ower = id;
			lender = friendId;
			isOwer = true;
		}
		amount = Double.valueOf(amt.getText().toString()).doubleValue();
		title = t.getText().toString();
		isExternal = 1;
		
		try{
			
			String accounts = settings.getString("accounts", "[]");
			double bal = amount;
			JSONObject accts = new JSONObject(accounts);
			JSONArray acctList = accts.getJSONArray("accounts");
			JSONObject acct = new JSONObject();
			int position = -1;
			for(int i = 0; i < acctList.length(); i++){
				if(acctList.getJSONObject(i).getString("id").equalsIgnoreCase(friendId)){
					acct = acctList.getJSONObject(i);
					position = i;
					break;
				}
			}
			acct.put("name",friendName);
			acct.put("id",friendId);
			if(isOwer){
			
				if(acct.has("amount")){
					bal = acct.getDouble("amount") - amount;
				}
				else{
					bal = amount*(-1);
				}
			}
			else if(!isOwer){
				if(acct.has("amount")){
					bal = acct.getDouble("amount") + amount;
				}
			}
			
			acct.put("amount", bal);
			
			if(position == -1){
				acctList.put(acct);
			}
			else if(position != -1){
				acctList.put(position, acct);
			}
			accts.put("accounts", acctList);
			
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("accounts", accts.toString());
			editor.commit();
		}
		catch(Exception e){
			Log.e("addFormActivity.onOkClick",e.toString());
		}
		finish();
	}
	
	
	private class AddLog extends AsyncTask<String,Void,String> 
    {
    @Override
    protected String doInBackground(String... url)
    {
    try 
    {
    // POST request to <service>/SaveVehicle
    	HttpPost request = new HttpPost(url[0]);
    	request.setHeader("Accept", "application/json");
    	request.setHeader("Content-type", "application/json");
    	JSONStringer user = new JSONStringer()
    		.object().key("log")
    		.object()
    		//.key("date").value(System.currentTimeMillis())
    		.key("ower").value(ower)
    		.key("lender").value(lender)
    		.key("title").value(title)
    		.key("amount").value(amount)
    		.key("flag").value(isExternal)
    		.endObject()
    		.endObject();

    	StringEntity entity = new StringEntity(user.toString());
    	request.setEntity(entity);

    	DefaultHttpClient httpClient = new DefaultHttpClient();
    	HttpResponse response = httpClient.execute(request);


    	//Log.e("WebInvoke", "Saving : " + response.getStatusLine().getStatusCode());	
    	//JSONObject res = new JSONObject(response.toString());
    	//Log.e("entity",response.getEntity().getContent().toString());
    	return "fser";

    } 

    catch (Exception e) 
    {
    	// 	TODO Auto-generated catch block
    	//Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
    	Log.e("error", e.toString());
    	return null;
    }
    }
    
    @Override
    protected void onPostExecute(String result){
    	//Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_LONG);
    	//Log.e("blah",result.toString());
    }

    }
}
