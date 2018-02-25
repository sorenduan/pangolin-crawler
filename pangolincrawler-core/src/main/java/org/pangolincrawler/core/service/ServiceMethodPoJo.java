package org.pangolincrawler.core.service;

import java.util.Date;

public class ServiceMethodPoJo {

  private long id;

  private String serviceName;

  private String methodName;

  private String version;

  private String description;
  private String inputDescription;
  private String outputDescription;

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

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
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

  public String getInputDescription() {
    return inputDescription;
  }

  public void setInputDescription(String inputDescription) {
    this.inputDescription = inputDescription;
  }

  public String getOutputDescription() {
    return outputDescription;
  }

  public void setOutputDescription(String outputDescription) {
    this.outputDescription = outputDescription;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

}
