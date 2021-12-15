#!/bin/bash

# [Optional] Uncomment below lines to build with latest Pinot changes
# cd ..
# mvn install -DskipTests -pl pinot-common,pinot-core,pinot-spi,pinot-java-client -am -Pbuild-shaded-jar || exit 1
# cd thirdeye

PROFILES_ARG=""

if [ $# -ne 0 ]
then
PROFILES_ARG="-P "
for var in "$@"
do
  PROFILES_ARG=${PROFILES_ARG}${var},
done
PROFILES_ARG=${PROFILES_ARG%,}
fi


echo "*******************************************************"
echo "Building ThirdEye"
echo "*******************************************************"

mvn install -DskipTests ${PROFILES_ARG}|| exit 1
# don't build frontend
#mvn install -pl "!thirdeye-frontend" -DskipTests ${PROFILES_ARG}|| exit 1
