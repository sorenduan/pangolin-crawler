package org.pangolincrawler.core;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.pangolincrawler.core.cache.CacheManager;
import org.pangolincrawler.core.config.SystemInfo;
import org.pangolincrawler.core.constants.Constants;
import org.pangolincrawler.core.constants.Constants.PangolinPropertyType;
import org.pangolincrawler.core.db.SystemCommonRdbService;
import org.pangolincrawler.core.http.HttpClientService;
import org.pangolincrawler.core.plugin.PluginConfig;
import org.pangolincrawler.core.plugin.PluginManager;
import org.pangolincrawler.core.startup.StartupManager;
import org.pangolincrawler.core.task.TaskManager;
import org.pangolincrawler.core.tools.JvmVersionChecker;
import org.pangolincrawler.core.utils.LoggerUtils;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

@SpringBootApplication(scanBasePackages = "org.pangolincrawler")
public class PangolinApplication {

  /**
   * global configuration.
   */
  private static PangolinApplicationConfig config;

  /**
   * server start time.
   */
  private static long bootAt;

  /**
   * global context.
   */
  private static ApplicationContext applicationContext;

  private static ConfigurableEnvironment environment;

  private static void stopDebugLogOnStartUp() {
    ILoggerFactory f = LoggerFactory.getILoggerFactory();
    Logger logger = f.getLogger("ROOT");
    if (logger instanceof ch.qos.logback.classic.Logger) {
      ((ch.qos.logback.classic.Logger) logger).setLevel(ch.qos.logback.classic.Level.ERROR);
    }
  }

  public static void run(String[] args) {
    run(args, Collections.emptyMap());
  }

  /**
   * https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-external-config.
   */
  public static void run(String[] args, Map<String, String> jvmProps) {

    if (!JvmVersionChecker.checkJvmVersion()) {
      System.exit(1);
    }

    bootAt = System.currentTimeMillis();

    System.out.println("Starting Pangolin Crawler Server...");
    stopDebugLogOnStartUp();

    // System.setProperty("debug", "false");
    System.setProperty("org.jooq.no-logo", "true");
    System.setProperty("https.protocols", "TLSv1.1");
    System.setProperty("banner.location", "classpath:server_banner.txt");
    // System.setProperty("logging.level.root", "OFF");

    if (MapUtils.isNotEmpty(jvmProps)) {
      jvmProps.forEach(System::setProperty);
    }

    String homeDir = getPangolinServerHomeDir();

    config = new PangolinApplicationConfig(homeDir);
    if (!checkBaseConfig()) {
      System.exit(Constants.SYS_EXIT_CODE_CONFIG_ERROR);
      return;
    }

    SpringApplicationBuilder builder = new SpringApplicationBuilder(PangolinApplication.class);

    builder.properties("spring.config.location:classpath:"
        + PangolinApplicationConfig.DEFAULT_CONFIG_FILE + "," + config.getConfigFilePath());


    SpringApplication app = builder.build();
    app.setBannerMode(Banner.Mode.CONSOLE);
    app.setLogStartupInfo(true);

    try {
      ConfigurableApplicationContext applicationContext = app.run(args);
      PangolinApplication.applicationContext = applicationContext;
      PangolinApplication.environment = applicationContext.getEnvironment();
      StartupManager.start();
      System.out.println("Start pangolin server start success.");
    } catch (Exception e) {
      e.printStackTrace();
      LoggerUtils.error(PangolinApplication.class, "Start pangolin server error, ", e);
      if (null != applicationContext) {
        shutdown();
      } else {
        System.exit(1);
      }
    }

  }

  private static String getPangolinServerHomeDir() {
    String homeDir = System.getProperty(Constants.SYSTEM_PROPERTY_PANGOLIN_PATH_HOME);
    if (StringUtils.isBlank(homeDir)) {
      homeDir = System.getenv(Constants.ENV_PANGOLIN_HOME);
    }

    if (StringUtils.isBlank(homeDir)) {
      homeDir = System.getProperty("user.dir");
    }

    // for log config.
    // eg. logging.file=${pangolin.path.home}/var/logs/pangolin.log
    if (StringUtils.isNotBlank(homeDir)
        && null == System.getProperty(Constants.SYSTEM_PROPERTY_PANGOLIN_PATH_HOME)) {
      System.setProperty(Constants.SYSTEM_PROPERTY_PANGOLIN_PATH_HOME, homeDir);
    }

    return homeDir;
  }

