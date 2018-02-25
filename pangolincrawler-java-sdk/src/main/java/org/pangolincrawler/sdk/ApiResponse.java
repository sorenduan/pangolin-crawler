package org.pangolincrawler.sdk;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Response;

public final class ApiResponse {

  public static final int CODE_CMD_INNER_ERROR = -1;

  private int code;

  private String message;

  private boolean success;

  private String body;

  public ApiResponse() {
  }

  public ApiResponse(int code, String message, boolean success) {
    this(code, message, success, null);
  }

  /**
   * abc.
   * 
   * @param code, 
   * @param message
   * @param success
   * @param body
   */
  public ApiResponse(int code, String message, boolean success, String body) {
    super();
    this.code = code;
    this.message = message;
    this.success = success;
    this.body = body;
  }

  public static ApiResponse build(boolean success, String body) {
    ApiResponse r = new ApiResponse();
    r.success = success;
    r.body = body;
    return r;
  }

  public static ApiResponse build(Response response) {
    Gson gson = new Gson();

    String rowJson;
    try {
      rowJson = response.body().string();
      if (null != rowJson && rowJson.trim().length() > 0) {
        JsonObject json = gson.fromJson(rowJson.trim(), JsonObject.class);

        ApiResponse r = new ApiResponse();

        if (json.has("success")) {
          r.success = json.get("success").getAsBoolean();
        }
        if (json.has("code")) {
          r.code = json.get("code").getAsInt();
        }

        if (json.has("message")) {
          r.message = json.get("message").getAsString();
        }

        if (json.has("body")) {
          r.body = json.get("body").getAsString();
        }

        return r;
      }
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }

    return null;

  }

  /**
   * @return the code
   */
  public int getCode() {
    return code;
  }

  /**
   * @param code
   *          the code to set
   */
  public void setCode(int code) {
    this.code = code;
  }

  /**
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * @param message
   *          the message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * @return the success
   */
  public boolean isSuccess() {
    return success;
  }

  /**
   * @param success
   *          the success to set
   */
  public void setSuccess(boolean success) {
    this.success = success;
  }

  /**
   * @return the body
   */
  public String getBody() {
    return body;
  }

  /**
   * @param body
   *          the body to set
   */
  public void setBody(String body) {
    this.body = body;
  }
}
