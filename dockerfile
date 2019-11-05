FROM clojure:lein
ENV LANG C.UTF-8

# create server's dir
ENV APP_HOME /apps
RUN mkdir $APP_HOME
WORKDIR $APP_HOME

ADD . $APP_HOME
WORKDIR $APP_HOME
