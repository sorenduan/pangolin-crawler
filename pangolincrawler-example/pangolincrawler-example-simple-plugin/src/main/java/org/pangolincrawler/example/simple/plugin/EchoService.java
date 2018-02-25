package org.pangolincrawler.example.simple.plugin;

import java.util.StringJoiner;

import org.pangolincrawler.sdk.PublicExternalService;

public class EchoService implements PublicExternalService {

	public EchoService() {
	}

	@Override
	public String call(String methodName, String input) {
		StringJoiner j = new StringJoiner(",");
		j.add("methodName:" + methodName).add("input:" + input);
		String s = j.toString();
		return s;
	}

}
