# Create a simple cron job

Create a job configuration file named 'job_config.yaml' like the following, that use the 'just_echo_processor' build in  processor for testing.:

```
---
job_key: exmpale_simple_cron_job
processor_key: just_echo_processor

# http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/crontrigger.html
# every 2 seconds
cron: "*/2 * * * * ?"

# You can enter any string here.
payload: "This is a simplest cond job."
```

Register the job with configuration file.

```
register-job /yourpath/job_config.yaml
{
  "id": 2,
  "jobKey": "exmpale_simple_cron_job",
  "cronExpression": "*/2 * * * * ?",
  "processorKey": "just_echo_processor",
  "payloadJson": "\"This is a simplest cond job.\"",
  "source": "manual",
  "status": 0,
  "attributeJson": "{}",
  "createAt": "Feb 24, 2018 3:00:44 PM",
  "modifyAt": "Feb 24, 2018 3:00:44 PM"
}
```

The cron job does not need to be started manually, that will run automatically. After the registration is successful, you will see the following information in the server log.

```
2018-02-24 15:17:02,538 INFO  [pool-2-thread-1|c7335b6d-bc5e-481b-a407-177d5f47369b:3_exmpale_simple_cron_job] TaskProcessorContainer [TaskProcessorContainer.java:88] - Running Task "c7335b6d-bc5e-481b-a407-177d5f47369b:3_exmpale_simple_cron_job " !
2018-02-24 15:17:02,550 INFO  [pool-2-thread-1|c7335b6d-bc5e-481b-a407-177d5f47369b:3_exmpale_simple_cron_job] SimpleProcessor [LoggerUtils.java:42] - "This is a simplest cond job."
2018-02-24 15:17:04,024 INFO  [pool-2-thread-2|82722a40-9581-41b8-9a26-38d9e6d23e3a:5_exmpale_simple_cron_job] TaskProcessorContainer [TaskProcessorContainer.java:88] - Running Task "82722a40-9581-41b8-9a26-38d9e6d23e3a:5_exmpale_simple_cron_job " !
2018-02-24 15:17:04,025 INFO  [pool-2-thread-2|82722a40-9581-41b8-9a26-38d9e6d23e3a:5_exmpale_simple_cron_job] SimpleProcessor [LoggerUtils.java:42] - "This is a simplest cond job."
2018-02-24 15:17:06,019 INFO  [pool-2-thread-3|0cf0ce45-2971-40d8-8bf0-a9781957332e:7_exmpale_simple_cron_job] TaskProcessorContainer [TaskProcessorContainer.java:88] - Running Task "0cf0ce45-2971-40d8-8bf0-a9781957332e:7_exmpale_simple_cron_job " !
2018-02-24 15:17:06,021 INFO  [pool-2-thread-3|0cf0ce45-2971-40d8-8bf0-a9781957332e:7_exmpale_simple_cron_job] SimpleProcessor [LoggerUtils.java:42] - "This is a simplest cond job."
2018-02-24 15:17:08,016 INFO  [pool-2-thread-4|8737b960-7627-4366-b757-e04b84582033:9_exmpale_simple_cron_job] TaskProcessorContainer [TaskProcessorContainer.java:88] - Running Task "8737b960-7627-4366-b757-e04b84582033:9_exmpale_simple_cron_job " !
2018-02-24 15:17:08,018 INFO  [pool-2-thread-4|8737b960-7627-4366-b757-e04b84582033:9_exmpale_simple_cron_job] SimpleProcessor [LoggerUtils.java:42] - "This is a simplest cond job."
2018-02-24 15:17:10,015 INFO  [pool-2-thread-5|1ab35211-c48b-4cda-a149-c5ff5a9cf538:11_exmpale_simple_cron_job] TaskProcessorContainer [TaskProcessorContainer.java:88] - Running Task "1ab35211-c48b-4cda-a149-c5ff5a9cf538:11_exmpale_simple_cron_job " !
2018-02-24 15:17:10,018 INFO  [pool-2-thread-5|1ab35211-c48b-4cda-a149-c5ff5a9cf538:11_exmpale_simple_cron_job] SimpleProcessor [LoggerUtils.java:42] - "This is a simplest cond job."
2018-02-24 15:17:12,020 INFO  [pool-2-thread-1|a7c81ab3-84f4-47c9-a28e-c80123aa0dd2:13_exmpale_simple_cron_job] TaskProcessorContainer [TaskProcessorContainer.java:88] - Running Task "a7c81ab3-84f4-47c9-a28e-c80123aa0dd2:13_exmpale_simple_cron_job " !
2018-02-24 15:17:12,022 INFO  [pool-2-thread-1|a7c81ab3-84f4-47c9-a28e-c80123aa0dd2:13_exmpale_simple_cron_job] SimpleProcessor [LoggerUtils.java:42] - "This is a simplest cond job."
2018-02-24 15:17:14,018 INFO  [pool-2-thread-2|19e2f654-abfd-4232-b9f3-e614b3a4ddb4:15_exmpale_simple_cron_job] TaskProcessorContainer [TaskProcessorContainer.java:88] - Running Task "19e2f654-abfd-4232-b9f3-e614b3a4ddb4:15_exmpale_simple_cron_job " !
2018-02-24 15:17:14,019 INFO  [pool-2-thread-2|19e2f654-abfd-4232-b9f3-e614b3a4ddb4:15_exmpale_simple_cron_job] SimpleProcessor [LoggerUtils.java:42] - "This is a simplest cond job."
2018-02-24 15:17:16,018 INFO  [pool-2-thread-3|49680f68-3cb3-47d9-9c5b-d2d6d0e5ad09:17_exmpale_simple_cron_job] TaskProcessorContainer [TaskProcessorContainer.java:88] - Running Task "49680f68-3cb3-47d9-9c5b-d2d6d0e5ad09:17_exmpale_simple_cron_job " !
2018-02-24 15:17:16,020 INFO  [pool-2-thread-3|49680f68-3cb3-47d9-9c5b-d2d6d0e5ad09:17_exmpale_simple_cron_job] SimpleProcessor [LoggerUtils.java:42] - "This is a simplest cond job."
2018-02-24 15:17:18,016 INFO  [pool-2-thread-4|217993e5-6c54-45fd-97ee-fd3c4f5ddc9d:19_exmpale_simple_cron_job] TaskProcessorContainer [TaskProcessorContainer.java:88] - Running Task "217993e5-6c54-45fd-97ee-fd3c4f5ddc9d:19_exmpale_simple_cron_job " !
2018-02-24 15:17:18,017 INFO  [pool-2-thread-4|217993e5-6c54-45fd-97ee-fd3c4f5ddc9d:19_exmpale_simple_cron_job] SimpleProcessor [LoggerUtils.java:42] - "This is a simplest cond job."
```

