package org.pangolincrawler.plugin.freeproxy.service;

public enum AnonymityEnum {
	UNKOWN(0, "unkown"), TRANSPARENT(1, "transparent"), ANONYMOUS(2, "anonymous"), ELITE(3, "elite");

	private int code;
	private String name;

	private AnonymityEnum(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public static AnonymityEnum fromName(String name) {
		if (name == null) {
			return null;
		}
		name = name.trim().toLowerCase();
		if (name.contains(TRANSPARENT.getName())) {
			return TRANSPARENT;
		} else if (name.contains(ANONYMOUS.getName())) {
			return ANONYMOUS;
		} else if (name.contains(ELITE.getName())) {
			return ELITE;
		}
		return UNKOWN;
	}

	public static AnonymityEnum fromCode(int code) {
		for (AnonymityEnum each : AnonymityEnum.values()) {
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
