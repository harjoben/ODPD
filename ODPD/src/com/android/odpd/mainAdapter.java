package com.android.odpd;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class mainAdapter extends ArrayAdapter<HashMap<String, Object>> {
	private final Context context;
	private final ArrayList<HashMap<String, Object>> values;

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
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		textView.setText(values.get(position).get("name").toString());
		// Change the icon for Windows and iPhone
		//String s = values[position];
		

		return rowView;
	}
}
