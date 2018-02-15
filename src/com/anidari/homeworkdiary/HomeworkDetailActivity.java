package com.anidari.homeworkdiary;

// TODO:	
//			Calendar event creation

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import com.anidari.homeworkdiary.HomeworkDetailListEntry.TYPE;
import com.anromus.homeworkdiary.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class HomeworkDetailActivity extends ListActivity implements
		OnItemClickListener {

	public static final String HOMEWORK_ENTRY = "HOMEWORK_ENTRY";
	public static final String DATE_CHANGED = "DATE_CHANGED";
	public static final String SUBJECT_CHANGED = "SUBJECT_CHANGED";

	DatabaseHandler db;

	ArrayList<HomeworkDetailListEntry> homeworkDetail; // stores each item for
	// the list
	ListView homework;
	private boolean isEditMode;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homework_detail_list);

		isEditMode = getIntent().getExtras().getBoolean(
				HomeworkActivity.HOMEWORK_EDIT);

		db = new DatabaseHandler(this);

		if (isEditMode) {
			// get the id of the record passed to the activity
			int id = getIntent().getExtras().getInt(
					HomeworkActivity.HOMEWORK_ID);

			homeworkDetail = db.getHomeworkDetailList(id);

		} else {
			// create a new, blank record
			homeworkDetail = db.getBlankRecord();
		}
		setListAdapter(new HomeworkDetailArrayAdapter(this,
				android.R.layout.simple_list_item_1, homeworkDetail));

		mList = (TouchInterceptor) getListView();
		mList.setDropListener(mDropListener);

		registerForContextMenu(mList);

		homework = getListView();
		homework.setOnItemClickListener(this);

		// do the initial setup for creating a new entry, important fields must
		// not be blank!
		if (!isEditMode) {
			createNewEntry();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.homework_entry, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// uses the item id to decide on action to take
		switch (item.getItemId()) {
		case R.id.action_delete_homework:
			deleteThisHomework();

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

	@Override
	public void onPause() {
		super.onPause();
		db.writeHomeworkDetailToDatabase(homeworkDetail);
	}

	/**
	 * starts the edit subject list activity
	 */
	private void editSubjectList() {
		Intent myIntent = new Intent(this, SubjectEditActivity.class);

		this.startActivity(myIntent);
	}

	/**
	 * ends the activity without writing any data
	 */
	private void cancelCreateNew() {
		Intent resultIntent = new Intent();
		setResult(Activity.RESULT_CANCELED, resultIntent);
		finish();
	}

	/**
	 * create a dialog to confirm deletion
	 */
	private void deleteThisHomework() {
		final Dialog confirmDeleteDialog = new Dialog(this);
		confirmDeleteDialog.setContentView(R.layout.dlg_confirm_delete);
		confirmDeleteDialog.setTitle(R.string.confirm_delete_title);
		confirmDeleteDialog.setCancelable(true);
		confirmDeleteDialog.getWindow().setBackgroundDrawableResource(
				R.drawable.gradient_dialog_bg);

		TextView confirmText = (TextView) confirmDeleteDialog
				.findViewById(R.id.txt_delete_info);

		confirmText.setText(this.getResources().getString(
				R.string.homework_delete_confirm));

		Button deleteButton = (Button) confirmDeleteDialog
				.findViewById(R.id.btn_delete_confirm);

		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				deleteThisHomeworkConfirm();

				confirmDeleteDialog.dismiss();
			}
		});

		Button cancelDelete = (Button) confirmDeleteDialog
				.findViewById(R.id.btn_delete_cancel);

		cancelDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				confirmDeleteDialog.dismiss();

			}

		});

		confirmDeleteDialog.show();
	}

	/**
	 * this scrubs the current homework and associated to do list from the
	 * database then ends the activity
	 */
	private void deleteThisHomeworkConfirm() {
		db.deleteHomework(homeworkDetail);
		Intent resultIntent = new Intent();
		setResult(Activity.RESULT_CANCELED, resultIntent);
		finish();
	}

	@Override
	public void onBackPressed() {
		// get the required data from the fields to be returned
		// only returns id, rest can be looked up from the database in main
		// activity
		int id = -1;
		boolean isDateChanged = false;
		boolean isSubjectChanged = false;
		boolean isCompletedChanged = false;
		for (HomeworkDetailListEntry hwk : homeworkDetail) {
			if (hwk.getType() == TYPE.TITLE) {
				id = hwk.getId();
				isSubjectChanged = hwk.getHasChanged();
			} else if (hwk.getType() == TYPE.DUE_DATE) {
				isDateChanged = hwk.getHasChanged();
			} else if (hwk.getType() == TYPE.FINISHED_GRADE) {
				isCompletedChanged = hwk.getHasChanged();
			}
		}

		// checking completed piggy-backs on the date changed because the
		// effect can be the same of needing to reload the time

		// intent for transporting inputed data back to main activity
		Intent resultIntent = new Intent();

		// puts the required items into the intent bundle to be retrieved in
		// calling activity

		resultIntent.putExtra(HOMEWORK_ENTRY, id);
		resultIntent
				.putExtra(DATE_CHANGED, isDateChanged || isCompletedChanged);
		resultIntent.putExtra(SUBJECT_CHANGED, isSubjectChanged);

		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}

	// Touch intercepter is used for making dragging events in the to do list
	private TouchInterceptor mList;

	private TouchInterceptor.DropListener mDropListener = new TouchInterceptor.DropListener() {
		public void drop(int from, int to) {

			// modified so that an to do list item cant be placed out of the to
			// do list#
			// will have to also change TouchInterceptor so that non-to do list
			// items can't be picked up and moved
			to = Math.max(to, TouchInterceptor.todoListStartPosition);
			to = Math.min(to, homeworkDetail.size() - 3);

			// Assuming that item is moved up the list
			int direction = -1;
			int loop_start = from;
			int loop_end = to;
			// For instance where the item is dragged down the list
			if (from < to) {
				direction = 1;
			}
			HomeworkDetailListEntry target = homeworkDetail.get(from);
			for (int i = loop_start; i != loop_end; i = i + direction) {
				homeworkDetail.set(i, homeworkDetail.get(i + direction));
			}
			homeworkDetail.set(to, target);

			((BaseAdapter) mList.getAdapter()).notifyDataSetChanged();
		}

	};

	/**
	 * make dialogs for the first two sections that must be filled in or the new
	 * entry will be cancelled
	 */
	private void createNewEntry() {

		// create a dialog for editing the title
		final Dialog dialog = new Dialog(this);

		// inflate the layout, this is important otherwise spinner throws
		// null pointer exception
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dlg_hwk_edit_title,
				(ViewGroup) findViewById(R.id.spn_subject_list));

		dialog.setContentView(layout);
		dialog.setTitle(R.string.homework_title);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawableResource(
				R.drawable.gradient_dialog_bg);
		// there are a lot of settings, for dialog, check them all out!
		final EditText inputBox = (EditText) dialog
				.findViewById(R.id.txt_title);

		// set up the spinner
		final Spinner subjectSpinner = (Spinner) layout
				.findViewById(R.id.spn_subject_list);

		// create the array adapter for it
		final SubjectArrayList subjectEntries = db.getArrayOfSubjects(this);

		final String[] subjectDisplay = subjectEntries.getStringsOfHomework();

		// sets the adapter for the spinner
		ArrayAdapter<String> subjectAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, subjectDisplay);
		subjectAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		subjectSpinner.setAdapter(subjectAdapter);

		// set up button
		final Button button = (Button) dialog.findViewById(R.id.btn_ok_button);

		// when cancelled, the create new activity is cancelled
		dialog.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface dialog) {

				Toast toast = Toast.makeText(HomeworkDetailActivity.this,
						getString(R.string.cancel_create_new),
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

				cancelCreateNew();

			}
		});

		// create an on click listener for the OK button
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String str = inputBox.getText().toString();

				if (str.length() > 1) {

					setName(str);

					setSubject(subjectEntries
							.getSubjectAtListPosition(subjectSpinner
									.getSelectedItemPosition()));

					dialog.dismiss();

					// Go straight on to setting a date
					initialSetDate();

				} else // no title, show warning toast and don't dismiss
				{
					Toast toast = Toast.makeText(HomeworkDetailActivity.this,
							getString(R.string.title_error_text),
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			}
		});

		// cancel button
		// set up button
		final Button buttonCancel = (Button) dialog
				.findViewById(R.id.btn_cancel_button);

		// when cancelled, the create new activity is cancelled
		buttonCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast toast = Toast.makeText(HomeworkDetailActivity.this,
						getString(R.string.cancel_create_new),
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

				cancelCreateNew();
				dialog.dismiss();
			}
		});

		// now that the dialog is set up, it's time to show it
		dialog.show();

	}

	private void initialSetDate() {
		// create a dialog with a date picker in it
		final Dialog dialogDate = new Dialog(this);
		dialogDate.setContentView(R.layout.dlg_hwk_edit_due_date);
		dialogDate.setTitle(R.string.due_date_set);
		dialogDate.setCancelable(true);
		dialogDate.getWindow().setBackgroundDrawableResource(
				R.drawable.gradient_dialog_bg);
		// there are a lot of settings, for dialog, check them all out!
		final DatePicker datePicker = (DatePicker) dialogDate
				.findViewById(R.id.datePicker1);

		datePicker.setSpinnersShown(false);

		// set the initial date displayed, should be the date of the
		// homework or today's date if null
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());

		datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DATE));

		// set up button
		final Button buttonOKDate = (Button) dialogDate
				.findViewById(R.id.btn_ok_date);

		// set up the on click listener for the OK button
		buttonOKDate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Calendar cal = Calendar.getInstance(TimeZone.getDefault());
				cal.set(datePicker.getYear(), datePicker.getMonth(),
						datePicker.getDayOfMonth());

				setDate(cal.getTimeInMillis());
				dialogDate.dismiss();

				addNewEntryToDb();

				// set the default reminder enabled
				setReminderEnabled(true);
			}
		});

		// set up button
		final Button buttonCancel = (Button) dialogDate
				.findViewById(R.id.btn_cancel_button);

		// when cancelled, the create new activity is cancelled
		buttonCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast toast = Toast.makeText(HomeworkDetailActivity.this,
						getString(R.string.cancel_create_new),
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

				cancelCreateNew();
				dialogDate.dismiss();
			}
		});

		// when cancelled, the create new activity is cancelled
		dialogDate.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface dialog) {

				Toast toast = Toast.makeText(HomeworkDetailActivity.this,
						getString(R.string.cancel_create_new),
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

				cancelCreateNew();

			}
		});

		// now that the dialog is set up, it's time to show it
		dialogDate.show();
	}

	/**
	 * checks the right box
	 * 
	 * @param enabled
	 */
	private void setReminderEnabled(boolean enabled) {
		for (HomeworkDetailListEntry hwk : homeworkDetail) {
			if (hwk.getType() == TYPE.REMINDER_SET) {
				hwk.setIsTicked(enabled);
				break;
			}
		}
	}

	/**
	 * checks the right box
	 * 
	 * @param enabled
	 */
	private void setCompleted(boolean completed) {
		for (HomeworkDetailListEntry hwk : homeworkDetail) {
			if (hwk.getType() == TYPE.FINISHED_GRADE) {
				hwk.setIsTicked(completed);
				break;
			}
		}
	}

	/**
	 * finalise the details to the database
	 */
	public void addNewEntryToDb() {
		HomeworkEntry newHwk = new HomeworkEntry();

		for (HomeworkDetailListEntry hwk : homeworkDetail) {
			switch (hwk.getType()) {
			case TITLE:
				newHwk.setHomeworkTitle(hwk.getText1());
				newHwk.setSubjectID(hwk.getId2());
				break;
			case DUE_DATE:
				newHwk.setDueDate(hwk.getDueDate());
				break;
			default:
				break;
			}
		}

		// adds the homework and sets the database id
		for (HomeworkDetailListEntry hwk : homeworkDetail) {
			if (hwk.getType() == TYPE.TITLE) {
				hwk.setId(db.addHomework(newHwk));
				break;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long id) {
		// this is the call back for any item selected, the int "position" gives
		// the item in the list selected
		final HomeworkDetailListEntry selectedEntry = (HomeworkDetailListEntry) getListAdapter()
				.getItem(position);
		switch (selectedEntry.getType()) {
		case TITLE:
			titleSelected(selectedEntry);
			break;
		case DUE_DATE:
			dueDateSelected(selectedEntry);
			break;
		case FINISHED_GRADE:
			finishedGradeSelected(selectedEntry);
			break;
		case REMINDER_SET:
			// no effect
			break;
		case TO_DO_TITLE:
			// should be non-selectable anyhow
			break;
		case TO_DO_ENTRY:
			toDoEntrySelected(selectedEntry);
			break;
		case TODO_ADD_NEW:
			newToDoEntrySelected(selectedEntry);
			break;

		default:
			break;
		}
	}

	private void titleSelected(HomeworkDetailListEntry selectedEntry) {

		// create a dialog for editing the title
		final Dialog dialog = new Dialog(this);

		// inflate the layout, this is important otherwise spinner throws
		// null pointer exception
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dlg_hwk_edit_title,
				(ViewGroup) findViewById(R.id.spn_subject_list));

		dialog.setContentView(layout);
		dialog.setTitle(R.string.homework_title);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawableResource(
				R.drawable.gradient_dialog_bg);
		// there are a lot of settings, for dialog, check them all out!
		final EditText inputBox = (EditText) dialog
				.findViewById(R.id.txt_title);

		// put the current title of homework in the edit text box
		inputBox.setText(selectedEntry.getText1());

		// set up the spinner
		final Spinner subjectSpinner = (Spinner) layout
				.findViewById(R.id.spn_subject_list);

		// create the array adapter for it
		final SubjectArrayList subjectEntries = db.getArrayOfSubjects(this);

		final String[] subjectDisplay = subjectEntries.getStringsOfHomework();

		// sets the adapter for the spinner
		ArrayAdapter<String> subjectAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, subjectDisplay);
		subjectAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		subjectSpinner.setAdapter(subjectAdapter);

		// sets the inital position on the spinner
		subjectSpinner.setSelection(subjectEntries
				.getPositionById(selectedEntry.getId2()));

		// set up button
		final Button button = (Button) dialog.findViewById(R.id.btn_ok_button);

		// create an on click listener for the OK button
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String str = inputBox.getText().toString();

				if (str.length() > 1) {

					setName(str);

					setSubject(subjectEntries
							.getSubjectAtListPosition(subjectSpinner
									.getSelectedItemPosition()));

					dialog.dismiss();
				} else // no title, show warning toast and don't dismiss
				{
					Toast toast = Toast.makeText(HomeworkDetailActivity.this,
							getString(R.string.title_error_text),
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			}
		});

		// set up button
		final Button buttonCancel = (Button) dialog
				.findViewById(R.id.btn_cancel_button);

		// when cancelled, the create new activity is cancelled
		buttonCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		// now that the dialog is set up, it's time to show it
		dialog.show();
	}

	private void dueDateSelected(HomeworkDetailListEntry selectedEntry) {

		// create a dialog with a date picker in it
		final Dialog dialogDate = new Dialog(this);
		dialogDate.setContentView(R.layout.dlg_hwk_edit_due_date);
		dialogDate.setTitle(R.string.due_date_set);
		dialogDate.setCancelable(true);
		dialogDate.getWindow().setBackgroundDrawableResource(
				R.drawable.gradient_dialog_bg);
		// there are a lot of settings, for dialog, check them all out!
		final DatePicker datePicker = (DatePicker) dialogDate
				.findViewById(R.id.datePicker1);

		datePicker.setSpinnersShown(false);
		
		// set the initial date displayed, should be the date of the
		// homework or today's date if null
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		if (selectedEntry.getDueDate() != 0L) {
			cal.setTimeInMillis(selectedEntry.getDueDate());
		}
		datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DATE));

		// set up button
		final Button buttonOKDate = (Button) dialogDate
				.findViewById(R.id.btn_ok_date);

		// set up the on click listener for the OK button
		buttonOKDate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Calendar cal = Calendar.getInstance(TimeZone.getDefault());
				cal.set(datePicker.getYear(), datePicker.getMonth(),
						datePicker.getDayOfMonth());

				setDate(cal.getTimeInMillis());
				dialogDate.dismiss();
			}
		});

		// set up button
		final Button buttonCancelDate = (Button) dialogDate
				.findViewById(R.id.btn_cancel_button);

		// when cancelled, the create new activity is cancelled
		buttonCancelDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialogDate.dismiss();
			}
		});

		// now that the dialog is set up, it's time to show it
		dialogDate.show();
	}

	private void finishedGradeSelected(HomeworkDetailListEntry selectedEntry) {

		// create a dialog for editing the final grade
		final Dialog dialogGrade = new Dialog(this);
		dialogGrade.setContentView(R.layout.dlg_hwk_edit_final_grade);
		dialogGrade.setTitle(R.string.homework_grade_title);
		dialogGrade.setCancelable(true);
		dialogGrade.getWindow().setBackgroundDrawableResource(
				R.drawable.gradient_dialog_bg);
		// there are a lot of settings, for dialog, check them all out!
		final EditText inputBoxGrade = (EditText) dialogGrade
				.findViewById(R.id.txt_grade);

		// put the current grade of homework in the edit text box
		if (selectedEntry.getText1() != null) {
			inputBoxGrade.setText(selectedEntry.getText1());
		}

		// set up OK button
		final Button buttonOKGrade = (Button) dialogGrade
				.findViewById(R.id.btn_edit_ok);

		// create an on click listener for the OK button
		buttonOKGrade.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String str = inputBoxGrade.getText().toString();
				if (str.length() > 0) {
					setGrade(str);
					setCompleted(true);
					dialogGrade.dismiss();
				} else {
					// not allowed to set an empty grade
					Toast toast = Toast.makeText(HomeworkDetailActivity.this,
							getString(R.string.no_empty_grade_allowed),
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			}
		});

		// set up cancel button
		final Button buttonCancelGrade = (Button) dialogGrade
				.findViewById(R.id.btn_edit_cancel);

		// create an on click listener for the OK button
		buttonCancelGrade.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialogGrade.dismiss();
			}
		});

		// now that the dialog is set up, it's time to show it
		dialogGrade.show();
	}

	private void toDoEntrySelected(HomeworkDetailListEntry selectedEntry) {
		// edit the to do list entry
		// create a dialog for editing the title
		final Dialog dialogToDo = new Dialog(this);
		dialogToDo.setContentView(R.layout.dlg_hwk_edit_to_do);
		dialogToDo.setTitle(R.string.edit_to_do_title);
		dialogToDo.setCancelable(true);
		dialogToDo.getWindow().setBackgroundDrawableResource(
				R.drawable.gradient_dialog_bg);
		// there are a lot of settings, for dialog, check them all out!
		final EditText inputBoxToDo = (EditText) dialogToDo
				.findViewById(R.id.txt_enter_todo);

		// put the current title of homework in the edit text box
		if (selectedEntry.getText1() != null)
			inputBoxToDo.setText(selectedEntry.getText1());

		// set up the checkbox
		final CheckBox checkBoxToDo = (CheckBox) dialogToDo
				.findViewById(R.id.todo_completed);

		checkBoxToDo.setChecked(selectedEntry.getIsTicked());

		// set up buttons
		final Button buttonOKToDo = (Button) dialogToDo
				.findViewById(R.id.btn_ok_button);

		final Button buttonDeleteToDo = (Button) dialogToDo
				.findViewById(R.id.btn_delete_button);

		final int entryId = selectedEntry.getId();

		// create an on click listener for the OK button
		buttonOKToDo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String str = inputBoxToDo.getText().toString();

				if (str.length() > 1) {

					boolean isChecked = checkBoxToDo.isChecked();
					setToDo(entryId, str, isChecked);
					dialogToDo.dismiss();

				} else // no text, delete entry
				{
					Toast toast = Toast.makeText(HomeworkDetailActivity.this,
							getString(R.string.delete_to_do_text),
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					deleteToDo(entryId);
					dialogToDo.dismiss();
				}
			}
		});

		buttonDeleteToDo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				deleteToDo(entryId);
				dialogToDo.dismiss();
			}
		});
		// now that the dialog is set up, it's time to show it
		dialogToDo.show();
	}

	private void newToDoEntrySelected(HomeworkDetailListEntry selectedEntry) {
		// edit the to do list entry
		// create a dialog for editing the title
		final Dialog dialogNewToDo = new Dialog(this);
		dialogNewToDo.setContentView(R.layout.dlg_hwk_edit_to_do);
		dialogNewToDo.setTitle(R.string.new_to_do_title);
		dialogNewToDo.setCancelable(true);
		dialogNewToDo.getWindow().setBackgroundDrawableResource(
				R.drawable.gradient_dialog_bg);
		// there are a lot of settings, for dialog, check them all out!
		final EditText inputBoxNewToDo = (EditText) dialogNewToDo
				.findViewById(R.id.txt_enter_todo);

		// set up the check box
		final CheckBox checkBoxNewToDo = (CheckBox) dialogNewToDo
				.findViewById(R.id.todo_completed);

		checkBoxNewToDo.setChecked(false);

		// set up button
		final Button buttonNewToDo = (Button) dialogNewToDo
				.findViewById(R.id.btn_ok_button);

		final int entryId = selectedEntry.getId();

		// create an on click listener for the OK button
		buttonNewToDo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String str = inputBoxNewToDo.getText().toString();

				if (str.length() > 1) {

					boolean isChecked = checkBoxNewToDo.isChecked();
					makeNewToDo(str, isChecked);
					dialogNewToDo.dismiss();

				} else // no text, delete entry
				{
					Toast toast = Toast.makeText(HomeworkDetailActivity.this,
							getString(R.string.delete_to_do_text),
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					deleteToDo(entryId);
					dialogNewToDo.dismiss();
				}
			}
		});
		// setup a cancel button (uses the delete button of the view)
		Button cancelButton = (Button) dialogNewToDo
				.findViewById(R.id.btn_delete_button);
		cancelButton.setText(getResources().getString(R.string.cancel));

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogNewToDo.dismiss();
			}
		});

		// now that the dialog is set up, it's time to show it
		dialogNewToDo.show();
	}

	// ********************************************************************************************
	// Setting the various components

	public void setName(String str) {

		for (HomeworkDetailListEntry hwk : homeworkDetail) {
			if (hwk.getType() == TYPE.TITLE) {
				hwk.setText1(str);
				break;
			}
		}

		((BaseAdapter) getListAdapter()).notifyDataSetChanged();
	}

	public void setSubject(SubjectEntry subject) {
		for (HomeworkDetailListEntry hwk : homeworkDetail) {
			if (hwk.getType() == TYPE.TITLE) {
				hwk.setText2(subject.getSubjectName());
				hwk.setId2(subject.getDatabaseID());
				hwk.setImage(subject.getSubjectPicture());
				break;
			}
		}
	}

	/**
	 * adds the date passed to the right list item
	 * 
	 * @param dateInMillis
	 */
	public void setDate(long dateInMillis) {

		for (HomeworkDetailListEntry hwk : homeworkDetail) {
			if (hwk.getType() == TYPE.DUE_DATE) {
				hwk.setDueDate(dateInMillis);
				break;
			}
		}

		((BaseAdapter) getListAdapter()).notifyDataSetChanged();
	}

	/**
	 * sets the grade element of the list to the argument
	 * 
	 * @param str
	 */
	public void setGrade(String str) {

		for (HomeworkDetailListEntry hwk : homeworkDetail) {
			if (hwk.getType() == TYPE.FINISHED_GRADE) {
				hwk.setText1(str);
				break;
			}
		}

		((BaseAdapter) getListAdapter()).notifyDataSetChanged();
	}

	public void setToDo(int entryId, String str, boolean isChecked) {

		for (HomeworkDetailListEntry hwk : homeworkDetail) {
			if (hwk.getType() == TYPE.TO_DO_ENTRY && hwk.getId() == entryId) {
				hwk.setText1(str);
				hwk.setIsTicked(isChecked);
				break;
			}
		}

		((BaseAdapter) getListAdapter()).notifyDataSetChanged();
	}

	/**
	 * this should update the database when it is made and then add that same
	 * entry on to the list
	 * 
	 * @param str
	 * @param isChecked
	 */
	public void makeNewToDo(String str, boolean isChecked) {

		// find out the current highest order number in the list
		// get the homework ID
		int highestOrderNumber = 0;
		int homeworkId = 0;

		for (HomeworkDetailListEntry hwk : homeworkDetail) {
			if (hwk.getType() == TYPE.TITLE) {
				homeworkId = hwk.getId();
			}
			if (hwk.getType() == TYPE.TO_DO_ENTRY) {
				highestOrderNumber++;
			}
		}

		// make a ToDoEntry object
		ToDoEntry toDo = new ToDoEntry(homeworkId, highestOrderNumber + 1, str,
				isChecked);

		// add it to the database, return value sets the id
		toDo.setId(db.addToDoEntry(toDo));

		// add it to the list
		HomeworkDetailListEntry newListEntry = new HomeworkDetailListEntry(toDo);

		homeworkDetail.add(homeworkDetail.size() - 2, newListEntry);

		((BaseAdapter) getListAdapter()).notifyDataSetChanged();

	}

	public void deleteToDo(int entryId) {

		HomeworkDetailListEntry hwkRemove = null;

		// gets a reference to the object to be deleted then removes it, can't
		// delete inside a for each loop
		for (HomeworkDetailListEntry hwk : homeworkDetail) {
			if (hwk.getType() == TYPE.TO_DO_ENTRY && hwk.getId() == entryId) {

				hwkRemove = hwk;
				break;
			}
		}
		if (hwkRemove != null)
			homeworkDetail.remove(hwkRemove);

		// also need to remove it from the database
		db.removeToDoEntry(entryId);

		((BaseAdapter) getListAdapter()).notifyDataSetChanged();

	}

	/**
	 * allows for editing settings, careful to update on return if needed
	 */
	private void launchSettingsActivity() {
		Intent myIntent = new Intent(this, SettingsActivity.class);

		this.startActivityForResult(myIntent, SettingsActivity.EDIT_SETTINGS_ID);
	}

}