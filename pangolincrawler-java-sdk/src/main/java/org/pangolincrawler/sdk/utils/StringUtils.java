package org.pangolincrawler.sdk.utils;

public final class StringUtils {

	private StringUtils() {
	}

	public static boolean isEmpty(String s) {
		if(s == null || s.trim().length() == 0) {
			return true;
		}
		return false;
	}
	public static boolean isNotEmpty(String s) {
		return ! isEmpty(s);
	}
	
	public static String trimToEmpty(String s) {
		if(isNotEmpty(s)) {
			return s.trim();
		}
		return "";
	}
}
