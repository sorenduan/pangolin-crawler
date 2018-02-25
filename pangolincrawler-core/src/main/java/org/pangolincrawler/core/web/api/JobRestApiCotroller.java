package org.pangolincrawler.core.web.api;

import java.util.List;

import org.pangolincrawler.core.constants.Constants;
import org.pangolincrawler.core.db.JobPoJo;
import org.pangolincrawler.core.job.ConfigValidaionResult;
import org.pangolincrawler.core.job.JobConfig;
import org.pangolincrawler.core.job.JobManager;
import org.pangolincrawler.core.task.JobValidationException;
import org.pangolincrawler.core.task.InnerTaskEntry;
import org.pangolincrawler.core.utils.JsonUtils;
import org.pangolincrawler.core.utils.LoggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/job")
public class JobRestApiCotroller extends BaseRestController {

  protected static Logger logger = LoggerFactory.getLogger(JobRestApiCotroller.class);

  @Autowired
  private JobManager jobManager;

  @PostMapping(value = "/_test", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ApiResponseEntity runTestJob(@RequestBody String json) {

    ApiResponseEntity r1 = this.validateJobConfig(json);
    if (r1.isSuccess()) {
      JobConfig job = JobConfig.buildFromJsonString(json);
      InnerTaskEntry task = this.jobManager.runJobLocally(job);
      String jsonResult = JsonUtils.toJson(task.getTraceContext());
      return ApiResponseEntity.build(true, jsonResult);
    } else {
      return r1;
    }
  }

  @DeleteMapping(value = "/{jobKey}")
  public ApiResponseEntity deleteJob(
      @PathVariable(name = "jobKey", required = true) String jobKey) {

    JobPoJo t = this.jobManager.getJobByJobKey(jobKey);

    if (null == t) {
      return ApiResponseEntity.build(false, "The Job of '" + jobKey + "' is not existed.");
    }
    this.jobManager.deleteJobByJobKey(jobKey);
    return ApiResponseEntity.build(true, "Delete the Job of '" + jobKey + "' success.");

  }

  @GetMapping(value = "/_list/{offset}")
  public ApiResponseEntity jobList(@PathVariable(name = "offset", required = false) int offset) {

    int pageSize = Constants.MAX_PAGE_SIZE;
    if (offset < 0) {
      offset = 0;
    }

    List<JobPoJo> list = jobManager.getJobList(null, offset, pageSize);

    return ApiResponseEntity.build(true, JsonUtils.toJson(list));

  }

  @GetMapping(value = "/_list")
  public ApiResponseEntity jobList() {
    return this.jobList(0);
  }

  @PostMapping
  public ApiResponseEntity register(@RequestBody String jobString) {

    JobConfig job = JobConfig.buildFromJsonString(jobString);

    try {
      JobPoJo jobDo = this.jobManager.registerWithJobConfig(job);
      return ApiResponseEntity.build(true, JsonUtils.toJson(jobDo));
    } catch (JobValidationException e) {
      throw new ApiException(ApiResponseCodeEnum.JOB_IS_INVALID,
          "Register Job Error : " + e.getMessage());
    } catch (Exception e) {
      LoggerUtils.error(this.getClass(), "Register Job Error.", e);
      throw new ApiException(ApiResponseCodeEnum.UNKOWN_ERROR,
          "Register Job Error : " + e.getMessage());
    }
  }

  @PutMapping
  public ApiResponseEntity update(@RequestBody String jobString) {

    JobConfig job = JobConfig.buildFromJsonString(jobString);
    try {
      JobPoJo jobDo = this.jobManager.updateWithJobConfig(job);
      return ApiResponseEntity.build(true, JsonUtils.toJson(jobDo));
    } catch (JobValidationException e) {
      throw new ApiException(ApiResponseCodeEnum.JOB_IS_INVALID,
          "Update Job Error : " + e.getLocalizedMessage());
    } catch (Exception e) {
      LoggerUtils.error(this.getClass(), "Register Job Error.", e);
      throw new ApiException(ApiResponseCodeEnum.UNKOWN_ERROR,
          "Update Job Error : " + e.getLocalizedMessage());
    }
  }

  @GetMapping(value = "/{jobKey}/_list")
  public ApiResponseEntity listJobByJobKey(
      @PathVariable(name = "jobKey", required = true) String jobKey) {
    List<JobPoJo> list = this.jobManager.getJobList(jobKey, 0, Constants.MAX_PAGE_SIZE);
    return ApiResponseEntity.build(true, JsonUtils.toJson(list));
  }

  @PutMapping(value = "/{jobKey}/{action}")
  public ApiResponseEntity updateOneJobWithAction(
      @PathVariable(name = "jobKey", required = true) String jobKey,
      @PathVariable(name = "action", required = true) String action) {
    boolean temporary = super.hasTheQueryParam("temporary");
    String returnText = "";

    switch (action) {
    case "_pause":
      if (this.jobManager.pauseCronJob(jobKey, temporary)) {
        returnText = "Success";
      } else {
        returnText = "Fail";
      }
      break;
    case "_resume":
      if (this.jobManager.resumeCronJob(jobKey, temporary)) {
        returnText = "Success";
      } else {
        returnText = "Fail";
      }
      break;
    case "_start":
      try {
        String taskId = this.jobManager.tiggerJob(jobKey);
        returnText += "Success, task id is '" + taskId + "'";
      } catch (Exception e) {
        LoggerUtils.error(this.getClass(), "Start the job of {" + jobKey + "} error", e);
        returnText += "Fail, " + e.getLocalizedMessage();
      }
      break;
    case "_disable":
      break;
    case "_enable":
      break;
    default:
      break;
    }

    return ApiResponseEntity.build(true, returnText);
  }

  @PostMapping(value = "/_validate", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ApiResponseEntity validateJobConfig(@RequestBody String jobString) {

    ConfigValidaionResult result = this.jobManager.validateJobConfig(jobString);
    if (result.isSuccess()) {
      return ApiResponseEntity.build(result.isSuccess(), ApiResponseCodeEnum.SUCCESS,
          result.getMessage(), "Success");
    }
    return ApiResponseEntity.build(result.isSuccess(), ApiResponseCodeEnum.JOB_IS_INVALID,
        result.getMessage());
  }
}
