package org.pangolincrawler.core.job;

public class JobManagerRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public JobManagerRuntimeException() {
  }

  public JobManagerRuntimeException(String message) {
    super(message);
  }

  public JobManagerRuntimeException(Throwable cause) {
    super(cause);
  }

  public JobManagerRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public JobManagerRuntimeException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
