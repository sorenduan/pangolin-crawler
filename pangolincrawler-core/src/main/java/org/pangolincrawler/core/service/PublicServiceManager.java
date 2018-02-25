package org.pangolincrawler.core.service;

import java.lang.ref.SoftReference;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DDLQuery;
import org.jooq.DSLContext;
import org.jooq.Insert;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.pangolincrawler.core.ConfigValidationException;
import org.pangolincrawler.core.PangolinApplication;
import org.pangolincrawler.core.common.ClassLoaderManager;
import org.pangolincrawler.core.constants.Constants;
import org.pangolincrawler.core.constants.Constants.PangolinPropertyType;
import org.pangolincrawler.core.db.SystemCommonRdbService;
import org.pangolincrawler.core.job.ConfigValidaionResult;
import org.pangolincrawler.core.plugin.ConfigKeyType;
import org.pangolincrawler.core.service.ServiceConfig.Method;
import org.pangolincrawler.core.service.impl.PublicDbService;
import org.pangolincrawler.core.utils.PublicServiceUtils;
import org.pangolincrawler.core.utils.SourceType;
import org.pangolincrawler.sdk.utils.SdkConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PublicServiceManager {

  protected static Logger logger = LoggerFactory.getLogger(PublicServiceManager.class);

  @Autowired
  private SystemCommonRdbService systemCommonRdbService;

  @Autowired
  private ClassLoaderManager classLoaderManager;

  private Map<String, SoftReference<ServiceDelegator>> services = new HashMap<>();

  @PostConstruct
  public void init() {
    //this.createServiceTable();
    //this.initServiceMethodTable();
    //this.registerSystemServices();
  }

  public void createServiceTable() {
    if (PangolinApplication.getPangolinPropertyAsBoolean(
        PangolinPropertyType.PROPERTY_PANGOLIN_DB_TABLE_AUTO_CREATE)) {
      DSLContext dsl = systemCommonRdbService.getDsl();

      DDLQuery query = dsl.createTableIfNotExists(ServiceTable.SERVICE)
          .column(ServiceTable.SERVICE.ID).column(ServiceTable.SERVICE.SERVICE_NAME)
          .column(ServiceTable.SERVICE.VERSION).column(ServiceTable.SERVICE.SOURCE)
          .column(ServiceTable.SERVICE.TYPE).column(ServiceTable.SERVICE.SERVICE_DESCRIPTION)
          .column(ServiceTable.SERVICE.ATTRIBUTE_JSON).column(ServiceTable.SERVICE.CREATE_AT)
          .column(ServiceTable.SERVICE.MODIFY_AT).constraints(
              DSL.constraint(ServiceTable.Keys.PK_ID.getName())
                  .primaryKey(ServiceTable.Keys.PK_ID.getFieldsArray()),
              DSL.constraint(ServiceTable.Keys.UK_KEY.getName())
                  .unique(ServiceTable.Keys.UK_KEY.getFieldsArray()));

      query.execute();
    }
  }

  public void initServiceMethodTable() {
    DSLContext dsl = systemCommonRdbService.getDsl();

    dsl.dropTableIfExists(ServiceMethodTable.SERVICE_METHODS).execute();

    DDLQuery query = dsl.createTableIfNotExists(ServiceMethodTable.SERVICE_METHODS)
        .column(ServiceMethodTable.SERVICE_METHODS.ID)
        .column(ServiceMethodTable.SERVICE_METHODS.SERVICE_NAME)
        .column(ServiceMethodTable.SERVICE_METHODS.METHOD_NAME)
        .column(ServiceMethodTable.SERVICE_METHODS.VERSION)
        .column(ServiceMethodTable.SERVICE_METHODS.METHOD_DESCRIPTION)
        .column(ServiceMethodTable.SERVICE_METHODS.INPUT_DESCRIPTION)
        .column(ServiceMethodTable.SERVICE_METHODS.OUTPUT_DESCRIPTION)
        .column(ServiceMethodTable.SERVICE_METHODS.CREATE_AT)
        .column(ServiceMethodTable.SERVICE_METHODS.MODIFY_AT).constraints(
            DSL.constraint(ServiceMethodTable.Keys.PK_ID.getName())
                .primaryKey(ServiceMethodTable.Keys.PK_ID.getFieldsArray()),
            DSL.constraint(ServiceMethodTable.Keys.UK_KEY.getName())
                .unique(ServiceMethodTable.Keys.UK_KEY.getFieldsArray()));

    query.execute();
  }

  public ConfigValidaionResult validateServiceConfig(ServiceConfig config) {
    ServiceConfigFormatValidator validator = new ServiceConfigFormatValidator(config);
    ConfigValidaionResult result = validator.validate();

    if (result.isFail()) {
      return result;
    }

    ServicePoJo servicePoJo = this.getServicePoJo(config.getServiceName(), config.getVersion());
    if (null != servicePoJo) {
      return ConfigValidaionResult.fail("the service '" + config.getServiceName() + ":"
          + config.getVersion() + "' is already existed.");
    }

    if (CollectionUtils.isNotEmpty(config.getMethods())) {
      List<ServiceMethodPoJo> oldMethods = this.listServiceMethods(config.getServiceName(),
          config.getMethodNames(), config.getVersion());
      if (CollectionUtils.isNotEmpty(oldMethods)) {
        StringJoiner joiner = new StringJoiner(",");
        oldMethods.forEach(m -> joiner.add(m.getMethodName()));
        return ConfigValidaionResult.fail("the methods '" + joiner.toString() + "' of service '"
            + config.getServiceName() + ":" + config.getVersion() + "'is already existed.");
      }
    }

    return result;
  }

  private void registerSystemServices() {
    ServiceConfig config = new ServiceConfig();
    String version = "0.1";

    config.setServiceName("system_simple_db_service");
    config.setDescription("Access the system database.");
    config.setType(ServicePoJo.ServiceType.JAVA);
    config.setVersion(version);
    config.addOption(ConfigKeyType.KEY_JAVA_CLASS.getName(), PublicDbService.class.getName());

    Method methodGetDbType = new ServiceConfig.Method();
    methodGetDbType.setMethodName(PublicDbService.METHOD_SERVER_DB_TYPE);
    methodGetDbType.setInputDescription("input is null");
    methodGetDbType
        .setOutputDescription("ouput is the database type name, for eample mysql, h2 ect.");
    methodGetDbType.setDescription("get the database type name.");

    Method methodSql = new ServiceConfig.Method();
    methodSql.setMethodName(PublicDbService.METHOD_EXEC_RAW_SQL);
    methodSql.setInputDescription("input is a sql string");
    methodSql.setOutputDescription("ouput is string or a json string.");
    methodSql.setDescription("execute the sql string.");

    config.setMethods(Arrays.asList(methodGetDbType, methodSql));

    try {
      if (this.validateServiceByConfig(config, false)) {
        ServicePoJo servicePoJo = PublicServiceUtils.convertFromServiceConfig(config);
        List<ServiceMethodPoJo> serviceMethodPoJoList = PublicServiceUtils
            .convertFromServiceMethodConfig(config);

        SourceType source = SourceType.build(SourceType.Type.SYSTEM, "");
        servicePoJo.setSource(source.toString());
        this.registorService(servicePoJo, serviceMethodPoJoList);
        String methods = StringUtils.join(config.getMethodNames(), ",");
        logger.info("Register system service '{}:{}' with methods '{}'", config.getServiceName(),
            config.getVersion(), methods);
      }
    } catch (Exception e) {
      logger.warn("Register system service error", e, this.getClass());
      this.deleteServiceByNameAndVersion(config.getServiceName(), version);
      this.deleteMethodsByServiceNameAndVersion(config.getServiceName(), version);
    }
  }

  public boolean validateServiceByConfig(ServiceConfig config, boolean throwException) {
    String serviceName = config.getServiceName();
    String version = config.getVersion();
    String[] methodNames = config.getMethodNames();
    ServicePoJo oldService = this.getServicePoJo(serviceName, version);

    if (null != oldService) {
      if (throwException) {
        throw new ConfigValidationException(
            "The service '" + serviceName + ":" + version + "' alreday existed.");
      } else {
        return false;
      }
    }

    List<ServiceMethodPoJo> methods = listServiceMethods(serviceName, methodNames, version);

    if (CollectionUtils.isNotEmpty(methods)) {
      StringJoiner j = new StringJoiner(",");
      methods.forEach(m -> j.add(m.getMethodName()));
      if (throwException) {
        throw new ConfigValidationException("The methods " + j.toString() + " of the service '"
            + serviceName + ":" + version + "' alreday existed.");
      } else {
        return false;
      }
    }
    return true;
  }

  public ServicePoJo getServicePoJo(String serviceName, String version) {
    DSLContext dsl = systemCommonRdbService.getDsl();

    SelectConditionStep<Record> cond = dsl.select().from(ServiceTable.SERVICE)
        .where(ServiceTable.SERVICE.SERVICE_NAME.eq(serviceName));

    if (!SdkConstants.LATEST_VERSION_KEY.equalsIgnoreCase(version)
        && StringUtils.isNotBlank(version)) {
      cond.and(ServiceTable.SERVICE.VERSION.eq(version));
    }

    Result<Record> r = cond.limit(1).fetch();
    List<ServicePoJo> list = r.into(ServicePoJo.class);
    if (CollectionUtils.isNotEmpty(list)) {
      return list.get(0);
    }
    return null;
  }

  public List<ServicePoJo> listServices(String serviceName, String version, String source) {

    DSLContext dsl = systemCommonRdbService.getDsl();

    SelectConditionStep<Record> cond = dsl.select().from(ServiceTable.SERVICE).where("1=1");

    if (StringUtils.isNotBlank(serviceName)) {
      cond.and(ServiceTable.SERVICE.SERVICE_NAME.eq(StringUtils.trimToEmpty(serviceName)));
    }
    if (StringUtils.isNotBlank(version)) {
      cond.and(ServiceTable.SERVICE.VERSION.eq(StringUtils.trimToEmpty(version)));
    }

    if (StringUtils.isNotBlank(source)) {
      cond.and(ServiceTable.SERVICE.SOURCE.eq(StringUtils.trimToEmpty(source)));
    }

    Result<Record> r = cond.fetch();
    if (r.isNotEmpty()) {
      return r.into(ServicePoJo.class);
    }

    return Collections.emptyList();
  }

  public List<ServicePoJo> listServices(String serviceName, String version) {
    return listServices(serviceName, version, null);
  }

  public List<ServiceMethodPoJo> listServiceMethods(String serviceName, String[] methodNames,
      String version) {
    DSLContext dsl = systemCommonRdbService.getDsl();
    Result<Record> r = dsl.select().from(ServiceMethodTable.SERVICE_METHODS)
        .where(ServiceMethodTable.SERVICE_METHODS.SERVICE_NAME.eq(serviceName))
        .and(ServiceMethodTable.SERVICE_METHODS.METHOD_NAME.in(Arrays.asList(methodNames)))
        .and(ServiceMethodTable.SERVICE_METHODS.VERSION.eq(version)).fetch();

    return r.into(ServiceMethodPoJo.class);
  }

  public List<ServiceMethodPoJo> listServiceMethod(String serviceName, String methodName,
      String version, int offset) {
    DSLContext dsl = systemCommonRdbService.getDsl();
    // Result<Record> r =

    SelectConditionStep<Record> cond = dsl.select().from(ServiceMethodTable.SERVICE_METHODS)
        .where("1=1");

    if (StringUtils.isNoneBlank(serviceName)) {
      cond.and(ServiceMethodTable.SERVICE_METHODS.SERVICE_NAME.eq(serviceName));
    }

    if (StringUtils.isNoneBlank(methodName)) {
      cond.and(ServiceMethodTable.SERVICE_METHODS.METHOD_NAME.eq(methodName));
    }

    if (StringUtils.isNotBlank(version)
        && !StringUtils.equals(version, SdkConstants.LATEST_VERSION_KEY)) {
      cond.and(ServiceMethodTable.SERVICE_METHODS.VERSION.eq(version));
    }

    if (StringUtils.equals(version, SdkConstants.LATEST_VERSION_KEY)) {
      cond.orderBy(ServiceMethodTable.SERVICE_METHODS.VERSION.desc());
    }

    if (offset > 0) {
      cond.limit(offset, Constants.MAX_PAGE_SIZE);
    }

    Result<Record> r = cond.fetch();

    return r.into(ServiceMethodPoJo.class);
  }

  public boolean registorService(ServiceConfig config) {
    ServicePoJo servicePoJo = PublicServiceUtils.convertFromServiceConfig(config);
    String clossloaderKey = PublicServiceUtils.genClassloaderKey(config.getServiceName(),
        config.getVersion());
    classLoaderManager.loadFromClasspath(clossloaderKey, null, config.getClasspath());
    List<ServiceMethodPoJo> methods = PublicServiceUtils.convertFromServiceMethodConfig(config);
    return this.registorService(servicePoJo, methods);
  }

  /**
   * use transaction.
   */
  private boolean registorService(ServicePoJo servicePojo, List<ServiceMethodPoJo> methods) {
    if (!this.insertService(servicePojo)) {
      this.deleteServiceByNameAndVersion(servicePojo.getServiceName(), servicePojo.getVersion());
      throw new PublicServiceException("Insert service '" + servicePojo.getServiceName() + ":"
          + servicePojo.getVersion() + "' data error, please verify whether the service has "
          + "already existed, and you could clear incorrect service first.");
    }

    if (CollectionUtils.isEmpty(methods)) {
      throw new PublicServiceException("Register service error, the method must be specified.");
    }

    methods.forEach(m -> {
      if (!this.insertMethod(m)) {
        this.deleteServiceByNameAndVersion(servicePojo.getServiceName(), servicePojo.getVersion());
        this.deleteMethodsByServiceNameAndVersion(m.getServiceName(), m.getVersion());
        throw new PublicServiceException("Insert method ('" + m.getMethodName() + ":"
            + m.getVersion() + "') data error, please verify whether the method has "
            + "already existed, and you could clear incorrect service first.");
      }
    });
    return true;
  }

  public boolean unregistorService(String serviceName, String version) {
    return this.deleteMethodsByServiceNameAndVersion(serviceName, version)
        && this.deleteServiceByNameAndVersion(serviceName, version);
  }

  public boolean unregistorServiceWithSource(String source) {
    List<ServicePoJo> serviceList = this.listServices(null, null, source);
    if (CollectionUtils.isNotEmpty(serviceList)) {
      serviceList.forEach(s -> {
        this.deleteMethodsByServiceNameAndVersion(s.getServiceName(), s.getVersion());
        this.deleteServiceByNameAndVersion(s.getServiceName(), s.getVersion());
      });
    }
    return true;
  }

  private boolean deleteServiceByNameAndVersion(String serviceName, String version) {
    DSLContext dsl = systemCommonRdbService.getDsl();
    return dsl.delete(ServiceTable.SERVICE).where(ServiceTable.SERVICE.SERVICE_NAME.eq(serviceName))
        .and(ServiceTable.SERVICE.VERSION.eq(version)).execute() > 0;
  }

  private boolean deleteMethodsByServiceNameAndVersion(String serviceName, String version) {
    DSLContext dsl = systemCommonRdbService.getDsl();
    return dsl.delete(ServiceMethodTable.SERVICE_METHODS)
        .where(ServiceMethodTable.SERVICE_METHODS.SERVICE_NAME.eq(serviceName))
        .and(ServiceMethodTable.SERVICE_METHODS.VERSION.eq(version)).execute() > 0;
  }

  private boolean insertService(ServicePoJo service) {
    DSLContext dsl = systemCommonRdbService.getDsl();

    Insert<ServiceTableRecored> insertQuery = dsl.insertInto(ServiceTable.SERVICE)
        .set(ServiceTable.SERVICE.SERVICE_NAME, service.getServiceName())
        .set(ServiceTable.SERVICE.VERSION, service.getVersion())
        .set(ServiceTable.SERVICE.SOURCE, service.getSource())
        .set(ServiceTable.SERVICE.SERVICE_DESCRIPTION, service.getDescription())
        .set(ServiceTable.SERVICE.TYPE, service.getType())
        .set(ServiceTable.SERVICE.SOURCE, service.getSource())
        .set(ServiceTable.SERVICE.ATTRIBUTE_JSON, service.getAttributeJson())
        .set(ServiceTable.SERVICE.CREATE_AT, LocalDateTime.now())
        .set(ServiceTable.SERVICE.MODIFY_AT, LocalDateTime.now());

    return insertQuery.execute() > 0;
  }

  public String call(String serviceName, String methodName, String version, String input) {

    ServicePoJo service = this.getServicePoJo(serviceName, version);
    if (null == service) {
      throw new PublicServiceException(
          "The service (" + serviceName + ":" + version + ") is not existed.");
    }

    List<ServiceMethodPoJo> methods = this.listServiceMethod(serviceName, methodName, version, 0);
    if (CollectionUtils.isEmpty(methods)) {
      throw new PublicServiceException("The method('" + methodName + "') of service (" + serviceName
          + ":" + version + ") is not existed.");
    }

    ServiceMethodPoJo method = methods.get(0);

    // get from local cache
    ServiceDelegator delegator = null;
    String key = genServiceLocalCacheKey(service.getServiceName(), method.getMethodName(),
        method.getVersion());
    if (this.services.containsKey(key)) {
      SoftReference<ServiceDelegator> cache = this.services.get(key);
      delegator = cache.get();
      if (null == delegator) {
        delegator = ServiceDelegator.build(service, method);
        this.services.put(key, new SoftReference<ServiceDelegator>(delegator));
      }
    } else {
      delegator = ServiceDelegator.build(service, method);
      this.services.put(key, new SoftReference<ServiceDelegator>(delegator));
    }
    if (null != delegator) {
      return delegator.run(input);
    }
    return null;
  }

  private String genServiceLocalCacheKey(String serviceName, String methodName, String version) {
    StringJoiner j = new StringJoiner(":");
    return j.add(serviceName).add(methodName).add(version).toString();
  }

  private boolean insertMethod(ServiceMethodPoJo method) {
    DSLContext dsl = systemCommonRdbService.getDsl();

    Insert<ServiceTableRecored> insertQuery = dsl.insertInto(ServiceMethodTable.SERVICE_METHODS)
        .set(ServiceMethodTable.SERVICE_METHODS.SERVICE_NAME, method.getServiceName())
        .set(ServiceMethodTable.SERVICE_METHODS.METHOD_NAME, method.getMethodName())
        .set(ServiceMethodTable.SERVICE_METHODS.VERSION, method.getVersion())
        .set(ServiceMethodTable.SERVICE_METHODS.INPUT_DESCRIPTION, method.getInputDescription())
        .set(ServiceMethodTable.SERVICE_METHODS.OUTPUT_DESCRIPTION, method.getOutputDescription())
        .set(ServiceMethodTable.SERVICE_METHODS.METHOD_DESCRIPTION, method.getDescription())
        .set(ServiceTable.SERVICE.CREATE_AT, LocalDateTime.now())
        .set(ServiceTable.SERVICE.MODIFY_AT, LocalDateTime.now());

    return insertQuery.execute() > 0;
  }

}
