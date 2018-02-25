package org.pangolincrawler.sdk.model;

public final class Job extends BaseModel {

  private String jobKey;
  private String crondExpression;

  public String getJobKey() {
    return jobKey;
  }

  public void setJobKey(String jobKey) {
    this.jobKey = jobKey;
  }

  public String getCrondExpression() {
    return crondExpression;
  }

  public void setCrondExpression(String crondExpression) {
    this.crondExpression = crondExpression;
  }

}
