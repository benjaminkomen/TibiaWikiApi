# Docker

The `Dockerfile` in this folder is used to build an image and
deploy to GCP Cloud run. You can test the docker image locally using
the following commands:

- Make sure you have Docker installed and running on your machine
- Set an environment variable using: `export GITHUB_TOKEN=1234` where you replace 1234 with a valid value
- Build the docker image by executing: `docker build -t tibiawikiapi -f ./docker/Dockerfile --build-arg GITHUB_TOKEN=$GITHUB_TOKEN .`
in your terminal from the root folder of this project
- Now run the dockerfile using: `docker run -it -e PORT=8080 -p 8080:8080 tibiawikiapi`