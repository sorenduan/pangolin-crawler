package org.pangolincrawler.core.job;

import com.google.gson.JsonObject;

import org.pangolincrawler.core.plugin.ConfigKeyType;

public class JobConfigJsonFormatValidator extends ConfigBaseValidator {

  private JsonObject json;

  public JobConfigJsonFormatValidator(JsonObject json) {
    this.json = json;
  }

  @Override
  public ConfigValidaionResult validate() {

    if (null == json) {
      return ConfigValidaionResult.fail("Config Json is Null.");
    }

    if (json.has(ConfigKeyType.KEY_JOB_KEY.getName())) {
      if (!json.get(ConfigKeyType.KEY_JOB_KEY.getName()).isJsonPrimitive()
          || !json.get(ConfigKeyType.KEY_JOB_KEY.getName()).getAsJsonPrimitive().isString()) {
        return ConfigValidaionResult
            .fail("The '" + ConfigKeyType.KEY_JOB_KEY.getName() + "' must be a string");
      }
    }

    if (json.has(ConfigKeyType.KEY_FILE_OUTPUT.getName())) {
      if (!json.get(ConfigKeyType.KEY_FILE_OUTPUT.getName()).isJsonObject()) {
        return ConfigValidaionResult
            .fail("The '" + ConfigKeyType.KEY_FILE_OUTPUT + "' must be valid struct.");
      }
    }

    return ConfigValidaionResult.success();
  }

}
