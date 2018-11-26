#!/usr/bin/env bash

webcrawler_home=$(dirname $0)/..

java -cp "${webcrawler_home}/lib" -jar "${webcrawler_home}/lib/web_crawler.jar" $@

exit $?
