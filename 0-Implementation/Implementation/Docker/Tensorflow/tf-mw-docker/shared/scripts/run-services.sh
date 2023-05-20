#!/usr/bin/env bash
server=$1
username=$2
password=$3
path=$4
tf_conf=$5
model_dir=$6
model_fn_name=$7

#Mount samba server
/etc/tf_setup/mount-samba.sh $server $username $password $path

if [[ -z $tf_conf ]] || [[ -z $model_dir ]] || [[ -z $model_fn_name ]]
then 
    echo "Please input all options for : tf_conf, model_dir and model_fn name"
fi

#Start TF Process
python3 runner.py -f ${tf_conf} -p /media/samba/${model_dir} -m ${model_fn_name}
