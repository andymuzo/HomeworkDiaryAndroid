package com.anidari.homeworkdiary;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class HumanReadableDate {

	final static String noDateSetText = "No date set";

	/*
	 * simple static method that takes a date in long and returns a nicely
	 * readable by human date as a string
	 * "Day, Xth of Month"
	 */
	public static String getDateString(long dateMs) {

		String dueDate;

		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.setTimeInMillis(dateMs);
		if (dateMs == 0L) {
			dueDate = noDateSetText;
		} else {
			dueDate = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG,
					Locale.getDefault())
					+ ", "
					+ cal.get(Calendar.DATE)
					+ AddTh.getTh(cal.get(Calendar.DATE))
					+ " of "
					+ cal.getDisplayName(Calendar.MONTH, Calendar.LONG,
							Locale.getDefault());
		}
		return dueDate;
	}
	

	/*
	 * simple static method that takes a date in long and returns a long nicely
	 * readable by human date as a string
	 */
	public static String getLongDateString(long dateMs) {

		String dueDate;

		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.setTimeInMillis(dateMs);

		dueDate = cal.get(Calendar.WEEK_OF_YEAR)
				+ ": "
				+ cal.get(Calendar.DATE)
				+ AddTh.getTh(cal.get(Calendar.DATE))
				+ " of "
				+ cal.getDisplayName(Calendar.MONTH, Calendar.LONG,
						Locale.getDefault())
				+ " - ";
		
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		
		dueDate = dueDate + cal.get(Calendar.DATE)
				+ AddTh.getTh(cal.get(Calendar.DATE))
				+ " of "
				+ cal.getDisplayName(Calendar.MONTH, Calendar.LONG,
						Locale.getDefault())  + ", " + cal.get(Calendar.YEAR);

		return dueDate;
	}

	public static String getTimeString(long timeMs) {

		String time;

		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.setTimeInMillis(timeMs);
		time = new String(cal.get(Calendar.HOUR_OF_DAY) + ":");

		if (cal.get(Calendar.MINUTE) < 10) {
			time = time.concat("0");
		}

		time = time.concat("" + cal.get(Calendar.MINUTE));

		return time;
	}

	public static String getYearAsString(long timeMs) {
		String time;

		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.setTimeInMillis(timeMs);
		time = new String("" + cal.get(Calendar.YEAR));

		return time;
	}

	/**
	 * shows from and to day, date month
	 * @param startDateMs
	 * @param startTimeMs
	 * @param endTimeMs
	 * @return
	 */
	public static String getTimetableDateAsString(long startDateMs,
			long startTimeMs, long endTimeMs) {
		String time;

		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.setTimeInMillis(startDateMs);
		time = new String(cal.getDisplayName(Calendar.DAY_OF_WEEK,
				Calendar.SHORT, Locale.US)
				+ " "
				+ cal.get(Calendar.DATE)
				+ AddTh.getTh(cal.get(Calendar.DATE))
				+ " "
				+ cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)
				+ ", "
				+ getTimeString(startTimeMs)
				+ " - "
				+ getTimeString(endTimeMs));

		return time;
	}

	public static String getTimetableTimeAsString(long startTimeMs,
			long endTimeMs) {
		String time;

		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.setTimeInMillis(startTimeMs);
		time = new String(getTimeString(startTimeMs) + "-"
				+ getTimeString(endTimeMs));

		return time;
	}

	public static String getWeekDate(long sessionStart) {

		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.setTimeInMillis(sessionStart);
		return cal.get(Calendar.WEEK_OF_YEAR) + " " + cal.get(Calendar.DATE)
				+ "/" + (cal.get(Calendar.MONTH) + 1) + "/"
				+ cal.get(Calendar.YEAR);
	}
	
	public static String getDateTimeTableList(long startDateMs) {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.setTimeInMillis(startDateMs);
		String time = new String(cal.get(Calendar.WEEK_OF_YEAR)
				+ ": " + cal.get(Calendar.DATE)
				+ AddTh.getTh(cal.get(Calendar.DATE))
				+ " "
				+ cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US)
				+ " until ");
				
				cal.add(Calendar.DAY_OF_YEAR, 6);
				
		time = time	+ cal.get(Calendar.DATE)
				+ AddTh.getTh(cal.get(Calendar.DATE))
				+ " "
				+ cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
				
		return time;
	}
}
