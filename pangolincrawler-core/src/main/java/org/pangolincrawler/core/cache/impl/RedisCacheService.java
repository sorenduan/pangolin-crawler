package org.pangolincrawler.core.cache.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.pangolincrawler.core.cache.CacheService;
import org.pangolincrawler.core.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

@Component("cacheServiceImpl")
@ConditionalOnProperty(name = Constants.PANGOLIN_CACHE_SERVICE_IMPL, havingValue = "org.pangolincrawler.core.cache.impl.RedisCacheService")
public class RedisCacheService implements CacheService {

  protected static Logger logger = LoggerFactory.getLogger(RedisCacheService.class);

  private JedisCluster redis = null;

  @Autowired
  Environment environment;

  public RedisCacheService() {

  }

  @PostConstruct
  public void init() {
    String services = environment.getProperty(Constants.PROPERTY_PANGOLIN_CACHE_REDIS_SERVERS);
    Set<HostAndPort> serverNodes = parseServers(services);
    redis = new JedisCluster(serverNodes);
  }

  private Set<HostAndPort> parseServers(String services) {
    Set<HostAndPort> set = new HashSet<>();
    if (StringUtils.isNotBlank(services)) {
      String[] parts1 = StringUtils.split(services, ",");
      if (null != parts1 && parts1.length > 0) {
        for (String eachHostAndPort : parts1) {
          String[] eachHostAndPortParts = StringUtils.split(eachHostAndPort, ":");
          if (eachHostAndPortParts.length >= 2) {
            String host = StringUtils.trimToEmpty(eachHostAndPortParts[0]);
            int port = NumberUtils.toInt(eachHostAndPortParts[1]);
            set.add(new HostAndPort(host, port));
          }
        }
      }
    }
    return set;
  }

  @Override
  public Serializable get(String key) {
    byte[] keyBytes = SerializationUtils.serialize(key);
    byte[] valueBytes = this.redis.get(keyBytes);
    return SerializationUtils.deserialize(valueBytes);
  }

  @Override
  public boolean put(String key, Serializable value) {
    byte[] keyBytes = SerializationUtils.serialize(key);
    byte[] valueBytes = SerializationUtils.serialize(key);
    this.redis.set(keyBytes, valueBytes);
    return true;
  }

  @Override
  public boolean put(String key, Serializable value, long expireTime, TimeUnit unit) {
    byte[] keyBytes = SerializationUtils.serialize(key);
    byte[] valueBytes = SerializationUtils.serialize(key);
    this.redis.set(keyBytes, valueBytes);
    this.redis.expire(keyBytes, (int) unit.toSeconds(expireTime));
    return true;
  }

  @Override
  public boolean delete(String key) {
    byte[] keyBytes = SerializationUtils.serialize(key);
    this.redis.del(keyBytes);
    return true;
  }

  @Override
  public long increase(String key, long expireTime, TimeUnit unit) {
    byte[] keyBytes = SerializationUtils.serialize(key);
    long ret = this.redis.incrBy(key, 1);
    this.redis.expire(keyBytes, (int) unit.toSeconds(expireTime));
    return ret;
  }

  @Override
  public long decrease(String key, long expireTime, TimeUnit unit) {
    byte[] keyBytes = SerializationUtils.serialize(key);
    long ret = this.redis.decrBy(keyBytes, 1);
    this.redis.expire(keyBytes, (int) unit.toSeconds(expireTime));
    return ret;
  }

  @Override
  public void shutdown(String host) {
    if (null != redis) {
      try {
        redis.close();
      } catch (IOException e) {
        logger.error("close jedis error", e);
      }
    }
  }

}
