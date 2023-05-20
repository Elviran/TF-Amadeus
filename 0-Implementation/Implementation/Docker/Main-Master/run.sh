#!/usr/bin/env bash

#Clean up
docker-compose down
docker volume create shared_drive

#Build Maven
mvn clean install -f ../../thesis-backend/tf-backend-common && \
mvn clean install -f ../../thesis-backend/tf-backend-service && \
mvn clean install -f ../../thesis-backend/tf-container-service

#Docker compose up
docker-compose up --build

