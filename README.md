# Pangolin Cralwer

## Overview

TODO

## Features

TODO

* distributed environment supported.
* multiple language supported.
* multiple database supported.

## Architecture

TODO

* Job:
* Processor:
* Task:
* Service:
* Plugin:

## Quick Start

### Download pangolin crawler release package 

```
$ wget 
$ tar -zxvf pangolin-crawler-release-<version>.tar.gz
$ cd angolin-crawler-release-<version>
```

Use the option `-d` or `--daemonize` run the server in background.

### Start the server 

```
$ ./bin/pangolincrawler.sh start
```
It will take a few seconds to start the server, and you will see something like the following information when the server start up success.  

```
Starting Pangolin Crawler Server...

      ___           ___           ___           ___           ___           ___                   ___     
     /\  \         /\  \         /\__\         /\  \         /\  \         /\__\      ___        /\__\    
    /::\  \       /::\  \       /::|  |       /::\  \       /::\  \       /:/  /     /\  \      /::|  |   
   /:/\:\  \     /:/\:\  \     /:|:|  |      /:/\:\  \     /:/\:\  \     /:/  /      \:\  \    /:|:|  |   
  /::\~\:\  \   /::\~\:\  \   /:/|:|  |__   /:/  \:\  \   /:/  \:\  \   /:/  /       /::\__\  /:/|:|  |__ 
 /:/\:\ \:\__\ /:/\:\ \:\__\ /:/ |:| /\__\ /:/__/_\:\__\ /:/__/ \:\__\ /:/__/     __/:/\/__/ /:/ |:| /\__\
 \/__\:\/:/  / \/__\:\/:/  / \/__|:|/:/  / \:\  /\ \/__/ \:\  \ /:/  / \:\  \    /\/:/  /    \/__|:|/:/  /
      \::/  /       \::/  /      |:/:/  /   \:\ \:\__\    \:\  /:/  /   \:\  \   \::/__/         |:/:/  / 
       \/__/        /:/  /       |::/  /     \:\/:/  /     \:\/:/  /     \:\  \   \:\__\         |::/  /  
                   /:/  /        /:/  /       \::/  /       \::/  /       \:\__\   \/__/         /:/  /   
                   \/__/         \/__/         \/__/         \/__/         \/__/                 \/__/    

#########################################################################################################
                                                                                                       
                           Pangolin Crawler Server, Version: 0.51

#########################################################################################################

Start pangolin server start success.
```


### Config a crawler job

Create a job config file named 'job_conf.yaml' like the following.

```yaml
# This is an example of crawling github blog post list.  
---
# Each job has a global unique key.
job_key: exmpale_github_blog

# Specify the processor key, and each job corresponds to a processor.
# the processor is a program that parses the HTML content.
# In this example, we using 'css_selector_processor' that is a built-in processor 
# for extracting html elements using the css selector.
processor_key: css_selector_processor

# Specifgy the target url for crawling.
url: https://github.com/blog/

# The payload parameter is input value for each processor,
# which can be a json structure or a string or number, and 
# the yaml content will be convert into json.  

# In this example, we specify the css selector in the 'playload'. 
# You can use some tools like chrome developer tools to find 
# the css selector of an html element.
payload: 
-
  # Specify the blog post list selector and key (used for return value).
  key: list
  selector: '#blog-main > div.blog-content > div.posts > div.blog-post'
  # Specify the selector for each post item.
  children: 
  - 
    # Specify the selector for the post title.
    key: title
    selector: 'h2.blog-post-title a'
  - 
    # Specify the selector for the post date.
    key: date
    selector: 'ul.blog-post-meta > li:nth-child(1)'
  - 
    # Specify the selector for the author.
    key: author
    selector: 'ul.blog-post-meta > li.fn.meta-item'

# Specify the output file where the parse result save.
file_output:
  dir: /tmp/pangolin_exmaple_github_blog

# Specify the request rate limition used to avoid request overload.
request_rate:
  # Send one http request every ten seconds
  expression: 1/10s

# Parse the next page
loop:
  # A regular expression used to define the link for the next page
  links_pattern: ^http(s?)://github\.com/blog\?after=.+
```

