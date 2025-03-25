package com.avides.springboot.springtainer.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ReflectionUtils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AbstractIT.EsConfiguration.class)
@DirtiesContext
public abstract class AbstractIT
{
    protected DockerClient dockerClient = DockerClientBuilder.getInstance().build();

    @Autowired
    protected ConfigurableEnvironment environment;

    @Autowired
    protected ElasticsearchRestTemplate elasticsearchRestTemplate;

    protected void index(IndexQuery indexQuery, IndexCoordinates indexCoordinates)
    {
        elasticsearchRestTemplate.index(indexQuery, indexCoordinates);
        elasticsearchRestTemplate.indexOps(indexCoordinates).refresh();
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
        public ElasticsearchRestTemplate elasticsearchRestTemplate()
        {
            var builder = RestClient.builder(new HttpHost(host, port));
            var client = new RestHighLevelClient(builder);
            ReflectionUtils.doWithFields(RestHighLevelClient.class, field ->
            {
                if (field.getName().equals("useAPICompatibility"))
                {
                    ReflectionUtils.makeAccessible(field);
                    field.setBoolean(client, true);
                }
            });
            return new ElasticsearchRestTemplate(client);
        }
    }
}
