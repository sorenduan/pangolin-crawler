package org.pangolincrawler.example.simple.plugin;

import org.pangolincrawler.sdk.task.TaskProcessor;
import org.pangolincrawler.sdk.task.TaskProcessorException;

public class EchoProcessor extends TaskProcessor {
	private static final long serialVersionUID = 1L;

	public EchoProcessor() {
	}

	@Override
	public String process(String payload) throws TaskProcessorException {
		return "payload is : " + payload;
	}

}
