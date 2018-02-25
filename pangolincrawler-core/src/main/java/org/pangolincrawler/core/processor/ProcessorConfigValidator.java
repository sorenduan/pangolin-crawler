package org.pangolincrawler.core.processor;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.pangolincrawler.core.job.ConfigBaseValidator;
import org.pangolincrawler.core.job.ConfigValidaionResult;

public class ProcessorConfigValidator extends ConfigBaseValidator {
  private ProcessorConfig config;

  public ProcessorConfigValidator(ProcessorConfig config) {
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

    if (StringUtils.isBlank(this.config.getProcessorKey())) {
      return ConfigValidaionResult.fail("The service name can't be blank.");
    }

    if (null == this.config.getType()) {
      return ConfigValidaionResult.fail("The type can't be null.");
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
