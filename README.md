# springtainer-elasticsearch

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.avides.springboot.springtainer/springtainer-elasticsearch/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.avides.springboot.springtainer/springtainer-elasticsearch)
[![Build](https://github.com/springtainer/springtainer-elasticsearch/workflows/release/badge.svg)](https://github.com/springtainer/springtainer-elasticsearch/actions)
[![Nightly build](https://github.com/springtainer/springtainer-elasticsearch/workflows/nightly/badge.svg)](https://github.com/springtainer/springtainer-elasticsearch/actions)
[![Coverage report](https://sonarcloud.io/api/project_badges/measure?project=springtainer_springtainer-elasticsearch&metric=coverage)](https://sonarcloud.io/dashboard?id=springtainer_springtainer-elasticsearch)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=springtainer_springtainer-elasticsearch&metric=alert_status)](https://sonarcloud.io/dashboard?id=springtainer_springtainer-elasticsearch)
[![Technical dept](https://sonarcloud.io/api/project_badges/measure?project=springtainer_springtainer-elasticsearch&metric=sqale_index)](https://sonarcloud.io/dashboard?id=springtainer_springtainer-elasticsearch)

### Dependency

```xml

<dependency>
  <groupId>com.avides.springboot.springtainer</groupId>
  <artifactId>springtainer-elasticsearch</artifactId>
  <version>2.0.0</version>
  <scope>test</scope>
</dependency>
```

### Configuration

Properties consumed (in `bootstrap.properties`):

- `embedded.container.elasticsearch.enabled` (default is `true`)
- `embedded.container.elasticsearch.startup-timeout` (default is `30`)
- `embedded.container.elasticsearch.docker-image` (default is `docker.elastic.co/elasticsearch/elasticsearch:8.17.3`)
- `embedded.container.elasticsearch.http-port` (default is `9200`)
- `embedded.container.elasticsearch.transport-host` (default is `9300`)

Properties provided (in `application-it.properties`):

- `embedded.container.elasticsearch.host`
- `embedded.container.elasticsearch.http-port`
- `embedded.container.elasticsearch.transport-port`

Example for minimal configuration in `application-it.properties`:

```
spring.data.elasticsearch.cluster-nodes=${embedded.container.elasticsearch.host}:${embedded.container.elasticsearch.transport-port}
spring.data.elasticsearch.properties.client.transport.ignore_cluster_name=true
```

## Logging

To reduce logging insert this into the logback-configuration:

```xml
<!-- Springtainer -->
<logger name="com.github.dockerjava.jaxrs" level="WARN" />
<logger name="com.github.dockerjava.core.command" level="WARN" />
<logger name="org.apache.http" level="WARN" />
```

## Labels

The container exports multiple labels to analyze running springtainers:

- `SPRINGTAINER_SERVICE=elasticsearch`
- `SPRINGTAINER_IMAGE=${embedded.container.elasticsearch.docker-image}`
- `SPRINGTAINER_STARTED=$currentTimestamp`
