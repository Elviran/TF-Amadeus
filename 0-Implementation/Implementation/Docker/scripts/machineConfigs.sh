#!/usr/bin/env bash

sudo apt update
sudo apt -y upgrade

echo "-----------Install Docker-----------------"

while true; do
    read -p "Do you wish to install this program?" yn
    case $yn in
        [Yy]* ) 
            sudo apt-get install -y \
                apt-transport-https \
                ca-certificates \
                curl \
                gnupg-agent \
                software-properties-common

            curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

            sudo add-apt-repository \
            "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
            $(lsb_release -cs) \
            stable"

            sudo apt-get update
            sudo apt-get install -y docker-ce docker-ce-cli containerd.io

            sudo usermod -aG docker $USER
            break;;
        [Nn]* ) break;;
        * ) echo "Please answer yes or no.";;
    esac
done


echo "-----------Installing Docker Compose-----------------"
while true; do
    read -p "Do you wish to install this program? " yn
    case $yn in
        [Yy]* ) 
            sudo curl -L "https://github.com/docker/compose/releases/download/1.25.5/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
            sudo chmod +x /usr/local/bin/docker-compose
            sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
            break;;
        [Nn]* ) 
        break;;
        * ) echo "Please answer yes or no.";;
    esac
done

echo "-----------Install Java-----------------"

while true; do
    read -p "Do you wish to install this program? " yn
    case $yn in
        [Yy]* ) 
            sudo apt-get install openjdk-11-jre openjdk-11-jdk
            cat export JAVA_HOME="/usr/lib/jvm/java-11-openjdk-amd64/" >> ~/.bashrc
            sudo update-alternatives --config java
            break;;
        [Nn]* ) break;;
        * ) echo "Please answer yes (Y) or no (N).";;
    esac
done

echo "-----------Install Maven-----------------"

while true; do
    read -p "Do you wish to install this program? " yn
    case $yn in
        [Yy]* ) 
            sudo apt -y install maven 
            break;;
        [Nn]* ) break;;
        * ) echo "Please answer yes (Y) or no (N).";;
    esac
done


echo "-----------Creating ssh key-----------------"
while true; do
    read -p "Generate SSH Key? " yn
    case $yn in
        [Yy]* ) 
            ssh-keygen -t rsa -N "" -f ~/.ssh/id_rsa
            cat ~/.ssh/id_rsa.pub
            break;;
        [Nn]* ) break;;
        * ) echo "Please answer yes or no.";;
    esac
done


echo "-----------Pulling Git Repository-----------------"

while true; do
    read -p "Setup git and pull project repository? " yn
    case $yn in
        [Yy]* ) 
            git config --global user.name "Irisann Agius"
            git config --global user.email "agiusiris@gmail.com"
            git clone https://bitbucket.org/irisann/s6.3b_agius_irisann.git

            cd ./s6.3b_agius_irisann

            git checkout feature/keras_model_to_estimator
            break;;
        [Nn]* ) exit;;
        * ) echo "Please answer yes or no.";;
    esac
done


echo "-----------Install Portainer-----------------"

while true; do
    read -p "Setup Portainer? " yn
    case $yn in
        [Yy]* ) 
            sudo docker volume create portainer_data
            sudo docker run -d -p 8000:8000 -p 9000:9000 --name=portainer --restart=always -v /var/run/docker.sock:/var/run/docker.sock -v portainer_data:/data portainer/portainer
            break;;
        [Nn]* ) exit;;
        * ) echo "Please answer yes or no.";;
    esac
done

source ~/.bashrc
newgrp docker



