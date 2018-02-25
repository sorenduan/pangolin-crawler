package org.pangolincrawler.core.cache;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class CacheExpression {

  private static String SEPERATOR = "/";

  private int time;
  private TimeUnit unit;

  private CacheExpression(int time, TimeUnit unit) {
    super();
    this.time = time;
    this.unit = unit;
  }

  public static CacheExpression buildFromString(String str) {
    String[] parts = StringUtils.split(str, SEPERATOR);
    if (parts.length != 2) {
      throw new CacheExpressionFormatException("The cache expression format error : " + str);
    }

    return new CacheExpression(parseNumber(parts[0]), parseUnit(parts[1]));
  }

  private static int parseNumber(String numStr) {
    int num = NumberUtils.toInt(numStr);
    return num;
  }

  private static TimeUnit parseUnit(String unitStr) {

    switch (StringUtils.trimToEmpty(unitStr).toLowerCase()) {
      case "ms":
        return TimeUnit.MILLISECONDS;
      case "s":
        return TimeUnit.SECONDS;
      case "m":
        return TimeUnit.MINUTES;
      case "h":
        return TimeUnit.HOURS;
      case "d":
        return TimeUnit.DAYS;
      default:
        break;
    }

    throw new CacheExpressionFormatException(
        "The cache expression time unit format error : " + unitStr);
  }

  public int getTime() {
    return time;
  }

  public TimeUnit getUnit() {
    return unit;
  }

}
