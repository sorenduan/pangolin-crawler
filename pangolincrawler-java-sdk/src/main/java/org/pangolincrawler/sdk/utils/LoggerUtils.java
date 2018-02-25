package org.pangolincrawler.sdk.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoggerUtils {

  private LoggerUtils() {
  }

  protected static Logger logger = LoggerFactory.getLogger("ROOT");

  public static void warn(String msg, Class<?> clazz) {
    getLogger(clazz).warn(msg);
  }

  public static void warn(String msg, Throwable t, Class<?> clazz) {
    getLogger(clazz).warn(msg, t);
  }

  public static void warn(Class<?> clazz, String format, Object... args) {
    getLogger(clazz).warn(format, args);
  }

  public static void error(String msg, Class<?> clazz) {
    getLogger(clazz).error(msg);
  }

  public static void error(Class<?> clazz, String msg, Throwable t) {
    getLogger(clazz).error(msg, t);
  }

  public static void error(Class<?> clazz, Throwable t) {
    getLogger(clazz).error("", t);
  }

  public static void error(Class<?> clazz, String format, Object... args) {
    getLogger(clazz).error(format, args);
  }

  public static void info(String msg, Class<?> clazz) {
    getLogger(clazz).info(msg);
  }

  public static void info(Class<?> clazz, String format, Object... args) {
    getLogger(clazz).info(format, args);
  }

  private static Logger getLogger(Class<?> clazz) {
    return LoggerFactory.getLogger(clazz);
  }
}
