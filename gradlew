#!/bin/sh
set -eu
ROOT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
GRADLE_VERSION="8.7"
GRADLE_DIR="$ROOT_DIR/.gradle-dist/gradle-$GRADLE_VERSION"
if [ ! -x "$GRADLE_DIR/bin/gradle" ]; then
  mkdir -p "$ROOT_DIR/.gradle-dist"
  ZIP="$ROOT_DIR/.gradle-dist/gradle-$GRADLE_VERSION-bin.zip"
  echo "Downloading Gradle $GRADLE_VERSION..."
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL --retry 3 -o "$ZIP" "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
  else
    wget -q --tries=3 -O "$ZIP" "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
  fi
  if command -v unzip >/dev/null 2>&1; then
    unzip -q -o "$ZIP" -d "$ROOT_DIR/.gradle-dist"
  else
    echo "unzip is required" >&2
    exit 1
  fi
fi
exec "$GRADLE_DIR/bin/gradle" "$@"
