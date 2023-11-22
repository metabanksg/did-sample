#!/bin/bash

echo "begin to build did-sample."

if [ -n "$1" ] ;then
    sed -i "/^repoType/crepoType=$1" gradle.properties
fi

#build
chmod u+x gradlew

./gradlew clean build -x checkMain
if [[ $? -ne 0 ]];then
	echo "gradle build did-sample failed"
	exit 1
fi
echo "build did-sample success."
cp -fr libs/* dist/lib/

mkdir -p logs

exit 0
