package org.pangolincrawler.core.db;

import java.util.Date;

public class JobPoJo {

  public  enum JobStatus {
    NORMAL(0, "Normal"), PAUSED(1, "Paused");
    private int code;
    private String name;

    private JobStatus(int code, String name) {
      this.code = code;
      this.name = name;
    }

    public int getCode() {
      return code;
    }


    public String getName() {
      return name;
    }
  }

  private long id;

  private String jobKey;

  private String groupKey;

  private String cronExpression;

  private String processorKey;

  private String nextJobKey;

  private byte[] payloadJsonBytes;

  private String payloadJson;

  private String source;

  private Integer status = JobStatus.NORMAL.getCode();

  private String attributeJson;

  private Date createAt;

  private Date modifyAt;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getJobKey() {
    return jobKey;
  }

  public void setJobKey(String jobKey) {
    this.jobKey = jobKey;
  }

  public String getCronExpression() {
    return cronExpression;
  }

  public void setCronExpression(String cronExpression) {
    this.cronExpression = cronExpression;
  }

  public String getProcessorKey() {
    return processorKey;
  }

  public void setProcessorKey(String processorKey) {
    this.processorKey = processorKey;
  }

  public String getNextJobKey() {
    return nextJobKey;
  }

  public void setNextJobKey(String nextJobKey) {
    this.nextJobKey = nextJobKey;
  }

  public String getPayloadJson() {
    return payloadJson;
  }

  public void setPayloadJson(String payloadJson) {
    this.payloadJson = payloadJson;
  }

  public String getAttributeJson() {
    return attributeJson;
  }

  public void setAttributeJson(String attributeJson) {
    this.attributeJson = attributeJson;
  }

  @Override
  public String toString() {
    return "JobDO [id=" + id + ", jobKey=" + jobKey + ", cronExpression=" + cronExpression
        + ", processorKey=" + processorKey + ", nextJobKey=" + nextJobKey + ", payloadJson="
        + payloadJson + ", attributeJson=" + attributeJson + "]";
  }

  public String getGroupKey() {
    return groupKey;
  }

  public void setGroupKey(String groupKey) {
    this.groupKey = groupKey;
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

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public byte[] getPayloadJsonBytes() {
    return payloadJsonBytes;
  }

  public void setPayloadJsonBytes(byte[] payloadJsonBytes) {
    this.payloadJsonBytes = payloadJsonBytes;
  }

}
