package com.anidari.homeworkdiary;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import com.anidari.homeworkdiary.HomeworkDetailListEntry.TYPE;
import com.anidari.homeworkdiary.MainMenuItem.MENU_TYPE;
import com.anromus.homeworkdiary.R;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

/* this class manages the database containing three tables:
 * 
 * 1. HOMEWORK
 * 
 * 2. SUBJECT
 * 
 * 3. TIMETABLE
 * 
 * It must be able to populate the classes HomeworkEntry, SubjectEntry, HomeworkListEntry and TimetableEntry
 */

public class DatabaseHandler extends SQLiteOpenHelper {

	// for debugging
	private static final boolean DELETE_DATABASE_ON_STARTUP = false;
	// General database

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database name
	private static final String DATABASE_NAME = "homeworkManager";

	// All-purpose column names
	private static final String KEY_ID = "id";
	private static final String KEY_SUBJECT_ID = "subject_id";

	// 1. HOMEWORK
	// table name
	private static final String TABLE_HOMEWORK = "homework";
	// table column names
	private static final String KEY_DUE_DATE = "due_date";
	private static final String KEY_HOMEWORK_TITLE = "homework_title";
	private static final String KEY_IS_REMINDER_SET = "is_reminder_set";
	private static final String KEY_IS_COMPLETED = "is_completed";
	private static final String KEY_FINAL_GRADE = "final_grade";
	private static final String KEY_TODO_LIST = "todo_list";

	// 2. SUBJECT
	// table name
	private static final String TABLE_SUBJECT = "subject";
	// table column names
	private static final String KEY_SUBJECT_NAME = "name";
	private static final String KEY_SUBJECT_PICTURE = "picture";

	// 3. TIMETABLE
	// session table name
	private static final String TABLE_TIMETABLE_SESSION = "timetable_session";
	// table column names
	private static final String KEY_SESSION_NAME = "session_name";
	private static final String KEY_TUTOR = "tutor";
	private static final String KEY_LOCATION = "location";
	private static final String KEY_START_TIME = "start_time";
	private static final String KEY_END_TIME = "end_time";
	private static final String KEY_START_DATE = "start_date";
	private static final String KEY_END_DATE = "end_date";
	private static final String KEY_FREQUENCY = "frequency";
	private static final String KEY_NOTE = "note";
	private static final String KEY_ACTIVE_DAYS = "active_days";
	private static final String KEY_COLOUR_ID = "colour_id";

	// timetable date table name
	// session table name
	private static final String TABLE_TIMETABLE_DATE = "timetable_date";
	// table column names
	private static final String KEY_DATE_LONG = "date_long";
	private static final String KEY_SESSION_ID = "session_id";

	// 4. TO DO LISTS
	// table name
	private static final String TABLE_TODO = "todo";
	// table column names
	private static final String KEY_HOMEWORK_ID = "homework_id";
	private static final String KEY_ORDER = "list_order";
	private static final String KEY_TEXT = "todo_text";
	private static final String KEY_COMPLETED = "completed";

	// Constructor
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// Below deletes database on startup, used for debugging purposes
		if (DELETE_DATABASE_ON_STARTUP)
			context.deleteDatabase(DATABASE_NAME);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// create each of the three tables in turn
		String CREATE_HOMEWORK_TABLE = "CREATE TABLE " + TABLE_HOMEWORK + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_DUE_DATE + " INTEGER,"
				+ KEY_HOMEWORK_TITLE + " STRING," + KEY_SUBJECT_ID
				+ " INTEGER," + KEY_IS_REMINDER_SET + " INTEGER,"
				+ KEY_IS_COMPLETED + " INTEGER," + KEY_FINAL_GRADE + " TEXT,"
				+ KEY_TODO_LIST + " BLOB" + ")";
		String CREATE_SUBJECT_TABLE = "CREATE TABLE " + TABLE_SUBJECT + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_SUBJECT_NAME
				+ " TEXT," + KEY_SUBJECT_PICTURE + " INTEGER" + ")";
		String CREATE_TIMETABLE_SESSION_TABLE = "CREATE TABLE "
				+ TABLE_TIMETABLE_SESSION + "(" + KEY_ID
				+ " INTEGER PRIMARY KEY," + KEY_SESSION_NAME + " TEXT,"
				+ KEY_TUTOR + " TEXT," + KEY_NOTE + " TEXT," + KEY_SUBJECT_ID
				+ " INTEGER," + KEY_LOCATION + " STRING," + KEY_START_TIME
				+ " INTEGER," + KEY_END_TIME + " INTEGER," + KEY_START_DATE
				+ " INTEGER," + KEY_END_DATE + " INTEGER," + KEY_FREQUENCY
				+ " TEXT," + KEY_ACTIVE_DAYS + " TEXT, " + KEY_COLOUR_ID
				+ " INTEGER" + ")";
		String CREATE_TIMETABLE_DATE_TABLE = "CREATE TABLE "
				+ TABLE_TIMETABLE_DATE + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
				+ KEY_DATE_LONG + " INTEGER," + KEY_SESSION_ID + " INTEGER"
				+ ")";
		String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_TODO + "(" + KEY_ID
				+ " INTEGER PRIMARY KEY," + KEY_HOMEWORK_ID + " INTEGER,"
				+ KEY_ORDER + " INTEGER," + KEY_TEXT + " TEXT," + KEY_COMPLETED
				+ " INTEGER" + ")";
		db.execSQL(CREATE_HOMEWORK_TABLE);
		db.execSQL(CREATE_SUBJECT_TABLE);
		db.execSQL(CREATE_TIMETABLE_SESSION_TABLE);
		db.execSQL(CREATE_TIMETABLE_DATE_TABLE);
		db.execSQL(CREATE_TODO_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Called when the database needs to be upgraded
		// Drop older table if it existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOMEWORK);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMETABLE_SESSION);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMETABLE_DATE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);

		// Create table again
		onCreate(db);
	}

