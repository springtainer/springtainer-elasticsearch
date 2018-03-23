package com.avides.springboot.testcontainer.elasticsearch;

import static com.avides.springboot.testcontainer.elasticsearch.ElasticsearchProperties.BEAN_NAME;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import com.avides.springboot.testcontainer.common.container.AbstractBuildingEmbeddedContainer;
import com.avides.springboot.testcontainer.common.container.EmbeddedContainer;

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

    public class ElasticsearchContainer extends AbstractBuildingEmbeddedContainer<ElasticsearchProperties>
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
            return envs;
        }

        @Override
        protected Map<String, Object> providedProperties()
        {
            Map<String, Object> provided = new HashMap<>();
            provided.put("embedded.container.elasticsearch.host", getContainerHost());
            provided.put("embedded.container.elasticsearch.http-port", Integer.valueOf(properties.getHttpPort()));
            provided.put("embedded.container.elasticsearch.transport-port", Integer.valueOf(getContainerPort(properties.getTransportPort())));
            return provided;
        }

        @SneakyThrows
        @Override
        protected boolean isContainerReady(ElasticsearchProperties properties)
        {
            Settings settings = Settings.builder()
                    .put("client.transport.ignore_cluster_name", true)
                    .build();

            try (TransportClient client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress
                            .getByName(getContainerHost()), getContainerPort(properties.getTransportPort()))))
            {
                client.admin()
                        .cluster()
                        .prepareHealth()
                        .setWaitForYellowStatus()
                        .get();
                return true;
            }
            catch (NoNodeAvailableException e)
            {
                Thread.sleep(100);
                return false;
            }
        }
    }
}
