FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && apt-get install -y --no-install-recommends \
    openjdk-17-jdk-headless \
    coreutils \
    gawk \
    grep \
    sed \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY . /app/
RUN chmod +x /app/build.sh

CMD ["/bin/bash"]
