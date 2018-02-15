package com.anidari.homeworkdiary;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import com.anromus.homeworkdiary.R;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MainMenuActivity extends ListActivity implements
		OnItemClickListener {

	private static final String HAS_NOT_BEEN_SETUP = "HAS_NOT_BEEN_SETUP";

	private DatabaseHandler db;

	private ArrayList<MainMenuItem> items;

	private ListView mainMenuListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		db = new DatabaseHandler(this);

		firstTimeSetup();

		updateNotifications();
	}

	@Override
	protected void onResume() {
		// calling this in onResume() so that it always refreshed the
		// information
		super.onResume();
		setup();
	}

	private void setup() {
		items = db.getMainMenuArrayList(this);

		setListAdapter(new MainMenuArrayAdapter(this,
				android.R.layout.simple_list_item_1, items));

		mainMenuListView = getListView();
		mainMenuListView.setOnItemClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// uses the item id to decide on action to take
		switch (item.getItemId()) {

		case R.id.action_populate_test:
			populateTestEntries();
			break;
		case R.id.action_delete_all_test:
			resetAllData();
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// sets the correct activity going
		switch (items.get(position).getType()) {
		case HOMEWORK:
			launchHomeworkActivity();
			break;
		case SUBJECT:
			launchSubjectActivity();
			break;
		case TIMETABLE:
			launchTimeTableActivity();
			break;
		default:
			break;
		}
	}

	private void launchHomeworkActivity() {
		Intent myIntent = new Intent(MainMenuActivity.this,
				HomeworkActivity.class);

		MainMenuActivity.this.startActivity(myIntent);
	}

	private void launchSubjectActivity() {
		Intent myIntent = new Intent(MainMenuActivity.this,
				SubjectEditActivity.class);

		MainMenuActivity.this.startActivity(myIntent);
	}

	private void launchTimeTableActivity() {
		Intent myIntent = new Intent(MainMenuActivity.this,
				TimeTableOrientationActivity.class);

		MainMenuActivity.this.startActivity(myIntent);
	}

	private void updateNotifications() {
		// stops the notification service if its still running
		stopService(new Intent(this, NotificationService.class));
		
		// getting the value for setting alarm
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		AlarmHandler ah = new AlarmHandler(this);

		if (preferences.getBoolean(SettingsActivity.PREF_NOTIFICATION_ENABLED,
				true)) {
			// sets the alarm handler for notifications
			ah.setAlarm(this);
		} else {
			ah.unsetAlarm(this);
		}
	}

	// setup
	// **********************************************************************************************

	/**
	 * called to set up the app the first time it is loaded
	 */
	private void firstTimeSetup() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		if (preferences.getBoolean(HAS_NOT_BEEN_SETUP, true)) {
			setupPreferences();
			// adds the undeleteable default subject name and saves its database
			// id
			// to the settings
			db.createDefaultSubject(this);
		}
	}

	/**
	 * setup of the preferences
	 */
	private void setupPreferences() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		SharedPreferences.Editor editor = preferences.edit();
		if (!preferences.contains(SettingsActivity.PREF_NOTIFICATION_TIME)) {

			// setting a default value of 6pm
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 18);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			editor.putLong(SettingsActivity.PREF_NOTIFICATION_TIME,
					cal.getTimeInMillis());
		}

		if (!preferences.contains(SettingsActivity.PREF_NOTIFICATION_ENABLED)) {
			editor.putBoolean(SettingsActivity.PREF_NOTIFICATION_ENABLED, true);
		}

		editor.putBoolean(HAS_NOT_BEEN_SETUP, false);
		editor.commit();
	}
	
	/**
	 * allows for editing settings, careful to update on return if needed
	 */
	private void launchSettingsActivity() {
		Intent myIntent = new Intent(this,
				SettingsActivity.class);

		this.startActivityForResult(myIntent,
				SettingsActivity.EDIT_SETTINGS_ID);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SettingsActivity.EDIT_SETTINGS_ID) {
			// updates the notifications, specifically for if the time or enabled has been changed
			updateNotifications();
		}
	}

	// Debugging methods
	// *****************************************************************************************
	private void populateTestEntries() {
		// TODO: this is to be used for testing purposes

		firstTimeSetup();
		
		if (db.isSubjectEmpty()) {
			// if (true) {
			// create some subject entries for testing
			populateTestSubjects();
		}
		
		if (db.isHomeworkEmpty()) {
			// if (true) {
			// create some homework entries for testing
			populateTestHomework();
		}

		if (db.isTimetableEmpty()) {
			// create some timetable entries for testing
			populateTestTimetable();
		}

		if (db.isToDoEmpty()) {
			// create some timetable entries for testing
			populateTestTodo();
		}
		
	}

	private void populateTestHomework() {
		// homeworkEntry constructor: int id, long dueDate, String
		// homeworkTitle,
		// int subjectID, boolean isReminderSet, boolean isCompleted,
		// String finalGrade
		// note: when creating a new entry id is ignored

		Calendar cal = Calendar.getInstance(TimeZone.getDefault());

		db.addHomework(new HomeworkEntry(0, cal.getTimeInMillis(),
				"Make Potions", 2, true, false, null));
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		db.addHomework(new HomeworkEntry(0, cal.getTimeInMillis(), "Do Maths",
				3, true, false, null));
		cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
		db.addHomework(new HomeworkEntry(0, cal.getTimeInMillis(), "Eat Food",
				4, true, false, null));
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		db.addHomework(new HomeworkEntry(0, cal.getTimeInMillis(),
				"Save the World", 5, true, true, null));
		cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
		db.addHomework(new HomeworkEntry(0, cal.getTimeInMillis(),
				"Hug a Tree", 6, true, false, null));
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		db.addHomework(new HomeworkEntry(0, cal.getTimeInMillis(),
				"Tell it Like it is", 7, true, true, "Pass"));
		cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
		db.addHomework(new HomeworkEntry(0, cal.getTimeInMillis(),
				"Appreciate Poetry", 8, true, false, null));
		cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		db.addHomework(new HomeworkEntry(0, cal.getTimeInMillis(),
				"Make a Jetpack", 9, true, false, null));
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		db.addHomework(new HomeworkEntry(0, cal.getTimeInMillis(),
				"Do More Maths", 3, true, true, "Distinction"));
		cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
		db.addHomework(new HomeworkEntry(0, cal.getTimeInMillis(),
				"Wash Behind your Ears", 10, true, false, null));
		cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		db.addHomework(new HomeworkEntry(0, cal.getTimeInMillis(),
				"Play Videogames", 11, true, true, "A+"));
		cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
		db.addHomework(new HomeworkEntry(0, cal.getTimeInMillis(),
				"Go to Sleep", 12, true, false, null));
	}

	private void populateTestSubjects() {

		// makes the blank subject
		db.createDefaultSubject(this);

		// adds the test subjects
		db.addSubject(new SubjectEntry(0, "Chemistry", R.drawable.xx_subject_001));
		db.addSubject(new SubjectEntry(0, "Mathematics", R.drawable.xx_subject_002));
		db.addSubject(new SubjectEntry(0, "Catering", R.drawable.xx_subject_003));
		db.addSubject(new SubjectEntry(0, "Art", R.drawable.xx_subject_004));
		db.addSubject(new SubjectEntry(0, "Biology", R.drawable.xx_subject_005));
		db.addSubject(new SubjectEntry(0, "French", R.drawable.xx_subject_006));
		db.addSubject(new SubjectEntry(0, "English", R.drawable.xx_subject_007));
		db.addSubject(new SubjectEntry(0, "Design Technology",
				R.drawable.xx_subject_008));
		db.addSubject(new SubjectEntry(0, "Sociology", R.drawable.xx_subject_009));
		db.addSubject(new SubjectEntry(0, "Computer Science",
				R.drawable.xx_subject_010));
		db.addSubject(new SubjectEntry(0, "Psychology", R.drawable.xx_subject_011));
	}

	private void populateTestTodo() {
		db.addToDoEntry(new ToDoEntry(0, 1, 0, "Find a bottle", true));
		db.addToDoEntry(new ToDoEntry(0, 1, 1, "Get some shampoo", false));
		db.addToDoEntry(new ToDoEntry(0, 1, 2, "Add coke", false));
		db.addToDoEntry(new ToDoEntry(0, 1, 3, "Heat for 30 mins", false));
		db.addToDoEntry(new ToDoEntry(
				0,
				1,
				4,
				"Write a book about feeding this marvelous medicine to a horrible person and hope for no jail sentence.",
				false));
	}

	private void populateTestTimetable() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 10);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.YEAR, 1);
		long endDate = cal.getTimeInMillis();
		cal.add(Calendar.YEAR, -1);
		long startTime = cal.getTimeInMillis();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		long endTime = cal.getTimeInMillis();
		int colour = 0;
		
		db.addNewTimetableEvent(new TimetableEntry(0, "Workshop",
				"Mr Anderson", "C55", "Tutor is sometimes late", 1, 1, null,
				startTime, endTime, startTime, endDate, 0L, "ONE_OFF",
				"nyynnyn", colour++));
		cal.add(Calendar.DAY_OF_WEEK, 1);
		startTime = cal.getTimeInMillis();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		endTime = cal.getTimeInMillis();
		db.addNewTimetableEvent(new TimetableEntry(0, "Tutorial", "Ms Bertram",
				"A1", null, 2, 1, null, startTime, endTime, startTime, endDate,
				0L, "LAST_OF_MONTH", "nnnnynn", colour++));
		cal.add(Calendar.HOUR_OF_DAY, -1);
		startTime = cal.getTimeInMillis();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		endTime = cal.getTimeInMillis();
		db.addNewTimetableEvent(new TimetableEntry(0, null, "Mr Caladonia",
				"T22", "Key is in office", 3, 1, null, startTime, endTime,
				startTime, endDate, 0L, "FORTNIGHTLY", "nnnyyyn", colour++));
		cal.add(Calendar.DAY_OF_WEEK, 1);
		startTime = cal.getTimeInMillis();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		endTime = cal.getTimeInMillis();
		db.addNewTimetableEvent(new TimetableEntry(0, "Seminar", "Mdm Deus",
				"C32", "Bring a pen and paper", 4, 1, null, startTime, endTime,
				startTime, endDate, 0L, "ONE_OFF", "ynnnnnn", colour++));
		cal.add(Calendar.DAY_OF_WEEK, 1);
		startTime = cal.getTimeInMillis();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		endTime = cal.getTimeInMillis();
		db.addNewTimetableEvent(new TimetableEntry(0, null, "Mr Edgerton",
				"C20", "Laptop", 5, 1, null, startTime, endTime, startTime,
				endDate, 0L, "SECOND_OF_MONTH", "nnnnynn", colour++));
		cal.add(Calendar.DAY_OF_WEEK, 1);
		startTime = cal.getTimeInMillis();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		endTime = cal.getTimeInMillis();
		db.addNewTimetableEvent(new TimetableEntry(0, "Video Conference",
				"Miss Forte", "T10", null, 6, 1, null, startTime, endTime,
				startTime, endDate, 0L, "WEEKLY", "nyynnyn", colour++));
		cal.add(Calendar.DAY_OF_WEEK, 1);
		startTime = cal.getTimeInMillis();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		endTime = cal.getTimeInMillis();
		db.addNewTimetableEvent(new TimetableEntry(0, "Workshop", "Mr Gunther",
				"C11", "Tutor is sometimes late", 7, 1, null, startTime,
				endTime, startTime, endDate, 0L, "WEEKLY", "ynnynnn", colour++));
	}

	/**
	 * WARNING:
	 * deletes everything as if the app was installed for the first time
	 */
	private void resetAllData() {
		db.deleteDatabase(this);
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
	}
	
}
