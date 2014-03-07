package com.example.feedme;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.update66.thai.news.R;

public class ImageLoader {

	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;

	public ImageLoader(Context context) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
	}

	final int stub_id = R.drawable.noimg2;
	public void DisplayImage(String url, ImageView imageView) {
		
		if (url == null) {
			imageView.setImageResource(R.drawable.noimg2);
			return;
		}
		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);
		imageView.setAdjustViewBounds(true);

		if (bitmap != null)
			imageView.setImageBitmap(bitmap);
			
			
		else {
			queuePhoto(url, imageView);
			imageView.setImageResource(stub_id);
		}
	}
	
	public void DisplayImageNoCache(String url, ImageView imageView) {
		
		if (url == null) {
			imageView.setImageResource(R.drawable.noimg2);
			return;
		}
		memoryCache.clear();
		fileCache.clear();
		imageViews.remove(imageView);
		imageViews.put(imageView, url);
		//memoryCache.clear();
		
		imageView.setAdjustViewBounds(true);
		queuePhotoNoCache(url, imageView);
		imageView.setImageResource(stub_id);
		
	}
	
//	public void loadBitmap(String url){
//		Bitmap bitmap = memoryCache.get(url);
//		if (bitmap != null)
//			return bitmap;
//			
//		else {
//			queuePhoto(url, imageView);
//			imageView.setImageResource(stub_id);
//		}
//	}
	
//	public void addMarkerImage(String url,Bubble bubble,GoogleMap googleMap){
//		
//		Bitmap bitmap = memoryCache.get(url);
//		bubble.marker = googleMap.addMarker(new MarkerOptions()
//		.position(new LatLng(bubble.lat, bubble.lng))
//		.icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
//		//.icon(BitmapDescriptorFactory.fromResource(bubble.getBigBubbleDrawable())));
//		bubble.marker.setVisible(Core.categoriesSetting[bubble.category]);
//		
//	}

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p,true));
	}
	
	private void queuePhotoNoCache(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p,false));
	}

	private Bitmap getBitmap(String url,boolean reused) {
		// TB TODO - Disable file stuff
		// TB TODO - This is not working on hardware device
		// Throws exception java.io.FileNotFoundException:
		// /mnt/sdcard/LazyList/234324324 (No such file or directory)
		// Was caused by missing permission to write files.
		// Do I even need this? Can't I just use memory?
		// If i use it, I need to clear the file cache every once in a while?
		File f = null;
		
		f= fileCache.getFile(url);

		// from SD cache
		Bitmap b = decodeFile(f);
		if (b != null&&reused)
			return b;

		// from web
		try {
			
			
			
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();

			// TB TODO - Disable file stuff
			OutputStream os = new FileOutputStream(f);
			Utils.CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f);
			// bitmap = BitmapFactory.decodeStream(is);

			
			
			return bitmap;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			//o.inJustDecodeBounds = true;
			
			FileInputStream fileInput = new FileInputStream(f);
			BitmapFactory.decodeStream(fileInput, null, o);
			try {
				fileInput.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 500;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
		//	scale = 0;
			o2.inSampleSize = scale;
			
			FileInputStream returnFileInput = new FileInputStream(f);
			Bitmap returnBitmap = BitmapFactory.decodeStream(returnFileInput, null, o2);
			try {
				returnFileInput.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return returnBitmap;//BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;
		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}
	
	// Task for the queue
		private class BitmapToLoad {
			public String url;
			public Bitmap imageView;
			public BitmapToLoad(String u, Bitmap i) {
				url = u;
				imageView = i;
			}
		}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;
		boolean reusedImageView;
		PhotosLoader(PhotoToLoad photoToLoad,boolean reusedImageView) {
			this.photoToLoad = photoToLoad;
			this.reusedImageView = reusedImageView;
		}

		@Override
		public void run() {
			if (reusedImageView&&imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url,reusedImageView);
			memoryCache.put(photoToLoad.url, bmp);
			if (reusedImageView&&imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad,reusedImageView);
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

//	
//	class BitmapLoader implements Runnable {
//		BitmapToLoad photoToLoad;
//		BitmapLoader(BitmapToLoad photoToLoad) {
//			this.photoToLoad = photoToLoad;
//		}
//
//		@Override
//		public void run() {
//			
//			Bitmap bmp = getBitmap(photoToLoad.url);
//			memoryCache.put(photoToLoad.url, bmp);
//			
//			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
//			Activity a = (Activity) photoToLoad.imageView.getContext();
//			a.runOnUiThread(bd);
//		}
//	}
	
	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;
		boolean reusedImageView;
		public BitmapDisplayer(Bitmap b, PhotoToLoad p,boolean reusedImageView) {
			bitmap = b;
			photoToLoad = p;
			this.reusedImageView = reusedImageView;
		}
		public void run() {
			if (imageViewReused(photoToLoad)&&reusedImageView)
				return;
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);
			else
				photoToLoad.imageView.setImageResource(stub_id);
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
		
	}

}