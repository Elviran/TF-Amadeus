# Tensorflow docker container builds

# What is it used for
- This directory holds two tensorflow docker directory, One for setting up a multiworker server (still not implemented) and one for launching a PS Tensorflow service.

- The difference between them will be the host names binded to them, different runner and differnt json configurations. 

# What component relies on what:
- These docker containers will ONLY be built from the container-service, as there are no python runner scripts, json files for tf_configs
user scripts and samba server connection. It will not execute on its own. 

- In the future, there will be a directory specificaly to be able to build locally but for now these cannot be executed without using the container-service.