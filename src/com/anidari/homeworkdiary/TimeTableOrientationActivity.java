package com.anidari.homeworkdiary;

import com.anromus.homeworkdiary.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.Menu;

public class TimeTableOrientationActivity extends Activity {

	public static int LANDSCAPE_TIMETABLE = 1;
	public static int PORTRAIT_TIMETABLE = 2;

	public static String WEEK_TO_VIEW = "WEEK_TO_VIEW";
	public static long EMPTY_LONG = -1L;

	long weekToView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_table_orientation);

		weekToView = EMPTY_LONG;
		// activity is launched in onResume()
	}

	@Override
	public void onResume() {
		super.onResume();
		launchCorrectActivity();
	}

	private void launchCorrectActivity() {

		// if landscape, launch the traditional timetable layout, if portrait
		// then launch the list view
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// landscape
			launchLandscapeActivity();
		} else {
			// portrait
			launchPortraitActivity();
		}

	}

	private void launchLandscapeActivity() {
		Intent myIntent = new Intent(TimeTableOrientationActivity.this,
				TimeTableActivity.class);

		if (weekToView != EMPTY_LONG) {
			myIntent.putExtra(WEEK_TO_VIEW, weekToView);
		}

		TimeTableOrientationActivity.this.startActivityForResult(myIntent,
				LANDSCAPE_TIMETABLE);
	}

	private void launchPortraitActivity() {
		Intent myIntent = new Intent(TimeTableOrientationActivity.this,
				TimeTableListActivity.class);

		if (weekToView != EMPTY_LONG) {
			myIntent.putExtra(WEEK_TO_VIEW, weekToView);
		}

		TimeTableOrientationActivity.this.startActivityForResult(myIntent,
				PORTRAIT_TIMETABLE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// the activity finishes with result OK if the orientation has changed
		// result cancelled means back has been pressed so the activity needs to
		// end
		if (resultCode == Activity.RESULT_CANCELED) {
			// must have been cancelled
			finishActivity();
		}
		else if (data.hasExtra(WEEK_TO_VIEW)){
			// get the week to view
			weekToView = data.getLongExtra(WEEK_TO_VIEW, EMPTY_LONG);
		}
	}

	private void finishActivity() {
		this.setResult(Activity.RESULT_OK);
		this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.time_table_orientation, menu);
		return true;
	}

}
