package org.pangolincrawler.cli;

public final class Constants {

  private Constants() {
  }

  public static final String GROUP_KEY_JOB = "Job Commands";
  public static final String GROUP_KEY_CACHE = "Cache Commands";
  public static final String GROUP_KEY_TASK = "Task Commands";
  public static final String GROUP_KEY_TPL = "Job and Processor skeleton template Commands";

  public static final String GROUP_KEY_PROCESSOR = "Processor Commands";

  public static final String GROUP_KEY_SERVICE = "Service And Methods Commands";

  public static final String GROUP_KEY_PLUGIN = "Plugin Commands";

  public static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

  public static final String SYSTEM_PROPERTY_PANGOLIN_PATH_HOME = "pangolin.path.home";

  public static final String DEFAULT_CHARSET = "UTF-8";

  public static final String ENV_PANGOLIN_HOME = "PANGOLIN_HOME";

  public static final String DEFAULT_CUSTOMER_CONFIG_FILENAME = "pangolin-cli-console.properties";

  public static final String COMMAND_NEW_LINE_INDENT = "\n                     ";
}
