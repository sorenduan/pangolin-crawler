package org.pangolincrawler.plugin.freeproxy.service;

public enum ProxyTypeEnum {
	UNKOWN(0, "unkown"), HTTP(1, "http"), HTTPS(2, "https"), SOCKS(3, "socks");

	private int code;
	private String name;

	private ProxyTypeEnum(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public static ProxyTypeEnum fromName(String name) {
		if (name == null) {
			return null;
		}
		name = name.trim().toLowerCase();
		if (name.contains(HTTP.getName())) {
			return HTTP;
		} else if (name.contains(HTTPS.getName())) {
			return HTTPS;
		} else if (name.contains(SOCKS.getName())) {
			return SOCKS;
		}
		return UNKOWN;
	}

	public static ProxyTypeEnum fromCode(int code) {
		for (ProxyTypeEnum each : ProxyTypeEnum.values()) {
			if (each.getCode() == code) {
				return each;
			}
		}
		return UNKOWN;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

}
