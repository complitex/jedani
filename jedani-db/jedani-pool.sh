#!/bin/bash

export GLASSFISH_ASADMIN="/opt/glassfish/bin/asadmin"

$GLASSFISH_ASADMIN create-jdbc-connection-pool \
    --datasourceclassname="com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource" \
    --restype="javax.sql.ConnectionPoolDataSource" \
    --property="url=jdbc\:mysql\://localhost\:3306/jedani:user=jedani:password=jedani:characterResul

$GLASSFISH_ASADMIN create-jdbc-resource --connectionpoolid jedaniPool jdbc/Jedani

$GLASSFISH_ASADMIN create-auth-realm \
    --classname="com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm" \
    --property="jaas-context=jdbcRealm:\
    datasource-jndi=jdbc/Jedani:\
    user-table=user:\
    user-name-column=login:\
    password-column=password:\
    group-table=user_group:\
    group-name-column=name:\
    charset=UTF-8" jedaniRealm