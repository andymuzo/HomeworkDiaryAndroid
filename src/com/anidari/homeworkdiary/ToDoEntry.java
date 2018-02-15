package com.anidari.homeworkdiary;

public class ToDoEntry {

	private int id;
	private int homeworkId;
	private int order;
	private String toDoItem;
	private boolean isDone;

	/**
	 * 
	 * @param id ignored if creating new
	 * @param homeworkId homework its attached to
	 * @param order	running order of the to do list, can handle gaps
	 * @param toDoItem the actual text
	 * @param isDone check box
	 */
	public ToDoEntry(int id, int homeworkId, int order, String toDoItem,
			boolean isDone) {
		// creates the todo item
		this.setId(id);
		this.setHomeworkId(homeworkId);
		this.setOrder(order);
		this.toDoItem = toDoItem;
		this.isDone = isDone;
	}
	
	public ToDoEntry(int homeworkId, int order, String toDoItem, boolean isDone) {
		
		// used for adding new item by user, id is generated automatically when sent to the database
		this.setHomeworkId(homeworkId);
		this.setOrder(order);
		this.toDoItem = toDoItem;
		this.isDone = isDone;
	}

	public ToDoEntry(String toDoItem) {
		// sets false as default when creating a new entry
		this.toDoItem = toDoItem;
		this.isDone = false;
	}

	// getters ***********************************************

	public int getId() {
		return id;
	}

	public int getHomeworkId() {
		return homeworkId;
	}

	public int getOrder() {
		return order;
	}

	public String getToDoItem() {
		return toDoItem;
	}

	public boolean getIsDone() {
		return isDone;
	}

	// setters ***********************************************

	public void setToDoItem(String toDoItem) {
		this.toDoItem = toDoItem;
	}

	public void setIsDone(boolean isDone) {
		this.isDone = isDone;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setHomeworkId(int homeworkId) {
		this.homeworkId = homeworkId;
	}

	public void setOrder(int order) {
		this.order = order;
	}

}
