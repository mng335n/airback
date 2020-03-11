#!/bin/sh

# -----------------------------------------------------------------------------
# Control Script for the airback Server
#
# Environment Variable Prerequisites
#   airback_OPTS   (Optional) Java runtime options used when the "start",
#                   "stop" command is executed.
#                   Include here and not in JAVA_OPTS all options, that should
#                   only be used by airback itself, not by the stop process,
#                   the version command etc.
#                   Examples are heap size, GC logging, JMX ports etc.
#
#   JAVA_HOME       Must point at your Java Development Kit installation.
#                   Required to run the with the "debug" argument.
#
#   airback_PID    (Optional) Path of the file which should contains the pid
#                   of the catalina startup java process, when start (fork) is
#                   used
# -----------------------------------------------------------------------------
# OS specific support.  $var _must_ be set to either true or false.

export airback_PORT=8089
export airback_OPTS="-noverify -server -Xms394m -Xmx768m -XX:NewSize=128m -XX:+DisableExplicitGC -XX:+CMSClassUnloadingEnabled -XX:+UseConcMarkSweepGC"

cygwin=false
darwin=false
os400=false
case "`uname`" in
CYGWIN*) cygwin=true;;
Darwin*) darwin=true;;
OS400*) os400=true;;
esac

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# Only set airback_HOME if not already set
[ -z "$airback_HOME" ] && airback_HOME=`cd "$PRGDIR/.." >/dev/null; pwd`

if [ -z "$airback_OUT" ] ; then
  airback_OUT="$airback_HOME"/logs/airback.out
fi

echo $airback_HOME
echo Log $airback_OUT

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$airback_HOME" ] && airback_HOME=`cygpath --unix "$airback_HOME"`
fi

# For OS400
if $os400; then
  # Set job priority to standard for interactive (interactive - 6) by using
  # the interactive priority - 6, the helper threads that respond to requests
  # will be running at the same priority as interactive jobs.
  COMMAND='chgjob job('$JOBNAME') runpty(6)'
  system $COMMAND

  # Enable multi threading
  export QIBM_MULTI_THREADED=Y
fi

# Bugzilla 37848: When no TTY is available, don't output to console
have_tty=0
if [ "`tty`" != "not a tty" ]; then
    have_tty=1
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  airback_HOME=`cygpath --absolute --windows "$airback_HOME"`
fi

# Set standard commands for invoking Java.
if [ -z "$JAVA_HOME" ] ; then
  _RUNJAVA=java
else
  _RUNJAVA="$JAVA_HOME"/bin/java
fi

# ----- Execute The Requested Command -----------------------------------------

# Bugzilla 37848: only output this if we have a TTY
if [ $have_tty -eq 1 ]; then
  echo "Using airback_HOME:   $airback_HOME"
  if [ "$1" = "debug" ] ; then
    echo "Using JAVA_HOME:       $JAVA_HOME"
  fi

  if [ ! -z "$airback_PID" ]; then
    echo "Using airback_PID:    $airback_PID"
  fi
fi

echo Param "$1"

if [ "$1" = "--start" ] ; then

  if [ ! -z "$airback_PID" ]; then
    if [ -f "$airback_PID" ]; then
      if [ -s "$airback_PID" ]; then
        echo "Existing PID file found during start."
        if [ -r "$airback_PID" ]; then
          PID=`cat "$airback_PID"`
          ps -p $PID >/dev/null 2>&1
          if [ $? -eq 0 ] ; then
            echo "airback appears to still be running with PID $PID. Start aborted."
            exit 1
          else
            echo "Removing/clearing stale PID file."
            rm -f "$airback_PID" >/dev/null 2>&1
            if [ $? != 0 ]; then
              if [ -w "$airback_PID" ]; then
                cat /dev/null > "$airback_PID"
              else
                echo "Unable to remove or clear stale PID file. Start aborted."
                exit 1
              fi
            fi
          fi
        else
          echo "Unable to read PID file. Start aborted."
          exit 1
        fi
      else
        rm -f "$airback_PID" >/dev/null 2>&1
        if [ $? != 0 ]; then
          if [ ! -w "$airback_PID" ]; then
            echo "Unable to remove or write to empty PID file. Start aborted."
            exit 1
          fi
        fi
      fi
    fi
  fi

  shift
  touch "$airback_OUT"
  cd ..
  eval \"$_RUNJAVA\" -jar $airback_HOME/executor.jar $airback_OPTS -Dserver.port=$airback_PORT &

  if [ ! -z "$airback_PID" ]; then
    echo $! > "$airback_PID"
  fi

