# Kubernetes Monitoring Telegram Bot
Bot, that send notifications in the telegram, received from [Kubewatch](https://github.com/bitnami-labs/kubewatch) and [Prometheus AlertManager](https://prometheus.io/docs/alerting/latest/alertmanager/).

[![Java CI](https://github.com/viento-group/kubernetes-monitoring-telegram-bot/actions/workflows/java-ci.yml/badge.svg)](https://github.com/viento-group/kubernetes-monitoring-telegram-bot/actions/workflows/java-ci.yml)

Tested with Kubewatch version [`0.1.0-debian-10-r334`](https://hub.docker.com/layers/bitnami/kubewatch/0.1.0-debian-10-r339/images/sha256-27b5142b9189871eeb6e87cfbb9ca4da9c669224667fde0eca03ed707c40586f?context=explore)
and `kube-prometheus-stack` helm chart version [`17.0.3`](https://artifacthub.io/packages/helm/prometheus-community/kube-prometheus-stack/17.0.3).

## Navigation
- [Configuration](#configuration)
- [API](#api)
- [Messages Examples](#messages-examples)
- [Build application](#build-application)
- [Running application](#running-application)
- [Build Docker image](#build-docker-image)

## Configuration
This bot can be configured in three ways:
1. [Using environment variables](#using-environment-variables)
2. [Using launch command parameters](#using-launch-command-parameters)
3. [Using `application.yml` file](#using-applicationyml-file)

### Using environment variables
You may set environment variables to customize the application.
Here is available environment variables:

Name | Default Value | Description
---- | ------------- | -----------
TELEGRAM_BOT_TOKEN | | Default telegram bot token.
TELEGRAM_KUBEWATCH_BOT_TOKEN | | Telegram bot token for Kubewatch notifications. If not specified, `TELEGRAM_BOT_TOKEN` will be used.
TELEGRAM_PROMETHEUS_BOT_TOKEN | | Telegram bot token for Prometheus alerts notifications. If not specified, `TELEGRAM_BOT_TOKEN` will be used.
LOGGING_LEVEL | info | Logging level. Available: `trace`, `debug`, `info`, `warn`, `error`, `fatal`, `off`. `info` is recommended for production use.
PORT | 8080 | Port, on which application should work.

### Using launch command parameters
You may set launch command parameters to customize the application.
Here is available launch command parameters:

Name | Default Value | Description
---- | ------------- | -----------
telegram.bot-token | | Default telegram bot token.
telegram.kubewatch-bot-token | | Telegram bot token for Kubewatch notifications. If not specified, `TELEGRAM_BOT_TOKEN` will be used.
telegram.prometheus-bot-token | | Telegram bot token for Prometheus alerts notifications. If not specified, `TELEGRAM_BOT_TOKEN` will be used.
application.logging-level | info | Logging level. Available: `trace`, `debug`, `info`, `warn`, `error`, `fatal`, `off`. `info` is recommended for production use.
logging.file.path | | Path to logging file.
logging.file.name | | Logging file name.
server.port | 8080 | Port, on which application should work.

See how to pass launch command parameters in [Running application](#running-application) section.

### Using `application.yml` file
You may override `/src/main/resources/application.yml` file and set there custom properties.
Then you should build project with new `application.yml` file and use it.
Properties are the same, as in [launch command parameters](#using-launch-command-parameters).

## API
This application use HTTP protocol to receive notifications from Kubewatch and Prometheus AlertManager.

### Kubewatch API
To receive notifications from Kubewatch, you need to provide following webhook url:
`http://localhost:8080/kubewatch/123,456` where `123,456` is a comma-separated telegram chat ids, in which telegram bot should send notifications.
You may receive you telegram chat id using [Telegram IDBot](https://telegram.me/myidbot).

**Attention**: you chat id may start with `-` or have any other symbols. You shouldn't delete them, you must provide them in URL as well.

Also, you may provide `format` request parameter, to specify message format.
For example: `http://localhost:8080/kubewatch/123,456?format=bold`.

Available formats: `default`, `bold`, `detailed`.

Kubewatch example configuration:
```yaml
handler:
  webhook:
    enabled: true
    url: "http://localhost:8080/kubewatch/123,456?format=bold"
```

### Prometheus AlertManager API
To receive Prometheus alerts form AlertManager, you need to provide following webhook url:
`http://localhost:8080/prometheus/123,456` where `123,456` is a comma-separated telegram chat ids, in which telegram bot should send notifications.
You may receive you telegram chat id using [Telegram IDBot](https://telegram.me/myidbot).

**Attention**: you chat id may start with `-` or have any other symbols. You shouldn't delete them, you must provide them in URL as well.

Also, you may provide `format` request parameter, to specify message format.
For example: `http://localhost:8080/prometheus/123,456?format=simple`.

Available formats: `default`, `simple`, `simple_summary`, `detailed`.

Also, you may use `filters` request parameters, to specify filers.
For example: `http://localhost:8080/prometheus/123,456?filters=firingOnly`.

Available filters:

| Filter code | Description |
| ----------- | ----------- |
`firingOnly` | Show only alerts with status `firing`
`resolvedOnly` | Show only alerts with status `resolved`
`withoutAlerts` | Do not show any alerts


Prometheus AlertManager example configuration:
```yaml
receivers:
  - name: telegram-bot
    webhook_configs:
      - send_resolved: true
        url: http://localhost:8080/prometheus/123,456?format=simple_summary&filters=firingOnly
```

## Messages Examples
- [Kubewatch Message](#kubewatch-message)
- [Prometheus Alert Message](#prometheus-alert-message)

### Kubewatch Message
#### `default`
> A `pod` in namespace `simple-ns` has been `updated`:\
`kube-system/vpnkit-controller`

#### `bold`
> A **pod** in namespace **simple-ns** has been **updated**:\
**kube-system/vpnkit-controller**

#### `detailed`
> **New Kubewatch status:**\
kind: **pod**\
name: **kube-system/vpnkit-controller**\
namespace: **simple-ns**\
reason: **updated**

### Prometheus Alert Message
#### `default`
> **Status: Firing 🔥**\
\
**Active Alert List:**\
**Target disappeared from Prometheus target discovery.**\
KubeControllerManager has disappeared from Prometheus target discovery.\
[Runbook URL](https://github.com/kubernetes-monitoring/kubernetes-mixin/tree/master/runbook.md#alert-name-kubecontrollermanagerdown) \
\
Alert Name: KubeControllerManagerDown\
Severity: critical ❗️\
-------------\
**An alert that should always be firing to certify that Alertmanager is working properly.**\
This is an alert meant to ensure that the entire alerting pipeline is functional.
This alert is always firing, therefore it should always be firing in Alertmanager
and always fire against a receiver. There are integrations with various notification
mechanisms that send a notification when this alert is not firing. For example the
"DeadMansSnitch" integration in PagerDuty.\
[Runbook URL](https://github.com/kubernetes-monitoring/kubernetes-mixin/blob/master/runbook.md#alert-name-watchdog) \
\
Alert Name: Watchdog\
Severity: none

#### `simple`
> **Status: Firing 🔥**\
\
✅ KubeControllerManagerDown\
🔥 KubeSchedulerDown\
🔥 Watchdog
 
#### `simple_summary`
> **Status: Firing 🔥**\
\
✅ Target disappeared from Prometheus target discovery.\
🔥 Target disappeared from Prometheus target discovery.\
🔥 An alert that should always be firing to certify that Alertmanager is working properly.

#### `detailed`
> **Status: Firing 🔥**\
\
**Active Alert List:**\
**Status: Firing 🔥**\
**Annotations:**\
**description:** KubeControllerManager has disappeared from Prometheus target discovery.\
**runbook_url:** https://github.com/kubernetes-monitoring/kubernetes-mixin/tree/master/runbook.md#alert-name-kubecontrollermanagerdown \
**summary:** Target disappeared from Prometheus target discovery.\
\
**Labels:**\
**alertname:** KubeControllerManagerDown\
**prometheus:** kube-prometheus-stack/kube-prometheus-stack-prometheus\
**severity:** critical

## Build application
To build application you should have **Java 11** installed on you machine.

Run `./gradlew build` command in repository root folder.
Your built jar file will be in `/build/libs/kubernetes-monitoring-telegram-bot-*application-version*.jar`.

You may also use `./gradlew build -x test` command to build without tests.

## Running application
To run application after you have built jar file you may use command:
```bash
$ java -jar ./build/libs/kubernetes-monitoring-telegram-bot-*application-version*.jar
```

You may also pass launch command parameters using `-Dkey=value` parameter, for example:
```bash
$ java -jar -Dserver.port=3000 ./build/libs/kubernetes-monitoring-telegram-bot-0.2.jar 
```

Or using `--key=value` parameter, for example:
```bash
$ java -jar ./build/libs/kubernetes-monitoring-telegram-bot-0.2.jar --server.port=3000 
```

## Build Docker image
To build docker image run following command:
```bash
$ ./gradlew bootBuildImage --imageName=viento-group/kubernetes-monitoring-telegram-bot 
```
You may use custom `imageName` parameter value.

Then you may use following command to push docker image to docker hub:
```bash
$ docker push docker.io/viento-group/kubernetes-monitoring-telegram-bot:latest 
```