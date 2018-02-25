
# Overview

Pangolin Crawler is a distribution crawler tools and not just a sdk.


# Job configuration




# Command line tools



## Pangolin cli tools

### Job commands 

#### view job information

Use `job-list` to list lastest 50 job's short information.

```
pangolin> job-list
+-------------------+---------------+----------------------+---------------+------+------+
|The Job key        |Job Description|Processor key         |Cron Expression|Source|Status|
+-------------------+---------------+----------------------+---------------+------+------+
|exmpale_github_blog|<empty>        |css_selector_processor|<empty>        |manual|Normal|
+-------------------+---------------+----------------------+---------------+------+------+
```

Use the `-v` option to show the detail information.

```
pangolin> job-list -v
+-------------------+---------------+----------------------+---------------+------+------+----------------------------------------------------------------------------+---------------------------------------------------------------+-----------------------+-----------------------+
|The Job key        |Job Description|Processor key         |Cron Expression|Source|Status|Payload                                                                     |Attributes                                                     |Job Created Time       |Job Last Modified Time |
+-------------------+---------------+----------------------+---------------+------+------+----------------------------------------------------------------------------+---------------------------------------------------------------+-----------------------+-----------------------+
|exmpale_github_blog|<empty>        |css_selector_processor|<empty>        |manual|Normal|[                                                                           |{                                                              |2018-02-24 11:49:09 CST|2018-02-24 11:49:09 CST|
|                   |               |                      |               |      |      |..{                                                                         |.."file_output":.{                                             |                       |                       |
|                   |               |                      |               |      |      |...."key":."list",                                                          |...."dir":."/tmp/pangolin_exmaple_github_blog"                 |                       |                       |
|                   |               |                      |               |      |      |...."selector":."#blog-main.>.div.blog-content.>.div.posts.>.div.blog-post",|..},                                                           |                       |                       |
|                   |               |                      |               |      |      |...."children":.[                                                           |.."loop":.{                                                    |                       |                       |
|                   |               |                      |               |      |      |......{                                                                     |...."links_pattern":."^http(s?)://github\\.com/blog\\?after=.+"|                       |                       |
|                   |               |                      |               |      |      |........"key":."title",                                                     |..},                                                           |                       |                       |
|                   |               |                      |               |      |      |........"selector":."h2.blog-post-title.a"                                  |.."request_rate":.{                                            |                       |                       |
|                   |               |                      |               |      |      |......},                                                                    |...."expression":."1/10s"                                      |                       |                       |
|                   |               |                      |               |      |      |......{                                                                     |..},                                                           |                       |                       |
|                   |               |                      |               |      |      |........"key":."date",                                                      |.."url":."https://github.com/blog/"                            |                       |                       |
|                   |               |                      |               |      |      |........"selector":."ul.blog-post-meta.>.li:nth-child(1)"                   |}                                                              |                       |                       |
|                   |               |                      |               |      |      |......},                                                                    |                                                               |                       |                       |
|                   |               |                      |               |      |      |......{                                                                     |                                                               |                       |                       |
|                   |               |                      |               |      |      |........"key":."author",                                                    |                                                               |                       |                       |
|                   |               |                      |               |      |      |........"selector":."ul.blog-post-meta.>.li.fn.meta-item"                   |                                                               |                       |                       |
|                   |               |                      |               |      |      |......}                                                                     |                                                               |                       |                       |
|                   |               |                      |               |      |      |....]                                                                       |                                                               |                       |                       |
|                   |               |                      |               |      |      |..}                                                                         |                                                               |                       |                       |
|                   |               |                      |               |      |      |]                                                                           |                                                               |                       |                       |
+-------------------+---------------+----------------------+---------------+------+------+----------------------------------------------------------------------------+---------------------------------------------------------------+-----------------------+-----------------------+

```

Use `help job-list` show more options.

#### register job 

Use `register-job` to register job from yaml file, that will return the job configuration as json format when registered successfully.

```
pangolin> register-job /filepath/job_config.yaml
{
  "id": 1,
  "jobKey": "exmpale_github_blog",
  "processorKey": "css_selector_processor",
  "payloadJson": "[{\"key\":\"list\",\"selector\":\"#blog-main \\u003e div.blog-content \\u003e div.posts \\u003e div.blog-post\",\"children\":[{\"key\":\"title\",\"selector\":\"h2.blog-post-title a\"},{\"key\":\"date\",\"selector\":\"ul.blog-post-meta \\u003e li:nth-child(1)\"},{\"key\":\"author\",\"selector\":\"ul.blog-post-meta \\u003e li.fn.meta-item\"}]}]",
  "source": "manual",
  "status": 0,
  "attributeJson": "{\"file_output\":{\"dir\":\"/tmp/pangolin_exmaple_github_blog\"},\"loop\":{\"links_pattern\":\"^http(s?)://github\\\\.com/blog\\\\?after=.+\"},\"request_rate\":{\"expression\":\"1/10s\"},\"url\":\"https://github.com/blog/\"}",
  "createAt": "Feb 24, 2018 11:04:17 AM",
  "modifyAt": "Feb 24, 2018 11:04:17 AM"
}
```

