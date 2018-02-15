package com.anidari.homeworkdiary;

import java.util.Calendar;
import java.util.Locale;

// holds information on a specific timetable slot as displayed in the timetable display

public class TimetableEntry {

	static final long EMPTY_LONG = -1L;
	static final int EMPTY_INT = -1;

	private int id;
	private String sessionName;
	private String tutor;
	private String location;
	private String note;
	// subject stuff
	private int subjectID;
	private int subjectImageResourceID;
	private String subjectName;
	// time stuff
	private long startTime; // this can use time since midnight
	private long endTime; // same as above
	private long startDate; // this can use Unix epoch of midnight (00:00am) on
							// the day
	private long endDate; // same as above

	private long sessionDate; // this contains the individual date of each
								// instance of this session. All other parts of
								// the session info between weeks will be
								// Identical except this.

	static public enum FREQUENCY {
		NONE, WEEKLY, FORTNIGHTLY, ONE_OFF, DATE_OF_MONTH, FIRST_OF_MONTH, LAST_OF_MONTH, SECOND_OF_MONTH, THIRD_OF_MONTH, FOURTH_OF_MONTH;
	}

	private FREQUENCY freq;

	private boolean[] activeDays; // starts on Sunday, this differs for the
									// Calendar class depending on Locale
									// annoyingly. When looking up by week need
									// to take care!
	// flags for displaying in a list
	private int timeTableColour;
	private boolean deleted;
	private boolean isEndOfSection;

	static public enum TYPE {
		ENTRY, SPACER, TITLE, WEEK_DIVIDER
	}

	private TYPE type;

	/**
	 * used for loading from the database and displaying in a list view or as a
	 * single entry in detail. Uses boolean[] for active days.
	 * 
	 * @param id
	 * @param sessionName
	 * @param tutor
	 * @param location
	 * @param note
	 * @param subjectID
	 * @param startTime
	 *            Time of day that the session begins, ignore the date!
	 * @param endTime
	 *            Time of day that the session ends, ignore the date!
	 * @param startDate
	 *            Date that the sessions of this type begin
	 * @param endDate
	 *            Date that the sessions of this type end
	 * @param sessionDate
	 *            Date of this particular session
	 * @param enumFrequencyAsString
	 * @param activeDays
	 */
	public TimetableEntry(int id, String sessionName, String tutor,
			String location, String note, int subjectID,
			int subjectImageResourceID, String subjectName, long startTime,
			long endTime, long startDate, long endDate, long sessionDate,
			String enumFrequencyAsString, boolean[] activeDays, int timeTableColour) {
		this.setId(id);
		this.setSessionName(sessionName);
		this.setTutor(tutor);
		this.setLocation(location);
		// subject
		this.setSubjectID(subjectID);
		this.setSubjectImageResourceID(subjectImageResourceID);
		this.setSubjectName(subjectName);
		// time
		this.setStartTime(startTime);
		this.setEndTime(endTime);
		this.setStartDate(startDate);
		this.setEndDate(endDate);
		this.setSessionDate(sessionDate);
		this.setFreq(FREQUENCY.valueOf(enumFrequencyAsString));
		this.setNote(note);
		// used in the list view
		this.setDeleted(false);
		this.setType(TYPE.ENTRY);
		this.setEndOfSection(false);
		this.setActiveDays(activeDays);
		this.setTimeTableColour(timeTableColour);
	}

	/**
	 * used for loading from the database and displaying in a list view or as a
	 * single entry in detail. Uses String for active days.
	 * 
	 * @param id
	 * @param sessionName
	 * @param tutor
	 * @param location
	 * @param note
	 * @param subjectID
	 * @param startTime
	 *            Time of day that the session begins, ignore the date!
	 * @param endTime
	 *            Time of day that the session ends, ignore the date!
	 * @param startDate
	 *            Date that the sessions of this type begin
	 * @param endDate
	 *            Date that the sessions of this type end
	 * @param sessionDate
	 *            Date of this particular session
	 * @param enumFrequencyAsString
	 * @param activeDays
	 */
	public TimetableEntry(int id, String sessionName, String tutor,
			String location, String note, int subjectID,
			int subjectImageResourceID, String subjectName, long startTime,
			long endTime, long startDate, long endDate, long sessionDate,
			String enumFrequencyAsString, String activeDays, int timeTableColour) {
		this.setId(id);
		this.setSessionName(sessionName);
		this.setTutor(tutor);
		this.setLocation(location);
		// subject
		this.setSubjectID(subjectID);
		this.setSubjectImageResourceID(subjectImageResourceID);
		this.setSubjectName(subjectName);
		// time
		this.setStartTime(startTime);
		this.setEndTime(endTime);
		this.setStartDate(startDate);
		this.setEndDate(endDate);
		this.setSessionDate(sessionDate);
		this.setFreq(FREQUENCY.valueOf(enumFrequencyAsString));
		this.setNote(note);
		// used in the list view
		this.setDeleted(false);
		this.setType(TYPE.ENTRY);
		this.setEndOfSection(false);
		this.setActiveDaysFromString(activeDays);
		this.setTimeTableColour(timeTableColour);
	}

