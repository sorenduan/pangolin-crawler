package org.pangolincrawler.core.cache;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class CacheManager implements CacheService {

  @Autowired
  @Qualifier("cacheServiceImpl")
  private CacheService service;

  
  @Override
  public Serializable get(String key) {
    if (key != null) {
      return service.get(key);
    } else {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  public <T> T get(String key, Class<T> clazz, T defaultValue) {
    if (null == clazz) {
      return defaultValue;
    }

    Object v = this.get(key);
    if (null != v) {
      return (T) v;
    }
    return defaultValue;
  }

  public Long getAsLong(String key) {
    Object v = this.get(key);
    if (v instanceof Long) {
      return (Long) v;
    }
    return null;
  }

  public String getAsString(String key) {
    Object v = this.get(key);
    if (v instanceof String) {
      return (String) v;
    }
    return null;
  }

  public Integer getAsInteger(String key) {
    Object v = this.get(key);
    if (v instanceof Integer) {
      return (Integer) v;
    }
    return null;
  }

  public Boolean getAsBoolean(String key, boolean defaultValue) {
    Object v = this.get(key);
    if (null != v && v instanceof Boolean) {
      return (Boolean) v;
    }
    return defaultValue;
  }

  @Override
  public boolean put(String key, Serializable value) {
    if (value != null) {
      return service.put(key, value);
    } else {
      return false;
    }
  }

  @Override
  public boolean put(String key, Serializable value, long expireTime, TimeUnit unit) {
    if (value != null) {
      return service.put(key, value, expireTime, unit);
    } else {
      return false;
    }
  }

  @Override
  public long increase(String key, long expireTime, TimeUnit unit) {
    return this.service.increase(key, expireTime, unit);
  }

  @Override
  public long decrease(String key, long expireTime, TimeUnit unit) {
    return this.service.decrease(key, expireTime, unit);
  }

  @Override
  public void shutdown(String host) {
    this.service.shutdown(host);
  }

  @Override
  public boolean delete(String key) {
    return this.service.delete(key);
  }

}
