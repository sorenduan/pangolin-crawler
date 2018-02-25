package org.pangolincrawler.core.web.api;

public class ApiException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private ApiResponseCodeEnum code;

  public ApiException() {
    code = ApiResponseCodeEnum.UNKOWN_ERROR;
  }

  public ApiException(ApiResponseCodeEnum code) {
    super();
    this.code = code;
  }

  public ApiException(ApiResponseCodeEnum code, String message) {
    super(message);
    this.code = code;
  }

  public ApiException(String message) {
    super(message);
  }

  public ApiException(Throwable cause) {
    super(cause);
  }

  public ApiException(String message, Throwable cause) {
    super(message, cause);
  }

  public ApiException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public ApiResponseCodeEnum getCode() {
    return code;
  }

}
