package com.anidari.homeworkdiary;

import java.util.ArrayList;

import com.anromus.homeworkdiary.R;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeworkDetailArrayAdapter extends
		ArrayAdapter<HomeworkDetailListEntry> {

	public static int AMOUNT_OF_VIEWS = 7;
	// view IDs returned by getItemViewType()
	private static int TITLE = 0;
	private static int DUE_DATE = 1;
	private static int FINISHED_OR_REMINDER = 2;
	private static int TO_DO_TITLE = 3;
	private static int TO_DO_ENTRY = 4;
	private static int TODO_ADD_NEW = 5;
	private static int SPACER_ID = 6;
	private static int DEFAULT = -1;

	// declare the ArrayList that will be passed in the constructor
	ArrayList<HomeworkDetailListEntry> homework;
	private final ImageLoader imageLoader;
	private LayoutInflater mInflater;

	public HomeworkDetailArrayAdapter(Context context, int textViewResourceId,
			ArrayList<HomeworkDetailListEntry> homework) {
		super(context, textViewResourceId, homework);
		imageLoader = new ImageLoader(context);
		this.homework = homework;
		mInflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
	}

	static class ViewHolder {
		protected CheckBox checkBox;
	}

	// override the view method here
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		/*
		 * position refers to the current position in the ArrayList
		 */
		HomeworkDetailListEntry h = homework.get(position);

		ImageView continueIcon = null;

		switch (h.getType()) {
		case TITLE:
			convertView = mInflater.inflate(
					R.layout.list_item_assignment_subject, null);
			// This is how you obtain a reference to the TextViews.
			// These TextViews are created in the XML files we defined.

			ImageView image = (ImageView) convertView
					.findViewById(R.id.subject_picture);
			continueIcon = (ImageView) convertView
					.findViewById(R.id.continue_icon);
			TextView title = (TextView) convertView
					.findViewById(R.id.list_homework_title);
			TextView subject = (TextView) convertView
					.findViewById(R.id.list_subject_title);

			// check to see if each individual textview is null.
			// if not, assign some text!
			if (image != null) {
				imageLoader.loadBitmap(h.getImage(), image);
			}
			if (continueIcon != null) {
				imageLoader.loadBitmap(R.drawable.small_arrow_right,
						continueIcon);
			}
			if (title != null) {
				title.setText(h.getText1());
			}
			if (subject != null) {
				subject.setText(h.getText2());
			}

			break;
		case DUE_DATE:
			convertView = mInflater.inflate(R.layout.list_item_due_date, null);
			TextView date = (TextView) convertView
					.findViewById(R.id.list_homework_due_date);
			continueIcon = (ImageView) convertView
					.findViewById(R.id.continue_icon);

			if (continueIcon != null) {
				imageLoader.loadBitmap(R.drawable.small_arrow_right,
						continueIcon);
			}
			if (date != null) {
				date.setText(HumanReadableDate.getDateString(h.getDueDate()));
			}

			break;
		case FINISHED_GRADE:
			convertView = mInflater.inflate(R.layout.list_item_finished_grade,
					null);

			final ViewHolder viewHolder = new ViewHolder();

			viewHolder.checkBox = (CheckBox) convertView
					.findViewById(R.id.list_homework_finished);
			continueIcon = (ImageView) convertView
					.findViewById(R.id.continue_icon);
			TextView finishedText = (TextView) convertView
					.findViewById(R.id.list_finished_text);

			if (continueIcon != null) {
				imageLoader.loadBitmap(R.drawable.small_arrow_right,
						continueIcon);
			}
			if (viewHolder.checkBox != null) {
				viewHolder.checkBox.setChecked(h.getIsTicked());
			}
			if (finishedText != null && h.getText1() != null) {
				finishedText.setText(h.getText1());
			}

			viewHolder.checkBox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							HomeworkDetailListEntry element = (HomeworkDetailListEntry) viewHolder.checkBox
									.getTag();
							element.setIsTicked(buttonView.isChecked());
						}
					});

			convertView.setTag(viewHolder);
			viewHolder.checkBox.setTag(homework.get(position));

			break;
		case REMINDER_SET:
			convertView = mInflater.inflate(
					R.layout.list_item_notification_set, null);

			final ViewHolder viewHolderRemind = new ViewHolder();

			viewHolderRemind.checkBox = (CheckBox) convertView
					.findViewById(R.id.chk_list_notification_set);

			if (viewHolderRemind.checkBox != null) {
				viewHolderRemind.checkBox.setChecked(h.getIsTicked());
			}

			viewHolderRemind.checkBox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							HomeworkDetailListEntry element = (HomeworkDetailListEntry) viewHolderRemind.checkBox
									.getTag();
							element.setIsTicked(buttonView.isChecked());
						}
					});

			convertView.setTag(viewHolderRemind);
			viewHolderRemind.checkBox.setTag(homework.get(position));

			break;
		case SPACER:
			convertView = mInflater.inflate(R.layout.list_item_blank_space,
					null);
			if (h.getIsTicked() == true) {
				convertView.setBackgroundResource(R.drawable.gradient_divider);
			}

			break;
		case TO_DO_TITLE:
			convertView = mInflater
					.inflate(R.layout.list_item_todo_title, null);

			break;
		case TO_DO_ENTRY:
			convertView = mInflater.inflate(R.layout.list_item_todo, null);

			final ViewHolder viewHolderToDo = new ViewHolder();

			viewHolderToDo.checkBox = (CheckBox) convertView
					.findViewById(R.id.checkBox1);
			ImageView moveIcon = (ImageView) convertView
					.findViewById(R.id.move_icon);
			final TextView todoText = (TextView) convertView
					.findViewById(R.id.list_todo_text);

			if (moveIcon != null) {
				imageLoader.loadBitmap(R.drawable.arrow_up_down, moveIcon);
			}
			if (viewHolderToDo.checkBox != null) {
				viewHolderToDo.checkBox.setChecked(h.getIsTicked());
			}
			if (todoText != null) {
				todoText.setText(h.getText1());
				if (h.getIsTicked()) {
					todoText.setPaintFlags(todoText.getPaintFlags()
							| Paint.STRIKE_THRU_TEXT_FLAG);
				}
			}

			viewHolderToDo.checkBox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							HomeworkDetailListEntry element = (HomeworkDetailListEntry) viewHolderToDo.checkBox
									.getTag();
							element.setIsTicked(buttonView.isChecked());

							if (isChecked) {
								todoText.setPaintFlags(todoText.getPaintFlags()
										| Paint.STRIKE_THRU_TEXT_FLAG);
							} else {
								todoText.setPaintFlags(todoText.getPaintFlags()
										& (~Paint.STRIKE_THRU_TEXT_FLAG));
							}
						}
					});

			convertView.setTag(viewHolderToDo);
			viewHolderToDo.checkBox.setTag(homework.get(position));

			break;
		case TODO_ADD_NEW:
			convertView = mInflater.inflate(R.layout.list_item_todo_new, null);
			ImageView addIcon = (ImageView) convertView
					.findViewById(R.id.new_icon);
			if (addIcon != null) {
				imageLoader.loadBitmap(R.drawable.plus_icon, addIcon);
			}
			break;
		}

		return convertView;

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

		switch (homework.get(itemId).getType()) {
		case TITLE:
			return TITLE;
		case DUE_DATE:
			return DUE_DATE;
		case FINISHED_GRADE:
		case REMINDER_SET:
			return FINISHED_OR_REMINDER;
		case TO_DO_TITLE:
			return TO_DO_TITLE;
		case TO_DO_ENTRY:
			return TO_DO_ENTRY;
		case TODO_ADD_NEW:
			return TODO_ADD_NEW;
		case SPACER:
			return SPACER_ID;
		default:
			return DEFAULT;
		}
	}

	@Override
	public boolean isEnabled(int position) {
		if (position == 0 || position == 2 || position == 6 || position == 7)
			return false;
		else
			return true;
	}

	/**
	 * used to get the entry needed to show the detailed homework view
	 * 
	 * @param id
	 * @return HomeworkListEntry at given id
	 */
	public HomeworkDetailListEntry getHomeworkEntryById(int id) {
		return homework.get(id);
	}

}
