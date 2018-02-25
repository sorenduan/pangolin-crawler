package org.pangolincrawler.core.startup;

import org.pangolincrawler.core.PangolinApplication;
import org.pangolincrawler.core.constants.Constants;
import org.pangolincrawler.core.plugin.PluginManager;

public class StartupManager {

  private StartupManager() {}
  
  /**
   * must run after spring boot app startup.
   */
  public static void start() {

    PluginManager pluginManager = PangolinApplication.getSystemService(PluginManager.class);
    pluginManager.loadLocalPlugins();

    boolean autoResend = PangolinApplication.getPangolinProperty(
        Constants.PangolinPropertyType.PROPERTY_HONEYCOMB_TASK_CRUSHED_AUTORESEND.getKey(), Boolean.class,
        Constants.PangolinPropertyType.PROPERTY_HONEYCOMB_TASK_CRUSHED_AUTORESEND
            .getDefaultValue(Boolean.class));
    if (autoResend) {
      CrushedTaskRestarter.start();
    }

  }

  public static void stop() {
    CrushedTaskRestarter.stop();
  }
}
