package com.anidari.homeworkdiary;

import java.util.ArrayList;

import com.anromus.homeworkdiary.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class MainMenuArrayAdapter extends ArrayAdapter<MainMenuItem> {

	public static int AMOUNT_OF_VIEWS = 2;
	// view IDs returned by getItemViewType()
	private static int SUBJECT_ENTRY = 0;
	private static int SPACER = 1;

	// declare the ArrayList that will be passed in the constructor
	ArrayList<MainMenuItem> items;
	private final ImageLoader imageLoader;
	private LayoutInflater mInflater;

	public MainMenuArrayAdapter(Context context, int textViewResourceId,
			ArrayList<MainMenuItem> items) {
		super(context, textViewResourceId, items);
		imageLoader = new ImageLoader(context);
		this.items = items;
		mInflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
	}

	static class ViewHolder {
		protected CheckBox checkBox;
	}

	public void updateAll(ArrayList<MainMenuItem> newItems) {
		items.clear();
		items.addAll(newItems);

		notifyDataSetChanged();
	}

	// override the view method here
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		MainMenuItem item = items.get(position);

		// assign the view being converted to a local variable
		View v = convertView;

		if (item.isFlaggedAsSpacer()) {
			// inflate the spacer layout
			v = mInflater.inflate(R.layout.list_item_blank_space, null);
		} else {
			// inflate the layout
			if (v == null) {
				v = mInflater.inflate(R.layout.main_menu_list_item, null);
			}

			// set the right background colour
			FrameLayout frame = (FrameLayout) v
					.findViewById(R.id.frame_main_menu_item);
			switch (item.getType()) {
			case TIMETABLE:
				frame.setBackgroundResource(R.drawable.gradient_main_menu_colour_two);
				break;
			case HOMEWORK:
				frame.setBackgroundResource(R.drawable.gradient_main_menu_colour_three);
				break;
			case SUBJECT:
				frame.setBackgroundResource(R.drawable.gradient_main_menu_colour_one);
				break;
			default:
				break;
			}

			// add the title into the text view
			TextView title = (TextView) v.findViewById(R.id.txt_title);

			if (title != null) {
				title.setText(item.getTitle());
			}

			// add the details into the text view
			TextView details = (TextView) v.findViewById(R.id.txt_details);

			if (details != null) {
				details.setText(item.getDetails());
			}

			// add the image
			// TODO: add in the image actually needing loading
			
			ImageView image = (ImageView) v
					.findViewById(R.id.img_background_main_menu_item);

			if (image != null) {
				switch (item.getType()) {
				case TIMETABLE:
					image.setImageResource(R.drawable.menu_logo_timetable);
					break;
				case HOMEWORK:
					image.setImageResource(R.drawable.menu_logo_homework);
					break;
				case SUBJECT:
					image.setImageResource(R.drawable.menu_logo_subject);
					break;
				default:
					break;
				}
			}

			
			// add the continue icon
			ImageView continueIcon = (ImageView) v
					.findViewById(R.id.continue_icon);

			if (continueIcon != null) {
				imageLoader.loadBitmap(R.drawable.small_arrow_right_white,
						continueIcon);
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
		if (items.get(itemId).isFlaggedAsSpacer()) {
			return SPACER;
		} else
			return SUBJECT_ENTRY;
	}

	/**
	 * used to get the item being pressed to start the activity
	 * 
	 * @param id
	 * @return HomeworkListEntry at given id
	 */
	public MainMenuItem getMenuItemById(int id) {
		return items.get(id);
	}

	@Override
	public boolean isEnabled(int position) {
		if (items.get(position).isFlaggedAsSpacer()) {
			return false;
		} else
			return true;
	}

}