#### start job manually 
 
Use `trigger-job` to start job manually with a job key, and return a task id if start successful.
```
pangolin> trigger-job exmpale_github_blog
Success, task id is '0158a55c-5698-4101-ac94-1d8a604facdb:5'
```  

#### update job configuration

Use `update-job` to update job configuration from a file.

```
pangolin> update-job job_config.yaml
{
  "id": 1,
  "jobKey": "exmpale_github_blog",
  "processorKey": "css_selector_processor",
  "payloadJson": "[{\"key\":\"list\",\"selector\":\"#blog-main \\u003e div.blog-content \\u003e div.posts \\u003e div.blog-post\",\"children\":[{\"key\":\"title\",\"selector\":\"h2.blog-post-title a\"},{\"key\":\"date\",\"selector\":\"ul.blog-post-meta \\u003e li:nth-child(1)\"},{\"key\":\"author\",\"selector\":\"ul.blog-post-meta \\u003e li.fn.meta-item\"}]}]",
  "source": "manual",
  "status": 0,
  "attributeJson": "{\"file_output\":{\"dir\":\"/tmp/pangolin_exmaple_github_blog\"},\"loop\":{\"links_pattern\":\"^http(s?)://github\\\\.com/blog\\\\?after=.+\"},\"request_rate\":{\"expression\":\"1/10s\"},\"url\":\"https://github.com/blog/\"}",
  "createAt": "Feb 25, 2018 10:39:35 AM",
  "modifyAt": "Feb 25, 2018 10:41:56 AM"
}
```

If you specify a cron expression, cron job will be rescheduled.

### Task commands

#### view task

Use `task-list` to view tasks, that will show the latest 50 tasks.

```
pangolin> task-list
pangolin> task-list exmpale_github_blog
Normal:0, Waiting:1, Running:0, Finished:6, Fail:0, 
+---------------------------------------+-------------------+--------------------------------------------------------------------------------+------------------------+---------------------------+--------------+------------------------+------------------------+------------------------+-------------------------------------+
|Task Id                                |Job Key            |url                                                                             |Create Time             |Host                       |Current Status|Start Time              |End Time                |Last Modify Time        |Extra Message                        |
+---------------------------------------+-------------------+--------------------------------------------------------------------------------+------------------------+---------------------------+--------------+------------------------+------------------------+------------------------+-------------------------------------+
|edd5a71d-8865-4a96-ba37-3ba289b5a830:22|exmpale_github_blog|https://github.com/blog?after=Y3Vyc29yOnYyOpK0MjAxNy0wOC0wOFQxNzowNToyNlrNCW0%3D|Feb 24, 2018 11:58:15 AM|vm1|Warting       |<empty>                 |<empty>                 |Feb 24, 2018 11:58:15 AM|                                     |
+---------------------------------------+-------------------+--------------------------------------------------------------------------------+------------------------+---------------------------+--------------+------------------------+------------------------+------------------------+-------------------------------------+
|8d42fb0a-15dc-4941-b38c-de1b656c8130:19|exmpale_github_blog|https://github.com/blog?after=Y3Vyc29yOnYyOpK0MjAxNy0wOS0xMlQxODoyNTo1NlrNCX4%3D|Feb 24, 2018 11:58:10 AM|vm1|Finished      |Feb 24, 2018 11:58:10 AM|Feb 24, 2018 11:58:15 AM|Feb 24, 2018 11:58:15 AM|Run at vm1.  |
+---------------------------------------+-------------------+--------------------------------------------------------------------------------+------------------------+---------------------------+--------------+------------------------+------------------------+------------------------+-------------------------------------+
|f19614f5-24c9-427d-bf30-61a8c7065257:16|exmpale_github_blog|https://github.com/blog?after=Y3Vyc29yOnYyOpK0MjAxNy0xMC0wOVQyMToxMDoyMFrNCY4%3D|Feb 24, 2018 11:57:48 AM|vm1|Finished      |Feb 24, 2018 11:58:00 AM|Feb 24, 2018 11:58:10 AM|Feb 24, 2018 11:58:10 AM|Run at vm1.  |
+---------------------------------------+-------------------+--------------------------------------------------------------------------------+------------------------+---------------------------+--------------+------------------------+------------------------+------------------------+-------------------------------------+
|9023f799-0086-43a0-b58d-4d6ea2ea984d:13|exmpale_github_blog|https://github.com/blog?after=Y3Vyc29yOnYyOpK0MjAxNy0xMS0wNlQxODowMDowMlrNCZ4%3D|Feb 24, 2018 11:57:32 AM|vm1|Finished      |Feb 24, 2018 11:57:44 AM|Feb 24, 2018 11:57:48 AM|Feb 24, 2018 11:57:48 AM|Run at vm1.  |
+---------------------------------------+-------------------+--------------------------------------------------------------------------------+------------------------+---------------------------+--------------+------------------------+------------------------+------------------------+-------------------------------------+
|a50f40af-2523-4cf1-b032-8d85fb28d125:10|exmpale_github_blog|https://github.com/blog?after=Y3Vyc29yOnYyOpK0MjAxNy0xMi0wNVQyMzoyODoxM1rNCaw%3D|Feb 24, 2018 11:57:29 AM|vm1|Finished      |Feb 24, 2018 11:57:29 AM|Feb 24, 2018 11:57:32 AM|Feb 24, 2018 11:57:32 AM|Run at vm1.  |
+---------------------------------------+-------------------+--------------------------------------------------------------------------------+------------------------+---------------------------+--------------+------------------------+------------------------+------------------------+-------------------------------------+
|9cc08126-9a2b-4151-a6f6-15a78e20d986:7 |exmpale_github_blog|https://github.com/blog?after=Y3Vyc29yOnYyOpK0MjAxOC0wMS0xOVQyMjowMjo0NVrNCbw%3D|Feb 24, 2018 11:56:55 AM|vm1|Finished      |Feb 24, 2018 11:56:55 AM|Feb 24, 2018 11:57:29 AM|Feb 24, 2018 11:57:29 AM|Run at vm1.  |
+---------------------------------------+-------------------+--------------------------------------------------------------------------------+------------------------+---------------------------+--------------+------------------------+------------------------+------------------------+-------------------------------------+
|0158a55c-5698-4101-ac94-1d8a604facdb:5 |exmpale_github_blog|https://github.com/blog/                                                        |Feb 24, 2018 11:56:41 AM|vm1|Finished      |Feb 24, 2018 11:56:42 AM|Feb 24, 2018 11:56:55 AM|Feb 24, 2018 11:56:55 AM|Run at vm1.  |
+---------------------------------------+-------------------+--------------------------------------------------------------------------------+------------------------+---------------------------+--------------+------------------------+------------------------+------------------------+-------------------------------------+

```