### Start  pangolin cli console

```
$ ./bin/pangolincrawler-cli.sh

Starting Pangolin Crawler Command Line Console ...

      ___           ___           ___           ___           ___           ___                   ___     
     /\  \         /\  \         /\__\         /\  \         /\  \         /\__\      ___        /\__\    
    /::\  \       /::\  \       /::|  |       /::\  \       /::\  \       /:/  /     /\  \      /::|  |   
   /:/\:\  \     /:/\:\  \     /:|:|  |      /:/\:\  \     /:/\:\  \     /:/  /      \:\  \    /:|:|  |   
  /::\~\:\  \   /::\~\:\  \   /:/|:|  |__   /:/  \:\  \   /:/  \:\  \   /:/  /       /::\__\  /:/|:|  |__ 
 /:/\:\ \:\__\ /:/\:\ \:\__\ /:/ |:| /\__\ /:/__/_\:\__\ /:/__/ \:\__\ /:/__/     __/:/\/__/ /:/ |:| /\__\
 \/__\:\/:/  / \/__\:\/:/  / \/__|:|/:/  / \:\  /\ \/__/ \:\  \ /:/  / \:\  \    /\/:/  /    \/__|:|/:/  /
      \::/  /       \::/  /      |:/:/  /   \:\ \:\__\    \:\  /:/  /   \:\  \   \::/__/         |:/:/  / 
       \/__/        /:/  /       |::/  /     \:\/:/  /     \:\/:/  /     \:\  \   \:\__\         |::/  /  
                   /:/  /        /:/  /       \::/  /       \::/  /       \:\__\   \/__/         /:/  /   
                   \/__/         \/__/         \/__/         \/__/         \/__/                 \/__/    

#########################################################################################################
                                                                                                       
                        Pangolin Crawler Cli Console, Version: 0.51
                        
                        https://github.com/sorenxing/pangolin-crawler

#########################################################################################################


pangolin> 

```

### Register job with the config file

Use `register-job` to register a job from a job configuration file, and it will return a json when successfully registered.

```
pangolin> register-job job_config.yaml
{
  "id": 1,
  "jobKey": "exmpale_github_blog",
  "processorKey": "css_selector_processor",
  "payloadJson": "[{\"key\":\"list\",\"selector\":\"#blog-main \\u003e div.blog-content \\u003e div.posts \\u003e div.blog-post\",\"children\":[{\"key\":\"title\",\"selector\":\"h2.blog-post-title a\"},{\"key\":\"date\",\"selector\":\"ul.blog-post-meta \\u003e li:nth-child(1)\"},{\"key\":\"author\",\"selector\":\"ul.blog-post-meta \\u003e li.fn.meta-item\"}]}]",
  "source": "manual",
  "status": 0,
  "attributeJson": "{\"file_output\":{\"dir\":\"/tmp/pangolin_exmaple_github_blog\"},\"loop\":{\"links_pattern\":\"^http(s?)://github\\\\.com/blog\\\\?after=.+\"},\"request_rate\":{\"expression\":\"1/10s\"},\"url\":\"https://github.com/blog/\"}",
  "createAt": "Feb 25, 2018 8:04:55 AM",
  "modifyAt": "Feb 25, 2018 8:04:55 AM"
}
```

Use `job-list` show all registered jobs. 

```
pangolin> job-list
+-------------------+---------------+----------------------+---------------+------+------+
|The Job key        |Job Description|Processor key         |Cron Expression|Source|Status|
+-------------------+---------------+----------------------+---------------+------+------+
|exmpale_github_blog|<empty>        |css_selector_processor|<empty>        |manual|Normal|
+-------------------+---------------+----------------------+---------------+------+------+
```

### Start job manually

