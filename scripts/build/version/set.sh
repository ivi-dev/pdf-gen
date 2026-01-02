#!/bin/bash

# Parse options (options start with '-') and collect positionals
OPTIONS=()
POSITIONALS=()
for a in "$@"; do
	case "$a" in
		-*) OPTIONS+=("$a") ;;
		*) POSITIONALS+=("$a") ;;
	esac
done

# Helper: return 0 if option is present
is_option_set() {
	local want="$1"
	for o in "${OPTIONS[@]}"; do
		if [ "$o" = "$want" ]; then
			return 0
		fi
	done
	return 1
}

# Detect snapshot option (accept --snapshot or -s)
SNAPSHOT=false
if is_option_set "--snapshot" || is_option_set "-s"; then
	SNAPSHOT=true
fi

# Use first non-option positional as the version value (fallback to $1)
VERSION="${POSITIONALS[0]:-$1}"

if [ -z "$VERSION" ]; then
	echo "Usage: set.sh [options] <version>"
	echo "Options: --snapshot, -s"
	exit 1
fi

# If snapshot option is set and VERSION doesn't already end with -SNAPSHOT, append it
if [ "$SNAPSHOT" = true ]; then
	case "$VERSION" in
		*-SNAPSHOT) ;;
		*) VERSION="${VERSION}-SNAPSHOT" ;;
	esac
fi

mvn versions:set -DnewVersion="$VERSION" -DgenerateBackupPoms=false

# Report the effective version setting it
NEWVER=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout 2>/dev/null || true)
if [[ -n "$NEWVER" ]]; then
    echo "Set $NEWVER as the new version"
fi