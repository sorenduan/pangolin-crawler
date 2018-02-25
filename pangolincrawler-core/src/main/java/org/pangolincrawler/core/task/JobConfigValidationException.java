package org.pangolincrawler.core.task;

public final class JobConfigValidationException extends Exception {

  private static final long serialVersionUID = 1L;

  public JobConfigValidationException() {
  }

  public JobConfigValidationException(String message) {
    super(message);
  }

  public JobConfigValidationException(Throwable cause) {
    super(cause);
  }

  public JobConfigValidationException(String message, Throwable cause) {
    super(message, cause);
  }

  public JobConfigValidationException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
