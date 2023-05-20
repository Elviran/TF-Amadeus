FROM ubuntu:16.04

RUN apt update -y && \
    apt install -y python3 python3-pip && \
    python3 -m pip install -U pip && \
    apt install -y samba && \
    apt install -y expect

#pip installations
RUN pip3 install tensorflow 

RUN adduser --disabled-password sambaserver

RUN mkdir -p /home/sambaserver/sambashare && \
    mkdir -p /shared/sambashare && \
    chown sambaserver /home/sambaserver/sambashare  && \
    chmod 777 /shared/sambashare

#Add samba-user-setup script
RUN mkdir -p /srv/conf/sambaserver
ADD ./bash-scripts /srv/conf/sambaserver
RUN chmod u+x -R /srv/conf/sambaserver/

#override smb.conf with what we have locally
COPY ./configs/smb.conf /etc/samba/smb.conf

WORKDIR  /srv/conf/sambaserver

#command line execute run-services.sh script
CMD ["/srv/conf/sambaserver/run-services.sh"] 