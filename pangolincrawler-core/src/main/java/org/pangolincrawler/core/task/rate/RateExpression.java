package org.pangolincrawler.core.task.rate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.pangolincrawler.core.cache.CacheExpressionFormatException;

/**
 * 10/20s,
 */
public class RateExpression implements Serializable {

  private static final long serialVersionUID = 1L;

  private static String SEPERATOR = "/";
  private static String LIST_SEPERATOR = ",";

  private long limitNum;
  private long unitNum;
  private RateExpressionTimeUnit unit;

  private RateExpression(long limitNum, long unitNum, RateExpressionTimeUnit unit) {
    super();
    this.limitNum = limitNum;
    this.unitNum = unitNum;
    this.unit = unit;
  }

  public RateExpression() {
    super();
  }

  public static RateExpression buildFromString(String str) {
    String[] parts = StringUtils.split(str, SEPERATOR);
    if (parts.length != 2) {
      throw new RateExpressionFormatException("The cache expression format error : " + str);
    }

    long limitNum = parseNumber(parts[0]);
    long unitNum = parseUnitNumber(parts[1]);

    RateExpressionTimeUnit unit = parseUnit(unitNum, parts[1]);

    return new RateExpression(limitNum, unitNum, unit);
  }

  public static List<RateExpression> buildListFromString(String str) {
    String[] parts = StringUtils.split(str, LIST_SEPERATOR);
    List<RateExpression> list = new ArrayList<>();

    for (String each : parts) {
      list.add(buildFromString(each));
    }
    return list;
  }

  private static long parseNumber(String numStr) {
    return NumberUtils.toLong(numStr);
  }

  private static long parseUnitNumber(String numStr) {
    String tmp = StringUtils.replaceAll(numStr, "[a-zA-Z]{1,2}$", "");
    return NumberUtils.toLong(tmp);
  }

  private static RateExpressionTimeUnit parseUnit(long unitNum, String unitStr) {

    String unitToken = StringUtils.remove(unitStr, String.valueOf(unitNum));

    if (StringUtils.isBlank(unitToken)) {
      throw new CacheExpressionFormatException(
          "The cache expression unit format error : " + unitStr);
    }
    return RateExpressionTimeUnit.parse(unitToken);

  }

  @Override
  public String toString() {
    return "RateExpression [limitNum=" + limitNum + ", unitNum=" + unitNum + ", unit=" + unit + "]";
  }

  public long getLimitNum() {
    return limitNum;
  }

  public void setLimitNum(long limitNum) {
    this.limitNum = limitNum;
  }

  public long getUnitNum() {
    return unitNum;
  }

  public void setUnitNum(long unitNum) {
    this.unitNum = unitNum;
  }

  public RateExpressionTimeUnit getUnit() {
    return unit;
  }

  public void setUnit(RateExpressionTimeUnit unit) {
    this.unit = unit;
  }

  public long getUnitNumMillis() {
    return this.unit.toTimeUnit().toMillis(this.unitNum);
  }
}
