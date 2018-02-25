package org.pangolincrawler.core.processor;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.pangolincrawler.core.constants.Constants;
import org.pangolincrawler.core.plugin.ConfigKeyType;
import org.pangolincrawler.sdk.task.TaskProcessor;
import org.pangolincrawler.sdk.task.TaskProcessorException;
import org.springframework.util.Base64Utils;

public class ScriptProcessorContainer extends TaskProcessor {

  private static final long serialVersionUID = -3287745103341168375L;

  private String executable;

  private String processorScriptFilepath;

  private long processTimeout = -1;


  private String taskToScriptArgs() throws UnsupportedEncodingException {

    JsonObject json = new JsonObject();

    json.addProperty(ConfigKeyType.KEY_PAYLOAD.getName(), this.getTask().getPayload());

    byte[] args = json.toString().getBytes(Constants.DEFAULT_CHARSET);

    return Base64Utils.encodeToString(args);
  }

  @Override
  public String process(String payload) throws TaskProcessorException {

    checkExecutable();
    checkScript();

    Process p = null;
    try {
      String args = taskToScriptArgs();
      ProcessBuilder pb = new ProcessBuilder(this.executable, this.processorScriptFilepath, args);
      p = pb.start();

      if (this.getProcessTimeout() > 0) {
        p.waitFor(this.getProcessTimeout(), TimeUnit.MILLISECONDS);
      } else {
        p.waitFor();
      }

      try (InputStream in = p.getInputStream()) {
        InputStreamReader reader = new InputStreamReader(in);
        return IOUtils.toString(reader);
      }

    } catch (IOException | InterruptedException e) {
      throw new TaskProcessorException("process script processor error", e);
    } finally {
      if (null != p) {
        p.destroyForcibly();
      }
    }
  }

  private boolean checkScript() throws TaskProcessorException {
    File file = new File(processorScriptFilepath);
    if (!file.exists()) {
      throw new TaskProcessorException(processorScriptFilepath + " is not existed.");
    }

    return true;
  }

  private void checkExecutable() throws TaskProcessorException {

    File file = new File(executable);
    if (!file.exists()) {
      throw new TaskProcessorException(executable + " is not existed.");
    }

    if (!file.canExecute()) {
      throw new TaskProcessorException(executable + " is not executable.");
    }
  }

  public String getExecutable() {
    return executable;
  }

  public void setExecutable(String executable) {
    this.executable = executable;
  }

  public String getProcessorScriptFilepath() {
    return processorScriptFilepath;
  }

  public void setProcessorScriptFilepath(String processorScriptFilepath) {
    this.processorScriptFilepath = processorScriptFilepath;
  }

  public long getProcessTimeout() {
    return processTimeout;
  }

  public void setProcessTimeout(long processTimeout) {
    this.processTimeout = processTimeout;
  }
}
