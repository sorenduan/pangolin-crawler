# Example

## Create a simple job

Create a simplest job just echo a string to console.

Firstly, use `processor-list` to view the latest 50 registered processor, you would see a processor named 'simple_echo_processor' , which is a build in processor just for testing. 

```
pangolin> processor-list
+----------------------+----------------------------------------------------+------------------------------------------------------------------+----------+------+-------+-----------------------+-----------------------+
|Processor Key         |Description                                         |Processor Class                                                   |Attributes|Source|Type   |Create time            |Last Modified Time     |
+----------------------+----------------------------------------------------+------------------------------------------------------------------+----------+------+-------+-----------------------+-----------------------+
|just_echo_processor |A simple Processor just echo payload                |org.pangolincrawler.core.processor.impl.SimpleProcessor           |<empty>   |system|<empty>|Feb 24, 2018 2:20:02 PM|Feb 24, 2018 2:20:02 PM|
+----------------------+----------------------------------------------------+------------------------------------------------------------------+----------+------+-------+-----------------------+-----------------------+
|css_selector_processor|Fetch url page and parse content with css  selector.|org.pangolincrawler.core.processor.impl.CssSelectorWorkerProcessor|<empty>   |system|<empty>|Feb 24, 2018 2:20:02 PM|Feb 24, 2018 2:20:02 PM|
|                      |the playload is a json structure.                   |                                                                  |          |      |       |                       |                       |
+----------------------+----------------------------------------------------+------------------------------------------------------------------+----------+------+-------+-----------------------+-----------------------+
```

Create a job configuration file named 'job_config.yaml' like this:

```
---
job_key: exmpale_just_echo_a_string
processor_key: just_echo_processor

# You can enter any string here.
payload: "This is a simplest example."
```

Register the job with configuration file.

```
register-job /yourpath/job_config.yaml
{
  "id": 1,
  "jobKey": "exmpale_just_echo_a_string",
  "processorKey": "just_echo_processor",
  "payloadJson": "\"This is a simplest example.\"",
  "source": "manual",
  "status": 0,
  "attributeJson": "{}",
  "createAt": "Feb 24, 2018 2:36:19 PM",
  "modifyAt": "Feb 24, 2018 2:36:19 PM"
}
```

Start the job with `trigger-job`.

```
pangolin> trigger-job exmpale_just_echo_a_string
Success, task id is '141af736-2571-4184-bfbd-a83ca5c621b5:3'
```

Then, you will see some thing like this in the server log.
```
2018-02-24 14:41:00,071 INFO  [pool-2-thread-1|141af736-2571-4184-bfbd-a83ca5c621b5:3_exmpale_just_echo_a_string] SimpleProcessor - "This is a simplest example."
```

Also, you can run the job with `test-job`, for example:

```
pangolin> test-job /yourpath/job_config.yaml
Waiting for result return....

{
  "exmpale_just_echo_a_string": {
    "process_result": "\"This is a simplest example.\""
  }
}
```
