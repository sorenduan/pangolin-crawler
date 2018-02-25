package org.pangolincrawler.core.cache.impl;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.pangolincrawler.core.PangolinApplication;
import org.pangolincrawler.core.cache.CacheService;
import org.pangolincrawler.core.constants.Constants;
import org.pangolincrawler.core.utils.LoggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("cacheServiceImpl")
@EnableScheduling
@ConditionalOnProperty(name = Constants.PANGOLIN_CACHE_SERVICE_IMPL, havingValue = "org.pangolincrawler.core.cache.impl.EhCacheService")
public class EhCacheService implements CacheService {

  protected static Logger logger = LoggerFactory.getLogger(EhCacheService.class);

  private CacheManager ehCacheManager;

  private ResourcePools pool;

  private Cache<String, CacheEntry> defaultCache;

  private static final String CACHE_NAME = "default_cache";


  @Autowired
  Environment environment;

  private static class CacheEntry implements Serializable {
    private static final long serialVersionUID = 1L;
    Serializable value;
    long expiredAt;

    public boolean isExpired() {
      if (expiredAt > 0) {
        return System.currentTimeMillis() > expiredAt;
      }
      return false;
    }

    public CacheEntry(Serializable value, long expiredAt) {
      super();
      this.value = value;
      this.expiredAt = expiredAt;
    }

    public CacheEntry(Serializable value) {
      super();
      this.value = value;
      this.expiredAt = -1;
    }

  }

  public EhCacheService() {
    this.init();
  }

  @PostConstruct
  private void init() {

    long heapSize = environment.getProperty(
        Constants.PangolinPropertyType.PROPERTY_PANGOLIN_LOCALCACHE_HEAP_SIZE.getKey(), Long.class,
        Constants.PangolinPropertyType.PROPERTY_PANGOLIN_LOCALCACHE_HEAP_SIZE
            .getDefaultValue(Long.class));

    long offHeapSize = environment.getProperty(
        Constants.PangolinPropertyType.PROPERTY_PANGOLIN_LOCALCACHE_OFFHEAP_SIZE.getKey(),
        Long.class, Constants.PangolinPropertyType.PROPERTY_PANGOLIN_LOCALCACHE_OFFHEAP_SIZE
            .getDefaultValue(Long.class));

    long diskSize = environment.getProperty(
        Constants.PangolinPropertyType.PROPERTY_PANGOLIN_LOCALCACHE_DISK_SIZE.getKey(), Long.class,
        Constants.PangolinPropertyType.PROPERTY_PANGOLIN_LOCALCACHE_DISK_SIZE
            .getDefaultValue(Long.class));

    String diskDir = environment.getProperty(
        Constants.PangolinPropertyType.PROPERTY_PANGOLIN_LOCALCACHE_DISK_SIZE.getKey(),
        String.class, Constants.PangolinPropertyType.PROPERTY_PANGOLIN_LOCALCACHE_DISK_SIZE
            .getDefaultValue(String.class));

    if (null == diskDir && null != PangolinApplication.getConfig()) {
      diskDir = PangolinApplication.getConfig().getDefaultLocalCacheDiskDir();
    }

    ResourcePoolsBuilder poolsBuilder = ResourcePoolsBuilder.newResourcePoolsBuilder()
        .heap(heapSize, MemoryUnit.B).offheap(offHeapSize, MemoryUnit.B);

    CacheManagerBuilder<CacheManager> cacheManagerBuilder = CacheManagerBuilder
        .newCacheManagerBuilder();

    if (StringUtils.isNotBlank(diskDir)) {
      poolsBuilder.disk(diskSize, MemoryUnit.MB, true);
      cacheManagerBuilder.with(CacheManagerBuilder.persistence(new File(diskDir)));
    }

    this.pool = poolsBuilder.build();

    this.ehCacheManager = cacheManagerBuilder.build(true);

    this.defaultCache = this.getCache();
  }

  /**
   * in 2 seconds.
   */
  @Scheduled(fixedDelay = 2000)
  public void cleanExpiredCache() {
    Cache<String, CacheEntry> cache = defaultCache;

    Iterator<Cache.Entry<String, CacheEntry>> it = cache.iterator();
    int i = 0;
    while (it.hasNext()) {
      Cache.Entry<String, CacheEntry> entry = it.next();
      if (entry.getValue().isExpired()) {
        cache.remove(entry.getKey());
        i++;
      }
      if (i > 10) {
        break;
      }
    }
  }

