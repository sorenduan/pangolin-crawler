package org.pangolincrawler.core;

public final class ConfigValidationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ConfigValidationException() {
  }

  public ConfigValidationException(String message) {
    super(message);
  }

  public ConfigValidationException(Throwable cause) {
    super(cause);
  }

  public ConfigValidationException(String message, Throwable cause) {
    super(message, cause);
  }

  public ConfigValidationException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
