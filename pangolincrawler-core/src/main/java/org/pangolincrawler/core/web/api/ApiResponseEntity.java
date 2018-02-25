package org.pangolincrawler.core.web.api;

import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseEntity extends ResponseEntity<String> {

  private boolean success;

  private ApiResponseCodeEnum code;

  private String message;

  private String responseBody;

  private ApiResponseEntity(boolean success, ApiResponseCodeEnum code, String message,
      String body) {
    super(body, HttpStatus.OK);
    this.success = success;
    this.code = code;
    this.message = message;
    this.responseBody = body;
  }


  public static ApiResponseEntity build(boolean success, ApiResponseCodeEnum code, String message,
      String body) {
    String responseBody = buildResponseBody(success, code, message, body);
    return new ApiResponseEntity(success, code, message, responseBody);
  }

  public static ApiResponseEntity build(boolean success, String body) {
    String responseBody = buildResponseBody(success, ApiResponseCodeEnum.UNKOWN_ERROR, null, body);
    return new ApiResponseEntity(success, null, null, responseBody);
  }

  public static ApiResponseEntity build(boolean success, ApiResponseCodeEnum code, String message) {
    String responseBody = buildResponseBody(success, code, message, null);
    return new ApiResponseEntity(success, code, message, responseBody);
  }

  private static String buildResponseBody(boolean success, ApiResponseCodeEnum code, String message,
      String body) {
    JsonObject json = new JsonObject();

    json.addProperty("success", success);

    if (!success) {
      json.addProperty("code", code.getCode());
    }

    if (StringUtils.isNoneBlank(message)) {
      json.addProperty("message", message);
    }

    if (StringUtils.isNoneBlank(body)) {
      json.addProperty("body", body);
    }

    return json.toString();
  }

  public boolean isSuccess() {
    return success;
  }

  public ApiResponseCodeEnum getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String getBody() {
    return responseBody;
  }


}
