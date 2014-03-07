package com.example.feedme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.update66.thai.news.R;



import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;


public class MainActivity extends Activity {
	
	
	String TAG = "MAIN";
	ArrayList<String> headlines;
	ArrayList<String> links;
	ArrayList<String> imageURL;
	ArrayList<String> descriptions;
	ArrayList<String> pubDate;
	Activity mActivity;
	ListView listNews;
	GridView gridView;
	ListView mDrawerList;
	DrawerLayout mDrawerLayout;
	Menu menu;
	String mTitle;
	SharedPreferences prefs;
	// ActionBarDrawerToggle indicates the presence of Navigation Drawer in the
	// action bar
	ActionBarDrawerToggle mDrawerToggle;
	ProgressDialog dialog;
	
	int mMode,mCategory;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//MobileCore.init(this,"10KWAQ1IKAKGYYNYEK0FGHBEE78DP", LOG_TYPE.DEBUG,AD_UNITS.OFFERWALL);
		
		 // Create the adView
	 
		
//		MobileCore.showOfferWall(this, new CallbackResponse() {
//			@Override
//			public void onConfirmation(TYPE arg0) {
//			//finish();
//			}
//		});
		
		setContentView(R.layout.activity_main);
		mMode = 0;
		mCategory = 0;
		mTitle = "";
		
		
		
		
		prefs = this.getSharedPreferences("com.example.feedme", Context.MODE_PRIVATE);
		mMode = prefs.getInt("mode", FeedsAdapter.TYPE_NORMAL);
		// Initializing instance variables
		headlines = new ArrayList<String>();
		links = new ArrayList<String>();
		imageURL = new ArrayList<String>();
		descriptions = new ArrayList<String>();
		pubDate =new ArrayList<String>();
		mActivity = this;
		listNews = (ListView) findViewById(R.id.listnews);
		
		
		
	 
		
		// Getting reference to the DrawerLayout
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.drawer_list);
		gridView = (GridView)findViewById(R.id.gridview1);
		// Getting reference to the ActionBarDrawerToggle
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_menu, R.string.app_name,
				R.string.app_name) {

			/** Called when drawer is closed */
			@SuppressLint("NewApi")
			public void onDrawerClosed(View view) {
				getActionBar().setTitle("Update66");
				invalidateOptionsMenu();
			}

			/** Called when a drawer is opened */
			@SuppressLint("NewApi")
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle("Select category");
				invalidateOptionsMenu();
			}
		};
		// Setting DrawerToggle on DrawerLayout
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		MenuAdapter adapter = new MenuAdapter(this, getResources().getStringArray(R.array.rivers));
		
		// Setting the adapter on mDrawerList
		mDrawerList.setAdapter(adapter);
//		// Creating an ArrayAdapter to add items to the listview mDrawerList
//		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getBaseContext(), R.layout.drawer_list_item,
//				getResources().getStringArray(R.array.rivers));
//
//		// Setting the adapter on mDrawerList
//		mDrawerList.setAdapter(adapter1);

		// Setting item click listener for the listview mDrawerList
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				view.setSelected(true);
				
				HashMap<String, String> hitParameters = new HashMap<String, String>();
//				   <item>การเมือง</item>
//			        <item>เศรษฐกิจ</item>
//			        <item>ต่างประเทศ</item>
//			        <item>กีฬา</item>
//			        <item>ไอที</item>
//			        <item>บันเทิง</item>
//			        <item>สุขภาพ</item>
//			          <item>เกี่ยวกับเรา</item>
				String[] category = {"Politics","Finance","Foreign","Sports","IT","Entertainment","Health","About"};
				
				hitParameters.put("Open category", category[position]);
				EasyTracker.getInstance(mActivity).send(MapBuilder
					      .createEvent("OPEN",     // Event category (required)
				                   "CLICKSIDEBAR",  // Event action (required)
				                   category[position],   // Event label
				                   null)            // Event value
				      .build()
				  );
				
				if(position<7){
					mCategory = position;
					 dialog = ProgressDialog.show(mActivity, "", "กำลังโหลด");
					DownloadRssTask d = new DownloadRssTask();
					d.execute(String.valueOf(position + 1));
				}
				else{
					Intent k = new Intent(mActivity,AboutUsActivity.class);
					startActivity(k);
				}
				
				// Closing the drawer
				mDrawerLayout.closeDrawer(mDrawerList);
			}
		});

		
		
		//View customNav = LayoutInflater.from(this).inflate(R.layout.actionbutton, null); // layout which contains your button.
		//Button mButton = (Button) customNav.findViewById(R.id.date_num);
	//	mButton.setOnClickListener(this);

		//getActionBar().setCustomView(customNav);
		//getActionBar().setDisplayShowCustomEnabled(true);
		
		// Enabling Home button
		getActionBar().setHomeButtonEnabled(true);

		// Enabling Up navigation
		getActionBar().setDisplayHomeAsUpEnabled(true);
		dialog = ProgressDialog.show(this, "", "กำลังโหลด");
