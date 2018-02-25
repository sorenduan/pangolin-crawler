package org.pangolincrawler.core.task;

import com.google.gson.JsonObject;

import org.pangolincrawler.core.constants.Constants;
import org.pangolincrawler.core.db.JobPoJo;
import org.pangolincrawler.core.job.BaseThreadPoolManagerService;
import org.pangolincrawler.core.job.JobManager;
import org.pangolincrawler.core.job.TaskStatisticManager;
import org.pangolincrawler.core.plugin.ConfigKeyType;
import org.pangolincrawler.core.processor.ProcessorManager;
import org.pangolincrawler.core.processor.ProcessorPoJo;
import org.pangolincrawler.core.processor.ScriptProcessorContainer;
import org.pangolincrawler.core.utils.JsonUtils;
import org.pangolincrawler.core.utils.LoggerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class TaskMessageConsumer extends BaseThreadPoolManagerService {

  @Value(Constants.PLACEHOLDER_PANGOLIN_WORKER_THREAD_DEFAULT_NUM)
  private int defaultThreadNum;

  @Autowired
  private ProcessorManager processorManager;

  @Autowired
  private TaskStatisticManager taskStatisticManager;

  @Autowired
  private JobManager jobManager;

  @JmsListener(destination = Constants.QUEUE_NAME_OF_TASK, containerFactory = "mqFactory")
  public void receive(InnerTaskEntry task) {
    try {
      TaskProcessorContainer processor = processorManager
          .createTaskProcessorContainerFromTaskEntry(task);
      if (processor.getProcessor() instanceof ScriptProcessorContainer) {
        fillScriptProcessor(task, (ScriptProcessorContainer) processor.getProcessor());
      }

      super.runTaskProcessor(processor);
    } catch (Exception e) {
      LoggerUtils.error(BaseThreadPoolManagerService.class, "receive task error", e);
      taskStatisticManager.makringTaskFail(task, e.getLocalizedMessage());
    }
  }

  private void fillScriptProcessor(InnerTaskEntry task, ScriptProcessorContainer processor) {
    JobPoJo job = jobManager.getJobByJobKey(task.getJobKey());
    if (null == job) {
      return;
    }
    ProcessorPoJo processorPoJo = processorManager.getProcessorByKey(job.getProcessorKey());
    if (null == processorPoJo) {
      return;
    }

    String jsonStr = processorPoJo.getAttributeJson();
    JsonObject json = JsonUtils.toJsonObject(jsonStr);

    if (json.has(ConfigKeyType.KEY_EXCUTABLE.getName())
        && json.get(ConfigKeyType.KEY_EXCUTABLE.getName()).isJsonPrimitive()) {
      processor.setExecutable(json.get(ConfigKeyType.KEY_EXCUTABLE.getName()).getAsString());
    }

    if (json.has(ConfigKeyType.KEY_SCRIPT_FILEPATH.getName())
        && json.get(ConfigKeyType.KEY_SCRIPT_FILEPATH.getName()).isJsonPrimitive()) {
      processor.setProcessorScriptFilepath(
          json.get(ConfigKeyType.KEY_SCRIPT_FILEPATH.getName()).getAsString());
    }

    if (json.has(ConfigKeyType.KEY_PROCESS_TIMEOUT.getName())
        && json.get(ConfigKeyType.KEY_PROCESS_TIMEOUT.getName()).isJsonPrimitive()) {
      processor
          .setProcessTimeout(json.get(ConfigKeyType.KEY_PROCESS_TIMEOUT.getName()).getAsLong());
    }
  }

  @Override
  protected int getDefaultThreadNum() {
    return this.defaultThreadNum;
  }
}