  /**
   * Used for plugin development.
   * 
   * @param pulginDir
   *          the directory that contain the 'plugin.yaml' file.
   * @return
   */
  public static PluginConfig registerPluginFromPluginDir(String pulginDir) {
    PluginManager pluginManager = getSystemService(PluginManager.class);
    if (pluginManager != null) {
      return pluginManager.registerPluginFromPluginDir(pulginDir);
    }
    return null;
  }

  public static void unregisterPlugin(String pluginKey) {
    PluginManager pluginManager = getSystemService(PluginManager.class);
    if (null != pluginManager) {
      pluginManager.unregisterPlugin(pluginKey);
    }
  }

  public static <T> T getSystemService(Class<T> clazz) {
    if (null != applicationContext) {
      return applicationContext.getBean(clazz);
    }
    return null;
  }

  public static String getPangolinProperty(String key) {
    return getPangolinProperty(key, null);
  }

  public static String getPangolinProperty(PangolinPropertyType type) {
    if (null != type) {
      return getPangolinProperty(type.getKey(), type.getDefaultValue(String.class));
    }
    return null;
  }

  public static Boolean getPangolinPropertyAsBoolean(PangolinPropertyType type) {
    if (null != type) {
      Object v = getPangolinProperty(type.getKey());

      if (null == v) {
        v = type.getDefaultValue(Boolean.class);
      }

      if (null != v && v instanceof Boolean) {
        return (boolean) v;
      }

    }
    return false;
  }

  public static String getPangolinProperty(String key, String defaultValue) {
    return getPangolinProperty(key, String.class, defaultValue);
  }

  public static <T> T getPangolinProperty(String key, Class<T> clazz, T defaultValue) {
    try {
      if (environment.containsProperty(key)) {
        T v = environment.getProperty(key, clazz, defaultValue);
        if (null != v) {
          return v;
        }
      }
    } catch (NullPointerException e) {
      LoggerUtils.warn(PangolinApplication.class, "The property {} is not existed.", key);
      return defaultValue;
    }
    return defaultValue;
  }

  public static <T> T getPangolinPropertyWithPropertyType(
      Constants.PangolinPropertyType propertyType, Class<T> clazz) {
    return getPangolinProperty(propertyType.getKey(), clazz, propertyType.getDefaultValue(clazz));
  }

  public static SystemCommonRdbService getSystemCommonRdbService() {
    return getSystemService(SystemCommonRdbService.class);
  }

  public static TaskManager getTaskManagerService() {
    return getSystemService(TaskManager.class);
  }

  public static HttpClientService getHttpClientService() {
    return getSystemService(HttpClientService.class);
  }

  public static CacheManager getCacheManagerService() {
    return getSystemService(CacheManager.class);
  }

  private static boolean checkBaseConfig() {

    if (StringUtils.isBlank(config.getHomeDirPath())) {
      LoggerUtils.warn("Pangolin Home Dir (" + Constants.ENV_PANGOLIN_HOME + ")  is blank",
          PangolinApplication.class);
    }

    if (!new File(config.getConfigFilePath()).exists()) {
      LoggerUtils.error("The config file '" + config.getConfigFilePath() + "' is not existed !",
          PangolinApplication.class);
      return false;
    }

    return true;
  }

  public static void shutdown() {
    LoggerUtils.info("Shutdown....", PangolinApplication.class);
    StartupManager.stop();

    CacheManager cacheManager = getSystemService(CacheManager.class);
    if (null != cacheManager) {
      cacheManager.shutdown(SystemInfo.getHostname());
    }
    if (null != applicationContext) {
      SpringApplication.exit(applicationContext);
    }

  }

  public static void main(String[] args) {
    PangolinApplication.run(args);
  }

  public static PangolinApplicationConfig getConfig() {
    return config;
  }

  /**
   * used for testing
   */
  public static void setConfig(PangolinApplicationConfig c) {
    config = c;
  }

  public static long getBootAt() {
    return bootAt;
  }

}
