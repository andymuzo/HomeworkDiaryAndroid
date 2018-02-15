package com.anidari.homeworkdiary;

import com.anromus.homeworkdiary.R;

import android.content.Context;
import android.content.res.Configuration;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * creates a 5x2 grid of colours to select or a 10 x 1 grid. Call setSingleRow()
 * to decide
 * 
 * @author ajrog_000
 * 
 */
public class ColourPickerLayout extends LinearLayout implements OnClickListener {

	public static final int SQUARE = 0;
	public static final int ROUND_BOTTOM = 1;
	public static final int SMALL_ROUNDED = 2;
	
	private final int AMOUNT_OF_COLOURS = 10;
	private final int ROW_1_ID = 100;
	private final int ROW_2_ID = 101;
	RadioButton[] selection;
	RadioGroup[] groupRow;

	private boolean isLandscape;

	public ColourPickerLayout(Context context) {
		super(context);
		setOrientationFlag(context);
		setup(context);
	}

	public ColourPickerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientationFlag(context);
		setup(context);
	}

	/**
	 * gets the id for the selected colour from the check box selected
	 * type should be one of ROUND_BOTTOM, SMALL_ROUNDED or SQUARE
	 * @param selection
	 * @param roundedEdges
	 * @return
	 */
	static public int getColourId(int selection, int type) {
		int id;

		switch (selection) {
		case 0:
			if (type == ROUND_BOTTOM) {
				id = R.drawable.timetable_list_end_00;
			} else if (type == SQUARE) {
				id = R.drawable.timetable_list_00;
			} else {
				id = R.drawable.timetable_horiz_00;
			}
			break;
		case 1:
			if (type == ROUND_BOTTOM) {
				id = R.drawable.timetable_list_end_01;
			} else if (type == SQUARE) {
				id = R.drawable.timetable_list_01;
			} else {
				id = R.drawable.timetable_horiz_01;
			}
			break;
		case 2:
			if (type == ROUND_BOTTOM) {
				id = R.drawable.timetable_list_end_02;
			} else if (type == SQUARE) {
				id = R.drawable.timetable_list_02;
			} else {
				id = R.drawable.timetable_horiz_02;
			}
			break;
		case 3:
			if (type == ROUND_BOTTOM) {
				id = R.drawable.timetable_list_end_03;
			} else if (type == SQUARE) {
				id = R.drawable.timetable_list_03;
			} else {
				id = R.drawable.timetable_horiz_03;
			}
			break;
		case 4:
			if (type == ROUND_BOTTOM) {
				id = R.drawable.timetable_list_end_04;
			} else if (type == SQUARE) {
				id = R.drawable.timetable_list_04;
			} else {
				id = R.drawable.timetable_horiz_04;
			}
			break;
		case 5:
			if (type == ROUND_BOTTOM) {
				id = R.drawable.timetable_list_end_05;
			} else if (type == SQUARE) {
				id = R.drawable.timetable_list_05;
			} else {
				id = R.drawable.timetable_horiz_05;
			}
			break;
		case 6:
			if (type == ROUND_BOTTOM) {
				id = R.drawable.timetable_list_end_06;
			} else if (type == SQUARE) {
				id = R.drawable.timetable_list_06;
			} else {
				id = R.drawable.timetable_horiz_06;
			}
			break;
		case 7:
			if (type == ROUND_BOTTOM) {
				id = R.drawable.timetable_list_end_07;
			} else if (type == SQUARE) {
				id = R.drawable.timetable_list_07;
			} else {
				id = R.drawable.timetable_horiz_07;
			}
			break;
		case 8:
			if (type == ROUND_BOTTOM) {
				id = R.drawable.timetable_list_end_08;
			} else if (type == SQUARE) {
				id = R.drawable.timetable_list_08;
			} else {
				id = R.drawable.timetable_horiz_08;
			}
			break;
		case 9:
			if (type == ROUND_BOTTOM) {
				id = R.drawable.timetable_list_end_09;
			} else if (type == SQUARE) {
				id = R.drawable.timetable_list_09;
			} else {
				id = R.drawable.timetable_horiz_09;
			}
			break;
		default:
			if (type == ROUND_BOTTOM) {
				id = R.drawable.timetable_list_end_00;
			} else if (type == SQUARE) {
				id = R.drawable.timetable_list_00;
			} else {
				id = R.drawable.timetable_horiz_00;
			}
			break;
		}

		return id;
	}

	public void setup(Context context) {
		// set the orientation of the linear layout
		setOrientation(VERTICAL);
		// sets up the radio groups
		if (isLandscape) {
			groupRow = new RadioGroup[1];
			groupRow[0] = new RadioGroup(context);
			groupRow[0].setOrientation(HORIZONTAL);
			groupRow[0].setId(ROW_1_ID);
		} else {
			// setup the groups
			groupRow = new RadioGroup[2];
			groupRow[0] = new RadioGroup(context);
			groupRow[0].setOrientation(HORIZONTAL);
			groupRow[0].setId(ROW_1_ID);
			groupRow[1] = new RadioGroup(context);
			groupRow[1].setOrientation(HORIZONTAL);
			groupRow[1].setId(ROW_2_ID);
		}

		// setup the layout params for the buttons
		RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
				RadioGroup.LayoutParams.WRAP_CONTENT,
				RadioGroup.LayoutParams.WRAP_CONTENT, 1.0f);

		// set up the radio buttons
		selection = new RadioButton[10];
		for (int i = 0; i < AMOUNT_OF_COLOURS; i++) {
			selection[i] = new RadioButton(context);
			selection[i].setText("");
			selection[i].setBackgroundResource(getColourId(i, SQUARE));
			selection[i].setId(i);
			selection[i].setOnClickListener(this);
			selection[i].setGravity(Gravity.CENTER);
			if (isLandscape) {
				groupRow[0].addView(selection[i], params);
			} else {
				groupRow[i / (AMOUNT_OF_COLOURS / 2)].addView(selection[i],
						params);
			}
		}

		addView(groupRow[0]);
		if (!isLandscape)
			addView(groupRow[1]);
	}

	/**
	 * true for one long row of the colours, false for a 5x2 row
	 * 
	 * @param singleRow
	 */
	private void setOrientationFlag(Context context) {
		isLandscape = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}

	@Override
	public void onClick(View v) {
		if (!isLandscape) {
			// when one row is selected, blanks the other
			switch (v.getId()) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				// blank row 2
				groupRow[1].clearCheck();
				break;
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
				// blank row 1
				groupRow[0].clearCheck();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * returns the grid number of the currently selected item rather than the
	 * R.color... id note that if nothing is selected it reurns -1
	 * 
	 * @return
	 */
	public int getSelectedId() {
		int id = groupRow[0].getCheckedRadioButtonId();
		if (!isLandscape && id == -1) {
			id = groupRow[1].getCheckedRadioButtonId();
		}
		return id;
	}

	/**
	 * returns true if any of the 10 colours are selected
	 * 
	 * @return
	 */
	public boolean isAnySelected() {
		// check the first row
		boolean isSelected = (groupRow[0].getCheckedRadioButtonId() != -1);
		// if the second row exists and nothing was selected in the first row
		if (!isLandscape && !isSelected) {
			isSelected = (groupRow[1].getCheckedRadioButtonId() != -1);
		}
		return isSelected;	
	}

	/**
	 * checks the right radio button given the id = 0 - 9
	 * 
	 * @param id
	 */
	public void select(int id) {
		// corrects for out of range selections
		id = Math.abs(id % AMOUNT_OF_COLOURS);
		if (!isLandscape) {
		groupRow[id / (AMOUNT_OF_COLOURS / 2)].check(id
				% (AMOUNT_OF_COLOURS / 2));
		}
		else {
			groupRow[0].check(id);
		}
	}
}
