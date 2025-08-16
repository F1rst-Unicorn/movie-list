#!/bin/bash

DEPLOYMENT_VM="${DEPLOYMENT_VM:-dp-server}"

ssh ${DEPLOYMENT_VM} "
        sudo pacman -Rsn movie-list movie-list-static --noconfirm;
        sudo rm -rf /usr/share/movie-list;
        sudo rm -rf /var/log/tomcat10/movie-list.log;
        sudo touch /var/log/tomcat10/movie-list.log;
        sudo chown tomcat10:tomcat10 /var/log/tomcat10/movie-list.log
        sudo setfacl -m u:jan:r /var/log/tomcat10/movie-list.log
        while [ -d /usr/share/tomcat10/webapp/movie-list ] ; do sleep 1 ; done;
        sudo -u postgres psql -c \"\$(
            sudo -u postgres psql -c \"
                select 'drop table if exists \\\"' || tablename || '\\\" cascade;' from pg_tables where schemaname = 'public';\" movie_list | tail -n +3 | head -n -2
        )\" movie_list
        "

exit 0
