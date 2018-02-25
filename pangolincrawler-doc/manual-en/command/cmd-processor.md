
# Processor commands

## show all processor

Use `processor-list` to view the latest 50 registered processor.After the service is started, system will registers same built-in processors such as 'just_echo_processor' and 'css_selector_processor'.

```
pangolin> processor-list
+----------------------+----------------------------------------------------+------------------------------------------------------------------+----------+------+-------+-----------------------+-----------------------+
|Processor Key         |Description                                         |Processor Class                                                   |Attributes|Source|Type   |Create time            |Last Modified Time     |
+----------------------+----------------------------------------------------+------------------------------------------------------------------+----------+------+-------+-----------------------+-----------------------+
|just_echo_processor |A simple Processor just echo payload                |org.pangolincrawler.core.processor.impl.SimpleProcessor           |<empty>   |system|<empty>|Feb 24, 2018 2:20:02 PM|Feb 24, 2018 2:20:02 PM|
+----------------------+----------------------------------------------------+------------------------------------------------------------------+----------+------+-------+-----------------------+-----------------------+
|css_selector_processor|Crawl the target webpage page and parse html with css selector.|org.pangolincrawler.core.processor.impl.CssSelectorWorkerProcessor|<empty>   |system|<empty>|Feb 24, 2018 2:20:02 PM|Feb 24, 2018 2:20:02 PM|
|                      |the playload is a json structure.                   |                                                                  |          |      |       |                       |                       |
+----------------------+----------------------------------------------------+------------------------------------------------------------------+----------+------+-------+-----------------------+-----------------------+
```

Use `help processor-list` show all the options.


## register processor

Use `register-processor` to register a processor with a configuration file.

```
pangolin> register-processor /yourpath/processor_config.yaml
{
  "id": 3,
  "processorKey": "exmpale_simple_plugin_echo_payload_processor",
  "processorClass": "org.pangolincrawler.example.simple.java.processor.SimpleProcessor",
  "attributeJson": "{\"java_classpath\":\"/yourpath/target/pangolincrawler-example-simple-plugin-0.51.jar\"}",
  "source": "manual",
  "createAt": "Feb 24, 2018 4:14:50 PM",
  "modifyAt": "Feb 24, 2018 4:14:50 PM"
}
```

## update processor

Use `register-processor` to update a processor with a configuration file.

```
pangolin> update-processor /yourpath/processor_config.yaml
{
  "id": 3,
  "processorKey": "exmpale_simple_java_processor",
  "processorClass": "org.pangolincrawler.example.simple.java.processor.SimpleProcessor",
  "attributeJson": "{\"context\":{\"name\":\"Tom\",\"city\":\"NK\"},\"java_classpath\":\"/target/pangolincrawler-example-simple-java-processor-0.51.jar\"}",
  "source": "manual",
  "createAt": "Feb 24, 2018 6:17:56 PM",
  "modifyAt": "Feb 24, 2018 8:55:26 PM"
}
```


