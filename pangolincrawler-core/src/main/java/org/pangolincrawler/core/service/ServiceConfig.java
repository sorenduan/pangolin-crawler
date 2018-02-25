package org.pangolincrawler.core.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.pangolincrawler.core.plugin.BaseConfig;
import org.pangolincrawler.core.plugin.ConfigKeyType;
import org.pangolincrawler.core.utils.JsonUtils;
import org.pangolincrawler.core.utils.SourceType;

public class ServiceConfig extends BaseConfig {

  private static final long serialVersionUID = 4115954236639114691L;

  public static class Method {
    private String methodName;
    private String description;
    private String inputDescription;
    private String outputDescription;
    private long processTimeout = -1;

    public Method() {
      super();
    }

    public String getMethodName() {
      return methodName;
    }

    public String getInputDescription() {
      return inputDescription;
    }

    public String getOutputDescription() {
      return outputDescription;
    }

    public long getProcessTimeout() {
      return processTimeout;
    }

    public String getDescription() {
      return description;
    }

    public void setMethodName(String methodName) {
      this.methodName = methodName;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public void setInputDescription(String inputDescription) {
      this.inputDescription = inputDescription;
    }

    public void setOutputDescription(String outputDescription) {
      this.outputDescription = outputDescription;
    }

    public void setProcessTimeout(long processTimeout) {
      this.processTimeout = processTimeout;
    }
  }

  private String serviceName;

  private List<Method> methods;

  private String description;

  private String version;

  private ServicePoJo.ServiceType type;

  private SourceType source;

  public ServiceConfig() {
    super();
    source = SourceType.ManualSourceType;
    methods = new ArrayList<>();
  }

  public static ServiceConfig buildFromJsonObject(JsonObject jsonObj) {

    ServiceConfig config = new ServiceConfig();

    if (jsonObj.has(ConfigKeyType.KEY_SERVICE_NAME.getName())) {
      config.serviceName = jsonObj.get(ConfigKeyType.KEY_SERVICE_NAME.getName()).getAsString();
    }

    if (jsonObj.has(ConfigKeyType.KEY_DESCRIPTION.getName())) {
      config.description = jsonObj.get(ConfigKeyType.KEY_DESCRIPTION.getName()).getAsString();
    }

    if (jsonObj.has(ConfigKeyType.KEY_DESCRIPTION.getName())) {
      config.description = jsonObj.get(ConfigKeyType.KEY_DESCRIPTION.getName()).getAsString();
    }

    if (jsonObj.has(ConfigKeyType.KEY_VERSION.getName())) {
      config.version = jsonObj.get(ConfigKeyType.KEY_VERSION.getName()).getAsString();
    }

    if (jsonObj.has(ConfigKeyType.KEY_SCRIPT_FILEPATH.getName())
        && jsonObj.get(ConfigKeyType.KEY_SCRIPT_FILEPATH.getName()).isJsonPrimitive()) {
      config.addOption(ConfigKeyType.KEY_SCRIPT_FILEPATH.getName(),
          jsonObj.get(ConfigKeyType.KEY_SCRIPT_FILEPATH.getName()).getAsString());
    }

    if (jsonObj.has(ConfigKeyType.KEY_TYPE.getName())) {
      String typeStr = jsonObj.get(ConfigKeyType.KEY_TYPE.getName()).getAsString();
      config.type = ServicePoJo.ServiceType.fromName(typeStr);
    }

    if (jsonObj.has(ConfigKeyType.KEY_EXCUTABLE.getName())
        && jsonObj.get(ConfigKeyType.KEY_EXCUTABLE.getName()).isJsonPrimitive()) {
      config.addOption(ConfigKeyType.KEY_EXCUTABLE.getName(),
          jsonObj.get(ConfigKeyType.KEY_EXCUTABLE.getName()).getAsString());
    }

    if (jsonObj.has(ConfigKeyType.KEY_SCRIPT_FILEPATH.getName())
        && jsonObj.get(ConfigKeyType.KEY_SCRIPT_FILEPATH.getName()).isJsonPrimitive()) {
      config.addOption(ConfigKeyType.KEY_SCRIPT_FILEPATH.getName(),
          jsonObj.get(ConfigKeyType.KEY_SCRIPT_FILEPATH.getName()).getAsString());
    }

    if (jsonObj.has(ConfigKeyType.KEY_JAVA_CLASS.getName())
        && jsonObj.get(ConfigKeyType.KEY_JAVA_CLASS.getName()).isJsonPrimitive()) {
      config.addOption(ConfigKeyType.KEY_JAVA_CLASS.getName(),
          jsonObj.get(ConfigKeyType.KEY_JAVA_CLASS.getName()).getAsString());
    }

    if (jsonObj.has(ConfigKeyType.KEY_JAVA_CLASSPATH.getName())
        && jsonObj.get(ConfigKeyType.KEY_JAVA_CLASSPATH.getName()).isJsonPrimitive()) {
      config.addOption(ConfigKeyType.KEY_JAVA_CLASSPATH.getName(),
          jsonObj.get(ConfigKeyType.KEY_JAVA_CLASSPATH.getName()).getAsString());
    }

    if (jsonObj.has(ConfigKeyType.KEY_METHODS.getName())
        && jsonObj.get(ConfigKeyType.KEY_METHODS.getName()).isJsonArray()) {
      fillMethods(config, jsonObj.get(ConfigKeyType.KEY_METHODS.getName()).getAsJsonArray());
    }

    return config;
  }