Use `trigger-job` start job manually. Of course, if you specify a cron expression, the job will run automatically.

```
pangolin> trigger-job exmpale_github_blog
Success, task id is '4bcca58e-60de-4733-97b6-68c17ebb1991:5'
```

View the job task with `task-list`

```
pangolin> task-list
Normal:0, Waiting:1, Running:0, Finished:1, Fail:0, 
+--------------------------------------+-------------------+--------------------------------------------------------------------------------+------------------------+---------------------------+--------------+------------------------+------------------------+------------------------+-------------------------------------+
|Task Id                               |Job Key            |url                                                                             |Create Time             |Host                       |Current Status|Start Time              |End Time                |Last Modify Time        |Extra Message                        |
+--------------------------------------+-------------------+--------------------------------------------------------------------------------+------------------------+---------------------------+--------------+------------------------+------------------------+------------------------+-------------------------------------+
|86ae4a11-9aa0-46ba-8fe2-7829abf71e0f:7|exmpale_github_blog|https://github.com/blog?after=Y3Vyc29yOnYyOpK0MjAxOC0wMS0xOVQyMjowMjo0NVrNCbw%3D|Feb 25, 2018 10:39:53 AM|chenghaodeMacBook-Pro.local|Warting       |<empty>                 |<empty>                 |Feb 25, 2018 10:39:53 AM|                                     |
+--------------------------------------+-------------------+--------------------------------------------------------------------------------+------------------------+---------------------------+--------------+------------------------+------------------------+------------------------+-------------------------------------+
|4bcca58e-60de-4733-97b6-68c17ebb1991:5|exmpale_github_blog|https://github.com/blog/                                                        |Feb 25, 2018 10:39:46 AM|chenghaodeMacBook-Pro.local|Finished      |Feb 25, 2018 10:39:46 AM|Feb 25, 2018 10:39:53 AM|Feb 25, 2018 10:39:53 AM|Run at vm1.  |
+--------------------------------------+-------------------+--------------------------------------------------------------------------------+------------------------+---------------------------+--------------+------------------------+------------------------+------------------------+-------------------------------------+

```

Check the parsed result in the `file_output` directory, that is be saved as as json format file.

```
$ ll /tmp/pangolin_exmaple_github_blog/
total 56
-rw-r--r--  1 sorenxing  wheel     55  2 24 13:35 _index.json
-rw-r--r--  1 sorenxing  wheel  20951  2 24 13:35 exmpale_github_blog_2018_02_24_13:35:39.213.CST.txt

$ cat /tmp/pangolin_exmaple_github_blog/exmpale_github_blog_2018_02_24_13:35:39.213.CST.txt
{"list":[{"children":{"title":[{"html":"Weak cryptographic standards removed","attrs":{"rel":"bookmark","href":"/blog/2507-weak-cryptographic-standards-removed"},"text":"Weak cryptographic ...
```

