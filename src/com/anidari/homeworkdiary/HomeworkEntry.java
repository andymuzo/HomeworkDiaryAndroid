package com.anidari.homeworkdiary;

import java.util.ArrayList;

// this is the main class that hold a record of a particular homework assignment and all it's details

public class HomeworkEntry {

	private int id;
	private long dueDate; // this will be stored in ms since linux epoch, need
							// to do some reading on formatting or helper
							// classes here!
	private String homeworkTitle;

	private int subjectID; // this is the id number used to look up the
							// following info on the subject

	private boolean isReminderSet;

	private boolean isCompleted;

	private String finalGrade;

	private ArrayList<ToDoEntry> toDoList;

	// TODO: implement the attached picture feature, probably stored as an
	// arrayList of strings containing the paths of the pics

	// Constructors

	public HomeworkEntry(int id, long dueDate, String homeworkTitle,
			int subjectID, boolean isReminderSet, boolean isCompleted,
			String finalGrade) {
		// TODO: left out toDoList, it will be serialised data, more research
		// needed
		this.id = id;
		this.dueDate = dueDate;
		this.homeworkTitle = homeworkTitle;
		this.subjectID = subjectID;
		this.isReminderSet = isReminderSet;
		this.isCompleted = isCompleted;
		this.finalGrade = finalGrade;
	}

	public HomeworkEntry() {
		this.id = 0;
		this.dueDate = 0L;
		this.homeworkTitle = null;
		this.subjectID = 0;
		this.isReminderSet = false;
		this.isCompleted = false;
		this.finalGrade = null;
	}
	
	// getters *********************************************************

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

	public boolean getIsReminderSet() {
		return this.isReminderSet;
	}

	public boolean getIsCompleted() {
		return this.isCompleted;
	}

	public String getFinalGrade() {
		return this.finalGrade;
	}

	public ArrayList<ToDoEntry> getToDoList() {
		return this.toDoList;
	}

	public String getSerialisedToDo() {
		// TODO: implement serialisation of the todo list for storage
		String placeholder = "placeholder";
		return placeholder;
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

	public void setIsReminderSet(boolean isReminderSet) {
		this.isReminderSet = isReminderSet;
	}

	public void setIsCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public void setFinalGrade(String finalGrade) {
		this.finalGrade = finalGrade;
	}

	// methods for operating the todo list *************************************

	// add new, tick, delete, edit, move

	public void addToDoEntry(String toDoItem) {
		// adds a new item at the end of the list
		toDoList.add(new ToDoEntry(toDoItem));
	}

	public void setDone(int entryIndex, boolean isDone) {
		// ticks/unticks the done box for a specified entry
		toDoList.get(entryIndex).setIsDone(isDone);
	}

	public void deleteEntry(int entryIndex) {
		// does exactly what is says on the tin.
		// might need to do error checking but it should be impossible to get an
		// outofbounds error if used correctly
		toDoList.remove(entryIndex);
	}

	public void editEntry(int entryIndex, String toDoItem) {
		// changes the list item for a specified entry
		toDoList.get(entryIndex).setToDoItem(toDoItem);
	}

	public void moveEntry(int oldIndex, int newIndex) {
		// changes the position of an entry in the list

		if (oldIndex > newIndex) {
			// moving up the list
			// hold the old entry in a temporary object
			ToDoEntry tempToDo = toDoList.get(oldIndex);
			// move all the ones in between up by 1
			for (int i = oldIndex; i > newIndex; i--) {
				toDoList.set(i, toDoList.get(i - 1));
			}
			// put the temporary object in the new position
			toDoList.set(newIndex, tempToDo);

		} else if (oldIndex < newIndex) {
			// moving down the list
			// hold the old entry in a temporary object
			ToDoEntry tempToDo = toDoList.get(oldIndex);
			// move all the ones in between down by 1
			for (int i = oldIndex; i < newIndex; i++) {
				toDoList.set(i, toDoList.get(i + 1));
			}
			// put the temporary object in the new position
			toDoList.set(newIndex, tempToDo);
		}
		// if they are the same this does nothing
	}

}
