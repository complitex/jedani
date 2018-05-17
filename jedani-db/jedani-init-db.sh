#!/bin/bash

mysql -ujedani -pjedani jedani < jedani-create.sql
mysql -ujedani -pjedani jedani < jedani-insert.sql