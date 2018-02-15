package com.anidari.homeworkdiary;

import java.util.Calendar;

import com.anromus.homeworkdiary.R;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

public class SettingsActivity extends Activity implements OnClickListener {

	public final static int EDIT_SETTINGS_ID = 299; // arbitrary number, needs to be unique for launch codes across the app
	
	public final static String SETTING_MAX_START_TIME = "SETTING_MAX_START_TIME";
	public final static String SETTING_MIN_END_TIME = "SETTING_MIN_END_TIME";
	public final static String SETTING_DAYS = "SETTING_DAYS";
	public final static String PREF_NOTIFICATION_TIME = "PREF_NOTIFICATION_TIME";
	public final static String PREF_NOTIFICATION_ENABLED = "PREF_NOTIFICATION_ENABLED";
	public final static String PREF_NO_SUBJECT_ID = "PREF_NO_SUBJECT_ID";
	public final static String PREF_SHOW_SUBJECT = "PREF_SHOW_SUBJECT";
	public final static String PREF_SHOW_LOCATION = "PREF_SHOW_LOCATION";
	public final static String PREF_SHOW_TUTOR = "PREF_SHOW_TUTOR";
	public final static String PREF_SHOW_SUBJECT_PORTRAIT = "PREF_SHOW_SUBJECT_PORTRAIT";
	public final static String PREF_SHOW_LOCATION_PORTRAIT = "PREF_SHOW_LOCATION_PORTRAIT";
	public final static String PREF_SHOW_TUTOR_PORTRAIT = "PREF_SHOW_TUTOR_PORTRAIT";
	
	CheckBox notificationEnabled;
	SharedPreferences preferences;
	TableRow rowNotificationTime;

	TextView txtNotificationTime;
	long notificationTime;

	ArrayAdapter<String> spinnerAdapter;
	Spinner startTime;
	Spinner endTime;
	DaySelecterLayout daySelect;
	
	CheckBox showSubject;
	CheckBox showLocation;
	CheckBox showTutor;
	
	CheckBox showSubjectPortrait;
	CheckBox showLocationPortrait;
	CheckBox showTutorPortrait;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		setupFields();