elif [ "$1" = "--stop" ] ; then

  shift

  SLEEP=5
  if [ ! -z "$1" ]; then
    echo $1 | grep "[^0-9]" >/dev/null 2>&1
    if [ $? -gt 0 ]; then
      SLEEP=$1
      shift
    fi
  fi

  FORCE=0
  if [ "$1" = "-force" ]; then
    shift
    FORCE=1
  fi

  if [ ! -z "$airback_PID" ]; then
    if [ -f "$airback_PID" ]; then
      if [ -s "$airback_PID" ]; then
        kill -0 `cat "$airback_PID"` >/dev/null 2>&1
        if [ $? -gt 0 ]; then
          echo "PID file found but no matching process was found. Stop aborted."
          exit 1
        fi
      else
        echo "PID file is empty and has been ignored."
      fi
    else
      echo "\$airback_PID was set but the specified file does not exist. Is airback running? Stop aborted."
      exit 1
    fi
  fi

  cd ..
  eval \"$_RUNJAVA\" -jar $airback_HOME/executor.jar --stop $airback_OPTS

  if [ ! -z "$airback_PID" ]; then
    if [ -f "$airback_PID" ]; then
      while [ $SLEEP -ge 0 ]; do
        kill -0 `cat "$airback_PID"` >/dev/null 2>&1
        if [ $? -gt 0 ]; then
          rm -f "$airback_PID" >/dev/null 2>&1
          if [ $? != 0 ]; then
            if [ -w "$airback_PID" ]; then
              cat /dev/null > "$airback_PID"
            else
              echo "airback stopped but the PID file could not be removed or cleared."
            fi
          fi
          break
        fi
        if [ $SLEEP -gt 0 ]; then
          sleep 1
        fi
        if [ $SLEEP -eq 0 ]; then
          if [ $FORCE -eq 0 ]; then
            echo "airback did not stop in time. PID file was not removed."
          fi
        fi
        SLEEP=`expr $SLEEP - 1 `
      done
    fi
  fi

  KILL_SLEEP_INTERVAL=5
  if [ $FORCE -eq 1 ]; then
    if [ -z "$airback_PID" ]; then
      echo "Kill failed: \$airback_PID not set"
    else
      if [ -f "$airback_PID" ]; then
        PID=`cat "$airback_PID"`
        echo "Killing airback with the PID: $PID"
        kill -9 $PID
        while [ $KILL_SLEEP_INTERVAL -ge 0 ]; do
            kill -0 `cat "$airback_PID"` >/dev/null 2>&1
            if [ $? -gt 0 ]; then
                rm -f "$airback_PID" >/dev/null 2>&1
                if [ $? != 0 ]; then
                    echo "airback was killed but the PID file could not be removed."
                fi
                break
            fi
            if [ $KILL_SLEEP_INTERVAL -gt 0 ]; then
                sleep 1
            fi
            KILL_SLEEP_INTERVAL=`expr $KILL_SLEEP_INTERVAL - 1 `
        done
        if [ $KILL_SLEEP_INTERVAL -gt 0 ]; then
            echo "airback has not been killed completely yet. The process might be waiting on some system call or might be UNINTERRUPTIBLE."
        fi
      fi
    fi
  fi

else

  echo "Usage: airback.sh ( commands ... )"
  echo "commands:"

  echo "  start             Start airback in a separate window"
  echo "  stop              Stop airback, waiting up to 5 seconds for the process to end"
  echo "  stop -force       Stop airback, wait up to 5 seconds and then use kill -KILL if still running"
  echo "Note: Waiting for the process to end and use of the -force option require that \$airback_PID is defined"
  exit 1

fi
