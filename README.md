ChromeCast Scala Example
========================

# Create S3 Bucket

```
$ sbt
sbt> awscfCreateBucket <Cloudformation Stack Name> <S3 Bucket Name>
```

# AWS Pollyで作った音声ファイルを Google Home で再生する

* [chromecast-java-api-v2](https://github.com/vitalidze/chromecast-java-api-v2)

## Docker

* [DOCKER COMES TO RASPBERRY PI](https://www.raspberrypi.org/blog/docker-comes-to-raspberry-pi/)

```
raspberrypi $ sudo -s
raspberrypi # apt remove docker docker-engine docker.io -y
raspberrypi # curl -sSL https://get.docker.com | sh
```

## Build

```
mac $ sbt clean docker:stage
mac $ docker build -t scala-cast:0.0.1-SNAPSHOT ./
mac $ cd target
mac $ tar cf scala-cast.tar docker
mac $ scp scala-cast.tar USER@RASPBERRYPI:./
mac $ ssh USER@RASPBERRYPI

raspberrypi $ mkdir scala-cast
raspberrypi $ cd scala-cast
raspberrypi $ tar xf ../scala-cast.tar
raspberrypi $ cd docker/stage
raspberrypi $ docker build -t scala-cast:0.0.1-SNAPSHOT ./
```

## Run

```
raspberrypi $ avahi-browse -r _googlecast._tcp -t
raspberrypi $ export ADDRESS=
raspberrypi $ export AWS_ACCESS_KEY_ID=
raspberrypi $ export AWS_SECRET_ACCESS_KEY=
raspberrypi $ export BUCKET_NAME=
raspberrypi $ docker run -e AWS_ACCESS_KEY_ID -e AWS_SECRET_ACCESS_KEY -e BUCKET_NAME -e ADDRESS -it scala-cast:0.0.1-SNAPSHOT --rm
```
