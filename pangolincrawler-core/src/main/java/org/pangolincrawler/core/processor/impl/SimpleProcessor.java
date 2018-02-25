package org.pangolincrawler.core.processor.impl;

import org.pangolincrawler.sdk.task.TaskProcessor;
import org.pangolincrawler.sdk.task.TaskProcessorException;
import org.pangolincrawler.sdk.utils.LoggerUtils;

/**
 * Just echo payload string.
 */
public class SimpleProcessor extends TaskProcessor {

  private static final long serialVersionUID = 6763515757756911955L;

  @Override
  public String process(String payload) throws TaskProcessorException {
    LoggerUtils.info(payload, this.getClass());
    return payload;
  }

}
