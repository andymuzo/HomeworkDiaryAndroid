package com.anidari.homeworkdiary;

import com.anromus.homeworkdiary.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	
	ImageLoader imageLoader;

	public ImageAdapter(Context c) {
		mContext = c;
		imageLoader = new ImageLoader(c);
	}

	@Override
	public int getCount() {
		return mThumbIds.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) { // not recycled
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(124, 124));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(8, 8, 8, 8);
		} else {
			imageView = (ImageView) convertView;
		}

		imageLoader.loadBitmap(mThumbIds[position], imageView);
		return imageView;
	}

	private Integer[] mThumbIds = { 
			R.drawable.xx_subject_001, R.drawable.xx_subject_002,
			R.drawable.xx_subject_003, R.drawable.xx_subject_004,
			R.drawable.xx_subject_005, R.drawable.xx_subject_006,
			R.drawable.xx_subject_007, R.drawable.xx_subject_008,
			R.drawable.xx_subject_009, R.drawable.xx_subject_010,
			R.drawable.xx_subject_011, R.drawable.xx_subject_012,
			R.drawable.xx_subject_013, R.drawable.xx_subject_014,
			R.drawable.xx_subject_015, R.drawable.xx_subject_016,
			R.drawable.xx_subject_017, R.drawable.xx_subject_018,
			R.drawable.xx_subject_019, R.drawable.xx_subject_020,
			R.drawable.xx_subject_021, R.drawable.xx_subject_022,
			R.drawable.xx_subject_023, R.drawable.xx_subject_024,
			R.drawable.xx_subject_025, R.drawable.xx_subject_026,
			R.drawable.xx_subject_027, R.drawable.xx_subject_028,
			R.drawable.xx_subject_029, R.drawable.xx_subject_030,
			R.drawable.xx_subject_031, R.drawable.xx_subject_032,
			R.drawable.xx_subject_033, R.drawable.xx_subject_034,
			R.drawable.xx_subject_035, R.drawable.xx_subject_036,
			R.drawable.xx_subject_037, R.drawable.xx_subject_038,
			R.drawable.xx_subject_039, R.drawable.xx_subject_040,
			R.drawable.xx_subject_041, R.drawable.xx_subject_042,
			R.drawable.xx_subject_043, R.drawable.xx_subject_044,
			R.drawable.xx_subject_045, R.drawable.xx_subject_046,
			R.drawable.xx_subject_047, R.drawable.xx_subject_048,
			R.drawable.xx_subject_049, R.drawable.xx_subject_050,
			R.drawable.xx_subject_051, R.drawable.xx_subject_052,
			R.drawable.xx_subject_053, R.drawable.xx_subject_054,
			R.drawable.xx_subject_055, R.drawable.xx_subject_056,
			R.drawable.xx_subject_057, R.drawable.xx_subject_058,
			R.drawable.xx_subject_059, R.drawable.xx_subject_060,
			R.drawable.xx_subject_061, R.drawable.xx_subject_062,
			R.drawable.xx_subject_063, R.drawable.xx_subject_064,
			R.drawable.xx_subject_065, R.drawable.xx_subject_066,
			R.drawable.xx_subject_067, R.drawable.xx_subject_068,
			R.drawable.xx_subject_069, R.drawable.xx_subject_070

	};

	public int getIdAtPosition(int position) {
		
		return mThumbIds[position];
	}
	
}
