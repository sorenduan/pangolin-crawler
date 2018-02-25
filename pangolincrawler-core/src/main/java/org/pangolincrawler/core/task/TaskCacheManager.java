package org.pangolincrawler.core.task;

import java.util.Calendar;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.pangolincrawler.core.cache.CacheManager;
import org.pangolincrawler.core.constants.Constants;
import org.pangolincrawler.core.job.LoopOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskCacheManager {

  private static Logger logger = LoggerFactory.getLogger(TaskCacheManager.class);

  private static final String TASK_COUNT_ONE_DAY_CACHE_PREFIX = "honycomb_task_counter_one_day_cache";

  private static final String URL_REPEAT_INTERVAL_CACHE_PREFIX = "honycomb_url_repeat_interval_cache";

  @Autowired
  private CacheManager cacheManager;

  public void increaseTaskCount(InnerTaskEntry task) {
    long timeLeftOfOneDay = calcOneDayLeftTime();
    if (timeLeftOfOneDay <= 0) {
      return;
    }

    String oneDayCacheKey = genOneDayCacheKey(task);
    this.cacheManager.increase(oneDayCacheKey, timeLeftOfOneDay, TimeUnit.MILLISECONDS);
  }

  public void decreaseTaskCount(InnerTaskEntry task) {
    long timeLeftOfOneDay = calcOneDayLeftTime();
    if (timeLeftOfOneDay <= 0) {
      return;
    }

    String oneDayCacheKey = genOneDayCacheKey(task);
    this.cacheManager.decrease(oneDayCacheKey, timeLeftOfOneDay, TimeUnit.MILLISECONDS);
  }

  private String genOneDayCacheKey(InnerTaskEntry task) {
    return new StringJoiner(Constants.CACHE_KEY_SEPERATOR).add(TASK_COUNT_ONE_DAY_CACHE_PREFIX)
        .add(task.getJobKey()).toString();
  }

  private String genRepeatCacheKey(InnerTaskEntry task) {
    return new StringJoiner(Constants.CACHE_KEY_SEPERATOR).add(URL_REPEAT_INTERVAL_CACHE_PREFIX)
        .add(StringUtils.trimToEmpty(task.getUrl())).toString();
  }

  public void markTaskForRepeatChecking(InnerTaskEntry task) {
    String cacheKey = genRepeatCacheKey(task);
    LoopOptions opts = task.getLoop();
    String url = task.getUrl();
    if (StringUtils.isNotBlank(url) && null != opts && opts.getRepetitionInvertalMs() > 0) {
      this.cacheManager.put(cacheKey, true, opts.getRepetitionInvertalMs(), TimeUnit.MILLISECONDS);
    }

  }

  public TaskManager.SendTaskResult checkTaskIsNotRepeat(InnerTaskEntry task) {

    TaskManager.SendTaskResult result = new TaskManager.SendTaskResult();

    String url = task.getUrl();
    LoopOptions opts = task.getLoop();
    if (StringUtils.isBlank(url) || null == opts) {
      result.setSuccess(true);
    } else {
      String cacheKey = genRepeatCacheKey(task);
      if (BooleanUtils.isTrue(this.cacheManager.getAsBoolean(cacheKey, false))) {

        if (logger.isDebugEnabled()) {
          logger.debug("Task repeat , the cache key is : {}", cacheKey);
        }

        result.setSuccess(false);
        result.setMessage("task repeat");
      } else {
        result.setSuccess(true);
      }
    }

    return result;
  }

  public void clearCacheForRepeatCheck(InnerTaskEntry task) {
    String cacheKey = genRepeatCacheKey(task);
    this.cacheManager.delete(cacheKey);
  }

  public TaskManager.SendTaskResult checkTaskCountLimtitionIsOk(InnerTaskEntry task) {

    TaskManager.SendTaskResult result = new TaskManager.SendTaskResult();

    if (task.getTaskCounter() == null || task.getTaskCounter().getOneDayLimition() < 0) {
      result.setSuccess(true);
      return result;
    }

    int limit = task.getTaskCounter().getOneDayLimition();

    String oneDayCacheKey = genOneDayCacheKey(task);

    int currCount = this.cacheManager.getAsInteger(oneDayCacheKey);
    if (currCount >= limit) {
      result.setMessage("Exceed the task onde day count limitition ( max is '" + limit + "').");
      result.setSuccess(false);
      return result;
    }

    result.setSuccess(true);
    return result;
  }

  private long calcOneDayLeftTime() {
    Calendar cal = Calendar.getInstance();

    cal.add(Calendar.DAY_OF_MONTH, 1);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.HOUR, 0);

    Calendar calNow = Calendar.getInstance();
    return cal.getTimeInMillis() - calNow.getTimeInMillis();
  }

}
