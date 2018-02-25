package org.pangolincrawler.core.task;

import org.pangolincrawler.sdk.task.TaskProcessor;

public class CronTaskEntry extends InnerTaskEntry {

  private String cronExpression;

  private static final long serialVersionUID = 6080133774355703843L;

  public CronTaskEntry() {
  }

  public CronTaskEntry(String cronExpression) {
    this.cronExpression = cronExpression;
  }

  public CronTaskEntry(String taskKey, Class<? extends TaskProcessor> processorClass) {
    super(taskKey, processorClass);
  }

  /**
   * @return the cronExpression
   */
  public String getCronExpression() {
    return cronExpression;
  }

  /**
   * @param cronExpression
   *          the cronExpression to set
   */
  public void setCronExpression(String cronExpression) {
    this.cronExpression = cronExpression;
  }

}
