package org.pangolincrawler.core.http;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.apache.commons.collections4.MapUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.pangolincrawler.core.cache.CacheManager;
import org.pangolincrawler.core.constants.Constants;
import org.pangolincrawler.core.utils.LoggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HttpClientService {

  protected static Logger logger = LoggerFactory.getLogger(HttpClientService.class);

  public static final MediaType TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

  @Autowired
  private CacheManager cacheManager;

  private OkHttpClient okHttpClient;

  @PostConstruct
  public void init() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();

    ConnectionPool connectionPool = new ConnectionPool(Constants.CONNECTION_POOL_MAX_IDLE_COUNT,
        Constants.CONNECTION_POOL_MAX_IDLE_MINUTES, TimeUnit.MINUTES);

    builder.connectionPool(connectionPool);

    builder.connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS);
    builder.readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS);
    builder.writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS);

    okHttpClient = builder.build();
  }

  private PangolinHttpClientResponse innerRequest(Request request) {
    try {
      try (Response response = okHttpClient.newCall(request).execute()) {

        return buildHttpClientResponse(response);
      }
    } catch (Exception e) {
      logger.error("Request Error.", e);
      throw new RuntimeException("An error occurred during request the url '"
          + request.url().toString() + "', caused by : " + e.getLocalizedMessage(), e);
    }
  }

  public void postJson(String url, String jsonContent) {
    RequestBody body = RequestBody.create(TYPE_JSON, jsonContent);
    innerRequest(new Request.Builder().post(body).url(url).build());
  }

  public PangolinHttpClientResponse request(PangolinHttpClientRequest request) {
    try {

      HttpClient httpClient = this.buildHttpClientInstance(request);
      HttpUriRequest httpClientRequest = this.buildHttpClientRequest(request);

      HttpResponse response = httpClient.execute(httpClientRequest);
      PangolinHttpClientResponse r = buildHttpClientResponse(response, request);

      if (request.isCachable()) {
        cacheManager.put(request.getUrl(), r);
      }
      return r;
    } catch (IOException | ProxyInfoNotFoundException e) {
      logger.error("An  Error occurred during the http get request", e);
      throw new RuntimeException("An error occurred during request the url '" + request.getUrl()
          + "', caused by : " + e.getLocalizedMessage(), e);
    }
  }

  private HttpUriRequest buildHttpClientRequest(PangolinHttpClientRequest request) {
    RequestBuilder build = RequestBuilder.get(request.getUrl());

    RequestConfig config = buildHttpRequestConfig(request);
    build.setConfig(config);
    HttpUriRequest httpClientRequest = build.build();
    if (MapUtils.isNotEmpty(request.getHeaders())) {
      for (Entry<String, String> each : request.getHeaders().entrySet()) {
        httpClientRequest.addHeader(each.getKey(), each.getValue());
      }
    }
    return httpClientRequest;
  }

  private HttpClient buildHttpClientInstance(PangolinHttpClientRequest request)
      throws ProxyInfoNotFoundException {
    HttpClientBuilder builder = HttpClients.custom();
    builder.setUserAgent(request.getUserAgent());

    if (null != request.getProxy()) {
      HttpHost proxy = new HttpHost(request.getProxy().getAddress(), request.getProxy().getPort());
      builder.setProxy(proxy);
    }

    CloseableHttpClient client = builder.build();
    return client;
  }

  private RequestConfig buildHttpRequestConfig(PangolinHttpClientRequest request) {
    RequestConfig.Builder configBuilder = RequestConfig.custom();
    configBuilder.setConnectTimeout(request.getTimeout());

    return configBuilder.build();
  }

  @Deprecated
  private PangolinHttpClientResponse buildHttpClientResponse(HttpResponse httpResponse,
      PangolinHttpClientRequest request) {
    PangolinHttpClientResponse response = new PangolinHttpClientResponse();

    try {
      if (null != httpResponse) {
        response.setHtml(EntityUtils.toString(httpResponse.getEntity(), request.getCharset()));
        response.setHttpStatusCode(httpResponse.getStatusLine().getStatusCode());
      } else {
        response.setSuccess(false);
      }

    } catch (ParseException | IOException e) {
      e.printStackTrace();
      response.setSuccess(false);
      response.setException(e);
    }
    return response;
  }

  private PangolinHttpClientResponse buildHttpClientResponse(Response httpResponse) {
    PangolinHttpClientResponse response = new PangolinHttpClientResponse();

    try {
      if (null != httpResponse) {

        response.setHtml(httpResponse.body().string());
        response.setHttpStatusCode(httpResponse.code());
      } else {
        response.setSuccess(false);
      }

    } catch (ParseException | IOException e) {
      response.setSuccess(false);
      response.setException(e);
      LoggerUtils.error(this.getClass(), e);
    }
    return response;
  }

}
