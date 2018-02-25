package org.pangolincrawler.core.cache;

public class CacheExpressionFormatException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public CacheExpressionFormatException() {
  }

  public CacheExpressionFormatException(String message) {
    super(message);
  }

  public CacheExpressionFormatException(Throwable cause) {
    super(cause);
  }

  public CacheExpressionFormatException(String message, Throwable cause) {
    super(message, cause);
  }

  public CacheExpressionFormatException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