Use `help task-list` show all the options.

```
pangolin> help task-list


NAME
    task-list - Show task infomation, that will show the latest 50 tasks.

SYNOPSYS
    task-list [[-j] string]  [-ve]  

OPTIONS
    -j or --jobkey  string
        Specify the job key
        [Optional, default = <none>]

    -ve or --vertical
        Display jobs infomation vertically
        [Optional, default = false]



pangolin> 
```

### Processor commands

#### show all processor

Use `help task-list` show all the options.

## Pangolin server control



# Development

## processor development

## public service development

## Plug-in development

### plugin 


# Multiple Language

## php 

### develop processor use php

### develop service by php

# Distribution mode

## Database integration

### MySQL integration

## Cache server integration

### Memcached integration

### Redis integration

## Message queue integration

### ActiveMQ integration

### RabbitMQ integration

TODO

## RabbitMQ integration

## Quartz scheduler configuration

http://www.quartz-scheduler.org/documentation/quartz-2.x/configuration/ConfigJDBCJobStoreClustering.html

```
org.quartz.jobStore.class=org.quartz.impl.jdbcjobstore.JobStoreTX
org.quartz.jobStore.driverDelegateClass=org.quartz.impl.jdbcjobstore.StdJDBCDelegate
org.quartz.jobStore.dataSource=myDS

org.quartz.dataSource.myDS.driver=com.mysql.jdbc.Driver
org.quartz.dataSource.myDS.URL=jdbc:mysql://localhost:3306/pangolin_quartz?createDatabaseIfNotExist=true
org.quartz.dataSource.myDS.user=root
org.quartz.dataSource.myDS.password=123456

org.quartz.jobStore.tablePrefix=QRTZ_
org.quartz.jobStore.isClustered=true
```

# Debug Or Test 

## Run job directly

Use `test-job` run job directly with a job configuration file . It does not create any task.
This is usually used to test the job configuration before registration.

```
pangolin> test-job /yourpath/job_config.yaml
Waiting for result return....

{
  "exmpale_github_blog": {
    "url": "https://github.com/blog/",
    "process_result": "{\"list\":[{\"children\":{\"title\":[{\"html\":\"Weak cryptographic standards removed\",\"attrs\":{\"rel\":\"bookmark\",\"href\":\"/blog/2507-weak-cryptographic-standards-removed\"},\"text\":\"Weak cryptographic standards removed\"}],\.......",
    "loop_links": [
      "https://github.com/blog?after=Y3Vyc29yOnYyOpK0MjAxOC0wMS0xOVQyMjowMjo0NVrNCbw%3D"
    ]
  }
}
```
