FROM gocd/gocd-agent-ubuntu-18.04:v19.10.0

MAINTAINER iterative.ai

ARG GOOGLE_APPLICATION_CREDENTIALS

USER root
RUN apt update && apt -y install python-pip
RUN pip install dvc dvc[all]

USER go
RUN export GOOGLE_APPLICATION_CREDENTIALS=${GOOGLE_APPLICATION_CREDENTIALS}
