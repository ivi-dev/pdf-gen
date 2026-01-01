#!/bin/bash

SCRIPTS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

bash "$SCRIPTS_DIR/build/lean.sh"
bash "$SCRIPTS_DIR/run.sh" "$@"