  private static void fillMethods(ServiceConfig config, JsonArray jsonArr) {
    if (null == jsonArr) {
      return;
    }

    Iterator<JsonElement> it = jsonArr.iterator();
    while (it.hasNext()) {
      JsonElement elem = it.next();
      if (elem.isJsonObject()) {
        Method m = buildMethod(elem.getAsJsonObject());
        config.methods.add(m);
      }
    }
  }

  private static Method buildMethod(JsonObject json) {
    Method m = new Method();

    // method name is a required option
    if (json.has(ConfigKeyType.KEY_METHOD_NAME.getName())
        && json.get(ConfigKeyType.KEY_METHOD_NAME.getName()).isJsonPrimitive()) {
      m.methodName = json.get(ConfigKeyType.KEY_METHOD_NAME.getName()).getAsString();
    } else {
      return null;
    }

    if (json.has(ConfigKeyType.KEY_METHOD_INPUT_DESCRIPTION.getName())
        && json.get(ConfigKeyType.KEY_METHOD_INPUT_DESCRIPTION.getName()).isJsonPrimitive()) {
      m.inputDescription = json.get(ConfigKeyType.KEY_METHOD_INPUT_DESCRIPTION.getName())
          .getAsString();
    }

    if (json.has(ConfigKeyType.KEY_METHOD_OUTPUT_DESCRIPTION.getName())
        && json.get(ConfigKeyType.KEY_METHOD_OUTPUT_DESCRIPTION.getName()).isJsonPrimitive()) {
      m.outputDescription = json.get(ConfigKeyType.KEY_METHOD_OUTPUT_DESCRIPTION.getName())
          .getAsString();
    }

    if (json.has(ConfigKeyType.KEY_METHOD_DESCRIPTION.getName())
        && json.get(ConfigKeyType.KEY_METHOD_DESCRIPTION.getName()).isJsonPrimitive()) {
      m.description = json.get(ConfigKeyType.KEY_METHOD_DESCRIPTION.getName()).getAsString();
    }

    if (json.has(ConfigKeyType.KEY_METHOD_PROCESS_TIMEOUT.getName())
        && json.get(ConfigKeyType.KEY_METHOD_PROCESS_TIMEOUT.getName()).isJsonPrimitive()) {
      m.processTimeout = json.get(ConfigKeyType.KEY_METHOD_PROCESS_TIMEOUT.getName()).getAsLong();
    } else {
      m.processTimeout = (int) ConfigKeyType.KEY_METHOD_PROCESS_TIMEOUT.getDefaultValue();
    }

    return m;
  }

  public static ServiceConfig buildFromJsonString(String jsonObj) {
    JsonObject json = JsonUtils.toJsonObject(jsonObj);
    return buildFromJsonObject(json);
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description
   *          the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  public String[] getMethodNames() {
    if (null == this.getMethods()) {
      return null;
    }
    String[] names = new String[this.getMethods().size()];

    int size = this.getMethods().size();
    for (int i = 0; i < size; i++) {
      names[i] = this.getMethods().get(i).getMethodName();
    }

    return names;
  }

  public ServicePoJo.ServiceType getType() {
    return type;
  }

  public String getClasspath() {
    return super.getOptionAsString(ConfigKeyType.KEY_JAVA_CLASSPATH);
  }

  /**
   * @return the serviceName
   */
  public String getServiceName() {
    return serviceName;
  }

  /**
   * @return the methods
   */
  public List<Method> getMethods() {
    return methods;
  }

  /**
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * @param serviceName
   *          the serviceName to set
   */
  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  /**
   * @param methods
   *          the methods to set
   */
  public void setMethods(List<Method> methods) {
    this.methods = methods;
  }

  /**
   * @param version
   *          the version to set
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * @param type
   *          the type to set
   */
  public void setType(ServicePoJo.ServiceType type) {
    this.type = type;
  }

  /**
   * @return the source
   */
  public SourceType getSource() {
    return source;
  }

  /**
   * @param source
   *          the source to set
   */
  public void setSource(SourceType source) {
    this.source = source;
  }

}