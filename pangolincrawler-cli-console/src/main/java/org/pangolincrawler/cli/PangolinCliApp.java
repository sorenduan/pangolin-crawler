package org.pangolincrawler.cli;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class PangolinCliApp {

  public static void main(String[] args) throws Exception {

    System.out.println("Starting Pangolin Crawler Command Line Console ...");

    String homeDir = getPangolinServerHomeDir();

    String configFilename = getPangolinConfigPath(homeDir);

    SpringApplicationBuilder builder = new SpringApplicationBuilder(PangolinCliApp.class);

    builder.properties(
        "spring.config.location:classpath:application.properties," + configFilename);

    SpringApplication app = builder.build();
    app.setBannerMode(Banner.Mode.CONSOLE);
    app.setLogStartupInfo(true);

    app.run(args);

  }

  private static String getPangolinConfigPath(String homeDir) {
    if (StringUtils.isNotBlank(homeDir)) {
      return homeDir + "/config/" + Constants.DEFAULT_CUSTOMER_CONFIG_FILENAME;
    }
    return "";
  }

  private static String getPangolinServerHomeDir() {
    String homeDir = System.getProperty(Constants.SYSTEM_PROPERTY_PANGOLIN_PATH_HOME);
    if (StringUtils.isBlank(homeDir)) {
      homeDir = System.getenv(Constants.ENV_PANGOLIN_HOME);
    }

    if (StringUtils.isBlank(homeDir)) {
      homeDir = System.getProperty("user.dir");
    }

    // eg. logging.file=${pangolin.path.home}/var/logs/pangolin.log
    if (StringUtils.isNotBlank(homeDir)
        && null == System.getProperty(Constants.SYSTEM_PROPERTY_PANGOLIN_PATH_HOME)) {
      System.setProperty(Constants.SYSTEM_PROPERTY_PANGOLIN_PATH_HOME, homeDir);
    }

    return homeDir;
  }

}