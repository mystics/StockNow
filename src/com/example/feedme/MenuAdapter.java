package com.example.feedme;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Bundle;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.update66.thai.news.R;
/**
 * Activity list
 * 
 * @author Virus
 * 
 */
public class MenuAdapter extends BaseAdapter {

	private Vector<String> dataMenu;
	
	private static LayoutInflater inflater = null;
	final static String TAG = "FeedsAdapter";
	
	Activity mA;
	public MenuAdapter(Activity a, String[] menu) {
		mA = a;
		//data = d;
		dataMenu = new Vector<String>();
		for(int i=0;i<menu.length;i++){
			dataMenu.add(menu[i]);
		}
		
		inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		
		
		
	}

	
	public int getCount() {
		
		return dataMenu.size();
	}



	@SuppressLint("SimpleDateFormat")
	public View getView( int position, View convertView, ViewGroup parent) {
		
		
		View vi = convertView;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.list_menu,null);
			
		}
		((TextView)(vi.findViewById(R.id.textView1))).setText(dataMenu.get(position).toString());
		
		return vi;
	}

	@Override
	public Object getItem(int position) {
		
		return null;
	}

	@Override
	public long getItemId(int position) {
		
		return 0;
	}
}