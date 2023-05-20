## Master Cluster Build

# What is it used for
- Builds the tf-cluster-service for a master machine in a cluster, It is used in conjuction with the container-service
to be able to launch a dockerized service to run distributed tensorflow

## How is it used
- Edit the environment file for the cluster name and the ip address of the machines within that cluster.
  no need to edit anything in the docker-compose