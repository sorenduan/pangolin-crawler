package org.pangolincrawler.core.utils;

import com.google.gson.JsonObject;

import org.pangolincrawler.core.db.JobPoJo;
import org.pangolincrawler.core.job.JobConfig;

public final class JobUtils {

  private JobUtils() {
  }

  public static JobPoJo convertFromTpl(JobConfig config) {
    JobPoJo job = new JobPoJo();

    job.setJobKey(config.getJobKey());
    job.setCronExpression(config.getCronExpression());
    job.setProcessorKey(config.getProcessorKey());
    job.setPayloadJson(config.getPayloadAsJson());
    job.setSource(config.getSource().toString());

    JsonObject json = JsonUtils.toJsonObject(config.getOptions());

    job.setAttributeJson(json.toString());

    return job;
  }

}
