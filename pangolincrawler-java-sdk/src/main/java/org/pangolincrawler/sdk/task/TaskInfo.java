package org.pangolincrawler.sdk.task;

import java.io.Serializable;
import java.util.Map;

public class TaskInfo implements Serializable {

  private static final long serialVersionUID = -4151908796804969263L;

  private String payload;

  private String taskId;

  private boolean isPreFetch;

  private String url;

  private String jobKey;

  private String processorKey;

  private String parentJobKey;

  private Map<String, Object> processorContext;

  public TaskInfo() {
  }

  /**
   * @return the payload
   */
  public String getPayload() {
    return payload;
  }

  /**
   * @param payload
   *          the payload to set
   */
  public void setPayload(String payload) {
    this.payload = payload;
  }

  /**
   * @return the taskId
   */
  public String getTaskId() {
    return taskId;
  }

  /**
   * @param taskId
   *          the taskId to set
   */
  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  /**
   * @return the isPreFetch
   */
  public boolean isPreFetch() {
    return isPreFetch;
  }

  /**
   * @param isPreFetch
   *          the isPreFetch to set
   */
  public void setPreFetch(boolean isPreFetch) {
    this.isPreFetch = isPreFetch;
  }

  /**
   * @return the url
   */
  public String getUrl() {
    return url;
  }

  /**
   * @param url
   *          the url to set
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * @return the parentJobKey
   */
  public String getParentJobKey() {
    return parentJobKey;
  }

  /**
   * @param parentJobKey
   *          the parentJobKey to set
   */
  public void setParentJobKey(String parentJobKey) {
    this.parentJobKey = parentJobKey;
  }

  /**
   * @return the processorContext
   */
  public Map<String, Object> getProcessorContext() {
    return processorContext;
  }

  /**
   * @param processorContext
   *          the processorContext to set
   */
  public void setProcessorContext(Map<String, Object> processorContext) {
    this.processorContext = processorContext;
  }

  public String getJobKey() {
    return jobKey;
  }

  public void setJobKey(String jobKey) {
    this.jobKey = jobKey;
  }

  public String getProcessorKey() {
    return processorKey;
  }

  public void setProcessorKey(String processorKey) {
    this.processorKey = processorKey;
  }

}
