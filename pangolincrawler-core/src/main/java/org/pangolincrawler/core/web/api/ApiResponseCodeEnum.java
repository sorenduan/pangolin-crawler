package org.pangolincrawler.core.web.api;

public enum ApiResponseCodeEnum {

  SUCCESS(0, "success"), UNKOWN_ERROR(1001, "some unkonw error."),

  JOB_IS_INVALID(2001, "job validation error"),

  PROCESSOR_IS_INVALID(4001, "processor validation error"),

  TPL_NOT_FOUND(3001, "The tpl is not found."),

  TPL_COMMON_EXCEPTION(3101, "unkown tpl error");

  private int code;
  private String desc;

  private ApiResponseCodeEnum(int code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  /**
   * @return the code
   */
  public int getCode() {
    return code;
  }

  /**
   * @return the desc
   */
  public String getDesc() {
    return desc;
  }

}