	/**
	 * used for creating spacers and headings
	 * 
	 * @param type
	 * @param displayText
	 */
	public TimetableEntry(TYPE type, String displayText) {
		// set type
		this.setType(type);
		// blank everything
		this.setId(EMPTY_INT);
		this.setSessionName(null);
		this.setTutor(null);
		this.setLocation(null);
		this.setSubjectID(EMPTY_INT);
		this.setSubjectImageResourceID(EMPTY_INT);
		this.setSubjectName(null);
		this.setStartTime(EMPTY_LONG);
		this.setEndTime(EMPTY_LONG);
		this.setStartDate(EMPTY_LONG);
		this.setEndDate(EMPTY_LONG);
		this.setSessionDate(EMPTY_LONG);
		this.setFreq(FREQUENCY.NONE);
		this.setNote(null);
		this.setDeleted(false);
		this.setEndOfSection(false);
		this.setActiveDays(new boolean[7]);
		this.setTimeTableColour(EMPTY_INT);

		switch (type) {
		case TITLE:
			// empty except for the title
		case WEEK_DIVIDER:
			// empty except for the title
			this.setSessionName(displayText);
			break;
		case ENTRY:
			// empty
		case SPACER:
			// empty
			break;

		}
	}

	/**
	 * used for creating new blank entries
	 * 
	 * @param type
	 *            should be ENTRY
	 */
	public TimetableEntry(TYPE type) {
		// set type
		this.setType(type);
		// blank everything
		this.setId(EMPTY_INT);
		this.setSessionName(null);
		this.setTutor(null);
		this.setLocation(null);
		this.setSubjectID(EMPTY_INT);
		this.setSubjectImageResourceID(EMPTY_INT);
		this.setSubjectName(null);
		this.setStartTime(EMPTY_LONG);
		this.setEndTime(EMPTY_LONG);
		this.setStartDate(EMPTY_LONG);
		this.setEndDate(EMPTY_LONG);
		this.setSessionDate(EMPTY_LONG);
		this.setFreq(FREQUENCY.NONE);
		this.setNote(null);
		this.setDeleted(false);
		this.setEndOfSection(false);
		this.setActiveDays(new boolean[7]);
		this.setTimeTableColour(EMPTY_INT);
	}
	
	/**
	 * constructor for cloning an existing entry
	 * @param entryToClone
	 */
	public TimetableEntry(TimetableEntry entryToClone) {
		this.paste(entryToClone);
	}

	/**
	 * for setting from database storage
	 * 
	 * @param activeDays
	 */
	public void setActiveDaysFromString(String activeDays) {
		boolean[] days = new boolean[7];

		// store them as "ynnyyny" for SMTWTFS
		for (int i = 0; i < activeDays.length(); i++) {
			if (activeDays.charAt(i) == 'y') {
				// set positive
				days[i] = true;
			} else {
				// set negative
				days[i] = false;
			}
		}

		setActiveDays(days);
	}

	/**
	 * for getting from database storage
	 * 
	 * @return
	 */
	public String getActiveDaysAsString() {
		boolean[] days = getActiveDays();
		String activeDaysString = new String();

		// store them as "ynnyyny" for SMTWTFS
		for (int i = 0; i < days.length; i++) {
			if (days[i] == true) {
				// set positive
				activeDaysString = activeDaysString + 'y';
			} else {
				// set negative
				activeDaysString = activeDaysString + 'n';
			}
		}
		return activeDaysString;
	}

	public String getDisplayTime() {
		return HumanReadableDate.getTimetableDateAsString(
				this.getSessionDate(), this.getStartTime(), this.getEndTime());
	}

	public String getShortDisplayTime() {
		return HumanReadableDate.getTimetableTimeAsString(this.getStartTime(),
				this.getEndTime());
	}
	
	public String getQuiteShortDisplayTime() {
		return HumanReadableDate.getTimeString(getStartTime())
				+ " - "
				+ HumanReadableDate.getTimeString(getEndTime());
	}

