#!/usr/bin/env bash

SET GLASSFISH_ASADMIN=/opt/glassfish/bin/asadmin.sh

call %GLASSFISH_ASADMIN% create-jdbc-connection-pool \
    --datasourceclassname="com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource" \
    --restype="javax.sql.ConnectionPoolDataSource" \
    --property="url=jdbc\:mysql\://localhost\:3306/jedani:user=jedani:password=jedani:characterResultSets=utf8:characterEncoding=utf8:useUnicode=true:connectionCollation=utf8_unicode_ci:autoReconnect=true" jedaniPool

call %GLASSFISH_ASADMIN% create-jdbc-resource --connectionpoolid jedaniPool jdbc/jedaniResource

call %GLASSFISH_ASADMIN% create-auth-realm \
    --classname com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm \
    --property jaas-context=jdbcRealm:\
    datasource-jndi=jdbc/pspofficeResource:\
    user-table=user:\
    user-name-column=login:\
    password-column=password:\
    group-table=user_group:\
    group-name-column=name:\
    charset=UTF-8 jedaniRealm