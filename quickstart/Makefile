PERSISTENCE_DB_NAME = thirdeye # do not change if you don't know what you're doing, it's hardcoded elsewhere
PERSISTENCE_MYSQL_HOST ?= 127.0.0.1
PERSISTENCE_MYSQL_USER ?=root
PERSISTENCE_MYSQL_TE_PWD ?=""
PERSISTENCE_MYSQL_PORT ?= 3306

DATASOURCE_DB_NAME = testdata # do not change if you don't know what you're doing, it's hardcoded elsewhere
DATASOURCE_MYSQL_HOST ?= 127.0.0.1
DATASOURCE_MYSQL_USER ?=root
DATASOURCE_MYSQL_TE_PWD ?=""
DATASOURCE_MYSQL_PORT ?= 3306


default:
	@echo "Make is installed."

create-persistence-db-tables:
	mysql -h ${PERSISTENCE_MYSQL_HOST} -P ${PERSISTENCE_MYSQL_PORT} -u ${PERSISTENCE_MYSQL_USER} -p ${PERSISTENCE_MYSQL_TE_PWD} < sql_scripts/create-persistence-db-tables.sql
	@echo "Databse thirdeye created. Tables created."

drop-persistence-tables:
	mysql -h ${PERSISTENCE_MYSQL_HOST} -P ${PERSISTENCE_MYSQL_PORT} -u ${PERSISTENCE_MYSQL_USER} -p ${PERSISTENCE_MYSQL_TE_PWD} < sql_scripts/drop-persistence-tables.sql
	@echo "Thirdeye persistence tables deleted."

create-datasource-tables:
	mysql -h ${PERSISTENCE_MYSQL_HOST} -P ${PERSISTENCE_MYSQL_PORT} -u ${PERSISTENCE_MYSQL_USER} -p ${PERSISTENCE_MYSQL_TE_PWD} < sql_scripts/create-datasource-db-tables.sql
	@echo "Thirdeye testdata databse created. Data generated in table transactions."
