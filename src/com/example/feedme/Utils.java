package com.example.feedme;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public class Utils {
	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public static boolean searchResultisMatch(String testString, String searchKeyword) {
		return testString.toLowerCase().indexOf(searchKeyword.toLowerCase()) != -1;
	}

	
	/**
	 * For test
	 */
	public static String rudeFilter(String text) {
		return text.replace("fuck you", "^_^");
	}

	public static String getCleanFullAnswer(String text) {
		if (text.equals("No Way")) {
			return "SELL";
		} else
			return "BUY";
	}
	public static String getCleanOppositeFullAnswer(String text) {
		if (text.equals("No Way")) {
			return "BUY";
		} else
			return "SELL";
	}
	

	/**
	 * Utility function // TB TODO - Probably should be some place better.
	 * 
	 * @param vals
	 * @param delim
	 * @return joined string
	 */
	public String joinString(int[] vals, String delim) {
		StringBuilder sb = new StringBuilder();
		String currDelim = "";
		for (int v = 0; v < vals.length; v++) {
			sb.append(currDelim).append(vals[v]);
			currDelim = delim;
		}
		return sb.toString();
	}
}