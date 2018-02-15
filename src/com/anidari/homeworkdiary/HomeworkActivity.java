package com.anidari.homeworkdiary;

import java.util.ArrayList;

import com.anromus.homeworkdiary.R;

import android.os.Bundle;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

public class HomeworkActivity extends ListActivity implements OnItemClickListener {

	static public int EDIT_HOMEWORK_REQUEST = 1;
	static public int ADD_NEW_HOMEWORK_REQUEST = 2;
	static public int EDIT_SUBJECT_REQUEST = 3;
	static public int EDIT_TIME_TABLE = 4;
	static public String HOMEWORK_ID = "HOMEWORK_ID";
	static public String HOMEWORK_DUE_DATE = "HOMEWORK_DUE_DATE";
	static public String HOMEWORK_TITLE = "HOMEWORK_TITLE";
	static public String HOMEWORK_SUBJECT_ID = "HOMEWORK_SUBJECT_ID";
	static public String HOMEWORK_SUBJECT_NAME = "HOMEWORK_SUBJECT_NAME";
	static public String HOMEWORK_SUBJECT_LOGO = "HOMEWORK_SUBJECT_LOGO";
	static public String HOMEWORK_IS_REMINDER_SET = "HOMEWORK_IS_REMINDER_SET";
	static public String HOMEWORK_IS_COMPLETED = "HOMEWORK_IS_COMPLETED";
	static public String HOMEWORK_FINAL_GRADE = "HOMEWORK_FINAL_GRADE";
	static public String HOMEWORK_TODO_LIST = "HOMEWORK_TODO_LIST";
	static public String HOMEWORK_ENTRY = "HOMEWORK_ENTRY";
	static public String HOMEWORK_EDIT = "HOMEWORK_EDIT";

	// code referring to the detail list activity
	private static final int EDIT_DETAIL_REQUEST = 1;

	// database handler
	DatabaseHandler db;

	// List view
	ListView homework;

	// homework detail
	ArrayList<HomeworkListEntry> homeworkList;

	// homework array adapter
	HomeworkArrayAdapter homeworkArrayAdapter;

