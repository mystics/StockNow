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
public class FeedsAdapter extends BaseAdapter {

	private Vector<String> dataHeadline;
	private Vector<String> dataImage;
	private Vector<String> dataPubDate;
	private static LayoutInflater inflater = null;
	final static String TAG = "FeedsAdapter";
	int type;
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_MINI = 1;
	public static final int TYPE_TEXTONLY = 2;
	public static final int TYPE_IMGONLY = 3;
	int screenWidth;
	int screenHeight;
	int adsPosition;
	ImageLoader mImageLoader;
	boolean mShowName;
	
	Activity mA;
	public FeedsAdapter(Activity a, String[] headLines,String[] images,String[] pubDate) {
		mA = a;
		//data = d;
		dataHeadline = new Vector<String>();
		for(int i=0;i<headLines.length;i++){
			dataHeadline.add(headLines[i]);
		}
		
		dataImage = new Vector<String>();
		for(int i=0;i<images.length;i++){
			dataImage.add(images[i]);
		}
		dataPubDate = new Vector<String>();
		for(int i=0;i<pubDate.length;i++){
			dataPubDate.add(pubDate[i]);
		}
		
		inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mImageLoader = new ImageLoader(mA);
		
		Display display = a.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		 screenWidth = size.x;
		 screenHeight = size.y;
		 
		 
		 adsPosition = (new Random()).nextInt(dataImage.size()-2)+1;
	}

	public void setType(int type){
		this.type = type;
		if(type==TYPE_IMGONLY)adsPosition=0;
	}
	
	public int getCount() {
		if(type!=TYPE_IMGONLY)
			return dataHeadline.size()+1;
		else return dataHeadline.size();
	}

	public int getAdsPosition(){
		return adsPosition;
	}
	
