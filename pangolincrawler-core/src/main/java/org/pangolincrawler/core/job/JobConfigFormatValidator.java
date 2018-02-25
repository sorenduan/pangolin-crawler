package org.pangolincrawler.core.job;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.pangolincrawler.core.plugin.ConfigKeyType;

public class JobConfigFormatValidator extends ConfigBaseValidator {
  private JobConfig config;

  public JobConfigFormatValidator(JobConfig config) {
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

    if (StringUtils.isBlank(this.config.getJobKey())) {
      return ConfigValidaionResult.fail("The job key can't be blank.");
    }

    if (StringUtils.isBlank(this.config.getProcessorKey())
        && this.config.getProcessorConfig() == null) {
      return ConfigValidaionResult
          .fail("You must set the processor key or processor config for the job '"
              + this.config.getJobKey() + "'.");
    }

    if (MapUtils.isNotEmpty(this.config.getOutputFileOptions())) {
      ConfigValidaionResult r = this.validateOutputOptions();
      if (r.isFail()) {
        return r;
      }
    }

    return ConfigValidaionResult.success();
  }

  private ConfigValidaionResult validateOutputOptions() {
    if (StringUtils
        .isBlank(this.config.getFileOutputOption(ConfigKeyType.KEY_FILE_OUTPUT_DIR.getName()))) {
      return ConfigValidaionResult
          .fail("You must specify the '" + ConfigKeyType.KEY_FILE_OUTPUT_DIR.getName() + "'");
    }

    return ConfigValidaionResult.success();
  }
}
