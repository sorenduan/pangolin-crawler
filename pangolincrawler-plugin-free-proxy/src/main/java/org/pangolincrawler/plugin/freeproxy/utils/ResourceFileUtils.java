package org.pangolincrawler.plugin.freeproxy.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public final class ResourceFileUtils {

	public static String getTableCreateSql() {
		return loadResourceFile(Constants.FILENAME_INIT_TABLE_SQL);
	}
	
	public static String getProxySiteUrls() {
		return loadResourceFile(Constants.FILENAME_PROXY_SITES);
	}
	
	
	private static String loadResourceFile(String filename) {
		try {
			Path path = Paths.get(ResourceFileUtils.class.getClassLoader().getResource(filename).toURI());
			StringBuilder data = new StringBuilder();
			Stream<String> lines = Files.lines(path);
			lines.forEach(line -> data.append(line).append("\n"));
			lines.close();

			return data.toString();
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(loadResourceFile("proxy.sql"));
	}
}
