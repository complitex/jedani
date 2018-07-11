#!/bin/bash

mysql -ujedani -pjedani jedani_db < jedani-create.sql
mysql -ujedani -pjedani jedani_db < jedani-insert.sql