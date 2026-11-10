#!/usr/bin/env bash
set -e

cd "$(dirname "$0")"

./mvnw clean package

if [ "$1" = "run" ]; then
    java -jar target/Lab2-1.0-SNAPSHOT.jar
fi
