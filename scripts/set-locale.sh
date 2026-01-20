#!/bin/bash

# Locale convenience table
declare -A locales=(
    ["en"]="en_US.UTF-8"
    ["bg"]="bg_BG.UTF-8"
)

# Disallow blank "locale" param
[ -z "$1" ] && \
printf "%s\n" "ERROR: No locale provided. Choose on of: ${!locales[@]}" | \
paste -sd, && \
exit 1

# Generate the selected locale at system-level
locale="${locales["$(echo "$1" | tr '[:upper:]' '[:lower:]')"]}"
locale-gen "$locale"

# Set env vars
export LANG=$locale
export LC_ALL=$locale

# Restart the shell
exec bash