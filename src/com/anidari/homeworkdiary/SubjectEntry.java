package com.anidari.homeworkdiary;

// simple storage class for the entries on the subject table

public class SubjectEntry {
	
	private static int EMPTY = -1;
	
	private int databaseID;
	private String subjectName;
	private int subjectPicture;
	// these are for the list view to round the appropriate corners
	private boolean isTop;
	private boolean isBottom;
	// flag for an empty spacer
	private boolean isSpacer;
	
	public SubjectEntry (int databaseID, String subjectName, int subjectPicture) {
		this.databaseID = databaseID;
		this.subjectName = subjectName;
		this.subjectPicture = subjectPicture;
		this.setTop(false);
		this.setBottom(false);
		this.setSpacer(false);
	}
	
	public SubjectEntry (int databaseID, String subjectName, int subjectPicture, boolean isTop, boolean isBottom) {
		this.databaseID = databaseID;
		this.subjectName = subjectName;
		this.subjectPicture = subjectPicture;
		this.setTop(isTop);
		this.setBottom(isBottom);
		this.setSpacer(false);
	}
	
	/**
	 * Makes a spacer
	 */
	public SubjectEntry () {
		this.databaseID = EMPTY;
		this.subjectName = null;
		this.subjectPicture = EMPTY;
		this.setTop(false);
		this.setBottom(false);
		this.setSpacer(true);
	}
	
	public void paste(SubjectEntry newSubject) {
		this.databaseID = newSubject.getDatabaseID();
		this.subjectName = newSubject.getSubjectName();
		this.subjectPicture = newSubject.getSubjectPicture();
		
	}
	
	// getters ****************************************************

	public int getDatabaseID() {
		return this.databaseID;
	}
	
	public String getSubjectName() {
		return this.subjectName;
	}
	
	public int getSubjectPicture() {
		return this.subjectPicture;
	}
	
	public boolean isTop() {
		return isTop;
	}

	public boolean isBottom() {
		return isBottom;
	}
	
	// setters ****************************************************
	
	public void setDatabaseID(int databaseID) {
		this.databaseID = databaseID;
	}
	
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	
	public void setSubjectPicture(int subjectPicture) {
		this.subjectPicture = subjectPicture;
	}

	public void setTop(boolean isTop) {
		this.isTop = isTop;
	}

	public void setBottom(boolean isBottom) {
		this.isBottom = isBottom;
	}

	public boolean isSpacer() {
		return isSpacer;
	}

	public void setSpacer(boolean isSpacer) {
		this.isSpacer = isSpacer;
	}
	
}
