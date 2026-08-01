#!/bin/sh

APP_ROOT=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
APP_HOME="$APP_ROOT"

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

if [ ! -f "$CLASSPATH" ]; then
  echo "Gradle wrapper JAR not found. Please run gradle wrapper first." >&2
  exit 1
fi

JAVA_EXE=${JAVA_HOME:+$JAVA_HOME/bin/java}
if [ -z "$JAVA_EXE" ]; then
  JAVA_EXE=java
fi

exec "$JAVA_EXE" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
