# Reactive-Java-Microservices-with-Lagom
Reactive Microservices using Lagom Framework

Project contains two services, blog and censured.
The blog service receives data via JSON objects, stored in a data base, the data and the persistent events that processed them.
The censured service receives the data for the topic subscribed by the blog service, and analyzes the posts, checking if they contain prohibited words, and stored them, already censored.

In this project, Event Sourcing and CQRS (Command Query Responsibility Segregation) are implemented.
It uses Apache Kafka to stream events between the two services, each containing a Cassandra database to store its immutable data.

Apache kafka is using in docker

Instructions

install Docker
Install Scala
Insomnia for insert object json in system
TablePlus for view information in Cassandra database (running port 9402)

In powershell using commands:
1-> docker-compose up -d
2-> sbt runAll