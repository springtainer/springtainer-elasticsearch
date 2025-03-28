package com.avides.springboot.springtainer.elasticsearch;

import static com.avides.springboot.springtainer.elasticsearch.ElasticsearchProperties.BEAN_NAME;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.elasticsearch.client.Request;
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
            envs.add("ES_JAVA_OPTS=-Xms750m -Xmx750m");
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
            try
            {
                var restClient = RestClient.builder(new HttpHost(getContainerHost(), getContainerPort(properties.getHttpPort()))).build();

                var request = new Request("GET", "/");
                request.addParameter("pretty", "true");
                restClient.performRequest(request);
                return true;
            }
            catch (@SuppressWarnings("unused") Exception e)
            {
                Thread.sleep(100);
                return false;
            }
        }
    }
}
