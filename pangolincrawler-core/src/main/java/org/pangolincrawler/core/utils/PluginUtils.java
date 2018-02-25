package org.pangolincrawler.core.utils;

import com.google.gson.JsonObject;

import org.pangolincrawler.core.plugin.PluginConfig;
import org.pangolincrawler.core.plugin.PluginPoJo;

public final class PluginUtils {

  private PluginUtils() {
  }

  public static PluginPoJo convertFromTpl(PluginConfig config) {
    PluginPoJo pluginPojo = new PluginPoJo();

    pluginPojo.setPlubinKey(config.getPluginKey());
    pluginPojo.setDescription(config.getDescription());

    JsonObject json = JsonUtils.toJsonObject(config.getOptions());
    pluginPojo.setAttributeJson(json.toString());

    return pluginPojo;
  }

}
