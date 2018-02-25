package org.pangolincrawler.core.utils;

import com.google.gson.JsonObject;

import org.pangolincrawler.core.processor.ProcessorConfig;
import org.pangolincrawler.core.processor.ProcessorPoJo;
import org.pangolincrawler.core.processor.ScriptProcessorContainer;

public final class ProcessorUtils {

  private ProcessorUtils() {
  }

  public static ProcessorPoJo createProcessorPoJoFromConfig(ProcessorConfig config) {
    ProcessorPoJo processorPojo = new ProcessorPoJo();

    processorPojo.setProcessorKey(config.getProcessorKey());
    processorPojo.setDescription(config.getDescription());
    processorPojo.setSource(config.getSource().toString());

    if (ProcessorPoJo.ProcessorType.SCRIPT.equals(config.getType())) {
      processorPojo.setProcessorClass(ScriptProcessorContainer.class.getName());
    } else if (ProcessorPoJo.ProcessorType.JAVA.equals(config.getType())) {
      processorPojo.setProcessorClass(config.getClassName());
    }

    JsonObject json = JsonUtils.toJsonObject(config.getOptions());
    processorPojo.setAttributeJson(json.toString());

    return processorPojo;
  }

}
