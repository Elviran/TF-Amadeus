FROM ubuntu:18.04

#Update
RUN apt-get update

#Installations
RUN apt install -y python3 python3-pip && \
    python3 -m pip install -U pip && \
    apt install -y smbclient && \
    apt install -y cifs-utils

##----PIP Installations----#
RUN pip3 install tensorflow && \
    pip3 install tensorflow_datasets

#----Create Files & Directories----#
RUN mkdir -p /srv/conf/ps-tensorflow/tf_configs && \
    mkdir /media/samba  && \
    mkdir /workspace/ && \
    mkdir /workspace/logs/

#----Add Files----#
ADD ./shared/scripts/ /workspace
ADD ./runner.py /workspace
ADD ./user.py /workspace

#----Permissions----#
RUN chmod u+x /workspace/mount-samba.sh && \
    chmod u+x /workspace/run-services.sh && \
    chmod -R u+rw /media/samba
