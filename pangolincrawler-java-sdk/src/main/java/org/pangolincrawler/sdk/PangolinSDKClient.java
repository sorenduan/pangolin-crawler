package org.pangolincrawler.sdk;

import org.pangolincrawler.sdk.model.Job;
import org.pangolincrawler.sdk.task.TaskInfo;

public interface PangolinSDKClient {

  // private static final PangolinSDKClient defaultInstance;

  /// ########################################################################################
  /// TPL API
  /// ########################################################################################
  public ApiResponse getTpl(String name);

  public ApiResponse testJob(String jsonContent);

  public ApiResponse getJobTplList();

  public ApiResponse getProcessorTplList();

  // ########################################################################################
  // Processor API
  // ########################################################################################

  /**
   * Get public processor info.
   * 
   * @param processorKey
   *          TODO
   * 
   * @return
   */
  public ApiResponse getProcessorList(String processorKey);

  public ApiResponse registerProcessorByJsonString(String json);

  // ########################################################################################
  // Job API
  // ########################################################################################

  /**
   * show job info
   * 
   * @return
   * @throws PangolinException
   */
  public ApiResponse getJobList();

  public ApiResponse removeJobByJobKey(String jobKey);

  public ApiResponse parseJobByJobKey(String jobKey, boolean temporary);

  public ApiResponse triggerJobByJobKey(String jobKey);

  public ApiResponse resumeJobByJobKey(String jobKey, boolean temporary);

  public ApiResponse getJobDetailList(String jobKey, boolean showDetail,
      boolean showList);

  public ApiResponse registerJobByJsonString(String json);

  public ApiResponse updateJobByJsonString(String json);

  public ApiResponse registerJob(Job job);

  public ApiResponse validateJobConfig(String json);

  // ########################################################################################
  // Task API
  // ########################################################################################

  public ApiResponse getTaskList(String jobKey);

  public ApiResponse clearTasks(String jobKey, boolean clearAll);

  public ApiResponse countTasks(String jobKey, int statusCode);

  public Job getJob(String key);

  public void sendTask(TaskInfo task);

  // ########################################################################################
  // Cache API
  // ########################################################################################

  public ApiResponse deleteCache(String jobKey);

  // ########################################################################################
  // Service API
  // ########################################################################################

  public ApiResponse listServices(String serviceName, String version, int offset);

  public ApiResponse listMethods(String serviceName, String version, String methodName,
      int offset);

  public ApiResponse callMethods(String serviceName, String version, String methodName,
      String input);

  // ########################################################################################
  // Plugin API
  // ########################################################################################
  public ApiResponse listLocalPlugins();

  public ApiResponse listRegisteredPlugins();

  public ApiResponse registerPlugin(String pluginKey);

  public ApiResponse unregisterPlugin(String pluginKey);

  ApiResponse unregisterProcessorByKey(String processorKey);

  ApiResponse updateProcessorByJsonString(String json);
}
