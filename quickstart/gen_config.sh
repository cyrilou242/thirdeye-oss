#LDAP - DIY
#JIRA - DIY

## DataSource - MySQL - VALUES CAN BE CHANGED HERE
export DATASOURCE_MYSQL_HOST=${DATASOURCE_MYSQL_HOST:="host.docker.internal"}  #resolves to localhost of docker host when accessed from ThirdEye container
export DATASOURCE_MYSQL_PORT=${DATASOURCE_MYSQL_PORT:="3306"}  #default mysql port
export DATASOURCE_MYSQL_DATABASE=${DATASOURCE_MYSQL_DATABASE:="testdata"}
export DATASOURCE_MYSQL_USER=${DATASOURCE_MYSQL_USER:="root"}
export DATASOURCE_MYSQL_PASS=${DATASOURCE_MYSQL_PASS:=""}


## Below: Change only if you know what you are doing
## Persistence - Where TE stores its metadata - MySql
export PERSISTENCE_MYSQL_HOST=${PERSISTENCE_MYSQL_HOST:="host.docker.internal"}  #resolves to localhost of docker host when accessed from ThirdEye container
export PERSISTENCE_MYSQL_PORT=${PERSISTENCE_MYSQL_PORT:="3306"}  #default mysql port
export PERSISTENCE_MYSQL_DATABASE=${PERSISTENCE_MYSQL_DATABASE:="thirdeye"}
export PERSISTENCE_MYSQL_USER=${PERSISTENCE_MYSQL_USER:="root"}
export PERSISTENCE_MYSQL_PASS=${PERSISTENCE_MYSQL_PASS:=""}


## Config generation based on env var
CONFIG_FOLDER=config
TEMPLATES_FOLDER=config-templates

mkdir -p ./${CONFIG_FOLDER}/data-sources
mkdir -p ./${CONFIG_FOLDER}/detector-config/anomaly-functions/
envsubst < ${TEMPLATES_FOLDER}/data-sources/cache-config.yml > ${CONFIG_FOLDER}/data-sources/cache-config.yml
envsubst < ${TEMPLATES_FOLDER}/data-sources/data-sources-config.yml > ${CONFIG_FOLDER}/data-sources/data-sources-config.yml
envsubst < ${TEMPLATES_FOLDER}/detector-config/anomaly-functions/alertFilter.properties > ${CONFIG_FOLDER}/detector-config/anomaly-functions/alertFilter.properties
envsubst < ${TEMPLATES_FOLDER}/detector-config/anomaly-functions/alertFilterAutotune.properties > ${CONFIG_FOLDER}/detector-config/anomaly-functions/alertFilterAutotune.properties
envsubst < ${TEMPLATES_FOLDER}/detector-config/anomaly-functions/functions.properties > ${CONFIG_FOLDER}/detector-config/anomaly-functions/functions.properties
envsubst < ${TEMPLATES_FOLDER}/dashboard.yml > ${CONFIG_FOLDER}/dashboard.yml
envsubst < ${TEMPLATES_FOLDER}/detector.yml > ${CONFIG_FOLDER}/detector.yml
envsubst < ${TEMPLATES_FOLDER}/persistence.yml > ${CONFIG_FOLDER}/persistence.yml
envsubst < ${TEMPLATES_FOLDER}/rca.yml > ${CONFIG_FOLDER}/rca.yml
OS=$(uname -s)
case "${OS}" in
    Linux*)     decode=d;;
    Darwin*)    decode=D;;
    *)          decode=d
esac

