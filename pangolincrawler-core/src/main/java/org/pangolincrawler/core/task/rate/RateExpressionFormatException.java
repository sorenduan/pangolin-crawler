package org.pangolincrawler.core.task.rate;

public class RateExpressionFormatException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public RateExpressionFormatException() {
  }

  public RateExpressionFormatException(String message) {
    super(message);
  }

  public RateExpressionFormatException(Throwable cause) {
    super(cause);
  }

  public RateExpressionFormatException(String message, Throwable cause) {
    super(message, cause);
  }

  public RateExpressionFormatException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
