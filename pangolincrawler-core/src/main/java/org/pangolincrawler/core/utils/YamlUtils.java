package org.pangolincrawler.core.utils;

import com.google.gson.JsonObject;

import org.yaml.snakeyaml.Yaml;


/**
 * https://bitbucket.org/asomov/snakeyaml/wiki/Documentation#markdown-header-loading-yaml.
 */
public final class YamlUtils {

  private YamlUtils() {
  }

  public static JsonObject convertToJsonObject(String yamlStr) {
    Yaml yaml = new Yaml();
    Object t = yaml.load(yamlStr);
    return JsonUtils.toJsonObject(t);
  }

}
