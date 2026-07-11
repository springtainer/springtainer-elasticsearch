package com.avides.springboot.springtainer.elasticsearch;

import static com.avides.springboot.springtainer.elasticsearch.ElasticsearchProperties.BEAN_NAME;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import com.avides.springboot.springtainer.common.container.AbstractBuildingEmbeddedContainer;
import com.avides.springboot.springtainer.common.container.EmbeddedContainer;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.HealthStatus;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;

import lombok.SneakyThrows;

@Configuration
@ConditionalOnProperty(name = "embedded.container.elasticsearch.enabled", matchIfMissing = true)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(ElasticsearchProperties.class)
public class EmbeddedElasticsearchContainerAutoConfiguration
{
    @ConditionalOnMissingBean(ElasticsearchContainer.class)
    @Bean(BEAN_NAME)
    public EmbeddedContainer elasticsearchContainer(ConfigurableEnvironment environment, ElasticsearchProperties properties)
    {
        return new ElasticsearchContainer("elasticsearch", environment, properties);
    }

    public static class ElasticsearchContainer extends AbstractBuildingEmbeddedContainer<ElasticsearchProperties>
    {
        public ElasticsearchContainer(String service, ConfigurableEnvironment environment, ElasticsearchProperties properties)
        {
            super(service, environment, properties);
        }

        @Override
        protected List<String> getEnvs()
        {
            List<String> envs = new ArrayList<>();
            envs.add("discovery.type=single-node");
            envs.add("xpack.security.enabled=false");
            envs.add("xpack.ml.enabled=false");
            envs.add("xpack.graph.enabled=false");
            envs.add("xpack.watcher.enabled=false");
            // -XX:+UseSerialGC is intentionally NOT set here: Elasticsearch's own default config/jvm.options already hardcodes
            // -XX:+UseG1GC, and combining both fatally crashes the JVM at startup with "Multiple garbage collectors selected"
            // (verified via a direct `docker run` against this exact image/tag)
            envs.add("ES_JAVA_OPTS=-Xms750m -Xmx750m -XX:TieredStopAtLevel=1");
            return envs;
        }

        @Override
        protected Map<String, Object> providedProperties()
        {
            Map<String, Object> provided = new HashMap<>();
            provided.put("embedded.container.elasticsearch.host", getContainerHost());
            provided.put("embedded.container.elasticsearch.http-port", Integer.valueOf(getContainerPort(properties.getHttpPort())));
            provided.put("embedded.container.elasticsearch.transport-port", Integer.valueOf(getContainerPort(properties.getTransportPort())));
            return provided;
        }

        @SneakyThrows
        @Override
        protected boolean isContainerReady(ElasticsearchProperties properties)
        {
            // The low-level RestClient is still used here, but only as the transport underneath
            // the new co.elastic.clients Java API client (RestHighLevelClient is gone in ES 8.x)
            try (var restClient = RestClient.builder(new HttpHost(getContainerHost(), getContainerPort(properties.getHttpPort()))).build())
            {
                var transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
                var client = new ElasticsearchClient(transport);
                var status = client.cluster().health().status();
                return status == HealthStatus.Green || status == HealthStatus.Yellow;
            }
            catch (@SuppressWarnings("unused") Exception e)
            {
                Thread.sleep(100);
                return false;
            }
        }
    }
}
