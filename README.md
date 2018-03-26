# Density-Maps

This repository contains the density maps microservice in the FIWOO platform

## Getting Started

### Prerequisites
- [Git](https://git-scm.com/)
- [Apache Maven 3.3.9]
- [Java 1.8]

### Developing
Install Apache Maven and Java 1.8 in your host.

## Build & Development
Run `mvn clean package` to generate the .jar file.

## Deployment with Docker

In order to deploy this microservice using Docker, follow this steps:


1. Download the docker image from Docker Hub

		`docker pull fiwoo/density-maps`

2. Run the image. Take into account that the microservice is started on port 4444. You can also configure Eureka URL and PORT using the environment variables as below: _EUREKAVHOST_ and _EUREKAPORT_

		`sudo docker run -d --name density_maps -p 4444:4444 -e "EUREKA_VHOST=<eureka host url>" -e "EUREKA_PORT=<eureka port>" fiwoo/density-maps`
