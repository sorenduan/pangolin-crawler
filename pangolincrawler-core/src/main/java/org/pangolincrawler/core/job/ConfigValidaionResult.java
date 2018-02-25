package org.pangolincrawler.core.job;

public class ConfigValidaionResult {
  private boolean success;
  private String message = "Success";

  public ConfigValidaionResult(boolean success, String message) {
    super();
    this.success = success;
    this.message = message;
  }

  public ConfigValidaionResult(boolean success) {
    super();
    this.success = success;
  }

  public static ConfigValidaionResult success() {
    return new ConfigValidaionResult(true);
  }

  public static ConfigValidaionResult fail(String message) {
    return new ConfigValidaionResult(false, message);
  }

  /**
   * @return the success
   */
  public boolean isSuccess() {
    return success;
  }

  public boolean isFail() {
    return !this.isSuccess();
  }

  /**
   * @param success
   *          the success to set
   */
  public void setSuccess(boolean success) {
    this.success = success;
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
}