FROM ubuntu:18.04

RUN apt update && \
    apt install -y docker.io && \
    apt install -y smbclient && \
    apt install -y cifs-utils && \
    apt install -y curl && \
    apt install -y default-jdk

RUN curl -L "https://github.com/docker/compose/releases/download/1.25.4/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

#---Create Directories---#
RUN mkdir /media/samba  && \
    mkdir /etc/tf_setup 

#----Add Files----#
ADD ./shared/scripts/ /etc/tf_setup

#----Permissions----#
RUN chmod u+x /etc/tf_setup/mount-samba.sh && \
    chmod -R u+rw /media/samba && \
    chmod +x /usr/local/bin/docker-compose

#Symbolic link as docker-compose is installed in usr/local instead of /usr/bin
RUN ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose

