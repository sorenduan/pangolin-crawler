package org.pangolincrawler.core.http;

import java.io.Serializable;

public class PangolinHttpClientResponse implements Serializable {

  private static final long serialVersionUID = -377925120636672054L;

  private boolean success;
  private int httpStatusCode;
  private String html;

  private Exception exception;

  public Exception getException() {
    return exception;
  }

  public void setException(Exception exception) {
    this.exception = exception;
  }

  public PangolinHttpClientResponse() {
    this.success = true;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public int getHttpStatusCode() {
    return httpStatusCode;
  }

  public void setHttpStatusCode(int httpStatusCode) {
    this.httpStatusCode = httpStatusCode;
  }

  public String getHtml() {
    return html;
  }

  public void setHtml(String html) {
    this.html = html;
  }

}
