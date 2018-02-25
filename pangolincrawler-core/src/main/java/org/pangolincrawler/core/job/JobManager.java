package org.pangolincrawler.core.job;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.pangolincrawler.core.constants.Constants;
import org.pangolincrawler.core.db.JobDbService;
import org.pangolincrawler.core.db.JobPoJo;
import org.pangolincrawler.core.plugin.ConfigKeyType;
import org.pangolincrawler.core.processor.ProcessorConfig;
import org.pangolincrawler.core.processor.ProcessorManager;
import org.pangolincrawler.core.processor.ProcessorPoJo;
import org.pangolincrawler.core.task.CronTaskEntry;
import org.pangolincrawler.core.task.CronTaskJobContainer;
import org.pangolincrawler.core.task.InnerTaskEntry;
import org.pangolincrawler.core.task.JobValidationException;
import org.pangolincrawler.core.task.TaskManager;
import org.pangolincrawler.core.task.rate.RateExpressions;
import org.pangolincrawler.core.utils.JobUtils;
import org.pangolincrawler.core.utils.JsonUtils;
import org.pangolincrawler.core.utils.LoggerUtils;
import org.pangolincrawler.core.utils.ProcessorUtils;
import org.pangolincrawler.core.utils.TaskUtils;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class JobManager {

  protected static Logger logger = LoggerFactory.getLogger(JobManager.class);

  // @NonNull
  // @Valid
  private Map<String, JobPoJo> jobsTable = new ConcurrentHashMap<>();

  @Autowired
  @Qualifier("cluster-scheduler")
  private Scheduler cronScheduler;

  @Autowired
  private ProcessorManager processorManager;

  @Autowired
  private JobDbService dbmsService;

  @Autowired
  private TaskManager taskManager;

  @PostConstruct
  public void init() {
    dbmsService.createJobTable();
    this.loadCronJob();
  }

  private void loadCronJob() {
    int jobCount = dbmsService.getCronJobCount();
    int pageSize = Constants.MAX_PAGE_SIZE;
    int pageCount = jobCount / pageSize + ((0 == jobCount % pageSize) ? 0 : 1);
    for (int i = 0; i < pageCount; i++) {
      List<JobPoJo> jobs = dbmsService.getCronJobList(i * pageSize, pageSize);
      jobs.stream().filter(j -> StringUtils.isNoneBlank(j.getCronExpression()))
          .map(this::createTaskEntryFromJobPoJo)
          .filter(eachTask -> eachTask instanceof CronTaskEntry)
          .forEach(eachTask -> this.scheduleCronJob((CronTaskEntry) eachTask));
    }
  }

  public boolean pauseCronJob(String jobKey, boolean temporary) {
    if (!temporary) {
      this.dbmsService.updateJobStatus(jobKey, JobPoJo.JobStatus.PAUSED);
    }
    return this.pauseCronJobIfExisted(jobKey);
  }

  public boolean resumeCronJob(String jobKey, boolean temporary) {
    if (!temporary) {
      this.dbmsService.updateJobStatus(jobKey, JobPoJo.JobStatus.NORMAL);
    }

    if (isCronJobExisted(jobKey)) {
      return this.resumeIfExisted(jobKey);
    } else {
      JobPoJo job = dbmsService.getOneByJobKey(jobKey);
      List<InnerTaskEntry> cronTask = createTaskEntryFromJobPoJo(job);
      if (cronTask instanceof CronTaskEntry) {
        this.scheduleCronJob((CronTaskEntry) cronTask);
      }
    }
    return true;
  }

  public boolean unregisterJobBySource(String source) {
    List<JobPoJo> jobs = this.dbmsService.listJobBySource(source);

    if (CollectionUtils.isNotEmpty(jobs)) {
      jobs.forEach(j -> this.pauseCronJob(j.getJobKey(), true));
      return this.dbmsService.deleteBySource(source);
    }

    return true;
  }

  public JobPoJo registerJobIfNotExisted(JobPoJo job) {
    if (!this.jobKeyIsExisted(job.getJobKey())) {
      return this.registerJob(job);
    }
    return null;
  }

  private boolean jobKeyIsExisted(String jobKey) {
    return null != this.dbmsService.getOneByJobKey(jobKey);
  }

  public JobPoJo registerJob(JobPoJo job) {
    JobPoJo newJob = null;

    if (null != dbmsService.getOneByJobKey(job.getJobKey())) {
      throw new JobValidationException("Job key '" + job.getJobKey()
          + "' is existed, please check whether the job key is duplicated.");
    }

    this.checkJobValidation(job);
    newJob = dbmsService.createJob(job);

    List<InnerTaskEntry> tasks = this.createTaskEntryFromJobPoJo(newJob);
    if (CollectionUtils.isNotEmpty(tasks)) {
      for (InnerTaskEntry eachTask : tasks) {
        if (StringUtils.isNoneBlank(newJob.getCronExpression())
            && eachTask instanceof CronTaskEntry) {
          this.scheduleCronJob((CronTaskEntry) eachTask);
        }
      }
    }

    cacheJob(newJob);
    return newJob;
  }

  public String tiggerJob(String jobkey) {
    JobPoJo job = dbmsService.getOneByJobKey(jobkey);

    if (null == job) {
      throw new JobValidationException("Job key (" + jobkey + ") is not existed.");
    }

    List<InnerTaskEntry> tasks = this.createTaskEntryFromJobPoJo(job);

    StringJoiner joiner = new StringJoiner(",");

    for (InnerTaskEntry task : tasks) {
      TaskManager.SendTaskResult r = this.taskManager.sendTask(task);
      if (r.isSuccess()) {
        joiner.add(task.getTaskId());
      } else {
        joiner.add(r.getMessage());
      }
    }
    return joiner.toString();
  }

  public JobPoJo registerWithJobConfig(JobConfig jobConfig) {

    ConfigValidaionResult v = this.validateJobConfig(jobConfig);
    if (v.isFail()) {
      throw new JobValidationException(v.getMessage());
    }
    JobPoJo job = JobUtils.convertFromTpl(jobConfig);

    if (null != job) {
      return this.registerJob(job);
    }
    return null;
  }

  public void deleteJobByJobKey(String jobKey) {
    this.dbmsService.deleteByJobKey(jobKey);
    this.cancelIfExisted(jobKey);
  }

  public JobPoJo getJobByJobKey(String jobKey) {
    return this.dbmsService.getOneByJobKey(jobKey);
  }

  public JobPoJo updateWithJobConfig(JobConfig jobConfig) {

    ConfigValidaionResult v = this.validateJobConfig(jobConfig);
    if (v.isFail()) {
      throw new JobValidationException(v.getMessage());
    }

    JobPoJo job = JobUtils.convertFromTpl(jobConfig);
    JobPoJo newJob = null;

    if (null == dbmsService.getOneByJobKey(job.getJobKey())) {
      throw new JobValidationException("Job key is not existed.");
    }

    this.checkJobValidation(job);
    newJob = dbmsService.update(job);

    List<InnerTaskEntry> tasks = this.createTaskEntryFromJobPoJo(newJob);

    // restart cron task
    for (InnerTaskEntry task : tasks) {
      if (TaskUtils.isCronTask(task)) {
        this.cancelIfExisted(newJob.getJobKey());
        this.scheduleCronJob((CronTaskEntry) task);
      }
    }

    cacheJob(newJob);
    return newJob;
  }

  /**
   * only one task can run , it is usually used for testing
   */
  public InnerTaskEntry runJobLocally(JobConfig jobConfig) {
    List<InnerTaskEntry> tasks = createTaskCronJobConfig(jobConfig);
    if (CollectionUtils.isNotEmpty(tasks)) {
      setTaskEntryRecursivelyForTest(tasks.get(0));
      taskManager.runTask(tasks.get(0));
      return tasks.get(0);
    }
    return null;
  }

  public void setTaskEntryRecursivelyForTest(InnerTaskEntry task) {
    task.setCacheable(false);
    task.setDistributed(false);

    task.setTraceable(true);
    task.setRunForTest(true);

    if (null != task.getNextTask()) {
      setTaskEntryRecursivelyForTest(task.getNextTask());
    }
  }

  public List<InnerTaskEntry> createTaskCronJobConfig(JobConfig jobConfig) {
    JobPoJo pojo = JobUtils.convertFromTpl(jobConfig);
    return this.createTaskEntryFromJobPoJo(pojo);
  }

  public List<JobPoJo> getJobList(String jobKey, int offset, int pageSize) {
    int pageSizeFixed = pageSize;

    if (pageSize > Constants.MAX_PAGE_SIZE) {
      LoggerUtils.warn(JobManager.class,
          "The page size must be less than {}, and the current value is {}",
          Constants.MAX_PAGE_SIZE, pageSize);
      pageSizeFixed = Constants.MAX_PAGE_SIZE;
    }

    return dbmsService.listJobs(jobKey, offset, pageSizeFixed);
  }

  private Map<String, Object> getProcessorContext(JobPoJo job) {
    ProcessorPoJo processor = null;
    if (StringUtils.isNotBlank(job.getProcessorKey())) {
      processor = this.processorManager.getProcessorByKey(job.getProcessorKey());
    } else {
      JsonObject json = JsonUtils.toJsonObject(job.getAttributeJson());
      if (null != json) {
        JsonObject processorConfigJson = JsonUtils.getJsonObject(json,
            ConfigKeyType.KEY_PROCESSOR_CONFIG.getName());
        ProcessorConfig processorConfig = ProcessorConfig.buildFromJsonObject(processorConfigJson);
        processor = ProcessorUtils.createProcessorPoJoFromConfig(processorConfig);
      }
    }

    if (null != processor) {
      return processor.getContext();
    }
    return null;
  }

  private List<InnerTaskEntry> createTaskEntryFromJobPoJo(JobPoJo job) {
    InnerTaskEntry task = null;
    if (StringUtils.isNoneBlank(job.getCronExpression())) {
      task = new CronTaskEntry(job.getCronExpression());

    } else {
      task = new InnerTaskEntry();
    }
    task.setJobKey(job.getJobKey());
    task.setPayload(job.getPayloadJson());

    task.setProcessorKey(job.getProcessorKey());
    // task.setProcessorClass(createProcessorClass(job));
    task.setProcessorContext(this.getProcessorContext(job));

    List<String> urlList = new ArrayList<>();
    String url = null;

    JsonObject json = JsonUtils.toJsonObject(job.getAttributeJson());
    if (null != json) {
      task.setOptions((HashMap<String, Object>) JsonUtils.toHashMap(json));
      fillAttrs(json, task);

      if (json.has(ConfigKeyType.KEY_URLS.getName())
          && json.get(ConfigKeyType.KEY_URLS.getName()).isJsonArray()) {
        urlList.addAll(JsonUtils
            .jsonArrayToStringList(json.get(ConfigKeyType.KEY_URLS.getName()).getAsJsonArray()));
      }

      url = JsonUtils.getString(json, ConfigKeyType.KEY_URL.getName());
      if (StringUtils.isNotBlank(url)) {
        urlList.add(url);
      }
    }

    // split the task according to the number of URL
    List<InnerTaskEntry> taskList = new ArrayList<>();

    if (CollectionUtils.isNotEmpty(urlList)) {
      for (String eachUrl : urlList) {
        InnerTaskEntry newTask = TaskUtils.cloneTaskEntry(task);
        newTask.setUrl(eachUrl);
        taskList.add(newTask);
      }
    } else {
      taskList.add(task);
    }

    task.setUrl(url);

    return taskList;
  }

  private void fillAttrs(JsonObject json, InnerTaskEntry task) {

    JsonObject loopJson = JsonUtils.getJsonObject(json, ConfigKeyType.KEY_LOOP.getName());
    if (null != loopJson) {
      LoopOptions loopOpts = new LoopOptions(loopJson);
      task.setLoop(loopOpts);
    }

    if (json.has(ConfigKeyType.KEY_TASK_SNAPSHOT_ENABLE.getName())
        && json.get(ConfigKeyType.KEY_TASK_SNAPSHOT_ENABLE.getName()).isJsonPrimitive()) {
      task.setTaskSnapshotEnable(
          json.get(ConfigKeyType.KEY_TASK_SNAPSHOT_ENABLE.getName()).getAsBoolean());
    }

    if (json.has(ConfigKeyType.KEY_REQEUST_RATE.getName())
        && json.get(ConfigKeyType.KEY_REQEUST_RATE.getName()).isJsonObject()) {
      RateExpressions rates = RateExpressions.buildFromJsonObject(
          json.get(ConfigKeyType.KEY_REQEUST_RATE.getName()).getAsJsonObject());
      task.setRates(rates);
    }

    if (json.has(ConfigKeyType.KEY_NEXT_JOB_CONFIG.getName())
        && json.get(ConfigKeyType.KEY_NEXT_JOB_CONFIG.getName()).isJsonObject()) {
      JobConfig nextJobConfig = JobConfig.buildFromJsonObject(
          json.get(ConfigKeyType.KEY_NEXT_JOB_CONFIG.getName()).getAsJsonObject());
      // not support multiple next tasks.
      List<InnerTaskEntry> nextTasks = this.createTaskCronJobConfig(nextJobConfig);
      task.setNextTask(nextTasks.get(0));
    }

    if (json.has(ConfigKeyType.KEY_NEXT_JOB_KEY.getName())
        && json.get(ConfigKeyType.KEY_NEXT_JOB_KEY.getName()).isJsonPrimitive()) {
      String nextJobkey = json.get(ConfigKeyType.KEY_NEXT_JOB_KEY.getName()).getAsString();
      // not support multiple next tasks.
      JobPoJo nextJobPojo = this.getJobByJobKey(nextJobkey);
      List<InnerTaskEntry> nextTasks = this.createTaskEntryFromJobPoJo(nextJobPojo);
      task.setNextTask(nextTasks.get(0));
    }
  }

  private void cacheJob(JobPoJo job) {
    jobsTable.put(job.getJobKey(), job);
  }

  private ConfigValidaionResult validateFromJsonObject(JsonObject json) {
    ConfigBaseValidator v1 = new JobConfigJsonFormatValidator(json);
    ConfigValidaionResult r1 = v1.validate();
    if (r1.isFail()) {
      return r1;
    }
    JobConfig config = JobConfig.buildFromJsonObject(json);

    ConfigBaseValidator v2 = new JobConfigFormatValidator(config);
    ConfigValidaionResult r2 = v2.validate();

    if (!this.processorManager.isProcessorKeyExisted(config.getProcessorKey())) {
      return ConfigValidaionResult
          .fail("The processer key '" + config.getProcessorKey() + "' is not registed.");
    }

    return r2;
  }

  public ConfigValidaionResult validateJobConfig(String jobConfigJson) {
    JsonElement elem = JsonUtils.toJsonElement(jobConfigJson);

    if (elem.isJsonArray()) {
      JsonArray jobArr = elem.getAsJsonArray();
      int len = jobArr.size();

      for (int i = 0; i < len; i++) {
        ConfigValidaionResult eachResult = validateFromJsonObject(jobArr.get(i).getAsJsonObject());
        if (eachResult.isFail()) {
          return eachResult;
        }
      }
    } else if (elem.isJsonObject()) {
      return validateFromJsonObject(elem.getAsJsonObject());
    }

    return ConfigValidaionResult.fail("The input string is not a valid json format.");
  }

  public ConfigValidaionResult validateJobConfig(JobConfig jobConfig) {
    JobConfigFormatValidator validator = new JobConfigFormatValidator(jobConfig);
    ConfigValidaionResult result = validator.validate();
    if (result.isFail()) {
      return result;
    }

    String url = jobConfig.getOptionAsString(ConfigKeyType.KEY_URL);
    List<String> urls = jobConfig.getOptionAsList(ConfigKeyType.KEY_URL);

    UrlValidator urlValidator = UrlValidator.getInstance();
    if (StringUtils.isNotBlank(url) && !urlValidator.isValid(url)) {
      return ConfigValidaionResult.fail("The url '" + url + "' is not a valid url.");
    }

    if (CollectionUtils.isNotEmpty(urls)) {
      for (String each : urls) {
        if (StringUtils.isNotBlank(each) && !urlValidator.isValid(each)) {
          return ConfigValidaionResult.fail("The url '" + each + "' is not a valid url.");
        }
      }
    }

    if (this.isCronJobExisted(jobConfig.getJobKey())) {
      return ConfigValidaionResult
          .fail("The job '" + jobConfig.getJobKey() + "' is already existed.");
    }

    return result;
  }

  private void checkJobValidation(JobPoJo job) {
    if (null == job) {
      throw new JobValidationException("Job is null");
    }

    if (StringUtils.isBlank(job.getJobKey())) {
      throw new JobValidationException("Job key is required.");
    }

    if (StringUtils.isNotBlank(job.getProcessorKey())
        && !processorManager.isProcessorKeyExisted(job.getProcessorKey())) {
      throw new JobValidationException("Processor key(" + job.getProcessorKey()
          + ") is not existed, please register the processor first.");
    }

    if (StringUtils.isNoneBlank(job.getCronExpression())
        && !CronExpression.isValidExpression(job.getCronExpression())) {
      throw new JobValidationException("The job crond expression is invalid.");
    }
  }

  private boolean cancelIfExisted(String pangolinJobKey) {
    JobKey cronJobKey = createCronJobKey(pangolinJobKey);
    try {
      if (this.cronScheduler.checkExists(cronJobKey)) {
        this.cronScheduler.interrupt(cronJobKey);
        return this.cronScheduler.deleteJob(cronJobKey);
      }
    } catch (SchedulerException e) {
      LoggerUtils.error(this.getClass(), "Cancel cron job error key:" + pangolinJobKey, e);
    }
    return false;
  }

  // for single node mode, not suitable for distribute env
  private boolean pauseCronJobIfExisted(String pangolinJobKey) {
    JobKey cronJobKey = createCronJobKey(pangolinJobKey);
    try {
      if (this.cronScheduler.checkExists(cronJobKey)) {
        this.cronScheduler.pauseJob(cronJobKey);
        return true;
      }
    } catch (SchedulerException e) {
      LoggerUtils.error(this.getClass(), "Cancel cron job error, key:" + pangolinJobKey, e);
    }
    return false;
  }

  private boolean isCronJobExisted(String jobKey) {
    JobKey cronJobKey = createCronJobKey(jobKey);
    try {
      return this.cronScheduler.checkExists(cronJobKey);
    } catch (SchedulerException e) {
      LoggerUtils.error(this.getClass(), "Check cron job existence error, key:" + jobKey, e);
    }
    return false;
  }

  private boolean resumeIfExisted(String pangolinJobKey) {
    JobKey cronJobKey = createCronJobKey(pangolinJobKey);
    try {
      if (this.cronScheduler.checkExists(cronJobKey)) {
        this.cronScheduler.resumeJob(cronJobKey);
        return true;
      }
    } catch (SchedulerException e) {
      LoggerUtils.error(this.getClass(), "Cancel cron job error, key:" + pangolinJobKey, e);
    }
    return false;
  }

  private JobKey createCronJobKey(String pangolinJobKey) {
    return JobKey.jobKey(pangolinJobKey);
  }

  private void scheduleCronJob(CronTaskEntry cronTask) {

    JobDetail jobDetail = JobBuilder.newJob(CronTaskJobContainer.class)
        .withIdentity(createCronJobKey(cronTask.getJobKey())).build();// 设置Job的名字和组

    try {
      if (!CronExpression.isValidExpression(cronTask.getCronExpression())) {
        logger.error("Schedule Job（Task:" + cronTask.getJobKey() + "） CronExpression "
            + cronTask.getCronExpression() + " is invalid !");
        return;
      }

      CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
          .cronSchedule(cronTask.getCronExpression());
      jobDetail.getJobDataMap().put(CronTaskJobContainer.CONTEXT_KEY_TASK_ENTRY, cronTask);

      CronTrigger cronTrigger = TriggerBuilder.newTrigger()
          .withIdentity(cronTask.getTaskId(), cronTask.getJobKey()).withSchedule(scheduleBuilder)
          .build();

      cronScheduler.scheduleJob(jobDetail, cronTrigger);

    } catch (Exception e) {
      logger.error("Schedule Job（Task:" + cronTask.getJobKey() + "） Error !", e);
    }
  }

}
