#!/bin/bash
# movie-list is a website to manage a large list of movies
# Copyright (C) 2019  The movie-list developers
#
# This file is part of the movie-list program suite.
#
# movie-list is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# movie-list is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.


set -e

ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../.."
PO_DIR=$ROOT/server/src/main/resources/locale
PACKAGE=de.njsm.movielist.server.util.locale
PACKAGE_DIR=$(echo $PACKAGE | tr '.' '/')
TARGET_ROOT=$ROOT/server/target/generated-sources/java
TARGET=$TARGET_ROOT/$PACKAGE_DIR
LANGUAGES=('de' 'en')

mkdir -p $TARGET

for lang in "${LANGUAGES[@]}" ; do
  msgfmt --locale $lang --java2 --source -d $TARGET_ROOT $PO_DIR/$lang.po
  sed -i "1 i\package $PACKAGE;" $TARGET_ROOT/Messages_$lang.java
  mv $TARGET_ROOT/Messages_$lang.java $TARGET
done