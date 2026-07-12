package com.avides.springboot.springtainer.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchClients;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.avides.springboot.springtainer.common.util.DockerClients;
import com.github.dockerjava.api.DockerClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AbstractIT.EsConfiguration.class)
@DirtiesContext
public abstract class AbstractIT
{
    protected DockerClient dockerClient = DockerClients.build();

    @Autowired
    protected ConfigurableEnvironment environment;

    @Autowired
    protected ElasticsearchOperations elasticsearchTemplate;

    protected void index(IndexQuery indexQuery, IndexCoordinates indexCoordinates)
    {
        elasticsearchTemplate.index(indexQuery, indexCoordinates);
        elasticsearchTemplate.indexOps(indexCoordinates).refresh();
    }

    @Configuration
    public static class EsConfiguration
    {
        @Value("${embedded.container.elasticsearch.host}")
        private String host;

        @Value("${embedded.container.elasticsearch.http-port}")
        private int port;

        @SuppressWarnings("resource")
        @Bean
        public ElasticsearchOperations elasticsearchTemplate()
        {
            var restClient = RestClient.builder(new HttpHost(host, port)).build();
            var client = ElasticsearchClients.createImperative(restClient);
            return new ElasticsearchTemplate(client);
        }
    }
}
