#!/bin/bash

ssh dp-server "
        sudo pacman -Rsn movie-list movie-list-static --noconfirm;
        sudo rm -rf /usr/share/movie-list;
        while [ -d /usr/share/tomcat8/webapp/movie-list ] ; do sleep 1 ; done;
        sudo -u postgres psql -c \"\$(
            sudo -u postgres psql -c \"
                select 'drop table if exists \\\"' || tablename || '\\\" cascade;' from pg_tables where schemaname = 'public';\" movie_list | tail -n +3 | head -n -2
        )\" movie_list
        "

exit 0
