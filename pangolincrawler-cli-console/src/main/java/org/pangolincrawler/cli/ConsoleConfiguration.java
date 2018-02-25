package org.pangolincrawler.cli;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(ignoreUnknownFields = false, prefix = "pangolin.rest-api")
public class ConsoleConfiguration {

	private String baseUrl;

	public ConsoleConfiguration() {
	}

	/**
	 * @return the baseUrl
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * @param baseUrl the baseUrl to set
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

}