	public void paste(TimetableEntry tt) {
		// copies all the details without referencing the same object or copying
		// the end of section flags
		// possibly should be implemented using clone()
		this.setId(tt.getId());
		this.setSessionName(tt.getSessionName());
		this.setTutor(tt.getTutor());
		this.setLocation(tt.getLocation());
		// subject
		this.setSubjectID(tt.getSubjectID());
		this.setSubjectImageResourceID(tt.getSubjectImageResourceID());
		this.setSubjectName(tt.getSubjectName());
		// time
		this.setStartTime(tt.getStartTime());
		this.setEndTime(tt.getEndTime());
		this.setStartDate(tt.getStartDate());
		this.setEndDate(tt.getEndDate());
		this.setFreq(tt.getFreq());
		this.setNote(tt.getNote());
		this.setType(tt.getType());
		this.setTimeTableColour(tt.getTimeTableColour());
	}

	/**
	 * returns true if there is at least one day selected
	 * 
	 * @return
	 */
	public boolean isAnyDaySelected() {

		boolean isSelected = false;
		for (boolean day : getActiveDays()) {
			if (day) {
				isSelected = true;
			}
		}
		return isSelected;
	}

	/**
	 * iterates over the active days to check that only one is selected
	 * 
	 * @return
	 */
	public boolean isMoreThanOneDaySelected() {
		boolean isOneSelected = false;
		boolean isMoreThanOneSelected = false;
		for (boolean day : getActiveDays()) {
			if (day) {
				if (isOneSelected) {
					isMoreThanOneSelected = true;
				}
				isOneSelected = true;
			}
		}
		return isMoreThanOneSelected;
	}

	// getters and setters
	// ***************************************************************

	public String getDayString() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.getSessionDate());
		return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG,
				Locale.US);
	}

	public int getDay() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.getSessionDate());
		return cal.get(Calendar.DAY_OF_WEEK) - 1;
	}

	/**
	 * returns the starting minute of the session, useful for displaying on
	 * timetable
	 * 
	 * @return time since midnight
	 */
	public int getStartMinute() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.getStartTime());
		return (cal.get(Calendar.HOUR_OF_DAY) * 60) + cal.get(Calendar.MINUTE);
	}

	/**
	 * returns the ending minute of the session, useful for displaying on
	 * timetable
	 * 
	 * @return time since midnight
	 */
	public int getEndMinute() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.getEndTime());
		return (cal.get(Calendar.HOUR_OF_DAY) * 60) + cal.get(Calendar.MINUTE);
	}

	/**
	 * returns the starting minute of the session, useful for displaying on
	 * timetable
	 * 
	 * @return time since midnight
	 */
	public int getStartHour() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.getStartTime());
		return cal.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * returns the ending minute of the session, useful for displaying on
	 * timetable
	 * 
	 * @return time since midnight
	 */
	public int getEndHour() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.getEndTime());
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		// Log.d("in getEndHour", "hour: " + hour);
		// Log.d("in getEndHour", "display time: " +
		// HumanReadableDate.getTimeString(getEndTime()));
		// check if it finishes on the hour or not
		if (cal.get(Calendar.MINUTE) != 0) {
			hour++;
		}
		return hour;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	public String getTutor() {
		return tutor;
	}

	public void setTutor(String tutor) {
		this.tutor = tutor;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public int getSubjectID() {
		return subjectID;
	}

	public void setSubjectID(int subjectID) {
		this.subjectID = subjectID;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	public FREQUENCY getFreq() {
		return freq;
	}

	public void setFreq(FREQUENCY freq) {
		this.freq = freq;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	public int getSubjectImageResourceID() {
		return subjectImageResourceID;
	}

	public void setSubjectImageResourceID(int subjectImageResourceID) {
		this.subjectImageResourceID = subjectImageResourceID;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public boolean isEndOfSection() {
		return isEndOfSection;
	}

	public void setEndOfSection(boolean isEndOfSection) {
		this.isEndOfSection = isEndOfSection;
	}

	public boolean[] getActiveDays() {
		return activeDays;
	}

	public void setActiveDays(boolean[] activeDays) {
		this.activeDays = activeDays;
	}

	/**
	 * this contains the individual date of each instance of this session. All
	 * other parts of the session info between weeks will be identical except
	 * this.
	 * 
	 * @return
	 */
	public long getSessionDate() {
		return sessionDate;
	}

	public void setSessionDate(long sessionDate) {
		this.sessionDate = sessionDate;
	}

	public int getTimeTableColour() {
		return timeTableColour;
	}

	public void setTimeTableColour(int timeTableColour) {
		this.timeTableColour = timeTableColour;
	}

}
