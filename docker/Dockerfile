# Use debian:latest as the base image
FROM debian

# Set environment variables
ENV LD_LIBRARY_PATH=/usr/local/lib

# Install necessary packages
RUN apt-get update && \
    apt-get install -y netselect-apt

RUN netselect-apt && mv sources.list /etc/apt/sources.list

RUN sed -i '/security.debian.org/d' /etc/apt/sources.list

RUN apt-get --allow-releaseinfo-change update && \
    apt-get install -y wget build-essential openjdk-17-jre openjdk-17-jdk git python3 zlib1g sudo openssh-client openssh-server

RUN git clone https://github.com/harrykieu/mpi-mapreduce-avgpts.git /app

# Download and install OpenMPI
RUN wget https://download.open-mpi.org/release/open-mpi/v5.0/openmpi-5.0.3.tar.gz && \
    tar -xvf /openmpi-5.0.3.tar.gz && \
    cd /openmpi-5.0.3 && \
    ./configure --enable-mpi-java && \
    make && \
    make install

ADD Makefile_docker /app/Makefile

# SSH server configuration.
RUN sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config

# SSH client configuration.
RUN echo "    StrictHostKeyChecking no" >> /etc/ssh/ssh_config
RUN echo "PubkeyAuthentication yes" >> /etc/ssh/sshd_config
RUN echo "ChallengeResponseAuthentication no" >> /etc/ssh/sshd_config
RUN echo "AuthorizedKeysFile /root/.ssh/authorized_keys" >> /etc/ssh/sshd_config

RUN mkdir -p /run/sshd

# Generate SSH key pair
RUN ssh-keygen -t rsa -f /root/.ssh/id_rsa -N "" && \
chmod 700 /root/.ssh && \
chmod 600 /root/.ssh/id_rsa && \
chmod 644 /root/.ssh/id_rsa.pub && \
touch /root/.ssh/authorized_keys && \
chmod 600 /root/.ssh/authorized_keys

RUN echo "Host *\n\tStrictHostKeyChecking no\n\tUserKnownHostsFile=/dev/null" > /root/.ssh/config

EXPOSE 22