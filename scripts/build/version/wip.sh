#!/bin/bash

mvn build-helper:parse-version versions:set \
    -DnewVersion='${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.incrementalVersion}-SNAPSHOT' \
    -DgenerateBackupPoms=false

# Report the effective version when a snapshot suffix is applied
NEWVER=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout 2>/dev/null || true)
if [[ -n "$NEWVER" && "$NEWVER" == *-SNAPSHOT ]]; then
    echo "Marked $NEWVER as SNAPSHOT version"
fi