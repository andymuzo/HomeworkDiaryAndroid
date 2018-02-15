package com.anidari.homeworkdiary;

/**
 * used to populate the main menu
 * @author ajrog_000
 *
 */
public class MainMenuItem {

	public static enum MENU_TYPE {
		HOMEWORK, SUBJECT, TIMETABLE, SPACER
	}
	
	private String title;
	private String details;
	private MENU_TYPE type;
	
	/**
	 * main menu item
	 * @param title
	 * @param details
	 */
	public MainMenuItem(String title, String details, MENU_TYPE type) {
		this.setTitle(title);
		this.setDetails(details);
		this.type = type;
	}
	
	/**
	 * used to make a blank spacer
	 */
	public MainMenuItem() {
		this.setTitle(null);
		this.setDetails(null);
		this.type = MENU_TYPE.SPACER;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
	
	public MENU_TYPE getType() {
		return type;
	}

	public boolean isFlaggedAsSpacer() {
		boolean flaggedAsSpacer;
		
		if (type == MENU_TYPE.SPACER) {
			flaggedAsSpacer = true;
		}
		else flaggedAsSpacer = false;
		
		return flaggedAsSpacer;
	}
}
