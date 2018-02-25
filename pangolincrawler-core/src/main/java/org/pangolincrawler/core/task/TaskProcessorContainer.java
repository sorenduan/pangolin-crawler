package org.pangolincrawler.core.task;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.pangolincrawler.core.PangolinApplication;
import org.pangolincrawler.core.cache.CacheManager;
import org.pangolincrawler.core.constants.Constants;
import org.pangolincrawler.core.db.TaskDbService;
import org.pangolincrawler.core.db.TaskPoJo;
import org.pangolincrawler.core.http.HttpClientService;
import org.pangolincrawler.core.http.PangolinHttpClientRequest;
import org.pangolincrawler.core.http.PangolinHttpClientResponse;
import org.pangolincrawler.core.job.LoopOptions;
import org.pangolincrawler.core.job.TaskStatisticManager;
import org.pangolincrawler.core.plugin.ConfigKeyType;
import org.pangolincrawler.core.processor.ProcessorManager;
import org.pangolincrawler.core.task.rate.RateManager;
import org.pangolincrawler.core.utils.JsonUtils;
import org.pangolincrawler.core.utils.LoggerUtils;
import org.pangolincrawler.core.utils.TaskUtils;
import org.pangolincrawler.core.utils.TimeUtils;
import org.pangolincrawler.core.utils.UrlUtils;
import org.pangolincrawler.sdk.task.TaskProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskProcessorContainer
    implements Runnable, Comparable<TaskProcessorContainer>, Serializable {

  private static final long serialVersionUID = 2142384788623763039L;

  protected static Logger logger = LoggerFactory.getLogger(TaskProcessorContainer.class);

  private InnerTaskEntry entry;

  private TaskProcessor processor;

  /**
   * run next job locally.
   */
  private boolean distributed = true;

  private String resultCache = null;

  private boolean cacheable = false;

  private PangolinHttpClientResponse preFetchHttpResponse;

  public TaskProcessorContainer(TaskProcessor processor) {
    super();
    this.processor = processor;
  }

  @Override
  public void run() {

    try {

      if (!entry.isRunForTest()) {
        if (isTaskCancelling()) {
          this.setTaskCancelled();
          logger.info("Cancel Task \"" + entry.getTaskIdAndKey() + " \" !");
          return;
        }

        this.setCurrentThreadName();
        logger.info("Running Task \"" + entry.getTaskIdAndKey() + " \" !");
        this.updateStatisticStatusForRuning();
      }
      if (entry.isTraceable()) {
        entry.putTraceValue(entry.getJobKey(), "url", entry.getUrl());
        entry.putTraceValue(entry.getJobKey(), "callback_url", entry.getCallbackUrl());
      }

      String processResult = null;
      if (cacheable && !entry.isRunForTest()) {
        processResult = resultCache;
      } else {
        this.doPreFetch();
        this.processor.setHtml(this.getPreFetechHtml());
        processResult = this.processor.process(entry.getPayload());
      }

      if (entry.isTraceable()) {
        entry.putTraceValue(entry.getJobKey(), "process_result", processResult);
      }

      if (!cacheable && !entry.isRunForTest()) {
        this.clearRate();
      }
      this.doCache(processResult);
      this.doOutputFile(processResult);
      this.doCallback(processResult);
      this.doLoop();
      this.doNextTask(processResult);

      this.updateStatisticStatusForFinished();
    } catch (Exception e) {
      logger.error("Running Task(" + entry.getTaskIdAndKey() + ") error!", e);
      if (entry.isTraceable()) {
        entry.putTraceValue(entry.getJobKey(), "process_error", e.getLocalizedMessage());
      }
      getStatisticManager().makringTaskFail(this.entry, e.getLocalizedMessage());
    } finally {
      this.decreaseTaskCount();
      this.doSanpshotForDebug();
    }
  }

  private void doSanpshotForDebug() {
    if (this.entry.isTaskSnapshotEnable()) {
      this.getTaskManagerService().snapshotTask(this.entry, this.getPreFetechHtml());
    }
  }

  private boolean isTaskCancelling() {
    TaskDbService taskDbService = PangolinApplication.getSystemService(TaskDbService.class);

    TaskPoJo task = taskDbService.getOneByTaskId(this.entry.getTaskId());
    return null == task || task.getStatusAsType().equals(TaskPoJo.TaskStatus.CANCELLING);
  }

  private void setTaskCancelled() {
    TaskStatisticManager statisticManager = this.getStatisticManager();
    statisticManager.makringTaskStatus(this.entry, TaskPoJo.TaskStatus.CANCELLED);
  }

  private void decreaseTaskCount() {
    this.getTaskCacheManager().decreaseTaskCount(this.entry);
  }

  private void updateStatisticStatusForRuning() {
    TaskStatisticManager statisticManager = this.getStatisticManager();
    statisticManager.markTaskRunning(this.entry);
  }

  private void updateStatisticStatusForFinished() {
    TaskStatisticManager statisticManager = this.getStatisticManager();
    statisticManager.makringTaskFinised(this.entry);
  }

  private TaskStatisticManager getStatisticManager() {
    return PangolinApplication.getSystemService(TaskStatisticManager.class);
  }

  @SuppressWarnings("rawtypes")
  private void doLoop() {
    if (null == this.entry.getLoop()) {
      return;
    }
    List<String> list = parseLinks();
    if (entry.isTraceable()) {
      entry.putTraceValue(this.entry.getJobKey(), "loop_links", (ArrayList) list);
    }
    if (CollectionUtils.isEmpty(list)) {
      return;
    }
    runLoop(list);
  }

  private void runLoop(List<String> urls) {
    if (entry.isRunForTest()) {
      return;
    }
    urls.forEach(eachLink -> {
      InnerTaskEntry newTask = TaskUtils.cloneTaskEntry(this.entry);
      newTask.setUrl(eachLink);
      sendTask(newTask);
    });
  }

  private ArrayList<String> normalizeUrls(List<String> urls) {
    Set<String> urlSet = new LinkedHashSet<>();
    urls.forEach(url -> {
      if (url.contains("#")) {
        String[] parts = StringUtils.split(url, "#");
        urlSet.add(StringUtils.trim(parts[0]));
      } else {
        urlSet.add(StringUtils.trim(url));
      }
    });
    return new ArrayList<>(urlSet);
  }

  private ArrayList<String> parseLinks() {

    ArrayList<String> linksList = new ArrayList<>();
    String html = this.getPreFetechHtml();
    LoopOptions loopOpts = this.entry.getLoop();

    if (StringUtils.isBlank(html) || null == loopOpts) {
      return linksList;
    }

    Document doc = Jsoup.parse(html);
    Elements links = doc.select("a[href]");

    int len = links.size();
    for (int i = 0; i < len; i++) {
      String link = links.get(i).attr("href");
      String absoluteUrl = UrlUtils.toAbsoluteUrl(entry.getUrl(), link);
      if (loopOpts.isMathed(absoluteUrl)) {
        linksList.add(absoluteUrl);
      }
    }
    return normalizeUrls(linksList);
  }

  private void clearRate() {
    if (entry.isRunForTest()) {
      return;
    }
    if (null != entry.getRates()) {
      RateManager rateManager = PangolinApplication.getSystemService(RateManager.class);
      rateManager.decrRate(entry);
    }
  }

  private void doCache(String processResult) {
    if (entry.isRunForTest()) {
      return;
    }
    if (entry.isCacheable()) {
      getCacheManager().put(entry.getCacheKey(), processResult, entry.getCacheExpirationMs(),
          TimeUnit.MILLISECONDS);
    }
  }

  private void doOutputFile(String processResult) {
    if (entry.getOptions() == null) {
      return;
    }

    if (null == entry.getOption(ConfigKeyType.KEY_FILE_OUTPUT)) {
      return;
    }

    String baseFileName = StringUtils
        .isNotBlank(entry.getOptionAsString(ConfigKeyType.KEY_FILE_OUTPUT_FILENAME))
            ? entry.getOptionAsString(ConfigKeyType.KEY_FILE_OUTPUT,
                ConfigKeyType.KEY_FILE_OUTPUT_FILENAME)
            : entry.getJobKey();
    String baseFileSuffix = StringUtils
        .isNotBlank(entry.getOptionAsString(ConfigKeyType.KEY_FILE_OUTPUT_FILE_SUFFIX))
            ? entry.getOptionAsString(ConfigKeyType.KEY_FILE_OUTPUT,
                ConfigKeyType.KEY_FILE_OUTPUT_FILE_SUFFIX)
            : String.valueOf(ConfigKeyType.KEY_FILE_OUTPUT_FILE_SUFFIX.getDefaultValue());

    String filename = baseFileName + "_" + TimeUtils.getCurrentDateTextForFileName()
        + baseFileSuffix;

    String dir = entry.getOptionAsString(ConfigKeyType.KEY_FILE_OUTPUT,
        ConfigKeyType.KEY_FILE_OUTPUT_DIR);

    String encoding = ConfigKeyType.KEY_FILE_OUTPUT_FILE_ENCODING.getDefaultValueAsString();

    File file = Paths.get(dir, filename).toFile();

    try {
      FileUtils.write(file, processResult, encoding);

      if (this.entry.isTraceable()) {
        this.entry.putTraceValue(this.entry.getJobKey(), "ouput_file", file.getAbsolutePath());
      }

      boolean genIndexJson = entry.getOptionAsBoolean(ConfigKeyType.KEY_FILE_OUTPUT,
          ConfigKeyType.KEY_FILE_OUTPUT_FILE_GENERATE_INDEX_FILE,
          ConfigKeyType.KEY_FILE_OUTPUT_FILE_GENERATE_INDEX_FILE.getDefaultValueAsBoolean());
      if (genIndexJson) {
        buildFileIndexJson(dir);
      }

    } catch (IOException e) {
      LoggerUtils.error(this.getClass(), "Write processResult to file(" + file.getAbsolutePath()
          + ") for job(" + entry.getJobKey() + ") error.", e);
    }
  }

  private void buildFileIndexJson(String path) {

    File[] files = Paths.get(path).toFile().listFiles((FileFilter) FileFileFilter.FILE);

    if (null == files) {
      return;
    }

    Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);

    List<String> r = new ArrayList<>();
    for (File each : files) {
      r.add(each.getName());
    }
    String jsonString = JsonUtils.toJson(r);

    File indexFile = Paths.get(path, Constants.OUTPUT_FILE_INDEX_FILENAME).toFile();

    try {
      FileUtils.write(indexFile, jsonString, StandardCharsets.UTF_8);
      if (this.entry.isTraceable()) {
        this.entry.putTraceValue(this.entry.getJobKey(), "ouput_index_file",
            indexFile.getAbsolutePath());
      }
    } catch (IOException e) {
      LoggerUtils.error(this.getClass(),
          "Write output index file to \"" + indexFile.getAbsolutePath() + "\" error.", e);
    }
  }

  private void doCallback(String processResult) {
    if (StringUtils.isNoneBlank(entry.getCallbackUrl())) {
      JsonObject json = new JsonObject();
      json.addProperty("processor_result", processResult);
      json.add("task_context", JsonUtils.toJsonObject(entry.getTaskContext()));
      getHttpRequestService().postJson(entry.getCallbackUrl(), JsonUtils.toJson(json));
    }
  }

  private void doNextTask(String preProcessResult) {

    if (null == entry || null == entry.getNextTask()) {
      return;
    }
    if (entry.isRunForTest()) {
      return;
    }

    InnerTaskEntry nextTask = entry.getNextTask();
    nextTask.setParentTaskId(entry.getTaskId());
    nextTask.setParentJobKey(entry.getJobKey());
    nextTask.setPayload(preProcessResult);

    if (MapUtils.isNotEmpty(entry.getTaskContext())) {
      nextTask.putContextAll(entry.getTaskContext());
    }

    if (entry.isTraceable() && nextTask.isTraceable()) {
      nextTask.setTraceContext(entry.getTraceContext());
    }

    if (this.isDistributed()) {
      getTaskManagerService().sendTask(nextTask);
    } else {
      try {

        ProcessorManager processorManager = PangolinApplication
            .getSystemService(ProcessorManager.class);

        TaskProcessorContainer nextProcessor = processorManager
            .createTaskProcessorContainerFromTaskEntry(nextTask);
        nextProcessor.run();
      } catch (InstantiationException | IllegalAccessException e) {
        logger.error("Run next processor error.", e);
      }
    }
  }

  protected String getPayload() {
    if (null != this.entry) {
      return this.entry.getPayload();
    }
    return null;
  }

  protected String getPreFetechHtml() {
    if (null != this.preFetchHttpResponse && this.preFetchHttpResponse.isSuccess()) {
      return this.preFetchHttpResponse.getHtml();
    }
    return null;
  }

  private void doPreFetch() {
    if (!this.getEntry().isPreFetch() || StringUtils.isBlank(this.getEntry().getUrl())) {
      return;
    }

    PangolinHttpClientRequest request = this.buildHttpRequest();
    this.preFetchHttpResponse = getHttpRequestService().request(request);
  }

  private PangolinHttpClientRequest buildHttpRequest() {

    PangolinHttpClientRequest request = new PangolinHttpClientRequest(entry.getUrl());

    request.setMethod(entry.getHttpRequestMethod());
    request.setCachable(entry.isCacheable());

    if (null != entry.getProxy()) {
      request.setProxy(entry.getProxy());
      if (MapUtils.isNotEmpty(entry.getProxy().getHeadersForProxy())) {
        request.addHeaders(entry.getProxy().getHeadersForProxy());
      }
    }
    return request;
  }

  private void setCurrentThreadName() {
    String sp = "|";
    String oldThradName = Thread.currentThread().getName();
    String[] oldThradNameParts = StringUtils.split(oldThradName, sp);
    if (null != oldThradNameParts && oldThradNameParts.length > 0) {
      String newThreadName = oldThradNameParts[0] + sp + this.getEntry().getTaskIdAndKey();
      Thread.currentThread().setName(newThreadName);
    }
  }

  protected void sendTask(InnerTaskEntry task) {
    getTaskManagerService().sendTask(task);
  }

  public InnerTaskEntry getEntry() {
    return entry;
  }

  public void setEntry(InnerTaskEntry entry) {
    this.entry = entry;
  }

  @Override
  public int compareTo(TaskProcessorContainer o) {

    if (this.entry.getPriority() > o.getEntry().getPriority()) {
      return -1;
    } else if (this.entry.getPriority() < o.getEntry().getPriority()) {
      return 1;
    }
    return 0;
  }

  public TaskManager getTaskManagerService() {
    return PangolinApplication.getTaskManagerService();
  }

  public CacheManager getCacheManager() {
    return PangolinApplication.getCacheManagerService();
  }

  public TaskCacheManager getTaskCacheManager() {
    return PangolinApplication.getSystemService(TaskCacheManager.class);
  }

  private HttpClientService getHttpRequestService() {
    return PangolinApplication.getHttpClientService();
  }

  public Logger getLogger() {
    return logger;
  }

  public boolean isDistributed() {
    return entry.isDistributed();
  }

  public void setDistributed(boolean distributed) {
    this.entry.setDistributed(distributed);
  }

  public void setResultCache(String resultCache) {
    this.resultCache = resultCache;
  }

  public void setCacheable(boolean cacheable) {
    this.cacheable = cacheable;
  }

  public TaskProcessor getProcessor() {
    return processor;
  }
}
