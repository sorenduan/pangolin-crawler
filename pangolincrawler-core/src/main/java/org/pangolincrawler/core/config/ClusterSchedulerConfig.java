package org.pangolincrawler.core.config;

import java.util.Properties;

import org.pangolincrawler.core.PangolinApplication;
import org.pangolincrawler.core.utils.LoggerUtils;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClusterSchedulerConfig {

  @Bean
  public SchedulerFactory cronSchedulerFactory() {
    StdSchedulerFactory factory = new StdSchedulerFactory();
    Properties props = null;
    if (null != PangolinApplication.getConfig()) {
      props = PangolinApplication.getConfig().getConfigProperties();
    }
    if (null != props) {
      try {
        factory.initialize(props);
      } catch (SchedulerException e) {
        LoggerUtils.error(this.getClass(), e);
      }
    }
    return factory;
  }

  @Bean("cluster-scheduler")
  public Scheduler cronScheduler(SchedulerFactory factory) {
    try {

      Scheduler scheduler = factory.getScheduler();

      if (!scheduler.isStarted()) {
        scheduler.startDelayed(1);
      }
      return scheduler;

    } catch (SchedulerException e) {
      LoggerUtils.error(this.getClass(), e);
    }

    return null;
  }

}