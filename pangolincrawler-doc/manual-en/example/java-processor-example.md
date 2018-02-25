# Create a java processor


First, you need import pangolin-crawler-sdk , the maven Dependency is :

(TODO get the sdk package)

```
<dependency>
    <groupId>org.pangolincrawler</groupId>
    <artifactId>pangolincrawler-java-sdk</artifactId>
    <version>${project.version}</version>
</dependency>
```


Crate a sub class of `TaskProcessor` like this:

```java
package org.pangolincrawler.example.simple.java.processor;

import org.pangolincrawler.sdk.task.TaskProcessor;
import org.pangolincrawler.sdk.task.TaskProcessorException;
import org.pangolincrawler.sdk.utils.LoggerUtils;

public class SimpleCustomProcessor extends TaskProcessor {

  private static final long serialVersionUID = 3603382926617515277L;

  @Override
  public String process(String payload) throws TaskProcessorException {

    print("TaskId:" + super.getTask().getTaskId());
    print("Target Url:" + super.getTask().getUrl());
    print("Processor Context:" + super.getTask().getProcessorContext());

    print("Payload :" + payload);
    print("Html :" + super.getHtml());

    return "Hello Processor!";
  }

  private void print(String message) {
    LoggerUtils.info(message, this.getClass());
  }

}
```

Build the jar with maven.

```
$ mvn package
```

Create processor configuration file named 'processor_config.yaml' like this:

```
# the global unique key
processor_key: exmpale_simple_java_processor

# set the value to 'java' for java processor implementation.
type: java

# Optional, a string or a map or array.
context:
  name: Tom
  city: NK

# set the full java class name of the processor
java_class: org.pangolincrawler.example.simple.java.processor.SimpleProcessor

# the location where to find the processor class, that is similar to the 'classpath' in java.
java_classpath: /youpath/target/pangolincrawler-example-simple-plugin-0.51.jar
```

Register processor with the command `register-processor`.

```
pangolin> register-processor /yourpath/processor_config.yaml
{
  "id": 3,
  "processorKey": "exmpale_simple_plugin_echo_payload_processor",
  "processorClass": "org.pangolincrawler.example.simple.java.processor.SimpleProcessor",
  "attributeJson": "{\"java_classpath\":\"/yourpath/pangolincrawler-example-simple-java-processor-0.51.jar\"}",
  "source": "manual",
  "createAt": "Feb 24, 2018 4:14:50 PM",
  "modifyAt": "Feb 24, 2018 4:14:50 PM"
}
```

A processor can not run alone, must be associated with a job.

Crate a job configuration file as the following:

```
# Required.
job_key: exmpale_simple_java_processor_job
# Required.
processor_key: exmpale_simple_java_processor

# Optional
url: "http://www.yahoo.com"

# Optional, You can enter any string here.
payload: "This is a simplest job for a simplest processor."
```

Register job.

```
pangolin> register-job /yourpath/job_config.yaml
{
  "id": 1,
  "jobKey": "exmpale_simple_java_processor_job",
  "processorKey": "exmpale_simple_java_processor",
  "payloadJson": "\"This is a simplest job for a simplest processor.\"",
  "source": "manual",
  "status": 0,
  "attributeJson": "{\"url\":\"www.yahoo.com\"}",
  "createAt": "Feb 24, 2018 5:01:51 PM",
  "modifyAt": "Feb 24, 2018 5:01:51 PM"
}
```

Start job for testing.

```
pangolin> trigger-job exmpale_simple_java_processor_job
Success, task id is '4cebfd91-3286-4d88-b722-5cbfde9f2e48:6'
```

And you will see the ouput printed in the server log.

```
...] SimpleProcessor [LoggerUtils.java:42] - TaskId:4cebfd91-3286-4d88-b722-5cbfde9f2e48:6
...] SimpleProcessor [LoggerUtils.java:42] - Target Url:http://www.yahoo.com
...] SimpleProcessor [LoggerUtils.java:42] - Processor Context:{name=Tom, city=NK}
...] SimpleProcessor [LoggerUtils.java:42] - Payload :"This is a simplest job for a simplest processor."
...] SimpleProcessor [LoggerUtils.java:42] - HTML :"....body class="my3columns ua-wk ua-mac ua-wk537...."
```

> 
If the jar changed , you need to restart the server for reloading the jar.

