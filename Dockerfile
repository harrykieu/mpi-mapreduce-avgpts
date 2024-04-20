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
ADD Makefile /app/Makefile

# Create a new user named 'mpi'
RUN useradd -ms /bin/bash mpi && \
    echo "mpi ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers.d/mpi

# Generate SSH key pair
RUN ssh-keygen -t rsa -f ~/.ssh/id_rsa -q -P ""

# Add the public key to authorized_keys
RUN cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys

# Disable strict host key checking
RUN echo "Host *" >> ~/.ssh/config && \
    echo "    StrictHostKeyChecking no" >> ~/.ssh/config


# Change the ownership of the /app directory to 'mpi'
RUN chown -R mpi:mpi /app

# Change to 'mpi' user
USER mpi