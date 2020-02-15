#!/bin/bash

set -e

ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../.."
PO_DIR=$ROOT/server/src/main/resources/locale
PACKAGE=de.njsm.movielist.util.locale
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