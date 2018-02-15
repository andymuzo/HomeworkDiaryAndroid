package com.anidari.homeworkdiary;

/**
 * used to store short entries relating to homework for building notifications
 * 
 * @author ajrog_000
 * 
 */

public class NotificationEntry {

	private String title;
	private String subject;
	private boolean isFinished;

	public NotificationEntry(String title, String subject, boolean isFinished) {
		this.setTitle(title);
		this.setSubject(subject);
		this.setFinished(isFinished);
	}

	// Getters ***************************************************************
	public String getTitle() {
		return title;
	}

	public String getSubject() {
		return subject;
	}

	public boolean isFinished() {
		return isFinished;
	}

	// Setters ***************************************************************
	public void setTitle(String title) {
		this.title = title;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}

}
