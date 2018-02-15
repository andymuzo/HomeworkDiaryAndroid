package com.anidari.homeworkdiary;

public class AddTh {

	/**
	 * Daft little method to return the correct end to a date "1st/2nd/3rd" etc
	 * @param date date to be appended
	 * @return String of the required value
	 */
	static String getTh(int date) {
		String dateEnd;
		switch (date) {
		case 1:
		case 21:
		case 31:
			dateEnd = "st";
			break;
		case 2:
		case 22:
			dateEnd = "nd";
			break;
		case 3:
		case 23:
			dateEnd = "rd";
			break;
		default:
			dateEnd = "th";
		}
		
		return dateEnd;
	}
}
