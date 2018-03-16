package com.avides.springboot.testcontainer.elasticsearch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.GetQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class EmbeddedElasticsearchContainerAutoConfigurationIT extends AbstractIT
{
    @Test
    public void testGeneratedProperties()
    {
        assertThat(environment.getProperty("embedded.container.elasticsearch.host")).isNotEmpty();
        assertThat(environment.getProperty("embedded.container.elasticsearch.http-port")).isNotEmpty();
        assertThat(environment.getProperty("embedded.container.elasticsearch.transport-port")).isNotEmpty();

        System.out.println();
        System.out.println("Resolved properties:");
        System.out.println("Host:           " + environment.getProperty("embedded.container.elasticsearch.host"));
        System.out.println("HTTP-Port:      " + environment.getProperty("embedded.container.elasticsearch.http-port"));
        System.out.println("Transport-Port: " + environment.getProperty("embedded.container.elasticsearch.transport-port"));
        System.out.println();
    }

    @Test
    public void testCrud()
    {
        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setId("key1");
        indexQuery.setObject(new DummyDocument("key1", "value1"));
        indexQuery.setIndexName("test");
        indexQuery.setType("test");
        index(indexQuery);

        GetQuery getQuery = new GetQuery();
        getQuery.setId("key1");
        assertEquals("value1", elasticsearchTemplate.queryForObject(getQuery, DummyDocument.class).getValue());

        DeleteQuery deleteQuery = new DeleteQuery();
        deleteQuery.setQuery(QueryBuilders.termQuery("key", "key1"));
        deleteQuery.setIndex("test");
        deleteQuery.setType("test");
        elasticsearchTemplate.delete(deleteQuery);

        assertNull(elasticsearchTemplate.queryForObject(getQuery, DummyDocument.class));
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfiguration
    {
        // nothing
    }

    @Document(indexName = "test", type = "test")
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class DummyDocument
    {
        @Id
        private String key;

        private String value;
    }
}