Of course, you can also use some tools like [https://jsonformatter.org/](jsonformatter.org) to beautify the json content

```
{
  "list": [
    {
      "children": {
        "title": [
          {
            "html": "Weak cryptographic standards removed",
            "attrs": {
              "rel": "bookmark",
              "href": "/blog/2507-weak-cryptographic-standards-removed"
            },
            "text": "Weak cryptographic standards removed"
          }
        ],
        "date": [
          {
            "html": "<svg aria-hidden=\"true\" class=\"octicon octicon-calendar\" height=\"16\" version=\"1.1\" viewbox=\"0 0 14 16\" width=\"14\">\n <path fill-rule=\"evenodd\" d=\"M13 2h-1v1.5c0 .28-.22.5-.5.5h-2c-.28 0-.5-.22-.5-.5V2H6v1.5c0 .28-.22.5-.5.5h-2c-.28 0-.5-.22-.5-.5V2H2c-.55 0-1 .45-1 1v11c0 .55.45 1 1 1h11c.55 0 1-.45 1-1V3c0-.55-.45-1-1-1zm0 12H2V5h11v9zM5 3H4V1h1v2zm6 0h-1V1h1v2zM6 7H5V6h1v1zm2 0H7V6h1v1zm2 0H9V6h1v1zm2 0h-1V6h1v1zM4 9H3V8h1v1zm2 0H5V8h1v1zm2 0H7V8h1v1zm2 0H9V8h1v1zm2 0h-1V8h1v1zm-8 2H3v-1h1v1zm2 0H5v-1h1v1zm2 0H7v-1h1v1zm2 0H9v-1h1v1zm2 0h-1v-1h1v1zm-8 2H3v-1h1v1zm2 0H5v-1h1v1zm2 0H7v-1h1v1zm2 0H9v-1h1v1z\" />\n</svg> February 23, 2018",
            "attrs": {
              "class": "meta-item"
            },
            "text": "February 23, 2018"
          }
        ],
        "author": [
          {
            "html": "<img alt=\"@ptoomey3\" class=\"author-avatar\" src=\"https://avatars2.githubusercontent.com/u/103360?s=36&amp;v=4\" height=\"18\" width=\"18\"> <a href=\"/ptoomey3\">ptoomey3</a>",
            "attrs": {
              "class": "fn meta-item"
            },
            "text": "ptoomey3"
          }
        ]
      },
      "attrs": {
        "class": "hentry blog-post "
      }
    },
    {
      "children": {
        "title": [
          {
            "html": "Label improvements: emoji, descriptions, and more",
            "attrs": {
              "rel": "bookmark",
              "href": "/blog/2505-label-improvements-emoji-descriptions-and-more"
            },
            "text": "Label improvements: emoji, descriptions, and more"
          }
        ],
        "date": [
          {
            "html": "<svg aria-hidden=\"true\" class=\"octicon octicon-calendar\" height=\"16\" version=\"1.1\" viewbox=\"0 0 14 16\" width=\"14\">\n <path fill-rule=\"evenodd\" d=\"M13 2h-1v1.5c0 .28-.22.5-.5.5h-2c-.28 0-.5-.22-.5-.5V2H6v1.5c0 .28-.22.5-.5.5h-2c-.28 0-.5-.22-.5-.5V2H2c-.55 0-1 .45-1 1v11c0 .55.45 1 1 1h11c.55 0 1-.45 1-1V3c0-.55-.45-1-1-1zm0 12H2V5h11v9zM5 3H4V1h1v2zm6 0h-1V1h1v2zM6 7H5V6h1v1zm2 0H7V6h1v1zm2 0H9V6h1v1zm2 0h-1V6h1v1zM4 9H3V8h1v1zm2 0H5V8h1v1zm2 0H7V8h1v1zm2 0H9V8h1v1zm2 0h-1V8h1v1zm-8 2H3v-1h1v1zm2 0H5v-1h1v1zm2 0H7v-1h1v1zm2 0H9v-1h1v1zm2 0h-1v-1h1v1zm-8 2H3v-1h1v1zm2 0H5v-1h1v1zm2 0H7v-1h1v1zm2 0H9v-1h1v1z\" />\n</svg> February 22, 2018",
            "attrs": {
              "class": "meta-item"
            },
            "text": "February 22, 2018"
          }
        ],
        "author": [
          {
            "html": "<img alt=\"@cheshire137\" class=\"author-avatar\" src=\"https://avatars1.githubusercontent.com/u/82317?s=36&amp;v=4\" height=\"18\" width=\"18\"> <a href=\"/cheshire137\">cheshire137</a>",
            "attrs": {
              "class": "fn meta-item"
            },
            "text": "cheshire137"
          }
        ]
      },
      "attrs": {
        "class": "hentry blog-post "
      }
    },
    ...
  ]
}
```

## License
Pangolin Crawler is released as open source software under the [GPL v3](https://opensource.org/licenses/gpl-3.0.html) 
license, see the [LICENSE](./LICENSE) file in the project root for the full license text.
