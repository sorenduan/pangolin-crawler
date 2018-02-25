package org.pangolincrawler.core.web.api;

import javax.servlet.http.HttpServletRequest;

import org.pangolincrawler.core.utils.LoggerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class BaseRestController {

  @Autowired
  private HttpServletRequest httpRequest;

  protected String getQueryParam(String key) {
    return httpRequest.getParameter(key);
  }

  protected boolean hasTheQueryParam(String key) {
    return null != httpRequest.getParameter(key);
  }

  @ExceptionHandler(Exception.class)
  protected ApiResponseEntity handleException(Throwable e) {
    LoggerUtils.error(this.getClass(), "", e);

    if (e instanceof ApiException) {
      return ApiResponseEntity.build(false, ((ApiException) e).getCode(),
          ((ApiException) e).getMessage());
    }

    return ApiResponseEntity.build(false, ApiResponseCodeEnum.UNKOWN_ERROR,
        e.getLocalizedMessage());
  }
}
