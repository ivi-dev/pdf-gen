#!/bin/bash

# Require exactly one of the long options: --major, --minor, --fix
MAJOR=false
MINOR=false
FIX=false
# By default append -SNAPSHOT unless explicitly disabled with --no-snapshot
SNAPSHOT=true
NO_SNAPSHOT=false

for a in "$@"; do
    case "$a" in
        --major) MAJOR=true ;;
        --minor) MINOR=true ;;
        --fix)   FIX=true ;;
        --no-snapshot|-ns) NO_SNAPSHOT=true ;;
        *)
            echo "Unknown option: $a"
            echo "Usage: increment.sh --major|--minor|--fix [--snapshot|-s|--no-snapshot]"
            exit 1
            ;;
    esac
done

# Ensure exactly one option was provided
count=0
[ "$MAJOR" = true ] && count=$((count+1))
[ "$MINOR" = true ] && count=$((count+1))
[ "$FIX" = true ] && count=$((count+1))
    if [ "$count" -eq 0 ]; then
    # No explicit segment option provided — default to patch/fix.
    FIX=true
elif [ "$count" -ne 1 ]; then
    echo "You must provide exactly one of --major, --minor or --fix"
    echo "Usage: increment.sh --major|--minor|--fix [--snapshot|-s|--no-snapshot]"
    exit 1
fi

# Map option to command
if [ "$MAJOR" = true ]; then
    CMD=major
elif [ "$MINOR" = true ]; then
    CMD=minor
else
    CMD=fix
fi

# Determine suffix for newVersion: append -SNAPSHOT only when requested
if [ "${NO_SNAPSHOT:-false}" = true ]; then
    SUFFIX=''
elif [ "${SNAPSHOT:-false}" = true ]; then
    SUFFIX='-SNAPSHOT'
else
    SUFFIX=''
fi
case "$CMD" in
    major)
        mvn build-helper:parse-version versions:set \
            -DnewVersion='${parsedVersion.nextMajorVersion}.0.0'"$SUFFIX" \
            -DgenerateBackupPoms=false
        ;;
    minor)
        mvn build-helper:parse-version versions:set \
            -DnewVersion='${parsedVersion.majorVersion}.${parsedVersion.nextMinorVersion}.0'"$SUFFIX" \
            -DgenerateBackupPoms=false
        ;;
    fix)
        mvn build-helper:parse-version versions:set \
            -DnewVersion='${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.nextIncrementalVersion}'"$SUFFIX" \
            -DgenerateBackupPoms=false
        ;;
esac