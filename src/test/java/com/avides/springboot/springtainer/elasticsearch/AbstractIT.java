package com.avides.springboot.springtainer.elasticsearch;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = { "spring.data.elasticsearch.cluster-nodes=${embedded.container.elasticsearch.host}:${embedded.container.elasticsearch.transport-port}", "spring.data.elasticsearch.properties.client.transport.ignore_cluster_name=true" })
@DirtiesContext
public abstract class AbstractIT
{
    protected DockerClient dockerClient = DockerClientBuilder.getInstance().build();

    @Autowired
    protected ConfigurableEnvironment environment;

    @Autowired
    protected ElasticsearchTemplate elasticsearchTemplate;

    protected void index(IndexQuery indexQuery)
    {
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(indexQuery.getObject().getClass());
    }
}
