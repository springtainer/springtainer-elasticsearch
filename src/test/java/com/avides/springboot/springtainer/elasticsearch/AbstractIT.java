package com.avides.springboot.springtainer.elasticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AbstractIT.EsConfiguration.class, properties = { "spring.data.elasticsearch.cluster-nodes=${embedded.container.elasticsearch.host}:${embedded.container.elasticsearch.transport-port}", "spring.data.elasticsearch.properties.client.transport.ignore_cluster_name=true" })
@DirtiesContext
public abstract class AbstractIT
{
    protected DockerClient dockerClient = DockerClientBuilder.getInstance().build();

    @Autowired
    protected ConfigurableEnvironment environment;

    @Autowired
    protected ElasticsearchTemplate elasticsearchTemplate;

    protected void index(IndexQuery indexQuery, IndexCoordinates indexCoordinates)
    {
        elasticsearchTemplate.index(indexQuery, indexCoordinates);
        elasticsearchTemplate.indexOps(indexCoordinates).refresh();
    }

    @Configuration
    public static class EsConfiguration
    {
        @Value("${embedded.container.elasticsearch.host}")
        private String esHost;

        @Value("${embedded.container.elasticsearch.transport-port}")
        private int port;

        @Bean
        public org.elasticsearch.client.Client elasticsearchClient() throws UnknownHostException
        {
            Settings settings = Settings.builder().put("client.transport.ignore_cluster_name", "true").build();
            TransportClient client = new PreBuiltTransportClient(settings);
            client.addTransportAddress(new TransportAddress(InetAddress.getByName(esHost), port));
            return client;
        }

        @Bean(name = { "elasticsearchOperations", "elasticsearchTemplate" })
        public ElasticsearchTemplate elasticsearchTemplate() throws UnknownHostException
        {
            return new ElasticsearchTemplate(elasticsearchClient());
        }
    }
}
