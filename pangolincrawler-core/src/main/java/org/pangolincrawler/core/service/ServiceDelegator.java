package org.pangolincrawler.core.service;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.pangolincrawler.core.PangolinApplication;
import org.pangolincrawler.core.common.ClassLoaderManager;
import org.pangolincrawler.core.constants.Constants;
import org.pangolincrawler.core.plugin.ConfigKeyType;
import org.pangolincrawler.core.service.ServicePoJo.ServiceType;
import org.pangolincrawler.core.utils.JsonUtils;
import org.pangolincrawler.core.utils.LoggerUtils;
import org.pangolincrawler.core.utils.SourceType;
import org.pangolincrawler.sdk.PublicExternalService;
import org.springframework.util.Base64Utils;

public class ServiceDelegator {

  private static class ServiceFutureTask implements Callable<String> {

    private PublicExternalService service;
    private String input;
    private String methodName;
    private String version;

    public ServiceFutureTask(PublicExternalService service, String input, String methodName,
        String version) {
      super();
      this.service = service;
      this.input = input;
      this.methodName = methodName;
      this.version = version;
    }

    @Override
    public String call() throws Exception {
      return this.service.call(methodName, input);
    }
  }

  private static ExecutorService threadPoolExecutor = Executors
      .newFixedThreadPool(Constants.DEFAULT_SERVICE_THREAD_POOL_SIZE);

  private PublicExternalService javaServiceImpl;

  private long timeout;

  private String executable;

  private String scriptPath;

  private String serviceUrl;

  private String serviceName;

  private String methodName;

  private String version;

  private String implClassName;

  private String implClasspath;

  private ServicePoJo.ServiceType type;

  private SourceType source;

  public static ServiceDelegator build(ServicePoJo service, ServiceMethodPoJo method) {
    ServiceDelegator dalegator = new ServiceDelegator();

    dalegator.serviceName = service.getServiceName();
    dalegator.methodName = method.getMethodName();
    dalegator.version = service.getVersion();
    dalegator.type = ServicePoJo.ServiceType.fromName(service.getType());
    dalegator.source = SourceType.buildStringString(service.getSource());

    String jsonStr = service.getAttributeJson();

    JsonObject json = JsonUtils.toJsonObject(jsonStr);

    if (json.has(ConfigKeyType.KEY_JAVA_CLASS.getName())
        && json.get(ConfigKeyType.KEY_JAVA_CLASS.getName()).isJsonPrimitive()) {
      dalegator.implClassName = json.get(ConfigKeyType.KEY_JAVA_CLASS.getName()).getAsString();
    }

    if (json.has(ConfigKeyType.KEY_EXCUTABLE.getName())
        && json.get(ConfigKeyType.KEY_EXCUTABLE.getName()).isJsonPrimitive()) {
      dalegator.executable = json.get(ConfigKeyType.KEY_EXCUTABLE.getName()).getAsString();
    }

    if (json.has(ConfigKeyType.KEY_SCRIPT_FILEPATH.getName())
        && json.get(ConfigKeyType.KEY_SCRIPT_FILEPATH.getName()).isJsonPrimitive()) {
      dalegator.scriptPath = json.get(ConfigKeyType.KEY_SCRIPT_FILEPATH.getName()).getAsString();
    }

    if (json.has(ConfigKeyType.KEY_TYPE.getName())
        && json.get(ConfigKeyType.KEY_TYPE.getName()).isJsonPrimitive()) {
      dalegator.type = ServiceType
          .fromName(json.get(ConfigKeyType.KEY_TYPE.getName()).getAsString());
    }

    if (json.has(ConfigKeyType.KEY_JAVA_CLASSPATH.getName())
        && json.get(ConfigKeyType.KEY_JAVA_CLASSPATH.getName()).isJsonPrimitive()) {
      dalegator.implClasspath = json.get(ConfigKeyType.KEY_JAVA_CLASSPATH.getName()).getAsString();
    }
    if (json.has(ConfigKeyType.KEY_METHOD_PROCESS_TIMEOUT.getName())
        && json.get(ConfigKeyType.KEY_METHOD_PROCESS_TIMEOUT.getName()).isJsonPrimitive()) {
      dalegator.timeout = json.get(ConfigKeyType.KEY_METHOD_PROCESS_TIMEOUT.getName()).getAsLong();
    }

    return dalegator;
  }

