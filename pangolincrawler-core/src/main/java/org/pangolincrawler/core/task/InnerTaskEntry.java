package org.pangolincrawler.core.task;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.pangolincrawler.core.http.PangolinHttpClientRequest;
import org.pangolincrawler.core.http.PangolinHttpProxyInfo;
import org.pangolincrawler.core.job.LoopOptions;
import org.pangolincrawler.core.plugin.BaseConfig;
import org.pangolincrawler.core.task.rate.RateExpressions;
import org.pangolincrawler.core.utils.TaskUtils;
import org.pangolincrawler.sdk.task.TaskProcessor;

public class InnerTaskEntry extends BaseConfig {

  private static final long serialVersionUID = 8644696970424807805L;

  private static final String TASK_CACHE_PRFIX = "honycomb_task_cache";

  protected String taskId;
  protected String parentTaskId;

  protected String jobKey;
  protected String parentJobKey;

  private HashMap<String, Object> processorContext;
  
  //private String processorClassName;
  
  private String processorKey;
  
  @Deprecated
  //private Class<? extends TaskProcessor> processorClass;

  /**
   * Task priority, smaller number with higher priority.
   */
  protected int priority = 10;

  private Map<String, String> taskContext;

  private Map<String, Serializable> traceContext;

  private RateExpressions rates;

  private LoopOptions loop;

  private TaskCountOptions taskCounter;

  private String payload;

  private String url;

  private boolean cacheable = false;

  private boolean taskSnapshotEnable = false;

  private long cacheExpirationMs = -1;

  private boolean preFetch = true;

  @Deprecated
  private boolean httpResponseCacheable = false;

  private boolean traceable = false;

  private boolean runForTest = false;

  private PangolinHttpProxyInfo proxy;

  private InnerTaskEntry nextTask;

  private boolean distributed = true;

  /**
   * Post task context and processor result to the callback url when task finished.
   */
  private String callbackUrl;

  private PangolinHttpClientRequest.Method httpRequestMethod = PangolinHttpClientRequest.Method.GET;

  public InnerTaskEntry() {
    super();
    taskContext = new LinkedHashMap<>();
    setTraceContext(new LinkedHashMap<>());
    this.taskId = TaskUtils.generateTaskId();
  }

  public String getTaskId() {
    return taskId;
  }

  public InnerTaskEntry(String taskKey, Class<? extends TaskProcessor> processorClass) {
    this();
    this.jobKey = taskKey;
    //this.processorClass = processorClass;
  }

  public String getPayload() {
    return payload;
  }

  public void setPayload(String payload) {
    this.payload = payload;
  }

  public String getParentTaskId() {
    return parentTaskId;
  }

  public void setParentTaskId(String parentTaskId) {
    this.parentTaskId = parentTaskId;
  }

  public String getJobKey() {
    return jobKey;
  }

  public void setJobKey(String taskKey) {
    this.jobKey = taskKey;
  }

  public void putContext(String key, String value) {
    if (null == this.taskContext) {
      this.taskContext = new LinkedHashMap<>();
    }

    if (null != key) {
      this.taskContext.put(key, value);
    }
  }

  public void putContextAll(Map<String, String> m) {
    if (null == this.taskContext) {
      this.taskContext = new LinkedHashMap<>();
    }

    if (MapUtils.isNotEmpty(m)) {
      this.taskContext.putAll(m);
    }

  }

  public String getTaskIdAndKey() {
    return this.getTaskId() + "_" + this.getJobKey();
  }

  public Map<String, String> getTaskContext() {
    return taskContext;
  }

