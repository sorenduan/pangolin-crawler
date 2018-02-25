package org.pangolincrawler.sdk;

import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

import org.pangolincrawler.sdk.utils.StringUtils;
import org.pangolincrawler.sdk.utils.UrlUtils;

public class SdkCientConfiguration {
  private String baseApiUrl = "http://127.0.0.1:9797/api/";

  private String encoding = StandardCharsets.UTF_8.name();

  public String getBaseProcessorApiUrl() {
    return this.getBaseApiUrl() + "/processor";
  }

  public String getBaseJobUrl() {
    return baseApiUrl + "/job";
  }

  public String getBaseTaskUrl() {
    return baseApiUrl + "/task";
  }

  public String getBaseCacheUrl() {
    return baseApiUrl + "/cache";
  }

  public String getBaseTplUrl() {
    return baseApiUrl + "/tpl";
  }

  public String getBaseServiceUrl() {
    return baseApiUrl + "/service";
  }

  public String getBasePluginUrl() {
    return baseApiUrl + "/plugin";
  }

  public String getBaseJobUrl(String jobKey) {
    return this.getBaseJobUrl() + "/" + jobKey;
  }

  public String getJobUrlWithAction(String jobKey, String action) {
    return this.getBaseJobUrl(jobKey) + "/" + action;
  }

  public String getTaskList(String jobKey) {
    String url = this.getBaseTaskUrl();
    if (null != jobKey && jobKey.trim().length() > 0) {
      url += "/" + jobKey.trim();
    }
    return url + "/_list";
  }

  public String getClearTasksUrl(String jobKey) {
    String url = this.getBaseTaskUrl() + "/";
    if (null != jobKey && jobKey.trim().length() > 0)
      url += "/" + jobKey.trim();
    return url;
  }

  public String getDeleteCacheUrl(String cacheKey) {
    String url = this.getBaseTaskUrl() + "/";
    if (null != cacheKey && cacheKey.trim().length() > 0)
      url += "/" + cacheKey.trim();
    return url;
  }

  public String getServiceListUrl(String serviceName, String version, String methodName,
      int offset) {
    String url = this.getBaseServiceUrl();
    String action = "/_list";

    if (offset > 0) {
      action += "?offset=" + offset;
    }

    if (StringUtils.isEmpty(serviceName) && StringUtils.isEmpty(version)
        && StringUtils.isEmpty(methodName)) {
      return url + action;
    } else if (StringUtils.isNotEmpty(serviceName) && StringUtils.isEmpty(version)
        && StringUtils.isEmpty(methodName)) {
      return url + "/" + serviceName + action;
    } else if (StringUtils.isNotEmpty(serviceName) && StringUtils.isNotEmpty(version)
        && StringUtils.isEmpty(methodName)) {
      return url + "/" + serviceName + "/" + version + action;
    } else if (StringUtils.isNotEmpty(serviceName) && StringUtils.isNotEmpty(version)
        && StringUtils.isNotEmpty(methodName)) {
      return url + "/" + serviceName + "/" + version + "/" + methodName + action;
    }

    return url + action;
  }

  public String getMethodsListUrl(String serviceName, String version, String methodName,
      int offset) {
    String url = this.getBaseServiceUrl();
    String action = "/_list_methods";

    if (offset > 0) {
      action += "?offset=" + offset;
    }

    if (StringUtils.isEmpty(serviceName) && StringUtils.isEmpty(version)
        && StringUtils.isEmpty(methodName)) {
      return url + action;
    } else if (StringUtils.isNotEmpty(serviceName) && StringUtils.isEmpty(version)
        && StringUtils.isEmpty(methodName)) {
      return url + "/" + serviceName + action;
    } else if (StringUtils.isNotEmpty(serviceName) && StringUtils.isNotEmpty(version)
        && StringUtils.isEmpty(methodName)) {
      return url + "/" + serviceName + "/" + version + action;
    } else if (StringUtils.isNotEmpty(serviceName) && StringUtils.isNotEmpty(version)
        && StringUtils.isNotEmpty(methodName)) {
      return url + "/" + serviceName + "/" + version + "/" + methodName + action;
    }
    return url + action;
  }

