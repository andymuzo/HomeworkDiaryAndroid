package com.anidari.homeworkdiary;

import java.util.ArrayList;
import java.util.Calendar;

import com.anromus.homeworkdiary.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class TimeTableListActivity extends ListActivity implements
		OnItemClickListener {

	ArrayList<TimetableEntry> timetable;
	DatabaseHandler db;
	TimeTableListArrayAdapter timetableAdapter;
	ListView timetableListView;
	Calendar weekToView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_table_list);

		db = new DatabaseHandler(this);

		weekToView = Calendar.getInstance();

		// finds the current week time in the bundle if available
		if (getIntent().hasExtra(
				TimeTableOrientationActivity.WEEK_TO_VIEW)) {
			weekToView.setTimeInMillis(getIntent().getExtras().getLong(
					TimeTableOrientationActivity.WEEK_TO_VIEW));
		}

		// sets the list to show the current week
		// will be adapted to show the week shown in TimeTableActivity
		timetable = db.getTimetableListForWeekFromCurrentTime(
				weekToView.getTimeInMillis(), this, true);

		// setup the list adapter
		timetableAdapter = new TimeTableListArrayAdapter(this,
				android.R.layout.simple_list_item_1, timetable);

		setListAdapter(timetableAdapter);

		timetableListView = getListView();
		timetableListView.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.time_table_list, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		TimetableEntry entry = timetable.get(position);
		editEntry(entry.getId(), entry.getSessionDate());
	}

	private void editEntry(int databaseID, long sessionDate) {
		Intent myIntent = new Intent(TimeTableListActivity.this,
				TimeTableNewActivity.class);

		myIntent.putExtra(TimeTableActivity.START_TYPE,
				TimeTableActivity.EDIT_TIME_TABLE_ENTRY);
		myIntent.putExtra(TimeTableActivity.DATABASE_ID, databaseID);
		myIntent.putExtra(TimeTableActivity.SESSION_DATE, sessionDate);

		TimeTableListActivity.this.startActivityForResult(myIntent,
				TimeTableActivity.EDIT_TIME_TABLE_ENTRY);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_previous_week:
			loadPreviousWeek();
			break;
		case R.id.action_next_week:
			loadNextWeek();
			break;
		case R.id.action_this_week:
			loadThisWeek();
			break;
		case R.id.action_new:
			createNewEntry();
			break;
		case R.id.action_settings:
			launchSettingsActivity();
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TimeTableActivity.ADD_NEW_TIME_TABLE_ENTRY
				|| requestCode == TimeTableActivity.EDIT_TIME_TABLE_ENTRY
				|| requestCode == SettingsActivity.EDIT_SETTINGS_ID) {
			if (resultCode == Activity.RESULT_OK) {
				refreshTimetable();
			}
		}
	}

	@Override
	public void onBackPressed() {
		// cancel the activity with a result code of CANCELLED.
		// This will cause the orientation handling activity to finish
		this.setResult(Activity.RESULT_CANCELED);
		this.finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Checks the orientation of the screen
		// only finishes if it changes to landscape
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Intent data = new Intent();
			data.putExtra(TimeTableOrientationActivity.WEEK_TO_VIEW,
					weekToView.getTimeInMillis());
			this.setResult(Activity.RESULT_OK, data);
			this.finish();
		}
	}

	/**
	 * reloads the week currently selected with the weekToView calendar object
	 */
	private void refreshTimetable() {
		timetable = db.getTimetableListForWeekFromCurrentTime(
				weekToView.getTimeInMillis(), this, true);

		timetableAdapter.updateAll(timetable, this);
	}

	public void loadPreviousWeek() {
		weekToView.add(Calendar.WEEK_OF_YEAR, -1);

		refreshTimetable();
	}

	public void loadNextWeek() {
		weekToView.add(Calendar.WEEK_OF_YEAR, 1);

		refreshTimetable();
	}

	public void loadThisWeek() {
		weekToView = Calendar.getInstance();

		refreshTimetable();
	}

	public void createNewEntry() {
		Intent myIntent = new Intent(TimeTableListActivity.this,
				TimeTableNewActivity.class);

		myIntent.putExtra("startType",
				TimeTableActivity.ADD_NEW_TIME_TABLE_ENTRY);

		TimeTableListActivity.this.startActivityForResult(myIntent,
				TimeTableActivity.ADD_NEW_TIME_TABLE_ENTRY);
	}
	
	/**
	 * allows for editing settings, careful to update on return if needed
	 */
	private void launchSettingsActivity() {
		Intent myIntent = new Intent(this,
				SettingsActivity.class);

		startActivityForResult(myIntent,
				SettingsActivity.EDIT_SETTINGS_ID);
	}
}
