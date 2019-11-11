package com.avides.springboot.testcontainer.elasticsearch;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.avides.springboot.testcontainer.common.container.AbstractEmbeddedContainerProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ConfigurationProperties("embedded.container.elasticsearch")
@Getter
@Setter
@ToString(callSuper = true)
public class ElasticsearchProperties extends AbstractEmbeddedContainerProperties
{
    public static final String BEAN_NAME = "embeddedElasticsearchContainer";

    private int httpPort = 9200;

    private int transportPort = 9300;

    public ElasticsearchProperties()
    {
        setDockerImage("docker.elastic.co/elasticsearch/elasticsearch:6.8.4");
    }
}
