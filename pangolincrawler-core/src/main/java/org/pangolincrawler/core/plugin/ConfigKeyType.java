package org.pangolincrawler.core.plugin;

import org.apache.commons.lang3.BooleanUtils;

public enum ConfigKeyType {
  // common
  
  //KEY_CLASSPATH("java_classpath"),

  // job
  KEY_JOB_KEY("job_key"),

  KEY_PAYLOAD("payload"),

  KEY_CRON_EXPRESSION("cron"),

  KEY_URL("url"),

  KEY_URLS("urls"),

  KEY_FILE_OUTPUT("file_output"),

  KEY_FILE_OUTPUT_DIR("dir"),

  KEY_FILE_OUTPUT_FILENAME("filename"),

  KEY_FILE_OUTPUT_FILE_SUFFIX("suffix", ".txt"),

  KEY_FILE_OUTPUT_FILE_GENERATE_INDEX_FILE("generate_index_file", true),

  // combined or isolated
  KEY_FILE_OUTPUT_FILE_POLICY("policy", "isolated"),

  KEY_FILE_OUTPUT_FILE_ENCODING("encoding", "UTF-8"),

  KEY_LOOP("loop"),
  // null for no loop
  KEY_LOOP_LINKS_PATTERN("links_pattern"),

  KEY_LOOP_REPETITION_INVERTAL("repetition_invertal", "24h"),

  KEY_REQEUST_CACHE("request_cache"),

  KEY_REQEUST_CACHE_INTERVAL("interval", "1d"),

  KEY_REQEUST_CACHE_POLICY("policy"),

  KEY_REQEUST_RATE("request_rate"),

  KEY_REQEUST_RATE_EXPRESSION("expression"),

  KEY_REQEUST_RATE_POLICY("policy"),

  KEY_TASK_COUNT("task_count"),

  KEY_TASK_COUNT_ONE_DAY("one_day"),

  KEY_NEXT_JOB_KEY("next_job_key"),

  KEY_NEXT_JOB_CONFIG("next_job_config"),

  KEY_PROCESSOR_CONFIG("processor_config"),

  KEY_TASK_SNAPSHOT_ENABLE("task_snapshot_enable", false),

  // plugin
  KEY_PLUGIN_KEY("plugin_key"),

  KEY_JOB_CONFIG_FILES("job_config_files"), KEY_SERVICE_CONFIG_FILES(
      "service_config_files"), KEY_PROCESSOR_CONFIG_FILES("processor_config_files"),

  // processor
  KEY_PROCESSOR_KEY("processor_key"),

  KEY_DESCRIPTION("description"),

  KEY_TYPE("type"),

  KEY_SCRIPT("script"),

  KEY_EXCUTABLE("excutable"),

  KEY_CONTEXT("context"),

  KEY_JAVA_CLASS("java_class"), KEY_JAVA_CLASSPATH("java_classpath"), KEY_SCRIPT_FILEPATH(
      "script_filepath"), KEY_PROCESS_TIMEOUT("process_timeout", -1),
  // service
  KEY_SERVICE_NAME("service_name"), KEY_VERSION("version"), KEY_METHODS("methods"), KEY_METHOD_NAME(
      "method_name"), KEY_METHOD_INPUT_DESCRIPTION(
          "input_description"), KEY_METHOD_OUTPUT_DESCRIPTION(
              "output_description"), KEY_METHOD_PROCESS_TIMEOUT("process_timeout",
                  -1), KEY_METHOD_DESCRIPTION("description"),

  KEY_REQEUST_URL("request_url"),;

  private String name;
  private Object defaultValue;

  private ConfigKeyType(String name, Object defaultValue) {
    this.name = name;
    this.defaultValue = defaultValue;
  }

  private ConfigKeyType(String name) {
    this.name = name;
    this.defaultValue = null;
  }

  public String getName() {
    return name;
  }

  public Object getDefaultValue() {
    return defaultValue;
  }

  public String getDefaultValueAsString() {
    if (null != defaultValue) {
      return String.valueOf(defaultValue);
    }
    return null;
  }

  public boolean getDefaultValueAsBoolean() {
    if (null == defaultValue) {
      return false;
    } else if (null != defaultValue && defaultValue instanceof Boolean) {
      return (Boolean) defaultValue;
    }
    return BooleanUtils.toBoolean(String.valueOf(defaultValue));
  }

}