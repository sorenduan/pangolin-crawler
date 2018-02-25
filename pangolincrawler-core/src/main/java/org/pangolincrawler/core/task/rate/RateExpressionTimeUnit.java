package org.pangolincrawler.core.task.rate;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

public enum RateExpressionTimeUnit {

  SECONDS("s"),

  /**
   * Time unit representing sixty seconds
   */
  MINUTES("m"),

  /**
   * Time unit in hour.
   */
  HOURS("h"),

  DAYS("d");

  private String unitString;

  private RateExpressionTimeUnit(String unitString) {
    this.unitString = unitString;
  }

  public String getUnitString() {
    return unitString;
  }


  public TimeUnit toTimeUnit() {

    switch (this) {
    case DAYS:
      return TimeUnit.DAYS;
    case HOURS:
      return TimeUnit.HOURS;
    case MINUTES:
      return TimeUnit.MINUTES;
    case SECONDS:
      return TimeUnit.SECONDS;

    default:
      break;
    }

    return null;
  }

  public static RateExpressionTimeUnit parse(String t) {
    RateExpressionTimeUnit[] units = RateExpressionTimeUnit.values();
    for (RateExpressionTimeUnit each : units) {
      if (StringUtils.equals(each.getUnitString(), t)) {
        return each;
      }
    }
    return null;
  }
}
