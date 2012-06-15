package com.android.odpd;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.app.*;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.facebook.android.*;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook.*;

public class MainActivity extends Activity {

    Facebook facebook = new Facebook("218187714969491");
    AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
    String me = "";
    String friends = "";
    String hello = "";
    String error = "";
    Bitmap bmp = null;
    boolean myself = false;
    boolean friend = false;
    
    //Shared Preferences file name
    public static final String PREF_NAME = "ODPD";
    private static final String SERVICE_URI = "http://odpd.cloudapp.net/RestServiceImpl.svc";
    public static SharedPreferences settings;
    public static TextView name;
    public static ImageView profilePic;
    public static ListView accountList;
    public static ProgressDialog progress;
    public static TabHost tabhost;
    private ArrayList<HashMap<String, Object>> accounts;
    private mainAdapter main;
    
    Map<String, String> map;
    
    //public static TabView tabs;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        name = (TextView)findViewById(R.id.helloTextView);
        profilePic = (ImageView)findViewById(R.id.imageView1);
        accountList = (ListView)findViewById(R.id.listAccount);
        tabhost = (TabHost)findViewById(R.id.tabhost);
        initTabs();
        settings = getSharedPreferences(PREF_NAME, 0);
        


        name.setVisibility(View.INVISIBLE);
        profilePic.setVisibility(View.INVISIBLE);

