#!/bin/bash

set -euo pipefail
set -x

builtin cd "$(dirname -- "${BASH_SOURCE[0]}")"

server="root@138.68.3.211"

./gradlew jar

jar_file=(build/libs/*.jar)
if [ "${#jar_file[@]}" -ne 1 ]; then
    echo "Could not determine the JAR file to deploy"
    exit 1
fi

version="$(md5sum < "$jar_file" | awk '{ print $1 }')"
deployed_code="/tmp/project-$(date +%s)-$version"

rm -rf "$deployed_code"
mkdir -p "$deployed_code"

cp "$jar_file" "$deployed_code/project.jar"
cp -R src/main/deploy "$deployed_code"
cp simConfig.txt "$deployed_code"

scp -r "$deployed_code" "$server":"$deployed_code"

ssh "$server" "launch_robot_code.sh $deployed_code $@"