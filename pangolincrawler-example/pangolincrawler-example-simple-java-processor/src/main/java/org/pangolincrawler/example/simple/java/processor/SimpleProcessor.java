package org.pangolincrawler.example.simple.java.processor;

import org.pangolincrawler.sdk.task.TaskProcessor;
import org.pangolincrawler.sdk.task.TaskProcessorException;
import org.pangolincrawler.sdk.utils.LoggerUtils;

public class SimpleProcessor extends TaskProcessor {

  private static final long serialVersionUID = 3603382926617515277L;

  @Override
  public String process(String payload) throws TaskProcessorException {

    print("TaskId:" + super.getTask().getTaskId());
    print("Target Url:" + super.getTask().getUrl());
    print("Processor Context:" + super.getTask().getProcessorContext());

    print("Payload :" + payload);
    //print("HTML :" + super.getHtml().replaceAll("\n", ""));

    return "Hello Processor!";
  }

  private void print(String message) {
    LoggerUtils.info(message, this.getClass());
  }

}
