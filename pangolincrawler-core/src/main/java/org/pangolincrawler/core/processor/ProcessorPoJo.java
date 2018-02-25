package org.pangolincrawler.core.processor;

import com.google.gson.JsonObject;

import java.util.Date;
import java.util.Map;

import org.apache.commons.codec.binary.StringUtils;
import org.pangolincrawler.core.plugin.ConfigKeyType;
import org.pangolincrawler.core.utils.JsonUtils;

public class ProcessorPoJo {

  public enum ProcessorType {
    JAVA("java"), GROOVY("groovy"), SCRIPT("script");
    private String name;

    private ProcessorType(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public static ProcessorPoJo.ProcessorType fromName(String name) {
      ProcessorPoJo.ProcessorType[] typeArr = ProcessorPoJo.ProcessorType.values();
      for (ProcessorPoJo.ProcessorType each : typeArr) {
        if (StringUtils.equals(each.getName(), name)) {
          return each;
        }
      }
      return null;
    }
  }

  private long id;

  private String processorKey;

  private String processorClass;

  private String attributeJson;

  private String description;

  private String source;

  private Date createAt;

  private Date modifyAt;

  public String getProcessorKey() {
    return processorKey;
  }

  public void setProcessorKey(String processorKey) {
    this.processorKey = processorKey;
  }

  public String getProcessorClassName() {
    return processorClass;
  }

  public void setProcessorClass(String processorClass) {
    this.processorClass = processorClass;
  }

  public String getAttributeJson() {
    return attributeJson;
  }

  public void setAttributeJson(String attributeJson) {
    this.attributeJson = attributeJson;
  }

  public Date getCreateAt() {
    return createAt;
  }

  public void setCreateAt(Date createAt) {
    this.createAt = createAt;
  }

  public Date getModifyAt() {
    return modifyAt;
  }

  public void setModifyAt(Date modifyAt) {
    this.modifyAt = modifyAt;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  private JsonObject getAttributeJsonAsJsonObject() {
    return JsonUtils.toJsonObject(this.attributeJson);
  }

  public Map<String, Object> getContext() {
    JsonObject attrJson = getAttributeJsonAsJsonObject();
    if (null != attrJson && attrJson.has(ConfigKeyType.KEY_CONTEXT.getName())
        && attrJson.get(ConfigKeyType.KEY_CONTEXT.getName()).isJsonObject()) {
      return JsonUtils
          .toHashMap(attrJson.get(ConfigKeyType.KEY_CONTEXT.getName()).getAsJsonObject());
    }
    return null;
  }

  public String getJavaClasspath() {
    JsonObject attrJson = getAttributeJsonAsJsonObject();
    if (null != attrJson && attrJson.has(ConfigKeyType.KEY_JAVA_CLASSPATH.getName())
        && attrJson.get(ConfigKeyType.KEY_JAVA_CLASSPATH.getName()).isJsonPrimitive()) {
      return attrJson.get(ConfigKeyType.KEY_JAVA_CLASSPATH.getName()).getAsString();
    }
    return null;
  }
}
