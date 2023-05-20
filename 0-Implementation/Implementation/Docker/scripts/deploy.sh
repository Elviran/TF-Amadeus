#!/usr/bin/env bash

cmd=$1
git=$2

case $1 in 
    main-master)
        echo "Building Main-Master \n"
        #if main-master
        cd ../../
        if [ ! -z $git ]; then
            git checkout .
            git pull
        fi 
        cd thesis-backend/tf-backend-common/
        mvn clean install
        cd ../tf-backend-service/
        mvn clean install
        cd ../tf-cluster-service/
        mvn clean install
        cd ../../Docker/Main-Master
        docker-compose down
        docker-compose up --build
        ;;
    master)
        #if master
        echo "Building Master"
        cd ../../
        if [ ! -z $git ]; then
            git checkout .
            git pull
        fi 
        cd thesis-backend/tf-backend-common/
        mvn clean install
        cd ../tf-cluster-service/
        mvn clean install
        cd ../../Docker/Master
        docker-compose down
        docker-compose up --build
        ;;
    container)
        #if container
        echo "Building Container"
        cd ../../
        if [ ! -z $git ]; then
            git checkout .
            git pull
        fi 
        cd thesis-backend/tf-container-service/
        mvn clean install
        cd ../../Docker/container-service
        docker-compose down
        docker-compose up --build
        ;;
    *)
        echo "Usage: main-master | master | container"
        exit 1
        ;;
    esac




