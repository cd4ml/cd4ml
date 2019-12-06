FROM ubuntu:18.04

MAINTAINER iterative.ai

ARG TOKEN
ARG REPO

RUN apt-get update
RUN apt-get install -y \
    curl \
    apt-transport-https \
    ca-certificates \
    software-properties-common \
    git

RUN useradd -ms /bin/bash runner && \
    usermod -aG sudo runner && \
    echo '%sudo ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers

USER runner

WORKDIR /home/runner

# Create a folder
RUN mkdir actions-runner 
WORKDIR actions-runner
RUN curl -O https://githubassets.azureedge.net/runners/2.161.0/actions-runner-linux-x64-2.161.0.tar.gz
RUN tar xzf ./actions-runner-linux-x64-2.161.0.tar.gz

RUN ./config.sh --url ${REPO} --token ${TOKEN}

CMD ["./run.sh"]