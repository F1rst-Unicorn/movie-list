#!/bin/bash


ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../.."

set -e

rm -rf "$ROOT/server/target/server.log"

cd "$ROOT/deploy"

ansible-playbook --extra-vars "ansible_become_pass=,ansible_sudo_pass= "    \
        "$ROOT/deploy/play_install.yml"

while ! curl -v http://dp-server/movie-list/login 2>&1 | grep "HTTP/1.1 200" >/dev/null ; do
    sleep 1
done

cd "$ROOT"
