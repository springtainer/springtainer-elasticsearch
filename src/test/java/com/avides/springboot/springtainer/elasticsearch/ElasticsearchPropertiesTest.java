package com.avides.springboot.springtainer.elasticsearch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ElasticsearchPropertiesTest
{
    @Test
    public void testDefaults()
    {
        var properties = new ElasticsearchProperties();
        assertTrue(properties.isEnabled());
        assertEquals(30, properties.getStartupTimeout());
        assertEquals("docker.elastic.co/elasticsearch/elasticsearch:8.18.8", properties.getDockerImage());

        assertEquals(9200, properties.getHttpPort());
        assertEquals(9300, properties.getTransportPort());
    }
}