	// Methods for the homework entries
	// ******************************************************
	// adding a new homework item
	public int addHomework(HomeworkEntry homework) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_DUE_DATE, homework.getDueDate());
		values.put(KEY_HOMEWORK_TITLE, homework.getHomeworkTitle());
		values.put(KEY_SUBJECT_ID, homework.getSubjectID());
		values.put(KEY_IS_REMINDER_SET, homework.getIsReminderSet());
		values.put(KEY_IS_COMPLETED, homework.getIsCompleted());
		values.put(KEY_FINAL_GRADE, homework.getFinalGrade());
		values.put(KEY_TODO_LIST, homework.getSerialisedToDo());

		// Inserting Row
		int newId = (int) db.insert(TABLE_HOMEWORK, null, values);

		db.close(); // closes database connection

		return newId;
	}

	public HomeworkEntry getHomeworkEntry(int id) {

		// fetches a readable database
		SQLiteDatabase db = this.getReadableDatabase();

		// creates a cursor for looking at the entries based on a query for the
		// id number passed to the method
		Cursor cursor = db.query(TABLE_HOMEWORK, new String[] { KEY_ID,
				KEY_DUE_DATE, KEY_HOMEWORK_TITLE, KEY_SUBJECT_ID,
				KEY_IS_REMINDER_SET, KEY_IS_COMPLETED, KEY_FINAL_GRADE },
				KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null,
				null, null);

		if (cursor != null)
			cursor.moveToFirst();

		HomeworkEntry homeworkEntry = new HomeworkEntry(Integer.parseInt(cursor
				.getString(0)), cursor.getLong(1), cursor.getString(2),
				cursor.getInt(3), (cursor.getInt(4) == 1),
				(cursor.getInt(5) == 1), cursor.getString(6));

		db.close();
		cursor.close();

		return homeworkEntry;
	}

	/**
	 * Returns a list of the titles of current homework as an ArrayList
	 * 
	 * @return
	 */
	public ArrayList<String> getHomeworkTitles() {
		// create a new list to return
		ArrayList<String> homeworkTitles = new ArrayList<String>();

		// get the titles from the database
		String titlesQuery = "SELECT " + KEY_HOMEWORK_TITLE + " FROM "
				+ TABLE_HOMEWORK;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(titlesQuery, null);

		// loop through the cursor to get all the entries into the array list
		if (cursor.moveToFirst()) {
			do {
				homeworkTitles.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}

		db.close();
		cursor.close();

		return homeworkTitles;
	}

	/**
	 * Returns the titles of current homework as a String array
	 * 
	 * @return
	 */
	public String[] getHomeworkTitlesString() {
		// get the ArrayList of strings
		ArrayList<String> homeworkTitles = getHomeworkTitles();
		// create a new arry of strings of the required length
		String[] homeworkTitlesString = new String[homeworkTitles.size()];
		// put the strings in the array
		homeworkTitlesString = homeworkTitles.toArray(homeworkTitlesString);
		// return it
		return homeworkTitlesString;
	}

	/**
	 * removes the given homework detail list entry and it's todo list from the
	 * database
	 * 
	 * @param homeworkEntry
	 */
	public void deleteHomework(ArrayList<HomeworkDetailListEntry> homeworkEntry) {
		deleteHomework(homeworkEntry.get(1).getId());
	}

	/**
	 * removes the homework entry with the given id number and it's todo list
	 * from the database
	 * 
	 * @param homeworkEntry
	 */
	public void deleteHomework(int homeworkId) {

		SQLiteDatabase db = this.getWritableDatabase();

		// first delete the associated to do items
		try {
			db.delete(TABLE_TODO, KEY_HOMEWORK_ID + " = ?", new String[] { ""
					+ homeworkId });
		} catch (Exception e) {
			e.printStackTrace();

		}

		// then delete the homework entry
		try {
			db.delete(TABLE_HOMEWORK, KEY_ID + " = ?", new String[] { ""
					+ homeworkId });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	/**
	 * method called by the delete options dialog
	 * 
	 * @param what
	 *            0 = completed, 1 = by subject, 2 = all
	 * @param subjectID
	 *            the id number of the subject, only takes effect if what = 1
	 * @param when
	 *            0 = 6 months, 1 = 3 months, 2 = 1 month, 3 = 2 weeks, 4 = 1
	 *            week, 5 = 1 day, 6 = all
	 */
	public void deleteSelection(int what, int subjectID, int when) {
		// what: 0 = completed, 1 = by subject, 2 = all
		// when: 0 = 6 months, 1 = 3 months, 2 = 1 month, 3 = 2 weeks, 4 = 1
		// week, 5 = 1 day, 6 = all

		SQLiteDatabase db = this.getReadableDatabase();

		// use a cursor to query the database for all of the homework matching
		// the criteria. Send the id numbers to the method deleteHomework() to
		// make sure to do lists are deleted too

		// the query to shape as needed
		String query = new String("SELECT " + KEY_ID + " FROM "
				+ TABLE_HOMEWORK);

		if (what != 2 || when != 6) {
			// then a where clause is needed
			query = query.concat(" WHERE ");
		}

		// add the first query
		if (what == 0) {
			query = query.concat(KEY_IS_COMPLETED + " = 1");
		} else if (what == 1) {
			query = query.concat(KEY_SUBJECT_ID + " = " + subjectID);
		}

		// adding the AND
		if (what == 2 || when == 6) {
			// no AND required
		} else {
			// needs an AND
			query = query.concat(" AND ");
		}

		// set up the date for the various date options
		// timeToDeleteCal at current time
		Calendar timeToDeleteCal = Calendar.getInstance(TimeZone.getDefault());
		timeToDeleteCal.set(Calendar.HOUR_OF_DAY, 0);
		timeToDeleteCal.set(Calendar.MINUTE, 0);
		timeToDeleteCal.set(Calendar.SECOND, 0);
		timeToDeleteCal.set(Calendar.MILLISECOND, 0);
		// any record with a time less than above is overdue

		switch (when) {
		case 0:
			// 6 months
			timeToDeleteCal.roll(Calendar.MONTH, -6);
			break;
		case 1:
			// 3 months
			timeToDeleteCal.roll(Calendar.MONTH, -3);
			break;
		case 2:
			// 1 month
			timeToDeleteCal.roll(Calendar.MONTH, -1);
			break;
		case 3:
			// 2 weeks
			timeToDeleteCal.roll(Calendar.WEEK_OF_YEAR, -2);
			break;
		case 4:
			// 1 week
			timeToDeleteCal.roll(Calendar.WEEK_OF_YEAR, -1);
			break;
		case 5:
			// 1 day
			break;
		default:
			// shouldn't be used
			break;
		}

		// add in the 2nd where clause if needed
		if (when != 6) {
			query = query.concat(KEY_DUE_DATE + " < "
					+ timeToDeleteCal.getTimeInMillis());

		}

		// create a cursor
		Cursor cursor = db.rawQuery(query, null);

		// iterate through the list to delete them
		if (cursor.moveToFirst()) {
			do {
				// delete the selected homework
				deleteHomework(cursor.getInt(0));
			} while (cursor.moveToNext());
		}
		db.close();
		cursor.close();
	}

	/**
	 * Gets the list of homework assignments for the front page, arranged and
	 * categorised by due date
	 * 
	 * @return the list to be fed to the array adapter
	 */
	public ArrayList<HomeworkListEntry> getAllHomework(Context context) {
		ArrayList<HomeworkListEntry> homeworkList = new ArrayList<HomeworkListEntry>();
		ArrayList<HomeworkListEntry> completedHomeworkList = new ArrayList<HomeworkListEntry>();
		// Select all query
		String selectQuery = "SELECT " + TABLE_HOMEWORK + "." + KEY_ID + ", "
				+ TABLE_HOMEWORK + "." + KEY_DUE_DATE + ", " + TABLE_HOMEWORK
				+ "." + KEY_HOMEWORK_TITLE + ", " + TABLE_HOMEWORK + "."
				+ KEY_IS_COMPLETED + ", " + TABLE_HOMEWORK + "."
				+ KEY_FINAL_GRADE + ", " + TABLE_HOMEWORK + "."
				+ KEY_SUBJECT_ID + ", " + TABLE_SUBJECT + "."
				+ KEY_SUBJECT_NAME + ", " + TABLE_SUBJECT + "."
				+ KEY_SUBJECT_PICTURE + " FROM " + TABLE_HOMEWORK + " JOIN "
				+ TABLE_SUBJECT + " ON " + TABLE_HOMEWORK + "."
				+ KEY_SUBJECT_ID + " = " + TABLE_SUBJECT + "." + KEY_ID
				+ " ORDER BY " + KEY_DUE_DATE;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		/*
		 * when looping through categories, made with titles
		 * 
		 * titles should only be inserted when needed titles: 0 - Overdue 1 -
		 * Today 2 - Tomorrow 3 - This week 4 - Next Week 5 - After Next Week
		 * 
		 * When "hwk" is created, check due date using getDateCategory() compare
		 * this to a local variable for current category if it is different,
		 * then create a new category title. if it is the same continue then
		 * create the new entry underneath
		 */

		int currentCategory = -1;
		int newCategory = -1;

		// looping through all rows and adding to the list
		if (cursor.moveToFirst()) {

			do {
				// HomeworkListEntry (int id, long dueDate, String
				// homeworkTitle, int subjectID, boolean isCompleted, String
				// finalGrade)

				boolean roundCorners = false;

				HomeworkListEntry hwk = new HomeworkListEntry();
				hwk.setId(Integer.parseInt(cursor.getString(0)));
				hwk.setDueDate(cursor.getLong(1));
				hwk.setHomeworkTitle(cursor.getString(2));
				hwk.setIsCompleted(cursor.getInt(3) == 1);
				hwk.setFinalGrade(cursor.getString(4));
				hwk.setSubjectID(cursor.getInt(5));
				hwk.setSubjectName(cursor.getString(6));
				hwk.setSubjectImageResource(cursor.getInt(7));

				// check for new title needed
				newCategory = getDateCategory(hwk.getDueDate(),
						hwk.getIsCompleted());
				if (newCategory == 6) {
					// put in the temporary completed list
					completedHomeworkList.add(hwk);
				} else if (newCategory != currentCategory) {
					// new title needed
					roundCorners = true;
					currentCategory = newCategory;
					// add spacer
					homeworkList.add(new HomeworkListEntry(true));
					// add title
					homeworkList.add(new HomeworkListEntry(
							getDateCategoryTitle(context, currentCategory)));

				}
				
				if (newCategory == 0) {
					hwk.setOverdue(true);
				}

				// round previous corners
				if (roundCorners) {
					int entryToRound = homeworkList.size() - 3;
					if (entryToRound >= 0) {
						homeworkList.get(entryToRound).setEndOfSection(true);
					}
				}

				if (newCategory != 6) {
					// Adding homework to list
					homeworkList.add(hwk);
				}
			} while (cursor.moveToNext());

			// round off the last entry
			homeworkList.get(homeworkList.size() - 1).setEndOfSection(true);

			// add a spacer
			homeworkList.add(new HomeworkListEntry(true));

			// append the list of completed homework
			if (completedHomeworkList.size() > 0) {
				// add a title
				homeworkList.add(new HomeworkListEntry(getDateCategoryTitle(
						context, 6)));

				// append the list
				homeworkList.addAll(completedHomeworkList);

				// round off the last entry
				homeworkList.get(homeworkList.size() - 1).setEndOfSection(true);

				// add a spacer
				homeworkList.add(new HomeworkListEntry(true));
			}
		}
		// close the cursor
		db.close();
		cursor.close();

		// return the homework list
		return homeworkList;
	}

	/**
	 * returns the string associated with a date category
	 * 
	 * @param category
	 * @return
	 */
	public String getDateCategoryTitle(Context context, int category) {

		switch (category) {
		case 0:
			return context.getString(R.string.date_cat_overdue);
		case 1:
			return context.getString(R.string.date_cat_today);
		case 2:
			return context.getString(R.string.date_cat_tomorrow);
		case 3:
			return context.getString(R.string.date_cat_this_week);
		case 4:
			return context.getString(R.string.date_cat_next_week);
		case 5:
			return context.getString(R.string.date_cat_after_next_week);
		case 6:
			return context.getString(R.string.date_cat_completed);
		default:
			return context.getString(R.string.date_cat_no_date);
		}
	}

	/**
	 * takes the due date as an argument and returns the date category as an int
	 * compared to today's date
	 * 
	 * 0 - Overdue 1 - Today 2 - Tomorrow 3 - This week 4 - Next Week 5 - After
	 * Next Week
	 */
	public int getDateCategory(long dueDate, boolean isCompleted) {

		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		Calendar dueCal = Calendar.getInstance(TimeZone.getDefault());
		dueCal.setTimeInMillis(dueDate);

		if (dueCal.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)) {
			// Today:
			return 1;
		}

		else if (cal.getTimeInMillis() > dueDate) {
			if (isCompleted) {
				// Completed and submitted
				return 6;
			} else {
				// Overdue:
				return 0;
			}

		} else if (dueCal.get(Calendar.DAY_OF_YEAR) == (cal
				.get(Calendar.DAY_OF_YEAR) + 1)) {
			// Tomorrow:
			return 2;

		} else if (dueCal.get(Calendar.WEEK_OF_YEAR) == cal
				.get(Calendar.WEEK_OF_YEAR)) {
			// This week:
			return 3;

		} else if (dueCal.get(Calendar.WEEK_OF_YEAR) == (cal
				.get(Calendar.WEEK_OF_YEAR) + 1)) {
			// Next week:
			return 4;

		} else {
			// After next week:
			return 5;
		}
	}

	/**
	 * Gets the list of homework assignments for the front page, arranged and
	 * categorised by subject
	 * 
	 * @return the list to be fed to the array adapter
	 */
	public ArrayList<HomeworkListEntry> getAllHomeworkBySubject(Context context) {
		ArrayList<HomeworkListEntry> homeworkList = new ArrayList<HomeworkListEntry>();
		// Select all query
		String selectQuery = "SELECT " + TABLE_HOMEWORK + "." + KEY_ID + ", "
				+ TABLE_HOMEWORK + "." + KEY_DUE_DATE + ", " + TABLE_HOMEWORK
				+ "." + KEY_HOMEWORK_TITLE + ", " + TABLE_HOMEWORK + "."
				+ KEY_IS_COMPLETED + ", " + TABLE_HOMEWORK + "."
				+ KEY_FINAL_GRADE + ", " + TABLE_HOMEWORK + "."
				+ KEY_SUBJECT_ID + ", " + TABLE_SUBJECT + "."
				+ KEY_SUBJECT_NAME + ", " + TABLE_SUBJECT + "."
				+ KEY_SUBJECT_PICTURE + " FROM " + TABLE_HOMEWORK + " JOIN "
				+ TABLE_SUBJECT + " ON " + TABLE_HOMEWORK + "."
				+ KEY_SUBJECT_ID + " = " + TABLE_SUBJECT + "." + KEY_ID
				+ " ORDER BY " + TABLE_SUBJECT + "." + KEY_SUBJECT_NAME 
				+ ", " + TABLE_HOMEWORK + "." + KEY_DUE_DATE;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		/*
		 * when looping through categories need to be made with titles
		 * 
		 * When "hwk" is created, check subject, compare this to a local
		 * variable for current category if it is different, then create a new
		 * category title. if it is the same continue then create the new entry
		 * underneath
		 */

		int currentSubject = -1;
		int newSubject = -1;

		// looping through all rows and adding to the list
		if (cursor.moveToFirst()) {
			do {
				// HomeworkListEntry (int id, long dueDate, String
				// homeworkTitle, int subjectID, boolean isCompleted, String
				// finalGrade)

				boolean roundCorners = false;

				HomeworkListEntry hwk = new HomeworkListEntry();
				hwk.setId(Integer.parseInt(cursor.getString(0)));
				hwk.setDueDate(cursor.getLong(1));
				hwk.setHomeworkTitle(cursor.getString(2));
				hwk.setIsCompleted(cursor.getInt(3) == 1);
				hwk.setFinalGrade(cursor.getString(4));
				hwk.setSubjectID(cursor.getInt(5));
				hwk.setSubjectName(cursor.getString(6));
				hwk.setSubjectImageResource(cursor.getInt(7));

				// check for overdue
				if (getDateCategory(hwk.getDueDate(),
						hwk.getIsCompleted()) == 0) {
					hwk.setOverdue(true);
				}
				
				// check for new title needed
				newSubject = hwk.getSubjectID();
				if (newSubject != currentSubject) {
					// new title needed
					roundCorners = true;
					currentSubject = newSubject;
					// add spacer
					homeworkList.add(new HomeworkListEntry(true));
					// add title
					homeworkList
							.add(new HomeworkListEntry(hwk.getSubjectName()));
				}

				// round previous corners
				if (roundCorners) {
					int entryToRound = homeworkList.size() - 3;
					if (entryToRound >= 0) {
						homeworkList.get(entryToRound).setEndOfSection(true);
					}
				}

				// Adding homework to list
				homeworkList.add(hwk);
			} while (cursor.moveToNext());

			// round off the last entry
			homeworkList.get(homeworkList.size() - 1).setEndOfSection(true);

			// add a final spacer
			homeworkList.add(new HomeworkListEntry(true));
		}
		// close the cursor
		db.close();
		cursor.close();

		// return the homework list
		return homeworkList;
	}

	/**
	 * Single homework assignments for updating the front page
	 * 
	 * @return
	 */
	public HomeworkListEntry getHomeworkListEntry(int id) {
		HomeworkListEntry hwk = new HomeworkListEntry();
		// Select all query
		String selectQuery = "SELECT " + TABLE_HOMEWORK + "." + KEY_ID + ", "
				+ TABLE_HOMEWORK + "." + KEY_DUE_DATE + ", " + TABLE_HOMEWORK
				+ "." + KEY_HOMEWORK_TITLE + ", " + TABLE_HOMEWORK + "."
				+ KEY_IS_COMPLETED + ", " + TABLE_HOMEWORK + "."
				+ KEY_FINAL_GRADE + ", " + TABLE_HOMEWORK + "."
				+ KEY_SUBJECT_ID + ", " + TABLE_SUBJECT + "."
				+ KEY_SUBJECT_NAME + ", " + TABLE_SUBJECT + "."
				+ KEY_SUBJECT_PICTURE + " FROM " + TABLE_HOMEWORK + " JOIN "
				+ TABLE_SUBJECT + " ON " + TABLE_HOMEWORK + "."
				+ KEY_SUBJECT_ID + " = " + TABLE_SUBJECT + "." + KEY_ID
				+ " WHERE " + TABLE_HOMEWORK + "." + KEY_ID + " = " + id
				+ " ORDER BY " + KEY_DUE_DATE;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to the list
		if (cursor.moveToFirst()) {

			// HomeworkListEntry (int id, long dueDate, String
			// homeworkTitle, int subjectID, boolean isCompleted, String
			// finalGrade)

			hwk.setId(Integer.parseInt(cursor.getString(0)));
			hwk.setDueDate(cursor.getLong(1));
			hwk.setHomeworkTitle(cursor.getString(2));
			hwk.setIsCompleted(cursor.getInt(3) == 1);
			hwk.setFinalGrade(cursor.getString(4));
			hwk.setSubjectID(cursor.getInt(5));
			hwk.setSubjectName(cursor.getString(6));
			hwk.setSubjectImageResource(cursor.getInt(7));

		}
		// close the cursor
		db.close();
		cursor.close();

		// return the homework list
		return hwk;
	}

	public boolean isHomeworkEmpty() {
		// helps with initial setup to see if the database needs populating
		String emptyQuery = "SELECT * FROM " + TABLE_HOMEWORK;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(emptyQuery, null);
		boolean returnBool = !cursor.moveToFirst();
		db.close();
		cursor.close();
		return returnBool;
	}

	/**
	 * pass it the id of a homework entry and it returns the list needed to
	 * populate the detailed homework view
	 * 
	 * @return ArrayList<HomeworkDetailListEntry>
	 */
	public ArrayList<HomeworkDetailListEntry> getHomeworkDetailList(int id) {
		ArrayList<HomeworkDetailListEntry> details = new ArrayList<HomeworkDetailListEntry>();

		SubjectEntry subject;

		// fetches a readable database
		SQLiteDatabase db = this.getReadableDatabase();

		// creates a cursor for looking at the entries based on a query for the
		// id number passed to the method
		Cursor cursor = db.query(TABLE_HOMEWORK, new String[] { KEY_ID,
				KEY_DUE_DATE, KEY_HOMEWORK_TITLE, KEY_SUBJECT_ID,
				KEY_IS_REMINDER_SET, KEY_IS_COMPLETED, KEY_FINAL_GRADE },
				KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null,
				null, null);

		if (cursor != null)
			cursor.moveToFirst();

		// finding the subject details
		subject = getSubjectEntry(cursor.getInt(3));

		// 0 spacer
		details.add(new HomeworkDetailListEntry(TYPE.SPACER));

		// 1 title
		details.add(new HomeworkDetailListEntry(TYPE.TITLE, id, cursor
				.getInt(3), cursor.getString(2), subject.getSubjectName(),
				subject.getSubjectPicture()));

		// 2 spacer
		details.add(new HomeworkDetailListEntry(TYPE.SPACER, true));

		// 3 due date
		if (!cursor.isNull(1)) {
			details.add(new HomeworkDetailListEntry(TYPE.DUE_DATE, cursor
					.getLong(1)));
		} else
			details.add(new HomeworkDetailListEntry(TYPE.DUE_DATE, 0L));

		// 4 reminder set
		details.add(new HomeworkDetailListEntry(TYPE.REMINDER_SET, cursor
				.getString(6), (cursor.getInt(4) == 1)));

		// 5 finished/grade
		if (!cursor.isNull(6)) {
			details.add(new HomeworkDetailListEntry(TYPE.FINISHED_GRADE, cursor
					.getString(6), (cursor.getInt(5) == 1)));
		} else
			details.add(new HomeworkDetailListEntry(TYPE.FINISHED_GRADE, null,
					(cursor.getInt(5) == 1)));

		// 6 spacer
		details.add(new HomeworkDetailListEntry(TYPE.SPACER));

		// 7 todo title
		details.add(new HomeworkDetailListEntry(TYPE.TO_DO_TITLE));

		// 8 add all the to do entries to the list in order
		cursor.close();
		details = appendToDoList(details, id);

		// 9 add the "add new to do list item" box at the end
		details.add(new HomeworkDetailListEntry(TYPE.TODO_ADD_NEW));

		// 10 spacer
		details.add(new HomeworkDetailListEntry(TYPE.SPACER));

		db.close();

		return details;
	}

	/**
	 * creates a blank homework entry to be displayed in the details view
	 */
	public ArrayList<HomeworkDetailListEntry> getBlankRecord() {
		ArrayList<HomeworkDetailListEntry> details = new ArrayList<HomeworkDetailListEntry>();
		// 0 spacer
		details.add(new HomeworkDetailListEntry(TYPE.SPACER));

		// 1 title
		details.add(new HomeworkDetailListEntry(TYPE.TITLE));

		// 2 spacer
		details.add(new HomeworkDetailListEntry(TYPE.SPACER, true));

		// 3 due date
		details.add(new HomeworkDetailListEntry(TYPE.DUE_DATE));

		// 4 reminder set, need to implement storing reminder details
		details.add(new HomeworkDetailListEntry(TYPE.REMINDER_SET));

		// 5 finished/grade
		details.add(new HomeworkDetailListEntry(TYPE.FINISHED_GRADE));

		// 6 spacer
		details.add(new HomeworkDetailListEntry(TYPE.SPACER));

		// 7 todo title
		details.add(new HomeworkDetailListEntry(TYPE.TO_DO_TITLE));

		// 8 add the "add new to do list item" box at the end
		details.add(new HomeworkDetailListEntry(TYPE.TODO_ADD_NEW));

		// 9 spacer
		details.add(new HomeworkDetailListEntry(TYPE.SPACER));

		return details;

	}

	/**
	 * takes the arrayList used in the homework detail activity and updates the
	 * database with it's contents, should be called in the onPause method.
	 * 
	 * @param homeworkDetail
	 */
	public void writeHomeworkDetailToDatabase(
			ArrayList<HomeworkDetailListEntry> homeworkDetail) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		int homeworkId = -1;
		int toDoCount = 0;

		// iterate over the list to set the correct details

		for (HomeworkDetailListEntry hwk : homeworkDetail) {

			if (hwk.getType() == TYPE.TITLE)
				homeworkId = hwk.getId();

			if (hwk.getHasChanged()) {
				switch (hwk.getType()) {

				case TITLE:
					if (hwk.getText1() != null)
						values.put(KEY_HOMEWORK_TITLE, hwk.getText1());
					values.put(KEY_SUBJECT_ID, hwk.getId2());
					break;
				case DUE_DATE:
					values.put(KEY_DUE_DATE, hwk.getDueDate());
					break;
				case FINISHED_GRADE:
					if (hwk.getText1() != null)
						values.put(KEY_FINAL_GRADE, hwk.getText1());
					values.put(KEY_IS_COMPLETED, hwk.getIsTicked());
					break;
				case REMINDER_SET:
					values.put(KEY_IS_REMINDER_SET, hwk.getIsTicked());
					break;

				default:
					break;
				}

			}

			// always update to do list entries
			if (hwk.getType() == TYPE.TO_DO_ENTRY) {

				ContentValues toDoValues = new ContentValues();
				toDoValues.put(KEY_ORDER, toDoCount);
				if (hwk.getText1() != null)
					toDoValues.put(KEY_TEXT, hwk.getText1());
				toDoValues.put(KEY_COMPLETED, hwk.getIsTicked());

				db.update(TABLE_TODO, toDoValues,
						"" + KEY_ID + " = " + hwk.getId(), null);

				toDoCount++;
			}
		}

		if (homeworkId != -1 && values.size() != 0) {
			db.update(TABLE_HOMEWORK, values, KEY_ID + " = " + homeworkId, null);
		}

		db.close();

	}

	// Methods for the todo list entries
	// *******************************************************

	/**
	 * adds todo entry to the database, automatically creates an ID number
	 * 
	 * @param todo
	 * @return the id of the newly created entry
	 */
	public int addToDoEntry(ToDoEntry todo) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_HOMEWORK_ID, todo.getHomeworkId());
		values.put(KEY_ORDER, todo.getOrder());
		values.put(KEY_TEXT, todo.getToDoItem());
		values.put(KEY_COMPLETED, todo.getIsDone());

		// Inserting Row
		int rowId = (int) db.insert(TABLE_TODO, null, values);

		db.close(); // closes database connection

		return rowId;
	}

	/**
	 * not implemented yet
	 * 
	 * @param id
	 * @return
	 */
	public ToDoEntry getToDoEntryById(int id) {
		ToDoEntry todo = null;

		return todo;
	}

	/**
	 * does exactly what you think it does
	 * 
	 * @param id
	 *            the row id of the entry to be removed
	 */
	public void removeToDoEntry(int id) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_TODO, KEY_ID + " = " + id, null);

		db.close();
	}

	/**
	 * this method appends the todo list entries on the the given ArrayList
	 * 
	 * @param details
	 *            the ArrayList
	 * @param id
	 *            the id of the homework
	 */
	public ArrayList<HomeworkDetailListEntry> appendToDoList(
			ArrayList<HomeworkDetailListEntry> details, int id) {

		SQLiteDatabase db = this.getReadableDatabase();

		// get a list of the todo entries for the specified homework in order
		String selectQuery = "SELECT " + KEY_ID + ", " + KEY_TEXT + ", "
				+ KEY_COMPLETED + " FROM " + TABLE_TODO + " WHERE "
				+ KEY_HOMEWORK_ID + " = " + id + " ORDER BY " + KEY_ORDER;

		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				details.add(new HomeworkDetailListEntry(TYPE.TO_DO_ENTRY,
						Integer.parseInt(cursor.getString(0)), id, cursor
								.getString(1), (cursor.getInt(2) == 1)));

			} while (cursor.moveToNext());

		}

		db.close();
		return details;

	}

	/**
	 * helps with initial setup to see if the database needs populating
	 * 
	 * @return
	 */
	public boolean isToDoEmpty() {
		String emptyQuery = "SELECT * FROM " + TABLE_TODO;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(emptyQuery, null);
		boolean returnBool = !cursor.moveToFirst();
		db.close();
		cursor.close();
		return returnBool;
	}

	// Methods for the subject entries
	// ***************************************************************************************

	/**
	 * creates an entry for the default subject and adds it's id as a long to
	 * the shared preferences, retrievable using
	 * SettingsEditor.PREF_NO_SUBJECT_ID
	 * 
	 * @param context
	 */
	public void createDefaultSubject(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		SharedPreferences.Editor editor = preferences.edit();

		long dbId = addSubject(new SubjectEntry(0, context.getResources()
				.getString(R.string.no_subject_set), R.drawable.question_mark),
				true);

		editor.putLong(SettingsActivity.PREF_NO_SUBJECT_ID, dbId);
		editor.commit();
	}

	public void addSubject(SubjectEntry subject) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_SUBJECT_NAME, subject.getSubjectName());
		values.put(KEY_SUBJECT_PICTURE, subject.getSubjectPicture());

		// Inserting Row
		db.insert(TABLE_SUBJECT, null, values);

		db.close(); // closes database connection
	}

	public long addSubject(SubjectEntry subject, boolean isPermanent) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_SUBJECT_NAME, subject.getSubjectName());
		values.put(KEY_SUBJECT_PICTURE, subject.getSubjectPicture());

		// Inserting Row
		long id = db.insert(TABLE_SUBJECT, null, values);

		db.close(); // closes database connection

		return id;
	}

	/**
	 * the subject will be updated in the database
	 * 
	 * @param subject
	 */
	public void updateSubject(SubjectEntry subject) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues subjectValues = new ContentValues();
		if (subject.getSubjectName() != null)
			subjectValues.put(KEY_SUBJECT_NAME, subject.getSubjectName());
		subjectValues.put(KEY_SUBJECT_PICTURE, subject.getSubjectPicture());

		db.update(TABLE_SUBJECT, subjectValues,
				"" + KEY_ID + " = " + subject.getDatabaseID(), null);

		db.close();
	}

	public void deleteSubject(SubjectEntry subject) {

		SQLiteDatabase db = this.getWritableDatabase();

		try {
			db.delete(TABLE_SUBJECT, KEY_ID + " = ?", new String[] { ""
					+ subject.getDatabaseID() });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}

	}

	/**
	 * used to check how many homework entries are assigned to a particular
	 * subject. Useful for checking before deleting a subject
	 * 
	 * @param subject
	 *            subject to check
	 * @return amount
	 */
	public int howManyHomeworkAssignedToSubject(int databaseId) {

		SQLiteDatabase db = this.getReadableDatabase();

		// counter
		int howMany = 0;

		// get a list of the todo entries for the specified homework in order
		String selectQuery = "SELECT " + KEY_SUBJECT_ID + " FROM "
				+ TABLE_HOMEWORK + " WHERE " + KEY_SUBJECT_ID + " = "
				+ databaseId;

		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				howMany++;

			} while (cursor.moveToNext());
		}
		db.close();

		return howMany;
	}

	public void unassignSubject(SubjectEntry subject, Context context) {

		SQLiteDatabase db = this.getWritableDatabase();

		// update all the entries from the given subject to the new one

		// finds the id of the "no subject" entry. If it doesn't exist in the
		// settings then it creates one
		long defaultId;

		do {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(context);

			defaultId = preferences.getLong(
					SettingsActivity.PREF_NO_SUBJECT_ID, -1L);

			if (defaultId == -1L) {
				createDefaultSubject(context);
			}
		} while (defaultId == -1L);

		// updates the relevant homework entries
		ContentValues values = new ContentValues();
		values.put(KEY_SUBJECT_ID, defaultId);

		db.update(TABLE_HOMEWORK, values,
				KEY_SUBJECT_ID + " = " + subject.getDatabaseID(), null);

		// updates the timetable entries
		db.update(TABLE_TIMETABLE_SESSION, values, KEY_SUBJECT_ID + " = "
				+ subject.getDatabaseID(), null);

		db.close();
	}

	/**
	 * returns an array list of the entries inside a wrapper class for a few
	 * extra methods
	 * 
	 * @return
	 */
	public SubjectArrayList getArrayOfSubjects(Context context) {

		return new SubjectArrayList(getSubjectArrayList(context, true));
	}

	/**
	 * fills an arraylist of all the subjects with pictures and IDs from the
	 * database and returns it
	 * 
	 * @return
	 */
	public ArrayList<SubjectEntry> getSubjectArrayList(Context context,
			boolean includeProtectedSubject) {

		SQLiteDatabase db = this.getReadableDatabase();

		ArrayList<SubjectEntry> subjectEntries = new ArrayList<SubjectEntry>();

		String whereClause = null;
		String[] whereArgs = null;

		// gets the id of the protected default subject
		if (!includeProtectedSubject) {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(context);

			long defaultID;
			do {
				defaultID = preferences.getLong(
						SettingsActivity.PREF_NO_SUBJECT_ID, -1L);

				if (defaultID == -1L) {
					createDefaultSubject(context);
				}
			} while (defaultID == -1L);

			whereClause = KEY_ID + " != ?";
			whereArgs = new String[] { "" + defaultID };

		}

		// creates a cursor for looking at the entries based on a query for the
		// id number passed to the method
		Cursor cursor = db.query(TABLE_SUBJECT, new String[] { KEY_ID,
				KEY_SUBJECT_NAME, KEY_SUBJECT_PICTURE }, whereClause,
				whereArgs, null, null, KEY_SUBJECT_NAME, null);

		// creates a spacer to start with
		subjectEntries.add(new SubjectEntry());

		if (cursor.moveToFirst()) {
			// flag for rounding the top of the first entry
			boolean roundTop = true;
			do {
				subjectEntries.add(new SubjectEntry(cursor.getInt(0), cursor
						.getString(1), cursor.getInt(2), roundTop, false));
				roundTop = false;
			} while (cursor.moveToNext());
		}
		// round the bottom
		subjectEntries.get(subjectEntries.size() - 1).setBottom(true);

		// creates a spacer at the end
		subjectEntries.add(new SubjectEntry());

		cursor.close();
		db.close();

		return subjectEntries;

	}

	public SubjectEntry getSubjectEntry(int subjectId) {
		SQLiteDatabase db = this.getReadableDatabase();

		// creates a cursor for looking at the entries based on a query for the
		// id number passed to the method
		Cursor cursor = db.query(TABLE_SUBJECT, new String[] { KEY_ID,
				KEY_SUBJECT_NAME, KEY_SUBJECT_PICTURE }, KEY_ID + "=?",
				new String[] { String.valueOf(subjectId) }, null, null, null,
				null);

		if (cursor != null)
			cursor.moveToFirst();

		SubjectEntry subjectEntry = new SubjectEntry(Integer.parseInt(cursor
				.getString(0)), cursor.getString(1), cursor.getInt(2));

		db.close();
		return subjectEntry;
	}

	public boolean isSubjectEmpty() {
		// helps with initial setup to see if the database needs populating
		String emptyQuery = "SELECT * FROM " + TABLE_SUBJECT;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(emptyQuery, null);
		boolean returnBool = !cursor.moveToFirst();
		db.close();
		cursor.close();
		return returnBool;
	}

	// Methods for the timetable entries
	// **********************************************************************************************************

	public boolean isTimetableEmpty() {
		// helps with initial setup to see if the database needs populating
		String emptyQuery = "SELECT * FROM " + TABLE_TIMETABLE_SESSION;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(emptyQuery, null);
		boolean returnBool = !cursor.moveToFirst();
		db.close();
		cursor.close();
		return returnBool;
	}

	/**
	 * used to check how many homework entries are assigned to a particular
	 * subject. Useful for checking before deleting a subject
	 * 
	 * @param subject
	 *            subject to check
	 * @return amount
	 */
	public int howManyTimetableAssignedToSubject(int databaseId) {

		SQLiteDatabase db = this.getReadableDatabase();

		// counter
		int howMany = 0;

		// get a list of the todo entries for the specified homework in order
		String selectQuery = "SELECT " + KEY_SUBJECT_ID + " FROM "
				+ TABLE_TIMETABLE_SESSION + " WHERE " + KEY_SUBJECT_ID + " = "
				+ databaseId;

		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				howMany++;

			} while (cursor.moveToNext());
		}
		db.close();

		return howMany;
	}

	public int addNewTimetableEvent(TimetableEntry newEntry) {
		// first create a new entry in the timetable session table
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_SESSION_NAME, newEntry.getSessionName());
		values.put(KEY_TUTOR, newEntry.getTutor());
		values.put(KEY_LOCATION, newEntry.getLocation());
		values.put(KEY_START_TIME, newEntry.getStartTime());
		values.put(KEY_END_TIME, newEntry.getEndTime());
		values.put(KEY_START_DATE, newEntry.getStartDate());
		values.put(KEY_END_DATE, newEntry.getEndDate());
		values.put(KEY_FREQUENCY, newEntry.getFreq().toString());
		values.put(KEY_NOTE, newEntry.getNote());
		values.put(KEY_SUBJECT_ID, newEntry.getSubjectID());
		values.put(KEY_ACTIVE_DAYS, newEntry.getActiveDaysAsString());
		values.put(KEY_COLOUR_ID, newEntry.getTimeTableColour());

		// Inserting Row
		int rowId = (int) db.insert(TABLE_TIMETABLE_SESSION, null, values);

		// then populate the timetable date table with info on when it occurs
		// gets the list of dates required
		ArrayList<Long> listOfDates = getAllInstancesOfTimetableDates(newEntry);

		ContentValues dateValues = new ContentValues();

		for (Long date : listOfDates) {
			dateValues.put(KEY_SESSION_ID, rowId);
			dateValues.put(KEY_DATE_LONG, date);

			db.insert(TABLE_TIMETABLE_DATE, null, dateValues);
			dateValues.clear();
		}

		db.close(); // closes database connection

		return rowId;
	}

	/**
	 * adds a new session without creating any dates. Returns the newly created
	 * id number.
	 * 
	 * @param entry
	 */
	public int addNewTimetableSession(TimetableEntry newEntry) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_SESSION_NAME, newEntry.getSessionName());
		values.put(KEY_TUTOR, newEntry.getTutor());
		values.put(KEY_LOCATION, newEntry.getLocation());
		values.put(KEY_START_TIME, newEntry.getStartTime());
		values.put(KEY_END_TIME, newEntry.getEndTime());
		values.put(KEY_START_DATE, newEntry.getStartDate());
		values.put(KEY_END_DATE, newEntry.getEndDate());
		values.put(KEY_FREQUENCY, newEntry.getFreq().toString());
		values.put(KEY_NOTE, newEntry.getNote());
		values.put(KEY_SUBJECT_ID, newEntry.getSubjectID());
		values.put(KEY_ACTIVE_DAYS, newEntry.getActiveDaysAsString());
		values.put(KEY_COLOUR_ID, newEntry.getTimeTableColour());

		// Inserting Row
		int rowId = (int) db.insert(TABLE_TIMETABLE_SESSION, null, values);

		return rowId;
	}

	/**
	 * creates the entry but removes any clashes with the list of ids given
	 * 
	 * @param newEntry
	 * @param clashes
	 * @return
	 */
	public int addNewTimetableEventMissingClashes(TimetableEntry newEntry,
			ArrayList<Long> clashes) {
		// first create a new entry in the timetable session table
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_SESSION_NAME, newEntry.getSessionName());
		values.put(KEY_TUTOR, newEntry.getTutor());
		values.put(KEY_LOCATION, newEntry.getLocation());
		values.put(KEY_START_TIME, newEntry.getStartTime());
		values.put(KEY_END_TIME, newEntry.getEndTime());
		values.put(KEY_START_DATE, newEntry.getStartDate());
		values.put(KEY_END_DATE, newEntry.getEndDate());
		values.put(KEY_FREQUENCY, newEntry.getFreq().toString());
		values.put(KEY_NOTE, newEntry.getNote());
		values.put(KEY_SUBJECT_ID, newEntry.getSubjectID());
		values.put(KEY_ACTIVE_DAYS, newEntry.getActiveDaysAsString());
		values.put(KEY_COLOUR_ID, newEntry.getTimeTableColour());

		// Inserting Row
		int rowId = (int) db.insert(TABLE_TIMETABLE_SESSION, null, values);

		// then populate the timetable date table with info on when it occurs
		// gets the list of dates required
		ArrayList<Long> listOfDates = getAllInstancesOfTimetableDatesMissingClashes(
				newEntry, clashes);

		ContentValues dateValues = new ContentValues();

		for (Long date : listOfDates) {
			dateValues.put(KEY_SESSION_ID, rowId);
			dateValues.put(KEY_DATE_LONG, date);

			db.insert(TABLE_TIMETABLE_DATE, null, dateValues);
			dateValues.clear();
		}

		db.close(); // closes database connection

		return rowId;
	}

	public ArrayList<Long> getAllInstancesOfTimetableDatesMissingClashes(
			TimetableEntry newEntry, ArrayList<Long> clashes) {

		// get the dates of the clashes
		SQLiteDatabase db = this.getReadableDatabase();

		String whereClause = " WHERE " + KEY_ID + " IN (";
		for (Long id : clashes) {

			if (clashes.indexOf(id) == clashes.size() - 1) {
				// last clause
				whereClause = whereClause + id;
			} else {
				whereClause = whereClause + id + ", ";
			}
		}
		whereClause = whereClause + ") ";

		String selectQuery = "SELECT " + KEY_DATE_LONG + " FROM "
				+ TABLE_TIMETABLE_DATE + whereClause + "ORDER BY "
				+ KEY_DATE_LONG;

		Cursor cursor = db.rawQuery(selectQuery, null);
		// because there is only allowed to be one instance of each timetable
		// entry on each day, if the entry is on the same day as an existing
		// entry that has been flagged as clashing it must collide and so can be
		// removed

		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();

		// all required dates with clashes still in them
		ArrayList<Long> dates = getAllInstancesOfTimetableDates(newEntry);
		// list of corrected dates to be returned
		ArrayList<Long> clashingDates = new ArrayList<Long>();

		if (cursor.moveToFirst()) {
			do {
				for (long date : dates) {
					if (onTheSameDay(date, cursor.getLong(0), cal1, cal2)) {
						// collision, add the date
						clashingDates.add(date);
					} else {
						// no collision, date is fine
					}
				}
			} while (cursor.moveToNext());
		}

		cursor.close();

		dates.removeAll(clashingDates);

		return dates;
	}

	/**
	 * checks to see if two times given as longs are on the same day. Used
	 * within an iterator so passing calendar instances saves it instantiating
	 * new ones each call
	 * 
	 * @param time1
	 * @param time2
	 * @param cal1
	 * @param cal2
	 * @return
	 */
	private boolean onTheSameDay(long time1, long time2, Calendar cal1,
			Calendar cal2) {
		cal1.setTimeInMillis(time1);
		cal2.setTimeInMillis(time2);

		boolean isTheSame = (cal1.get(Calendar.DAY_OF_YEAR) == cal2
				.get(Calendar.DAY_OF_YEAR))
				&& (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR));

		return isTheSame;
	}

	public ArrayList<Long> getAllInstancesOfTimetableDates(
			TimetableEntry newEntry) {
		return getAllInstancesOfTimetableDates(newEntry.getStartDate(),
				newEntry.getEndDate(), newEntry.getStartTime(),
				newEntry.getFreq(), newEntry.getActiveDays());
	}

	public ArrayList<Long> getAllInstancesOfTimetableDates(long startDate,
			long endDate, long startTime, TimetableEntry.FREQUENCY freq,
			boolean[] activeDays) {
		// possible frequencies:
		// NONE, WEEKLY, FORTNIGHTLY, ONE_OFF, DATE_OF_MONTH,
		// FIRST_OF_MONTH, LAST_OF_MONTH, SECOND_OF_MONTH, THIRD_OF_MONTH,
		// FOURTH_OF_MONTH
		ArrayList<Long> listOfDates = new ArrayList<Long>();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(startDate);

		// setting the startDate to the correct time helps with displaying the
		// entry properly
		Calendar startTimeCal = Calendar.getInstance();
		startTimeCal.setTimeInMillis(startTime);

		cal.set(Calendar.HOUR_OF_DAY, startTimeCal.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, startTimeCal.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, startTimeCal.get(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, startTimeCal.get(Calendar.MILLISECOND));

		// start date doesn't have to be an active day

		// -1 to put it in line with the array starting at 0
		int today;
		int count;
		int dayOfWeek;

		switch (freq) {
		case ONE_OFF:
			listOfDates.add(cal.getTimeInMillis());
			break;
		case WEEKLY:
			do {
				today = cal.get(Calendar.DAY_OF_WEEK) - 1;
				if (activeDays[today]) {
					listOfDates.add(cal.getTimeInMillis());
				}
				cal.add(Calendar.DAY_OF_WEEK, 1);
			} while (cal.getTimeInMillis() <= endDate);
			break;
		case FORTNIGHTLY:
			int currentWeek;
			do {
				today = cal.get(Calendar.DAY_OF_WEEK) - 1;
				currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
				if (activeDays[today]) {
					listOfDates.add(cal.getTimeInMillis());
				}
				cal.add(Calendar.DAY_OF_WEEK, 1);
				// check to see if we're at the end of the week, is so skip a
				// week
				if (currentWeek != cal.get(Calendar.WEEK_OF_YEAR)) {
					// roll over the week
					cal.add(Calendar.WEEK_OF_YEAR, 1);
				}
			} while (cal.getTimeInMillis() <= endDate);
			break;
		case DATE_OF_MONTH:
			// it is assumed that the date of month required is the start date
			do {
				listOfDates.add(cal.getTimeInMillis());
				cal.add(Calendar.MONTH, 1);
			} while (cal.getTimeInMillis() <= endDate);
			break;
		case FIRST_OF_MONTH:
			// for any of the following the activeDays should contain a single
			// positive entry
			count = 0;
		case SECOND_OF_MONTH:
			count = 1;
		case THIRD_OF_MONTH:
			count = 2;
		case FOURTH_OF_MONTH:
			count = 3;

			// get the day of week required
			dayOfWeek = 0;
			while (!activeDays[dayOfWeek]) {
				dayOfWeek++;
			}
			// offset for calendar starting on 1 rather than 0
			dayOfWeek++;

			// sets to the first of the month
			cal.set(Calendar.DATE, 1);

			// finds the required day
			do {
				today = cal.get(Calendar.DAY_OF_WEEK);
				if (today == dayOfWeek) {
					// get to the 1st, 2nd, 3rd or 4th of the month
					for (int i = 0; i < count; i++) {
						cal.add(Calendar.WEEK_OF_MONTH, 1);
					}
					// add the date to the list
					listOfDates.add(cal.getTimeInMillis());
					// reset it to the 1st of next month
					cal.add(Calendar.MONTH, 1);
					cal.set(Calendar.DATE, 1);
				} else {
					cal.add(Calendar.DAY_OF_WEEK, 1);
				}

			} while (cal.getTimeInMillis() <= endDate);

			break;

		case LAST_OF_MONTH:
			// get the day of week required
			dayOfWeek = 0;
			while (!activeDays[dayOfWeek]) {
				dayOfWeek++;
			}
			// offset for calendar starting on 1 rather than 0
			dayOfWeek++;

			do {
				// roll on to the next month
				cal.add(Calendar.MONTH, 1);
				// roll back from it until the day is found
				do {
					cal.add(Calendar.DATE, 1);
				} while (cal.get(Calendar.DAY_OF_WEEK) != dayOfWeek);

				// add the date to the list
				listOfDates.add(cal.getTimeInMillis());
				// roll onto next month
				cal.add(Calendar.MONTH, 1);
			} while (cal.getTimeInMillis() <= endDate);

			break;
		default:
			break;
		}

		return listOfDates;
	}

	/**
	 * this is a pretty expensive method but it is needed when making new
	 * timetable entries to see if anything clashes. It ignores already existing
	 * instances of the given entry.
	 * 
	 * @param entry
	 * @return
	 */
	public ArrayList<Long> getTimetableClashes(TimetableEntry entry) {

		// creates all of the dates needed for the new entry
		ArrayList<Long> events = getAllInstancesOfTimetableDates(entry);

		// array to return full of clashing events
		ArrayList<Long> clashingEvents = new ArrayList<Long>();

		// need the session length for collision detection
		long sessionLengthInMs = entry.getEndTime() - entry.getStartTime();

		SQLiteDatabase db = getReadableDatabase();

		// looks up the sessions on the same day as the entries

		String whereClause = " WHERE (";

		Calendar cal = Calendar.getInstance();
		boolean includeOR = true;
		for (Long event : events) {
			if (events.indexOf(event) == (events.size() - 1)) {
				includeOR = false;
			}

			whereClause = whereClause + "(" + TABLE_TIMETABLE_DATE + "."
					+ KEY_DATE_LONG + " BETWEEN " + getDayStartTime(event, cal)
					+ " AND " + getDayEndTime(event, cal) + ")"
					+ includeOr(includeOR);
		}

		// ignores instances of itself (used for editing)
		if (entry.getId() != TimetableEntry.EMPTY_INT) {
			whereClause = whereClause + "AND (" + TABLE_TIMETABLE_DATE + "."
					+ KEY_SESSION_ID + " != " + entry.getId() + ") ";
		}

		String selectQuery = "SELECT " + TABLE_TIMETABLE_DATE + "."
				+ KEY_DATE_LONG + ", " + TABLE_TIMETABLE_DATE + "." + KEY_ID
				+ ", " + TABLE_TIMETABLE_SESSION + "." + KEY_START_TIME + ", "
				+ TABLE_TIMETABLE_SESSION + "." + KEY_END_TIME + ", "
				+ TABLE_TIMETABLE_DATE + "." + KEY_SESSION_ID + " FROM "
				+ TABLE_TIMETABLE_DATE + " JOIN " + TABLE_TIMETABLE_SESSION
				+ " ON " + TABLE_TIMETABLE_SESSION + "." + KEY_ID + " = "
				+ TABLE_TIMETABLE_DATE + "." + KEY_SESSION_ID + whereClause
				+ "ORDER BY " + TABLE_TIMETABLE_DATE + "." + KEY_DATE_LONG;

		Cursor cursor = db.rawQuery(selectQuery, null);

		// now do collision detection on the returned queries
		// the session date is in the timetable dates table is set to the start
		// time of the session
		// just need to work out the length of the session

		// cursor:
		// 0 = date long
		// 1 = date id
		// 2 = session start time
		// 3 = session end time
		// 4 = session id
		if (cursor.moveToFirst()) {
			do {
				long storedSessionLength = getSessionLength(cursor.getLong(2),
						cursor.getLong(3));

				// check the collision
				if (doTimesCollide(cursor.getLong(0), storedSessionLength,
						events, sessionLengthInMs)) {
					clashingEvents.add(cursor.getLong(1));
				}

			} while (cursor.moveToNext());
		}

		// Return gives something detailed that can then be
		// used to remove clashing entries if required or at least give
		// information about what clashes
		return clashingEvents;
	}

	/**
	 * needed because the time element of the start and end time may be correct
	 * but the date portion might not be the same. The sessionDate is always set
	 * to the start time.
	 * 
	 * @param sessionDate
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	private long getSessionLength(long sessionDate, long endTime) {
		Calendar startCal = Calendar.getInstance();
		startCal.setTimeInMillis(sessionDate);

		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(endTime);

		endCal.set(Calendar.YEAR, startCal.get(Calendar.YEAR));
		endCal.set(Calendar.DAY_OF_YEAR, startCal.get(Calendar.DAY_OF_YEAR));

		return endCal.getTimeInMillis() - sessionDate;
	}

	/**
	 * this is a pretty expensive operation, especially when performed for lots
	 * of sessions! all parameters in milliseconds
	 * 
	 * @param session1Start
	 * @param session1Length
	 * @param events
	 * @param eventLength
	 * @return
	 */
	private boolean doTimesCollide(long session1Start, long session1Length,
			ArrayList<Long> events, long eventLength) {
		// TODO: there is a quick way of getting out of this because events is
		// in date order
		boolean doesCollide = false;
		for (Long eventStart : events) {

			if ((session1Start >= eventStart && session1Start < eventStart
					+ eventLength)
					|| (eventStart >= session1Start && eventStart < session1Start
							+ session1Length)) {
				// session 1 start time within session 2 OR session 2 start time
				// in session 1
				doesCollide = true;
			}
			// look to see if event time after the end of session 1.
			// If it is then break the loop.

			if (session1Start + session1Length < eventStart) {
				// collisions impossible after this point due to sorting by
				// date-time order
				break;
			}

		}
		return doesCollide;
	}

	private String getDayStartTime(Long event, Calendar cal) {
		cal.setTimeInMillis(event);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return new String("" + cal.getTimeInMillis());
	}

	private String getDayEndTime(Long event, Calendar cal) {
		cal.setTimeInMillis(event);
		cal.set(Calendar.HOUR_OF_DAY, 24);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);

		return new String("" + cal.getTimeInMillis());
	}

	private String includeOr(boolean includeOR) {
		String orString;

		if (includeOR) {
			orString = " OR ";
		} else {
			orString = ") ";
		}

		return orString;
	}

	/**
	 * used for simply updating the text details of a timetable entry, not the
	 * times or dates
	 * 
	 * @param updateEvent
	 */
	public void updateTimetableEntryDetails(TimetableEntry entry) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		// subject
		values.put(KEY_SUBJECT_ID, entry.getSubjectID());
		// title
		if (entry.getSessionName() != null)
			values.put(KEY_SESSION_NAME, entry.getSessionName());
		// tutor
		if (entry.getTutor() != null)
			values.put(KEY_TUTOR, entry.getTutor());
		// location
		if (entry.getLocation() != null)
			values.put(KEY_LOCATION, entry.getLocation());
		// colour
		values.put(KEY_COLOUR_ID, entry.getTimeTableColour());

		try {
			db.update(TABLE_TIMETABLE_SESSION, values,
					KEY_ID + " = " + entry.getId(), null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	/**
	 * swaps the old session id number for a new session id number for a entry
	 * in timetable dates table on a specific date
	 * 
	 * @param oldId
	 * @param newId
	 * @param sessionDate
	 */
	public void changeTimetableDateOwner(int oldId, int newId, long sessionDate) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_SESSION_ID, newId);

		try {
			db.update(TABLE_TIMETABLE_DATE, values, "(" + KEY_SESSION_ID
					+ " = " + oldId + ") AND (" + KEY_DATE_LONG + " = "
					+ sessionDate + ")", null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	/**
	 * used for simply updating the text details of a timetable entry, not the
	 * dates
	 * 
	 * @param updateEvent
	 */
	public void updateTimetableEntryDetailsAndTimes(TimetableEntry entry) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		// subject
		values.put(KEY_SUBJECT_ID, entry.getSubjectID());
		// title
		if (entry.getSessionName() != null)
			values.put(KEY_SUBJECT_NAME, entry.getSessionName());
		// tutor
		if (entry.getTutor() != null)
			values.put(KEY_TUTOR, entry.getTutor());
		// location
		if (entry.getLocation() != null)
			values.put(KEY_LOCATION, entry.getLocation());
		// colour
		values.put(KEY_COLOUR_ID, entry.getTimeTableColour());
		// start time
		values.put(KEY_START_TIME, entry.getStartTime());
		// end time
		values.put(KEY_END_TIME, entry.getEndTime());

		try {
			db.update(TABLE_TIMETABLE_SESSION, values, "" + KEY_ID + " = "
					+ entry.getId(), null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	/**
	 * deletes a single session date
	 * 
	 * @param sessionId
	 * @param startTime
	 */
	public void deleteTimetableSessionDateByTimeAndSessionID(int sessionId,
			long startTime) {
		SQLiteDatabase db = this.getWritableDatabase();

		String whereClause = "(" + KEY_SESSION_ID + " = " + sessionId
				+ ") AND (" + KEY_DATE_LONG + " = " + startTime + ")";

		try {
			db.delete(TABLE_TIMETABLE_DATE, whereClause, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	/**
	 * passing a list of the ids that need deleting wipes them out. Be warned
	 * that this may leave sessions without any dates and so uneditable.
	 * 
	 * @param dateIds
	 */
	public void deleteSessionDatesByDateId(ArrayList<Long> dateIds) {
		SQLiteDatabase db = this.getWritableDatabase();

		String whereClause = KEY_ID + " IN (";
		for (Long id : dateIds) {

			if (dateIds.indexOf(id) == dateIds.size() - 1) {
				// last clause
				whereClause = whereClause + id;
			} else {
				whereClause = whereClause + id + ", ";
			}
		}
		whereClause = whereClause + ")";

		try {
			db.delete(TABLE_TIMETABLE_DATE, whereClause, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	/**
	 * removes all trace of this entry from both the session and date tables
	 * 
	 * @param entry
	 */
	public void deleteTimeTableEntry(TimetableEntry entry) {
		SQLiteDatabase db = this.getWritableDatabase();

		// delete the entry from the session table
		String whereClauseSession = KEY_ID + " = " + entry.getId();

		try {
			db.delete(TABLE_TIMETABLE_SESSION, whereClauseSession, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// delete the same entry from the date table
		String whereClauseDate = KEY_SESSION_ID + " = " + entry.getId();

		try {
			db.delete(TABLE_TIMETABLE_DATE, whereClauseDate, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	/**
	 * returns the entry populated in full with all relevant details for a given
	 * database id. if it can't find the entry it will return a new empty entry
	 */
	public TimetableEntry getTimetableEntryById(int id) {

		TimetableEntry entry;
		SQLiteDatabase db = this.getReadableDatabase();

		String query = new String("SELECT " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_ID + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_SESSION_NAME + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_TUTOR + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_LOCATION + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_NOTE + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_SUBJECT_ID + ", " + TABLE_SUBJECT + "."
				+ KEY_SUBJECT_PICTURE + ", " + TABLE_SUBJECT + "."
				+ KEY_SUBJECT_NAME + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_START_TIME + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_END_TIME + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_START_DATE + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_END_DATE + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_FREQUENCY + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_ACTIVE_DAYS + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_COLOUR_ID + ", " + TABLE_TIMETABLE_DATE + "."
				+ KEY_DATE_LONG + " FROM " + TABLE_TIMETABLE_DATE + " JOIN "
				+ TABLE_TIMETABLE_SESSION + " ON " + TABLE_TIMETABLE_SESSION
				+ "." + KEY_ID + " = " + TABLE_TIMETABLE_DATE + "."
				+ KEY_SESSION_ID + " JOIN " + TABLE_SUBJECT + " ON "
				+ TABLE_TIMETABLE_SESSION + "." + KEY_SUBJECT_ID + " = "
				+ TABLE_SUBJECT + "." + KEY_ID + " WHERE "
				+ TABLE_TIMETABLE_SESSION + "." + KEY_ID + " = " + id);

		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			entry = new TimetableEntry(cursor.getInt(0), // id
					cursor.getString(1), // sessionName
					cursor.getString(2), // tutor
					cursor.getString(3), // location
					cursor.getString(4), // note
					cursor.getInt(5), // subjectID
					cursor.getInt(6), // subjectImageResourceID
					cursor.getString(7), // subjectName
					cursor.getLong(8), // startTime
					cursor.getLong(9), // endTime
					cursor.getLong(10), // startDate
					cursor.getLong(11), // endDate
					cursor.getLong(15), // sessionDate
					cursor.getString(12), // enumFrequencyAsString
					cursor.getString(13), // activeDays
					cursor.getInt(14) // timeTableColour
			);
		} else {
			entry = new TimetableEntry(TimetableEntry.TYPE.ENTRY);
		}

		return entry;
	}

	/**
	 * Returns the list for use in the timetable list activity, any from the
	 * same week as the given time
	 * 
	 * @param includeListItems
	 *            flag - true for list item with all added bits, false for
	 *            timetable view
	 */
	public ArrayList<TimetableEntry> getTimetableListForWeekFromCurrentTime(
			long currentTime, Context context, boolean includeListItems) {
		return getTimetableListForWeek(getTimeAtStartOfWeek(currentTime),
				context, includeListItems);
	}

	/**
	 * Returns the list for use in the timetable list activity, any from the
	 * same week as the given time
	 * 
	 * @param includeListItems
	 *            flag - true for list item with all added bits, false for
	 *            timetable view
	 */
	public ArrayList<TimetableEntry> getTimetableListForWeekFromCurrentTime(
			Context context, boolean includeListItems) {
		Calendar cal = Calendar.getInstance();

		return getTimetableListForWeek(
				getTimeAtStartOfWeek(cal.getTimeInMillis()), context,
				includeListItems);
	}

	/**
	 * Returns the list for use in the timetable list activity
	 * 
	 * @param includeListItems
	 *            flag - true for list item with all added bits, false for
	 *            timetable view
	 * 
	 * @param weekStartTime
	 *            - the time at the start of the week in question. Use helper
	 *            method getTimeAtStartOfWeek().
	 * @return
	 */
	public ArrayList<TimetableEntry> getTimetableListForWeek(
			long weekStartTime, Context context, boolean includeListItems) {

		ArrayList<TimetableEntry> timetable = new ArrayList<TimetableEntry>();

		// get the limit
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(weekStartTime);
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		long weekEndTime = cal.getTimeInMillis() - 1L;

		// query the database for all entries with a start time in between the
		// given weekStartTime and a week later
		SQLiteDatabase db = this.getReadableDatabase();

		String query = new String("SELECT " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_ID + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_SESSION_NAME + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_TUTOR + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_LOCATION + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_NOTE + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_SUBJECT_ID + ", " + TABLE_SUBJECT + "."
				+ KEY_SUBJECT_PICTURE + ", " + TABLE_SUBJECT + "."
				+ KEY_SUBJECT_NAME + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_START_TIME + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_END_TIME + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_START_DATE + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_END_DATE + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_FREQUENCY + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_ACTIVE_DAYS + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_COLOUR_ID + ", " + TABLE_TIMETABLE_DATE + "."
				+ KEY_DATE_LONG + " FROM " + TABLE_TIMETABLE_DATE + " JOIN "
				+ TABLE_TIMETABLE_SESSION + " ON " + TABLE_TIMETABLE_SESSION
				+ "." + KEY_ID + " = " + TABLE_TIMETABLE_DATE + "."
				+ KEY_SESSION_ID + " JOIN " + TABLE_SUBJECT + " ON "
				+ TABLE_TIMETABLE_SESSION + "." + KEY_SUBJECT_ID + " = "
				+ TABLE_SUBJECT + "." + KEY_ID + " WHERE "
				+ TABLE_TIMETABLE_DATE + "." + KEY_DATE_LONG + " >= "
				+ weekStartTime + " AND " + TABLE_TIMETABLE_DATE + "."
				+ KEY_DATE_LONG + " <= " + weekEndTime + " ORDER BY "
				+ TABLE_TIMETABLE_DATE + "." + KEY_DATE_LONG);

		int currentDay = -1;
		int newDay = -1;
		boolean roundCorners = false;

		Cursor cursor = db.rawQuery(query, null);

		// puts the week and date in
		if (includeListItems) {
			// add a spacer then a title first
			// add spacer
			timetable.add(new TimetableEntry(TimetableEntry.TYPE.SPACER, null));
			// add title
			timetable.add(new TimetableEntry(TimetableEntry.TYPE.WEEK_DIVIDER,
					context.getResources().getString(R.string.week)
							+ " "
							+ HumanReadableDate
									.getDateTimeTableList(weekStartTime)));
		}

		if (cursor.moveToFirst()) {
			do {

				roundCorners = false;

				TimetableEntry tt = new TimetableEntry(cursor.getInt(0), // id
						cursor.getString(1), // sessionName
						cursor.getString(2), // tutor
						cursor.getString(3), // location
						cursor.getString(4), // note
						cursor.getInt(5), // subjectID
						cursor.getInt(6), // subjectImageResourceID
						cursor.getString(7), // subjectName
						cursor.getLong(8), // startTime
						cursor.getLong(9), // endTime
						cursor.getLong(10), // startDate
						cursor.getLong(11), // endDate
						cursor.getLong(15), // sessionDate
						cursor.getString(12), // enumFrequencyAsString
						cursor.getString(13), // activeDays
						cursor.getInt(14) // timeTableColour
				);

				// check for new title needed
				newDay = tt.getDay();
				if (newDay != currentDay && includeListItems) {
					// new title needed
					roundCorners = true;
					currentDay = newDay;
					// add spacer
					timetable.add(new TimetableEntry(
							TimetableEntry.TYPE.SPACER, null));
					// add title
					timetable
							.add(new TimetableEntry(TimetableEntry.TYPE.TITLE,
									HumanReadableDate.getDateString(cursor
											.getLong(15))));
				}

				// round previous corners
				if (roundCorners) {
					int entryToRound = timetable.size() - 3;
					if (entryToRound >= 0) {
						timetable.get(entryToRound).setEndOfSection(true);
					}
				}

				timetable.add(tt);
			} while (cursor.moveToNext());

			// round off the last entry
			timetable.get(timetable.size() - 1).setEndOfSection(true);
		}

		if (includeListItems) {
			// add spacer
			timetable.add(new TimetableEntry(TimetableEntry.TYPE.SPACER, null));
		}

		cursor.close();
		db.close();

		return timetable;
	}

	/**
	 * Returns the list for use in the timetable list activity
	 * 
	 * @param weekStartTime
	 *            - the time at the start of the week in question. Use helper
	 *            method getTimeAtStartOfWeek().
	 * @return
	 */
	public ArrayList<TimetableEntry> getTimetableAll() {

		ArrayList<TimetableEntry> timetable = new ArrayList<TimetableEntry>();

		// query the database for all entries with a start time in between the
		// given weekStartTime and a week later
		SQLiteDatabase db = this.getReadableDatabase();

		String query = new String("SELECT " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_ID + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_SESSION_NAME + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_TUTOR + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_LOCATION + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_NOTE + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_SUBJECT_ID + ", " + TABLE_SUBJECT + "."
				+ KEY_SUBJECT_PICTURE + ", " + TABLE_SUBJECT + "."
				+ KEY_SUBJECT_NAME + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_START_TIME + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_END_TIME + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_START_DATE + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_END_DATE + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_FREQUENCY + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_ACTIVE_DAYS + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_COLOUR_ID + ", " + TABLE_TIMETABLE_DATE + "."
				+ KEY_DATE_LONG + " FROM " + TABLE_TIMETABLE_DATE + " JOIN "
				+ TABLE_TIMETABLE_SESSION + " ON " + TABLE_TIMETABLE_SESSION
				+ "." + KEY_ID + " = " + TABLE_TIMETABLE_DATE + "."
				+ KEY_SESSION_ID + " JOIN " + TABLE_SUBJECT + " ON "
				+ TABLE_TIMETABLE_SESSION + "." + KEY_SUBJECT_ID + " = "
				+ TABLE_SUBJECT + "." + KEY_ID + " ORDER BY "
				+ TABLE_TIMETABLE_DATE + "." + KEY_DATE_LONG);

		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			do {
				timetable.add(new TimetableEntry(cursor.getInt(0), // id
						cursor.getString(1), // sessionName
						cursor.getString(2), // tutor
						cursor.getString(3), // location
						cursor.getString(4), // note
						cursor.getInt(5), // subjectID
						cursor.getInt(6), // subjectImageResourceID
						cursor.getString(7), // subjectName
						cursor.getLong(8), // startTime
						cursor.getLong(9), // endTime
						cursor.getLong(10), // startDate
						cursor.getLong(11), // endDate
						cursor.getLong(15), // sessionDate
						cursor.getString(12), // enumFrequencyAsString
						cursor.getString(13), // activeDays
						cursor.getInt(14) // TimeTableColour
						));
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();

		return timetable;
	}

	/**
	 * DEBUG METHOD
	 */
	public void logAllTimetableEntries() {
		// query the database for all entries with a start time in between the
		// given weekStartTime and a week later
		SQLiteDatabase db = this.getReadableDatabase();

		String query = new String("SELECT " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_ID + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_SESSION_NAME + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_TUTOR + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_LOCATION + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_NOTE + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_SUBJECT_ID + ", " + TABLE_SUBJECT + "."
				+ KEY_SUBJECT_PICTURE + ", " + TABLE_SUBJECT + "."
				+ KEY_SUBJECT_NAME + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_START_TIME + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_END_TIME + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_START_DATE + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_END_DATE + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_FREQUENCY + ", " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_ACTIVE_DAYS + ", " + TABLE_TIMETABLE_DATE + "."
				+ KEY_DATE_LONG + " FROM " + TABLE_TIMETABLE_SESSION + " JOIN "
				+ TABLE_TIMETABLE_DATE + " ON " + TABLE_TIMETABLE_SESSION + "."
				+ KEY_ID + " = " + TABLE_TIMETABLE_DATE + "." + KEY_SESSION_ID
				+ " JOIN " + TABLE_SUBJECT + " ON " + TABLE_TIMETABLE_SESSION
				+ "." + KEY_SUBJECT_ID + " = " + TABLE_SUBJECT + "." + KEY_ID);

		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			do {
				Log.d("in logAllTimetableEntries", "" + cursor.getInt(0));
				Log.d("in logAllTimetableEntries", "" + cursor.getString(1));
				Log.d("in logAllTimetableEntries", "" + cursor.getString(2));
				Log.d("in logAllTimetableEntries", "" + cursor.getString(3));
				Log.d("in logAllTimetableEntries", "" + cursor.getString(4));
				Log.d("in logAllTimetableEntries", "" + cursor.getInt(5));
				Log.d("in logAllTimetableEntries", "" + cursor.getInt(6));
				Log.d("in logAllTimetableEntries", "" + cursor.getString(7));
				Log.d("in logAllTimetableEntries", "" + cursor.getLong(8));
				Log.d("in logAllTimetableEntries", "" + cursor.getLong(9));
				Log.d("in logAllTimetableEntries", "" + cursor.getLong(10));
				Log.d("in logAllTimetableEntries", "" + cursor.getLong(11));
				Log.d("in logAllTimetableEntries", "" + cursor.getString(12));
				Log.d("in logAllTimetableEntries", "" + cursor.getString(13));
				Log.d("in logAllTimetableEntries", "" + cursor.getLong(14));

				// id
				// sessionName
				// tutor
				// location
				// note
				// subjectID
				// subjectImageResourceID
				// subjectName
				// startTime
				// endTime
				// startDate
				// endDate
				// enumFrequencyAsString
				// activeDays
				// sessionDate

			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
	}

	// Methods for the general database use

	// Methods for Main Menu
	// **********************************************************************************************************

	/**
	 * populates an array list for display on the main menu
	 * 
	 * @return
	 */
	public ArrayList<MainMenuItem> getMainMenuArrayList(Context context) {
		ArrayList<MainMenuItem> items = new ArrayList<MainMenuItem>();
		// add spacer
		items.add(new MainMenuItem());
		// add timetable
		String timeTableDetails = new String(context.getResources().getString(
				R.string.main_menu_timetable_details));
		int sessionsThisWeek = getNumberOfTimetableSessionsThisWeek();
		if (sessionsThisWeek == 1) {
			timeTableDetails = timeTableDetails
					+ " "
					+ sessionsThisWeek
					+ " "
					+ context.getResources().getString(
							R.string.main_menu_timetable_details_single);
		} else {
			timeTableDetails = timeTableDetails
					+ " "
					+ sessionsThisWeek
					+ " "
					+ context.getResources().getString(
							R.string.main_menu_timetable_details_plural);
		}

		items.add(new MainMenuItem(context.getResources().getString(
				R.string.main_menu_timetable_title), timeTableDetails,
				MENU_TYPE.TIMETABLE));
		// add spacer
		items.add(new MainMenuItem());
		// add homework diary
		String homeworkDetails = new String(context.getResources().getString(
				R.string.main_menu_homework_details));
		int homeworkThisWeek = getNumberOfHomeworkDueInSevenDays();
		if (homeworkThisWeek == 1) {
			homeworkDetails = homeworkDetails
					+ " "
					+ homeworkThisWeek
					+ " "
					+ context.getResources().getString(
							R.string.main_menu_homework_details_single);
		} else {
			homeworkDetails = homeworkDetails
					+ " "
					+ homeworkThisWeek
					+ " "
					+ context.getResources().getString(
							R.string.main_menu_homework_details_plural);
		}

		items.add(new MainMenuItem(context.getResources().getString(
				R.string.main_menu_homework_title), homeworkDetails,
				MENU_TYPE.HOMEWORK));

		// add spacer
		items.add(new MainMenuItem());
		// add subject editor
		String subjectDetails = new String(context.getResources().getString(
				R.string.main_menu_subject_details));
		int subjects = getNumberOfSubjects();
		if (subjects == 1) {
			subjectDetails = subjectDetails
					+ " "
					+ subjects
					+ " "
					+ context.getResources().getString(
							R.string.main_menu_subject_details_single);
		} else {
			subjectDetails = subjectDetails
					+ " "
					+ subjects
					+ " "
					+ context.getResources().getString(
							R.string.main_menu_subject_details_plural);
		}

		items.add(new MainMenuItem(context.getResources().getString(
				R.string.main_menu_subject_title), subjectDetails,
				MENU_TYPE.SUBJECT));

		// add spacer
		items.add(new MainMenuItem());

		return items;
	}

	private int getNumberOfTimetableSessionsThisWeek() {
		// TODO: this
		// get the upper and low time bounds
		Calendar cal = Calendar.getInstance();
		long weekStartTime = getTimeAtStartOfWeek(cal.getTimeInMillis());
		// get the limit
		cal.setTimeInMillis(weekStartTime);
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		long weekEndTime = cal.getTimeInMillis() - 1L;

		// query the database for all entries with a start time in between the
		// given weekStartTime and a week later
		SQLiteDatabase db = this.getReadableDatabase();

		String query = new String("SELECT " + KEY_DATE_LONG + " FROM "
				+ TABLE_TIMETABLE_DATE + " WHERE " + KEY_DATE_LONG + " >= "
				+ weekStartTime + " AND " + KEY_DATE_LONG + " <= "
				+ weekEndTime);

		Cursor cursor = db.rawQuery(query, null);

		int sessions = 0;
		if (cursor.moveToFirst()) {
			do {
				sessions++;
			} while (cursor.moveToNext());

		}

		cursor.close();
		return sessions;
	}

	private int getNumberOfHomeworkDueInSevenDays() {

		// get the upper and low time bounds
		// start at the beginning of today
		Calendar cal = Calendar.getInstance();
		long startTime = getTimeAtStartOfDay(cal.getTimeInMillis());
		// get the limit
		cal.setTimeInMillis(startTime);
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		long endTime = cal.getTimeInMillis() - 1L;

		// get the titles from the database
		String titlesQuery = "SELECT " + KEY_DUE_DATE + " FROM "
				+ TABLE_HOMEWORK + " WHERE " + KEY_DUE_DATE + " >= "
				+ startTime + " AND " + KEY_DUE_DATE + " <= " + endTime;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(titlesQuery, null);

		// loop through the cursor to get all the entries into the array list
		int homeworks = 0;
		if (cursor.moveToFirst()) {
			do {
				homeworks++;
			} while (cursor.moveToNext());
		}

		cursor.close();

		return homeworks;
	}

	private int getNumberOfSubjects() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_SUBJECT, new String[] { KEY_ID }, null,
				null, null, null, null, null);

		// set to -1 to account for the default, permanent subject
		int subjects = -1;

		if (cursor.moveToFirst()) {
			do {
				subjects++;
			} while (cursor.moveToNext());
		}

		cursor.close();

		return subjects;
	}

	// Methods for Notifications
	// **********************************************************************************************************

	/**
	 * returns a list of homework due tomorrow or null if none are due
	 * 
	 * @return
	 */
	public ArrayList<NotificationEntry> getTomorrowsHomework() {
		// needs the following:
		// title
		// subject
		// subject image
		// is finished boolean
		// due date and is reminder set not returned, just used in query

		// get the value of the start and end of tomorrow in milliseconds
		long minDate;
		long maxDate;
		ArrayList<NotificationEntry> homework = new ArrayList<NotificationEntry>();

		Calendar cal = Calendar.getInstance();

		cal.roll(Calendar.DAY_OF_WEEK, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);

		minDate = cal.getTimeInMillis();

		SQLiteDatabase db = this.getReadableDatabase();

		cal.roll(Calendar.DAY_OF_WEEK, 1);
		cal.roll(Calendar.MILLISECOND, -1);

		maxDate = cal.getTimeInMillis();

		// query the database to get the cursor
		String selectQuery = "SELECT " + TABLE_HOMEWORK + "."
				+ KEY_HOMEWORK_TITLE + ", " + TABLE_SUBJECT + "."
				+ KEY_SUBJECT_NAME + ", " + TABLE_HOMEWORK + "."
				+ KEY_IS_COMPLETED + " FROM " + TABLE_HOMEWORK + " JOIN "
				+ TABLE_SUBJECT + " ON " + TABLE_HOMEWORK + "."
				+ KEY_SUBJECT_ID + " = " + TABLE_SUBJECT + "." + KEY_ID
				+ " WHERE " + TABLE_HOMEWORK + "." + KEY_IS_REMINDER_SET
				+ " = 1 AND " + TABLE_HOMEWORK + "." + KEY_DUE_DATE
				+ " BETWEEN " + minDate + " AND " + maxDate;

		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				homework.add(new NotificationEntry(cursor.getString(0), cursor
						.getString(1), cursor.getInt(2) == 1));

			} while (cursor.moveToNext());

		}
		db.close();
		cursor.close();

		return homework;
	}

	/**
	 * this method returns the time at the beginning of a week (starting on
	 * Sunday) for a given time during the week
	 * 
	 * @param timeInWeek
	 * @return
	 */
	public long getTimeAtStartOfWeek(long timeInWeek) {
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.SUNDAY);
		cal.setTimeInMillis(timeInWeek);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTimeInMillis();
	}

	/**
	 * returns the calendar time at the very start of the day that the argument
	 * belongs to
	 * 
	 * @param timeInWeek
	 * @return
	 */
	public long getTimeAtStartOfDay(long timeInWeek) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeInWeek);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTimeInMillis();
	}

	/**
	 * Deletes the entire database... careful now!
	 * 
	 * @param context
	 */
	public void deleteDatabase(Context context) {
		context.deleteDatabase(DATABASE_NAME);
	}
}
