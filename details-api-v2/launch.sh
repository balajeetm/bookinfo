#!/bin/sh
# Supported ENV variables:
# * MAX_HEAP: the max heap size to pass to JVM as Xmx.  If not set, resonable default is set.
# * SERVICE: the target jar to launch
# * COUCH_HOST: the IP or DNS name of the Couch host
#
# Use of additional Java options and arguments:
# * docker run service_image:tag -o '-myjavaoptions' -o '-Dmydefinition=myvalue' --my.arg1="quoted value with space" 
# * command above will start the java application as following:
# * java [default_options...] -myjavaoptions -Dmydefinition=myvalue -jar service.jar [default_args...] --my.arg1="quoted value with space"


### Set default environment variables
default_env_vars() {
  [ -z "$MAX_HEAP" ] && MAX_HEAP="512m"
}

### Construct ARGS and OPTIONS
default_cmd_args() {
  ARGS=""
  [ ! -z "$CONFIG_LOC" ] && ARGS="$ARGS --spring.config.location='$CONFIG_LOC'"

  OPTIONS=""
  [ ! -z "$MAX_HEAP" ] && OPTIONS="$OPTIONS -Xmx${MAX_HEAP}" 
}

### Parse arguments. Set $EXTRA_ARGS and EXTRA_OPTIONS 
parse_args() {
  EXTRA_ARGS=""
  EXTRA_OPTIONS=""
  while [ "$#" -gt 0 ] ; do
    case "$1" in
      --show-command)
        SHOW_COMMAND="true"
        shift
        ;;
      -o|--java_options)
        shift
        EXTRA_OPTIONS="$EXTRA_OPTIONS $1"
        shift
        ;;
      --*=*)
        # extra key-value args (add quotation)
        arg="${1%%=*}=\"${1#*=}\""
        echo $arg
        EXTRA_ARGS="$EXTRA_ARGS $arg"
        shift
        ;;
      *)
        # default as extra args
        EXTRA_ARGS="$EXTRA_ARGS $1"
        shift
        ;;
    esac
  done
}

### Main

default_env_vars
default_cmd_args
parse_args "$@"
SHOW_COMMAND="true"

cmd="exec java \
  -XX:MaxMetaspaceSize=256M \
  -XX:MaxTenuringThreshold=15 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/data/conf/dump.hprof \
  "
cmd=$cmd" ${OPTIONS}"
cmd=$cmd" ${EXTRA_OPTIONS}"
cmd=$cmd" -jar /service/app.jar"
cmd=$cmd" ${ARGS}"

[ ! -z "$SHOW_COMMAND" ] && echo $cmd

echo $cmd > run.sh
chmod 755 run.sh
echo "Testing"
cat run.sh
sh run.sh
#eval "$cmd"
