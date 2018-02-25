package org.pangolincrawler.core.task;

public final class JobValidationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public JobValidationException() {
  }

  public JobValidationException(String message) {
    super(message);
  }

  public JobValidationException(Throwable cause) {
    super(cause);
  }

  public JobValidationException(String message, Throwable cause) {
    super(message, cause);
  }

  public JobValidationException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
