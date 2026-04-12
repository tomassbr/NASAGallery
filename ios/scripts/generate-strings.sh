#!/bin/zsh -l

# This ensures that relative paths are correct no matter where the script is executed
cd "$(dirname "$0")"

cd ../..

export JAVA_HOME=$(/usr/libexec/java_home -v 21 2>/dev/null || /usr/libexec/java_home -v 17 2>/dev/null)
echo "Generating MR resources from .xml files"
./gradlew :shared:base:generateMRcommonMain < /dev/null
