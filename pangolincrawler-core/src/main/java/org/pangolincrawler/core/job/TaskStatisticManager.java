package org.pangolincrawler.core.job;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.pangolincrawler.core.config.SystemInfo;
import org.pangolincrawler.core.db.TaskDbService;
import org.pangolincrawler.core.db.TaskPoJo;
import org.pangolincrawler.core.task.InnerTaskEntry;
import org.pangolincrawler.core.utils.LoggerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskStatisticManager {


  @Autowired
  private TaskDbService taskDbService;

  @PostConstruct
  public void init() {
    this.taskDbService.createTaskTable();
  }

  public void markTaskCreated(InnerTaskEntry task) {
    TaskPoJo pojo = convertTaskEntryToTaskPoJo(task);

    if (null != task) {
      this.taskDbService.createJob(pojo);
    }
  }

  private String buildCommconEtraMessage(InnerTaskEntry task) {
    StringBuilder sb = new StringBuilder();
    if (StringUtils.isNotBlank(task.getParentTaskId())) {
      sb.append("Parent task id : ");
      sb.append(task.getParentTaskId());
      sb.append("\n");
    }
    return sb.toString();
  }

  public void markTaskRunning(InnerTaskEntry task) {
    String message = buildCommconEtraMessage(task) + "Run at " + SystemInfo.getHostname() + ". ";
    this.taskDbService.updateTaskStatus(task.getTaskId(), TaskPoJo.TaskStatus.RUNNING, message);
  }

  public void markTaskCrushed(InnerTaskEntry task) {
    String message = "Task is crushed. ";
    this.taskDbService.updateTaskStatus(task.getTaskId(), TaskPoJo.TaskStatus.CRUSHED, message);
  }

  public void makringTaskFinised(InnerTaskEntry task) {
    this.taskDbService.updateTaskStatus(task.getTaskId(), TaskPoJo.TaskStatus.FININSHED, null);
  }

  public void makringTaskFail(InnerTaskEntry task, String failMessage) {
    String message = "Fail at " + SystemInfo.getHostname() + ". Cause by " + failMessage;
    this.taskDbService.updateTaskStatus(task.getTaskId(), TaskPoJo.TaskStatus.FAIL, message);
  }

  public void makringTaskWaiting(InnerTaskEntry task) {
    this.taskDbService.updateTaskStatus(task.getTaskId(), TaskPoJo.TaskStatus.WAITING, null);
  }

  public void makringTaskStatus(InnerTaskEntry task, TaskPoJo.TaskStatus status) {
    this.taskDbService.updateTaskStatus(task.getTaskId(), status, "");
  }

  private TaskPoJo convertTaskEntryToTaskPoJo(InnerTaskEntry task) {
    if (null == task) {
      return null;
    }

    byte[] bytes = SerializationUtils.serialize(task);

    TaskPoJo pojo = new TaskPoJo();

    pojo.setJobKey(task.getJobKey());
    pojo.setTaskId(task.getTaskId());
    pojo.setUrl(task.getUrl());
    pojo.setTaskObj(bytes);

    return pojo;
  }

  public synchronized void report() {
    LoggerUtils.info("", this.getClass());
  }
}
