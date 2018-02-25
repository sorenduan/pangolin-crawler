package org.pangolincrawler.core.service;

import java.util.Date;

import org.apache.commons.codec.binary.StringUtils;

public class ServicePoJo {

  public enum ServiceType {
    JAVA("java"), GROOVY("groovy"), SCRIPT("script");
    private String name;

    private ServiceType(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }


    public static ServicePoJo.ServiceType fromName(String name) {
      ServicePoJo.ServiceType[] typeArr = ServicePoJo.ServiceType.values();
      for (ServicePoJo.ServiceType each : typeArr) {
        if (StringUtils.equals(each.getName(), name)) {
          return each;
        }
      }
      return null;
    }
  }

  private long id;

  private String serviceName;

  private String source;

  private String version;

  private String type;

  private String attributeJson;

  private String description;

  private Date createAt;

  private Date modifyAt;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getAttributeJson() {
    return attributeJson;
  }

  public void setAttributeJson(String attributeJson) {
    this.attributeJson = attributeJson;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

}
