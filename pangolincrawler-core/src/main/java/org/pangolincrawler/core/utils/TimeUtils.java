package org.pangolincrawler.core.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public final class TimeUtils {

  private TimeUtils() {
  }

  public static String getCurrentDateTextForFileName() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH:mm:ss.SSS.z");
    return ZonedDateTime.now().format(formatter);
  }

  public static LocalDateTime timestampToLocalDateTime(long ms) {
    return LocalDateTime.ofInstant(Instant.ofEpochSecond(ms), TimeZone.getDefault().toZoneId());
  }

}
