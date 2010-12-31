/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itnoles.shared;

import org.apache.http.*; //HttpEntity, HttpResponse and HttpStatus
import org.apache.http.client.methods.HttpGet;

import android.graphics.*; //Bitmap and BitmapFactory
import android.os.*; // AsyncTask and Handler
import android.net.http.AndroidHttpClient;
import android.util.Log;
import android.widget.ImageView;

import java.io.*; //FilterInputStream and InputStream
import java.lang.ref.*; //SoftReference and WeakReference
import java.util.*; //HashMap and LinkedHashMap
import java.util.concurrent.ConcurrentHashMap;

/**
 * This helper class download images from the Internet and binds those with the provided ImageView.
 *
 * It requires the INTERNET permission, which should be added to your application's manifest
 * file.
 *
 * A local cache of downloaded images is maintained internally to improve performance.
 */
public class ImageDownloader {
	private static final String TAG = "ImageDownloader";
	
	/**
	 * Download the specified image from the Internet and binds it to the provided ImageView. The
	 * binding is immediate if the image is found in the cache and will be done asynchronously
	 * otherwise. A null bitmap will be associated to the ImageView if an error occurs.
	 *
	 * @param url The URL of the image to download.
	 * @param imageView The ImageView to bind the downloaded image to.
	 */
	public void download(String url, ImageView imageView) {
		// State sanity: url is guaranteed to never be null in cache keys.
		if (url == null) {
			imageView.setImageDrawable(null);
			return;
		}
		
		resetPurgeTimer();
		Bitmap bitmap = getBitmapFromCache(url);
		if (bitmap == null)
			forceDownload(url, imageView);
		else
			imageView.setImageBitmap(bitmap);
	}
	
	/**
	 * Same as download but the image is always downloaded and the cache is not used.
	 * Kept private at the moment as its interest is not clear.
	 */
	private void forceDownload(String url, ImageView imageView) {
		imageView.setMinimumHeight(156);
		new BitmapDownloaderTask(imageView).execute(url);
	}
	
	Bitmap downloadBitmap(String url) {
		// AndroidHttpClient is not allowed to be used from the main thread
		final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		try {
			final HttpGet getRequest = new HttpGet(url);
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w(TAG, "Error " + statusCode + " while retrieving bitmap from " + url);
				return null;
			}
			final HttpEntity entity = response.getEntity();
			if (entity == null)
				return null;
			
			InputStream inputStream = null;
			try {
				inputStream = entity.getContent();
				// return BitmapFactory.decodeStream(inputStream);
				// Bug on slow connections, fixed in future release.
				return BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
			} finally {
				if (inputStream != null)
					inputStream.close();
				entity.consumeContent();
			}
		} catch (Exception e) {
			Log.w(TAG, "Error while retrieving bitmap from " + url, e);
		} finally {
			client.close();
		}
		return null;
	}

	/*
	 * An InputStream that skips the exact number of bytes provided, unless it reaches EOF.
	 */
	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}
		
		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int b = read();
					if (b < 0)
						break;  // we reached EOF
					else
						bytesSkipped = 1; // we read one byte
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}
	
	/**
	 * The actual AsyncTask that will asynchronously download the image.
	 */
	class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		private String url;
		private final WeakReference<ImageView> imageViewReference;
		
		public BitmapDownloaderTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}
		
		/**
		 * Actual download method.
		 */
		@Override
		protected Bitmap doInBackground(String... params) {
			url = params[0];
			return downloadBitmap(url);
		}
		
		/**
		 * Once the image is downloaded, associates it to the imageView
		 */
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled())
				bitmap = null;
				
			addBitmapToCache(url, bitmap);
			
			if (imageViewReference != null) {
				ImageView imageView = imageViewReference.get();
				imageView.setImageBitmap(bitmap);
			}
		}
	}
	
	/*
	 * Cache-related fields and methods.
	 * 
	 * We use a hard and a soft cache. A soft reference cache is too aggressively cleared by the
	 * Garbage Collector.
	 */
	
	private static final int HARD_CACHE_CAPACITY = 10;
	private static final int DELAY_BEFORE_PURGE = 10 * 1000; // in milliseconds
	
	// Soft cache for bitmaps kicked out of hard cache
	private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache =
	new ConcurrentHashMap<String, SoftReference<Bitmap>>(HARD_CACHE_CAPACITY / 2);
	
	// Hard cache, with a fixed maximum capacity and a life duration
	private final HashMap<String, Bitmap> sHardBitmapCache = 
	new LinkedHashMap<String, Bitmap>(HARD_CACHE_CAPACITY / 2, 0.75f, true) {
		@Override
		protected boolean removeEldestEntry(LinkedHashMap.Entry<String, Bitmap> eldest) {
			if (size() > HARD_CACHE_CAPACITY) {
				// Entries push-out of hard reference cache are transferred to soft reference cache
				sSoftBitmapCache.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue()));
				return true;
			} else
				return false;
		}
	};
	
	private final Handler purgeHandler = new Handler();
	
	private final Runnable purger = new Runnable() {
		public void run() {
			clearCache();
		}
	};
	
	/**
	 * Adds this bitmap to the cache.
	 * @param bitmap The newly downloaded bitmap.
	 */
	private void addBitmapToCache(String url, Bitmap bitmap) {
		if (bitmap != null) {
			synchronized (sHardBitmapCache) {
				sHardBitmapCache.put(url, bitmap);
			}
		}
	}
	
	/**
	 * @param url The URL of the image that will be retrieved from the cache.
	 * @return The cached bitmap or null if it was not found.
	 */
	private Bitmap getBitmapFromCache(String url) {
		// First try the hard reference cache
		synchronized (sHardBitmapCache) {
			final Bitmap bitmap = sHardBitmapCache.get(url);
			if (bitmap != null) {
				// Bitmap found in hard cache
				// Move element to first position, so that it is removed last
				sHardBitmapCache.remove(url);
				sHardBitmapCache.put(url, bitmap);
				return bitmap;
			}
		}
		
		// Then try the soft reference cache
		SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(url);
		if (bitmapReference != null) {
			final Bitmap bitmap = bitmapReference.get();
			if (bitmap != null)
				// Bitmap found in soft cache
				return bitmap;
			else
				// Soft reference has been Garbage Collected
				sSoftBitmapCache.remove(url);
		}
		return null;
	}
	
	/**
	 * Clears the image cache used internally to improve performance. Note that for memory
	 * efficiency reasons, the cache will automatically be cleared after a certain inactivity delay.
	 */
	public void clearCache() {
		sHardBitmapCache.clear();
		sSoftBitmapCache.clear();
	}
	
	/**
	 * Allow a new delay before the automatic cache clear is done.
	 */
	private void resetPurgeTimer() {
		purgeHandler.removeCallbacks(purger);
		purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE);
	}
}