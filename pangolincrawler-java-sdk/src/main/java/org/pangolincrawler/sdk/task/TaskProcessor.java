package org.pangolincrawler.sdk.task;

import java.io.Serializable;

public abstract class TaskProcessor implements Serializable {

  private static final long serialVersionUID = 2142384788623763039L;

  private TaskInfo task;

  private String html;

  public abstract String process(String payload) throws TaskProcessorException;

  /**
   * @return the html
   */
  public String getHtml() {
    return html;
  }

  /**
   * @param html
   *          the html to set
   */
  public void setHtml(String html) {
    this.html = html;
  }

  /**
   * @return the info
   */
  public TaskInfo getTask() {
    return task;
  }

  /**
   * @param info
   *          the info to set
   */
  public void setTask(TaskInfo info) {
    this.task = info;
  }
}
