package com.anidari.homeworkdiary;

import java.util.ArrayList;
import java.util.Calendar;

import com.anromus.homeworkdiary.R;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TimeTableActivity extends Activity implements OnClickListener {

	public static int EDIT_TIME_TABLE_AS_LIST = 100;
	public static int ADD_NEW_TIME_TABLE_ENTRY = 101;
	public static int EDIT_TIME_TABLE_ENTRY = 102;
	public static String START_TYPE = "startType";
	public static String DATABASE_ID = "databaseID";
	public static String SESSION_DATE = "sessionDate";
	// TODO: these need deleting:
	public static String SETTING_MAX_START_TIME = "SETTING_MAX_START_TIME";
	public static String SETTING_MIN_END_TIME = "SETTING_MIN_END_TIME";
	public static String SETTING_DAYS = "SETTING_DAYS";

	DatabaseHandler db;
	ArrayList<TimetableEntry> timetableList;
	Calendar weekToView;

	// width of the table, set to the width of the screen
	int tableWidth;

	ScrollView scrollH;
	TableLayout table;

	TableRow tblInvis; // might width to 0 and controlling via weight causes the
						// cell to stretch downwards

	TableRow rowTitle;
	TableRow rowUnderline;
	TableRow[] rows;

	boolean[] dayNeeded;
	boolean satNeeded; // flag for including Saturday
	boolean sunNeeded; // flag for including Sunday

	// TODO: delete the following when done:
	TableRow tblTitle;

	View tblUnderline;
	View tblUnderline2;

	TableRow tblRow2;
	TableRow tblRow3;
	TableRow tblRow4;
	TableRow tblRow5;
	TableRow tblRow6;
	TableRow tblRow7;
	TableRow tblRow8;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		db = new DatabaseHandler(this);
		weekToView = Calendar.getInstance();

		// finds the current week time in the bundle if available
		if (getIntent().hasExtra(TimeTableOrientationActivity.WEEK_TO_VIEW)) {
			weekToView.setTimeInMillis(getIntent().getExtras().getLong(
					TimeTableOrientationActivity.WEEK_TO_VIEW));
		}

		// sets the list to show the current week
		timetableList = db.getTimetableListForWeekFromCurrentTime(
				weekToView.getTimeInMillis(), this, false);

		setupTable();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.time_table, menu);
		return true;
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
		if (requestCode == ADD_NEW_TIME_TABLE_ENTRY
				|| requestCode == EDIT_TIME_TABLE_ENTRY 
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
		// only finishes if it changes to portrait
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			Intent data = new Intent();
			data.putExtra(TimeTableOrientationActivity.WEEK_TO_VIEW,
					weekToView.getTimeInMillis());
			this.setResult(Activity.RESULT_OK, data);
			this.finish();
		}
	}

	private void refreshTimetable() {
		// sets the list to show the current week
		timetableList = db.getTimetableListForWeekFromCurrentTime(
				weekToView.getTimeInMillis(), this, false);

		setupTable();
	}

	private void loadPreviousWeek() {
		weekToView.add(Calendar.WEEK_OF_YEAR, -1);

		// sets the list to show the current week
		timetableList = db.getTimetableListForWeekFromCurrentTime(
				weekToView.getTimeInMillis(), this, false);

		setupTable();
	}

	private void loadNextWeek() {
		weekToView.add(Calendar.WEEK_OF_YEAR, 1);

		// sets the list to show the current week
		timetableList = db.getTimetableListForWeekFromCurrentTime(
				weekToView.getTimeInMillis(), this, false);

		setupTable();
	}

	private void loadThisWeek() {
		weekToView = Calendar.getInstance();

		// sets the list to show the current week
		timetableList = db.getTimetableListForWeekFromCurrentTime(
				weekToView.getTimeInMillis(), this, false);

		setupTable();
	}

	/**
	 * launches new activity for creating a new entry to add to the timetable
	 */
	private void createNewEntry() {
		Intent myIntent = new Intent(TimeTableActivity.this,
				TimeTableNewActivity.class);

		myIntent.putExtra("startType", ADD_NEW_TIME_TABLE_ENTRY);

		TimeTableActivity.this.startActivityForResult(myIntent,
				ADD_NEW_TIME_TABLE_ENTRY);
	}

	/**
	 * launches new activity for editing the given entry
	 * 
	 * @param databaseID
	 */
	private void editEntry(int databaseID, long sessionDate) {
		Intent myIntent = new Intent(TimeTableActivity.this,
				TimeTableNewActivity.class);

		myIntent.putExtra(START_TYPE, EDIT_TIME_TABLE_ENTRY);
		myIntent.putExtra(DATABASE_ID, databaseID);
		myIntent.putExtra(SESSION_DATE, sessionDate);

		TimeTableActivity.this.startActivityForResult(myIntent,
				EDIT_TIME_TABLE_ENTRY);
	}

	public void launchListTT() {
		Intent myIntent = new Intent(TimeTableActivity.this,
				TimeTableListActivity.class);

		TimeTableActivity.this.startActivityForResult(myIntent,
				EDIT_TIME_TABLE_AS_LIST);
	}

	/**
	 * the main sequence of methods for populating the table
	 */
	private void setupTable() {
		initialiseFlags();
		createScrollView();
		createTable();
		createRows();
		populateRowsFromArray();
		addRowsToTable();
		setContentView(scrollH);
	}

	private void initialiseFlags() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		String activeDays = preferences.getString(SETTING_DAYS, "nyyyyyn");

		dayNeeded = new boolean[7];

		// stored as "ynnyyny" for SMTWTFS
		for (int i = 0; i < activeDays.length(); i++) {
			if (activeDays.charAt(i) == 'y') {
				// set positive
				dayNeeded[i] = true;
			} else {
				// set negative
				dayNeeded[i] = false;
			}
		}
	}

	private void createScrollView() {
		// create a scroll view
		scrollH = new ScrollView(this);
		ViewGroup.LayoutParams scrollHParams = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		scrollH.setLayoutParams(scrollHParams);
		scrollH.setBackground(getResources().getDrawable(R.drawable.slate_bg));

		scrollH.setFillViewport(true);
	}

	private void createTable() {
		// create a table in the scroll view
		table = new TableLayout(this);
		table.setShrinkAllColumns(false);
		table.setStretchAllColumns(true);

		table.setLayoutParams(new ScrollView.LayoutParams(
				ScrollView.LayoutParams.MATCH_PARENT,
				ScrollView.LayoutParams.WRAP_CONTENT));
	}

	private void createRows() {
		rows = new TableRow[7];

		for (int i = 0; i < 7; i++) {
			rows[i] = new TableRow(this);
		}
	}

	private TextView getWeekLabel() {
		
		TextView text = new TextView(this);
		text.setText(this.getResources().getString(R.string.week) + " "
				+ HumanReadableDate.getLongDateString(db.getTimeAtStartOfWeek(weekToView.getTimeInMillis())));
		text.setBackgroundColor(getResources().getColor(R.color.darken_background));
		text.setTextAppearance(this, R.style.timeTableTitleText);
		text.setGravity(Gravity.CENTER);
		text.setPadding(16, 8, 16, 8);
		
		return text;
	}

	private void addRowsToTable() {
		TableLayout.LayoutParams params = new TableLayout.LayoutParams(
				TableLayout.LayoutParams.MATCH_PARENT,
				TableLayout.LayoutParams.WRAP_CONTENT);

		table.addView(getWeekLabel(), params);
		
		// add the title row
		table.addView(rowTitle, params);

		table.addView(getUnderlineRow());

		for (int i = 0; i < 7; i++) {
			// check to see if Saturday or Sunday rows are needed
			if (dayNeeded[i]) {
				table.addView(rows[i], params);
				table.addView(getUnderlineRow());
			}
		}
		scrollH.addView(table);
	}

	private void populateRowsFromArray() {
		// work out what hours it covers

		// initialise these to preference settings of minimum time to display
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		int earliestStart = preferences.getInt(SETTING_MAX_START_TIME, 9);
		int latestFinish = preferences.getInt(SETTING_MIN_END_TIME, 17);
		for (TimetableEntry entry : timetableList) {
			earliestStart = Math.min(earliestStart, entry.getStartHour());
			latestFinish = Math.max(latestFinish, entry.getEndHour());
		}

		// create the title hours row
		rowTitle = new TableRow(this);
		createTitleRow(rowTitle, earliestStart, latestFinish);

		int earliestStartMins = earliestStart * 60;
		// populate the 7 days
		// this gives the last entry time for each day
		int[] time = { earliestStartMins, earliestStartMins, earliestStartMins,
				earliestStartMins, earliestStartMins, earliestStartMins,
				earliestStartMins };

		// add in the labels for the days (mon, tue etc.)
		addDayLabels();

		boolean showSubject = preferences.getBoolean(SettingsActivity.PREF_SHOW_SUBJECT, true);
		boolean showLocation = preferences.getBoolean(SettingsActivity.PREF_SHOW_LOCATION, true);
		boolean showTutor = preferences.getBoolean(SettingsActivity.PREF_SHOW_TUTOR, true);
		
		// the list is in order of start time so it is easy to iterate through
		// the days
		for (TimetableEntry entry : timetableList) {

			int entryDay = entry.getDay();
			int startMinute = entry.getStartMinute();
			int endMinute = entry.getEndMinute();

			// set the active days flags
			dayNeeded[entryDay] = true;

			// see if empty space is needed
			if (startMinute != time[entryDay]) {
				// create empty space
				TextView spacer = new TextView(this);
				spacer.setLayoutParams(new TableRow.LayoutParams(0,
						TableRow.LayoutParams.MATCH_PARENT,
						((float) startMinute - time[entryDay])));
				spacer.setBackgroundColor(getResources().getColor(R.color.invisible));
				rows[entryDay].addView(spacer);

				time[entryDay] = startMinute;
			}

			// create the entry with the required weight
			TableRow.LayoutParams params = new TableRow.LayoutParams(0,
					TableRow.LayoutParams.MATCH_PARENT,
					((float) endMinute - startMinute));

			TimetableEntryLayout entryLayout = new TimetableEntryLayout(this);
			// gives the timetable entry to the layout
			entryLayout.setChildren(entry);
			// sets id for use by on click
			entryLayout.setId(entry.getId());
			entryLayout.setSessionDate(entry.getSessionDate());
			entryLayout.setOnClickListener(this);
			// shows just the needed fields
			entryLayout.setVisibleFields(showSubject, showLocation, showTutor);
			// set background colour
			entryLayout.setBackgroundResource(ColourPickerLayout.getColourId(entry.getTimeTableColour(), ColourPickerLayout.SMALL_ROUNDED));
			// adding to the row
			rows[entryDay].addView(entryLayout, params);

			time[entryDay] = endMinute;

		}

		// fill in the last bit of empty space for each row
		for (int i = 0; i < 7; i++) {
			if (time[i] != latestFinish * 60) {
				// create a blank bit at the end, using a plain entry so as to 
				// provide the correct height for empty fields

				TimetableEntryLayout spacer = new TimetableEntryLayout(this);
				spacer.setVisibleFields(showSubject, showLocation, showTutor);
				spacer.blankIt();
				spacer.setLayoutParams(new TableRow.LayoutParams(0,
						TableRow.LayoutParams.MATCH_PARENT,
						((float) (latestFinish * 60) - time[i])));
				spacer.setBackgroundColor(getResources().getColor(R.color.invisible));

				rows[i].addView(spacer);
			}
		}

	}

	private void addDayLabels() {
		// TODO: change how the day labels work, need their own custom view object
		TableRow.LayoutParams params = new TableRow.LayoutParams(0,
				TableRow.LayoutParams.MATCH_PARENT, 40f);
		for (int i = 0; i < 7; i++) {
			TextView label = new TextView(this);
			label.setLayoutParams(params);
			label.setBackground(getResources().getDrawable(R.drawable.gradient_tt_day_01));

			label.setText(this.getResources().getStringArray(R.array.tt_days)[i]);
			label.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
			label.setPadding(8, 0, 8, 0);
			label.setTextAppearance(this, R.style.dayLabelText);
			rows[i].addView(label);
		}
	}

	private void createTitleRow(TableRow row, int start, int finish) {

		// for choosing the colour
		boolean flipFlop = true;

		// create a blank space
		TextView spacer = new TextView(this);
		TableRow.LayoutParams spacerParams = new TableRow.LayoutParams(0,
				TableRow.LayoutParams.WRAP_CONTENT, 40f);
		spacer.setBackgroundColor(getResources().getColor(R.color.invisible));

		row.addView(spacer, spacerParams);

		// iterate over the times
		for (int i = 0; i < finish - start; i++) {
			TableRow.LayoutParams params = new TableRow.LayoutParams(0,
					TableRow.LayoutParams.MATCH_PARENT, 60f);
			String displayText;

			// display the correct hour
			if ((start + i) == 12) {
				displayText = "12";
			} else {
				displayText = "" + ((start + i) % 12);
			}

			// add the appropriate am or pm
			if ((start + i) < 12) {
				displayText = displayText.concat("am");
			} else {
				displayText = displayText.concat("pm");
			}

			TextView t = new TextView(this);
			t.setText(displayText);
			t.setTextColor(getResources().getColor(R.color.extremely_light_red));
			t.setGravity(Gravity.CENTER);

			// colour it
			if (flipFlop) {
				// TODO: set these to two different colours
				t.setBackground(getResources().getDrawable(R.drawable.gradient_tt_times)); // have this reference colour
													// variable
			} else {
				t.setBackground(getResources().getDrawable(R.drawable.gradient_tt_times_two)); // have this reference colour
													// variable
			}
			flipFlop = !flipFlop;

			row.addView(t, params);
		}

	}

	/**
	 * makes an underline filling the given table row
	 * 
	 * @param row
	 */
	public View getUnderlineRow() {
		TableRow.LayoutParams params = new TableRow.LayoutParams(
				TableRow.LayoutParams.MATCH_PARENT, 2, 60.0f);
		View underline = new View(this);
		underline.setLayoutParams(params);
		underline.setBackgroundColor(getResources().getColor(R.color.mid_orange));

		return underline;
	}

	@Override
	public void onClick(View v) {

		// this will almost certainly break if the onClick is called by any
		// other type of view. If this needs to be done a check should be done
		// first to determine the type of view and act accordingly.
		TimetableEntryLayout entryLayout = (TimetableEntryLayout) this
				.findViewById(v.getId());

		for (TimetableEntry entry : timetableList) {

			if ((entry.getId() == v.getId())
					&& (entry.getSessionDate() == entryLayout.getSessionDate())) {
				// edit the entry
				editEntry(entry.getId(), entry.getSessionDate());
				break;
			}
		}
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