  public String run(String input) {

    try {
      switch (this.type) {
      case JAVA:
        return this.runAsJava(input);
      case SCRIPT:
        return this.runAsScript(input);
      default:
        break;
      }
    } catch (Exception e) {
      LoggerUtils.error(this.getClass(), "Run service error", e);
      return "System error!";
    }
    return null;
  }

  private String runAsJava(String input) {
    PublicExternalService service = loadService();

    if (this.timeout > 0) {
      FutureTask<String> task = new FutureTask<>(
          new ServiceFutureTask(service, this.methodName, version, input));
      threadPoolExecutor.submit(task);
      try {
        task.wait(this.timeout);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new PublicServiceException("run service time out", e);
      }
    } else {
      return service.call(methodName, input);
    }
    return null;
  }

  private PublicExternalService loadService() {
    if (null != this.javaServiceImpl) {
      return this.javaServiceImpl;
    }

    PublicExternalService service = this.getServiceImpl();
    if (null == service) {
      try {
        service = loadClassFromClasspath();
      } catch (Exception e) {
        LoggerUtils.error(this.getClass(),
            "The service impl '(" + this.implClassName + ")' is not existed.", e);
        throw new PublicServiceException(e);
      }
    }
    this.javaServiceImpl = service;
    return service;
  }

  private boolean checkScript() {
    File file = new File(this.scriptPath);
    if (!file.exists()) {
      throw new PublicServiceException(this.scriptPath + " is not existed.");
    }

    return true;
  }

  private void checkExecutable() {

    File file = new File(this.executable);
    if (!file.exists()) {
      throw new PublicServiceException(executable + " is not existed.");
    }

    if (!file.canExecute()) {
      throw new PublicServiceException(executable + " is not executable.");
    }

  }

  private String taskToScriptArgs(String input) throws UnsupportedEncodingException {
    byte[] args = input.getBytes(Constants.DEFAULT_CHARSET);
    return Base64Utils.encodeToString(args);
  }

  protected String runAsScript(String input) {

    Process p = null;
    try {
      checkExecutable();
      checkScript();

      String args = taskToScriptArgs(input);
      ProcessBuilder pb = new ProcessBuilder(this.executable, this.scriptPath, args);
      p = pb.start();

      if (this.timeout > 0) {
        p.waitFor(this.timeout, TimeUnit.MILLISECONDS);
      } else {
        p.waitFor();
      }

      try (InputStream in = p.getInputStream()) {
        InputStreamReader reader = new InputStreamReader(in);
        return IOUtils.toString(reader);
      }

    } catch (IOException | InterruptedException e) {
      throw new PublicServiceException("process script processor error", e);
    } finally {
      if (null != p) {
        p.destroy();
      }
    }
  }

  private PublicExternalService loadClassFromClasspath() {

    ClassLoaderManager classLoaderManager = PangolinApplication
        .getSystemService(ClassLoaderManager.class);

    String pluginKey = null;
    if (null != this.source && this.source.isPluglinSource()) {
      pluginKey = this.source.getExtra();
    }

    return classLoaderManager.loadServiceInstance(this.serviceName, this.version, pluginKey,
        this.implClassName);

  }

  private PublicExternalService getServiceImpl() {
    try {
      Object service = Class.forName(this.implClassName).newInstance();
      if (service instanceof PublicExternalService) {
        return (PublicExternalService) service;
      }

    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      LoggerUtils.error(this.getClass(), "", e);
    }
    return null;
  }

}
