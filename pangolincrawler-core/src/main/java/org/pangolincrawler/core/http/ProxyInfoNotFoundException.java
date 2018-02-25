package org.pangolincrawler.core.http;

public class ProxyInfoNotFoundException extends Exception {

  private static final long serialVersionUID = 1L;

  public ProxyInfoNotFoundException() {
  }

  public ProxyInfoNotFoundException(String message) {
    super(message);
  }

  public ProxyInfoNotFoundException(Throwable cause) {
    super(cause);
  }

  public ProxyInfoNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public ProxyInfoNotFoundException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
