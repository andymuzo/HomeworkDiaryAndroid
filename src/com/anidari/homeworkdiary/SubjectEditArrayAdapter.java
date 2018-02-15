package com.anidari.homeworkdiary;

import java.util.ArrayList;

import com.anromus.homeworkdiary.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class SubjectEditArrayAdapter extends ArrayAdapter<SubjectEntry> {

	public static int AMOUNT_OF_VIEWS = 2;
	// view IDs returned by getItemViewType()
	private static int SUBJECT_ENTRY = 0;
	private static int SPACER = 1;

	// declare the ArrayList that will be passed in the constructor
	ArrayList<SubjectEntry> subjects;
	private final ImageLoader imageLoader;
	private LayoutInflater mInflater;

	public SubjectEditArrayAdapter(Context context, int textViewResourceId,
			ArrayList<SubjectEntry> subjects) {
		super(context, textViewResourceId, subjects);
		imageLoader = new ImageLoader(context);
		this.subjects = subjects;
		mInflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
	}

	static class ViewHolder {
		protected CheckBox checkBox;
	}

	/**
	 * updating
	 */
	public void updateSingleEntry(int id, SubjectEntry replacementSubject) {
		// reload the homework from the database
		if (id != -1) {
			for (SubjectEntry subject : subjects) {
				if (subject.getDatabaseID() == id) {
					subject.paste(replacementSubject);
				}
			}
		}
		notifyDataSetChanged();
	}

	public void updateAll(ArrayList<SubjectEntry> newSubjectList) {
		subjects.clear();
		subjects.addAll(newSubjectList);

		notifyDataSetChanged();
	}

	// override the view method here
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		SubjectEntry s = subjects.get(position);

		// assign the view being converted to a local variable
		View v = convertView;

		if (s.isSpacer()) {
			// inflate the layout
			v = mInflater.inflate(R.layout.list_item_blank_space, null);
		} else {
			// inflate the layout
			if (v == null) {
				v = mInflater.inflate(R.layout.subject_list_item, null);
			}
			
			// add the subject name into the text view
			TextView txtSubjectName = (TextView) v
					.findViewById(R.id.txt_subject_title);

			if (txtSubjectName != null) {
				txtSubjectName.setText(s.getSubjectName());
			}

			// add the image
			ImageView image = (ImageView) v
					.findViewById(R.id.img_subject_picture);

			if (image != null) {
				imageLoader.loadBitmap(s.getSubjectPicture(), image);
			}
			
			// adds the delete icon
			ImageView deleteIcon = (ImageView) v.findViewById(R.id.img_delete_icon);
			
			if (image != null) {
				imageLoader.loadBitmap(R.drawable.delete_action_icon, deleteIcon);
			}
			
			deleteIcon.setTag(s.getDatabaseID());
			
			if (s.isTop() && s.isBottom()){
				v.setBackgroundResource(R.drawable.gradient_body_start_end);
			}
			else if (s.isTop()) {
				v.setBackgroundResource(R.drawable.gradient_body_start);
			} 
			else if (s.isBottom()) {
				v.setBackgroundResource(R.drawable.gradient_body_end);
			} 
			else {
				v.setBackgroundResource(R.drawable.gradient_body);

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
		if (itemId == subjects.size() - 1 || itemId == 0) {
			return SPACER;
		} else
			return SUBJECT_ENTRY;
	}

	/**
	 * used to get the entry needed to show the edit subject view
	 * 
	 * @param id
	 * @return HomeworkListEntry at given id
	 */
	public SubjectEntry getSubjectEntryById(int id) {
		return subjects.get(id);
	}
	
	@Override
	public boolean isEnabled(int position) {
		if (position == 0 || position == subjects.size() - 1)
			return false;
		else
			return true;
	}

}
