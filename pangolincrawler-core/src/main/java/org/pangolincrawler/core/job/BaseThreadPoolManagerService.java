package org.pangolincrawler.core.job;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.pangolincrawler.core.task.TaskProcessorContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;

public abstract class BaseThreadPoolManagerService implements InitializingBean {

  protected static Logger logger = LoggerFactory.getLogger(BaseThreadPoolManagerService.class);

  @Autowired
  protected JmsMessagingTemplate jmsMessagingTemplate;

  private ThreadPoolExecutor defaultThreadPool;

  private Map<String, SoftReference<ThreadPoolExecutor>> threadPoolUnshared;

  public BaseThreadPoolManagerService() {
    this.threadPoolUnshared = new HashMap<>();
  }

  private ThreadPoolExecutor getThreadPool(String taskKey) {
    if (this.threadPoolUnshared.containsKey(taskKey)) {
      SoftReference<ThreadPoolExecutor> pool = this.threadPoolUnshared.get(taskKey);
      if (null != pool.get()) {
        return pool.get();
      }
    }

    return this.defaultThreadPool;
  }

  protected void runTaskProcessor(TaskProcessorContainer processor) {

    ThreadPoolExecutor executor = this.getThreadPool(processor.getEntry().getJobKey());

    if (this.queueIsFull(executor)) {
      while (this.queueIsFull(executor)) {
        try {
          logger.warn("Task(" + processor.getEntry().getJobKey()
              + ") Queue is full waiting for scheduling!");
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          logger.error("Waiting for scheduling error!", e);
          Thread.currentThread().interrupt();
        }
      }
    }
    executor.execute(processor);
  }

  public boolean queueIsFull(ThreadPoolExecutor executor) {
    return executor.getQueue().size() >= this.getQueueSize();
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    this.defaultThreadPool = this.createThreadPool(this.getDefaultThreadNum());
    this.threadPoolUnshared = new HashMap<>();

  }

  protected ThreadPoolExecutor createThreadPool(int maxNum) {
    return new ThreadPoolExecutor(this.getDefaultThreadNum(), this.getDefaultThreadNum(), 60L,
        TimeUnit.SECONDS, new PriorityBlockingQueue<Runnable>(this.getQueueSize()));
  }

  private int getQueueSize() {
    return this.getDefaultThreadNum() * 2;
  }

  protected abstract int getDefaultThreadNum();
}
