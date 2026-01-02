#!/bin/bash

mvn build-helper:parse-version versions:set \
    -DnewVersion='${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.incrementalVersion}' \
    -DgenerateBackupPoms=false

# Report the effective version when finalizing (non-SNAPSHOT) version
NEWVER=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout 2>/dev/null || true)
if [[ -n "$NEWVER" ]]; then
    echo "Marked $NEWVER as final version"
fi