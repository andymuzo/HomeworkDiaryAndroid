package com.anidari.homeworkdiary;

import com.anromus.homeworkdiary.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.view.View.OnClickListener;

/**
 * Creates a linear layout containing checkboxes for selecting the 7 days of the
 * week and a few methods for accessing them
 * 
 * @author ajrog_000
 * 
 */
public class DaySelecterLayout extends LinearLayout implements OnClickListener {

	private CheckBox[] checkBoxes;
	private boolean singleDayOnly;
	private boolean hasChanged;
	private View startDivider;
	private View endDivider;

	public DaySelecterLayout(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);

		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.LEFT);

		singleDayOnly = true;
		hasChanged = false;

		setupCheckBoxes(context);
	}

	private void setupCheckBoxes(Context context) {
		checkBoxes = new CheckBox[7];

		LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
				TableRow.LayoutParams.MATCH_PARENT, 2);

		startDivider = new View(context);
		startDivider.setBackgroundColor(getResources().getColor(R.color.complimentary_orange));
		addView(startDivider, dividerParams);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				TableRow.LayoutParams.WRAP_CONTENT,
				TableRow.LayoutParams.WRAP_CONTENT);

		for (int i = 0; i < checkBoxes.length; i++) {
			checkBoxes[i] = new CheckBox(context);
			checkBoxes[i].setText(getResources().getStringArray(
					R.array.tt_days_full)[i]);
			checkBoxes[i].setOnClickListener(this);
			checkBoxes[i].setGravity(Gravity.CENTER);

			addView(checkBoxes[i], params);
		}

		checkBoxes[0].setId(R.id.check_box_sunday);
		checkBoxes[1].setId(R.id.check_box_monday);
		checkBoxes[2].setId(R.id.check_box_tuesday);
		checkBoxes[3].setId(R.id.check_box_wednesday);
		checkBoxes[4].setId(R.id.check_box_thursday);
		checkBoxes[5].setId(R.id.check_box_friday);
		checkBoxes[6].setId(R.id.check_box_saturday);

		endDivider = new View(context);
		endDivider.setBackgroundColor(getResources().getColor(R.color.complimentary_orange));
		addView(endDivider, dividerParams);

	}

	public void setSingleDayOnly(boolean isSingleDayOnly) {
		singleDayOnly = isSingleDayOnly;
	}

	/**
	 * used for putting the selections into a the TimetableEntry class
	 * 
	 * @return
	 */
	public boolean[] getAsAnArray() {
		boolean[] checkArray = new boolean[7];

		int i = 0;
		for (CheckBox check : checkBoxes) {
			checkArray[i++] = check.isChecked();
		}

		return checkArray;
	}

	public boolean hasChanged() {
		return hasChanged;
	}

	public void checkBoxWithId(int id) {
		// check the position is within range first
		for (CheckBox check : checkBoxes) {
			if (check.getId() == id) {

			} else if (singleDayOnly) {
				// check to see if more than one can be selected
				// blank everything else if not
				check.setChecked(false);
			}
		}
		hasChanged = true;
	}

	public void clearAll() {
		for (CheckBox check : checkBoxes) {
			check.setChecked(false);
		}
		hasChanged = true;
	}

	public void clearAllButOne() {
		boolean firstNotChecked = true;

		for (CheckBox check : checkBoxes) {
			// keeps just the first one checked, blanks the rest
			if (firstNotChecked && check.isChecked()) {
				firstNotChecked = false;
			} else {
				check.setChecked(false);
			}
		}
		hasChanged = true;
	}

	/**
	 * returns true is at least one box is selected
	 * 
	 * @return
	 */
	public boolean isAnythingSelected() {
		boolean isSelected = false;
		for (CheckBox check : checkBoxes) {
			if (check.isChecked()) {
				isSelected = true;
			}
		}

		return isSelected;
	}

	public void setDays(boolean[] days) {
		for (int i = 0; i < checkBoxes.length; i++) {
			checkBoxes[i].setChecked(days[i]);
		}
	}

	public void setDays(String activeDays) {
		// store them as "ynnyyny" for SMTWTFS
		for (int i = 0; i < activeDays.length(); i++) {
			if (activeDays.charAt(i) == 'y') {
				// set positive
				checkBoxes[i].setChecked(true);
			} else {
				// set negative
				checkBoxes[i].setChecked(false);
			}
		}
	}

	public String getDaysAsString() {
		String activeDaysString = new String();

		// store them as "ynnyyny" for SMTWTFS
		for (int i = 0; i < checkBoxes.length; i++) {
			if (checkBoxes[i].isChecked()) {
				// set positive
				activeDaysString = activeDaysString + 'y';
			} else {
				// set negative
				activeDaysString = activeDaysString + 'n';
			}
		}
		return activeDaysString;
	}

	@Override
	public void onClick(View v) {
		hasChanged = true;
		checkBoxWithId(v.getId());
	}
}
