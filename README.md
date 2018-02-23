# Density-Maps

This repository contains the density maps microservice in the FIWOO platform

## Getting Started

### Prerequisites
- [Git](https://git-scm.com/)
- [Python](python.org) Python >= 2.7

### Developing
- Run `pip install flask requests` to install Flask dependences.

## Build & Development
Run `python Controller_density_Map.py` to start the microservice.

## Testing
Run `python Test_density_maps.py -v` to run the unit tests.

## Deployment with Docker

In order to deploy this microservice using Docker, follow this steps:


1. Download the docker image from Docker Hub

		`docker pull fiwoo/density-maps`

2. Run the image. Take into account that the microservice is started on port 5000.

		`sudo docker run -d --name density_maps -p 5000:5000 fiwoo/density-maps`
