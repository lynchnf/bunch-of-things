#!/bin/bash
base_dir=$(dirname "$0")
cd $base_dir
java -jar ../lib/bunch-of-things-${pom.version}.jar
exit 0
