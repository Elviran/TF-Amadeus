#!/usr/bin/env bash
server=$1
username=$2
password=$3
path=$4

if [[ -z $server ]] || [[ -z $username ]] || [[ -z $password ]] || [[-z $path]]
then 
    echo "Please input all options for : Server name, Server file directory and where you wish to mount the directory"
fi

mount -t cifs //$server  -o username=$username -o password=$password $path

mount -a

#As the docker file closes when running the script, added it here for now will change later
#tail -f /dev/null
