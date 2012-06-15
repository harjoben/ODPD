package com.android.odpd;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.odpd.*;

public class mainAdapter extends ArrayAdapter<HashMap<String, Object>> {
	private final Context context;
	private final ArrayList<HashMap<String, Object>> values;
	Bitmap[] bmps = new Bitmap[200];
	int pos;
	//Bitmap bmp = null;
	public mainAdapter(Context context, ArrayList<HashMap<String, Object>> values) {
		super(context, R.layout.rows, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.rows, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		TextView amtTextView = (TextView) rowView.findViewById(R.id.amount);
		final ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		textView.setText(values.get(position).get("name").toString());
		amtTextView.setText(values.get(position).get("amount").toString());
		double bal = Double.valueOf(values.get(position).get("amount").toString()).doubleValue();
		if(bal < 0){
			amtTextView.setTextColor(Color.RED);
		}
		else{
			amtTextView.setTextColor(Color.GREEN);
		}
		
		final String id = values.get(position).get("id").toString();
		//imageView.setImageURI(new Uri("http://graph.facebook.com/"+id+"/picture"));
//		pos = position;
//		bmps[position] = null;
//		new Thread(new Runnable(){
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				try{
//					URL picUrl = new URL("http://graph.facebook.com/"+id+"/picture");
//					Bitmap mIcon1 = BitmapFactory.decodeStream(picUrl.openConnection().getInputStream());
//					//bmps[pos] = mIcon1;
//					
//				}
//				catch(Exception e){
//					Log.e("error", e.toString());
//				}
//			}
//		}).start();
		// Change the icon for Windows and iPhone
		//String s = values[position];

		//while(bmps[position] == null){}
		//imageView.setImageBitmap(bmps[position]);
		return rowView;
	}
}
