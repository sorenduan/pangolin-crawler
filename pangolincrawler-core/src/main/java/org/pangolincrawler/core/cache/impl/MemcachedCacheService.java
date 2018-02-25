package org.pangolincrawler.core.cache.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.pangolincrawler.core.PangolinApplication;
import org.pangolincrawler.core.cache.CacheService;
import org.pangolincrawler.core.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * https://github.com/killme2008/xmemcached/
 */
@Component("cacheServiceImpl")
@ConditionalOnProperty(name = Constants.PANGOLIN_CACHE_SERVICE_IMPL, havingValue = "org.pangolincrawler.core.cache.impl.MemcachedCacheService")
public class MemcachedCacheService implements CacheService {

  protected static Logger logger = LoggerFactory.getLogger(MemcachedCacheService.class);

  private MemcachedClient memcachedClient = null;

  @Autowired
  Environment environment;

  public MemcachedCacheService() {
  }

  @PostConstruct
  public void init() {

    String services = environment.getProperty(Constants.PROPERTY_PANGOLIN_CACHE_MEMCACHED_SERVERS);

    MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(services));
    builder.setSessionLocator(new KetamaMemcachedSessionLocator());
    try {
      memcachedClient = builder.build();
    } catch (IOException e) {
      logger.error("Instance memcached client error.", e);
    }
  }

  @Override
  public Serializable get(String key) {
    try {
      return this.memcachedClient.get(key);
    } catch (TimeoutException | InterruptedException | MemcachedException e) {
      logger.error("get value error.", e);
    }
    return null;
  }

  @Override
  public boolean put(String key, Serializable value) {
    try {
      return memcachedClient.set(key, 0, value);
    } catch (TimeoutException | InterruptedException | MemcachedException e) {
      logger.error("put value into memcached error.", e);
    }
    return false;
  }

  @Override
  public boolean delete(String key) {
    try {
      return memcachedClient.delete(key);
    } catch (TimeoutException | InterruptedException | MemcachedException e) {
      logger.error("delete value from memcached error.", e);
    }
    return false;
  }

  @Override
  public boolean put(String key, Serializable value, long expireTime, TimeUnit unit) {
    try {
      return memcachedClient.set(key, (int) unit.toSeconds(expireTime), value);
    } catch (TimeoutException | InterruptedException | MemcachedException e) {
      logger.error("set value into memcached error.", e);
    }
    return false;
  }

  @Override
  public long increase(String key, long expireTime, TimeUnit unit) {
    try {
      return memcachedClient.incr(key, 1, 0, (int) unit.toSeconds(expireTime),
          (int) unit.toSeconds(expireTime));
    } catch (TimeoutException | InterruptedException | MemcachedException e) {
      logger.error("increase error.", e);
    }
    return 0L;
  }

  @Override
  public long decrease(String key, long expireTime, TimeUnit unit) {
    try {
      return memcachedClient.decr(key, 1, 0, (int) unit.toSeconds(expireTime),
          (int) unit.toSeconds(expireTime));
    } catch (TimeoutException | InterruptedException | MemcachedException e) {
      logger.error("decrease error.", e);
    }
    return 0;
  }

  @Override
  public void shutdown(String host) {
    try {
      if (null != memcachedClient) {
        memcachedClient.shutdown();
      }
    } catch (IOException e) {
      logger.error("shutdown error.", e);
    }
  }

}
