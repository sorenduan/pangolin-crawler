package org.pangolincrawler.core.task;

import java.io.Serializable;

public class TaskCountOptions implements Serializable {

  private static final long serialVersionUID = 1L;

  private int oneDayLimition;

  /**
   * @return the oneDayLimition
   */
  public int getOneDayLimition() {
    return oneDayLimition;
  }

  /**
   * @param oneDayLimition
   *          the oneDayLimition to set
   */
  public void setOneDayLimition(int oneDayLimition) {
    this.oneDayLimition = oneDayLimition;
  }

}
