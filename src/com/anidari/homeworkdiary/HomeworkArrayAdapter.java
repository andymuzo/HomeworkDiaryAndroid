package com.anidari.homeworkdiary;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import com.anromus.homeworkdiary.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeworkArrayAdapter extends ArrayAdapter<HomeworkListEntry> {

	public static int AMOUNT_OF_VIEWS = 3;
	// view IDs returned by getItemViewType()
	private static int TITLE = 0;
	private static int HOMEWORK_ENTRY = 1;
	private static int SPACER_ID = 2;

	// declare the ArrayList that will be passed in the constructor
	ArrayList<HomeworkListEntry> homework;
	private final ImageLoader imageLoader;
	private LayoutInflater mInflater;

	public HomeworkArrayAdapter(Context context, int textViewResourceId,
			ArrayList<HomeworkListEntry> homework) {
		super(context, textViewResourceId, homework);
		imageLoader = new ImageLoader(context);
		this.homework = homework;
		mInflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
	}

	static class ViewHolder {
		protected CheckBox checkBox;
	}

	/**
	 * updating
	 */
	public void updateSingleEntry(int id, HomeworkListEntry replacementHwk) {
		// reload the homework from the database
		if (id != -1) {
			for (HomeworkListEntry hwk : homework) {
				if (hwk.getId() == id) {
					hwk.paste(replacementHwk);
				}
			}
		}
		notifyDataSetChanged();
	}

	public void updateAll(ArrayList<HomeworkListEntry> newHomeworkList) {
		homework.clear();
		homework.addAll(newHomeworkList);

		notifyDataSetChanged();
	}

	// override the view method here
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		HomeworkListEntry h = homework.get(position);

		// assign the view being converted to a local variable
		View v = convertView;

		// first the case of a title, else do a normal entry
		if (h.getIsTitle()) {
			v = mInflater.inflate(R.layout.main_list_title, null);
			TextView titleText = (TextView) v
					.findViewById(R.id.txt_main_list_title);

			if (titleText != null) {
				titleText.setText(h.getHomeworkTitle());
			}

		} else if (h.isSpacer()) {
			v = mInflater.inflate(R.layout.list_item_blank_space, null);
		} else { // must be a homework entry

			// first check to see if the view is null. if so, inflate it.
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.single_homework_list_item, null);
			}

			Calendar cal = Calendar.getInstance(TimeZone.getDefault());

			if (h != null) {

				// This is how you obtain a reference to the TextViews.
				// These TextViews are created in the XML files we defined.

				ImageView image = (ImageView) v
						.findViewById(R.id.subject_picture);
				TextView title = (TextView) v.findViewById(R.id.homework_title);
				TextView dueDate = (TextView) v.findViewById(R.id.due_date);
				TextView subject = (TextView) v.findViewById(R.id.subject_name);

				// check to see if each individual textview is null.
				// if not, assign some text!
				if (image != null) {
					// TODO: find a sensible way to set the required width and
					// height of the images
					// image.setImageBitmap(BitmapTools.decodeSampledBitmapFromResource(context.getResources(),
					// h.getSubjectImageResource(), 100, 100));
					imageLoader.loadBitmap(h.getSubjectImageResource(), image);

				}
				if (title != null) {
					title.setText(h.getHomeworkTitle());
				}
				if (dueDate != null) {
					if (h.getIsCompleted() && h.getFinalGrade() != null) {
						// displays the final grade
						dueDate.setText(h.getFinalGrade());
					} else {
						// displays the due date
						cal.setTimeInMillis(h.getDueDate());
						dueDate.setText(cal.getDisplayName(
								Calendar.DAY_OF_WEEK, Calendar.LONG,
								Locale.getDefault())
								+ ", "
								+ cal.get(Calendar.DATE)
								+ AddTh.getTh(cal.get(Calendar.DATE))
								+ " of "
								+ cal.getDisplayName(Calendar.MONTH,
										Calendar.LONG, Locale.getDefault()));
					}
				}
				if (subject != null) {
					subject.setText(h.getSubjectName());
				}
			}
			
			// rounded corners for end of section
			// red for overdue, green for completed
			if (h.isEndOfSection()) {
				if (h.getIsCompleted()) {
					// completed
					v.setBackgroundResource(R.drawable.timetable_list_end_01);
				} else if (h.isOverdue()) {
					// Overdue
					v.setBackgroundResource(R.drawable.timetable_list_end_08);
				} else {
					// Neutral
					v.setBackgroundResource(R.drawable.gradient_body_end);
				}
			} else {
				if (h.getIsCompleted()) {
					// completed
					v.setBackgroundResource(R.drawable.timetable_list_01);
				} else if (h.isOverdue()) {
					// Overdue
					v.setBackgroundResource(R.drawable.timetable_list_08);
				} else {
					// Neutral
					v.setBackgroundResource(R.drawable.gradient_body);
				}
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
		HomeworkListEntry hwk = homework.get(itemId);
		if (hwk.getIsTitle()) {
			return TITLE;
		} else if (hwk.isSpacer()) {
			return SPACER_ID;
		} else {
			return HOMEWORK_ENTRY;
		}
	}

	@Override
	public boolean isEnabled(int position) {
		// titles are disabled in terms of selection
		HomeworkListEntry hwk = homework.get(position);
		if (hwk.getIsTitle() || hwk.isSpacer()) {
			return false;
		} else
			return true;
	}

	/**
	 * used to get the entry needed to show the detailed homework view
	 * 
	 * @param id
	 * @return HomeworkListEntry at given id
	 */
	public HomeworkListEntry getHomeworkEntryById(int id) {
		return homework.get(id);
	}

}
