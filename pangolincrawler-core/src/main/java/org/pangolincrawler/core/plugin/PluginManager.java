package org.pangolincrawler.core.plugin;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DDLQuery;
import org.jooq.DSLContext;
import org.jooq.Insert;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.pangolincrawler.core.PangolinApplication;
import org.pangolincrawler.core.common.ClassLoaderManager;
import org.pangolincrawler.core.common.PangolinServerException;
import org.pangolincrawler.core.constants.Constants;
import org.pangolincrawler.core.constants.Constants.PangolinPropertyType;
import org.pangolincrawler.core.db.SystemCommonRdbService;
import org.pangolincrawler.core.job.ConfigValidaionResult;
import org.pangolincrawler.core.job.JobConfig;
import org.pangolincrawler.core.job.JobManager;
import org.pangolincrawler.core.processor.ProcessorConfig;
import org.pangolincrawler.core.processor.ProcessorManager;
import org.pangolincrawler.core.service.PublicServiceManager;
import org.pangolincrawler.core.service.ServiceConfig;
import org.pangolincrawler.core.service.ServiceTable;
import org.pangolincrawler.core.utils.LoggerUtils;
import org.pangolincrawler.core.utils.PluginUtils;
import org.pangolincrawler.core.utils.SourceType;
import org.pangolincrawler.core.utils.YamlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PluginManager {

  @Autowired
  private JobManager jobManager;

  @Autowired
  private PublicServiceManager serviceManager;

  @Autowired
  private ProcessorManager processorManager;

  @Autowired
  private ClassLoaderManager classLoaderManager;

  @Autowired
  private SystemCommonRdbService systemCommonRdbService;

  private Map<String, PluginConfig> localPlugins = new HashMap<>();

  @PostConstruct
  public void createPluginTable() {
    if (PangolinApplication.getPangolinPropertyAsBoolean(
        PangolinPropertyType.PROPERTY_PANGOLIN_DB_TABLE_AUTO_CREATE)) {
      DSLContext dsl = systemCommonRdbService.getDsl();
      DDLQuery query = this.createCreatePluginTableDDLQuery(dsl.dialect());
      dsl.execute(query);
    }
  }

  public List<PluginPoJo> listRegisteredPlugins() {
    DSLContext dsl = systemCommonRdbService.getDsl();

    Result<Record> r = dsl.select().from(PluginTable.PLUGIN.getQualifiedName()).fetch();

    if (r.isNotEmpty()) {
      return r.into(PluginPoJo.class);
    }

    return Collections.emptyList();
  }

  public List<PluginPoJo> listLocalPlugins() {
    if (MapUtils.isEmpty(localPlugins)) {
      return Collections.emptyList();
    }
    List<String> pluginKeys = new ArrayList<>(localPlugins.keySet());
    List<PluginPoJo> pluginsRegistered = this.listPluginsWithKeys(pluginKeys);

    List<PluginPoJo> pluginsForReturn = new ArrayList<>();

    localPlugins.forEach((key, p1) -> {
      PluginPoJo pr = null;
      if (CollectionUtils.isNotEmpty(pluginsRegistered)) {
        for (PluginPoJo p2 : pluginsRegistered) {
          if (StringUtils.equals(key, p2.getPlubinKey())) {
            pr = p2;
            pr.setRegistered(true);
            break;
          }

        }
      }
      if (pr == null) {
        pr = PluginUtils.convertFromTpl(p1);
        pr.setRegistered(false);
      }
      pluginsForReturn.add(pr);
    });

    return pluginsForReturn;
  }

  public List<PluginPoJo> listPluginsWithKeys(List<String> pluginKeyList) {
    DSLContext dsl = systemCommonRdbService.getDsl();

    Result<Record> r = dsl.select().from(PluginTable.PLUGIN.getQualifiedName())
        .where(PluginTable.PLUGIN.PLUGIN_KEY.in(pluginKeyList)).fetch();

    if (r.isNotEmpty()) {
      return r.into(PluginPoJo.class);
    }

    return Collections.emptyList();
  }

  private boolean deletePlugin(String pluginKey) {
    DSLContext dsl = systemCommonRdbService.getDsl();
    return dsl.delete(PluginTable.PLUGIN).where(PluginTable.PLUGIN.PLUGIN_KEY.eq(pluginKey))
        .execute() > 0;
  }

  private DDLQuery createCreatePluginTableDDLQuery(SQLDialect dialect) {
    DSLContext dsl = DSL.using(dialect);

    return dsl.createTableIfNotExists(PluginTable.PLUGIN).column(PluginTable.PLUGIN.ID)
        .column(PluginTable.PLUGIN.PLUGIN_KEY).column(PluginTable.PLUGIN.DESCRIPTION)
        .column(PluginTable.PLUGIN.ATTRIBUTE_JSON).column(PluginTable.PLUGIN.STATUS)
        .column(PluginTable.PLUGIN.CREATE_AT).column(PluginTable.PLUGIN.MODIFY_AT).constraints(
            DSL.constraint(PluginTable.Keys.PK_ID.getName())
                .primaryKey(PluginTable.Keys.PK_ID.getFieldsArray()),
            DSL.constraint(PluginTable.Keys.UK_KEY.getName())
                .unique(PluginTable.Keys.UK_KEY.getFieldsArray()));
  }

  public void loadLocalPlugins() {
    List<File> allPluginDirs = decompressPluginPackage();
    if (CollectionUtils.isNotEmpty(allPluginDirs)) {
      allPluginDirs.forEach(f -> {
        try {
          PluginConfig config = loadPluginConfig(f, false);
          classLoaderManager.loadFromClasspath(config.getPluginKey(), config.getPluginDir(),
              config.getClasspath());
          localPlugins.put(config.getPluginKey(), config);
        } catch (IOException e) {
          LoggerUtils.error(this.getClass(),
              "Error occurred when loading  plugin '" + f.getAbsolutePath(), e);
        }
      });
    }
  }

  private PluginConfig loadPluginConfig(File pluginDir, boolean isSimple) throws IOException {
    File configFile = new File(pluginDir, Constants.DEFAULT_PLUGIN_CONFIG_FILENAME);
    JsonObject jsonObj = getConfigFileAsJson(configFile);
    if (isSimple) {
      return PluginConfig.buildSimpleFromJsonObject(jsonObj, pluginDir.getAbsolutePath());
    } else {
      return PluginConfig.buildFromJsonObject(jsonObj, pluginDir.getAbsolutePath());
    }
  }

  private JsonObject getConfigFileAsJson(File configFile) throws IOException {
    String yamlConfig = FileUtils.readFileToString(configFile, Constants.DEFAULT_CHARSET);
    return YamlUtils.convertToJsonObject(yamlConfig);
  }

  public boolean unregisterPlugin(String pluginKey) {
    try {
      String source = SourceType.buildPluginSourceType(pluginKey).toString();
      this.serviceManager.unregistorServiceWithSource(source);
      this.processorManager.unregisterProcessor(source);
      this.jobManager.unregisterJobBySource(source);
      this.deletePlugin(pluginKey);
    } catch (Exception e) {
      LoggerUtils.error(this.getClass(), "Unregister '" + pluginKey + "' error.", e);
    }
    return true;
  }

  public String registerPlugin(String pluginKey) {
    PluginConfig config = localPlugins.get(pluginKey);
    if (null == config) {
      throw new PangolinServerException(
          "plugin '" + pluginKey + "' is not loaded , please restart the server.");
    }
    return this.registerPlugin(config);
  }

  public String registerPlugin(PluginConfig config) {
    if (null == config) {
      return "Config is null.";
    }

    try {

      String pluginKey = config.getPluginKey();

      List<String> responseReportList = new ArrayList<>();

      if (config.isSimpleConfig()) {
        config = this.loadPluginConfig(new File(config.getPluginDir()), false);
        if (null != config) {
          localPlugins.put(config.getPluginKey(), config);
        }
      }

      SourceType source = SourceType.buildPluginSourceType(pluginKey);
      PluginPoJo plugin = this.getPluginByKey(pluginKey);
      if (null != plugin) {
        throw new PangolinServerException("the plugin '" + pluginKey + "' is already existed.");
      }

      validatePluginConfig(config);

      List<ServiceConfig> serviceConfigList = config.getServiceConfigList();
      if (CollectionUtils.isNotEmpty(serviceConfigList)) {
        serviceConfigList.forEach(s -> {
          s.setSource(source);
          if (this.serviceManager.registorService(s)) {
            responseReportList
                .add("Register service '" + s.getServiceName() + ":" + s.getVersion() + "' OK");
          } else {
            responseReportList.add("Register service fail, please check the server log, key is '"
                + s.getServiceName() + ":" + s.getVersion());
          }
        });
      }

      List<ProcessorConfig> processorConfigList = config.getProcessorConfigList();
      if (CollectionUtils.isNotEmpty(processorConfigList)) {
        processorConfigList.forEach(p -> {
          p.setSource(source);
          if (null != this.processorManager.registerWithProcessorConfig(p)) {
            responseReportList.add("Register processor OK, key:'" + p.getProcessorKey());
          } else {
            responseReportList.add("Register processor '" + p.getProcessorKey()
                + "' Fail, please check the server log.");
          }
        });
      }

      List<JobConfig> jobConfigList = config.getJobConfigList();
      if (CollectionUtils.isNotEmpty(jobConfigList)) {
        jobConfigList.forEach(j -> {
          j.setSource(source);
          if (null != this.jobManager.registerWithJobConfig(j)) {
            responseReportList.add("Register job OK, '" + j.getJobKey());
          } else {
            responseReportList.add(
                "Register processor '" + j.getJobKey() + "' Fail, please check the server log.");
          }
        });
      }

      PluginPoJo pojo = PluginUtils.convertFromTpl(config);

      if (insertPlugin(pojo)) {
        responseReportList.add("Register plugin '" + pojo.getPlubinKey() + "' OK.");
      } else {
        responseReportList.add("Register plugin '" + pojo.getPlubinKey()
            + "' Fail, insert db error, please check the server log.");
      }
      return StringUtils.joinWith("\n", responseReportList);
    } catch (Exception e) {
      LoggerUtils.error(this.getClass(), "Regster the plugin '" + config + "' error", e);
      if (null != config) {
        this.unregisterPlugin(config.getPluginKey());
      }
    }
    return "Error";
  }

  private boolean insertPlugin(PluginPoJo service) {
    DSLContext dsl = systemCommonRdbService.getDsl();

    Insert<PluginTableRecored> insertQuery = dsl.insertInto(PluginTable.PLUGIN)
        .set(PluginTable.PLUGIN.PLUGIN_KEY, service.getPlubinKey())
        .set(PluginTable.PLUGIN.DESCRIPTION, service.getDescription())
        .set(ServiceTable.SERVICE.ATTRIBUTE_JSON, service.getAttributeJson())
        .set(ServiceTable.SERVICE.CREATE_AT, LocalDateTime.now())
        .set(ServiceTable.SERVICE.MODIFY_AT, LocalDateTime.now());

    return insertQuery.execute() > 0;
  }

  public PluginPoJo getPluginByKey(String key) {
    DSLContext dsl = systemCommonRdbService.getDsl();
    Result<Record> r = dsl.select().from(PluginTable.PLUGIN)
        .where(PluginTable.PLUGIN.PLUGIN_KEY.eq(key)).limit(1).fetch();

    List<PluginPoJo> list = r.into(PluginPoJo.class);

    if (CollectionUtils.isNotEmpty(list)) {
      return list.get(0);
    }
    return null;
  }

  private void validatePluginConfig(PluginConfig config) {

    List<ServiceConfig> serviceConfigList = config.getServiceConfigList();

    if (CollectionUtils.isNotEmpty(serviceConfigList)) {
      serviceConfigList.forEach(each -> {
        ConfigValidaionResult result = this.serviceManager.validateServiceConfig(each);
        if (result.isFail()) {
          throw new PangolinServerException(result.getMessage());
        }
      });
    }

    List<ProcessorConfig> processorConfigList = config.getProcessorConfigList();

    if (CollectionUtils.isNotEmpty(processorConfigList)) {
      processorConfigList.forEach(each -> {
        ConfigValidaionResult result = this.processorManager.validateProcessorConfig(each);
        if (result.isFail()) {
          throw new PangolinServerException(result.getMessage());
        }
      });
    }

    List<JobConfig> jobConfigList = config.getJobConfigList();

    if (CollectionUtils.isNotEmpty(jobConfigList)) {
      jobConfigList.forEach(each -> {
        ConfigValidaionResult result = this.jobManager.validateJobConfig(each);
        if (result.isFail()) {
          throw new PangolinServerException(result.getMessage());
        }
      });
    }
  }

  public PluginConfig registerPluginFromPluginDir(String dir) {
    PluginConfig config;
    try {
      config = loadPluginConfig(new File(dir), true);
      this.registerPlugin(config);
    } catch (IOException e) {
      throw new PangolinServerException(e);
    }
    return config;
  }

  private List<File> decompressPluginPackage() {
    List<File> allPluginDirs = listAllLocalPluginDir();
    Set<String> allPluginPath = new HashSet<>();

    if (CollectionUtils.isNotEmpty(allPluginDirs)) {
      allPluginDirs.forEach(f -> allPluginPath.add(f.getAbsolutePath()));
    }

    List<File> allZipFiles = listAllLocalUncompressPluginFile();
    if (CollectionUtils.isNotEmpty(allZipFiles)) {
      allZipFiles.forEach(f -> {
        String willPath = f.getAbsolutePath().replaceAll("\\.zip$", "");
        if (!allPluginPath.contains(willPath)) {
          decompressOnePluginZipPackage(f);
        }
      });
      return listAllLocalPluginDir();
    } else {
      return allPluginDirs;
    }
  }

  private void decompressOnePluginZipPackage(File file) {
    try {
      ZipFile zipFile = new ZipFile(file.getAbsolutePath());
      zipFile.extractAll(file.getParent());
    } catch (ZipException e) {
      throw new PangolinServerException("decompress '" + file + "' error.", e);
    }
  }

  private List<File> listAllLocalUncompressPluginFile() {
    String pluginsDir = PangolinApplication.getConfig().getDefaultLocalPluginsDir();

    File pluginDir = new File(pluginsDir);

    if (!pluginDir.isDirectory() || !pluginDir.exists()) {
      throw new PangolinServerException("the plugins ('" + pluginsDir + "') dir is not existed.");
    }

    Collection<File> files = FileUtils.listFiles(pluginDir, new String[] { "plugin.zip" }, false);

    if (CollectionUtils.isNotEmpty(files)) {
      return new ArrayList<>(files);
    }
    return Collections.emptyList();
  }

  private List<File> listAllLocalPluginDir() {
    String pluginsDir = PangolinApplication.getConfig().getDefaultLocalPluginsDir();
    Collection<File> pluginDirs = FileUtils.listFilesAndDirs(new File(pluginsDir),
        new IOFileFilter() {
          @Override
          public boolean accept(File file) {
            return false;
          }

          @Override
          public boolean accept(File dir, String name) {
            return false;
          }

        }, new IOFileFilter() {

          @Override
          public boolean accept(File file) {
            return file.isDirectory()
                && StringUtils.endsWith(file.getName(), Constants.DEFAULT_PLUGIN_DIR_SUFFIX);
          }

          @Override
          public boolean accept(File dir, String name) {
            return false;
          }

        });

    if (CollectionUtils.isNotEmpty(pluginDirs)) {
      pluginDirs.remove(new File(pluginsDir));
    }

    return new ArrayList<>(pluginDirs);

  }

}
