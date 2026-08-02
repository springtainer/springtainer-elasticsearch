# springtainer-elasticsearch

[![Maven Central](https://img.shields.io/maven-central/v/com.avides.springboot.springtainer/springtainer-elasticsearch.svg?label=maven-central)](https://search.maven.org/artifact/com.avides.springboot.springtainer/springtainer-elasticsearch)
[![Release](https://github.com/springtainer/springtainer-elasticsearch/actions/workflows/release.yml/badge.svg)](https://github.com/springtainer/springtainer-elasticsearch/actions/workflows/release.yml)
[![Nightly build](https://github.com/springtainer/springtainer-elasticsearch/actions/workflows/nightly.yml/badge.svg)](https://github.com/springtainer/springtainer-elasticsearch/actions/workflows/nightly.yml)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=springtainer_springtainer-elasticsearch&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=springtainer_springtainer-elasticsearch)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=springtainer_springtainer-elasticsearch&metric=coverage)](https://sonarcloud.io/summary/new_code?id=springtainer_springtainer-elasticsearch)

### Dependency

```xml
<dependency>
  <groupId>com.avides.springboot.springtainer</groupId>
  <artifactId>springtainer-elasticsearch</artifactId>
  <version>4.0.0-RC1</version>
  <scope>test</scope>
</dependency>
```

### Configuration

Properties consumed (in `bootstrap.properties`):

- `embedded.container.elasticsearch.enabled` (default is `true`)
- `embedded.container.elasticsearch.startup-timeout` (default is `30`)
- `embedded.container.elasticsearch.docker-image` (default is `docker.elastic.co/elasticsearch/elasticsearch:8.19.18`)
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

## Spring's test-context cache is bounded automatically

`spring.test.context.cache.maxSize=1` ships as a classpath `spring.properties`
resource inside springtainer-common itself, so it's picked up automatically for every consumer - no configuration
needed on your side. This bounds Spring's test-context cache so a no-longer-current context (and, via its
`ContextClosedEvent` listener, its embedded container) gets evicted and cleanly closed as soon as a differently-configured
context needs the slot, instead of piling up unclosed until the whole JVM exits.

This works the same way whether tests are launched via Maven Surefire/Failsafe or directly from an IDE's own test
runner (e.g. Eclipse), since Spring resolves it from the classpath (`org.springframework.core.SpringProperties`) rather
than from a JVM system property.

## Logging

To reduce logging insert this into the logback-configuration:

```xml
<!-- Springtainer -->
<logger name="com.github.dockerjava" level="WARN" />
```

## Labels

The container exports multiple labels to analyze running springtainers:

- `SPRINGTAINER_SERVICE=elasticsearch`
- `SPRINGTAINER_IMAGE=${embedded.container.elasticsearch.docker-image}`
- `SPRINGTAINER_STARTED=$currentTimestamp`
