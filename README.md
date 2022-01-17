### JAX RS REST MICROSERVICE BASED BACKEND 
## Introduction
# This is a backend applicaton for a client side bank application made for learning purposes, this backend is based on a microservice architecture following the JAX RS specification (Using Jersey implementation) and there is still plenty of room for improvement.
# The project was done mainly using Java 8 (Although not tested on an actual Java 8 JVM)

## Manual
# I included in the repo the pom.xml for maven as the project was made with maven, You will also need RabbitMQ Server and MySQL installed on your machine.
# Setup your IDE of choice and just run the services on the ports you like (The project is not dockerized)

## Improvement
# As I said the project is barebone and there is still room for a lot of improvement, the most notable ones are:
# - The Dockerization of the services
# - Implementation of a load balancer in the Api Gateway
# - Improving the JWT Refresh token mechanism
# - Implementing a service discovery mechanism 
# - Improving the JWT RSA Key pair storing mechanism
# - Properly using relative paths throughout the project for easing the configurability
# - ...
