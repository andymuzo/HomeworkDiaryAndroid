package com.anidari.homeworkdiary;

import com.anromus.homeworkdiary.R;

// a cut down container class for entries on the main list interface

public class HomeworkListEntry {
	final static int UNUSED_INT = -1;
	final static long UNUSED_LONG = -1L;
	
	
	private int id;
	private long dueDate; // this will be stored in ms since linux epoch, need
							// to do some reading on formatting or helper
							// classes here!
	private String homeworkTitle;
	private boolean isCompleted;
	private String finalGrade;

	private int subjectID; // this is the id number used to look up the
							// following info on the subject
	private int subjectImageResource;
	private String subjectName;

	private boolean isTitle; // when set to true a title with the contents of
								// the homework title string is displayed. Used
								// for grouping by due dates and subjects

	private boolean isEndOfSection;
	
	private boolean isOverdue;
	
	private boolean isSpacer;
	
	// Constructors
	// empty constructor
	public HomeworkListEntry() {
		// sets a default image to return
		this.subjectImageResource = R.drawable.ic_launcher;
	}

	// full constructor
	public HomeworkListEntry(int id, long dueDate, String homeworkTitle,
			int subjectID, boolean isCompleted, String finalGrade) {
		this.id = id;
		this.dueDate = dueDate;
		this.homeworkTitle = homeworkTitle;
		this.subjectID = subjectID;
		this.isCompleted = isCompleted;
		this.finalGrade = finalGrade;
		this.subjectImageResource = R.drawable.ic_launcher;
		this.setIsTitle(false);
		this.setEndOfSection(false);
		this.isOverdue = false;
	}

	/**
	 * title constructor, creates a small box with a heading in it
	 * 
	 * @param title
	 *            Text to be displayed as title
	 */
	public HomeworkListEntry(String title) {
		// the only parts needed
		this.homeworkTitle = title;
		this.setIsTitle(true);
		
		// everything else unused
		this.id = UNUSED_INT;
		this.dueDate = UNUSED_LONG;
		this.subjectID = UNUSED_INT;
		this.isCompleted = false;
		this.finalGrade = null;
		this.subjectImageResource = UNUSED_INT;
		this.setSpacer(false);
		this.isOverdue = false;
	}
	
	/**
	 * spacer constructor, creates a small box with a heading in it
	 * 
	 * @param title
	 *            Text to be displayed as title
	 */
	public HomeworkListEntry(boolean isSpacer) {
		// the only parts needed
		
		this.setSpacer(isSpacer);
		
		// everything else unused
		this.homeworkTitle = null;
		this.id = UNUSED_INT;
		this.dueDate = UNUSED_LONG;
		this.subjectID = UNUSED_INT;
		this.isCompleted = false;
		this.finalGrade = null;
		this.subjectImageResource = UNUSED_INT;
		this.setIsTitle(false);
		this.isOverdue = false;
	}
	

	public void paste(HomeworkListEntry hwk) {
		this.id = hwk.getId();
		this.dueDate = hwk.getDueDate();
		this.homeworkTitle = hwk.getHomeworkTitle();
		this.subjectID = hwk.getSubjectID();
		this.isCompleted = hwk.getIsCompleted();
		this.finalGrade = hwk.getFinalGrade();
		this.subjectImageResource = hwk.getSubjectImageResource();
		this.subjectName = hwk.getSubjectName();
		this.setIsTitle(false);
		this.setSpacer(false);
	}

	public boolean isOverdue() {
		return isOverdue;
	}
	
	public void setOverdue(boolean isOverdue) {
		this.isOverdue = isOverdue;
	}
	
	public int getId() {
		return this.id;
	}

	public long getDueDate() {
		return this.dueDate;
	}

	public int getSubjectID() {
		return this.subjectID;
	}

	public String getHomeworkTitle() {
		return this.homeworkTitle;
	}

	public boolean getIsCompleted() {
		return this.isCompleted;
	}

	public String getFinalGrade() {
		return this.finalGrade;
	}

	public int getSubjectImageResource() {
		return subjectImageResource;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public boolean getIsTitle() {
		return isTitle;
	}
	
	public boolean isEndOfSection() {
		return isEndOfSection;
	}
	
	public boolean isSpacer() {
		return isSpacer;
	}

	
	// setters ************************************************************

	public void setId(int id) {
		this.id = id;
	}

	public void setDueDate(long dueDate) {
		this.dueDate = dueDate;
	}

	public void setHomeworkTitle(String homeworkTitle) {
		this.homeworkTitle = homeworkTitle;
	}

	public void setSubjectID(int subjectID) {
		this.subjectID = subjectID;
	}

	public void setIsCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public void setFinalGrade(String finalGrade) {
		this.finalGrade = finalGrade;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public void setSubjectImageResource(int subjectImageResource) {
		this.subjectImageResource = subjectImageResource;
	}

	public void setIsTitle(boolean isTitle) {
		this.isTitle = isTitle;
	}

	public void setEndOfSection(boolean isEndOfSection) {
		this.isEndOfSection = isEndOfSection;
	}

	public void setSpacer(boolean isSpacer) {
		this.isSpacer = isSpacer;
	}

}
