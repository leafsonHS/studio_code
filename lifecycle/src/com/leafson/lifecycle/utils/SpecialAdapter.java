package com.leafson.lifecycle.utils;

import java.util.List;
import java.util.Map;

import com.leafson.lifecycle.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class SpecialAdapter extends SimpleAdapter {

	private int[] colors = new int[] { 0x30FF0000, 0x300000FF };
	public SpecialAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		int colorPos = position % colors.length;
//		if (colorPos == 1)
//			view.setBackgroundColor(Color.parseColor("#E2DEAF")); 
//		else
//			view.setBackgroundColor(Color.parseColor("#F3F2D4"));
		
		if (colorPos == 1)
			view.setBackgroundColor(Color.parseColor("#ffffff")); 
		else
			view.setBackgroundColor(Color.parseColor("#f8f8f8"));
		return view;
	}
}
