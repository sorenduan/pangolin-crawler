package org.pangolincrawler.core.web.api;

import org.pangolincrawler.core.utils.ResourceFileUtils;

public final class TplFactory {

  public enum TplType {
    CSS_SIMPLE_TPL("example_with_css_selector",
        "A job config example for fetch url and parse with css selector.",
        "classpath:yaml_tpl/example_with_css_selector.yaml"), SIMPLE_TPL("example_simple_job_tpl",
            "A simple job example just echo payload.",
            "classpath:yaml_tpl/example_simple_job_tpl.yaml"),

    BASIC_PROCESSOR_TPL("basic_processor_tpl", "A basic processor template.",
        "classpath:yaml_tpl/basic_processor_tpl.yaml"),

    BASIC_SERVICE_TPL("basic_public_service_tpl", "A basic service config template.",
        "classpath:yaml_tpl/basic_public_service_tpl.yaml"),

    BASIC_PLUGIN_TPL("basic_plugin_tpl", "A basic plugin config template.",
        "classpath:yaml_tpl/basic_public_service_tpl.yaml");

    private String name;
    private String desc;
    private String path;

    private TplType(String name, String desc, String path) {
      this.name = name;
      this.desc = desc;
      this.path = path;
    }

    public static TplType fromName(String name) {

      TplType[] tpls = TplType.values();

      for (TplType each : tpls) {
        if (each.name.equalsIgnoreCase(name)) {
          return each;
        }
      }
      return null;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getDesc() {
      return desc;
    }

    public void setDesc(String desc) {
      this.desc = desc;
    }

    public String getPath() {
      return path;
    }

    public void setPath(String path) {
      this.path = path;
    }
  }

  public static String getTplContent(TplType type) {
    return ResourceFileUtils.getString(type.getPath());
  }

}