  public String getCallServiceUrl(String serviceName, String version, String methodName) {
    String url = this.getBaseServiceUrl();
    String action = "_call";

    StringJoiner joiner = new StringJoiner("/");

    joiner.add(url);
    joiner.add(serviceName);
    joiner.add(version);
    joiner.add(methodName);
    joiner.add(action);

    return joiner.toString();
  }

  public String getTasksCountUrl(String jobKey) {
    String url = this.getBaseTaskUrl();
    if (null != jobKey && jobKey.trim().length() > 0)
      url += "/" + jobKey.trim();

    url += "/_count";

    return url;
  }

  public String getParseJobUrl(String jobKey, boolean temporary) {
    String url = this.getJobUrlWithAction(jobKey, "_pause");
    if (temporary) {
      url += "?temporary";
    }
    return url;
  }

  public String getStartJobUrl(String jobKey) {
    return this.getJobUrlWithAction(jobKey, "_start");
  }

  public String getResumeJobUrl(String jobKey, boolean temporary) {
    String url = this.getJobUrlWithAction(jobKey, "_resume");
    if (temporary) {
      url += "?temporary";
    }
    return url;
  }

  /**
   * the url for testing job
   *
   * @return
   */
  public String getTestJobUrl() {
    return this.getBaseJobUrl() + "/_test";
  }

  public String getTplUrl(String name) {
    return this.getBaseTplUrl() + "/" + name;
  }

  public String getJobTplListUrl() {
    return this.getBaseTplUrl() + "/_job_list";
  }

  public String getJobConfigValidationUrl() {
    return this.getBaseJobUrl() + "/_validate";
  }

  public String getJobListUrl() {
    return getJobListUrl(0, null);
  }

  public String getJobListUrl(String jobKey) {
    return getJobListUrl(0, jobKey);
  }

  public String getJobListUrl(int offset, String jobKey) {
    String url = this.getBaseJobUrl();
    if (null != jobKey) {
      url = url + "/" + jobKey;
    }

    url = url + "/_list";

    if (offset > 0) {
      url = UrlUtils.addUrlParam("offset", offset, url, encoding);
    }
    return url;
  }

  // #######################################
  // Processor Rest URL
  // #######################################

  public String getProcessorListUrl(String processorKey) {
    String url = this.getBaseProcessorApiUrl();
    if (StringUtils.isNotEmpty(processorKey)) {
      url += "/" + processorKey;
    }
    return url + "/_list";
  }

  public String getSingleProcessorUrl(String processorKey) {
    return this.getBaseProcessorApiUrl() + "/" + processorKey;
  }

  public String getProcessorTplListUrl() {
    return this.getBaseTplUrl() + "/_processor_list";
  }
  

  // #######################################
  // Plugin Rest URL
  // #######################################

  public String getLocalPluginListUrl() {
    return this.getBasePluginUrl() + "/_list_local";
  }

  public String getRegisteredPluginListUrl() {
    return this.getBasePluginUrl() + "/_list";
  }

  public String getRegisterPluginUrl(String pluginKey) {
    return this.getBasePluginUrl() + "/" + pluginKey;
  }

  /**
   * @return the baseApiUrl
   */
  public String getBaseApiUrl() {
    return baseApiUrl;
  }

  /**
   * @param baseApiUrl
   *          the baseApiUrl to set
   */
  public void setBaseApiUrl(String baseApiUrl) {
    this.baseApiUrl = baseApiUrl;
  }

  /**
   * @return the encoding
   */
  public String getEncoding() {
    return encoding;
  }

  /**
   * @param encoding
   *          the encoding to set
   */
  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }
}
