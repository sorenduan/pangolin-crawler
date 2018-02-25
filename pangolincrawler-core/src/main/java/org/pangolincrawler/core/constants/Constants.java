package org.pangolincrawler.core.constants;

public final class Constants {
  public static final String DEFAULT_CHARSET = "UTF-8";

  public static final String ENV_PANGOLIN_HOME = "PANGOLIN_HOME";

  /**
   * eg. -Dpangolin.config.filepath=/yourpath/pangolin.properties
   */
  public static final String SYSTEM_PROPERTY_PANGOLIN_CONFIG_FILE = "pangolin.config.filepath";

  /**
   * eg. -Dpangolin.path.home=/pangolincrawler_home_dir/
   */
  public static final String SYSTEM_PROPERTY_PANGOLIN_PATH_HOME = "pangolin.path.home";

  public static final String PLACEHOLDER_PANGOLIN_WORKER_THREAD_DEFAULT_NUM = "${pangolin.worker.thread.default-num:10}";

  public static final String PANGOLIN_TRIGGER_THREAD_DEFAULT_NUM = "${pangolin.trigger.thread.default-num:10}";

  public static final int SYS_EXIT_CODE_CONFIG_ERROR = 1;

  public static final String CACHE_KEY_SEPERATOR = "_";

  public static final String QUEUE_NAME_OF_TASK = "task_queue";

  public static final int MAX_PAGE_SIZE = 50;

  public static final int DEFAULT_SERVICE_THREAD_POOL_SIZE = 10;

  public static final String OUTPUT_FILE_INDEX_FILENAME = "_index.json";

  public static final String DEFAULT_PLUGIN_DIR_SUFFIX = ".plugin";
  public static final String DEFAULT_PLUGIN_ZIP_SUFFIX = ".plugin.zip";

  public static final String DEFAULT_PLUGIN_CONFIG_FILENAME = "plugin.yaml";

  /**
   * default timeout for http request.
   */
  public static final int DEFAULT_HTTP_TIMEOUT = 5000;

  public static final String DB_TABLE_DEFAULT_PREFIX = "pangolin_";

  public static final String PROPERTY_DB_TABLE_PREFIX = "pangolin.table.prefix";

  //public static final String PANGOLIN_CACHE_SERVICE_IMPL_PLACEHOLDER = "${pangolin.cache.service.impl}";

  public static final String PANGOLIN_CACHE_SERVICE_IMPL = "pangolin.cache.service.impl";

  public static final String PROPERTY_PANGOLIN_TASK_SNAPSHOT_PATH = "pangolin.task.snapshot.path";

  public static final String PROPERTY_PANGOLIN_CACHE_MEMCACHED_SERVERS = "pangolin.cache.memcached.servers";

  public static final String PROPERTY_PANGOLIN_CACHE_REDIS_SERVERS = "pangolin.cache.redis.servers";

  public static final String DEFAULT_VALUE_CACHE_SERVICE_IMPL_CLASS_FULLNAME = "";

  public static final int CONNECT_TIMEOUT = 10;
  /**
   * write timeout, default 10s.
   */
  public static final int WRITE_TIMEOUT = 0;

  public static final int READ_TIMEOUT = 30;

  public static final int CONNECTION_POOL_MAX_IDLE_COUNT = 32;

  /**
   * in minutes.
   */
  public static final int CONNECTION_POOL_MAX_IDLE_MINUTES = 5;

  public enum PangolinPropertyType {
    PROPERTY_HONEYCOMB_TASK_CRUSHED_AUTORESEND("honeycomb.task.crushed.autoResend", true),

    // unit with byte, default 100mb
    PROPERTY_PANGOLIN_LOCALCACHE_HEAP_SIZE("pangolin.localcache.heap.size", 100 * 1024 * 1024L),

    // unit with byte, default 200mb
    PROPERTY_PANGOLIN_LOCALCACHE_OFFHEAP_SIZE("pangolin.localcache.offheap.size",
        200 * 1024 * 1024L),

    // unit with byte, default 500mb
    PROPERTY_PANGOLIN_LOCALCACHE_DISK_SIZE("pangolin.localcache.disk.size", 500 * 1024 * 1024L),

    // dir for disk cache, default is system temp dir
    PROPERTY_PANGOLIN_LOCALCACHE_DISK_DIR("pangolin.localcache.disk.dir", null),

    PROPERTY_PANGOLIN_DB_TABLE_AUTO_CREATE("pangolin.db.table.auto-create", false);

    private String key;
    private Object defaultValue;

    private PangolinPropertyType(String key, Object defaultValue) {
      this.key = key;
      this.defaultValue = defaultValue;
    }

    public String getKey() {
      return key;
    }

    public Object getDefaultValue() {
      return defaultValue;
    }

    @SuppressWarnings("unchecked")
    public <T> T getDefaultValue(Class<T> clazz) {
      if (null != clazz) {
        return (T) defaultValue;
      }
      return null;
    }
  }

  private Constants() {
  }
}
