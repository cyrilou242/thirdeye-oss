# Thirdeye OSS quickstart
This is a quickstart base for the OSS ThirdEye.
[See an introduction to Thirdeye here](https://engineering.linkedin.com/blog/2019/01/introducing-thirdeye--linkedins-business-wide-monitoring-platfor).
It does not contain thirdeye sources, but only a custom Dockerfile and some internal resources. 
See the resources in [internal-res](internal_res) folder.  

This project is made to test and manage everything related to configurations and operations of ThirdEye.

## Pre requisites: 
- `mysql` on your machine, `mysql` cli. Tested with mysql5.7.
- `docker` (recent version), `docker-compose`. 
  Recommended config: Memory >=4Gb, Swap>=1.5gb, Disk Image: >=5Gb free.
- `make`. run `make` in your shell to check.
- `envsubst`. Run `envsubst --help` in your shell to check.

## Prepare SQL tables
The tutorial assumes a local mysql instance, and uses user `root` and empty password to create things. 
It can be changed with environment variables, if need be.

Tables for ThirdEye metadata persistence:
You may be prompted for your password.
    
    # creation in database thirdeye
    make create-persistence-db-tables

Demo table with some data:
You may be prompted for your password.
    
    # creation in database testdata - replace by your own when you're ready
    make create-datasource-tables

## Quick Local Start

    # build - may take some time
    docker build -t thirdeye -f Dockerfile .
    
    # generate a config - it will be mounted to the docker container
    ./gen_config.sh

    # run 
    docker-compose up
    
Then go to [localhost:1426](http://localhost:1426/)

This setup generates the config from [configs-templates](config-templates).  
This quick start can be used to test modifications to the config files.  
Stop the docker containers: `docker-compose down`; edit the configurations in the [config](./config) folder, and relaunch with `docker-compose up`    
You do not need to rebuild the docker image.

## Playing with ThirdEye
### Add metric from testdata: 
- Go to the [import metrics page](http://localhost:1426/app/#/self-serve/import-sql-metric)
- select the Mysql database
- Fill the config:

  - table name: `transactions`
  - time column: `timestamp_millis`
  - timezone: `UTC`
  - time format: `EPOCH`
  - time granularity: `1MILLISECONDS`
  - Click Add Dimension
    - Dimension 0: `device`
  - Click Add metric
    - Metrics 0: `transaction_value`
    - Aggregation Method: `SUM`
  
Check the schema/values of `transactions` with your mysql client to understand the config above.

- Click on [root cause analysis](http://localhost:1426/app/#/rootcause) in the top bar.
- Type transaction_value in the metric bar and click on it.
- Change the window config:
  - Display Window: Oct 1 - Oct 22
  - Granularity: 1 Day

- Play around: 
  - change the baseline
  - set the investigation period to October 11 - October 13 
  - see what dimensions is detected as anomalous by the algorithm
  - play with the heatmap
  - add to chart with filters on mobile, see if the algorithm was correct!
  

Next: reproduce with your own MySQL instance/tables.
You can add MySQL source in [config-templates/data-sources/data-sources-config.yml](config-templates/data-sources/data-sources-config.yml)

### Create alert
See [alert setup doc](https://thirdeye.readthedocs.io/en/latest/alert_setup.html#alert-setup).

### Admin
You can go to [the admin page](http://localhost:1426/thirdeye-admin) to see artefacts created by TE.

## Contributing
### Contributing to ThirdEye
When possible, try to contribute directly to the [open source project](https://github.com/project-thirdeye/thirdeye).
To change the ThirdEye source code used at build, change `PINOT_BRANCH` and `PINOT_GIT_URL` values in the [dockerfile](Dockerfile).

### Contributing to this project 
Please contact Cyril de Catheu.

## Docker help
Read if your new to docker.
### How to build a docker image

Usage:

```SHELL
docker build --no-cache -t [Docker Tag] --build-arg PINOT_BRANCH=[PINOT_BRANCH] --build-arg PINOT_GIT_URL=[PINOT_GIT_URL] --build-arg MAVEN_VERSION=[MAVEN_VERSION] -f Dockerfile .
```

Example

```SHELL
docker build --no-cache -t thirdeye --build-arg PINOT_BRANCH=mysql-quickstart --build-arg PINOT_GIT_URL=https://github.com/cyrilou242/incubator-pinot.git --build-arg MAVEN_VERSION=3.6.3 -f Dockerfile .
# equivalent to
docker build -t thirdeye -f Dockerfile .
```

### How to Run interactive mode

##### Interactive launch dashboard server with default configuration 

```SHELL
docker run -it --name thirdeye -p 1426:1426 -p 1427:1427 thirdeye:latest /bin/bash
./gen_config.sh
java -Dlog4j.configurationFile=log4j2.xml -cp "./bin/thirdeye-pinot.jar" org.apache.pinot.thirdeye.dashboard.ThirdEyeDashboardApplication "./config"
```

##### Interactive launch backend server with default configuration

```SHELL
docker run -it -p 1426:1426 -p 1427:1427 thirdeye:latest /bin/bash
./gen_config.sh
java -Dlog4j.configurationFile=log4j2.xml -cp "./bin/thirdeye-pinot.jar" org.apache.pinot.thirdeye.anomaly.ThirdEyeAnomalyApplication "./config"
```
