package org.pangolincrawler.core.task.rate;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.pangolincrawler.core.cache.CacheManager;
import org.pangolincrawler.core.task.InnerTaskEntry;
import org.pangolincrawler.core.utils.LoggerUtils;
import org.pangolincrawler.core.utils.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RateManager {

  private static final String CACHE_PREFIX = "pangolin_rate_cache_key";
  private static final String CACHE_KEY_SP = "_";
  private static final int MAX_SIZE = 10000;

  @Autowired
  private CacheManager cacheManager;

  private TreeMap<Long, InnerTaskEntry> waitingTaskPool;

  public RateManager() {
    waitingTaskPool = new TreeMap<>();
  }

  public void incrRate(InnerTaskEntry task) {
    RateExpressions expressions = task.getRates();
    String policyKey = this.getPolicyTarget(expressions.getPolicy(), task);
    if (!expressions.isEmpty()) {
      expressions.getExpressionList().forEach(expr -> this.incrRateWithOne(policyKey, expr));
    }
  }

  public void decrRate(InnerTaskEntry task) {
    RateExpressions expressions = task.getRates();
    String policyKey = this.getPolicyTarget(expressions.getPolicy(), task);
    if (expressions.isEmpty()) {
      expressions.getExpressionList().forEach(expr -> this.decrRateWithOne(policyKey, expr));
    }
  }

  public synchronized void waitForSchedule(InnerTaskEntry task) {
    if (task.getRates().isEmpty()) {
      return;
    }
    long tryToScheduleTime = System.currentTimeMillis() + task.getRates().getMinIntervalMillis();

    saveTask(tryToScheduleTime, task);
  }

  private void saveTask(long time, InnerTaskEntry task) {
    if (this.waitingTaskPool.size() < MAX_SIZE) {
      this.waitingTaskPool.put(time, task);
    } else {
      LoggerUtils.warn("waiting task pool is full", this.getClass());
    }
  }

  private void incrRateWithOne(String policyKey, RateExpression expr) {
    String cacheKey = this.buildRateCacheKey(policyKey, expr);
    this.cacheManager.increase(cacheKey, expr.getUnitNum(), expr.getUnit().toTimeUnit());
    this.cacheManager.increase(cacheKey, expr.getUnitNum(), expr.getUnit().toTimeUnit());
    this.cacheManager.increase(cacheKey, expr.getUnitNum(), expr.getUnit().toTimeUnit());
    this.cacheManager.increase(cacheKey, expr.getUnitNum(), expr.getUnit().toTimeUnit());
    Object k = this.cacheManager.get(cacheKey);
    
    System.out.println(k);
  }

  private void decrRateWithOne(String policyKey, RateExpression expr) {
    String cacheKey = this.buildRateCacheKey(policyKey, expr);
    this.cacheManager.decrease(cacheKey, expr.getUnitNum(), expr.getUnit().toTimeUnit());

  }

  public boolean canSchedule(InnerTaskEntry task) {
    RateExpressions expressions = task.getRates();
    if (expressions.isEmpty()) {
      return true;
    }

    RateExpressions.Policy policyType = expressions.getPolicy();

    String policyKey = getPolicyTarget(policyType, task);

    for (RateExpression each : expressions.getExpressionList()) {
      if (!this.canSchedule(policyKey, each)) {
        return false;
      }
    }
    return true;
  }

  public String getPolicyTarget(RateExpressions.Policy policyType, InnerTaskEntry task) {

    if (RateExpressions.Policy.BY_HOST.equals(policyType)) {
      return UrlUtils.parseHostFromUrl(task.getUrl());
    }

    return null;
  }

  public synchronized void removeFromWaitingPool(List<Long> keys) {
    if (CollectionUtils.isNotEmpty(keys)) {
      keys.forEach(this.waitingTaskPool::remove);
    }
  }

  private String buildRateCacheKey(String policyKey, RateExpression expression) {
    return StringUtils.joinWith(CACHE_KEY_SP, CACHE_PREFIX, policyKey, expression.getUnitNum(),
        expression.getUnit().getUnitString());
  }

  private boolean canSchedule(String policyKey, RateExpression expression) {
    String cacheKey = this.buildRateCacheKey(policyKey, expression);
    long c = this.cacheManager.get(cacheKey, Long.class, 0L);
    return c < expression.getLimitNum();
  }


  public Map<Long, InnerTaskEntry> getWaitingTaskPool() {
    return waitingTaskPool;
  }

}
