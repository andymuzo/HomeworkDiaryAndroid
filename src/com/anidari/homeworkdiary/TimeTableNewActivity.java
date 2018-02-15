package com.anidari.homeworkdiary;

import java.util.ArrayList;
import java.util.Calendar;

import com.anidari.homeworkdiary.TimetableEntry.FREQUENCY;
import com.anidari.homeworkdiary.TimetableEntry.TYPE;
import com.anromus.homeworkdiary.R;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.RadioButton;

public class TimeTableNewActivity extends Activity implements OnClickListener {

	public final static String DEFAULT_START_DATE = "DEFAULT_START_DATE";
	public final static String DEFAULT_END_DATE = "DEFAULT_END_DATE";
	public final static String DEFAULT_SESSION_FREQENCY = "DEFAULT_SESSION_FREQUENCY";
	public final static String DEFAULT_SESSION_DURATION = "DEFAULT_SESSION_DURATION";
	public final static String LAST_TIMETABLE_COLOUR = "LAST_TIMETABLE_COLOUR";

	private TimetableEntry entry;
	// rows for clicking on and setting times and dates
	TableRow rowStartTime;
	TableRow rowEndTime;
	TableRow rowStartDate;
	TableRow rowEndDate;
	TableRow rowDateOfMonth;
	TableRow rowFrequency;

	TableRow rowEditTitle;
	DaySelecterLayout daySelecter;
	ColourPickerLayout colourPicker;

	TextView txtSelectTextTitle;
	View txtSelectTextTitleUnderline;
	TextView txtSessionTimeTitle;
	View sessionTimeTitleUnderline;
	TextView txtSessionDaysTitle;
	View sessionDaysTitleUnderline;

	TextView txtStartTime;
	TextView txtEndTime;
	TextView txtStartDate;
	TextView txtEndDate;
	TextView txtDateOfMonth;
	TextView txtStartDateTitle;

	EditText editTitle;
	EditText editWhere;
	EditText editWho;
	EditText editNotes;

	DatabaseHandler db = new DatabaseHandler(this);
	SubjectArrayList subjectEntries;
	Spinner subjectSpinner;
	String[] subjectDisplay;
	Spinner frequencySpinner;
	String[] frequencyDisplay;

	Button editTimeAndDate;
	Button setButton;
	Button cancelButton;

	int dateOfMonth;

