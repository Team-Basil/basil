package basil;

import java.util.*;

public class LineNumLookup {

	protected static Map<String, Integer> lookup;
	
	
	public static int getLineNum(String ordinal) {
		if(lookup == null) {
			lookup = new HashMap();
			
			lookup.put("1st", 1);
			lookup.put("2nd", 2);
			lookup.put("3rd", 3);
			lookup.put("4th", 4);
			lookup.put("5th", 5);
			lookup.put("6th", 6);
			lookup.put("7th", 7);
			lookup.put("8th", 8);
			lookup.put("9th", 9);
			lookup.put("10th", 10);
			lookup.put("11th", 11);
			lookup.put("12th", 12);
			lookup.put("13th", 13);
			lookup.put("14th", 14);
			lookup.put("15th", 15);
			lookup.put("16th", 16);
			lookup.put("17th", 17);
			lookup.put("18th", 18);
			lookup.put("19th", 19);
			lookup.put("20th", 20);
			
		}
		return lookup.get(ordinal);
	}
}
