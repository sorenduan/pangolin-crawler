package org.pangolincrawler.core.service;

public class PublicServiceException extends RuntimeException {

  private static final long serialVersionUID = 1214267158584454957L;

  public PublicServiceException() {
  }

  public PublicServiceException(String message) {
    super(message);
  }

  public PublicServiceException(Throwable cause) {
    super(cause);
  }

  public PublicServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  public PublicServiceException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
