package org.pangolincrawler.core.cache;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public interface CacheService {

  public Serializable get(String key);

  public boolean put(String key, Serializable value);

  public boolean put(String key, Serializable value, long expireTime, TimeUnit unit);

  public boolean delete(String key);

  public long increase(String key, long expireTime, TimeUnit unit);

  public long decrease(String key, long expireTime, TimeUnit unit);

  public void shutdown(String host);

}
