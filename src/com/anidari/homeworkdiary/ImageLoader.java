package com.anidari.homeworkdiary;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ImageView;

public class ImageLoader {

	Context context;

	public ImageLoader(Context context) {
		this.context = context;
	}

	public void loadBitmap(int resId, ImageView imageView) {
		if (cancelPotentialWork(resId, imageView)) {

			resetPurgeTimer();
			Bitmap bitmap = getBitmapFromCache(Integer.toString(resId));

			if (bitmap == null) {
				// create a new task for the given imageView
				final BitmapWorkerTask task = new BitmapWorkerTask(imageView);

				// creates a placeholder drawable that is used to determine if
				// the ImageView in question is already in use,
				// also creates a new weak reference to the task
				final AsyncDrawable asyncDrawable = new AsyncDrawable(task);

				imageView.setImageDrawable(asyncDrawable);
				task.execute(resId);
			} else
				imageView.setImageBitmap(bitmap);
		}
	}

	/**
	 * Uses asyncDrawable placeholder to check if there is already a task in
	 * progress for a particular imageView. If there is then that task is
	 * cancelled and replaced by the new one
	 * 
	 * @return true if no task is associated with the ImageView, or an existing
	 *         task was cancelled
	 */
	public static boolean cancelPotentialWork(int data, ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

		if (bitmapWorkerTask != null) {
			final int bitmapData = bitmapWorkerTask.data;
			if (bitmapData != data) {
				// Cancel previous task
				bitmapWorkerTask.cancel(true);
			} else {
				// The same work is already in progress
				return false;
			}
		}
		// No task associated with the ImageView, or an existing task was
		// cancelled
		return true;
	}

	/**
	 * Returns the task associated with a particular ImageView if one exists,
	 * returns null otherwise
	 */
	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		// checks to see if the image reference still refers to an ImageView
		if (imageView != null) {
			// Return the view's drawable, or null if no drawable has been
			// assigned
			final Drawable drawable = imageView.getDrawable();
			// if the drawable is the placeholder put in by AsyncDrawable then
			// returns the task associated with the ImageView
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}

	/**
	 * The main threaded task where the image is loaded from file and placed in
	 * the proper ImageView
	 */
	class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private int data = 0;

		// private Context context;

		public BitmapWorkerTask(ImageView imageView) {
			// Use a WeakReference to ensure the ImageView can be garbage
			// collected
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(Integer... params) {
			data = params[0];
			return BitmapTools.decodeSampledBitmapFromResource(
					context.getResources(), data, 100, 100);
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			addBitmapToCache(Integer.toString(data), bitmap);

			if (imageViewReference != null && bitmap != null) {
				final ImageView imageView = imageViewReference.get();
				final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
				if (this == bitmapWorkerTask && imageView != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		}
	}

	/**
	 * Extends ColourDrawable to create a placeholder drawable that can be used
	 * to reference a task associated with an ImageView
	 */
	static class AsyncDrawable extends ColorDrawable {
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		public AsyncDrawable(BitmapWorkerTask bitmapWorkerTask) {
			super(Color.TRANSPARENT);
			bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(
					bitmapWorkerTask);
		}

		public BitmapWorkerTask getBitmapWorkerTask() {
			return bitmapWorkerTaskReference.get();
		}
	}

	/*
	 * Cache-related fields and methods.
	 * 
	 * We use a hard and a soft cache. A soft reference cache is too
	 * aggressively cleared by the Garbage Collector.
	 */

	// NOTE: The below code was taken from
	// https://code.google.com/p/android-imagedownloader/source/browse/trunk/src/com/example/android/imagedownloader/ImageDownloader.java
	// It would probably be better using the LruCache as described at
	// http://developer.android.com/training/displaying-bitmaps/cache-bitmap.html

	private static final int HARD_CACHE_CAPACITY = 10;
	private static final int DELAY_BEFORE_PURGE = 10 * 1000; // in milliseconds

	// Hard cache, with a fixed maximum capacity and a life duration
	private final HashMap<String, Bitmap> sHardBitmapCache = new LinkedHashMap<String, Bitmap>(
			HARD_CACHE_CAPACITY / 2, 0.75f, true) {

		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(
				LinkedHashMap.Entry<String, Bitmap> eldest) {
			if (size() > HARD_CACHE_CAPACITY) {
				// Entries push-out of hard reference cache are transferred to
				// soft reference cache
				sSoftBitmapCache.put(eldest.getKey(),
						new SoftReference<Bitmap>(eldest.getValue()));
				return true;
			} else
				return false;
		}
	};

	// Soft cache for bitmaps kicked out of hard cache
	private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(
			HARD_CACHE_CAPACITY / 2);

	private final Handler purgeHandler = new Handler();

	private final Runnable purger = new Runnable() {
		public void run() {
			clearCache();
		}
	};

	/**
	 * Adds this bitmap to the cache.
	 * 
	 * @param bitmap
	 *            The newly loaded bitmap.
	 */
	private void addBitmapToCache(String reference, Bitmap bitmap) {
		if (bitmap != null) {
			synchronized (sHardBitmapCache) {
				sHardBitmapCache.put(reference, bitmap);
			}
		}
	}

	/**
	 * @param android
	 *            reference as string of the image that will be retrieved from
	 *            the cache.
	 * @return The cached bitmap or null if it was not found.
	 */
	private Bitmap getBitmapFromCache(String reference) {
		// First try the hard reference cache
		synchronized (sHardBitmapCache) {
			final Bitmap bitmap = sHardBitmapCache.get(reference);
			if (bitmap != null) {
				// Bitmap found in hard cache
				// Move element to first position, so that it is removed last
				sHardBitmapCache.remove(reference);
				sHardBitmapCache.put(reference, bitmap);
				return bitmap;
			}
		}

		// Then try the soft reference cache
		SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(reference);
		if (bitmapReference != null) {
			final Bitmap bitmap = bitmapReference.get();
			if (bitmap != null) {
				// Bitmap found in soft cache
				return bitmap;
			} else {
				// Soft reference has been Garbage Collected
				sSoftBitmapCache.remove(reference);
			}
		}

		return null;
	}

	/**
	 * Clears the image cache used internally to improve performance. Note that
	 * for memory efficiency reasons, the cache will automatically be cleared
	 * after a certain inactivity delay.
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
