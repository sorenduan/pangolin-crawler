package org.pangolincrawler.core.plugin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.pangolincrawler.core.common.PangolinServerException;
import org.pangolincrawler.core.constants.Constants;
import org.pangolincrawler.core.job.JobConfig;
import org.pangolincrawler.core.processor.ProcessorConfig;
import org.pangolincrawler.core.service.ServiceConfig;
import org.pangolincrawler.core.utils.PublicServiceUtils;
import org.pangolincrawler.core.utils.YamlUtils;

public class PluginConfig extends BaseConfig {

  private static final long serialVersionUID = 1L;

  // without load job and processor and service config
  private boolean isSimpleConfig;

  private String pluginDir;

  private String pluginKey;

  private String description;

  private List<JobConfig> jobConfigList = new ArrayList<>();

  private List<ProcessorConfig> processorConfigList = new ArrayList<>();

  private List<ServiceConfig> serviceConfigList = new ArrayList<>();

  public PluginConfig() {
    super();
  }

  private static String convertToAbsolutePath(String filepath, String parentPath) {
    File file = new File(filepath);
    if (file.isAbsolute()) {
      return file.getAbsolutePath();
    }

    file = new File(new File(parentPath), filepath);
    return file.getAbsolutePath();
  }

  public static PluginConfig buildFromJsonObject(JsonObject jsonObj, String pluginDir) {
    PluginConfig config = buildSimpleFromJsonObject(jsonObj, pluginDir);
    config.setSimpleConfig(false);

    if (jsonObj.has(ConfigKeyType.KEY_JOB_CONFIG_FILES.getName())) {
      JsonElement jobConfigFileJson = jsonObj.get(ConfigKeyType.KEY_JOB_CONFIG_FILES.getName());
      if (jobConfigFileJson.isJsonPrimitive()) {
        String jobConfigFilepath = jobConfigFileJson.getAsString();
        config.addOption(ConfigKeyType.KEY_JOB_CONFIG_FILES.getName(), jobConfigFilepath);
        config.jobConfigList.add(buildConfigFromFile(
            convertToAbsolutePath(jobConfigFilepath, pluginDir), JobConfig.class));
      } else if (jobConfigFileJson.isJsonArray()) {
        config.addOption(ConfigKeyType.KEY_JOB_CONFIG_FILES.getName(),
            jobConfigFileJson.toString());
        Iterator<JsonElement> it = jobConfigFileJson.getAsJsonArray().iterator();
        while (it.hasNext()) {
          config.jobConfigList.add(buildConfigFromFile(
              convertToAbsolutePath(it.next().getAsString(), pluginDir), JobConfig.class));
        }
      }
    }

    if (jsonObj.has(ConfigKeyType.KEY_PROCESSOR_CONFIG_FILES.getName())) {
      JsonElement configFileJson = jsonObj.get(ConfigKeyType.KEY_PROCESSOR_CONFIG_FILES.getName());

      if (configFileJson.isJsonPrimitive()) {
        String configFilepath = configFileJson.getAsString();
        config.addOption(ConfigKeyType.KEY_PROCESSOR_CONFIG_FILES.getName(), configFilepath);
        config.processorConfigList.add(buildConfigFromFile(
            convertToAbsolutePath(configFilepath, pluginDir), ProcessorConfig.class));
      } else if (configFileJson.isJsonArray()) {
        config.addOption(ConfigKeyType.KEY_PROCESSOR_CONFIG_FILES.getName(),
            configFileJson.toString());
        Iterator<JsonElement> it = configFileJson.getAsJsonArray().iterator();
        while (it.hasNext()) {
          config.processorConfigList.add(buildConfigFromFile(
              convertToAbsolutePath(it.next().getAsString(), pluginDir), ProcessorConfig.class));
        }
      }
    }

    if (jsonObj.has(ConfigKeyType.KEY_SERVICE_CONFIG_FILES.getName())) {
      JsonElement configFileJson = jsonObj.get(ConfigKeyType.KEY_SERVICE_CONFIG_FILES.getName());

      if (configFileJson.isJsonPrimitive()) {
        String configFilepath = configFileJson.getAsString();
        config.addOption(ConfigKeyType.KEY_SERVICE_CONFIG_FILES.getName(), configFilepath);
        config.serviceConfigList.add(buildConfigFromFile(
            convertToAbsolutePath(configFilepath, pluginDir), ServiceConfig.class));
      } else if (configFileJson.isJsonArray()) {
        config.addOption(ConfigKeyType.KEY_SERVICE_CONFIG_FILES.getName(),
            configFileJson.toString());
        Iterator<JsonElement> it = configFileJson.getAsJsonArray().iterator();
        while (it.hasNext()) {
          config.serviceConfigList.add(buildConfigFromFile(
              convertToAbsolutePath(it.next().getAsString(), pluginDir), ServiceConfig.class));
        }
      }
    }

    if (CollectionUtils.isNotEmpty(config.serviceConfigList)) {
      List<String> serviceKeyList = new ArrayList<>();
      config.serviceConfigList.forEach(c -> {
        String key = PublicServiceUtils.genClassloaderKey(c.getServiceName(), c.getVersion());
        serviceKeyList.add(key);
      });
      config.addOption("service_list", StringUtils.joinWith(", ", serviceKeyList));
    }

    if (CollectionUtils.isNotEmpty(config.processorConfigList)) {
      List<String> keyList = new ArrayList<>();
      config.processorConfigList.forEach(c -> keyList.add(c.getProcessorKey()));
      config.addOption("processor_list", StringUtils.joinWith(", ", keyList));
    }

    if (CollectionUtils.isNotEmpty(config.jobConfigList)) {
      List<String> keyList = new ArrayList<>();
      config.jobConfigList.forEach(c -> keyList.add(c.getJobKey()));
      config.addOption("job_list", StringUtils.joinWith(", ", keyList));
    }

    return config;
  }

