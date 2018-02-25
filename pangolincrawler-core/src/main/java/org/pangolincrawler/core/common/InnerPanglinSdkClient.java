package org.pangolincrawler.core.common;

import javax.annotation.PostConstruct;

import org.pangolincrawler.core.processor.ProcessorManager;
import org.pangolincrawler.core.service.PublicServiceManager;
import org.pangolincrawler.core.task.InnerTaskEntry;
import org.pangolincrawler.core.task.TaskManager;
import org.pangolincrawler.core.utils.TaskUtils;
import org.pangolincrawler.sdk.ApiResponse;
import org.pangolincrawler.sdk.PangolinSDKClient;
import org.pangolincrawler.sdk.SdkClientFactory;
import org.pangolincrawler.sdk.model.Job;
import org.pangolincrawler.sdk.task.TaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InnerPanglinSdkClient implements PangolinSDKClient {

  @Autowired
  private PublicServiceManager serviceManager;

  @Autowired
  private TaskManager taskManager;

  @Autowired
  private ProcessorManager processorManager;

  @PostConstruct
  public void init() {
    SdkClientFactory.setInstance(this);
  }

  @Override
  public ApiResponse getTpl(String name) {
    return null;
  }

  @Override
  public ApiResponse testJob(String jsonContent) {
    return null;
  }

  @Override
  public ApiResponse getJobTplList() {
    return null;
  }

  @Override
  public ApiResponse getProcessorTplList() {
    return null;
  }

  @Override
  public ApiResponse getProcessorList(String processorKey) {
    return null;
  }

  @Override
  public ApiResponse registerProcessorByJsonString(String json) {
    return null;
  }

  @Override
  public ApiResponse getJobList() {
    return null;
  }

  @Override
  public ApiResponse removeJobByJobKey(String jobKey) {
    return null;
  }

  @Override
  public ApiResponse parseJobByJobKey(String jobKey, boolean temporary) {
    return null;
  }

  @Override
  public ApiResponse triggerJobByJobKey(String jobKey) {
    return null;
  }

  @Override
  public ApiResponse resumeJobByJobKey(String jobKey, boolean temporary) {
    return null;
  }

  @Override
  public ApiResponse getJobDetailList(String jobKey, boolean showDetail, boolean showList) {
    return null;
  }

  @Override
  public ApiResponse registerJobByJsonString(String json) {
    return null;
  }

  @Override
  public ApiResponse updateJobByJsonString(String json) {
    return null;
  }

  @Override
  public ApiResponse registerJob(Job job) {
    return null;
  }

  @Override
  public ApiResponse validateJobConfig(String json) {
    return null;
  }

  @Override
  public ApiResponse getTaskList(String jobKey) {
    return null;
  }

  @Override
  public ApiResponse clearTasks(String jobKey, boolean clearAll) {
    return null;
  }

  @Override
  public ApiResponse countTasks(String jobKey, int statusCode) {
    return null;
  }

  @Override
  public Job getJob(String key) {
    return null;
  }

  @Override
  public ApiResponse deleteCache(String jobKey) {
    return null;
  }

  @Override
  public ApiResponse listServices(String serviceName, String version, int offset) {
    return null;
  }

  @Override
  public ApiResponse listMethods(String serviceName, String version, String methodName,
      int offset) {
    return null;
  }

  @Override
  public ApiResponse callMethods(String serviceName, String version, String methodName,
      String input) {
    String r = serviceManager.call(serviceName, methodName, version, input);
    return ApiResponse.build(true, r);
  }

  @Override
  public ApiResponse listLocalPlugins() {
    return null;
  }

  @Override
  public ApiResponse listRegisteredPlugins() {
    return null;
  }

  @Override
  public ApiResponse registerPlugin(String pluginKey) {
    return null;
  }

  @Override
  public ApiResponse unregisterPlugin(String pluginKey) {
    return null;
  }

  @Override
  public void sendTask(TaskInfo task) {
    // TODO
    InnerTaskEntry entry = TaskUtils.taskInfoToTaskEntry(task, null);

    taskManager.sendTask(entry);
  }

  @Override
  public ApiResponse unregisterProcessorByKey(String processorKey) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ApiResponse updateProcessorByJsonString(String json) {
    // TODO Auto-generated method stub
    return null;
  }

}
