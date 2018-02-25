package org.pangolincrawler.core.utils;

import org.apache.commons.lang3.StringUtils;

public class SourceType {

  private static final String SP = ":";

  private Type type;
  private String extra;

  public static final SourceType ManualSourceType = SourceType.build(Type.MANUAL, "");
  public static final SourceType SystemSourceType = SourceType.build(Type.SYSTEM, "");

  public static enum Type {
    PLUGIN("plugin"), MANUAL("manual"), SYSTEM("system");

    private String name;

    private Type(String name) {
      this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
      return name;
    }


    public static Type fromName(String name) {
      String[] parts = StringUtils.split(name, SP);
      Type[] typeArr = Type.values();
      for (Type each : typeArr) {
        if (StringUtils.equals(each.getName(), parts[0])) {
          return each;
        }
      }
      return null;
    }
  }

  public static SourceType buildStringString(String source) {

    String[] parts = StringUtils.split(source, SP);
    if (null == parts || parts.length < 1) {
      return null;
    }

    Type type = Type.fromName(source);

    if (null == type) {
      return null;
    }

    String extra = parts.length >= 2 ? StringUtils.trimToEmpty(parts[1]) : null;

    return build(type, extra);
  }

  public static SourceType buildPluginSourceType(String pluginKey) {
    return build(Type.PLUGIN, pluginKey);
  }

  public static SourceType build(Type type, String extra) {
    SourceType r = new SourceType();
    r.type = type;
    r.extra = extra;
    return r;
  }

  @Override
  public String toString() {
    if (StringUtils.isNotBlank(this.extra)) {
      return type.getName() + SP + this.extra;
    }
    return this.type.getName();
  }

  public boolean isPluglinSource() {
    return Type.PLUGIN.equals(this.type);
  }

  /**
   * @return the type
   */
  public Type getType() {
    return type;
  }

  /**
   * @return the extra
   */
  public String getExtra() {
    return extra;
  }

}