  public void putTraceValue(String key, Serializable value) {
    this.getTraceContext().put(key, value);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void putTraceValue(String k1, String k2, Serializable value) {
    Serializable v1 = this.getTraceContext().get(k1);
    if (null == v1) {
      v1 = new LinkedHashMap<>();
      this.getTraceContext().put(k1, v1);
    }

    if (!(v1 instanceof Map)) {
      throw new IllegalArgumentException("The Value of '" + k1 + "' is not a map.");
    }

    ((Map) v1).put(k2, value);
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

//  public Class<? extends TaskProcessor> getProcessorClass() {
//    return processorClass;
//  }
//
//  public void setProcessorClass(Class<? extends TaskProcessor> processorClass) {
//    this.processorClass = processorClass;
//  }

  
  
  public String getUrl() {
    return url;
  }

//  public String getProcessorClassName() {
//    return processorClassName;
//  }
//
//  public void setProcessorClassName(String processorClassName) {
//    this.processorClassName = processorClassName;
//  }

  
  public void setUrl(String url) {
    this.url = url;
  }

  public String getProcessorKey() {
    return processorKey;
  }

  public void setProcessorKey(String processorKey) {
    this.processorKey = processorKey;
  }

  public boolean isPreFetch() {
    return preFetch;
  }

  public void setPreFetch(boolean preFetch) {
    this.preFetch = preFetch;
  }

  public PangolinHttpProxyInfo getProxy() {
    return proxy;
  }

  public void setProxy(PangolinHttpProxyInfo proxy) {
    this.proxy = proxy;
  }

  public InnerTaskEntry getNextTask() {
    return nextTask;
  }

  public void setNextTask(InnerTaskEntry nextTask) {
    this.nextTask = nextTask;
  }

  public PangolinHttpClientRequest.Method getHttpRequestMethod() {
    return httpRequestMethod;
  }

  public void setHttpRequestMethod(PangolinHttpClientRequest.Method httpRequestMethod) {
    this.httpRequestMethod = httpRequestMethod;
  }

  public String getCallbackUrl() {
    return callbackUrl;
  }

  public void setCallbackUrl(String callbackUrl) {
    this.callbackUrl = callbackUrl;
  }

  public boolean isTraceable() {
    return traceable;
  }

  public void setTraceable(boolean traceable) {
    this.traceable = traceable;
  }

  public boolean isCacheable() {
    return cacheable;
  }

  public void setCacheable(boolean useCacheable) {
    this.cacheable = useCacheable;
  }

  private String buildCacheKey(String baseCacheKey) {
    return StringUtils.joinWith("_", TASK_CACHE_PRFIX, baseCacheKey);
  }

  public String getCacheKey() {
    return buildCacheKey(url);
  }

  public RateExpressions getRates() {
    return rates;
  }

  public boolean hasRates() {
    return null != this.rates && !rates.isEmpty();
  }

  public void setRates(RateExpressions rates) {
    this.rates = rates;
  }

  public void setCacheExpirationMs(long cacheExpirationMs) {
    this.cacheExpirationMs = cacheExpirationMs;
  }

  public long getCacheExpirationMs() {
    return cacheExpirationMs;
  }

  public LoopOptions getLoop() {
    return loop;
  }

  public boolean isDistributed() {
    return distributed;
  }

  public void setDistributed(boolean distributed) {
    this.distributed = distributed;
  }

  public boolean isRunForTest() {
    return runForTest;
  }

  public void setRunForTest(boolean runForTest) {
    this.runForTest = runForTest;
  }

  public Map<String, Serializable> getTraceContext() {
    return traceContext;
  }

  public void setTraceContext(Map<String, Serializable> traceContext) {
    this.traceContext = traceContext;
  }

  public void setLoop(LoopOptions loop) {
    this.loop = loop;
  }

  public TaskCountOptions getTaskCounter() {
    return taskCounter;
  }

  public void setTaskCounter(TaskCountOptions taskCounter) {
    this.taskCounter = taskCounter;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public boolean isTaskSnapshotEnable() {
    return taskSnapshotEnable;
  }

  public void setTaskSnapshotEnable(boolean taskSnapshotEnable) {
    this.taskSnapshotEnable = taskSnapshotEnable;
  }

  public String getParentJobKey() {
    return parentJobKey;
  }

  public void setParentJobKey(String parentJobKey) {
    this.parentJobKey = parentJobKey;
  }

  public Map<String, Object> getProcessorContext() {
    return processorContext;
  }

  public void setProcessorContext(Map<String, Object> processorContext) {
    if (processorContext instanceof HashMap) {
      this.processorContext = (HashMap<String, Object>) processorContext;
    }
  }

}
