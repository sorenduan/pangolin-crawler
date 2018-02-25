package org.pangolincrawler.plugin.freeproxy.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.pangolincrawler.plugin.freeproxy.service.FreeProxyService;
import org.pangolincrawler.plugin.freeproxy.service.ProxyPoJo;
import org.pangolincrawler.plugin.freeproxy.service.ProxyTypeEnum;
import org.pangolincrawler.sdk.task.TaskProcessor;
import org.pangolincrawler.sdk.task.TaskProcessorException;

public class ProxyVailabilityCheckerProcessor extends TaskProcessor {
	private static final long serialVersionUID = 1626970967349631277L;

	public ProxyVailabilityCheckerProcessor() {
	}

	@Override
	public String process(String payload) throws TaskProcessorException {
		Map<String, Object> context = this.getInfo().getProcessorContext();
		Object url = context.get("url_for_check");
		if (url != null) {
			String urlStr = String.valueOf(url);
			checkProxies(urlStr);
		}
		return null;
	}

	private void checkProxies(String url) {
		List<ProxyPoJo> proxies = FreeProxyService.instance().getLastestModifedProxyList(ProxyPoJo.STATUS_NEWPROXY);

		if (null != proxies && proxies.size() > 0) {
			proxies.forEach(p -> {
				if (!checkProxy(p, url)) {
					FreeProxyService.instance().updateProxyStatusById(p.getId(), ProxyPoJo.STATUS_UNAVAILABLE);
				}
			});
		}
	}

	private static boolean checkProxy(ProxyPoJo proxyPoJo, String url) {
		try {
			if (null == proxyPoJo || null == proxyPoJo.getHost() || url == null) {
				return false;
			}

			InetSocketAddress addr = new InetSocketAddress(proxyPoJo.getHost(), proxyPoJo.getPort());
			URL urlObj = new URL(url);
			ProxyTypeEnum proxyType = ProxyTypeEnum.fromCode(proxyPoJo.getType());

			Proxy proxy = null;
			if (ProxyTypeEnum.HTTP.equals(proxyType) || ProxyTypeEnum.HTTPS.equals(proxyType)) {
				proxy = new Proxy(Proxy.Type.HTTP, addr);
			} else if (ProxyTypeEnum.SOCKS.equals(proxyType)) {
				proxy = new Proxy(Proxy.Type.SOCKS, addr);
			} else {
				return false;
			}

			HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection(proxy);

			conn.setRequestProperty("Accept-Charset", "utf-8");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36");
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(10 * 1000);
			conn.setInstanceFollowRedirects(true);

			conn.connect();
			int code = conn.getResponseCode();
			if (code > 200) {
				return false;
			}
			String host = getHost(url);
			String html = getHtml(conn);
			if (null == html || html.trim().length() < 100) {
				return false;
			}
			return html.contains(host);
		} catch (IOException | URISyntaxException e) {
			//e.printStackTrace();
		}

		return false;
	}

	private static String getHost(String url) throws URISyntaxException {
		URI uri = new URI(url);
		String host = uri.getHost();
		return host;
	}

	private static String getHtml(HttpURLConnection conn) throws IOException {
		try (InputStream in = conn.getInputStream()) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		}
	}

	public static void main(String[] args) {
		// from https://www.us-proxy.org/
		ProxyPoJo proxyPoJo = new ProxyPoJo();
		proxyPoJo.setPort(3128);
		proxyPoJo.setType(ProxyTypeEnum.HTTP.getCode());
		proxyPoJo.setHost("35.189.86.114");
		boolean r = checkProxy(proxyPoJo, "https://www.google.com");
		System.out.println(r);
	}
}
