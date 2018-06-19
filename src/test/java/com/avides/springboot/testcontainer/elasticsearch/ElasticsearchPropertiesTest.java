package com.avides.springboot.testcontainer.elasticsearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ElasticsearchPropertiesTest
{
    @Test
    public void testDefaults()
    {
        ElasticsearchProperties properties = new ElasticsearchProperties();
        assertTrue(properties.isEnabled());
        assertEquals(30, properties.getStartupTimeout());
        assertEquals("docker.elastic.co/elasticsearch/elasticsearch:5.6.10", properties.getDockerImage());

        assertEquals(9200, properties.getHttpPort());
        assertEquals(9300, properties.getTransportPort());
    }
}
