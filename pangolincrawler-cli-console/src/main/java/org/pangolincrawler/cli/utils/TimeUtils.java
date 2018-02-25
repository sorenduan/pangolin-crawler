package org.pangolincrawler.cli.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class TimeUtils {

	private TimeUtils() {
	}

	public static String convertServerTimeFormat(String time) {

		DateFormat localFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US);

		Date date;
		try {
			date = localFormat.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return time;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		return format.format(date);
	}

	public static LocalDateTime timestampToLocalDateTime(long ms) {
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(ms), TimeZone.getDefault().toZoneId());
	}

}
