package com.android.odpd;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.android.*;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook.*;

public class MainActivity extends Activity {

    Facebook facebook = new Facebook("218187714969491");
    AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
    String me = "";
    String friends = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        

        facebook.authorize(this, new String[] { "email", "publish_checkins", "read_friendlists" },
        		new DialogListener() {
            @Override
            public void onComplete(Bundle values) {
            	mAsyncRunner.request("me", new RequestListener(){

        			@Override
        			public void onComplete(String response, Object state) {
        				// TODO Auto-generated method stub
        				me = response;
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
            	
            }

            @Override
            public void onError(DialogError e) {
            	me=e.toString();
            }

            @Override
            public void onCancel() {}
        });
        
        Button button = (Button)findViewById(R.id.buttonTest);
        
    }

    public void onClick(View v){
    	TextView hello = (TextView)findViewById(R.id.helloTextView);
        //ImageView profilePic = (ImageView)findViewById(R.id.imageView1);
        //URL picUrl = null;
    	try{
    		JSONObject json = new JSONObject(me);
    		Iterator keys = json.keys();
    		Map<String, String> map = new HashMap<String, String>();
    		while(keys.hasNext()){
    			String key = (String)keys.next();
    			map.put(key, json.getString(key));
    		}
    		
    		//hello.setText(map.get("username"));
    		//picUrl = new URL("http://graph.facebook.com/"+map.get("id")+"/picture");
    		//Bitmap mIcon1 = BitmapFactory.decodeStream(picUrl.openConnection().getInputStream());
    		//profilePic.setImageBitmap(mIcon1);
    		
    	}
    	catch(Exception e){}
    	hello.setText(me);
    	
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }
}