//		DownloadRssTask d = new DownloadRssTask();
//		d.execute();
		
		LoadContentFromServer d = new LoadContentFromServer();
		d.execute();
	}

	@Override
	protected void onStart() {
	    super.onStart();
	    EasyTracker.getInstance(this).activityStart(this); 
	   
	}   
	
	public InputStream getInputStream(URL url) {
		try {
			return url.openConnection().getInputStream();
		} catch (IOException e) {
			return null;
		}
	}

	
	
	  class LoadContentFromServer extends AsyncTask<Object, Integer, Object> {
	        
			@Override
	        protected Object doInBackground(Object... params) {
	        	
	        	String strXMLURL = "http://feeds2.feedburner.com/settrade/researchAll?format=xml";
	        	headlines.clear();
				links.clear();
				imageURL.clear();
				descriptions.clear();
				pubDate.clear();
	        	URL url;
				try {
					url = new URL(strXMLURL);
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document doc = db.parse(new InputSource(url.openStream()));
					doc.getDocumentElement().normalize();
					
					NodeList nodeList = doc.getElementsByTagName("item");
			        
			    
					
					Log.d("nodeList.getLength",String.valueOf(nodeList.getLength()));
					for(int i = 0; i < nodeList.getLength(); i++){

		    			
						Node item = nodeList.item(i);
						NodeList nodeItem = item.getChildNodes();
						String h=null,d=null,l=null,p=null;
						for(int j=0;j<nodeItem.getLength();j++){
							Log.v("nodeList.Nameh",nodeItem.item(j).getNodeName() + "/"+nodeItem.item(j).getTextContent());
							if(nodeItem.item(j).getNodeName().equals("title")){
								h = nodeItem.item(j).getTextContent();
							}
							else if(nodeItem.item(j).getNodeName().equals("description")){
								d = nodeItem.item(j).getTextContent();
							}
							else if(nodeItem.item(j).getNodeName().equals("link")){
								l = nodeItem.item(j).getTextContent();
							}
							else if(nodeItem.item(j).getNodeName().equals("pubDate")){
								p = nodeItem.item(j).getTextContent();
							}
						}
					
						if(h!=null&&d!=null&&l!=null&&p!=null){
							headlines.add(h);
							descriptions.add(removeDirtyBracket(removeImageTag(d)));
							links.add(l);
							pubDate.add(p);
							imageURL.add("");
						}
		    				
					}
					
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SAXException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		
				
				
	            return null;
	        }

	        @Override
	        public void onProgressUpdate(Integer... progress) {
	        	//imageAdapter.notifyDataSetChanged();
	        }
	        
	        @Override
	        protected void onPostExecute(Object result) {
	           // setProgressBarIndeterminateVisibility(false); // When Finish
	        	
	        	final String[] hlArr = headlines.toArray(new String[headlines.size()]);

				final String[] imgUrlArr = imageURL.toArray(new String[imageURL.size()]);
				final String[] desArr = descriptions.toArray(new String[descriptions.size()]);
				final String[] linkArr = links.toArray(new String[links.size()]);
				final String[] pubDataArr = pubDate.toArray(new String[pubDate.size()]);
				
			
				final FeedsAdapter adapter = new FeedsAdapter(mActivity, hlArr, imgUrlArr,pubDataArr);
				adapter.setType(mMode);
				
				if(mMode == FeedsAdapter.TYPE_IMGONLY){
					listNews.setVisibility(View.GONE);
					gridView.setVisibility(View.VISIBLE);
					gridView.setAdapter(null);
					gridView.setAdapter(adapter);
					
					
					
					gridView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> root, View view, int position, long id) {
							
							
							Intent k = new Intent(mActivity, NewsDetailActivity.class);
							k.putExtra("headline", hlArr[position]);
							k.putExtra("image", imgUrlArr[position]);
							k.putExtra("description", desArr[position]);
							k.putExtra("link", linkArr[position]);
							mActivity.startActivity(k);
						}
					});
				}
				else{
					
					listNews.setVisibility(View.VISIBLE);
					gridView.setVisibility(View.GONE);
					listNews.setAdapter(null);
					listNews.setAdapter(adapter);
					listNews.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> root, View view, int position, long id) {
							
							int adsPosition = adapter.getAdsPosition();
							Log.v(TAG,position+":"+adsPosition);
							if(position>=adsPosition){
								position = position - 1;
							}
							
							Intent k = new Intent(mActivity, NewsDetailActivity.class);
							k.putExtra("headline", hlArr[position]);
							k.putExtra("image", imgUrlArr[position]);
							k.putExtra("description", desArr[position]);
							k.putExtra("link", linkArr[position]);
							mActivity.startActivity(k);
						}
					});
				}
				
				if(dialog!=null)
				dialog.cancel();
			
	        }
	    }	
	  	
	
	private class DownloadRssTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... feed) {
			try {
				String feedCat;
				if (feed == null || feed.length == 0)
					feedCat = "1";
				else
					feedCat = feed[0];
				//URL url = new URL("http://www.update66.com/rss/" + feedCat + ".xml");
				//String strUrl = "http://www.stock2morrow.com/external.php?type=RSS2&forumids=2";
				//String strUrl = ("http://feeds2.feedburner.com/settrade/researchStock?format=xml";
				String strUrl ="http://feeds2.feedburner.com/settrade/researchAll?format=xml";
				//String strUrl ="http://www.ryt9.com/stock/rssfeed.xml";
				
				URL url = new URL(strUrl);
				
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(false);
				XmlPullParser xpp = factory.newPullParser();

				// We will get the XML from an input stream
				InputStreamReader reader = new InputStreamReader(getInputStream(url),"ASCII");
				
//				
//				BufferedReader rdr =  new BufferedReader(reader);
//		         String line = "";
//		         /*while ((line = rdr.readLine())!=null) {
//		           //String line = rdr.readLine();
//		           //System.out.println(line);
//		          sb.append(line);
//		         }
//		         */
//		        
//		         for(int i = 0; i<file.length();i++){
//		          int c = rdr.read();
//		          sb.append((char)c);
//		         }
//		         
//		         rdr.close();
		        
				xpp.setInput(reader);

				Log.v(TAG,"ENDOCINGGGG"+xpp.getInputEncoding());

				headlines.clear();
				links.clear();
				imageURL.clear();
				descriptions.clear();
				pubDate.clear();
				
				extract(convertStreamToString(getInputStream(url)));
				

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			// Binding data
			final String[] hlArr = headlines.toArray(new String[headlines.size()]);

			final String[] imgUrlArr = imageURL.toArray(new String[imageURL.size()]);
			final String[] desArr = descriptions.toArray(new String[descriptions.size()]);
			final String[] linkArr = links.toArray(new String[links.size()]);
			final String[] pubDataArr = pubDate.toArray(new String[pubDate.size()]);
			
		
			final FeedsAdapter adapter = new FeedsAdapter(mActivity, hlArr, imgUrlArr,pubDataArr);
			adapter.setType(mMode);
			
			if(mMode == FeedsAdapter.TYPE_IMGONLY){
				listNews.setVisibility(View.GONE);
				gridView.setVisibility(View.VISIBLE);
				gridView.setAdapter(null);
				gridView.setAdapter(adapter);
				
				
				
				gridView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> root, View view, int position, long id) {
						
						
						Intent k = new Intent(mActivity, NewsDetailActivity.class);
						k.putExtra("headline", hlArr[position]);
						k.putExtra("image", imgUrlArr[position]);
						k.putExtra("description", desArr[position]);
						k.putExtra("link", linkArr[position]);
						mActivity.startActivity(k);
					}
				});
			}
			else{
				
				listNews.setVisibility(View.VISIBLE);
				gridView.setVisibility(View.GONE);
				listNews.setAdapter(null);
				listNews.setAdapter(adapter);
				listNews.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> root, View view, int position, long id) {
						
						int adsPosition = adapter.getAdsPosition();
						Log.v(TAG,position+":"+adsPosition);
						if(position>=adsPosition){
							position = position - 1;
						}
						
						Intent k = new Intent(mActivity, NewsDetailActivity.class);
						k.putExtra("headline", hlArr[position]);
						k.putExtra("image", imgUrlArr[position]);
						k.putExtra("description", desArr[position]);
						k.putExtra("link", linkArr[position]);
						mActivity.startActivity(k);
					}
				});
			}
			
			if(dialog!=null)
			dialog.cancel();
		}
	}

	private String removeTagfromTitle(String body) {

		return body.replaceAll("\\<(/?[^\\>]+)\\>", "");

	}

	private String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	private String extractfromPattern(String body,String pattern){
		Pattern pat = Pattern.compile(pattern, Pattern.DOTALL);
		Matcher matcher = pat.matcher(body);
		if(matcher.find()) {
			return matcher.group(1);
		}
		else return null;
	}
	
	
	private String cleanDescription(String s){
		s = s.replace("P { margin: 0px; }","");
		return s;
	}
	
	private void extract(String body){
		Log.v(TAG,body);
		Pattern pattern = Pattern.compile("<item>(.*?)</item>",Pattern.DOTALL);
		Matcher matcher = pattern.matcher(body);
		
		while (matcher.find()) {
			
			String content = matcher.group(1);
			
			String h  = extractfromPattern(content,"<title>(.*?)</title>");
			String d  = extractfromPattern(content,"<description>(.*?)</description>");
			d = cleanDescription(d);
			String i = "";
			String l  = extractfromPattern(content,"<link>(.*?)</link>");
			String p  = extractfromPattern(content,"<pubDate>(.*?)</pubDate>");
			//String source = extractfromPattern(content,"<dc:creator>(.*?)</dc:creator>");
			//Log.v(TAG,"DATA is "+h +"========"+ d +"========"+ i +"========"+ l +"========"+p +"========"+source);
			
			if(h!=null&&d!=null&&l!=null&&p!=null){
				headlines.add(h);
				descriptions.add(removeDirtyBracket(removeImageTag(d)));
				links.add(l);
				pubDate.add(p);
				imageURL.add(i);
			}
		}
	}
	
	
	private String removeDirtyBracket(String body){
		return body.replaceAll("]]>", "");
	}
	
	private String removeImageTag(String body) {
		String returnString = body.replaceAll("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>", "");
		returnString = returnString.replaceAll("<IMG[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>", "");
		Log.v(TAG,"REMooooooooooooooooooVOEGINNGGGG..."+returnString);
		return returnString;
//		Pattern pattern = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
//		Matcher matcher = pattern.matcher(body);
//		StringBuilder builder = new StringBuilder();
//		int i = 0;
//		String imgURL = "";
//		while (matcher.find()) {
//
//			imgURL = matcher.group(1); // Access a submatch group; String can't
//										// do this.
//		}
//		
//		return imgURL;

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	/** Handling the touch event of app icon */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		String viewChangeto="NavButton";
		
		if(item.getItemId()==R.id.action_show_mini){
			mMode = FeedsAdapter.TYPE_MINI;
			menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.list_imgtext));
			dialog = ProgressDialog.show(mActivity, "", "กำลังโหลด");
			 DownloadRssTask d = new DownloadRssTask();
			 d.execute(String.valueOf(mCategory+1));
			 viewChangeto="mini image";
		}
		else if(item.getItemId()==R.id.action_show_normal){
			mMode = FeedsAdapter.TYPE_NORMAL;
			menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.list_fullimg));
			dialog = ProgressDialog.show(mActivity, "", "กำลังโหลด");
			 DownloadRssTask d = new DownloadRssTask();
			 d.execute(String.valueOf(mCategory+1));
			 
			 viewChangeto="normal image";
		}
		else if(item.getItemId()==R.id.action_show_text){
			mMode = FeedsAdapter.TYPE_TEXTONLY;
			menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.list_textonly));
			dialog = ProgressDialog.show(mActivity, "", "กำลังโหลด");
			 DownloadRssTask d = new DownloadRssTask();
			 d.execute(String.valueOf(mCategory+1));
			 
			 viewChangeto="text only";
		}
		else if(item.getItemId()==R.id.action_show_grid){
			mMode = FeedsAdapter.TYPE_IMGONLY;
			menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.list_imgonly2));
			dialog = ProgressDialog.show(mActivity, "", "กำลังโหลด");
			 DownloadRssTask d = new DownloadRssTask();
			 d.execute(String.valueOf(mCategory+1));
			 
			 viewChangeto="Grid";
		}
		
		
		EasyTracker.getInstance(mActivity).send(MapBuilder
			      .createEvent("OPEN",     // Event category (required)
		                   "CHANGEVIEW",  // Event action (required)
		                   viewChangeto,   // Event label
		                   null)            // Event value
		                    .build()
		);
		
		prefs.edit().putInt("mode", mMode).commit();
		 
		
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			
			
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/** Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

		menu.findItem(R.id.action_show_normal).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		if(mMode ==FeedsAdapter.TYPE_NORMAL){
			menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.list_fullimg));
		}
		else if(mMode ==FeedsAdapter.TYPE_MINI){
			menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.list_imgtext));
		}
		else if(mMode ==FeedsAdapter.TYPE_TEXTONLY){
			menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.list_textonly));
		}
		else{
			menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.list_imgonly2));
		}
		this.menu = menu;
		return true;
	}
	
	@Override
	protected void onStop() {
	    super.onStop();

	    EasyTracker.getInstance(this).activityStop(this); 
	}

	@Override
	protected void onDestroy() {
		
	    super.onDestroy();

	 
	}

	@Override
	public void onBackPressed() {

	        super.onBackPressed();
	}
}
