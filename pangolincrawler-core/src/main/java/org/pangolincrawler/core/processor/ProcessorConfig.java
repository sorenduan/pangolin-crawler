package org.pangolincrawler.core.processor;

import com.google.gson.JsonObject;

import org.pangolincrawler.core.plugin.BaseConfig;
import org.pangolincrawler.core.plugin.ConfigKeyType;
import org.pangolincrawler.core.utils.JsonUtils;
import org.pangolincrawler.core.utils.SourceType;

public class ProcessorConfig extends BaseConfig {

  private static final long serialVersionUID = 1L;

  private String processorKey;

  private String description;

  private String className;

  private SourceType source = SourceType.ManualSourceType;

  private ProcessorPoJo.ProcessorType type;

  public static ProcessorConfig buildFromJsonObject(JsonObject jsonObj) {

    ProcessorConfig config = new ProcessorConfig();

    if (jsonObj.has(ConfigKeyType.KEY_PROCESSOR_KEY.getName())) {
      config.processorKey = jsonObj.get(ConfigKeyType.KEY_PROCESSOR_KEY.getName()).getAsString();
    }

    if (jsonObj.has(ConfigKeyType.KEY_DESCRIPTION.getName())) {
      config.description = jsonObj.get(ConfigKeyType.KEY_DESCRIPTION.getName()).getAsString();
    }

    if (jsonObj.has(ConfigKeyType.KEY_CONTEXT.getName())
        && jsonObj.get(ConfigKeyType.KEY_CONTEXT.getName()).isJsonObject()) {
      JsonObject context = jsonObj.get(ConfigKeyType.KEY_CONTEXT.getName()).getAsJsonObject();
      config.addOption(ConfigKeyType.KEY_CONTEXT, JsonUtils.toMap(context));
    }

    if (jsonObj.has(ConfigKeyType.KEY_TYPE.getName())) {
      String typeStr = jsonObj.get(ConfigKeyType.KEY_TYPE.getName()).getAsString();
      config.type = ProcessorPoJo.ProcessorType.fromName(typeStr);
    }

    if (jsonObj.has(ConfigKeyType.KEY_JAVA_CLASS.getName())) {
      config.className = jsonObj.get(ConfigKeyType.KEY_JAVA_CLASS.getName()).getAsString();
    }
    if (jsonObj.has(ConfigKeyType.KEY_DESCRIPTION.getName())) {
      config.description = jsonObj.get(ConfigKeyType.KEY_DESCRIPTION.getName()).getAsString();
    }

    if (jsonObj.has(ConfigKeyType.KEY_EXCUTABLE.getName())
        && jsonObj.get(ConfigKeyType.KEY_EXCUTABLE.getName()).isJsonPrimitive()) {
      config.addOption(ConfigKeyType.KEY_EXCUTABLE.getName(),
          jsonObj.get(ConfigKeyType.KEY_EXCUTABLE.getName()).getAsString());
    }

    if (jsonObj.has(ConfigKeyType.KEY_JAVA_CLASSPATH.getName())
        && jsonObj.get(ConfigKeyType.KEY_JAVA_CLASSPATH.getName()).isJsonPrimitive()) {
      config.addOption(ConfigKeyType.KEY_JAVA_CLASSPATH.getName(),
          jsonObj.get(ConfigKeyType.KEY_JAVA_CLASSPATH.getName()).getAsString());
    }

    if (jsonObj.has(ConfigKeyType.KEY_SCRIPT_FILEPATH.getName())
        && jsonObj.get(ConfigKeyType.KEY_SCRIPT_FILEPATH.getName()).isJsonPrimitive()) {
      config.addOption(ConfigKeyType.KEY_SCRIPT_FILEPATH.getName(),
          jsonObj.get(ConfigKeyType.KEY_SCRIPT_FILEPATH.getName()).getAsString());
    }

    return config;
  }

  public String getClasspath() {
    return super.getOptionAsString(ConfigKeyType.KEY_JAVA_CLASSPATH);
  }

  public static ProcessorConfig buildFromJsonString(String jsonObj) {
    JsonObject json = JsonUtils.toJsonObject(jsonObj);
    return buildFromJsonObject(json);
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getProcessorKey() {
    return processorKey;
  }

  public ProcessorPoJo.ProcessorType getType() {
    return type;
  }

  public String getClassName() {
    return className;
  }

  public SourceType getSource() {
    return source;
  }

  public void setSource(SourceType source) {
    this.source = source;
  }
}