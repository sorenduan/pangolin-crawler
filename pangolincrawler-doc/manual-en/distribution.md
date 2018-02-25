# Distribution mode


## Database configuration

By default, pangolin uses an embedded h2 database , bug in distributed mode, you must use a independent database such as mysql or postgres and so on.

### MySQL integration

You can download a mysql installer from [bitnami.com](https://bitnami.com/stack/mysql).

#### create database table

Create database table with the sql script in then pangolin pakcage's `script` directory.

```
mysql> source /your_pangolin_hom/script/pangolin_db_mysql.sql
```

#### jdbc configuration 

```
spring.datasource.url=jdbc:mysql://localhost:3306/<your database name>?createDatabaseIfNotExist=true
spring.datasource.username=<your username>
spring.datasource.password=<your password>
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
```

### Postgres integration

TODO

## Quartz scheduler configuration in distribution mode

### Create Quartz database table.

You can find the sql script in the jar file of 'quartz-2.3.0.jar'

```
$ unzip quartz-2.3.0.jar
$ tree -f | grep sql
│   │   │   │   ├── ./org/quartz/impl/jdbcjobstore/tables_cloudscape.sql
│   │   │   │   ├── ./org/quartz/impl/jdbcjobstore/tables_cubrid.sql
│   │   │   │   ├── ./org/quartz/impl/jdbcjobstore/tables_db2.sql
│   │   │   │   ├── ./org/quartz/impl/jdbcjobstore/tables_db2_v72.sql
│   │   │   │   ├── ./org/quartz/impl/jdbcjobstore/tables_db2_v8.sql
│   │   │   │   ├── ./org/quartz/impl/jdbcjobstore/tables_db2_v95.sql
│   │   │   │   ├── ./org/quartz/impl/jdbcjobstore/tables_derby.sql
│   │   │   │   ├── ./org/quartz/impl/jdbcjobstore/tables_derby_previous.sql
│   │   │   │   ├── ./org/quartz/impl/jdbcjobstore/tables_firebird.sql
│   │   │   │   ├── ./org/quartz/impl/jdbcjobstore/tables_h2.sql
│   │   │   │   ├── ./org/quartz/impl/jdbcjobstore/tables_hsqldb.sql
│   │   │   │   ├── ./org/quartz/impl/jdbcjobstore/tables_hsqldb_old.sql
│   │   │   │   ├── ./org/quartz/impl/jdbcjobstore/tables_informix.sql
│   │   │   │   ├── ./org/quartz/impl/jdbcjobstore/tables_mysql.sql
│   │   │   │   ├── ./org/quartz/impl/jdbcjobstore/tables_mysql_innodb.sql
│   │   │   │   ├── ./org/quartz/impl/jdbcjobstore/tables_oracle.sql
│   │   │   │   ├── ./org/quartz/impl/jdbcjobstore/tables_pointbase.sql
│   │   │   │   ├── ./org/quartz/impl/jdbcjobstore/tables_postgres.sql
│   │   │   │   ├── ./org/quartz/impl/jdbcjobstore/tables_sapdb.sql
│   │   │   │   ├── ./org/quartz/impl/jdbcjobstore/tables_solid.sql
│   │   │   │   ├── ./org/quartz/impl/jdbcjobstore/tables_sqlServer.sql
│   │   │   │   └── ./org/quartz/impl/jdbcjobstore/tables_sybase.sql

$ mysql
msyql> source ./org/quartz/impl/jdbcjobstore/tables_mysql_innodb.sql

```

### Quartz configuration.

Access the [quartz site](http://www.quartz-scheduler.org/documentation/quartz-2.x/configuration/ConfigJDBCJobStoreClustering.html) for more information.

```
org.quartz.jobStore.class=org.quartz.impl.jdbcjobstore.JobStoreTX
org.quartz.jobStore.driverDelegateClass=org.quartz.impl.jdbcjobstore.StdJDBCDelegate
org.quartz.jobStore.dataSource=quartzDs

org.quartz.dataSource.quartzDs.driver=com.mysql.jdbc.Driver
org.quartz.dataSource.quartzDs.URL=jdbc:mysql://localhost:3306/<your database name>?createDatabaseIfNotExist=true
org.quartz.dataSource.myDS.user=<your username>
org.quartz.dataSource.quartzDs.password=<your password>

org.quartz.jobStore.tablePrefix=QRTZ_
org.quartz.jobStore.isClustered=true

```


## cache server configuration

### Memcached 

#### Memcached Install

You can use `brew install memcached` on MacOS.

#### configuration

```
pangolin.cache.service.impl=org.pangolincrawler.core.cache.impl.MemcachedCacheService
pangolin.cache.memcached.servers=127.0.0.1:11211,127.0.0.1:11212
```

### redis configuration 

TODO

### config 

## Message Queue configuration

### ActiveMQ  integration

```
spring.activemq.broker-url=tcp://localhost:61616
spring.activemq.user=admin
spring.activemq.password=admin
spring.activemq.in-memory=false
spring.activemq.pool.enabled=true
```

