package org.pangolincrawler.core.job;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.pangolincrawler.core.plugin.BaseConfig;
import org.pangolincrawler.core.plugin.ConfigKeyType;
import org.pangolincrawler.core.processor.ProcessorConfig;
import org.pangolincrawler.core.utils.JsonUtils;
import org.pangolincrawler.core.utils.SourceType;

public class JobConfig extends BaseConfig {

  private static final long serialVersionUID = 1L;

  private static String KEY_JOB_KEY = "job_key";

  private static String KEY_CRON_EXPRESSION = "cron";

  private static String KEY_PROCESSOR_KEY = "processor_key";

  private static String KEY_PAYLOAD = "payload";

  private static String KEY_DESCRIPTION = "description";

  public static String KEY_URL = "url";

  private String jobKey;

  private String cronExpression;

  private String processorKey;

  private ProcessorConfig processorConfig;

  private String description;

  private Object payload;

  private SourceType source = SourceType.ManualSourceType;

  public JobConfig() {
    super();
  }

  public void addFileOutputOption(String key, String value) {
    this.addOptions(ConfigKeyType.KEY_FILE_OUTPUT, key, value);
  }

  public void addLoopOption(String key, String value) {
    this.addOptions(ConfigKeyType.KEY_LOOP, key, value);
  }

  public String getFileOutputOption(String key) {
    return MapUtils.getString(this.getOutputFileOptions(), key);
  }

  public boolean getFileOutputOptionAsBoolean(String key) {
    String v = getFileOutputOption(key);
    return BooleanUtils.toBoolean(v);
  }

  public String getJobKey() {
    return jobKey;
  }

  public void setJobKey(String jobKey) {
    this.jobKey = jobKey;
  }

  public Object getPayload() {
    return payload;
  }

  public void setPayload(Object payload) {
    this.payload = payload;
  }

  public String getPayloadAsJson() {
    if (null == this.payload) {
      return null;
    }
    Gson gson = new Gson();
    return gson.toJson(this.payload);
  }

  public String getCronExpression() {
    return cronExpression;
  }

  public void setCronExpression(String cronExpression) {
    this.cronExpression = cronExpression;
  }

  public String getProcessorKey() {
    return processorKey;
  }

  public void setProcessorKey(String processorKey) {
    this.processorKey = processorKey;
  }

  public static JobConfig buildFromJsonObject(JsonObject jsonObj) {

    JobConfig config = new JobConfig();

    if (jsonObj.has(KEY_JOB_KEY) && jsonObj.get(KEY_JOB_KEY).isJsonPrimitive()) {
      config.setJobKey(jsonObj.get(KEY_JOB_KEY).getAsString());
    }

    if (jsonObj.has(KEY_CRON_EXPRESSION) && jsonObj.get(KEY_CRON_EXPRESSION).isJsonPrimitive()) {
      config.setCronExpression(jsonObj.get(KEY_CRON_EXPRESSION).getAsString());
    }

    if (jsonObj.has(KEY_PROCESSOR_KEY) && jsonObj.get(KEY_PROCESSOR_KEY).isJsonPrimitive()) {
      config.setProcessorKey(jsonObj.get(KEY_PROCESSOR_KEY).getAsString());
    }

    if (jsonObj.has(KEY_DESCRIPTION) && jsonObj.get(KEY_DESCRIPTION).isJsonPrimitive()) {
      config.setDescription(jsonObj.get(KEY_DESCRIPTION).getAsString());
    }

    if (jsonObj.has(KEY_PAYLOAD)) {
      config.setPayload(jsonObj.get(KEY_PAYLOAD));
    }

    if (jsonObj.has(ConfigKeyType.KEY_TASK_SNAPSHOT_ENABLE.getName())) {
      config.addOption(ConfigKeyType.KEY_TASK_SNAPSHOT_ENABLE.getName(),
          jsonObj.get(ConfigKeyType.KEY_TASK_SNAPSHOT_ENABLE.getName()).getAsBoolean());
    }

    if (jsonObj.has(ConfigKeyType.KEY_NEXT_JOB_KEY.getName())
        && jsonObj.get(ConfigKeyType.KEY_NEXT_JOB_KEY.getName()).isJsonPrimitive()) {
      config.addOption(ConfigKeyType.KEY_NEXT_JOB_KEY,
          jsonObj.get(ConfigKeyType.KEY_NEXT_JOB_KEY.getName()).getAsString());
    }

    if (jsonObj.has(ConfigKeyType.KEY_NEXT_JOB_CONFIG.getName())
        && jsonObj.get(ConfigKeyType.KEY_NEXT_JOB_CONFIG.getName()).isJsonObject()) {
      config.addOption(ConfigKeyType.KEY_NEXT_JOB_CONFIG,
          jsonObj.get(ConfigKeyType.KEY_NEXT_JOB_CONFIG.getName()).getAsJsonObject());
    }

    fillBaseOptions(config, jsonObj);

    if (jsonObj.has(ConfigKeyType.KEY_FILE_OUTPUT.getName())
        && jsonObj.get(ConfigKeyType.KEY_FILE_OUTPUT.getName()).isJsonObject()) {
      fillFileOutputOptions(config,
          jsonObj.get(ConfigKeyType.KEY_FILE_OUTPUT.getName()).getAsJsonObject());
    }

    if (jsonObj.has(ConfigKeyType.KEY_LOOP.getName())
        && jsonObj.get(ConfigKeyType.KEY_LOOP.getName()).isJsonObject()) {
      fillLoopOptions(config, jsonObj.get(ConfigKeyType.KEY_LOOP.getName()).getAsJsonObject());
    }

    if (jsonObj.has(ConfigKeyType.KEY_REQEUST_RATE.getName())
        && jsonObj.get(ConfigKeyType.KEY_REQEUST_RATE.getName()).isJsonObject()) {
      fillRequestRateOptions(config,
          jsonObj.get(ConfigKeyType.KEY_REQEUST_RATE.getName()).getAsJsonObject());
    }

    if (jsonObj.has(ConfigKeyType.KEY_TASK_COUNT.getName())
        && jsonObj.get(ConfigKeyType.KEY_TASK_COUNT.getName()).isJsonObject()) {
      fillTaskCountOptions(config,
          jsonObj.get(ConfigKeyType.KEY_TASK_COUNT.getName()).getAsJsonObject());
    }

    return config;
  }

