# Main-Master Cluster controller docker compose build

# What is it used for
- This compose directory allows to build all the containers that will be executed on an active main-master machine.

# Builds:
- Angular Front-end website
- Spring boot backend service, it mounts the target jar file and with docker-compose creates a container for it to be executed.
- Samba Server, it uses the sambaserver-docker directory to build and setup a samba server on a docker container.

# What the Backend component relies on what:
- The spring boot backend service relies on the Samba Server to be active and running.
- The Angular Front-end website required the spring boot backend service to be active as it is it's backend server.
- For docker services, the tf-cluster-service (Master Directory) needs to be initiated on each master cluster machine.

# How do you run this?
- Start run.sh script to create a docker volume and compose up the project.