  // private void initClearJob() {
  //
  // try {
  //
  // // TODO
  //
  // SchedulerFactory schfa = new StdSchedulerFactory();
  // Scheduler scheduler = schfa.getScheduler();
  //
  // JobDetail jobDetail = JobBuilder.newJob(EhCacheCleanerJob.class)
  // .withIdentity("ehcache_clearer").build();
  //
  // CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 */1 * * * ?");
  //
  // CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity("ehcache_clearer")
  // .withSchedule(scheduleBuilder).build();
  //
  // scheduler.scheduleJob(jobDetail, cronTrigger);
  //
  // } catch (Exception e) {
  // logger.error("Schedule ehcache_clearer Error !", e);
  // }
  //
  // }

  private Cache<String, CacheEntry> getCache() {
    return getCache(-1, null);
  }

  private Cache<String, CacheEntry> getCache(long num, TimeUnit unit) {

    String cacheName = buildCacheName(num, unit);

    Cache<String, CacheEntry> cache = null;
    cache = this.ehCacheManager.getCache(cacheName, String.class, CacheEntry.class);

    if (null == cache) {

      CacheConfigurationBuilder<String, CacheEntry> builder = CacheConfigurationBuilder
          .newCacheConfigurationBuilder(String.class, CacheEntry.class, pool);
      if (num < 0) {
        builder.withExpiry(Expirations.noExpiration());
      } else {
        builder.withExpiry(Expirations.timeToLiveExpiration(Duration.of(num, unit)));
      }

      cache = this.ehCacheManager.createCache(cacheName, builder.build());
    }
    return cache;
  }

  private String buildCacheName(long num, TimeUnit unit) {
    if (num > 0 && null != unit) {
      return "cache_" + num + "_" + unit.toString().toLowerCase();
    }
    return CACHE_NAME;
  }

  @Override
  public Serializable get(String key) {
    Cache<String, CacheEntry> cache = getCache();
    if (null != cache) {
      CacheEntry entry = cache.get(key);
      if (null != entry) {
        if (entry.isExpired()) {
          cache.remove(key);
        } else {
          return entry.value;
        }
      }
    }
    return null;
  }

  @Override
  public boolean put(String key, Serializable value) {
    return put(key, value, -1, null);
  }

  @Override
  public boolean put(String key, Serializable value, long expiredAt, TimeUnit unit) {
    Cache<String, CacheEntry> cache = getCache(-1, unit);
    if (null != cache) {
      if (expiredAt > 0 && null != unit) {
        cache.put(key,
            new CacheEntry(value, System.currentTimeMillis() + unit.toMillis(expiredAt)));
      } else {
        cache.put(key, new CacheEntry(value));
      }
      return true;
    }
    return false;
  }

  @Override
  public long increase(String key, long expireTime, TimeUnit unit) {
    return this.updateCounter(key, +1, expireTime, unit);
  }

  // not suitable for distrubution
  private synchronized long updateCounter(String key, long count, long expireTime, TimeUnit unit) {
    Serializable oldV = this.get(key);
    if (oldV == null) {
      this.put(key, count, expireTime, unit);
    } else if (oldV instanceof Integer || oldV instanceof Long) {
      long oldLong = (long) oldV;
      this.put(key, oldLong + count, expireTime, unit);
    }

    Serializable newValue = this.get(key);
    if (newValue instanceof Integer || newValue instanceof Long) {
      return (long) newValue;
    }
    return -1;
  }

  @Override
  public long decrease(String key, long expireTime, TimeUnit unit) {
    return this.updateCounter(key, -1, expireTime, unit);
  }

  @Override
  public void shutdown(String host) {
    this.ehCacheManager.close();
  }

  @Override
  public boolean delete(String key) {
    try {
      this.getCache().remove(key);
    } catch (Exception e) {
      LoggerUtils.error(this.getClass(), "delete cache for \"" + key + "\" error.", e);
      return false;
    }
    return true;
  }

}
