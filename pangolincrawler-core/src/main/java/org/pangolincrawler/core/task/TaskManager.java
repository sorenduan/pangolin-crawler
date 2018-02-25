package org.pangolincrawler.core.task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.pangolincrawler.core.PangolinApplication;
import org.pangolincrawler.core.cache.CacheManager;
import org.pangolincrawler.core.constants.Constants;
import org.pangolincrawler.core.db.TaskDbService;
import org.pangolincrawler.core.db.TaskPoJo;
import org.pangolincrawler.core.job.TaskStatisticManager;
import org.pangolincrawler.core.processor.ProcessorManager;
import org.pangolincrawler.core.task.rate.RateManager;
import org.pangolincrawler.core.utils.LoggerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class TaskManager {

  public static class SendTaskResult {
    boolean success;
    String message;

    public SendTaskResult(boolean success, String message) {
      super();
      this.success = success;
      this.message = message;
    }

    public SendTaskResult(boolean success) {
      super();
      this.success = success;
    }

    public SendTaskResult() {
      super();
      this.success = true;
    }

    public boolean isSuccess() {
      return success;
    }

    public void setSuccess(boolean success) {
      this.success = success;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }
  }

  @Autowired
  private JmsMessagingTemplate jmsMessagingTemplate;

  @Autowired
  private TaskStatisticManager statisticService;

  @Autowired
  private RateManager rateManager;

  @Autowired
  private ProcessorManager processorManager;

  @Autowired
  private CacheManager cacheManager;

  @Autowired
  private TaskCacheManager taskCacheManager;

  @Autowired
  private TaskDbService taskDbService;

  public SendTaskResult sendTask(InnerTaskEntry task) {
    return this.sendTask(task, false);
  }

  public SendTaskResult sendTask(InnerTaskEntry task, boolean isRecent) {

    synchronized (this) {
      SendTaskResult taskRpeatCheckResult = taskCacheManager.checkTaskIsNotRepeat(task);
      if (!taskRpeatCheckResult.isSuccess()) {
        return taskRpeatCheckResult;
      }

      SendTaskResult taskCountCheckResult = taskCacheManager.checkTaskCountLimtitionIsOk(task);
      if (!taskCountCheckResult.isSuccess()) {
        return taskCountCheckResult;
      }

      this.taskCacheManager.markTaskForRepeatChecking(task);
    }

    if (!isRecent) {
      statisticService.markTaskCreated(task);
    }

    try {

      if (task.isCacheable()) {
        String cacheResult = cacheManager.getAsString(task.getCacheKey());
        if (null != cacheResult) {
          this.runTask(task, cacheResult);
          return new SendTaskResult(true, "Run From Cache");
        }
      }

      if (!task.hasRates()) {
        this.innerSendTask(task);
        return new SendTaskResult(true, "Send OK.");
      } else if (task.hasRates()) {
        if (rateManager.canSchedule(task)) {
          this.sendWithRate(task);
          return new SendTaskResult(true, "Send OK (With Rate).");
        } else {
          rateManager.waitForSchedule(task);
          statisticService.makringTaskWaiting(task);
          return new SendTaskResult(true, task.getTaskId() + " waiting for schedule.");
        }
      }
    } catch (Exception e) {
      this.taskCacheManager.clearCacheForRepeatCheck(task);
      LoggerUtils.error(this.getClass(), "Send task error.", e);
    }

    return new SendTaskResult(false, "Unkown Error.");

  }

  private void sendWithRate(InnerTaskEntry task) {
    rateManager.incrRate(task);
    this.innerSendTask(task);
  }

  /**
   * schedule waiting task locally, in 2 seconds.
   */
  @Scheduled(fixedDelay = 2000)
  public void trySchedulWaitTask() {

    List<Long> keysForDel = new ArrayList<>();
    Set<Entry<Long, InnerTaskEntry>> set = this.rateManager.getWaitingTaskPool().entrySet();
    long nowTs = System.currentTimeMillis();
    for (Entry<Long, InnerTaskEntry> each : set) {
      if (each.getKey() > nowTs) {
        break;
      }
      InnerTaskEntry eachTask = each.getValue();

      TaskPoJo taskPoJo = taskDbService.getOneByTaskId(eachTask.getTaskId());
      if (null != taskPoJo) {
        if (eachTask.isCacheable()) {
          String cacheResult = cacheManager.getAsString(eachTask.getCacheKey());
          if (null != cacheResult) {
            this.runTask(eachTask, cacheResult);
            keysForDel.add(each.getKey());
          }
        } else if (this.rateManager.canSchedule(eachTask)) {
          this.sendWithRate(each.getValue());
          keysForDel.add(each.getKey());
        }
      } else {
        keysForDel.add(each.getKey());
      }

    }
    this.rateManager.removeFromWaitingPool(keysForDel);
  }

  private void innerSendTask(InnerTaskEntry task) {
    try {
      this.jmsMessagingTemplate.convertAndSend(Constants.QUEUE_NAME_OF_TASK, task);
      taskCacheManager.increaseTaskCount(task);
    } catch (Exception e) {
      statisticService.makringTaskFail(task, e.getLocalizedMessage());
      LoggerUtils.error(this.getClass(), "Send task error.", e);
    }
  }

  public void snapshotTask(InnerTaskEntry task, String html) {
    try {
      String dirStr = PangolinApplication.getConfig().getTaskSnapshotDir();
      String snapshotDirStr = StringUtils.joinWith(File.separator, dirStr, task.getJobKey());
      File snapshotDir = new File(snapshotDirStr);
      if (!snapshotDir.exists() || !snapshotDir.isDirectory()) {
        snapshotDir.mkdirs();
      }

      String snapshotFilename = task.getTaskId() + ".snapshot";

      StringBuilder sb = new StringBuilder();

      sb.append(task.toString());
      sb.append("\n===============================================\n");
      sb.append(html);

      File snapshotFile = new File(snapshotDir, snapshotFilename);

      FileUtils.write(snapshotFile, sb.toString(), Constants.DEFAULT_CHARSET);
    } catch (IOException e) {
      LoggerUtils.error(this.getClass(), "snapshot task error", e);
    }
  }

  public void runTask(InnerTaskEntry task, String cacheResult) {
    try {

      TaskProcessorContainer processor = processorManager
          .createTaskProcessorContainerFromTaskEntry(task);
      if (null != cacheResult) {
        processor.setCacheable(true);
        processor.setResultCache(cacheResult);
      }
      processor.run();
    } catch (InstantiationException | IllegalAccessException e) {
      LoggerUtils.error(this.getClass(), "Run processor error.", e);
    }
  }

  public void runTask(InnerTaskEntry task) {
    runTask(task, null);
  }

}
