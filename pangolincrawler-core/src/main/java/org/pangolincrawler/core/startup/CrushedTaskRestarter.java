package org.pangolincrawler.core.startup;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.pangolincrawler.core.PangolinApplication;
import org.pangolincrawler.core.config.SystemInfo;
import org.pangolincrawler.core.db.TaskDbService;
import org.pangolincrawler.core.db.TaskPoJo;
import org.pangolincrawler.core.task.TaskCacheManager;
import org.pangolincrawler.core.task.InnerTaskEntry;
import org.pangolincrawler.core.task.TaskManager;
import org.pangolincrawler.core.utils.LoggerUtils;
import org.pangolincrawler.core.utils.TimeUtils;

public class CrushedTaskRestarter implements Runnable {

  private static Thread runingThread = null;

  @Override
  public void run() {
    TaskDbService taskDb = PangolinApplication.getSystemService(TaskDbService.class);
    TaskManager taskManager = PangolinApplication.getSystemService(TaskManager.class);
    TaskCacheManager taskCacheManager = PangolinApplication
        .getSystemService(TaskCacheManager.class);

    LocalDateTime startAt = TimeUtils.timestampToLocalDateTime(PangolinApplication.getBootAt());

    String hostname = SystemInfo.getHostname();

    if (StringUtils.isBlank(hostname)) {
      LoggerUtils.warn("Host name is blank!", this.getClass());
      return;
    }

    taskDb.updateCrushedTaskList(hostname, startAt);
    int count = taskDb.countTasksWithHost(hostname, TaskPoJo.TaskStatus.CRUSHED);

    LoggerUtils.info(count + " crushed task need to resent.", this.getClass());

    if (count > 0) {
      while (true) {
        InnerTaskEntry task = taskDb.getOneCrushedTask(hostname, startAt);
        if (null == task) {
          break;
        } else {
          LoggerUtils.info("Resent task '" + task.getTaskId() + "'", this.getClass());
          taskCacheManager.clearCacheForRepeatCheck(task);
          taskManager.sendTask(task, true);
        }
      }
    }

  }

  public static void start() {
    runingThread = new Thread(new CrushedTaskRestarter());
    runingThread.start();
  }

  public static void stop() {
    if (null != runingThread) {
      runingThread.interrupt();
    }
  }
}
