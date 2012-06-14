package com.android.odpd;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class friendsListAdapter extends ArrayAdapter<HashMap<String, Object>> {

	ArrayList<HashMap<String, Object>> Strings;
	private final Context context;
	public friendsListAdapter(Context context, int textViewResourceId,
			ArrayList<HashMap<String, Object>> Strings) {

		//let android do the initializing :)
		super(context, textViewResourceId, Strings);
		this.Strings = Strings;
		this.context = context;
	}


	//class for caching the views in a row  
	private class ViewHolder
	{
		ImageView photo;
		TextView name;

	}

	ViewHolder viewHolder;
	LayoutInflater inflater;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if(convertView==null)
		{
			inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView=inflater.inflate(R.layout.friends_row, null);
			viewHolder=new ViewHolder();

			//cache the views
			//viewHolder.photo=(ImageView) convertView.findViewById(R.id.photo);
			viewHolder.name=(TextView) convertView.findViewById(R.id.name);
			//viewHolder.team=(TextView) convertView.findViewById(R.id.team);

			//link the cached views to the convertview
			convertView.setTag(viewHolder);

		}
		else
			viewHolder=(ViewHolder) convertView.getTag();


		//int photoId=(Integer) searchResults.get(position).get("photo");

		//set the data to be displayed
		//viewHolder.photo.setImageDrawable(getResources().getDrawable(photoId));
		viewHolder.name.setText(Strings.get(position).get("name").toString());
		//viewHolder.team.setText(searchResults.get(position).get("team").toString());

		//return the view to be displayed
		return convertView;
	}








	//	private final Context context;
	//	private final String[] values;
	//	//private final Bitmap[] images;
	//
	//	public friendsListAdapter(Context context, String[] values) {
	//		super(context, R.layout.friends_row, values);
	//		this.context = context;
	//		this.values = values;
	//		//this.images = images;
	//	}
	//
	//	@Override
	//	public View getView(int position, View convertView, ViewGroup parent) {
	//		LayoutInflater inflater = (LayoutInflater) context
	//				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	//		View rowView = inflater.inflate(R.layout.friends_row, parent, false);
	//		TextView textView = (TextView) rowView.findViewById(R.id.name);
	//		ImageView imageView = (ImageView) rowView.findViewById(R.id.profile);
	//		textView.setText(values[position]);
	//		//imageView.setImageBitmap(images[position]);
	//
	//		return rowView;
	//	}
}
