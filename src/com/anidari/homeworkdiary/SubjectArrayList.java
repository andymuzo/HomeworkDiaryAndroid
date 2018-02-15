package com.anidari.homeworkdiary;

import java.util.ArrayList;

/**
 * basically contains an arrayList but does a few more specific functions like
 * returning a list of subjects as strings and allowing the lookup of an entry
 * via the item number in said list
 * 
 * @author ajrog_000
 * 
 */
public class SubjectArrayList {

	ArrayList<SubjectEntry> subjectList = new ArrayList<SubjectEntry>();

	public SubjectArrayList(ArrayList<SubjectEntry> subjectList) {
		this.subjectList = subjectList;
	}

	/**
	 * gets the main ArrayList this is based on
	 * 
	 * @return
	 */
	public ArrayList<SubjectEntry> getSubjectArrayList() {
		return subjectList;
	}

	/**
	 * suitable for a spinner
	 * 
	 * @return
	 */
	public String[] getStringsOfHomework() {

		// ArrayList<String> subjectStrings = new ArrayList<String>();

		// the -2 accommodates the spacer at the start and end of the list
		String[] stringList = new String[subjectList.size() - 2];

		int position = 0;

		for (SubjectEntry subject : subjectList) {

			if (!subject.isSpacer()) {
				stringList[position++] = subject.getSubjectName();
			}

		}

		return stringList;
		// return subjectStrings;
	}

	public SubjectEntry getSubjectAtListPosition(int listPos) {
		// the + 1 accommodates the empty spacer at the start of the list
		return subjectList.get(listPos + 1);
	}

	/**
	 * gets the position in the list of a subject entry by it's subject id
	 * number. Useful for initially setting the position of a spinner
	 * 
	 * @param subjectId
	 * @return
	 */
	public int getPositionById(int subjectId) {

		int position = -1;

		for (SubjectEntry sub : subjectList) {
			if (sub.getDatabaseID() == subjectId)
				break;
			position++;
		}

		// stops it from returning a negative just in case list is empty, shouldn't happen
		if (position == -1) {
			position = 0;
		}
		
		return position;
	}
}
