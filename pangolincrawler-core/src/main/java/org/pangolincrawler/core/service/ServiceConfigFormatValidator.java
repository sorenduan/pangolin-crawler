package org.pangolincrawler.core.service;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.pangolincrawler.core.job.ConfigBaseValidator;
import org.pangolincrawler.core.job.ConfigValidaionResult;

public class ServiceConfigFormatValidator extends ConfigBaseValidator {
  private ServiceConfig config;

  public ServiceConfigFormatValidator(ServiceConfig config) {
    this.config = config;
  }

  @Override
  public ConfigValidaionResult validate() {
    ConfigValidaionResult r1 = this.validateBaseInfo();
    if (!r1.isSuccess()) {
      return r1;
    }

    return ConfigValidaionResult.success();
  }

  private ConfigValidaionResult validateBaseInfo() {

    if (StringUtils.isBlank(this.config.getServiceName())) {
      return ConfigValidaionResult.fail("The service name can't be blank.");
    }

    if (StringUtils.isBlank(this.config.getVersion())) {
      return ConfigValidaionResult.fail("The version can't be blank.");
    }

    if (MapUtils.isNotEmpty(this.config.getOptions())) {
      ConfigValidaionResult r = this.validateOutputOptions();
      if (r.isFail()) {
        return r;
      }
    }
    return ConfigValidaionResult.success();
  }

  private ConfigValidaionResult validateOutputOptions() {
    return ConfigValidaionResult.success();
  }
}
