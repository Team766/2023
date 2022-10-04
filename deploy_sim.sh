#!/bin/bash

set -euo pipefail

builtin cd "$(dirname -- "${BASH_SOURCE[0]}")"

if [ $# -gt 0 ]; then
    server="$1"
elif [ -f sim_robots.lst ]; then
    PS3="Which sim robot would you like to use? "
    COLUMNS=1
    cancel_option="Cancel Deployment"
    select name in $(cat sim_robots.lst) "$cancel_option"
    do
        server="${name-"$REPLY"}"
        break
    done
    if [ "$server" == "$cancel_option" ]; then
        echo "Canceled"
        exit
    fi
fi

if [ -z "${server-}" ]; then
    # Sleep here is necessary to allow Theia to finish opening the task terminal
    # before displaying the error message.
    sleep 1
    echo "Please supply the URL of the sim robot"
    exit 1
fi

echo -e "Deploying to $server\n"

set -x

user="root"

./gradlew jar

jar_file=( build/libs/*.jar )
[ "${#jar_file[@]}" -eq 1 ] || (echo "Output jar file could not be determined"; exit 1)

version="$(md5sum < "$jar_file" | awk '{ print $1 }')"
deployed_code="/tmp/project-$(date +%s)-$version"

rm -rf "$deployed_code"
mkdir -p "$deployed_code"

cp "$jar_file" "$deployed_code/project.jar"
cp -R src/main/deploy "$deployed_code"
cp simConfig.txt "$deployed_code"

scp -o "StrictHostKeyChecking=no" -r "$deployed_code" "${user}@${server}":"$deployed_code"

ssh -o "StrictHostKeyChecking=no" "${user}@${server}" "launch_robot_code.sh $deployed_code $@"

set +x
echo -e "\nDeploy finished. Your code is running"
echo -e "\nOpen simulation viewer at http://$server"