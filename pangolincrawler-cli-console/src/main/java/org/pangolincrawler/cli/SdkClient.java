package org.pangolincrawler.cli;

import org.pangolincrawler.sdk.PangolinSDKClient;
import org.pangolincrawler.sdk.SdkCientConfiguration;
import org.pangolincrawler.sdk.impl.RestPangolinSDKClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class SdkClient implements InitializingBean {

	@Autowired
	private ConsoleConfiguration consoleConfiguration;

	private PangolinSDKClient client;
	
	private SdkCientConfiguration config;

	public void afterPropertiesSet() throws Exception {
		config = new SdkCientConfiguration();
		config.setBaseApiUrl(consoleConfiguration.getBaseUrl());
		client = new RestPangolinSDKClient(config);
	}

	/**
	 * @return the client
	 */
	public PangolinSDKClient getClient() {
		return client;
	}

}
