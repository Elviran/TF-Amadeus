#!/usr/bin/env bash

#Clean up
docker-compose down

#Build Maven
mvn clean install -f ../../thesis-backend/tf-backend-common && \
mvn clean install -f ../../thesis-backend/tf-container-service

#Docker compose up
docker-compose up --build
