package org.pangolincrawler.core.db;

import java.util.Date;

public class TaskPoJo {

  public enum TaskStatus {
    NORMAL(0, "Normal"), WAITING(1, "Waiting"), RUNNING(2, "Running"), FININSHED(3,
        "Finished"), FAIL(10, "Fail"), CANCELLING(11,
            "Cancelling"), CANCELLED(12, "Cancelled"), CRUSHED(13, "Crushed");
    private int code;
    private String name;

    private TaskStatus(int code, String name) {
      this.code = code;
      this.name = name;
    }

    public int getCode() {
      return code;
    }

    public String getName() {
      return name;
    }

    public static TaskStatus fromCode(int code) {
      TaskStatus[] statusArr = TaskStatus.values();
      for (TaskStatus each : statusArr) {
        if (each.getCode() == code) {
          return each;
        }
      }
      return null;
    }
  }

  private long id;

  private String jobKey;

  private String taskId;

  private String url;

  /**
   * current task status.
   */
  private Integer status;

  private String extraMessage;

  /**
   * where the task run at.
   */
  private String host;

  private Date runAt;

  private Date finishAt;

  private Date createAt;

  private Date modifyAt;

  private byte[] taskObj;

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

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Integer getStatus() {
    if (null == status) {
      return TaskStatus.NORMAL.getCode();
    }
    return status;
  }

  public TaskStatus getStatusAsType() {
    if (null == status) {
      return TaskStatus.NORMAL;
    }
    return TaskStatus.fromCode(this.status);
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Date getRunAt() {
    return runAt;
  }

  public void setRunAt(Date runAt) {
    this.runAt = runAt;
  }

  public Date getFinishAt() {
    return finishAt;
  }

  public void setFinishAt(Date finishAt) {
    this.finishAt = finishAt;
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

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public String getExtraMessage() {
    return extraMessage;
  }

  public void setExtraMessage(String extraMessage) {
    this.extraMessage = extraMessage;
  }

  public byte[] getTaskObj() {
    return taskObj;
  }

  public void setTaskObj(byte[] taskObj) {
    this.taskObj = taskObj;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

}
