# Container-service docker build

## What it is used for
- This container hosts a micro-service container that connects with
  the hosts' docker.sock and be able to conduct docker commands.

- This component will be used to start the tensorflow containers by
  connecting inside the samba server and retrieve the docker directory
  within the current job work directory that is sent by tf-backend-consumer.

- This container service's goal is to be placed in each machine and wait 
  for calls from the backend-consumer to start,stop,pause,cleanup containers.

## How to run
- To run just write docker-compose up in the cmd line. 