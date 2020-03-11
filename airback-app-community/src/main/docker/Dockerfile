FROM openjdk:8-jre-alpine
MAINTAINER Hai Nguyen <hainguyen@airbase.com>

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/airbase/executor.jar", "-noverify"]

ADD staging/bin              /usr/share/airbase/bin
ADD staging/config           /usr/share/airbase/config
ADD staging/i18n             /usr/share/airbase/i18n
# Add Maven dependencies (not shaded into the artifact; Docker-cached)
ADD staging/lib              /usr/share/airbase/lib
# Add the service itself
ADD staging/executor.jar     /usr/share/airbase/executor.jar

EXPOSE 8089