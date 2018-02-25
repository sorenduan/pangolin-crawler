package org.pangolincrawler.core.plugin;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class BaseConfig implements Serializable {

  private static final long serialVersionUID = 1L;

  private HashMap<String, Object> options;

  public BaseConfig() {
    this.options = new HashMap<>();
  }

  public void setOptions(HashMap<String, Object> options) {
    this.options = options;
  }

  public void addOption(String name, Object value) {
    this.options.put(name, value);
  }

  public void addOption(ConfigKeyType type, Object value) {
    this.options.put(type.getName(), value);
  }

  public Map<String, Object> getOptions() {
    return options;
  }

  public String getOptionAsString(ConfigKeyType k1, ConfigKeyType k2) {
    Map<String, Object> v1 = getOptionAsMap(k1);
    if (null != v1) {
      Object v2 = v1.get(k2.getName());
      if (v2 instanceof String) {
        return (String) v2;
      }
      return String.valueOf(v2);
    }
    return null;
  }

  public boolean getOptionAsBoolean(ConfigKeyType k1, ConfigKeyType k2, boolean defaultValue) {
    Map<String, Object> v1 = getOptionAsMap(k1);
    if (null != v1) {
      Object v2 = v1.get(k2.getName());
      if (v2 instanceof Boolean) {
        return (Boolean) v2;
      }
      return defaultValue;
    }
    return defaultValue;
  }

  public boolean hasKeys(ConfigKeyType k1, ConfigKeyType k2) {
    Map<String, Object> v1 = getOptionAsMap(k1);
    if (null == v1) {
      return false;
    } else {
      Object v2 = v1.get(k2.getName());
      if (null == v2) {
        return false;
      }
    }
    return true;
  }

  public Integer getOptionAsInteger(ConfigKeyType k1, ConfigKeyType k2) {
    Map<String, Object> v1 = getOptionAsMap(k1);
    if (null != v1) {
      Object v2 = v1.get(k2.getName());
      if (v2 instanceof Integer) {
        return (Integer) v2;
      } else {
        return NumberUtils.toInt(String.valueOf(v2));
      }
    }
    return null;
  }

  public void addOptions(ConfigKeyType k1, ConfigKeyType k2, Object value) {
    Map<String, Object> k1Values = this.getOptionAsMap(k1);
    if (null == k1Values) {
      k1Values = new LinkedHashMap<>();
      this.addOptionWIthMap(k1.getName(), k1Values);
    }
    k1Values.put(k2.getName(), value);
  }

  public void addOptions(ConfigKeyType k1, String k2, String value) {
    Map<String, Object> k1Values = this.getOptionAsMap(k1);
    if (null == k1Values) {
      k1Values = new LinkedHashMap<>();
      this.addOptionWIthMap(k1.getName(), k1Values);
    }

    k1Values.put(k2, value);
  }

  public void addOptionWIthMap(String name, Map<String, Object> value) {
    this.options.put(name, value);
  }

  public boolean getOptionAsBoolean(ConfigKeyType key) {
    String v = this.getOptionAsString(key);
    return BooleanUtils.toBoolean(v);
  }

  @SuppressWarnings("unchecked")
  public Map<String, Object> getOptionAsMap(ConfigKeyType key) {
    Object v = this.getOption(key);
    if (v instanceof Map) {
      return (Map<String, Object>) v;
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public List<String> getOptionAsList(ConfigKeyType key) {
    Object v = this.getOption(key);
    if (v instanceof List) {
      return (List<String>) v;
    }
    return Collections.emptyList();
  }

  public Object getOption(ConfigKeyType key) {
    return this.options.get(key.getName());
  }

  public String getOptionAsString(ConfigKeyType key) {
    Object v = getOption(key);
    if (null != v) {
      return String.valueOf(v);
    }
    return null;
  }

  public Map<String, Object> getOutputFileOptions() {
    return getOptionAsMap(ConfigKeyType.KEY_FILE_OUTPUT);
  }

}