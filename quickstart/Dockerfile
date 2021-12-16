FROM openjdk:8 as thirdeye_build_env

ARG PINOT_BRANCH=master
ARG PINOT_GIT_URL="https://github.com/cyrilou242/incubator-pinot.git"
RUN echo "Trying to build Thirdeye from [ ${PINOT_GIT_URL} ] on branch [ ${PINOT_BRANCH} ]"

ENV TE_HOME=/opt/thirdeye
ENV TE_BUILD_DIR=/opt/thirdeye-build

# extra dependency for running launcher
RUN apt-get update \
    && apt-get install -y --no-install-recommends vim wget curl git automake bison flex g++ libboost-all-dev libevent-dev libssl-dev libtool make pkg-config \
    && echo '{ "allow_root": true }' > /root/.bowerrc \
    && rm -rf /var/lib/apt/lists/*


# fixme rewrite this
# node and npm
RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.35.3/install.sh | bash \
    && export NVM_DIR="$HOME/.nvm" \
    && [ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh" \
    && nvm install v10.24.1 \
    && npm install -g phantomjs --unsafe-perm --ignore-scripts

# install maven
RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && wget https://www.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz -P /tmp \
  && tar -xzf /tmp/apache-maven-*.tar.gz -C /usr/share/maven --strip-components=1 \
  && rm -f /tmp/apache-maven-*.tar.gz \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG /opt/.m2

RUN git clone -b ${PINOT_BRANCH} ${PINOT_GIT_URL} ${TE_BUILD_DIR} \
    && cd ${TE_BUILD_DIR}  \
    && ./install.sh \
    && mkdir -p ${TE_HOME}/config/default \
    && mkdir -p ${TE_HOME}/bin \
    && cp -rp ${TE_BUILD_DIR}/thirdeye-pinot/config/* ${TE_HOME}/config/default/. \
    && rm ${TE_BUILD_DIR}/thirdeye-pinot/target/thirdeye-pinot-*-sources.jar \
    && cp ${TE_BUILD_DIR}/thirdeye-pinot/target/thirdeye-pinot-*.jar ${TE_HOME}/bin/thirdeye-pinot.jar \
    && rm -rf ${TE_BUILD_DIR}

COPY log4j2.xml ${TE_HOME}/

# copy the geenrator and the templates if you want the container to be responsible for generating the config
#COPY gen_config.sh ${TE_HOME}/
#COPY config-templates ${TE_HOME}/config-templates

FROM openjdk:8-jdk-slim

LABEL MAINTAINER=cyril@startree.ai

ENV TE_HOME=/opt/thirdeye

COPY --from=thirdeye_build_env ${TE_HOME} ${TE_HOME}

VOLUME ["${TE_HOME}/config"]
EXPOSE 1426 1427
WORKDIR ${TE_HOME}