#!/bin/bash

SCRIPTS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

java -jar "$SCRIPTS_DIR/../target/pdfgen-1.0-SNAPSHOT.jar" "$@"