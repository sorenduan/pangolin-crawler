package org.pangolincrawler.sdk;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class Constants {
	/**
	 * default charset
	 */
	public static final Charset UTF_8 = StandardCharsets.UTF_8;
	/**
	 * connection timeout, default 10s
	 */
	public static final int CONNECT_TIMEOUT = 10;
	/**
	 * write timeout, default 10s
	 */
	public static final int WRITE_TIMEOUT = 0;

	public static final int READ_TIMEOUT = 30;

	public static final int CONNECTION_POOL_MAX_IDLE_COUNT = 32;

	public static final int CONNECTION_POOL_MAX_IDLE_MINUTES = 5;

	private Constants() {
	}
}