  /**
   * only name and description.
   */
  public static PluginConfig buildSimpleFromJsonObject(JsonObject jsonObj, String pluginDir) {
    PluginConfig config = new PluginConfig();
    config.pluginDir = pluginDir;
    config.isSimpleConfig = true;

    if (jsonObj.has(ConfigKeyType.KEY_PLUGIN_KEY.getName())
        && jsonObj.get(ConfigKeyType.KEY_PLUGIN_KEY.getName()).isJsonPrimitive()) {
      config.pluginKey = jsonObj.get(ConfigKeyType.KEY_PLUGIN_KEY.getName()).getAsString();
    }

    if (jsonObj.has(ConfigKeyType.KEY_DESCRIPTION.getName())
        && jsonObj.get(ConfigKeyType.KEY_DESCRIPTION.getName()).isJsonPrimitive()) {
      config.description = jsonObj.get(ConfigKeyType.KEY_DESCRIPTION.getName()).getAsString();
    }

    if (jsonObj.has(ConfigKeyType.KEY_JAVA_CLASSPATH.getName())
        && jsonObj.get(ConfigKeyType.KEY_JAVA_CLASSPATH.getName()).isJsonPrimitive()) {
      config.addOption(ConfigKeyType.KEY_JAVA_CLASSPATH.getName(),
          jsonObj.get(ConfigKeyType.KEY_JAVA_CLASSPATH.getName()).getAsString());
    }

    return config;
  }

  public String getClasspath() {
    return super.getOptionAsString(ConfigKeyType.KEY_JAVA_CLASSPATH);
  }

  @SuppressWarnings("unchecked")
  private static <T> T buildConfigFromFile(String filepath, Class<T> clazz) {

    File configFile = new File(StringUtils.trimToEmpty(filepath));
    if (!configFile.exists() || !configFile.isFile()) {
      throw new PangolinServerException(filepath + " is not existed or a file.");
    }

    if (!configFile.canRead()) {
      throw new PangolinServerException(filepath + " can not be read.");
    }

    String content;
    try {
      content = FileUtils.readFileToString(new File(filepath), Constants.DEFAULT_CHARSET);
      JsonObject json = YamlUtils.convertToJsonObject(content);

      if (JobConfig.class.equals(clazz)) {
        return (T) JobConfig.buildFromJsonObject(json);
      } else if (ProcessorConfig.class.equals(clazz)) {
        return (T) ProcessorConfig.buildFromJsonObject(json);
      } else if (ServiceConfig.class.equals(clazz)) {
        return (T) ServiceConfig.buildFromJsonObject(json);
      }

    } catch (IOException e) {
      throw new PangolinServerException("read " + filepath + " fail.", e);
    }
    return null;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPluginDir() {
    return pluginDir;
  }

  public void setPluginDir(String pluginDir) {
    this.pluginDir = pluginDir;
  }

  public String getPluginKey() {
    return pluginKey;
  }

  public void setPluginKey(String pluginKey) {
    this.pluginKey = pluginKey;
  }

  public List<JobConfig> getJobConfigList() {
    return jobConfigList;
  }

  public void setJobConfigList(List<JobConfig> jobConfigList) {
    this.jobConfigList = jobConfigList;
  }

  public List<ProcessorConfig> getProcessorConfigList() {
    return processorConfigList;
  }

  public void setProcessorConfigList(List<ProcessorConfig> processorConfigList) {
    this.processorConfigList = processorConfigList;
  }

  public List<ServiceConfig> getServiceConfigList() {
    return serviceConfigList;
  }

  public void setServiceConfigList(List<ServiceConfig> serviceConfigList) {
    this.serviceConfigList = serviceConfigList;
  }

  public boolean isSimpleConfig() {
    return isSimpleConfig;
  }

  public void setSimpleConfig(boolean isSimpleConfig) {
    this.isSimpleConfig = isSimpleConfig;
  }

}