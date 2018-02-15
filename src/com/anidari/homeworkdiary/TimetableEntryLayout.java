package com.anidari.homeworkdiary;

import com.anromus.homeworkdiary.R;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TimetableEntryLayout extends LinearLayout {

	private final float timeTextSize = 10f;
	private final float textSize = 12f;

	private TextView timeText;
	private TextView subjectText;
	private TextView locationText;
	private TextView tutorText;

	private long sessionDate; // this is used to help identify the entry that is
								// was created from

	public TimetableEntryLayout(Context context) {
		super(context);

		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER_VERTICAL);

		timeText = new TextView(context);
		timeText.setText("with");
		timeText.setHorizontalFadingEdgeEnabled(true);
		timeText.setMaxLines(1);
		timeText.setEllipsize(null);
		timeText.setTextSize(timeTextSize);
		timeText.setBackground(getResources().getDrawable(
				R.drawable.timetable_entry_time_header));
		timeText.setTextColor(getResources().getColor(R.color.ghost_white));
		timeText.setGravity(Gravity.CENTER);
		timeText.setPadding(2, 0, 2, 0);
		addView(timeText);

		subjectText = new TextView(context);
		subjectText.setText("title test");
		subjectText.setHorizontalFadingEdgeEnabled(true);
		subjectText.setMaxLines(1);
		subjectText.setEllipsize(null);
		subjectText.setTextSize(textSize);
		subjectText.setPadding(2, 0, 2, 0);
		addView(subjectText);

		locationText = new TextView(context);
		locationText.setText("location");
		locationText.setHorizontalFadingEdgeEnabled(true);
		locationText.setMaxLines(1);
		locationText.setEllipsize(null);
		locationText.setTextSize(textSize);
		locationText.setPadding(2, 0, 2, 0);
		addView(locationText);

		tutorText = new TextView(context);
		tutorText.setText("location");
		tutorText.setHorizontalFadingEdgeEnabled(true);
		tutorText.setMaxLines(1);
		tutorText.setEllipsize(null);
		tutorText.setTextSize(textSize);
		tutorText.setPadding(2, 0, 2, 0);
		addView(tutorText);

		sessionDate = 0L;
	}

	public void setChildren(String time, String subject, String location,
			String tutor) {
		timeText.setText(time);
		subjectText.setText(scrubBreakingSpaces(subject));
		locationText.setText(scrubBreakingSpaces(location));
		tutorText.setText(scrubBreakingSpaces(tutor));
	}

	public void setChildren(TimetableEntry t) {
		timeText.setText(t.getShortDisplayTime());
		subjectText.setText(scrubBreakingSpaces(t.getSubjectName()));
		locationText.setText(scrubBreakingSpaces(t.getLocation()));
		tutorText.setText(scrubBreakingSpaces(t.getTutor()));
	}

	public void setSessionDate(long sessionDate) {
		this.sessionDate = sessionDate;
	}

	public long getSessionDate() {
		return sessionDate;
	}

	/**
	 * used by the settings to determine what fields to show. Default is show
	 * all.
	 * 
	 * @param subjectVisible
	 * @param tutorVisible
	 * @param locationVisible
	 */
	public void setVisibleFields(boolean subjectVisible,
			boolean locationVisible, boolean tutorVisible) {
		subjectText.setVisibility(subjectVisible ? View.VISIBLE : View.GONE);
		locationText.setVisibility(locationVisible ? View.VISIBLE : View.GONE);
		tutorText.setVisibility(tutorVisible ? View.VISIBLE : View.GONE);
	}

	/**
	 * makes this into a spacer by setting all the background to invisible
	 */
	public void blankIt() {
		timeText.setText("");
		subjectText.setText("");
		locationText.setText("");
		tutorText.setText("");
		timeText.setBackgroundColor(getResources().getColor(R.color.invisible));
	}

	/**
	 * replaces all spaces with non-breaking spaces so that the single line
	 * display doesn't cut off the second word e.g. "Mr Anderson" wont be broken
	 * on two lines, instead it'll show "Mr Ande"
	 * 
	 * @param input
	 * @return
	 */
	public String scrubBreakingSpaces(String input) {
		return input.replace(' ', '\u00A0');
	}
}
