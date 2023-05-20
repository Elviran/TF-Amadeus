FROM ubuntu:19.04

#Update
RUN apt-get update

#Installations
RUN apt install -y netcat ssh iputils-ping  && \
    apt install -y python3 python3-pip && \
    apt install -y pssh && \
    python3 -m pip install -U pip && \
    apt install -y smbclient && \
    apt install -y cifs-utils

##----PIP Installations----#
RUN pip3 install tensorflow && \
    pip3 install tensorflow_datasets

#----Create Files & Directories----#
RUN mkdir -p /srv/conf/ps-tensorflow/tf_configs && \
    mkdir /media/samba  && \
    mkdir /etc/tf_setup 

#----Add Files----#
ADD ./shared/scripts/ /etc/tf_setup

#----Permissions----#
RUN chmod u+x /etc/tf_setup/mount-samba.sh && \
    chmod u+x /etc/tf_setup/run-services.sh && \
    chmod -R u+rw /media/samba