  private static void fillBaseOptions(JobConfig config, JsonObject jsonObj) {
    if (jsonObj.has(ConfigKeyType.KEY_URL.getName())
        && jsonObj.get(ConfigKeyType.KEY_URL.getName()).isJsonPrimitive()) {
      config.addOption(ConfigKeyType.KEY_URL.getName(),
          jsonObj.get(ConfigKeyType.KEY_URL.getName()).getAsString());
    }

    if (jsonObj.has(ConfigKeyType.KEY_URLS.getName())
        && jsonObj.get(ConfigKeyType.KEY_URLS.getName()).isJsonArray()) {
      List<String> urls = JsonUtils
          .jsonArrayToStringList(jsonObj.get(ConfigKeyType.KEY_URLS.getName()).getAsJsonArray());
      config.addOption(ConfigKeyType.KEY_URLS.getName(), urls);
    }

    if (jsonObj.has(ConfigKeyType.KEY_PROCESSOR_CONFIG.getName())
        && jsonObj.get(ConfigKeyType.KEY_PROCESSOR_CONFIG.getName()).isJsonObject()) {
      JsonObject processoConfig = jsonObj.get(ConfigKeyType.KEY_PROCESSOR_CONFIG.getName())
          .getAsJsonObject();
      config.processorConfig = ProcessorConfig.buildFromJsonObject(processoConfig);
      config.addOption(ConfigKeyType.KEY_PROCESSOR_CONFIG, JsonUtils.toMap(processoConfig));
    }
  }

  private static void fillFileOutputOptions(JobConfig config, JsonObject jsonObj) {
    if (jsonObj.has(ConfigKeyType.KEY_FILE_OUTPUT_DIR.getName())
        && jsonObj.get(ConfigKeyType.KEY_FILE_OUTPUT_DIR.getName()).isJsonPrimitive()) {
      config.addFileOutputOption(ConfigKeyType.KEY_FILE_OUTPUT_DIR.getName(),
          jsonObj.get(ConfigKeyType.KEY_FILE_OUTPUT_DIR.getName()).getAsString());
    }

    if (jsonObj.has(ConfigKeyType.KEY_FILE_OUTPUT_FILE_SUFFIX.getName())
        && jsonObj.get(ConfigKeyType.KEY_FILE_OUTPUT_FILE_SUFFIX.getName()).isJsonPrimitive()) {
      config.addFileOutputOption(ConfigKeyType.KEY_FILE_OUTPUT_FILE_SUFFIX.getName(),
          jsonObj.get(ConfigKeyType.KEY_FILE_OUTPUT_FILE_SUFFIX.getName()).getAsString());
    }

    if (jsonObj.has(ConfigKeyType.KEY_FILE_OUTPUT_FILE_GENERATE_INDEX_FILE.getName()) && jsonObj
        .get(ConfigKeyType.KEY_FILE_OUTPUT_FILE_GENERATE_INDEX_FILE.getName()).isJsonPrimitive()) {
      config.addFileOutputOption(ConfigKeyType.KEY_FILE_OUTPUT_FILE_GENERATE_INDEX_FILE.getName(),
          jsonObj.get(ConfigKeyType.KEY_FILE_OUTPUT_FILE_GENERATE_INDEX_FILE.getName())
              .getAsString());
    }

    if (jsonObj.has(ConfigKeyType.KEY_FILE_OUTPUT_FILENAME.getName())
        && jsonObj.get(ConfigKeyType.KEY_FILE_OUTPUT_FILENAME.getName()).isJsonPrimitive()) {
      config.addFileOutputOption(ConfigKeyType.KEY_FILE_OUTPUT_FILENAME.getName(),
          jsonObj.get(ConfigKeyType.KEY_FILE_OUTPUT_FILENAME.getName()).getAsString());
    }
  }

  private static void fillLoopOptions(JobConfig config, JsonObject jsonObj) {

    if (jsonObj.has(ConfigKeyType.KEY_LOOP_LINKS_PATTERN.getName())
        && jsonObj.get(ConfigKeyType.KEY_LOOP_LINKS_PATTERN.getName()).isJsonPrimitive()) {
      config.addLoopOption(ConfigKeyType.KEY_LOOP_LINKS_PATTERN.getName(),
          jsonObj.get(ConfigKeyType.KEY_LOOP_LINKS_PATTERN.getName()).getAsString());
    }

    if (jsonObj.has(ConfigKeyType.KEY_LOOP_REPETITION_INVERTAL.getName())
        && jsonObj.get(ConfigKeyType.KEY_LOOP_REPETITION_INVERTAL.getName()).isJsonPrimitive()) {
      config.addLoopOption(ConfigKeyType.KEY_LOOP_REPETITION_INVERTAL.getName(),
          jsonObj.get(ConfigKeyType.KEY_LOOP_REPETITION_INVERTAL.getName()).getAsString());
    }
  }

  private static void fillTaskCountOptions(JobConfig config, JsonObject jsonObj) {
    if (jsonObj.has(ConfigKeyType.KEY_TASK_COUNT_ONE_DAY.getName())
        && jsonObj.get(ConfigKeyType.KEY_TASK_COUNT_ONE_DAY.getName()).isJsonPrimitive()) {
      config.addOptions(ConfigKeyType.KEY_TASK_COUNT, ConfigKeyType.KEY_TASK_COUNT_ONE_DAY,
          jsonObj.get(ConfigKeyType.KEY_TASK_COUNT_ONE_DAY.getName()).getAsInt());
    }

  }

  private static void fillRequestRateOptions(JobConfig config, JsonObject jsonObj) {

    if (jsonObj.has(ConfigKeyType.KEY_REQEUST_RATE_EXPRESSION.getName())
        && jsonObj.get(ConfigKeyType.KEY_REQEUST_RATE_EXPRESSION.getName()).isJsonPrimitive()) {
      config.addOptions(ConfigKeyType.KEY_REQEUST_RATE, ConfigKeyType.KEY_REQEUST_RATE_EXPRESSION,
          jsonObj.get(ConfigKeyType.KEY_REQEUST_RATE_EXPRESSION.getName()).getAsString());
    }
    if (jsonObj.has(ConfigKeyType.KEY_REQEUST_RATE_POLICY.getName())
        && jsonObj.get(ConfigKeyType.KEY_REQEUST_RATE_POLICY.getName()).isJsonPrimitive()) {
      config.addOptions(ConfigKeyType.KEY_REQEUST_RATE, ConfigKeyType.KEY_REQEUST_RATE_POLICY,
          jsonObj.get(ConfigKeyType.KEY_REQEUST_RATE_POLICY.getName()).getAsString());
    }

  }

  public static JobConfig buildFromJsonString(String jsonObj) {
    JsonObject json = JsonUtils.toJsonObject(jsonObj);
    return buildFromJsonObject(json);
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public SourceType getSource() {
    return source;
  }

  public void setSource(SourceType source) {
    this.source = source;
  }

  public ProcessorConfig getProcessorConfig() {
    return processorConfig;
  }

}