	private View getViewTextOnly(View convertView,int position,String strHL){
		//Mini image view
		View vi = convertView;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.list_feed_textonly, null);
			vi.setTag("1");
		}
		
		if(vi.getTag()!=null||vi.getTag().equals("2")){
			vi = inflater.inflate(R.layout.list_feed_textonly, null);
			vi.setTag("1");
		}
		
		TextView headline = (TextView)vi.findViewById(R.id.textView1);
		headline.setText(strHL);
		
		return vi;
	}
	
	private View getViewMini(View convertView,int position,String strHL){
		//Mini image view
		View vi = convertView;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.list_feed_imgtext, null);
			vi.setTag("1");
		}
		
		if(vi.getTag()!=null||vi.getTag().equals("2")){
			vi = inflater.inflate(R.layout.list_feed_imgtext, null);
			vi.setTag("1");
		}
		TextView headline = (TextView)vi.findViewById(R.id.textView1);
		headline.setText(strHL);
		TextView updateAt = (TextView)vi.findViewById(R.id.update);
		ImageView imgNews = (ImageView)vi.findViewById(R.id.imageView1);
		mImageLoader.DisplayImage((String)dataImage.get(position),imgNews);
		updateAt.setText(dataPubDate.get(position).toString());
		return vi;
	}
	
	private View getImageonlyView(View convertView,int position,String strHL){
		//Mini image view
				View vi = convertView;
				if (convertView == null) {
					vi = inflater.inflate(R.layout.list_feed_imgonly, null);
					vi.setTag("1");
				}
				
				if(vi.getTag()!=null||vi.getTag().equals("2")){
					vi = inflater.inflate(R.layout.list_feed_imgonly, null);
					vi.setTag("1");
				}
				
				TextView headline = (TextView)vi.findViewById(R.id.textView1);
				ImageView imgNews = (ImageView)vi.findViewById(R.id.imageView1);
				if(position<dataImage.size()&&!dataImage.get(position).toString().equals("")){
					
					headline.setVisibility(View.GONE);
					imgNews.setVisibility(View.VISIBLE);
					mImageLoader.DisplayImage((String)dataImage.get(position),imgNews);
					
					ViewGroup.LayoutParams layoutParams = imgNews.getLayoutParams();
					layoutParams.height =screenWidth>>1; //this is in pixels
					layoutParams.width = screenWidth>>1;
					imgNews.setLayoutParams(layoutParams);
					imgNews.setScaleType(ScaleType.CENTER_CROP);
				}
				else{
					
					
					headline.setText(strHL);
					headline.setVisibility(View.VISIBLE);
				//	imgNews.setVisibility(View.GONE);
					imgNews.setImageDrawable(mA.getResources().getDrawable(R.drawable.noimg2));
				//	mImageLoader.DisplayImage((String)dataImage.get(position),imgNews);
				
//					ViewGroup.LayoutParams layoutParams = headline.getLayoutParams();
//					layoutParams.height =screenWidth>>1; //this is in pixels
//					layoutParams.width = screenWidth>>1;
//					imgNews.setLayoutParams(layoutParams);
//					imgNews.setScaleType(ScaleType.CENTER_CROP);
//					headline.setLayoutParams(layoutParams);
				}
				
				
				
				return vi;
	}
	
	private View getViewNormal(View convertView,int position,String strHL){
		//Full image view
		View vi = convertView;
		
		
		
		if(position<dataImage.size()&&!dataImage.get(position).toString().equals("")){
			
			if (convertView == null) {
				vi = inflater.inflate(R.layout.list_feed, null);
				vi.setTag("1");
			}
			else{
				if(vi.getTag()==null||!vi.getTag().equals("1")){
					vi = inflater.inflate(R.layout.list_feed, null);
					vi.setTag("1");
				}
			}
			TextView headline = (TextView)vi.findViewById(R.id.textView1);
			ImageView imgProfile = (ImageView)vi.findViewById(R.id.imageView1);
			if(position<dataImage.size()&&!dataImage.get(position).toString().equals(""))
			mImageLoader.DisplayImage((String)dataImage.get(position),imgProfile);
			
			int newHeight = screenWidth;
			// double check my math, this should be right, though
			int newWidth = screenWidth;
			
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(newWidth, newHeight);
			imgProfile.setLayoutParams(params);
			imgProfile.setScaleType(ImageView.ScaleType.CENTER_CROP);
			
			
			headline.setText(strHL);
		}
		else{
			//No image view
			//Full image view
			if (convertView == null) {
				vi = inflater.inflate(R.layout.list_feed_imgtext, null);
				vi.setTag("2");
			}
			else{
				if(!vi.getTag().equals("2")){
					vi = inflater.inflate(R.layout.list_feed_imgtext, null);
					vi.setTag("2");
				}
			}
			
			TextView headline = (TextView)vi.findViewById(R.id.textView1);
			if(headline!=null)
			headline.setText(strHL);
			TextView updateAt = (TextView)vi.findViewById(R.id.update);
			if(updateAt!=null)
			updateAt.setText(dataPubDate.get(position).toString());
			
		}
		return vi;
	}

	@SuppressLint("SimpleDateFormat")
	public View getView( int position, View convertView, ViewGroup parent) {
		Log.v(TAG,"Postiion:"+position);
		if(position>adsPosition&&type!=TYPE_IMGONLY){
			position = position - 1;
		}
		
		else
		if(position ==adsPosition&&type!=TYPE_IMGONLY){
			
			if(convertView==null||!convertView.getTag().equals("2")){
				 // Create a new AdView
		        AdView adView = new AdView(mA, AdSize.BANNER,
		                                   "a153032538e539a");

		        // Convert the default layout parameters so that they play nice with
		        // ListView.

		        float density = mA.getResources().getDisplayMetrics().density;
		        int height = Math.round(AdSize.BANNER.getHeight() * density);
		        AbsListView.LayoutParams params = new AbsListView.LayoutParams(
		            AbsListView.LayoutParams.FILL_PARENT,
		            height);
		        adView.setLayoutParams(params);

		        AdRequest adR = new AdRequest();
		 	 //  adR.addTestDevice(AdRequest.TEST_EMULATOR);
		 	  // adR.setTesting(true);
		 	    // Load the adView with the ad request.
		 	    adView.loadAd(adR);
		 	    adView.setTag("2");
		        return adView;
			}
			else return convertView;
		}
		
		
		View vi = convertView;
		String strHL = (String)dataHeadline.get(position);
		
		if(type==TYPE_NORMAL){
			vi = getViewNormal(vi,position,strHL);
		}
		else if(type==TYPE_MINI){
			vi = getViewMini(vi,position,strHL);
		}
		else if(type==TYPE_TEXTONLY){
			vi = getViewTextOnly(vi, position, strHL);
		}
		else{
			vi = getImageonlyView(vi,position,strHL);
		}
		
		
		
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