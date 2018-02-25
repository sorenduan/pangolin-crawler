package org.pangolincrawler.core.common;

public class PangolinServerException extends RuntimeException {

  private static final long serialVersionUID = 1214267158584454957L;

  public PangolinServerException() {
  }

  public PangolinServerException(String message) {
    super(message);
  }

  public PangolinServerException(Throwable cause) {
    super(cause);
  }

  public PangolinServerException(String message, Throwable cause) {
    super(message, cause);
  }

  public PangolinServerException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