	// list view state switch
	boolean isOrderedByDate = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homework_overview);

		// Create the new database handler
		db = new DatabaseHandler(this);

		// setting up the listView
		homeworkList = db.getAllHomework(this);

		// setting the list adapter
		homeworkArrayAdapter = new HomeworkArrayAdapter(this,
				android.R.layout.simple_list_item_1, homeworkList);

		setListAdapter(homeworkArrayAdapter);
		homework = getListView();
		homework.setOnItemClickListener(this);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// check which request is returning
		if (requestCode == EDIT_DETAIL_REQUEST) {
			// Check the request was successful
			if (resultCode == RESULT_OK) {

				// get the data from the intent here
				int idReturned = data.getIntExtra("HOMEWORK_ENTRY", -1);
				boolean isDateChanged = data.getBooleanExtra("DATE_CHANGED",
						false);
				boolean isSubjectChanged = data.getBooleanExtra(
						"SUBJECT_CHANGED", false);

				// what to update depends on how the list is currently ordered
				if (isOrderedByDate) {

					// if the date is changed the entire data set should be
					// reloaded
					// due to the re-ordering needed
					if (isDateChanged) {
						homeworkArrayAdapter.updateAll(db.getAllHomework(this));
					} else
						homeworkArrayAdapter.updateSingleEntry(idReturned,
								db.getHomeworkListEntry(idReturned));
				} else {
					// if ordered by subject then the list should be reloaded
					// if the subject or date changes, sub-ordered by date
					if (isSubjectChanged || isDateChanged) {
						homeworkArrayAdapter.updateAll(db
								.getAllHomeworkBySubject(this));
					} else
					homeworkArrayAdapter.updateSingleEntry(idReturned,
							db.getHomeworkListEntry(idReturned));
				}
			} else if (resultCode == RESULT_CANCELED) {
				// deleted, update the list
				homeworkArrayAdapter.updateAll(db.getAllHomework(this));
			}
		} else if (requestCode == ADD_NEW_HOMEWORK_REQUEST
				|| requestCode == EDIT_SUBJECT_REQUEST
				|| requestCode == SettingsActivity.EDIT_SETTINGS_ID) {
			// check for success
			if (resultCode == RESULT_OK) {
				// update the list
				homeworkArrayAdapter.updateAll(db.getAllHomework(this));
			} else if (resultCode == RESULT_CANCELED) {
				// deleted, update the list
				homeworkArrayAdapter.updateAll(db.getAllHomework(this));
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// uses the item id to decide on action to take
		switch (item.getItemId()) {
		case R.id.action_toggle_group_by_subject:
			if (isOrderedByDate) {
				isOrderedByDate = false;
				homeworkArrayAdapter
						.updateAll(db.getAllHomeworkBySubject(this));
			} else {
				isOrderedByDate = true;
				homeworkArrayAdapter.updateAll(db.getAllHomework(this));
			}
			break;
		case R.id.action_new_homework:
			addNewHomework();
			break;
		case R.id.action_delete_homework:
			cleanUpDialog();
			break;
		case R.id.action_edit_subject_list:
			editSubjectList();
			break;
		case R.id.action_settings:
			launchSettingsActivity();
			break;
		default:
			break;
		}

		return true;
	}

	private void cleanUpDialog() {

		// create a dialog for editing the title
		final Dialog dialog = new Dialog(this);

		// inflate the layout, this is important otherwise spinner throws
		// null pointer exception
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		View layout = inflater.inflate(R.layout.dlg_delete_homework_options,
				null);

		dialog.setContentView(layout);
		dialog.setTitle(R.string.dlg_delete_options);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawableResource(R.drawable.gradient_dialog_bg);

		// set up the what spinner
		final Spinner whatSpinner = (Spinner) dialog
				.findViewById(R.id.spn_delete_what);

		final String[] whatDisplay = new String[] {
				getResources().getString(R.string.dlg_delete_option_completed),
				getResources().getString(R.string.dlg_delete_option_by_subject),
				getResources().getString(R.string.dlg_delete_option_all) };

		ArrayAdapter<String> whatAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, whatDisplay);
		whatAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		whatSpinner.setAdapter(whatAdapter);
		
		// set up the subject spinner
		final Spinner subjectSpinner = (Spinner) dialog
				.findViewById(R.id.spn_delete_by_subject);

		// create the array adapter for it
		final SubjectArrayList subjectEntries = db.getArrayOfSubjects(this);

		final String[] subjectDisplay = subjectEntries.getStringsOfHomework();

		ArrayAdapter<String> subjectAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, subjectDisplay);
		subjectAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		subjectSpinner.setAdapter(subjectAdapter);

		// set up the when spinner
		final Spinner whenSpinner = (Spinner) dialog
				.findViewById(R.id.spn_delete_when);

		final String[] whenDisplay = new String[] {
				getResources().getString(R.string.dlg_delete_time_1),
				getResources().getString(R.string.dlg_delete_time_2),
				getResources().getString(R.string.dlg_delete_time_3),
				getResources().getString(R.string.dlg_delete_time_4),
				getResources().getString(R.string.dlg_delete_time_5),
				getResources().getString(R.string.dlg_delete_time_6),
				getResources().getString(R.string.dlg_delete_time_7) };

		ArrayAdapter<String> whenAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, whenDisplay);
		whenAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		whenSpinner.setAdapter(whenAdapter);

		// show or hide the subject options
		whatSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// show the subject spinner if selected
				if (position == 1) {
					// show it
					subjectSpinner.setVisibility(View.VISIBLE);
				}
				else {
					// hide it
					subjectSpinner.setVisibility(View.GONE);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// do nothing
			}
		});
		
		// set up the buttons
		// set up cancel button
		final Button buttonCancel = (Button) dialog
				.findViewById(R.id.btn_delete_cancel);

		// when cancelled, dismiss the dialog
		buttonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		// set up confirm button
		final Button buttonDelete = (Button) dialog
				.findViewById(R.id.btn_delete_confirm);

		// delete accordingly
		buttonDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// pass the spinner settings to a delete method and dismiss dialog
				int subjectDatabaseID = subjectEntries.getSubjectAtListPosition(subjectSpinner.getSelectedItemPosition()).getDatabaseID();
				
				deleteSelection(
						whatSpinner.getSelectedItemPosition(),
						subjectDatabaseID,
						whenSpinner.getSelectedItemPosition());
				dialog.dismiss();
			}
		});

		dialog.show();

	}
	
	private void deleteSelection(int what, int subjectID, int when) {
		// what: 0 = completed, 1 = by subject, 2 = all
		// when: 0 = 6 months, 1 = 3 months, 2 = 1 month, 3 = 2 weeks, 4 = 1 week, 5 = 1 day, 6 = all
		
		// create a final confirmation dialog if they select all for both what and when
		
		db.deleteSelection(what, subjectID, when);
		
		homeworkArrayAdapter.updateAll(db.getAllHomework(this));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long id) {
		// gets the id of the selected entry
		HomeworkListEntry selectedHomework = (HomeworkListEntry) getListAdapter()
				.getItem(position);

		// launch the new activity, the REQUEST is an int used to know what is
		// meant to happen on opening the acitvity, new or edit
		Intent myIntent = new Intent(HomeworkActivity.this,
				HomeworkDetailActivity.class);
		// To avoid having to implement parcelable or serializable putting key
		// pair values
		myIntent.putExtra(HOMEWORK_ID, selectedHomework.getId());

		db.close();

		myIntent.putExtra(HOMEWORK_EDIT, true);

		HomeworkActivity.this.startActivityForResult(myIntent,
				EDIT_HOMEWORK_REQUEST);
	}

	// intents ************************************************************************************
	
	/**
	 * launch the new activity, the REQUEST is an int used to know what is meant
	 * to happen on opening the acitvity, new or edit
	 */
	private void addNewHomework() {
		Intent myIntent = new Intent(HomeworkActivity.this,
				HomeworkDetailActivity.class);

		myIntent.putExtra(HOMEWORK_EDIT, false);

		HomeworkActivity.this.startActivityForResult(myIntent,
				ADD_NEW_HOMEWORK_REQUEST);

	}

	private void editSubjectList() {
		Intent myIntent = new Intent(HomeworkActivity.this,
				SubjectEditActivity.class);

		HomeworkActivity.this
				.startActivityForResult(myIntent, EDIT_SUBJECT_REQUEST);
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
	
}
