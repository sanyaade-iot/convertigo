ARG TYPE=amd64
FROM convertigo/docker-base:$TYPE-v1

ARG CONVERTIGO_WAR_URL

WORKDIR $CATALINA_HOME

RUN curl -fSL -o /tmp/convertigo.war $CONVERTIGO_WAR_URL \
    && mkdir -p webapps/convertigo \
    && (cd webapps/convertigo \
        && unzip -q /tmp/convertigo.war \
        && (test -d WEB-INF/xvnc && chmod a+x WEB-INF/xvnc/* || true) \
        && ((test -f /sbin/apk || test "$(dpkg --print-architecture)" != "i386") && rm -rf WEB-INF/xulrunner WEB-INF/xvnc WEB-INF/lib/swt_* || true) \
        && rm -rf /tmp/*)

WORKDIR /workspace
