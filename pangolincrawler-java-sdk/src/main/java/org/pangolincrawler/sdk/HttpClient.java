package org.pangolincrawler.sdk;

import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

public class HttpClient {

  private final OkHttpClient httpClient;

  public HttpClient() {

    OkHttpClient.Builder builder = new OkHttpClient.Builder();

    ConnectionPool connectionPool = new ConnectionPool(
        Constants.CONNECTION_POOL_MAX_IDLE_COUNT,
        Constants.CONNECTION_POOL_MAX_IDLE_MINUTES, TimeUnit.MINUTES);

    builder.connectionPool(connectionPool);

    builder.connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS);
    builder.readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS);
    builder.writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS);

    httpClient = builder.build();
  }

}
