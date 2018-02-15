package com.anidari.homeworkdiary;

import java.util.ArrayList;

import com.anromus.homeworkdiary.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TimeTableListArrayAdapter extends ArrayAdapter<TimetableEntry> {

	public static int AMOUNT_OF_VIEWS = 4;
	// view IDs returned by getItemViewType()
	private static int TITLE_ID = 0;
	private static int ENTRY_ID = 1;
	private static int SPACER_ID = 2;
	private static int WEEK_DIVIDER_ID = 3;

	// declare the ArrayList that will be passed in the constructor
	ArrayList<TimetableEntry> timetable;
	private final ImageLoader imageLoader;
	private LayoutInflater mInflater;
	private long defaultSubjectID;

	// visibility flags
	boolean showSubject;
	boolean showLocation;
	boolean showTutor;

	public TimeTableListArrayAdapter(Context context, int textViewResourceId,
			ArrayList<TimetableEntry> tt) {
		super(context, textViewResourceId, tt);
		imageLoader = new ImageLoader(context);
		this.timetable = tt;
		mInflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);

		updatePreferences(context);
	}

	public void updatePreferences(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		defaultSubjectID = preferences.getLong(
				SettingsActivity.PREF_NO_SUBJECT_ID, -1);

		showSubject = preferences.getBoolean(
				SettingsActivity.PREF_SHOW_SUBJECT_PORTRAIT, true);
		showLocation = preferences.getBoolean(
				SettingsActivity.PREF_SHOW_LOCATION_PORTRAIT, true);
		showTutor = preferences.getBoolean(
				SettingsActivity.PREF_SHOW_TUTOR_PORTRAIT, true);
	}

	/**
	 * updating
	 */
	public void updateSingleEntry(int id, TimetableEntry replacementTT) {
		// reload the homework from the database
		if (id != -1) {
			for (TimetableEntry tt : timetable) {
				if (tt.getId() == id) {
					tt.paste(replacementTT);
				}
			}
		}
		notifyDataSetChanged();
	}

	public void updateAll(ArrayList<TimetableEntry> newTimetableList,
			Context context) {
		timetable.clear();
		updatePreferences(context);
		timetable.addAll(newTimetableList);

		notifyDataSetChanged();
	}

	// override the view method here
	@SuppressLint("CutPasteId")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		TimetableEntry tt = timetable.get(position);

		// assign the view being converted to a local variable
		View v = convertView;

		switch (tt.getType()) {
		// first the case of a title, else do a normal entry
		
		
		case TITLE:
			v = mInflater.inflate(R.layout.main_list_title, null);
			TextView titleText = (TextView) v
					.findViewById(R.id.txt_main_list_title);

			if (titleText != null) {
				titleText.setText(tt.getSessionName());
			}
			// set the title to the regular header gradient
			v.setBackgroundResource(R.drawable.gradient_title);
			
			break;
		case WEEK_DIVIDER:
			v = mInflater.inflate(R.layout.main_list_title, null);
			TextView titleTextDiv = (TextView) v
					.findViewById(R.id.txt_main_list_title);

			if (titleTextDiv != null) {
				titleTextDiv.setText(tt.getSessionName());
				titleTextDiv.setTextAppearance(getContext(), R.style.timeTableTitleText);
			}
			// set the background to the header dark color
			v.setBackgroundResource(R.drawable.gradient_tt_week_divider);
			
			break;
		case SPACER:
			v = mInflater.inflate(R.layout.list_item_blank_space, null);
			break;
		case ENTRY:

			// first check to see if the view is null. if so, inflate it.
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.time_table_list_item, null);
			}

			if (v != null) {

				ImageView image = (ImageView) v
						.findViewById(R.id.subject_picture);
				TextView subject = (TextView) v.findViewById(R.id.txt_subject);
				TextView location = (TextView) v.findViewById(R.id.txt_location);
				TextView time = (TextView) v.findViewById(R.id.txt_time_start_end);
				TextView tutor = (TextView) v.findViewById(R.id.txt_tutor);

				// check to see if each individual textview is null.
				// if not, assign some text!
				if (image != null) {
					imageLoader.loadBitmap(tt.getSubjectImageResourceID(), image);

				}
				if (subject != null) {
					if (tt.getSubjectID() != defaultSubjectID) {
					subject.setText(tt.getSubjectName());
					} else {
						subject.setText(tt.getSessionName());
					}
				}
				if (location != null) {
					location.setText(tt.getLocation());
				}
				if (time != null) {
					time.setText(tt.getQuiteShortDisplayTime());
				}
				if (tutor != null) {
					tutor.setText(tt.getTutor());
				}
				
				// which views to show
				subject.setVisibility(showSubject ? View.VISIBLE : View.GONE);
				location.setVisibility(showLocation ? View.VISIBLE : View.GONE);
				tutor.setVisibility(showTutor ? View.VISIBLE : View.GONE);
			
				// sets the background shape
				v.setBackgroundResource(ColourPickerLayout.getColourId(
						tt.getTimeTableColour(), 
						tt.isEndOfSection() ? ColourPickerLayout.ROUND_BOTTOM : ColourPickerLayout.SQUARE));
			}
		}
		// the view must be returned to our activity
		return v;

	}

	/**
	 * overridden to return the amount of different views
	 * 
	 */
	@Override
	public int getViewTypeCount() {
		return AMOUNT_OF_VIEWS;
	}

	/**
	 * returns the id of the view for the given list item
	 */
	@Override
	public int getItemViewType(int itemId) {
		TimetableEntry tt = timetable.get(itemId);
		switch (tt.getType()) {
		case TITLE:
			return TITLE_ID;
		case SPACER:
			return SPACER_ID;
		case WEEK_DIVIDER:
			return WEEK_DIVIDER_ID;
		case ENTRY:
			return ENTRY_ID;
		default:
			return SPACER_ID;
		}
	}

	@Override
	public boolean isEnabled(int position) {
		// titles are disabled in terms of selection
		TimetableEntry tt = timetable.get(position);
		switch (tt.getType()) {
		case ENTRY:
			return true;
		case TITLE:
		case SPACER:
		case WEEK_DIVIDER:
		default:
			return false;
		}
	}

	/**
	 * used to get the entry needed to show the detailed homework view
	 * 
	 * @param id
	 * @return HomeworkListEntry at given id
	 */
	public TimetableEntry getHomeworkEntryById(int id) {
		return timetable.get(id);
	}

}
