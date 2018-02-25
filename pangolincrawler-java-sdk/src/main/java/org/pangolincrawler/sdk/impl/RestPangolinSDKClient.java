package org.pangolincrawler.sdk.impl;

import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.pangolincrawler.sdk.ApiResponse;
import org.pangolincrawler.sdk.Constants;
import org.pangolincrawler.sdk.PangolinSDKClient;
import org.pangolincrawler.sdk.SdkCientConfiguration;
import org.pangolincrawler.sdk.model.Job;
import org.pangolincrawler.sdk.task.TaskInfo;
import org.pangolincrawler.sdk.utils.StringUtils;
import org.pangolincrawler.sdk.utils.UrlUtils;

public final class RestPangolinSDKClient implements PangolinSDKClient {

  private final OkHttpClient httpClient;

  private static final MediaType MEDIA_TYPE_JSON = MediaType
      .parse("application/json; charset=utf-8");
  private static final MediaType MEDIA_TYPE_TEXT = MediaType
      .parse("plain/text; charset=utf-8");

  private SdkCientConfiguration config;

  public RestPangolinSDKClient(SdkCientConfiguration config) {
    this.config = config;

    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    ConnectionPool connectionPool = new ConnectionPool(
        Constants.CONNECTION_POOL_MAX_IDLE_COUNT,
        Constants.CONNECTION_POOL_MAX_IDLE_MINUTES, TimeUnit.MINUTES);

    builder.connectionPool(connectionPool);

    builder.connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS);
    builder.readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS);
    builder.writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS);

    httpClient = builder.build();
  }

  public ApiResponse request(Request request) {
    try {
      try (Response response = getHttpClient().newCall(request).execute()) {
        if (response.isSuccessful()) {
          ApiResponse apiResponse = ApiResponse.build(response);
          return apiResponse;
        }
        return new ApiResponse(ApiResponse.CODE_CMD_INNER_ERROR,
            buildHttpErrorMessage(response), false);
      }
    } catch (Exception e) {
      return new ApiResponse(ApiResponse.CODE_CMD_INNER_ERROR, e.getLocalizedMessage(),
          false);
    }
  }

  private OkHttpClient getHttpClient() {
    return getHttpClient(-1);
  }

  private OkHttpClient getHttpClient(long timeoutBtMs) {
    if (timeoutBtMs > 0) {
      OkHttpClient.Builder builder = new OkHttpClient.Builder();
      ConnectionPool connectionPool = new ConnectionPool(
          Constants.CONNECTION_POOL_MAX_IDLE_COUNT,
          Constants.CONNECTION_POOL_MAX_IDLE_MINUTES, TimeUnit.MINUTES);

      builder.connectionPool(connectionPool);

      builder.connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS);
      builder.writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS);

      builder.readTimeout(timeoutBtMs, TimeUnit.MILLISECONDS);

      return builder.build();
    }
    return getDefaultHttpClient();

  }

  private OkHttpClient getDefaultHttpClient() {
    return this.httpClient;
  }

  public String buildHttpErrorMessage(Response response) {

    StringBuilder sb = new StringBuilder();

    sb.append("Response Code is ");
    sb.append(response.code());
    sb.append(", url is ");
    sb.append(response.request().url());
    sb.append(".");
    if (response.code() >= 400) {
      sb.append(". Please check the server log.");
    }
    return sb.toString();
  }

  /// ########################################################################################
  /// TPL API
  /// ########################################################################################

  public ApiResponse getTpl(String name) {
    return request(new Request.Builder().get().url(this.config.getTplUrl(name)).build());
  }

  public ApiResponse testJob(String jsonContent) {
    RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, jsonContent);
    return request(
        new Request.Builder().post(body).url(this.config.getTestJobUrl()).build());
  }

  public ApiResponse getJobTplList() {
    return request(
        new Request.Builder().get().url(this.config.getJobTplListUrl()).build());
  }

  public ApiResponse getProcessorTplList() {
    return request(
        new Request.Builder().get().url(this.config.getProcessorTplListUrl()).build());
  }

  // ########################################################################################
  // Processor API
  // ########################################################################################

  /**
   * Get public processor information list.
   */
  public ApiResponse getProcessorList(String processorKey) {
    return request(new Request.Builder().get()
        .url(this.config.getProcessorListUrl(processorKey)).build());
  }

  public ApiResponse registerProcessorByJsonString(String json) {
    RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
    Request request = new Request.Builder().post(body)
        .url(this.config.getBaseProcessorApiUrl()).build();
    return request(request);
  }

  public ApiResponse updateProcessorByJsonString(String json) {
    RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
    Request request = new Request.Builder().put(body)
        .url(this.config.getBaseProcessorApiUrl()).build();
    return request(request);
  }

  public ApiResponse unregisterProcessorByKey(String processorKey) {
    String url = this.config.getSingleProcessorUrl(processorKey);
    Request request = new Request.Builder().delete().url(url).build();
    return request(request);
  }

  // ########################################################################################
  // Job API
  // ########################################################################################

  /**
   * show job info
   */
  public ApiResponse getJobList() {
    return getJobDetailList(null, false, true);
  }

  public ApiResponse removeJobByJobKey(String jobKey) {
    String jobUrl = this.config.getBaseJobUrl(jobKey);
    Request request = new Request.Builder().delete().url(jobUrl).build();
    return request(request);
  }

  public ApiResponse parseJobByJobKey(String jobKey, boolean temporary) {
    String jobUrl = this.config.getParseJobUrl(jobKey, temporary);
    RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, jobKey);
    Request request = new Request.Builder().put(body).url(jobUrl).build();
    return request(request);
  }

  public ApiResponse triggerJobByJobKey(String jobKey) {
    String jobUrl = this.config.getStartJobUrl(jobKey);
    RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, jobKey);
    Request request = new Request.Builder().put(body).url(jobUrl).build();
    return request(request);
  }

  public ApiResponse resumeJobByJobKey(String jobKey, boolean temporary) {
    String jobUrl = this.config.getResumeJobUrl(jobKey, temporary);
    RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, jobKey);
    Request request = new Request.Builder().put(body).url(jobUrl).build();
    return request(request);
  }

  public ApiResponse getJobDetailList(String jobKey, boolean showDetail,
      boolean showList) {
    String jobListUrl = this.config.getJobListUrl(jobKey);
    if (showDetail) {
      jobListUrl = UrlUtils.addUrlParam("detail", true, jobListUrl, config.getEncoding());
    }

    Request request = new Request.Builder().get().url(jobListUrl).build();
    return request(request);
  }

  public ApiResponse registerJobByJsonString(String json) {
    RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
    Request request = new Request.Builder().post(body).url(this.config.getBaseJobUrl())
        .build();
    return request(request);
  }

  public ApiResponse updateJobByJsonString(String json) {
    RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
    Request request = new Request.Builder().put(body).url(this.config.getBaseJobUrl())
        .build();
    return request(request);
  }

  public ApiResponse registerJob(Job job) {
    if (null != job) {
      return this.registerJobByJsonString(job.toJson());
    }
    return new ApiResponse(ApiResponse.CODE_CMD_INNER_ERROR,
        "The job to registering is null", false);
  }

  public ApiResponse validateJobConfig(String json) {
    RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
    Request request = new Request.Builder().post(body)
        .url(this.config.getJobConfigValidationUrl()).build();
    return request(request);
  }

  // ########################################################################################
  // Task API
  // ########################################################################################

  public ApiResponse getTaskList(String jobKey) {
    String taskListUrl = this.config.getTaskList(jobKey);
    Request request = new Request.Builder().get().url(taskListUrl).build();
    return request(request);
  }

  public ApiResponse clearTasks(String jobKey, boolean clearAll) {
    String url = this.config.getClearTasksUrl(jobKey);

    url = UrlUtils.addUrlParam("all", clearAll, url, config.getEncoding());

    Request request = new Request.Builder().delete().url(url).build();
    return request(request);
  }

  public ApiResponse countTasks(String jobKey, int statusCode) {
    String url = this.config.getTasksCountUrl(jobKey);
    if (statusCode >= 0) {
      url = UrlUtils.addUrlParam("status", statusCode, url, config.getEncoding());
    }
    Request request = new Request.Builder().get().url(url).build();
    return request(request);
  }

  public Job getJob(String key) {
    return null;
  }

  // ########################################################################################
  // Cache API
  // ########################################################################################

  public ApiResponse deleteCache(String jobKey) {
    String url = this.config.getDeleteCacheUrl(jobKey);
    Request request = new Request.Builder().delete().url(url).build();
    return request(request);
  }

  // ########################################################################################
  // Service API
  // ########################################################################################

  public ApiResponse listServices(String serviceName, String version, int offset) {
    String url = this.config.getServiceListUrl(serviceName, version, null, offset);
    Request request = new Request.Builder().get().url(url).build();
    return request(request);
  }

  public ApiResponse listMethods(String serviceName, String version, String methodName,
      int offset) {
    String url = this.config.getMethodsListUrl(serviceName, version, methodName, offset);
    Request request = new Request.Builder().get().url(url).build();
    return request(request);
  }

  public ApiResponse callMethods(String serviceName, String version, String methodName,
      String input) {
    String url = this.config.getCallServiceUrl(serviceName, version, methodName);
    // request body can't be emtpy.
    if (StringUtils.isEmpty(input)) {
      input = "Call " + serviceName + ":" + methodName + ":" + version;
    }
    RequestBody body = RequestBody.create(MEDIA_TYPE_TEXT,
        StringUtils.trimToEmpty(input));
    Request request = new Request.Builder().post(body).url(url).build();

    return request(request);
  }

  // ########################################################################################
  // Plugin API
  // ########################################################################################
  public ApiResponse listLocalPlugins() {
    String url = this.config.getLocalPluginListUrl();
    Request request = new Request.Builder().get().url(url).build();
    return request(request);
  }

  public ApiResponse listRegisteredPlugins() {
    String url = this.config.getRegisteredPluginListUrl();
    Request request = new Request.Builder().get().url(url).build();
    return request(request);
  }

  public ApiResponse registerPlugin(String pluginKey) {
    String url = this.config.getRegisterPluginUrl(pluginKey);
    RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, pluginKey);
    return request(new Request.Builder().post(body).url(url).build());
  }

  public ApiResponse unregisterPlugin(String pluginKey) {
    String url = this.config.getRegisterPluginUrl(pluginKey);
    return request(new Request.Builder().delete().url(url).build());
  }

  @Override
  public void sendTask(TaskInfo task) {
    // TODO Auto-generated method stub

  }
}
