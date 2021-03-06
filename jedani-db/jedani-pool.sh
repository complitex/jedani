#!/bin/bash

export GLASSFISH_ASADMIN="/opt/glassfish5/bin/asadmin"

$GLASSFISH_ASADMIN create-jdbc-connection-pool \
    --datasourceclassname="com.mysql.cj.jdbc.MysqlConnectionPoolDataSource" \
    --restype="javax.sql.ConnectionPoolDataSource" \
    --property="url=jdbc\:mysql\://localhost\:3306/jedani_db:user=jedani:password=jedani:\
    characterResultSets=utf8:characterEncoding=utf8:useUnicode=true:connectionCollation=utf8_unicode_ci:\
    autoReconnect=true:useSSL=false" jedaniPool

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
    charset=UTF-8:\
    digest-algorithm=SHA-256" jedaniRealm
