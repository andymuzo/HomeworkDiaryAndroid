package com.anidari.homeworkdiary;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AlarmHandler {

	private AlarmManager alarmManager;
	private PendingIntent notifyIntent;

	public AlarmHandler(Context context) {
		alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
	}

	public void setAlarm(Context context) {

		// the time saved in the settings also has a date with it as a calendar
		// time in millis
		// to get at the date properly some settings are taken from the current
		// time
		Calendar current = Calendar.getInstance();
		Calendar alarmTime = Calendar.getInstance();

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		long alarmTimeLong = preferences.getLong(
				SettingsActivity.PREF_NOTIFICATION_TIME, 18L * 60L * 60L * 1000L);

		alarmTime.setTimeInMillis(alarmTimeLong);
		alarmTime.set(Calendar.YEAR, current.get(Calendar.YEAR));
		alarmTime.set(Calendar.MONTH, current.get(Calendar.MONTH));
		alarmTime
				.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));

		// check to see if we are before or after today's alarm
		if (alarmTime.getTimeInMillis() <= current.getTimeInMillis()) {
			// after the time, so add on 1 day to the alarm time
			alarmTime.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		Intent myIntent = new Intent(context, NotificationService.class);

		notifyIntent = PendingIntent.getService(context, 0, myIntent, 0);

		alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(),
				notifyIntent);
	}

	public void unsetAlarm(Context context) {
		Intent myIntent = new Intent(context, NotificationService.class);
		// recreate it here before calling cancel
		notifyIntent = PendingIntent.getService(context, 0, myIntent, 0);

		alarmManager.cancel(notifyIntent);
	}

}