        progress = ProgressDialog.show(this, "", "Loading...", true);
        me = settings.getString("me", "me");
        if(me == "me"){
        	facebook.authorize(this, new String[] { "email", "publish_checkins" },
        			new DialogListener() {
            @Override
            public void onComplete(Bundle values) {
            	mAsyncRunner.request("me", new RequestListener(){

        			@Override
        			public void onComplete(String response, Object state) {
        				// TODO Auto-generated method stub
        				me = response;
        				
        				//Storing the about me json in local storage
        				SharedPreferences.Editor editor = settings.edit();
        				editor.putString("me", me);
        				editor.commit();

        				parseData();

        			}
        			@Override
        			public void onIOException(IOException e, Object state) {
        				// TODO Auto-generated method stub
        				
        			}
        			@Override
        			public void onFileNotFoundException(FileNotFoundException e,
        					Object state) {
        				// TODO Auto-generated method stub
        				
        			}
        			@Override
        			public void onMalformedURLException(MalformedURLException e,
        					Object state) {
        				// TODO Auto-generated method stub
        				
        			}
        			@Override
        			public void onFacebookError(FacebookError e, Object state) {
        				// TODO Auto-generated method stub
        				
        			}
                	
                });
            
            	mAsyncRunner.request("me/friends", new RequestListener(){

        			@Override
        			public void onComplete(String response, Object state) {
        				// TODO Auto-generated method stub
        				friends = response;
        				
        				SharedPreferences.Editor editor = settings.edit();
        				editor.putString("friends", friends);
        				editor.commit();
        				
        			}
        			@Override
        			public void onIOException(IOException e, Object state) {
        				// TODO Auto-generated method stub
        				
        			}
        			@Override
        			public void onFileNotFoundException(FileNotFoundException e,
        					Object state) {
        				// TODO Auto-generated method stub
        				
        			}
        			@Override
        			public void onMalformedURLException(MalformedURLException e,
        					Object state) {
        				// TODO Auto-generated method stub
        				
        			}
        			@Override
        			public void onFacebookError(FacebookError e, Object state) {
        				// TODO Auto-generated method stub
        				
        			}
                	
                });
            
            }

            @Override
            public void onFacebookError(FacebookError error) {
            	MainActivity.this.error = error.toString();
            }

            @Override
            public void onError(DialogError e) {
            	error=e.toString();
            }

            @Override
            public void onCancel() {}
        });
        }
        else{
        	MainActivity.this.parseData();
        }
        
    }

    public void initTabs(){
    	tabhost.setup();
        
        tabhost.addTab(tabhost.newTabSpec("Accounts")
                .setIndicator("Accounts")
                .setContent(R.id.accounts));

        tabhost.addTab(tabhost.newTabSpec("Expenses")
                .setIndicator("Expenses")
                .setContent(R.id.expenses));
		
    }
    
    public void parseData(){

    	final URL picUrl;
    	try{
    		//Parse the JSON data about me
    		JSONObject json = new JSONObject(me);
    		Iterator keys = json.keys();
    		map = new HashMap<String, String>();
    		while(keys.hasNext()){
    			String key = (String)keys.next();
    			map.put(key, json.getString(key));
    		}
    		
    		hello = map.get("first_name") + " " + map.get("last_name");
    		//hello = friends;
    		//Retrieve my profile picture
    		picUrl = new URL("http://graph.facebook.com/"+map.get("id")+"/picture");

    		new Thread(new Runnable(){
    			
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try{
						Bitmap mIcon1 = BitmapFactory.decodeStream(picUrl.openConnection().getInputStream());
    					bmp = mIcon1;
    					MainActivity.this.runOnUiThread(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								profilePic.setImageBitmap(bmp);
							}
    						
    					});
					}
					catch(Exception e){
						error = e.toString();
					}
				}
    		}).start();
    		new AddUser().execute(SERVICE_URI + "/AddUser");
    		new GetSummary().execute(SERVICE_URI + "/GetSummary/" + map.get("id"));
    		
    		this.runOnUiThread(new Runnable(){
    			public void run(){
    				
    				displayData();
    				
    			}
    		});
    		
    	}
    	catch(Exception e){
    		error = e.toString();
    	}
    }
    
    public void displayData(){
    	
    	name.setVisibility(View.VISIBLE);
        profilePic.setVisibility(View.VISIBLE);
        progress.hide();
        if(error == ""){
        	name.setText(this.hello);
        	//initLists();
        	
        }
        else{
        	//Error message
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage(error)
        	       .setCancelable(false)
        	       .setNeutralButton("OK", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	                dialog.cancel();
        	           }
        	       });
        	AlertDialog alert = builder.create();
        }
        	
    	
    }
    
    public void initLists(){
    	main = new mainAdapter(this, accounts);
    	ListView lv = (ListView)findViewById(R.id.listAccount);
    	lv.setAdapter(main);
    	
    	//Adding the summary objects to the local storage
    	try{
    		//String accts = settings.getString("accounts", "[\"accounts\" : {}]");
    		JSONObject tabs = new JSONObject();// = new JSONObject(accts);
    		JSONArray friendTabs = new JSONArray();// = tabs.getJSONArray("accounts");
    		JSONObject tab;// = new JSONObject();
    		//String myID = map.get("id");
    		//double bal = 0;
    		//String hisID = "";
    		for(int i = 0; i < accounts.size(); i++){
    			tab = new JSONObject();
    			tab.put("name", accounts.get(i).get("name").toString());
    			tab.put("id", accounts.get(i).get("id").toString());
    			tab.put("amount", Double.valueOf(accounts.get(i).get("amount").toString()).doubleValue());
    			tab.put("date", accounts.get(i).get("date").toString());
    			friendTabs.put(tab);
    		}
    		tabs.put("accounts", friendTabs);
    		Log.d("tabs",tabs.toString());
    		SharedPreferences.Editor editor = settings.edit();
    		editor.putString("accounts", tabs.toString());
    		editor.commit();
    	}
    	catch(Exception e){
    		//Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
    		//Log.e("tabs",tabs.toString());
    		Log.e("error",e.toString());
    	}
    }
    
    public void onAddButtonClick(View v){
    	Intent startNewActivityOpen = new Intent(MainActivity.this, addFriendActivity.class);
    	startActivity(startNewActivityOpen);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }
    
    @Override
    public void onResume(){
    	try{
    		this.accounts.clear();
    		HashMap<String, Object> temp;
    		String tabs = settings.getString("accounts", "[]");
    		JSONObject tab = new JSONObject(tabs);
    		JSONArray tabList = tab.getJSONArray("accounts");
    		for(int i = 0; i < tabList.length(); i++){
    			temp = new HashMap<String, Object>();
    			temp.put("id", tabList.getJSONObject(i).getString("id"));
    			temp.put("name", tabList.getJSONObject(i).getString("name"));
    			temp.put("amount", tabList.getJSONObject(i).getDouble("amount"));
    			accounts.add(temp);
    		}
    		main.notifyDataSetChanged();
    	}
    	catch(Exception e){
    		Log.e("MainActivity.onResume", e.toString());
    	}
    	super.onResume();
    }
    
    private class AddUser extends AsyncTask<String,Void,String> 
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
    		.object().key("user")
    		.object()
    		.key("firstName").value(map.get("first_name"))
    		.key("lastName").value(map.get("last_name"))
    		.key("email").value(map.get("email"))
    		.key("facebookId").value(map.get("id"))
    		.endObject()
    		.endObject();

    	StringEntity entity = new StringEntity(user.toString());
    	request.setEntity(entity);

    	DefaultHttpClient httpClient = new DefaultHttpClient();
    	HttpResponse response = httpClient.execute(request);


    	//Log.e("WebInvoke", "Saving : " + response.getStatusLine().getStatusCode());	
    	//JSONObject res = new JSONObject(response.toString());
    	Log.e("entity",response.getEntity().getContent().toString());
    	return response.getEntity().getContent().toString();

    } 

    catch (Exception e) 
    {
    	// 	TODO Auto-generated catch block
    	e.printStackTrace();
    	return null;
    }
    }
    
    @Override
    protected void onPostExecute(String result){
    	//Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_LONG);
    	//Log.e("blah",result.toString());
    }

    
    }

    private class GetSummary extends AsyncTask<String,Void,String> 
    {
    @Override
    protected String doInBackground(String... url)
    {
    try 
    {
    // POST request to <service>/SaveVehicle
    	HttpGet request = new HttpGet(url[0]);
    	request.setHeader("Accept", "application/json");
    	request.setHeader("Content-type", "application/json");
    	
    	//StringEntity entity = new StringEntity(user.toString());
    	//request.setEntity(entity);

    	DefaultHttpClient httpClient = new DefaultHttpClient();
    	HttpResponse response = httpClient.execute(request);

    	String result = getResponse(response.getEntity());
    	//Log.e("WebInvoke", "Saving : " + response.getStatusLine().getStatusCode());	
    	//JSONObject res = new JSONObject(response.toString());
    	Log.e("entity",result);
    	return result;

    } 

    catch (Exception e) 
    {
    	// 	TODO Auto-generated catch block
    	e.printStackTrace();
    	return null;
    }
    }
    
    @Override
    protected void onPostExecute(String result){
    	//Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_LONG);
    	//Log.e("blah",result.toString());
    	try{
    		
    		JSONObject results = new JSONObject(result);
    		
    		JSONArray summaries = results.getJSONArray("GetSummaryResult");
    		//String[] values = new String[summaries.length()];
    		ArrayList<HashMap<String, Object>> values = new ArrayList<HashMap<String, Object>>();
    		HashMap<String, Object> temp;
    		String myID = map.get("id");
    		String hisID = "";
    		double bal = 0;
    		for (int i = 0; i < summaries.length(); i++){
    			JSONObject summary = summaries.getJSONObject(i);
    			temp = new HashMap<String, Object>();
    			if(myID.equalsIgnoreCase(summary.getString("ower"))){
    				bal = (summary.getDouble("amount")) * (-1);
    				hisID = summary.getString("lender");
    			}
    			else if(myID.equalsIgnoreCase(summary.getString("lender"))){
    				bal = summary.getDouble("amount");
    				hisID = summary.getString("ower");
    			}
    			//bal = Double.valueOf(accounts.get(i).get("amount").toString()).doubleValue();
    			String friend = settings.getString("friends", "");
    			JSONObject friends = new JSONObject(friend);
    			JSONArray friendList = friends.getJSONArray("data");
    			for(int k = 0; k < friendList.length(); k++){
    				if(friendList.getJSONObject(k).getString("id").equalsIgnoreCase(hisID)){
    					//tab.put("name", friendList.getJSONObject(k).getString("name").toString());
    					temp.put("name", friendList.getJSONObject(k).getString("name").toString());
    					break;
    				}
    			}
    			
    			
    			
    			temp.put("id", hisID);
    			//temp.put("lender", summary.getString("lender"));
    			temp.put("amount", bal);
    			temp.put("date", summary.getString("date"));
    			
    			values.add(temp);
    			//values[i] = summary.toString();
    		}
    		MainActivity.this.accounts = values;
    		MainActivity.this.runOnUiThread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					initLists();
				}
    			
    		});
    		
    	}
    	catch(Exception e){
    		Log.e("error",e.toString());
    	}
    	
    }
    
    private String getResponse( HttpEntity entity )
    {
    String response = "";

    try
    {
    int length = ( int ) entity.getContentLength();
    StringBuffer sb = new StringBuffer( length );
    InputStreamReader isr = new InputStreamReader( entity.getContent(), "UTF-8" );
    char buff[] = new char[length];
    int cnt;
    while ( ( cnt = isr.read( buff, 0, length - 1 ) ) > 0 )
    {
    sb.append( buff, 0, cnt );
    }

    response = sb.toString();
    isr.close();
    } catch ( IOException ioe ) {
    ioe.printStackTrace();
    }

    return response;
    }
    }
}

