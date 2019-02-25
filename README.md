springboot-testcontainer-elasticsearch
======================================

[![Maven Central](https://img.shields.io/maven-metadata/v/http/central.maven.org/maven2/com/avides/springboot/testcontainer/springboot-testcontainer-elasticsearch/maven-metadata.xml.svg)](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.avides.springboot.testcontainer%22%20AND%20a%3A%22springboot-testcontainer-elasticsearch%22)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/93c723ba5a204c5ea56ff9e19aa74d22)](https://www.codacy.com/app/avides-builds/springboot-testcontainer-elasticsearch)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/93c723ba5a204c5ea56ff9e19aa74d22)](https://www.codacy.com/app/avides-builds/springboot-testcontainer-elasticsearch)
[![Build Status](https://travis-ci.org/springboot-testcontainer/springboot-testcontainer-elasticsearch.svg?branch=master)](https://travis-ci.org/springboot-testcontainer/springboot-testcontainer-elasticsearch)

### Dependency
```xml
<dependency>
	<groupId>com.avides.springboot.testcontainer</groupId>
	<artifactId>springboot-testcontainer-elasticsearch</artifactId>
	<version>0.1.0-RC11</version>
	<scope>test</scope>
</dependency>
```

### Configuration
Properties consumed (in `bootstrap.properties`):
- `embedded.container.elasticsearch.enabled` (default is `true`)
- `embedded.container.elasticsearch.startup-timeout` (default is `30`)
- `embedded.container.elasticsearch.docker-image` (default is `docker.elastic.co/elasticsearch/elasticsearch:6.5.4`)
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
<!-- Testcontainers -->
<logger name="com.github.dockerjava.jaxrs" level="WARN" />
<logger name="com.github.dockerjava.core.command" level="WARN" />
<logger name="org.apache.http" level="WARN" />
```

## Labels
The container exports multiple labels to analyze running testcontainers:
- `TESTCONTAINER_SERVICE=elasticsearch`
- `TESTCONTAINER_IMAGE=${embedded.container.elasticsearch.docker-image}`
- `TESTCONTAINER_STARTED=$currentTimestamp`
