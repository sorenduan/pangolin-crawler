package org.pangolincrawler.sdk;

import okhttp3.Response;

public final class PangolinException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private Response response;

  public PangolinException(Response response) {
    this.response = response;
  }

  public PangolinException(Response response, String msg) {
    super(msg);
    this.response = response;
  }

  public PangolinException(Exception e) {
    this(e, null);
  }

  public PangolinException(Exception e, String msg) {
    super(msg, e);
    this.response = null;
  }

  public PangolinException(String msg) {
    super(msg);
  }

}
