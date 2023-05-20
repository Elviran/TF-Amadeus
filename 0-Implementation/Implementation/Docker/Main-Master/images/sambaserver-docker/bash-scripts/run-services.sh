#!/usr/bin/env bash

#Run to set user and service
./samba-user-setup.sh sambaserver test123

#Start Daemon
/etc/init.d/smbd start

cd /shared/sambashare
mkdir pending_tasks 
chmod -R 777 pending_tasks

#Tail /dev/null for container not to exit
#TODO:// after add tensorboard for directory
tail -f /dev/null


