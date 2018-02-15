package com.anidari.homeworkdiary;

/**
 * Rightly or wrongly this class is a generic container class for the various
 * variables that exist within each homework detail ListView entry. This means
 * an ArrayList of these objects can be passed to the ListAdapter and it will
 * use their position in the list or the TYPE to figure out what to display.
 * 
 * This is a slight waste of memory but its pretty minimal considering, an
 * alternative would be to extend a base class for each type
 * 
 * @author ajrog_000
 * 
 */
public class HomeworkDetailListEntry {

	public static enum TYPE {
		TITLE, DUE_DATE, FINISHED_GRADE, REMINDER_SET, TO_DO_TITLE, TO_DO_ENTRY, TODO_ADD_NEW, SPACER
	}

	public static int UNUSED = -1;

	private TYPE type;
	private int id;
	private int id2;
	private int image;
	private String text1;
	private String text2;
	private boolean isTicked;
	private long dueDate;
	private boolean hasChanged; // this is set to false until the entry has been
								// edited, used to only update the database
								// entries needed

	/**
	 * Used for making the title box
	 * 
	 * @param type
	 *            should be TYPE.TITLE
	 * @param text1
	 *            Homework title
	 * @param text2
	 *            Subject title
	 * @param imageId
	 *            subject image id (R.id.whatever)
	 */
	public HomeworkDetailListEntry(TYPE type, int id, int subjectId,
			String text1, String text2, int image) {

		this.type = type;
		this.text1 = text1;
		this.text2 = text2;
		this.image = image;
		this.id = id;
		this.id2 = subjectId;

		// unused
		this.isTicked = false;
		this.setHasChanged(false);
	}

	/**
	 * For creating due date box
	 * 
	 * @param type
	 *            TYPE.DUE_DATE
	 * @param text1
	 *            the date in human-readable format (use Calendar)
	 */
	public HomeworkDetailListEntry(TYPE type, long dueDate) {
		this.type = type;
		this.dueDate = dueDate;

		// unused
		this.text1 = null;
		this.text2 = null;
		this.image = UNUSED;
		this.isTicked = false;
		this.id = UNUSED;
		this.id2 = UNUSED;
		this.setHasChanged(false);
	}

	/**
	 * Used for TYPE.FINISHED_GRADE
	 * 
	 * @param type
	 *            TYPE.FINISHED_GRADE
	 * @param text1
	 *            text to display
	 * @param isCheckboxChecked
	 *            pretty obvious
	 */
	public HomeworkDetailListEntry(TYPE type, String text1, boolean isTicked) {

		this.type = type;
		this.text1 = text1;
		this.isTicked = isTicked;

		// unused
		this.text2 = null;
		this.image = UNUSED;
		this.id = UNUSED;
		this.id2 = UNUSED;
		this.setHasChanged(false);
	}
	
	/**
	 * Used for TYPE.REMINDER_SET
	 * 
	 * @param type
	 *            TYPE.REMINDER_SET
	 * @param text1
	 *            text to display
	 * @param isCheckboxChecked
	 *            pretty obvious
	 */
	public HomeworkDetailListEntry(TYPE type, boolean isTicked, int id) {

		// id is the id used by the notification service to update a set notification
		this.type = type;
		this.isTicked = isTicked;
		this.id = id;
		
		// unused
		this.text1 = null;
		this.text2 = null;
		this.image = UNUSED;
		this.id2 = UNUSED;
		this.setHasChanged(false);
	}

	/**
	 * Used for TYPE.TO_DO_ENTRY
	 * 
	 * @param type
	 *            TYPE.FINISHED_GRADE, TYPE.REMINDER_SET or TYPE.TO_DO_ENTRY
	 * @param text1
	 *            text to display
	 * @param isCheckboxChecked
	 *            pretty obvious
	 */
	public HomeworkDetailListEntry(TYPE type, int todoId, int homeworkId,
			String text1, boolean isTicked) {

		this.type = type;
		this.text1 = text1;
		this.isTicked = isTicked;
		this.id = todoId;
		this.id2 = homeworkId;

		// unused
		this.text2 = null;
		this.image = UNUSED;
		this.setHasChanged(false);
	}

	/**
	 * Used to create headings, add new todo box and populating a new empty detailed entry, also for spacer 
	 * 
	 * @param type
	 *            TYPE.TODO_TITLE or TYPE.TODO_ADD_NEW or TYPE.SPACER
	 */
	public HomeworkDetailListEntry(TYPE type) {
		this.type = type;

		// unused
		this.text1 = null;
		this.text2 = null;
		this.image = UNUSED;
		this.isTicked = false;
		this.id = UNUSED;
		this.id2 = UNUSED;
		this.setHasChanged(false);
	}
	
	/**
	 * Used to create coloured spacer 
	 * 
	 * @param type
	 *            TYPE.SPACER
	 */
	public HomeworkDetailListEntry(TYPE type, boolean isColoured) {
		this.type = type;
		this.isTicked = isColoured;

		// unused
		this.text1 = null;
		this.text2 = null;
		this.image = UNUSED;
		this.id = UNUSED;
		this.id2 = UNUSED;
		this.setHasChanged(false);
	}
	
	/** creates a new entry out of a to do list entry object
	 * 
	 * @param toDo Must be fully filled in
	 */
	public HomeworkDetailListEntry(ToDoEntry toDo) {
		this.type = TYPE.TO_DO_ENTRY;
		this.text1 = toDo.getToDoItem();
		this.isTicked = toDo.getIsDone();
		this.id = toDo.getId();
		this.id2 = toDo.getHomeworkId();

		// unused
		this.text2 = null;
		this.image = UNUSED;
		this.setHasChanged(false);
	}

	// getters **************************************************************
	public TYPE getType() {
		return type;
	}

	public int getImage() {
		return image;
	}

	public String getText1() {
		return text1;
	}

	public String getText2() {
		return text2;
	}

	public boolean getIsTicked() {
		return isTicked;
	}

	public int getId() {
		return id;
	}

	public int getId2() {
		return id2;
	}

	public long getDueDate() {
		return dueDate;
	}
	
	public boolean getHasChanged() {
		return hasChanged;
	}

	// Setters **************************************************************
	public void setType(TYPE type) {
		this.type = type;
		this.setHasChanged(true);
	}

	public void setImage(int image) {
		this.image = image;
		this.setHasChanged(true);
	}

	public void setText1(String text1) {
		this.text1 = text1;
		this.setHasChanged(true);
	}

	public void setText2(String text2) {
		this.text2 = text2;
		this.setHasChanged(true);
	}

	public void setIsTicked(boolean isTicked) {
		this.isTicked = isTicked;
		this.setHasChanged(true);
	}

	public void setId(int id) {
		this.id = id;
		this.setHasChanged(true);
	}

	public void setId2(int id2) {
		this.id2 = id2;
		this.setHasChanged(true);
	}

	public void setDueDate(long dueDate) {
		this.dueDate = dueDate;
		this.setHasChanged(true);
	}

	public void setHasChanged(boolean hasChanged) {
		this.hasChanged = hasChanged;
	}

}