	boolean newEntry; // flag used to differentiate between new entry and
						// editing existing entry
	int databaseID; // the id of the entry currently being edited
	boolean timesEdited; // flag used when editing an entry's times
	boolean datesEdited; // flag used when editing an entry's dates
	boolean startDateEdited;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.timetable_new_entry);

		// set the on click listener for the appropriate rows
		setupRows();

		setupDaySelecter();

		setupColourPicker();
		
		setupTextViews();

		setupEditTexts();

		setupSubjectSpinner();

		setupFrequencySpinner();

		setupButtons();

		setupEntry();

		setupInitialValues();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_delete) {
			// create delete options
			createDeleteDialog();
		}
		return true;
	}

	private void setupRows() {
		rowStartTime = (TableRow) this.findViewById(R.id.row_start_time);
		rowEndTime = (TableRow) this.findViewById(R.id.row_end_time);
		rowStartDate = (TableRow) this.findViewById(R.id.row_start_date);
		rowEndDate = (TableRow) this.findViewById(R.id.row_end_date);
		rowDateOfMonth = (TableRow) this.findViewById(R.id.row_date_of_month);
		rowEditTitle = (TableRow) this.findViewById(R.id.row_edit_title);
		rowFrequency = (TableRow) this.findViewById(R.id.row_date_frequency);

		rowStartTime.setOnClickListener(this);
		rowEndTime.setOnClickListener(this);
		rowStartDate.setOnClickListener(this);
		rowEndDate.setOnClickListener(this);
		rowDateOfMonth.setOnClickListener(this);
	}

	private void setupDaySelecter() {
		daySelecter = (DaySelecterLayout) this.findViewById(R.id.day_selecter);
	}
	
	private void setupColourPicker() {
		colourPicker = (ColourPickerLayout) this.findViewById(R.id.colour_picker);
	}

	private void setupTextViews() {
		txtStartTime = (TextView) this.findViewById(R.id.txt_start_time);
		txtEndTime = (TextView) this.findViewById(R.id.txt_end_time);
		txtStartDate = (TextView) this.findViewById(R.id.txt_start_date);
		txtEndDate = (TextView) this.findViewById(R.id.txt_end_date);
		txtDateOfMonth = (TextView) this.findViewById(R.id.txt_date_of_month);
		txtSelectTextTitle = (TextView) this
				.findViewById(R.id.txt_session_dates);
		txtSelectTextTitleUnderline = (View) this
				.findViewById(R.id.session_dates_underline);
		txtStartDateTitle = (TextView) this
				.findViewById(R.id.txt_start_date_title);
		txtSessionTimeTitle = (TextView) this
				.findViewById(R.id.txt_session_time_title);
		sessionTimeTitleUnderline = (View) this
				.findViewById(R.id.session_time_title_underline);
		txtSessionDaysTitle = (TextView) this
				.findViewById(R.id.txt_session_days_title);
		sessionDaysTitleUnderline = (View) this
				.findViewById(R.id.session_days_title_underline);
	}

	private void setupEditTexts() {
		editTitle = (EditText) this.findViewById(R.id.edtxt_title);
		editWhere = (EditText) this.findViewById(R.id.edtxt_where);
		editWho = (EditText) this.findViewById(R.id.edtxt_with);
		editNotes = (EditText) this.findViewById(R.id.edtxt_notes);
	}

	private void setupSubjectSpinner() {
		// set up the spinner
		subjectSpinner = (Spinner) findViewById(R.id.spn_subject_list);

		// create the array adapter for it
		subjectEntries = db.getArrayOfSubjects(this);

		subjectDisplay = subjectEntries.getStringsOfHomework();

		// sets the adapter for the spinner
		ArrayAdapter<String> subjectAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, subjectDisplay);
		subjectAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		subjectSpinner.setAdapter(subjectAdapter);

		subjectSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (subjectDisplay[position].equals(getResources().getString(
						R.string.no_subject_set))) {
					// change the display to make visible the title edit text
					// view
					showTitleEditText(true);
				} else {
					showTitleEditText(false);
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// do nothing

			}

		});
	}

	private void setupFrequencySpinner() {
		// set up the spinner
		frequencySpinner = (Spinner) findViewById(R.id.spn_frequency);

		frequencyDisplay = getResources().getStringArray(R.array.tt_frequency);

		// sets the adapter for the spinner
		ArrayAdapter<String> subjectAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, frequencyDisplay);
		subjectAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		frequencySpinner.setAdapter(subjectAdapter);

		frequencySpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						updateFrequencyDisplayOptions(position);

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// do nothing

					}

				});
	}

	private void setupButtons() {
		setButton = (Button) this.findViewById(R.id.btn_confirm);
		cancelButton = (Button) this.findViewById(R.id.btn_cancel);
		editTimeAndDate = (Button) this
				.findViewById(R.id.btn_edit_time_and_date);

		setButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		editTimeAndDate.setOnClickListener(this);
	}

	private void setupEntry() {
		// new entry flags this activity for how it handles the setup and output
		newEntry = ((Integer) this.getIntent().getExtras()
				.get(TimeTableActivity.START_TYPE))
				.equals(TimeTableActivity.ADD_NEW_TIME_TABLE_ENTRY);
		if (newEntry) {
			databaseID = TimetableEntry.EMPTY_INT;
			entry = new TimetableEntry(TYPE.ENTRY);
		} else {
			// get the entry from the database using the passed ID
			databaseID = (Integer) this.getIntent().getExtras()
					.get(TimeTableActivity.DATABASE_ID);
			entry = db.getTimetableEntryById(databaseID);
		}
	}

	private void setupInitialValues() {
		// depends on whether making new or editing existing
		if (newEntry) {
			// frequency spinner
			// sets to one-off initially, get this from settings in future
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(this);
			
			// insert the default values if they exist
			if (preferences.contains(DEFAULT_SESSION_FREQENCY)) {
				FREQUENCY defaultFreq = TimetableEntry.FREQUENCY
						.valueOf(preferences.getString(
								DEFAULT_SESSION_FREQENCY, null));
				setFreqSpinnerFromEnum(defaultFreq);
				entry.setFreq(defaultFreq);
			} else {
				frequencySpinner.setSelection(0);
				updateFrequencyDisplayOptions(0);
			}
			if (preferences.contains(DEFAULT_START_DATE)) {
				setStartDate(preferences.getLong(DEFAULT_START_DATE, 0L));
			}

			if (preferences.contains(DEFAULT_END_DATE)) {
				setEndDate(preferences.getLong(DEFAULT_END_DATE, 0L));
			}
			
			colourPicker.select(preferences.getInt((LAST_TIMETABLE_COLOUR + 1), 0));

			dateOfMonth = TimetableEntry.EMPTY_INT;
			editTimeAndDate.setVisibility(View.GONE);
			datesEdited = true;
			timesEdited = true;
			startDateEdited = true;
		} else {
			// initially hide the times and date edits to prevent changes unless
			// really needed
			hideTimesAndDates();
			// populate all of the fields with the data currently held in the
			// entry
			subjectSpinner.setSelection(subjectEntries.getPositionById(entry
					.getSubjectID()));
			editTitle.setText(entry.getSessionName());
			editWhere.setText(entry.getLocation());
			editWho.setText(entry.getTutor());
			editNotes.setText(entry.getNote());
			// not using the setters made in class because these update the
			// entry as well
			txtStartTime.setText(HumanReadableDate.getTimeString(entry
					.getStartTime()));
			txtEndTime.setText(HumanReadableDate.getTimeString(entry
					.getEndTime()));
			setFreqSpinnerFromEnum(entry.getFreq());
			txtStartDate.setText(HumanReadableDate.getDateString(entry
					.getStartDate()));
			txtEndDate.setText(HumanReadableDate.getDateString(entry
					.getEndDate()));
			if (entry.getFreq() == FREQUENCY.DATE_OF_MONTH) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(entry.getStartDate());
				txtDateOfMonth.setText(cal.get(Calendar.DATE));
			}
			
			colourPicker.select(entry.getTimeTableColour());
			
			daySelecter.setDays(entry.getActiveDays());
			datesEdited = false;
			timesEdited = false;
			startDateEdited = false;
			colourPicker.select(entry.getTimeTableColour());
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.time_table_new, menu);
		return true;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.row_start_time:
			// TODO: finish implementing comment below
			// sets an intial value to pass to the dialog based on the currently
			// stored time, if that doesn't exist it should get the last used
			// from settings, if neither exist it will use the current time.

			long initialStartTime = entry.getStartTime();
			// checks to see if it hasn't been set yet
			if (initialStartTime == TimetableEntry.EMPTY_LONG) {
				Calendar cal = Calendar.getInstance();
				initialStartTime = cal.getTimeInMillis();
			}
			createTimePickerDialog(initialStartTime, true);
			break;
		case R.id.row_end_time:
			long initialEndTime = entry.getEndTime();
			// checks to see if it hasn't been set yet
			if (initialEndTime == TimetableEntry.EMPTY_LONG) {
				Calendar cal = Calendar.getInstance();
				initialEndTime = cal.getTimeInMillis();
			}
			createTimePickerDialog(initialEndTime, false);
			break;
		case R.id.row_start_date:
			long initialStartDate = entry.getStartDate();
			// checks to see if it hasn't been set yet
			if (initialStartDate == TimetableEntry.EMPTY_LONG) {
				Calendar cal = Calendar.getInstance();
				initialStartDate = cal.getTimeInMillis();
			}
			createDatePickerDialog(initialStartDate, true);
			break;
		case R.id.row_end_date:
			long initialEndDate = entry.getEndDate();
			// checks to see if it hasn't been set yet
			if (initialEndDate == TimetableEntry.EMPTY_LONG) {
				Calendar cal = Calendar.getInstance();
				initialEndDate = cal.getTimeInMillis();
			}
			createDatePickerDialog(initialEndDate, false);
			break;
		case R.id.row_date_of_month:
			createDateOfMonthPickerDialog(dateOfMonth);
			break;
		case R.id.btn_confirm:
			if (newEntry) {
				confirmCreateNew();
			} else {
				confirmEditEntry();
			}
			break;
		case R.id.btn_cancel:
			cancelActivity();
			break;
		case R.id.btn_edit_time_and_date:
			showTimesAndDates();
			break;
		default:
			break;

		}

	}

	private void createDeleteDialog() {
		// make confirm dialog
		final Dialog dlg = new Dialog(this);
		dlg.setContentView(R.layout.dlg_delete_timetable_entry);
		dlg.setTitle(getResources().getString(R.string.deleting_entry_title));
		dlg.setCancelable(true);
		dlg.getWindow().setBackgroundDrawableResource(
				R.drawable.gradient_dialog_bg);

		// setup the radio buttons
		final RadioGroup editChoices = (RadioGroup) dlg
				.findViewById(R.id.rg_confirm_delete_choices);

		// set up the set button
		Button confirm = (Button) dlg.findViewById(R.id.btn_delete);

		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
				boolean deleteAllInstances = true;
				switch (editChoices.getCheckedRadioButtonId()) {
				case R.id.rb_delete_all_instances:
					deleteAllInstances = true;
					break;
				case R.id.rb_delete_just_this_one:
					deleteAllInstances = false;
					break;
				default:
					break;
				}
				deleteEntries(deleteAllInstances);
			}
		});

		// setup the cancel button
		Button cancel = (Button) dlg.findViewById(R.id.btn_cancel);

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}

		});

		dlg.show();
	}

	private void deleteEntries(boolean deleteAllInstances) {
		if (deleteAllInstances) {
			db.deleteTimeTableEntry(entry);
		} else {
			db.deleteTimetableSessionDateByTimeAndSessionID(
					entry.getId(),
					this.getIntent().getExtras()
							.getLong(TimeTableActivity.SESSION_DATE));
		}
		this.finishWithResultOK();
	}

	private void cancelActivity() {
		// end activity
		setResult(Activity.RESULT_CANCELED);
		finish();
	}

	/**
	 * sets the visibility of time and date editors to visible and makes the
	 * edit button gone
	 */
	private void showTimesAndDates() {
		// needs to show the correct frequency
		txtSessionTimeTitle.setVisibility(View.VISIBLE);
		sessionTimeTitleUnderline.setVisibility(View.VISIBLE);
		txtSessionDaysTitle.setVisibility(View.VISIBLE);
		sessionDaysTitleUnderline.setVisibility(View.VISIBLE);
		rowStartTime.setVisibility(View.VISIBLE);
		rowEndTime.setVisibility(View.VISIBLE);
		rowFrequency.setVisibility(View.VISIBLE);
		updateFrequencyDisplayOptions(0);
		editTimeAndDate.setVisibility(View.GONE);
	}

	/**
	 * called when this activity is started in edit mode so as to not
	 * unnecessarily make changes to times and dates
	 */
	private void hideTimesAndDates() {
		rowFrequency.setVisibility(View.GONE);
		daySelecter.setVisibility(View.GONE);
		rowDateOfMonth.setVisibility(View.GONE);
		rowStartDate.setVisibility(View.GONE);
		rowEndDate.setVisibility(View.GONE);
		txtSelectTextTitle.setVisibility(View.GONE);
		txtSelectTextTitleUnderline.setVisibility(View.GONE);
		txtSessionTimeTitle.setVisibility(View.GONE);
		sessionTimeTitleUnderline.setVisibility(View.GONE);
		txtSessionDaysTitle.setVisibility(View.GONE);
		sessionDaysTitleUnderline.setVisibility(View.GONE);
		rowStartTime.setVisibility(View.GONE);
		rowEndTime.setVisibility(View.GONE);

		editTimeAndDate.setVisibility(View.VISIBLE);
	}

	@Override
	public void onBackPressed() {
		cancelActivity();
	}

	private void createTimePickerDialog(long initialTime,
			final boolean isStartTime) {
		// boolean flag toggles between it being start or end time,
		// I know it's bad design but the on click method needed
		// to be able to call a method rather than return a value

		// create a time picker dialog and assign the value to the
		// correct text view
		final Dialog dlg = new Dialog(this);
		dlg.setContentView(R.layout.dlg_set_time);
		dlg.setCancelable(true);
		dlg.getWindow().setBackgroundDrawableResource(
				R.drawable.gradient_dialog_bg);

		TextView instructions = (TextView) dlg
				.findViewById(R.id.txt_set_time_instructions);
		if (isStartTime) {
			dlg.setTitle(getResources().getString(R.string.start_time));
			instructions.setText(R.string.dlg_start_time_title);
		} else {
			dlg.setTitle(getResources().getString(R.string.end_time));
			instructions.setText(R.string.dlg_end_time_title);
		}

		// create a calendar to get the initial values into the time picker.
		// on pressing the set button the reverse process occours and the
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

				if (isStartTime) {
					setStartTime(timeSet);
					if (entry.getEndTime() == TimetableEntry.EMPTY_LONG) {
						setEndTime(getDefaultEndTime(timeSet));
					}
				} else {
					setEndTime(timeSet);
				}
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

	private void setStartTime(long startTime) {
		txtStartTime.setText(HumanReadableDate.getTimeString(startTime));
		entry.setStartTime(startTime);
		timesEdited = true;
	}

	private void setEndTime(long endTime) {
		txtEndTime.setText(HumanReadableDate.getTimeString(endTime));
		entry.setEndTime(endTime);
		timesEdited = true;
	}
	
	/**
	 * passing this the start time will return the start time plus the default session length
	 * @param timeSet
	 * @return
	 */
	private long getDefaultEndTime(long timeSet) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		// default set to 1 hour
		return timeSet + preferences.getLong(DEFAULT_SESSION_DURATION, (1000L * 60L * 60L));
	}

	private void createDatePickerDialog(long initialDate,
			final boolean isStartDate) {
		// boolean flag toggles between it being start or end time,
		// I know it's bad design but the on click method needed
		// to be able to call a method rather than return a value

		// create a time picker dialog and assign the value to the
		// correct text view
		final Dialog dlg = new Dialog(this);
		dlg.setContentView(R.layout.dlg_set_date);
		dlg.setCancelable(true);
		dlg.getWindow().setBackgroundDrawableResource(
				R.drawable.gradient_dialog_bg);

		TextView instructions = (TextView) dlg
				.findViewById(R.id.txt_set_date_instructions);
		if (isStartDate) {
			dlg.setTitle(getResources().getString(R.string.start_date));
			instructions.setText(R.string.dlg_start_date_title);
		} else {
			dlg.setTitle(getResources().getString(R.string.end_date));
			instructions.setText(R.string.dlg_end_date_title);
		}

		// create a calendar to get the initial values into the time picker.
		// on pressing the set button the reverse process occours and the
		// time will be returned as a long in milliseconds
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(initialDate);

		final DatePicker dp = (DatePicker) dlg.findViewById(R.id.dpPickDate);
		dp.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH));
		
		Button set = (Button) dlg.findViewById(R.id.btn_confirm);

		set.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, dp.getYear());
				cal.set(Calendar.MONTH, dp.getMonth());
				cal.set(Calendar.DAY_OF_MONTH, dp.getDayOfMonth());
				long dateSet = cal.getTimeInMillis();

				if (isStartDate) {
					setStartDate(dateSet);
				} else {
					setEndDate(dateSet);
				}
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

	private void createDateOfMonthPickerDialog(int currentDay) {
		// create a time picker dialog and assign the value to the
		// correct text view
		final Dialog dlg = new Dialog(this);
		dlg.setContentView(R.layout.dlg_choose_date);
		dlg.setTitle(getResources().getString(R.string.date_of_month));
		dlg.setCancelable(true);
		dlg.getWindow().setBackgroundDrawableResource(
				R.drawable.gradient_dialog_bg);

		// setup the edit text view
		final EditText date = (EditText) dlg
				.findViewById(R.id.etxt_date_of_month);
		if (currentDay != TimetableEntry.EMPTY_INT) {
			date.setText("" + currentDay);
		}

		// set up the set button
		Button set = (Button) dlg.findViewById(R.id.btn_confirm);

		set.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
				int enteredDate = TimetableEntry.EMPTY_INT;
				try {
					if (date.getText().toString().length() > 0) {
						enteredDate = Integer.parseInt(date.getText()
								.toString());
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}

				if (enteredDate != TimetableEntry.EMPTY_INT) {
					// boundary checking and correcting
					enteredDate = Math.min(enteredDate, 31);
					enteredDate = Math.max(enteredDate, 1);

					// sets the entered date
					setDateOfMonth(enteredDate);
				}
			}
		});

		// setup the cancel button
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
	 * used for setting freq = DAY_OF_MONTH
	 * 
	 * @param enteredDate
	 */
	private void setDateOfMonth(int enteredDate) {
		// uses a value outside of the TimetableEntry class, when added to the
		// database the start date is updated to reflect the date added here
		dateOfMonth = enteredDate;
		txtDateOfMonth.setText(enteredDate);
		datesEdited = true;
	}

	private void updateFrequencyDisplayOptions(int selection) {

		datesEdited = true;

		switch (selection) {
		case 0:
			// One Off
			daySelecter.setVisibility(View.GONE);
			rowDateOfMonth.setVisibility(View.GONE);
			rowStartDate.setVisibility(View.VISIBLE);
			rowEndDate.setVisibility(View.GONE);
			txtSelectTextTitle.setVisibility(View.GONE);
			txtSelectTextTitleUnderline.setVisibility(View.GONE);
			// change the title of the date entry to "Date" All other options
			// below change it to "Start Date:"
			txtStartDateTitle.setText(getResources().getString(
					R.string.date_title_short));

			break;
		case 1:
			// Weekly

		case 2:
			// Every Other Week
			daySelecter.setVisibility(View.VISIBLE);
			daySelecter.setSingleDayOnly(false);
			rowDateOfMonth.setVisibility(View.GONE);
			rowStartDate.setVisibility(View.VISIBLE);
			rowEndDate.setVisibility(View.VISIBLE);
			txtSelectTextTitle.setVisibility(View.VISIBLE);
			txtSelectTextTitleUnderline.setVisibility(View.VISIBLE);
			txtStartDateTitle.setText(getResources().getString(
					R.string.start_date));

			break;
		case 3:
			// Date of the Month
			daySelecter.setVisibility(View.GONE);
			rowDateOfMonth.setVisibility(View.VISIBLE);
			rowStartDate.setVisibility(View.VISIBLE);
			rowEndDate.setVisibility(View.VISIBLE);
			txtSelectTextTitle.setVisibility(View.VISIBLE);
			txtSelectTextTitleUnderline.setVisibility(View.VISIBLE);
			txtStartDateTitle.setText(getResources().getString(
					R.string.start_date));

			break;
		case 4:
			// First * of the Month

		case 5:
			// Second * of the Month

		case 6:
			// Third * of the Month

		case 7:
			// Fourth * of the Month

		case 8:
			// Last * of the Month
			daySelecter.setVisibility(View.VISIBLE);
			daySelecter.setSingleDayOnly(true);
			daySelecter.clearAllButOne();
			rowDateOfMonth.setVisibility(View.GONE);
			rowStartDate.setVisibility(View.VISIBLE);
			rowEndDate.setVisibility(View.VISIBLE);
			txtSelectTextTitle.setVisibility(View.VISIBLE);
			txtSelectTextTitleUnderline.setVisibility(View.VISIBLE);
			txtStartDateTitle.setText(getResources().getString(
					R.string.start_date));
			break;
		default:
			break;
		}
	}

	/**
	 * used for hiding the title attribute unless no subject is selected
	 * 
	 * @param showIt
	 */
	private void showTitleEditText(boolean showIt) {
		if (showIt) {
			rowEditTitle.setVisibility(View.VISIBLE);
		} else {
			rowEditTitle.setVisibility(View.GONE);
		}
	}

	private void setStartDate(long startDate) {
		txtStartDate.setText(HumanReadableDate.getDateString(startDate));
		entry.setStartDate(startDate);
		datesEdited = true;
		startDateEdited = true;
	}

	private void setEndDate(long endDate) {
		txtEndDate.setText(HumanReadableDate.getDateString(endDate));
		entry.setEndDate(endDate);
		datesEdited = true;
	}

	/**
	 * makes a dialog to confirm whether the edits should happen to all dates or
	 * just the one selected
	 */
	private void confirmEditEntry() {
		datesEdited = datesEdited || daySelecter.hasChanged();

		// make confirm dialog
		final Dialog dlg = new Dialog(this);
		dlg.setContentView(R.layout.dlg_confirm_edit_entry);
		dlg.setTitle(getResources().getString(R.string.editing_entry_title));
		dlg.setCancelable(true);
		dlg.getWindow().setBackgroundDrawableResource(
				R.drawable.gradient_dialog_bg);

		// setup the radio buttons
		final RadioGroup editChoices = (RadioGroup) dlg
				.findViewById(R.id.rg_confirm_edit_choices);
		
		editChoices.check(R.id.rb_edit_all_instances);
		
		// set up the set button
		Button confirm = (Button) dlg.findViewById(R.id.btn_confirm);

		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
				boolean editAllInstances = true;
				switch (editChoices.getCheckedRadioButtonId()) {
				case R.id.rb_edit_all_instances:
					editAllInstances = true;
					break;
				case R.id.rb_edit_just_this_one:
					editAllInstances = false;
					break;
				default:
					break;
				}
				makeEdits(editAllInstances);
			}
		});

		// setup the cancel button
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
	 * has two possible actions based on whether the dates have been altered. If
	 * not then it simply collects the updated information and gives it to the
	 * database. If the times or dates have changed then the info is updated,
	 * the time database has its entries for this event removed and then is
	 * re-populated with the new dates. Collision detection is done first
	 * (ignoring clashes with itself) so it is still cancel-able if clashes
	 * exist.
	 */
	private void makeEdits(boolean editAll) {
		fillEntry();

		Toast toast = Toast.makeText(this, getString(R.string.checking),
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

		// check for missing fields
		ArrayList<String> missingFields = getMissingDateFields();
		
		if (missingFields.size() > 0) {
			makeMissingFieldsDialog(missingFields);
		} else {
			// no missing fields
			if (editAll) {
				// editing all
				if (!datesEdited && !timesEdited) {
					// nothing edited so just update the session info
					db.updateTimetableEntryDetails(entry);
					finishWithResultOK();
				} else {
					// times or dates have changed
					// set the start date if needed
					if (entry.getFreq().equals(
							TimetableEntry.FREQUENCY.DATE_OF_MONTH)) {
						setTheStartDate();
					}
					// check to see if any dates are clashing
					// note: this is quite an expensive operation
					ArrayList<Long> clashingEntries = db
							.getTimetableClashes(entry);

					if (clashingEntries.size() > 0) {
						// make a dialog with options for deleting clashing
						// entries or
						// cancelling
						makeClashingEntriesDialog(clashingEntries);

					} else {
						editEntryAndFinish();
					}
				}
			} else {
				// editing single entry
				if (!timesEdited && !datesEdited) {
					// update single entry details, requires deleting the
					// session date of the current one, making a new entry at
					// the same date for the new one

					// change the entry to be a one-off session on the day
					// specified
					entry.setFreq(FREQUENCY.ONE_OFF);
					entry.setActiveDaysFromString("nnnnnnn");
					entry.setStartDate(this.getIntent().getExtras()
							.getLong(TimeTableActivity.SESSION_DATE));

					// create new session entry
					int newEntryId = db.addNewTimetableSession(entry);

					// change date entry to new session id
					db.changeTimetableDateOwner(entry.getId(), newEntryId,
							entry.getStartDate());

					// end activity
					setResult(Activity.RESULT_OK);
					finish();

				} else {
					// collision detection required for new time
					// then write to database after clash checks
					// clone entry
					TimetableEntry clonedEntry = new TimetableEntry(entry);

					// change to one-off
					clonedEntry.setFreq(FREQUENCY.ONE_OFF);
					clonedEntry.setActiveDaysFromString("nnnnnnn");
					if (!startDateEdited) {
						clonedEntry.setStartDate(this.getIntent().getExtras()
								.getLong(TimeTableActivity.SESSION_DATE));
					}
					// collision detection
					ArrayList<Long> clashingEntries = db
							.getTimetableClashes(clonedEntry);

					if (clashingEntries.size() > 0) {
						makeClashingEntriesDialogForSingleEdit(clashingEntries,
								clonedEntry);
					}

					// deletes old session, writes in new session, finished with
					// OK result
					editSingleEntryAndFinish(clonedEntry);
				}
			}
		}
	}

	private void editSingleEntryAndFinish(TimetableEntry clonedEntry) {
		// delete old session on date originally passed to the
		// activity
		db.deleteTimetableSessionDateByTimeAndSessionID(entry.getId(), this
				.getIntent().getExtras()
				.getLong(TimeTableActivity.SESSION_DATE));
		// write cloned entry to session and dates table
		db.addNewTimetableEvent(clonedEntry);
		// end activity
		setResult(Activity.RESULT_OK);
		finish();
	}

	private void editEntryAndFinish() {
		if (datesEdited) {
			// deletes previous entry and puts in a new one
			db.deleteTimeTableEntry(entry);
			addResultAndFinish();
		} else {
			db.updateTimetableEntryDetailsAndTimes(entry);
		}
		// end activity
		setResult(Activity.RESULT_OK);
		finish();
	}

	private void confirmCreateNew() {
		// add in the details to the database and check that all required
		// info is there

		// warning toast for the wait
		Toast toast = Toast.makeText(this,
				getString(R.string.checking_for_clashes), Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

		// get the info from edit texts, spinners and day picker into the entry
		fillEntry();

		// check the inputs are valid
		// edit text items can be blank, the time and date fields can't be
		ArrayList<String> missingFields = getMissingDateFields();
		if (missingFields.size() <= 0) {
			// set the start date if needed
			if (entry.getFreq().equals(TimetableEntry.FREQUENCY.DATE_OF_MONTH)) {
				setTheStartDate();
			}
			// check to see if any dates are clashing
			// note: this is quite an expensive operation
			ArrayList<Long> clashingEntries = db.getTimetableClashes(entry);

			if (clashingEntries.size() > 0) {
				// make a dialog with options for deleting clashing entries or
				// cancelling
				makeClashingEntriesDialog(clashingEntries);

			} else {
				addResultAndFinish();
			}
		} else {
			// make a dialog saying what is missing
			makeMissingFieldsDialog(missingFields);
		}
	}

	/**
	 * takes the date of month field and applies it to the start date
	 */
	private void setTheStartDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(entry.getStartDate());
		cal.set(Calendar.DAY_OF_MONTH, dateOfMonth);
		entry.setStartDate(cal.getTimeInMillis());
	}

	private void addResultAndFinish() {
		// add to the database
		db.addNewTimetableEvent(entry);
		// update default field values
		saveDefaults();
		// end activity
		setResult(Activity.RESULT_OK);
		finish();
	}

	private void fillEntry() {
		// subject
		entry.setSubjectID(subjectEntries.getSubjectAtListPosition(
				subjectSpinner.getSelectedItemPosition()).getDatabaseID());
		// title
		if (editTitle.getText() != null) {
			entry.setSessionName(editTitle.getText().toString());
		}
		// where?
		if (editWhere.getText() != null) {
			entry.setLocation(editWhere.getText().toString());
		}
		// with who?
		if (editWho.getText() != null) {
			entry.setTutor(editWho.getText().toString());
		}
		// notes
		if (editNotes.getText() != null) {
			entry.setNote(editNotes.getText().toString());
		}
		// colourSelecter
		entry.setTimeTableColour(colourPicker.getSelectedId());
		
		// frequency
		entry.setFreq(getFreqFromSpinner());

		// day selector
		entry.setActiveDays(daySelecter.getAsAnArray());

		// note: times are filled in when selected

	}

	/**
	 * gives options for what to do with the clashing entries
	 * 
	 * @param clashingEntries
	 */
	private void makeClashingEntriesDialog(ArrayList<Long> clashingEntries) {
		final Dialog dlg = new Dialog(this);
		dlg.setContentView(R.layout.dlg_clashing_timetable_warning);
		dlg.setTitle(getResources().getString(R.string.clashes_found));
		dlg.setCancelable(true);
		dlg.getWindow().setBackgroundDrawableResource(
				R.drawable.gradient_dialog_bg);

		// for passing clashing entries list on
		final ArrayList<Long> clashes = clashingEntries;

		// setup the info TextView
		TextView infoText = (TextView) dlg
				.findViewById(R.id.txt_clashing_timetable_warning);
		infoText.setText(clashingEntries.size() + " "
				+ getResources().getString(R.string.amount_of_clashes_found));

		// setup the radio buttons
		final RadioGroup clashChoices = (RadioGroup) dlg
				.findViewById(R.id.rg_clashing_session_choices);
		
		clashChoices.check(R.id.rb_leave_out_new_clashing_sessions);

		// set up the set button
		Button confirm = (Button) dlg.findViewById(R.id.btn_confirm);

		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
				switch (clashChoices.getCheckedRadioButtonId()) {
				case R.id.rb_overwrite_clashing_sessions:
					overwriteClashingSessions(clashes);
					if (newEntry) {
						addResultAndFinish();
					} else {
						editEntryAndFinish();
					}
					break;
				case R.id.rb_leave_out_new_clashing_sessions:
					if (!newEntry) {
						deleteCurrentEntryFromDataBase();
					}
					addEntryButLeaveOutNewClashingSessions(clashes);
					// save the default settings for certain fields
					if (newEntry)
						saveDefaults();
					finishWithResultOK();
					break;
				case R.id.rb_keep_all_clashing_sessions:
					if (newEntry) {
						addResultAndFinish();
					} else {
						editEntryAndFinish();
					}
					break;
				default:
					break;
				}
			}
		});

		// setup the cancel button
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
	 * used when the times or dates have changed when editing just a single
	 * instance of a session
	 * 
	 * @param clashingEntries
	 */
	private void makeClashingEntriesDialogForSingleEdit(
			ArrayList<Long> clashingEntries, TimetableEntry clonedEntry) {
		final Dialog dlg = new Dialog(this);
		dlg.setContentView(R.layout.dlg_clashing_timetable_warning);
		dlg.setTitle(getResources().getString(R.string.clashes_found));
		dlg.setCancelable(true);
		dlg.getWindow().setBackgroundDrawableResource(
				R.drawable.gradient_dialog_bg);

		// for passing clashing entries list on
		final ArrayList<Long> clashes = clashingEntries;
		final TimetableEntry cloned = clonedEntry;

		// setup the info TextView
		TextView infoText = (TextView) dlg
				.findViewById(R.id.txt_clashing_timetable_warning);
		infoText.setText(clashingEntries.size() + " "
				+ getResources().getString(R.string.amount_of_clashes_found));

		// setup the radio buttons
		final RadioGroup clashChoices = (RadioGroup) dlg
				.findViewById(R.id.rg_clashing_session_choices);
		
		clashChoices.check(R.id.rb_overwrite_clashing_sessions);
		
		// hide the leave_out_new_clashing sessions option
		final RadioButton leaveOutNew = (RadioButton) dlg
				.findViewById(R.id.rb_leave_out_new_clashing_sessions);
		leaveOutNew.setVisibility(View.GONE);

		// set up the set button
		Button confirm = (Button) dlg.findViewById(R.id.btn_confirm);

		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
				switch (clashChoices.getCheckedRadioButtonId()) {
				case R.id.rb_overwrite_clashing_sessions:
					// delete the clashing session
					overwriteClashingSessions(clashes);
					// add the new one
					editSingleEntryAndFinish(cloned);
					break;
				case R.id.rb_keep_all_clashing_sessions:
					editSingleEntryAndFinish(cloned);
					break;
				default:
					break;
				}
			}
		});

		// setup the cancel button
		Button cancel = (Button) dlg.findViewById(R.id.btn_cancel);

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}

		});

		dlg.show();
	}

	private void overwriteClashingSessions(ArrayList<Long> clashes) {
		db.deleteSessionDatesByDateId(clashes);
	}

	private void addEntryButLeaveOutNewClashingSessions(ArrayList<Long> clashes) {
		db.addNewTimetableEventMissingClashes(entry, clashes);
	}

	private void finishWithResultOK() {
		// end activity

		setResult(Activity.RESULT_OK);
		finish();
	}

	private void deleteCurrentEntryFromDataBase() {
		db.deleteTimeTableEntry(entry);
	}

	private void setFreqSpinnerFromEnum(TimetableEntry.FREQUENCY frequency) {
		switch (frequency) {
		case ONE_OFF:
			frequencySpinner.setSelection(0);
			break;
		case WEEKLY:
			frequencySpinner.setSelection(1);
			break;
		case FORTNIGHTLY:
			frequencySpinner.setSelection(2);
			break;
		case DATE_OF_MONTH:
			frequencySpinner.setSelection(3);
			break;
		case FIRST_OF_MONTH:
			frequencySpinner.setSelection(4);
			break;
		case SECOND_OF_MONTH:
			frequencySpinner.setSelection(5);
			break;
		case THIRD_OF_MONTH:
			frequencySpinner.setSelection(6);
			break;
		case FOURTH_OF_MONTH:
			frequencySpinner.setSelection(7);
			break;
		case LAST_OF_MONTH:
			frequencySpinner.setSelection(8);
			break;
		case NONE:
		default:
			// one off
			frequencySpinner.setSelection(0);
			break;
		}

	}

	/**
	 * returns the enum associated with a position on the spinner passed as an
	 * argument
	 * 
	 * @return
	 */
	private TimetableEntry.FREQUENCY getFreqFromSpinner() {
		TimetableEntry.FREQUENCY freq;

		switch (frequencySpinner.getSelectedItemPosition()) {
		case 0:
			// One Off
			freq = TimetableEntry.FREQUENCY.ONE_OFF;
			break;
		case 1:
			// Weekly
			freq = TimetableEntry.FREQUENCY.WEEKLY;
			break;
		case 2:
			// Every Other Week
			freq = TimetableEntry.FREQUENCY.FORTNIGHTLY;
			break;
		case 3:
			// Date of the Month
			freq = TimetableEntry.FREQUENCY.DATE_OF_MONTH;
			break;
		case 4:
			// First * of the Month
			freq = TimetableEntry.FREQUENCY.FIRST_OF_MONTH;
			break;
		case 5:
			// Second * of the Month
			freq = TimetableEntry.FREQUENCY.SECOND_OF_MONTH;
			break;
		case 6:
			// Third * of the Month
			freq = TimetableEntry.FREQUENCY.THIRD_OF_MONTH;
			break;
		case 7:
			// Fourth * of the Month
			freq = TimetableEntry.FREQUENCY.FOURTH_OF_MONTH;
			break;
		case 8:
			// Last * of the Month
			freq = TimetableEntry.FREQUENCY.LAST_OF_MONTH;
			break;
		default:
			freq = TimetableEntry.FREQUENCY.NONE;
			break;
		}

		return freq;
	}

	private ArrayList<String> getMissingDateFields() {
		ArrayList<String> missingFields = new ArrayList<String>();
		// this all depends on the frequency, flag up different fields for
		// different frequencies

		// needed by all:
		// start time
		if (!isFieldFilled(entry.getStartTime())) {
			missingFields.add(getResources().getString(
					R.string.missing_start_time));
		}
		// end time
		if (!isFieldFilled(entry.getEndTime())) {
			missingFields.add(getResources().getString(
					R.string.missing_end_time));
		}

		// end time after start time
		if ((isFieldFilled(entry.getStartTime()) && isFieldFilled(entry
				.getEndTime()))
				&& isStartTimeAfterEndTime(entry.getStartTime(),
						entry.getEndTime())) {

			missingFields.add(getResources().getString(
					R.string.missing_end_time_after_start));
		}

		switch (entry.getFreq()) {
		case ONE_OFF:

			// One Off

			// start date
			if (!isFieldFilled(entry.getStartDate())) {
				missingFields.add(getResources().getString(
						R.string.missing_date));
			}

			break;
		case WEEKLY:
			// Weekly

		case FORTNIGHTLY:
			// Every Other Week

			// start date
			if (!isFieldFilled(entry.getStartDate())) {
				missingFields.add(getResources().getString(
						R.string.missing_start_date));
			}

			// end date
			if (!isFieldFilled(entry.getEndDate())) {
				missingFields.add(getResources().getString(
						R.string.missing_end_date));
			}

			// end date after start date
			if ((isFieldFilled(entry.getStartDate()) && isFieldFilled(entry
					.getEndDate()))
					&& (entry.getStartDate() >= entry.getEndDate())) {
				missingFields.add(getResources().getString(
						R.string.missing_end_date_after_start));
			}

			// day selected
			if (!entry.isAnyDaySelected()) {
				missingFields.add(getResources().getString(
						R.string.missing_days));
			}

			break;
		case DATE_OF_MONTH:
			// Date of the Month

			// the start date field is used to determine the day of the month
			// this will be on. The start date stored by the timetable entry
			// object is the one used by the other freqencies.
			// As such the integer "dateOfMonth" will be checked and the start
			// date updated when written to the database

			// start date
			if (!isFieldFilled(entry.getStartDate())) {
				missingFields.add(getResources().getString(
						R.string.missing_start_date));
			}

			// end date
			if (!isFieldFilled(entry.getEndDate())) {
				missingFields.add(getResources().getString(
						R.string.missing_end_date));
			}

			// end date after start date
			if ((isFieldFilled(entry.getStartDate()) && isFieldFilled(entry
					.getEndDate()))
					&& (entry.getStartDate() >= entry.getEndDate())) {
				missingFields.add(getResources().getString(
						R.string.missing_end_date_after_start));
			}

			if (dateOfMonth == TimetableEntry.EMPTY_INT) {
				missingFields.add(getResources().getString(
						R.string.missing_date_of_month));
			}

			break;
		case FIRST_OF_MONTH:
			// First * of the Month

		case SECOND_OF_MONTH:
			// Second * of the Month

		case THIRD_OF_MONTH:
			// Third * of the Month

		case FOURTH_OF_MONTH:
			// Fourth * of the Month

		case LAST_OF_MONTH:
			// Last * of the Month

			// start date
			if (!isFieldFilled(entry.getStartDate())) {
				missingFields.add(getResources().getString(
						R.string.missing_start_date));
			}

			// end date
			if (!isFieldFilled(entry.getEndDate())) {
				missingFields.add(getResources().getString(
						R.string.missing_end_date));
			}

			// end date after start date
			if ((isFieldFilled(entry.getStartDate()) && isFieldFilled(entry
					.getEndDate()))
					&& (entry.getStartDate() >= entry.getEndDate())) {
				missingFields.add(getResources().getString(
						R.string.missing_end_date_after_start));
			}

			// day selected
			if (!entry.isAnyDaySelected()) {
				missingFields.add(getResources()
						.getString(R.string.missing_day));
			}

			if (entry.isMoreThanOneDaySelected()) {
				missingFields.add(getResources().getString(
						R.string.missing_too_many_days_selected));
			}
			break;
		default:
			break;
		}

		return missingFields;
	}

	private boolean isFieldFilled(long field) {
		return field != TimetableEntry.EMPTY_LONG;
	}

	/**
	 * takes longs to populate calendars, the time is tested independently of
	 * the date.
	 * 
	 * @param startTime
	 * @param endTime
	 * @return returns true if start is after or equal to the end
	 */
	private boolean isStartTimeAfterEndTime(long startTime, long endTime) {
		Calendar startCal = Calendar.getInstance();
		Calendar endCal = Calendar.getInstance();

		startCal.setTimeInMillis(startTime);
		endCal.setTimeInMillis(endTime);

		// make the cals refer to the same day
		startCal.set(Calendar.YEAR, endCal.get(Calendar.YEAR));
		startCal.set(Calendar.DAY_OF_YEAR, endCal.get(Calendar.DAY_OF_YEAR));

		// returns true if start is after or equal to the end
		return startCal.getTimeInMillis() >= endCal.getTimeInMillis();
	}

	/**
	 * useful for working out the default duration of a session based on the
	 * previous entry
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	private long getSessionDuration(long startTime, long endTime) {
		Calendar startCal = Calendar.getInstance();
		Calendar endCal = Calendar.getInstance();

		startCal.setTimeInMillis(startTime);
		endCal.setTimeInMillis(endTime);

		// make the cals refer to the same day
		startCal.set(Calendar.YEAR, endCal.get(Calendar.YEAR));
		startCal.set(Calendar.DAY_OF_YEAR, endCal.get(Calendar.DAY_OF_YEAR));

		// returns true if start is after or equal to the end
		return endCal.getTimeInMillis() - startCal.getTimeInMillis();
	}

	private void makeMissingFieldsDialog(ArrayList<String> missingFields) {

		final Dialog dlg = new Dialog(this);
		dlg.setContentView(R.layout.dlg_missing_fields);
		dlg.setTitle(getResources().getString(R.string.missing_fields));
		dlg.setCancelable(true);
		dlg.getWindow().setBackgroundDrawableResource(
				R.drawable.gradient_dialog_bg);

		// setup and populate the text view
		TextView text = (TextView) dlg
				.findViewById(R.id.txt_missing_fields_details);

		String details = "";

		for (String s : missingFields) {
			details = new String(details + "- " + s + "\n");
		}

		text.setText(details);

		// setup OK button
		Button ok = (Button) dlg.findViewById(R.id.btn_confirm);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}

		});

		dlg.show();
	}

	/**
	 * commits the default values to the shared preferences for use when
	 * creating new entries. These should only by used to populate default
	 * values in new, not edit.
	 */
	private void saveDefaults() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();

		editor.putLong(DEFAULT_START_DATE, entry.getStartDate());
		editor.putLong(DEFAULT_END_DATE, entry.getEndDate());
		editor.putString(DEFAULT_SESSION_FREQENCY, entry.getFreq().toString());
		editor.putLong(DEFAULT_SESSION_DURATION,
				getSessionDuration(entry.getStartTime(), entry.getEndTime()));
		editor.putInt(LAST_TIMETABLE_COLOUR, entry.getTimeTableColour());

		editor.commit();
	}
}
