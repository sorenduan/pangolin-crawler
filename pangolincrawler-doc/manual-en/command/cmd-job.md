# Job commands 

## view job information

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

## register job 

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

## start job manually 
 
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


## Test and run job 

Use `test-job` run job directly with a job configuration file . It does not create any task.
This is usually used to test the job configuration before registration.

It is important to note that cron expression do not work in this way.

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


## control cron job 


