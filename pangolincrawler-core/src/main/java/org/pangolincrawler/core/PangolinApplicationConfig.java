package org.pangolincrawler.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.pangolincrawler.core.constants.Constants;
import org.pangolincrawler.sdk.utils.LoggerUtils;
import org.springframework.core.io.ClassPathResource;

public final class PangolinApplicationConfig {

  /**
   * the configuration file in the jar.
   */
  public static final String DEFAULT_CONFIG_FILE = "application.properties";

  private String homeDirPath;

  private String configFilePath;

  private Properties configProperties;

  public PangolinApplicationConfig(String homePath) {
    this.homeDirPath = homePath;
    if (StringUtils.isBlank(this.homeDirPath)) {
      String defaultPath = new File("").getAbsolutePath();
      this.homeDirPath = defaultPath;
    }
    this.configFilePath = System.getProperty(Constants.SYSTEM_PROPERTY_PANGOLIN_CONFIG_FILE,
        this.getDefaultConfigFilepath());
  }

  public String getConfigDirPath() {
    return this.homeDirPath + File.separator + "config";
  }

  private String getDefaultConfigFilepath() {
    return this.getConfigDirPath() + "/pangolin.properties";
  }

  /**
   * return the configFilePath from user.
   */
  public String getConfigFilePath() {
    return configFilePath;
  }

  /**
   * get pangolin server home directory.
   */
  public String getHomeDirPath() {
    return homeDirPath;
  }

  public String getAppDirPath(String... subDirs) {

    if (null == subDirs) {
      return null;
    }
    StringJoiner joiner = new StringJoiner(File.separator);
    for (String each : subDirs) {
      joiner.add(each);
    }
    return StringUtils.joinWith(File.separator, this.getHomeDirPath(), joiner.toString());
  }

  public Properties getConfigProperties() {
    if (null != configProperties) {
      return configProperties;
    }

    try {
      ClassPathResource systemConfigFile = new ClassPathResource(DEFAULT_CONFIG_FILE);

      Properties props = new Properties();
      props.load(systemConfigFile.getInputStream());

      File customeConfigFile = new File(this.getConfigFilePath());
      if (customeConfigFile.isFile()) {
        try (FileInputStream fin = new FileInputStream(customeConfigFile)) {
          props.load(fin);
        }
      }
      configProperties = props;
    } catch (IOException e) {
      LoggerUtils.error(this.getClass(), "Load properties from file error.", e);
    }
    return configProperties;
  }

  public String getTaskSnapshotDir() {
    return getAppDirPath("var", "task_snapshot");
  }

  public String getDefaultLocalCacheDiskDir() {
    return getAppDirPath("var", "cache");
  }

  public String getDefaultLocalPluginsDir() {
    return getAppDirPath("plugins");
  }
}
