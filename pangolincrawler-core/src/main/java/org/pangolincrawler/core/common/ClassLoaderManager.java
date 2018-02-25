package org.pangolincrawler.core.common;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.pangolincrawler.core.utils.LoggerUtils;
import org.pangolincrawler.core.utils.PublicServiceUtils;
import org.pangolincrawler.sdk.PublicExternalService;
import org.pangolincrawler.sdk.task.TaskProcessor;
import org.springframework.stereotype.Component;

@Component
public class ClassLoaderManager {

  /**
   * used to lazy load jar.
   */
  private static final class ClassLoaderWrapper {
    private String classLoaderKey;
    private String parentPath;
    private String classpath;
    private ClassLoader classLoader;
    private List<URL> jarFileUrlList;
    private Date changeTime;

    private boolean loaded = false;

    public ClassLoader getClassLoader() {
      synchronized (this) {
        if (loaded) {
          return classLoader;
        }
        if (CollectionUtils.isNotEmpty(jarFileUrlList)) {
          this.classLoader = new URLClassLoader(jarFileUrlList.toArray(new URL[] {}));
        }
        loaded = true;
      }

      return classLoader;
    }

  }

  private Map<String, ClassLoaderWrapper> classloaderMap;

  public ClassLoaderManager() {
    this.classloaderMap = new HashMap<>();
  }

  public void loadFromClasspath(String key, String parentPath, String classpath) {
    if (StringUtils.isBlank(classpath)) {
      return;
    }

    String[] parts = classpath.split(":|;");

    Set<String> jarPathSet = new HashSet<>();

    for (String eachPart : parts) {
      Set<File> each = loadOneClasspathPart(parentPath, eachPart);
      if (CollectionUtils.isNotEmpty(each)) {
        each.forEach(p -> jarPathSet.add(p.getAbsolutePath()));
      }
    }

    if (CollectionUtils.isEmpty(jarPathSet)) {
      return;
    }

    List<URL> jarFileUrlList = new ArrayList<>();

    jarPathSet.forEach(s -> {
      try {
        jarFileUrlList.add(new File(s).toURI().toURL());
      } catch (MalformedURLException e) {
        LoggerUtils.error(this.getClass(), "", e);
      }
    });

    ClassLoaderWrapper wrapper = new ClassLoaderWrapper();

    wrapper.classLoaderKey = key;
    wrapper.jarFileUrlList = jarFileUrlList;
    wrapper.parentPath = parentPath;
    wrapper.classpath = classpath;

    classloaderMap.put(key, wrapper);
  }

  private Set<File> loadOneClasspathPart(String parentDir, String classpathPart) {
    classpathPart = StringUtils.trimToEmpty(classpathPart);
    if (StringUtils.isBlank(classpathPart)) {
      return Collections.emptySet();
    }

    File classpathPartFile = new File(classpathPart);
    if (!classpathPartFile.isAbsolute() && StringUtils.isNotBlank(parentDir)) {
      classpathPartFile = new File(parentDir, classpathPart);
    }

    Set<File> jarPathSet = new HashSet<>();

    if (classpathPartFile.isFile()
        && FilenameUtils.isExtension(classpathPartFile.getName(), "jar")) {
      jarPathSet.add(classpathPartFile);
    } else if (classpathPartFile.isDirectory()) {
      jarPathSet = listJarPathFromDir(classpathPartFile);
    } else if (StringUtils.contains("*", classpathPartFile.getName())) {
      jarPathSet = listJarPathFromDir(classpathPartFile.getParentFile());
    }

    return jarPathSet;

  }

  private Set<File> listJarPathFromDir(File dirFile) {
    if (!dirFile.isDirectory()) {
      return Collections.emptySet();
    }

    FileUtils.listFiles(dirFile, new String[] { "jar" }, false);

    return new HashSet<>(FileUtils.listFiles(dirFile, new String[] { "jar" }, false));
  }

  private boolean needToReload(String loaderKey, Date changeTime) {
    if (this.classloaderMap.containsKey(loaderKey) && null != changeTime) {
      ClassLoaderWrapper wrapper = this.classloaderMap.get(loaderKey);
      return null != wrapper && null != wrapper.changeTime && wrapper.changeTime.before(changeTime);

    }
    return false;
  }

  private void reloadClassLoader(String key, String classpath) {
    this.loadFromClasspath(key, null, classpath);
  }

  public Class<?> loadClass(String key, String parentKey, String className, String classpath,
      Date modifyAt) {
    try {
      if (!this.classloaderMap.containsKey(key) && StringUtils.isNotBlank(classpath)) {
        this.loadFromClasspath(key, null, classpath);
      }

      if (needToReload(key, modifyAt)) {
        reloadClassLoader(key, classpath);
      }

      if (this.classloaderMap.containsKey(key)) {
        ClassLoaderWrapper wrapper = this.classloaderMap.get(key);

        if (null == wrapper && StringUtils.isNotBlank(parentKey)) {
          wrapper = this.classloaderMap.get(parentKey);
        }

        if (null != wrapper) {
          ClassLoader classloader = wrapper.getClassLoader();
          if (classloader != null) {
            return classloader.loadClass(className);
          }
        }
        return Class.forName(className);
      }
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      LoggerUtils.error(this.getClass(), "An error occured whern load class " + className, e);
    }
    return null;
  }

  public PublicExternalService loadServiceInstance(String serviceName, String version,
      String pluginKey, String className) {
    String key = PublicServiceUtils.genClassloaderKey(serviceName, version);
    Class<?> clazz = loadClass(key, pluginKey, className, null, null);
    if (null == clazz) {
      return null;
    }
    return loadServiceInstance(clazz);
  }

  private PublicExternalService loadServiceInstance(Class<?> clazz) {

    try {
      Object t = clazz.newInstance();
      if (t instanceof PublicExternalService) {
        return (PublicExternalService) t;
      }
    } catch (InstantiationException | IllegalAccessException e) {
      LoggerUtils.error(this.getClass(), "load class instance " + clazz + " error.", e);
    }

    return null;
  }

  private TaskProcessor loadProcessorInstance(Class<?> clazz) {
    try {
      Object t = clazz.newInstance();
      if (t instanceof TaskProcessor) {
        return (TaskProcessor) t;
      }
    } catch (InstantiationException | IllegalAccessException e) {
      LoggerUtils.error(this.getClass(), "load class instance " + clazz + " error.", e);
    }

    return null;
  }

  public TaskProcessor loadProcessorInstance(String processorKey, String pluginKey,
      String className) {
    Class<?> clazz = loadClass(processorKey, pluginKey, className, null, null);
    if (null == clazz) {
      return null;
    }
    return loadProcessorInstance(clazz);
  }

}
