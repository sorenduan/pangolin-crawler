package org.pangolincrawler.core.processor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DDLQuery;
import org.jooq.DSLContext;
import org.jooq.Insert;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.pangolincrawler.core.PangolinApplication;
import org.pangolincrawler.core.common.ClassLoaderManager;
import org.pangolincrawler.core.constants.Constants.PangolinPropertyType;
import org.pangolincrawler.core.db.SystemCommonRdbService;
import org.pangolincrawler.core.job.ConfigValidaionResult;
import org.pangolincrawler.core.processor.impl.CssSelectorWorkerProcessor;
import org.pangolincrawler.core.processor.impl.SimpleProcessor;
import org.pangolincrawler.core.task.InnerTaskEntry;
import org.pangolincrawler.core.task.TaskProcessorContainer;
import org.pangolincrawler.core.utils.ProcessorUtils;
import org.pangolincrawler.core.utils.SourceType;
import org.pangolincrawler.sdk.PangolinException;
import org.pangolincrawler.sdk.task.TaskInfo;
import org.pangolincrawler.sdk.task.TaskProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProcessorManager {

  protected static Logger logger = LoggerFactory.getLogger(ProcessorManager.class);

  @Autowired
  private SystemCommonRdbService systemCommonRdbService;

  @Autowired
  private ClassLoaderManager classLoaderManager;

  @PostConstruct
  public void init() {
    this.createProcessorTable();
    this.registerSysProcessor();
  }

  public ProcessorPoJo registerWithProcessorConfig(ProcessorConfig config) {
    ProcessorPoJo processorPojo = ProcessorUtils.createProcessorPoJoFromConfig(config);
    classLoaderManager.loadFromClasspath(config.getProcessorKey(), null, config.getClasspath());
    if (null != processorPojo) {
      return this.registorProcessor(processorPojo);
    }
    return null;
  }

  public boolean unregisterProcessor(String processorKey) {
    ProcessorPoJo processorPojo = this.getProcessorByKey(processorKey);
    if (null != processorPojo) {
      return deleteProcessorByKey(processorKey);
    }
    return false;
  }

  public boolean unregisterProcessorBySource(String source) {
    return this.deleteProcessorBySource(source);
  }

  private void registerProcessorWityKeyAndClass(String key, String desc,
      Class<? extends TaskProcessor> clazz, String source) {
    ProcessorPoJo cssSelector = new ProcessorPoJo();
    cssSelector.setProcessorClass(clazz.getName());
    cssSelector.setProcessorKey(key);
    cssSelector.setDescription(desc);
    cssSelector.setSource(source);
    this.registorProcessor(cssSelector);
  }

  private void registerSysProcessor() {
    String source = SourceType.build(SourceType.Type.SYSTEM, "").toString();
    this.registerProcessorWityKeyAndClass("css_selector_processor",
        "Crawl the target webpage page and parse html with css selector,\n and the playload is a json structure.",
        CssSelectorWorkerProcessor.class, source);
    this.registerProcessorWityKeyAndClass("just_echo_processor",
        "A simple Processor just echo payload", SimpleProcessor.class, source);
  }

  public DDLQuery createProcessorTableDDLQuery(SQLDialect dialect) {
    DSLContext dsl = DSL.using(dialect);
    return dsl.createTableIfNotExists(ProcessorTable.PROCESSOR).column(ProcessorTable.PROCESSOR.ID)
        .column(ProcessorTable.PROCESSOR.PROCESSOR_KEY).column(ProcessorTable.PROCESSOR.DESCRIPTION)
        .column(ProcessorTable.PROCESSOR.SOURCE).column(ProcessorTable.PROCESSOR.ATTRIBUTE_JSON)
        .column(ProcessorTable.PROCESSOR.PROCESSOR_CLASSNAME)
        .column(ProcessorTable.PROCESSOR.CREATE_AT).column(ProcessorTable.PROCESSOR.MODIFY_AT)
        .constraints(
            DSL.constraint(ProcessorTable.Keys.PK_ID.getName())
                .primaryKey(ProcessorTable.Keys.PK_ID.getFieldsArray()),
            DSL.constraint(ProcessorTable.Keys.UK_PROCESSOR_KEY.getName())
                .unique(ProcessorTable.Keys.UK_PROCESSOR_KEY.getFieldsArray()));

  }

  public void createProcessorTable() {
    if (PangolinApplication.getPangolinPropertyAsBoolean(
        PangolinPropertyType.PROPERTY_PANGOLIN_DB_TABLE_AUTO_CREATE)) {
      DSLContext dsl = systemCommonRdbService.getDsl();
      DDLQuery ddl = createProcessorTableDDLQuery(dsl.dialect());
      dsl.execute(ddl);
    }
  }

  public List<ProcessorPoJo> listProcessors(int offset, int pageSize) {
    DSLContext dsl = systemCommonRdbService.getDsl();

    SelectConditionStep<Record> cond = dsl.select()
        .from(ProcessorTable.PROCESSOR.getQualifiedName()).where("1=1");

    Result<Record> r = cond.orderBy(ProcessorTable.PROCESSOR.CREATE_AT.desc())
        .limit(offset, pageSize).fetch();

    return r.into(ProcessorPoJo.class);
  }

  public ProcessorPoJo getProcessorByKey(String key) {
    DSLContext dsl = systemCommonRdbService.getDsl();
    Result<Record> r = dsl.select().from(ProcessorTable.PROCESSOR.getQualifiedName())
        .where(ProcessorTable.PROCESSOR.PROCESSOR_KEY.eq(key)).limit(1).fetch();

    List<ProcessorPoJo> list = r.into(ProcessorPoJo.class);

    if (CollectionUtils.isNotEmpty(list)) {
      return list.get(0);
    }
    return null;
  }

  public List<ProcessorPoJo> getProcessorBySource(String source) {
    DSLContext dsl = systemCommonRdbService.getDsl();
    Result<Record> r = dsl.select().from(ProcessorTable.PROCESSOR.getQualifiedName())
        .where(ProcessorTable.PROCESSOR.SOURCE.eq(source)).fetch();

    List<ProcessorPoJo> list = r.into(ProcessorPoJo.class);

    if (CollectionUtils.isNotEmpty(list)) {
      return list;
    }
    return Collections.emptyList();
  }

  public ConfigValidaionResult validateProcessorConfig(ProcessorConfig config) {
    ProcessorConfigValidator validator = new ProcessorConfigValidator(config);
    ConfigValidaionResult result = validator.validate();

    if (result.isFail()) {
      return result;
    }

    ProcessorPoJo oldOne = this.getProcessorByKey(config.getProcessorKey());
    if (null != oldOne) {
      return ConfigValidaionResult
          .fail("The processor '" + config.getProcessorKey() + "' is already existed.");
    }

    return result;
  }

  public boolean isProcessorKeyExisted(String key) {
    return null != this.getProcessorByKey(key);
  }

  public Class<? extends TaskProcessor> getProcessorClassByKey(String key) {
    return getProcessorClassByKey(key, null);
  }

  public TaskProcessor getProcessorInstanceClassByKey(String key)
      throws InstantiationException, IllegalAccessException {
    Class<? extends TaskProcessor> clazz = getProcessorClassByKey(key);
    if (null != clazz) {
      return clazz.newInstance();
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public Class<? extends TaskProcessor> getProcessorClassByKey(String key, ProcessorConfig config) {
    ProcessorPoJo processor = null;
    if (StringUtils.isNotBlank(key)) {
      processor = this.getProcessorByKey(key);
    } else if (null != config) {
      processor = ProcessorUtils.createProcessorPoJoFromConfig(config);
    }

    if (null != processor) {
      Class<?> clazz;

      String pluginKey = null;

      if (StringUtils.isNoneBlank(processor.getSource())) {
        SourceType source = SourceType.buildStringString(processor.getSource());
        if (null != source && source.isPluglinSource()) {
          pluginKey = source.getExtra();
        }
      }

      clazz = classLoaderManager.loadClass(key, pluginKey, processor.getProcessorClassName(),
          processor.getJavaClasspath(), processor.getModifyAt());
      if (null == clazz) {
        return null;
      }

      if (TaskProcessor.class.isAssignableFrom(clazz)) {
        return (Class<? extends TaskProcessor>) clazz;
      }
    }
    return null;
  }

  private boolean deleteProcessorByKey(String processorKey) {
    DSLContext dsl = systemCommonRdbService.getDsl();
    return dsl.delete(ProcessorTable.PROCESSOR)
        .where(ProcessorTable.PROCESSOR.PROCESSOR_KEY.eq(processorKey)).execute() > 0;
  }

  private boolean deleteProcessorBySource(String source) {
    DSLContext dsl = systemCommonRdbService.getDsl();
    return dsl.delete(ProcessorTable.PROCESSOR).where(ProcessorTable.PROCESSOR.SOURCE.eq(source))
        .execute() > 0;
  }

  public TaskProcessorContainer createTaskProcessorContainerFromTaskEntry(InnerTaskEntry task)
      throws InstantiationException, IllegalAccessException {
    TaskProcessor processor = createTaskProcessorFromTaskEntry(task);
    TaskProcessorContainer processorContainer = new TaskProcessorContainer(processor);
    processorContainer.setEntry(task);
    processor.setTask(taskEntryToTaskInfo(task));
    return processorContainer;
  }

  private TaskProcessor createTaskProcessorFromTaskEntry(InnerTaskEntry task)
      throws InstantiationException, IllegalAccessException {

    TaskProcessor processor = this.getProcessorInstanceClassByKey(task.getProcessorKey());

    if (null == processor) {
      throw new PangolinException("Create processor error.");
    }

    processor.setTask(taskEntryToTaskInfo(task));
    return processor;
  }

  private static TaskInfo taskEntryToTaskInfo(InnerTaskEntry entry) {
    TaskInfo info = new TaskInfo();

    info.setPayload(entry.getPayload());
    info.setPreFetch(entry.isPreFetch());
    info.setTaskId(entry.getTaskId());
    info.setUrl(entry.getUrl());
    info.setParentJobKey(entry.getParentJobKey());
    info.setProcessorContext(entry.getProcessorContext());
    info.setJobKey(entry.getJobKey());

    return info;
  }

  public ProcessorPoJo updateProcessorWithConfig(ProcessorConfig processor) {
    ProcessorPoJo pojo = ProcessorUtils.createProcessorPoJoFromConfig(processor);
    return updateProcessor(pojo);
  }

  private ProcessorPoJo updateProcessor(ProcessorPoJo processor) {

    if (null == processor) {
      return null;
    }

    DSLContext dsl = systemCommonRdbService.getDsl();

    dsl.update(ProcessorTable.PROCESSOR)
        .set(ProcessorTable.PROCESSOR.DESCRIPTION, processor.getDescription())
        .set(ProcessorTable.PROCESSOR.PROCESSOR_CLASSNAME, processor.getProcessorClassName())
        .set(ProcessorTable.PROCESSOR.ATTRIBUTE_JSON, processor.getAttributeJson())
        .set(ProcessorTable.PROCESSOR.MODIFY_AT, LocalDateTime.now())
        .where(ProcessorTable.PROCESSOR.PROCESSOR_KEY.eq(processor.getProcessorKey())).execute();

    return this.getProcessorByKey(processor.getProcessorKey());
  }

  public ProcessorPoJo registorProcessor(ProcessorPoJo processor) {

    ProcessorPoJo theOldOne = this.getProcessorByKey(processor.getProcessorKey());
    if (null != theOldOne) {
      logger.warn("The processor '{}' is already existed", processor.getProcessorKey());
      return null;
    }

    DSLContext dsl = systemCommonRdbService.getDsl();

    Insert<ProcessorTableRecored> insertQuery = dsl.insertInto(ProcessorTable.PROCESSOR)
        .set(ProcessorTable.PROCESSOR.PROCESSOR_KEY, processor.getProcessorKey())
        .set(ProcessorTable.PROCESSOR.SOURCE, processor.getSource())
        .set(ProcessorTable.PROCESSOR.PROCESSOR_CLASSNAME, processor.getProcessorClassName())
        .set(ProcessorTable.PROCESSOR.ATTRIBUTE_JSON, processor.getAttributeJson())
        .set(ProcessorTable.PROCESSOR.DESCRIPTION, processor.getDescription())
        .set(ProcessorTable.PROCESSOR.CREATE_AT, LocalDateTime.now())
        .set(ProcessorTable.PROCESSOR.MODIFY_AT, LocalDateTime.now());

    if (insertQuery.execute() > 0) {
      return this.getProcessorByKey(processor.getProcessorKey());
    }

    return null;

  }

}
