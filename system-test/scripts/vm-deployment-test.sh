#!/bin/bash


ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../.."
DEPLOYMENT_VM="${DEPLOYMENT_VM:-dp-server}"

set -e

rm -rf "$ROOT/server/target/server.log"

cd "$ROOT/deploy"

sed -i "s/dp-server/$DEPLOYMENT_VM/g" $ROOT/deploy/inventory
ansible-playbook --extra-vars "ansible_become_pass=,ansible_sudo_pass= "    \
        "$ROOT/deploy/play_install.yml"
git checkout -- $ROOT/deploy/inventory

while ! curl -v http://${DEPLOYMENT_VM}/movie-list/login 2>&1 | grep "HTTP/1.1 200" >/dev/null ; do
    sleep 1
done

cd "$ROOT"
