package org.pangolincrawler.core.task;

import org.pangolincrawler.core.PangolinApplication;
import org.pangolincrawler.core.utils.TaskUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CronTaskJobContainer implements Job {

  public static final String CONTEXT_KEY_TASK_ENTRY = "task_entry";

  public void execute(JobExecutionContext context) throws JobExecutionException {

    CronTaskEntry task = (CronTaskEntry) context.getJobDetail().getJobDataMap()
        .get(CONTEXT_KEY_TASK_ENTRY);

    TaskManager taskManagerService = PangolinApplication
        .getSystemService(TaskManager.class);
    //create a new task id
    task.setTaskId(TaskUtils.generateTaskId());
    taskManagerService.sendTask(task);

  }

}
