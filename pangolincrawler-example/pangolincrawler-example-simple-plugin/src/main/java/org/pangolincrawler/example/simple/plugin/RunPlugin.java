package org.pangolincrawler.example.simple.plugin;

import java.util.HashMap;
import java.util.Map;

import org.pangolincrawler.core.PangolinApplication;
import org.pangolincrawler.core.constants.Constants;
import org.pangolincrawler.core.plugin.PluginConfig;

public class RunPlugin {

  public RunPlugin() {

  }

  public static void main(String[] args) {

    Map<String, String> params = new HashMap<>();
    params.put(Constants.SYSTEM_PROPERTY_PANGOLIN_PATH_HOME,
        "/distribution");
    PangolinApplication.run(args, params);

    String pluginConfigDir = "pangolincrawler-example-simple-plugin/src/main/resources/";

    PluginConfig config = PangolinApplication
        .registerPluginFromPluginDir(pluginConfigDir);

    // PangolinApplication.unregisterPlugin(config.getPluginKey());
    System.out.println();
  }
}
