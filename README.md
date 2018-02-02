ChromeCast Scala Example
========================

AWS Pollyで作った音声ファイルを Google Home で再生する

* [chromecast-java-api-v2](https://github.com/vitalidze/chromecast-java-api-v2)

# Build

```sh
$ export AWS_ACCESS_KEY_ID=
$ export AWS_SECRET_ACCESS_KEY=
$ export BUCKET_NAME=
$ export ADDRESS=
$ docker run -e AWS_ACCESS_KEY_ID -e AWS_SECRET_ACCESS_KEY -e BUCKET_NAME -e ADDRESS -it scala-cast:0.0.1-SNAPSHOT --rm
```