		populateFields();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);

		return true;
	}

	/**
	 * tying all the objects to the view
	 */
	private void setupFields() {
		notificationEnabled = (CheckBox) findViewById(R.id.chk_settings_notify_enable);
		rowNotificationTime = (TableRow) findViewById(R.id.row_notification_time);
		rowNotificationTime.setOnClickListener(this);

		txtNotificationTime = (TextView) findViewById(R.id.txt_notification_time);

		spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.time_list));
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// set up the spinners
		startTime = (Spinner) findViewById(R.id.spn_set_start_time);
		startTime.setAdapter(spinnerAdapter);

		// set up the spinners
		endTime = (Spinner) findViewById(R.id.spn_set_end_time);
		endTime.setAdapter(spinnerAdapter);

		// set up the day selector
		daySelect = (DaySelecterLayout) findViewById(R.id.day_selecter);
		
		showSubject = (CheckBox) findViewById(R.id.chk_timetable_show_subject);
		showLocation = (CheckBox) findViewById(R.id.chk_timetable_show_location);
		showTutor = (CheckBox) findViewById(R.id.chk_timetable_show_tutor);
		
		showSubjectPortrait = (CheckBox) findViewById(R.id.chk_portrait_timetable_show_subject);
		showLocationPortrait = (CheckBox) findViewById(R.id.chk_portrait_timetable_show_location);
		showTutorPortrait = (CheckBox) findViewById(R.id.chk_portrait_timetable_show_tutor);
	}

	/**
	 * populates the view from the settings
	 */
	private void populateFields() {
		// first get the state of all current settings
		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		notificationEnabled.setChecked(preferences.getBoolean(PREF_NOTIFICATION_ENABLED, true));
		
		notificationTime = preferences.getLong(PREF_NOTIFICATION_TIME, 18L * 60L * 60L * 1000L);
		setNotificationTimeView(notificationTime);
		
		startTime.setSelection(preferences.getInt(SETTING_MAX_START_TIME, 9));
		endTime.setSelection(preferences.getInt(SETTING_MIN_END_TIME, 17));
		
		daySelect.setSingleDayOnly(false);
		daySelect.setDays(preferences.getString(SETTING_DAYS, "nyyyyyn"));
		
		showSubject.setChecked(preferences.getBoolean(PREF_SHOW_SUBJECT, true));
		showLocation.setChecked(preferences.getBoolean(PREF_SHOW_LOCATION, true));
		showTutor.setChecked(preferences.getBoolean(PREF_SHOW_TUTOR, true));
		
		showSubjectPortrait.setChecked(preferences.getBoolean(PREF_SHOW_SUBJECT_PORTRAIT, true));
		showLocationPortrait.setChecked(preferences.getBoolean(PREF_SHOW_LOCATION_PORTRAIT, true));
		showTutorPortrait.setChecked(preferences.getBoolean(PREF_SHOW_TUTOR_PORTRAIT, true));
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.row_notification_time) {
			createTimePickerDialog(notificationTime);
		}
	}

	private void createTimePickerDialog(long initialTime) {
		// create a time picker dialog and assign the value to the
		// correct text view
		final Dialog dlg = new Dialog(this);
		dlg.setContentView(R.layout.dlg_set_time);
		dlg.setCancelable(true);
		dlg.getWindow().setBackgroundDrawableResource(
				R.drawable.gradient_dialog_bg);

		TextView instructions = (TextView) dlg
				.findViewById(R.id.txt_set_time_instructions);

		dlg.setTitle(getResources().getString(R.string.pick_time_title));
		instructions.setText(R.string.dlg_pick_time);

		// create a calendar to get the initial values into the time picker.
		// on pressing the set button the reverse process occurs and the
		// time will be returned as a long in milliseconds
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(initialTime);

		final TimePicker tp = (TimePicker) dlg.findViewById(R.id.tpPickTime);
		tp.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
		tp.setCurrentMinute(cal.get(Calendar.MINUTE));

		Button set = (Button) dlg.findViewById(R.id.btn_confirm);

		set.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, tp.getCurrentHour());
				cal.set(Calendar.MINUTE, tp.getCurrentMinute());
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				long timeSet = cal.getTimeInMillis();
				
				setNotificationTime(timeSet);
			}
		});

		Button cancel = (Button) dlg.findViewById(R.id.btn_cancel);

		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
		
		dlg.show();
	}
	
	/**
	 * sets the time both as the long that will be stored in settings and updates the view
	 * @param timeSet
	 */
	private void setNotificationTime(long timeSet) {
		setNotificationTimeView(timeSet);
		notificationTime = timeSet;
	}
	
	/**
	 * puts a human-readable time in the text view
	 * @param notificationTime
	 */
	private void setNotificationTimeView(long notificationTime) {
		txtNotificationTime.setText(HumanReadableDate.getTimeString(notificationTime));
	}
	
	@Override
	public void onBackPressed() {
		updateSettings();
		setResult(RESULT_OK);
		finish();
	}
	
	public void updateSettings() {
		
		SharedPreferences.Editor editor = preferences.edit();

		// homework settings
		editor.putLong(PREF_NOTIFICATION_TIME, notificationTime);
		editor.putBoolean(PREF_NOTIFICATION_ENABLED, notificationEnabled.isChecked());
		
		// timetable settings
		editor.putInt(SETTING_MAX_START_TIME, startTime.getSelectedItemPosition());
		editor.putInt(SETTING_MIN_END_TIME, endTime.getSelectedItemPosition());
		editor.putString(SETTING_DAYS, daySelect.getDaysAsString());
		
		editor.putBoolean(PREF_SHOW_SUBJECT, showSubject.isChecked());
		editor.putBoolean(PREF_SHOW_LOCATION, showLocation.isChecked());
		editor.putBoolean(PREF_SHOW_TUTOR, showTutor.isChecked());
		
		editor.putBoolean(PREF_SHOW_SUBJECT_PORTRAIT, showSubjectPortrait.isChecked());
		editor.putBoolean(PREF_SHOW_LOCATION_PORTRAIT, showLocationPortrait.isChecked());
		editor.putBoolean(PREF_SHOW_TUTOR_PORTRAIT, showTutorPortrait.isChecked());

		editor.commit();

	}
}
