package org.pangolincrawler.plugin.freeproxy.service;

import java.io.Serializable;

public class ProxyPoJo implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int STATUS_NEWPROXY = 0;
	public static final int STATUS_AVAILABLE = 1;
	public static final int STATUS_UNAVAILABLE = 2;

	private long id;

	private String host;

	private int port;

	private String country;

	private int anonymity;

	private int type;

	public ProxyPoJo() {
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the anonymity
	 */
	public int getAnonymity() {
		return anonymity;
	}

	/**
	 * @param anonymity
	 *            the anonymity to set
	 */
	public void setAnonymity(int anonymity) {
		this.anonymity = anonymity;
	}
}
