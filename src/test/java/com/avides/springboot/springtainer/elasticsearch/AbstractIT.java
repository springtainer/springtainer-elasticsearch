package com.avides.springboot.springtainer.elasticsearch;

import java.io.IOException;

import org.apache.hc.core5.http.HttpHost;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.avides.springboot.springtainer.common.util.DockerClients;
import com.github.dockerjava.api.DockerClient;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.Jackson3JsonpMapper;
import co.elastic.clients.transport.rest5_client.Rest5ClientTransport;
import co.elastic.clients.transport.rest5_client.low_level.Rest5Client;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AbstractIT.EsConfiguration.class)
@DirtiesContext
public abstract class AbstractIT
{
    protected static final String INDEX = "test";

    protected DockerClient dockerClient = DockerClients.build();

    @Autowired
    protected ConfigurableEnvironment environment;

    @Autowired
    protected ElasticsearchClient elasticsearchClient;

    /**
     * Indexes the given document and refreshes the index, so that it is immediately visible to searches.
     */
    protected void index(String id, Object document) throws IOException
    {
        elasticsearchClient.index(request -> request.index(INDEX).id(id).document(document));
        refresh();
    }

    protected void delete(String id) throws IOException
    {
        elasticsearchClient.delete(request -> request.index(INDEX).id(id));
        refresh();
    }

    protected void refresh() throws IOException
    {
        elasticsearchClient.indices().refresh(request -> request.index(INDEX));
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
        public ElasticsearchClient elasticsearchClient()
        {
            var restClient = Rest5Client.builder(new HttpHost(host, port)).build();
            return new ElasticsearchClient(new Rest5ClientTransport(restClient, new Jackson3JsonpMapper()));
        }
    }
}
