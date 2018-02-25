package org.pangolincrawler.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.pangolincrawler.core.task.CronTaskEntry;
import org.pangolincrawler.core.task.InnerTaskEntry;
import org.pangolincrawler.core.task.TaskProcessorContainer;
import org.pangolincrawler.sdk.task.TaskInfo;
import org.pangolincrawler.sdk.task.TaskProcessor;

public class TaskUtils {

  public static final String TASK_ID_SEPERATOR = ":";

  private static final Object lock = new Object();
  private static volatile Long counter = 1L;

  private static String generateTaskId(String prefix) {
    String id = UUID.randomUUID().toString();
    if (StringUtils.isNotBlank(prefix)) {
      id = StringUtils.joinWith(TASK_ID_SEPERATOR, prefix, id);
    }
    return id + TASK_ID_SEPERATOR + getNextCounter();
  }

  private TaskUtils() {
  }

  private static long getNextCounter() {
    synchronized (lock) {
      counter++;
    }
    return counter;
  }

  public static String generateTaskId() {
    return generateTaskId(null);
  }



  public static InnerTaskEntry taskInfoToTaskEntry(TaskInfo info,
      Class<? extends TaskProcessor> processorClass) {

    InnerTaskEntry entry = new InnerTaskEntry();

    entry.setUrl(info.getUrl());
    entry.setProcessorContext(info.getProcessorContext());
    entry.setPayload(info.getPayload());

    if (StringUtils.isNotBlank(info.getUrl())) {
      entry.setPreFetch(true);
    }

    return entry;
  }



  public static boolean isCronTask(InnerTaskEntry task) {
    if (null != task) {
      return task instanceof CronTaskEntry;
    }
    return false;
  }

  public static InnerTaskEntry cloneTaskEntry(InnerTaskEntry task) {
    InnerTaskEntry newTask = deepClone(task);
    cleanCloneTask(newTask);
    return newTask;
  }

  private static void cleanCloneTask(InnerTaskEntry task) {
    if (null != task) {
      task.setTaskId(TaskUtils.generateTaskId());
      task.setParentTaskId(null);
      task.setParentJobKey(null);
      task.getTaskContext().clear();
      if (null != task.getNextTask()) {
        cleanCloneTask(task.getNextTask());
      }
    }
  }

  private static InnerTaskEntry deepClone(InnerTaskEntry target) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(target);
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      ObjectInputStream ois = new ObjectInputStream(bais);
      return (InnerTaskEntry) ois.readObject();
    } catch (Exception e) {
      LoggerUtils.error(TaskUtils.class, "Clone TaskEntry error", e);
      return null;
    }
  }

}