Also you can use the command `task-list` to view the task status.

```
pangolin> task-list
Normal:0, Waiting:0, Running:0, Finished:9, Fail:0, 
+---------------------------------------+-----------------------+-------+-----------------------+---------------------------+--------------+-----------------------+-----------------------+-----------------------+-------------------------------------+
|Task Id                                |Job Key                |url    |Create Time            |Host                       |Current Status|Start Time             |End Time               |Last Modify Time       |Extra Message                        |
+---------------------------------------+-----------------------+-------+-----------------------+---------------------------+--------------+-----------------------+-----------------------+-----------------------+-------------------------------------+
|217993e5-6c54-45fd-97ee-fd3c4f5ddc9d:19|exmpale_simple_cron_job|<empty>|Feb 24, 2018 3:17:18 PM|hostname_VM1|Finished      |Feb 24, 2018 3:17:18 PM|Feb 24, 2018 3:17:18 PM|Feb 24, 2018 3:17:18 PM|Run at hostname_VM1.  |
+---------------------------------------+-----------------------+-------+-----------------------+---------------------------+--------------+-----------------------+-----------------------+-----------------------+-------------------------------------+
|49680f68-3cb3-47d9-9c5b-d2d6d0e5ad09:17|exmpale_simple_cron_job|<empty>|Feb 24, 2018 3:17:16 PM|hostname_VM1|Finished      |Feb 24, 2018 3:17:16 PM|Feb 24, 2018 3:17:16 PM|Feb 24, 2018 3:17:16 PM|Run at hostname_VM1.  |
+---------------------------------------+-----------------------+-------+-----------------------+---------------------------+--------------+-----------------------+-----------------------+-----------------------+-------------------------------------+
|19e2f654-abfd-4232-b9f3-e614b3a4ddb4:15|exmpale_simple_cron_job|<empty>|Feb 24, 2018 3:17:14 PM|hostname_VM1|Finished      |Feb 24, 2018 3:17:14 PM|Feb 24, 2018 3:17:14 PM|Feb 24, 2018 3:17:14 PM|Run at hostname_VM1.  |
+---------------------------------------+-----------------------+-------+-----------------------+---------------------------+--------------+-----------------------+-----------------------+-----------------------+-------------------------------------+
|a7c81ab3-84f4-47c9-a28e-c80123aa0dd2:13|exmpale_simple_cron_job|<empty>|Feb 24, 2018 3:17:12 PM|hostname_VM1|Finished      |Feb 24, 2018 3:17:12 PM|Feb 24, 2018 3:17:12 PM|Feb 24, 2018 3:17:12 PM|Run at hostname_VM1.  |
+---------------------------------------+-----------------------+-------+-----------------------+---------------------------+--------------+-----------------------+-----------------------+-----------------------+-------------------------------------+
|1ab35211-c48b-4cda-a149-c5ff5a9cf538:11|exmpale_simple_cron_job|<empty>|Feb 24, 2018 3:17:10 PM|hostname_VM1|Finished      |Feb 24, 2018 3:17:10 PM|Feb 24, 2018 3:17:10 PM|Feb 24, 2018 3:17:10 PM|Run at hostname_VM1.  |
+---------------------------------------+-----------------------+-------+-----------------------+---------------------------+--------------+-----------------------+-----------------------+-----------------------+-------------------------------------+
|8737b960-7627-4366-b757-e04b84582033:9 |exmpale_simple_cron_job|<empty>|Feb 24, 2018 3:17:08 PM|hostname_VM1|Finished      |Feb 24, 2018 3:17:08 PM|Feb 24, 2018 3:17:08 PM|Feb 24, 2018 3:17:08 PM|Run at hostname_VM1.  |
+---------------------------------------+-----------------------+-------+-----------------------+---------------------------+--------------+-----------------------+-----------------------+-----------------------+-------------------------------------+
|0cf0ce45-2971-40d8-8bf0-a9781957332e:7 |exmpale_simple_cron_job|<empty>|Feb 24, 2018 3:17:06 PM|hostname_VM1|Finished      |Feb 24, 2018 3:17:06 PM|Feb 24, 2018 3:17:06 PM|Feb 24, 2018 3:17:06 PM|Run at hostname_VM1.  |
+---------------------------------------+-----------------------+-------+-----------------------+---------------------------+--------------+-----------------------+-----------------------+-----------------------+-------------------------------------+
|82722a40-9581-41b8-9a26-38d9e6d23e3a:5 |exmpale_simple_cron_job|<empty>|Feb 24, 2018 3:17:04 PM|hostname_VM1|Finished      |Feb 24, 2018 3:17:04 PM|Feb 24, 2018 3:17:04 PM|Feb 24, 2018 3:17:04 PM|Run at hostname_VM1.  |
+---------------------------------------+-----------------------+-------+-----------------------+---------------------------+--------------+-----------------------+-----------------------+-----------------------+-------------------------------------+
|c7335b6d-bc5e-481b-a407-177d5f47369b:3 |exmpale_simple_cron_job|<empty>|Feb 24, 2018 3:17:02 PM|hostname_VM1|Finished      |Feb 24, 2018 3:17:02 PM|Feb 24, 2018 3:17:02 PM|Feb 24, 2018 3:17:02 PM|Run at hostname_VM1.  |
+---------------------------------------+-----------------------+-------+-----------------------+---------------------------+--------------+-----------------------+-----------------------+-----------------------+-------------------------------------+

```

Use `pause-job` to pause the cron job , and the the job status will be 'Paused'.

```
pangolin> pause-job exmpale_simple_cron_job
Success

pangolin> job-list exmpale_simple_cron_job
+-----------------------+---------------+-------------------+---------------+------+------+
|The Job key            |Job Description|Processor key      |Cron Expression|Source|Status|
+-----------------------+---------------+-------------------+---------------+------+------+
|exmpale_simple_cron_job|<empty>        |just_echo_processor|*/2 * * * * ?  |manual|Paused|
+-----------------------+---------------+-------------------+---------------+------+------+
```

Use `resume-job` to resume the cron job parsed.

```
pangolin> resume-job exmpale_simple_cron_job
Success
```

