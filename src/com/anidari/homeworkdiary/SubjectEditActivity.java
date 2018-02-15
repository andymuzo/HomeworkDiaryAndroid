package com.anidari.homeworkdiary;

import java.util.ArrayList;

import com.anromus.homeworkdiary.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SubjectEditActivity extends ListActivity implements
		OnItemClickListener {

	private ImageLoader imageLoader;

	private DatabaseHandler db;

	private ArrayList<SubjectEntry> subjects;

	private ListView subjectListView;
	
	private SubjectEditArrayAdapter listAdapter;

	private int imageId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subject_edit);

		// populate the list
		db = new DatabaseHandler(this);

		subjects = db.getSubjectArrayList(this, false);

		listAdapter = new SubjectEditArrayAdapter(this,
				android.R.layout.simple_list_item_1, subjects);
		
		setListAdapter(listAdapter);

		// sets the click listener for the view to this
		subjectListView = getListView();
		subjectListView.setOnItemClickListener(this);

		imageLoader = new ImageLoader(this);

		imageId = 0;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.subject_edit, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// position gives the array number

		// the item in the list selected
		final SubjectEntry selectedEntry = (SubjectEntry) getListAdapter()
				.getItem(position);

		// if (!selectedEntry.isSpacer()) {
		// must be a subject entry
		// create a dialog for editing the title
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.subject_edit);
		dialog.setTitle(R.string.edit_subject);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawableResource(
				R.drawable.gradient_dialog_bg);

		// set up title
		final EditText titleEditText = (EditText) dialog
				.findViewById(R.id.txt_edit_subject_name);
		titleEditText.setText(selectedEntry.getSubjectName());

		// set up image view
		final ImageView subjectIcon = (ImageView) dialog
				.findViewById(R.id.img_subject_icon);

		imageLoader.loadBitmap(selectedEntry.getSubjectPicture(), subjectIcon);
		// stores the image id if it changes before committing it
		imageId = selectedEntry.getSubjectPicture();

		// Set up grid view
		final GridView iconGrid = (GridView) dialog
				.findViewById(R.id.icon_grid_view);
		iconGrid.setAdapter(new ImageAdapter(this));

		iconGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				imageId = ((ImageAdapter) iconGrid.getAdapter())
						.getIdAtPosition(position);
				imageLoader.loadBitmap(imageId, subjectIcon);
			}
		});

		// set up OK button
		final Button buttonOK = (Button) dialog.findViewById(R.id.btn_ok);

		// set up the on click listener for the OK button
		buttonOK.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// checks for empty text box
				String title = titleEditText.getText().toString();

				if (title.length() <= 0) {
					// doesn't let you save an empty title
					Toast.makeText(
							SubjectEditActivity.this,
							getResources().getText(
									R.string.empty_subject_warning),
							Toast.LENGTH_SHORT).show();
				} else {
					setSubjectDetails(selectedEntry, title);
					dialog.dismiss();
				}
			}
		});

		// set up Cancel button
		final Button buttonCancel = (Button) dialog
				.findViewById(R.id.btn_cancel);

		// set up the on click listener for the OK button
		buttonCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		// now that the dialog is set up, it's time to show it
		dialog.show();
	}

	private void deleteSubject(int databaseId) {

		final int hwkAssigned = db.howManyHomeworkAssignedToSubject(databaseId);
		final int timetableAssigned = db
				.howManyTimetableAssignedToSubject(databaseId);

		final SubjectEntry subjectToDelete = db.getSubjectEntry(databaseId);
		// make a warning dialog to say that deleting will cause
		// homework entries to be set to no subject, OK to confirm
		// delete

		final Dialog confirmDeleteDialog = new Dialog(this);
		confirmDeleteDialog.setContentView(R.layout.dlg_confirm_delete);
		confirmDeleteDialog.setTitle(R.string.confirm_delete_title);
		confirmDeleteDialog.setCancelable(true);
		confirmDeleteDialog.getWindow().setBackgroundDrawableResource(
				R.drawable.gradient_dialog_bg);

		TextView confirmText = (TextView) confirmDeleteDialog
				.findViewById(R.id.txt_delete_info);

		// "warning:"
		String warningText = getResources().getString(
				R.string.subject_delete_text_warning);

		// "homework entry/ies"
		if (hwkAssigned == 1) {
			warningText = warningText
					+ hwkAssigned
					+ " "
					+ getResources().getString(
							R.string.subject_delete_text_homework_entry);
		} else if (hwkAssigned > 1) {
			warningText = warningText
					+ hwkAssigned
					+ " "
					+ getResources().getString(
							R.string.subject_delete_text_homework_entries);
		}

		// " and "
		if (hwkAssigned > 0 && timetableAssigned > 0) {
			warningText = warningText + " and ";
		}

		// "timetable entries"
		if (timetableAssigned == 1) {
			warningText = warningText
					+ timetableAssigned
					+ " "
					+ getResources().getString(
							R.string.subject_delete_text_timetable_entry);
		} else if (timetableAssigned > 1) {
			warningText = warningText
					+ timetableAssigned
					+ " "
					+ getResources().getString(
							R.string.subject_delete_text_timetable_entries);
		}

		// "is/are linked to [subject], it/they will be set to "No Subject"."
		if (timetableAssigned + hwkAssigned == 1) {
			// only 1 needed so singular
			warningText = warningText
					+ " "
					+ getResources().getString(
							R.string.subject_delete_text_single1)
					+ " "
					+ subjectToDelete.getSubjectName()
					+ getResources().getString(
							R.string.subject_delete_text_single2);
		} else {
			warningText = warningText
					+ " "
					+ getResources().getString(
							R.string.subject_delete_text_plural1)
					+ " "
					+ subjectToDelete.getSubjectName()
					+ this.getResources().getString(
							R.string.subject_delete_text_plural2);
		}

		// only set it if there is at least one
		if (timetableAssigned + hwkAssigned != 0) {
			confirmText.setText(warningText);
		} else {
			confirmText.setText(getResources().getString(
					R.string.subject_delete_text_no_entries));
		}

		Button deleteButton = (Button) confirmDeleteDialog
				.findViewById(R.id.btn_delete_confirm);

		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				confirmDeleteSubject(subjectToDelete);

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

	private void confirmDeleteSubject(SubjectEntry subjectToDelete) {
		// set the homework currently associated with it to no subject
		db.unassignSubject(subjectToDelete, this);
		// get rid of it from database
		db.deleteSubject(subjectToDelete);
		// get rid of it from subject list
		((SubjectEditArrayAdapter) getListAdapter()).updateAll(db
				.getSubjectArrayList(this, false));
	}

	private void createNewSubject() {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.subject_edit);
		dialog.setTitle(R.string.edit_subject);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawableResource(
				R.drawable.gradient_dialog_bg);

		// set up title
		final EditText titleEditText = (EditText) dialog
				.findViewById(R.id.txt_edit_subject_name);

		// set up image view
		final ImageView subjectIcon = (ImageView) dialog
				.findViewById(R.id.img_subject_icon);

		// stores the image id if it changes before committing it
		imageId = R.drawable.question_mark;
		imageLoader.loadBitmap(imageId, subjectIcon);

		// Set up grid view
		final GridView iconGrid = (GridView) dialog
				.findViewById(R.id.icon_grid_view);
		iconGrid.setAdapter(new ImageAdapter(this));

		iconGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				imageId = ((ImageAdapter) iconGrid.getAdapter())
						.getIdAtPosition(position);
				imageLoader.loadBitmap(imageId, subjectIcon);
			}
		});

		// set up OK button
		final Button buttonOK = (Button) dialog.findViewById(R.id.btn_ok);

		// set up the on click listener for the OK button
		buttonOK.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// checks for empty text box
				String title = titleEditText.getText().toString();

				if (title.length() <= 0) {
					// doesn't let you save an empty title
					Toast.makeText(
							SubjectEditActivity.this,
							getResources().getText(
									R.string.empty_subject_warning),
							Toast.LENGTH_SHORT).show();
				} else {
					addNewSubject(title);
					dialog.dismiss();
				}
			}
		});

		// set up Cancel button
		final Button buttonCancel = (Button) dialog
				.findViewById(R.id.btn_cancel);

		// set up the on click listener for the OK button
		buttonCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		// now that the dialog is set up, it's time to show it
		dialog.show();

	}

	/**
	 * adds the title to the database with whatever image is currently imageId
	 * 
	 * @param title
	 */
	@SuppressWarnings("unchecked")
	private void addNewSubject(String title) {
		db.addSubject(new SubjectEntry(0, title, imageId));
		((ArrayAdapter<SubjectEntry>) getListAdapter()).notifyDataSetChanged();
		((SubjectEditArrayAdapter) getListAdapter()).updateAll(db
				.getSubjectArrayList(this, false));
	}

	@SuppressWarnings("unchecked")
	private void setSubjectDetails(SubjectEntry selectedEntry, String newTitle) {

		selectedEntry.setSubjectName(newTitle);
		selectedEntry.setSubjectPicture(imageId);

		((ArrayAdapter<SubjectEntry>) getListAdapter()).notifyDataSetChanged();

		// update database
		db.updateSubject(selectedEntry);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// uses the item id to decide on action to take
		switch (item.getItemId()) {
		case R.id.action_settings:
			launchSettingsActivity();
			break;
		case R.id.action_new_subject:
			createNewSubject();
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		// intent for transporting inputed data back to main activity
		Intent resultIntent = new Intent();

		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}

	/**
	 * allows for editing settings, careful to update on return if needed
	 */
	private void launchSettingsActivity() {
		Intent myIntent = new Intent(this, SettingsActivity.class);

		this.startActivityForResult(myIntent, SettingsActivity.EDIT_SETTINGS_ID);
	}

	/**
	 * called directly from the delete button on the list view item
	 * @param v
	 */
	public void deleteButtonPressed(View v) {
		deleteSubject((Integer) v.getTag());
	}
}
